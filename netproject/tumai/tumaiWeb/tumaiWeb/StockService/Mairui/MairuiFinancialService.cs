using Microsoft.EntityFrameworkCore;
using System.Text.Json;
using tumaiWeb.Data.Entities;
using tumaiWeb.Data.Repository;

namespace tumaiWeb.StockService.Mairui
{
    public class MairuiFinancialService
    {
        private readonly AppDbContext _db;
        private readonly IBaseService _baseService;
        public MairuiFinancialService(AppDbContext db, IBaseService baseService)
        {
            _db = db;
            _baseService = baseService;
        }

        private static readonly JsonSerializerOptions _jsonOpts = new()
        {
            WriteIndented = false
        };

        public async Task<List<StockSelect>> QuerySelfStockListAsync()
        {
            var sList = await _baseService.QueryObjectAsync<StockSelect>("select * from stockselect",null);
            return sList;
        }


        /*
         - **推荐：每周 1 次，周日凌晨 02:00 执行**
- 可选保守方案：每 10 天一次
- 不建议：每日拉取，极度浪费额度
- 执行逻辑：批量遍历股票列表，Upsert 更新 StockBasic；失败股票记录日志，重试。
         */
        public async Task SaveStockBasicsData(string stockBasicsJson)
        {
            using var document = JsonDocument.Parse(stockBasicsJson);

            if (document.RootElement.ValueKind != JsonValueKind.Array)
                throw new ArgumentException("stockBasicsJson 必须是 JSON 数组");

            var stockBasicEntities = new List<StockBasic>();
            var now = DateTime.UtcNow;

            foreach (var element in document.RootElement.EnumerateArray())
            {
                var dm = MairuiDictHelper.GetString(element, "dm");
                var mc = MairuiDictHelper.GetString(element, "mc");
                var jys = MairuiDictHelper.GetString(element, "jys");

                if (string.IsNullOrWhiteSpace(dm))
                    continue;

                var market = jys?.Trim().ToUpperInvariant();

                var entity = new StockBasic
                {
                    StockCode = dm.Split('.')[0],
                    StockName = mc,
                    Market = market,

                    ListDate = null,
                    IsDelist = 0,
                    IsActive = true,
                    Industry = null,

                    CreateTime = now,
                    UpdateTime = now
                };

                stockBasicEntities.Add(entity);
            }

            await UpsertStockBasics(stockBasicEntities);
        }



        /*
         数据特点：A 股财报有固定披露时间窗口：
年报：次年 1–4 月；一季报：4 月；半年报：7–8 月；三季报：10 月。非披露期几乎无新增数据。
        #### 方案 A【推荐，节省额度】

- **披露期：每日凌晨 01:00 执行**（每年 1/4/7/10 月）
- **非披露期：每周一次，周日凌晨 01:00**

> 
> 逻辑：只拉取你关注股票列表，拉取指定时间区间财报，Upsert 写入 StockFinancialReport，**入库瞬间自动计算衍生指标：NetAssetPerShare、ROE、毛利率、净利率等**。衍生指标**不单独定时任务**，财报一入库就计算。

        #### 方案 B【简单，开发省事，不做月份判断】

> 
> 统一 **每周日凌晨 01:00** 执行一次财报全量拉取
> 优点：代码简单，不用写月份分支；缺点：非财报披露期大部分请求返回空，轻微浪费额度。原型阶段优先选 B。

> 
> 重要提醒：
> 财报接口是**历史静态数据，不需要秒 / 小时级轮询**；已经入库的报告期不会变化，Upsert 不会重复生成新记录，只会覆盖更新。
         */
        /// <summary>
        /// 麦蕊财报入库入口
        /// </summary>
        /// <param name="stockCode">完整股票代码 如：600036.SH</param>
        /// <param name="profitJson">利润表接口返回json</param>
        /// <param name="balanceJson">资产负债表接口返回json</param>
        /// <param name="cashJson">现金流量表接口返回json</param>
        public async Task SaveFinancialData(string stockCode, string profitJson, string balanceJson, string cashJson)
        {
            var sm = stockCode.Split(".");
            // 1. 直接反序列成字典列表，无任何DTO
            using var profitDocument = JsonDocument.Parse(profitJson);
            using var balanceDocument = JsonDocument.Parse(balanceJson);
            using var cashDocument = JsonDocument.Parse(cashJson);

            // 2. 利润表：字典映射到EF实体
            List<IncomeStatementItemDto> incomeEntities = new();
            foreach (var element in profitDocument.RootElement.EnumerateArray())
            {
                var jzrq = MairuiDictHelper.GetString(element, "jzrq")!;
                var plrq = MairuiDictHelper.GetString(element, "plrq");

                var entity = new IncomeStatementItemDto
                {
                    StockCode = sm[0],
                    Market = sm[1],
                    ReportDate = jzrq,
                    PublishDate = plrq,
                    ReportType = MairuiDictHelper.GetReportType(jzrq),

                    // 收入
                    TotalRevenue = MairuiDictHelper.GetDecimal(element, "yyzsr"),
                    OperatingRevenue = MairuiDictHelper.GetDecimal(element, "yysr"),

                    // 成本费用
                    OperatingCost = MairuiDictHelper.GetDecimal(element, "yycb"),
                    TaxAndSurcharges = MairuiDictHelper.GetDecimal(element, "yysjjfj"),
                    SellingExpense = MairuiDictHelper.GetDecimal(element, "xsfy"),
                    AdminExpense = MairuiDictHelper.GetDecimal(element, "glfy"),
                    RndExpense = MairuiDictHelper.GetDecimal(element, "yffy"),
                    FinanceExpense = MairuiDictHelper.GetDecimal(element, "cwfy"),
                    InterestExpense = MairuiDictHelper.GetDecimal(element, "lxzc"),
                    InterestIncome = MairuiDictHelper.GetDecimal(element, "lxsr"),

                    // 利润
                    OperatingProfit = MairuiDictHelper.GetDecimal(element, "yylr"),
                    NonOperatingIncome = MairuiDictHelper.GetDecimal(element, "ywsr"),
                    NonOperatingExpense = MairuiDictHelper.GetDecimal(element, "ywzc"),

                    TotalProfit =
                        MairuiDictHelper.GetDecimal(element, "lrzef")
                        ?? MairuiDictHelper.GetDecimal(element, "lrze"),

                    IncomeTaxExpense = MairuiDictHelper.GetDecimal(element, "sdsfy"),

                    // 净利润
                    NetProfit = MairuiDictHelper.GetDecimal(element, "jlr"),
                    NetProfitParent =
                        MairuiDictHelper.GetDecimal(element, "gsmgsyzzdjlr"),
                    MinorityProfit =
                        MairuiDictHelper.GetDecimal(element, "ssgdsy"),
                    DeductNonProfit =
                        MairuiDictHelper.GetDecimal(element, "jlrhfcjcx"),

                    // 每股收益
                    BasicEps = MairuiDictHelper.GetDecimal(element, "jbmgsy"),
                    DilutedEps = MairuiDictHelper.GetDecimal(element, "xsmgsy"),

                    // 直接保存当前 JSON 对象
                    RawJson = JsonDocument.Parse(element.GetRawText()),

                    CreateTime = DateTime.UtcNow
                };

                incomeEntities.Add(entity);
            }

            // 3. 资产负债表：字典映射到EF实体
            List<BalanceSheetItemDto> balanceEntities = new();
            foreach (var element in balanceDocument.RootElement.EnumerateArray())
            {
                var jzrq = MairuiDictHelper.GetString(element, "jzrq")!;
                var plrq = MairuiDictHelper.GetString(element, "plrq");

                var entity = new BalanceSheetItemDto
                {
                    StockCode = sm[0],
                    Market = sm[1],
                    ReportDate = jzrq,
                    PublishDate = plrq,
                    ReportType = MairuiDictHelper.GetReportType(jzrq),

                    // ===== 资产部分 =====
                    TotalCurrentAsset =
                        MairuiDictHelper.GetDecimal(element, "ldzchj"),

                    MonetaryFund =
                        MairuiDictHelper.GetDecimal(element, "hbzj"),

                    TradingFinancialAsset =
                        MairuiDictHelper.GetDecimal(element, "jyxjrzcf"),

                    BillReceivable =
                        MairuiDictHelper.GetDecimal(element, "yspj"),

                    AccountReceivable =
                        MairuiDictHelper.GetDecimal(element, "yszk"),

                    Prepayment =
                        MairuiDictHelper.GetDecimal(element, "yfkx"),

                    Inventory =
                        MairuiDictHelper.GetDecimal(element, "ch"),

                    OtherCurrentAsset =
                        MairuiDictHelper.GetDecimal(element, "qtldzcf"),

                    TotalNonCurrentAsset =
                        MairuiDictHelper.GetDecimal(element, "fldzchj"),

                    FixedAssetOriginal =
                        MairuiDictHelper.GetDecimal(element, "gdzcyz"),

                    FixedAssetNet =
                        MairuiDictHelper.GetDecimal(element, "gdzcjz"),

                    IntangibleAsset =
                        MairuiDictHelper.GetDecimal(element, "wxzcf"),

                    Goodwill =
                        MairuiDictHelper.GetDecimal(element, "sy"),

                    LongTermEquityInvest =
                        MairuiDictHelper.GetDecimal(element, "cqgqtz"),

                    TotalAsset =
                        MairuiDictHelper.GetDecimal(element, "zczj"),

                    // ===== 负债部分 =====
                    TotalCurrentLiability =
                        MairuiDictHelper.GetDecimal(element, "ldfzhj"),

                    ShortTermLoan =
                        MairuiDictHelper.GetDecimal(element, "dqjk"),

                    BillPayable =
                        MairuiDictHelper.GetDecimal(element, "yfpj"),

                    AccountPayable =
                        MairuiDictHelper.GetDecimal(element, "yfzk"),

                    AdvanceReceived =
                        MairuiDictHelper.GetDecimal(element, "ysk"),

                    SalaryPayable =
                        MairuiDictHelper.GetDecimal(element, "yfgzxcf"),

                    TaxPayable =
                        MairuiDictHelper.GetDecimal(element, "yjsf"),

                    TotalNonCurrentLiability =
                        MairuiDictHelper.GetDecimal(element, "fldfzhj"),

                    LongTermLoan =
                        MairuiDictHelper.GetDecimal(element, "cqjk"),

                    BondPayable =
                        MairuiDictHelper.GetDecimal(element, "yfzq"),

                    TotalLiability =
                        MairuiDictHelper.GetDecimal(element, "fzhj"),

                    // ===== 所有者权益 =====
                    TotalEquity =
                        MairuiDictHelper.GetDecimal(element, "syzqyhj"),

                    PaidInCapital =
                        MairuiDictHelper.GetDecimal(element, "sszbf"),

                    CapitalReserve =
                        MairuiDictHelper.GetDecimal(element, "zbgj"),

                    SurplusReserve =
                        MairuiDictHelper.GetDecimal(element, "ylgj"),

                    UndistributedProfit =
                        MairuiDictHelper.GetDecimal(element, "wfplr"),

                    ParentEquity =
                        MairuiDictHelper.GetDecimal(element, "gsmgdqsyhj"),

                    MinorityEquity =
                        MairuiDictHelper.GetDecimal(element, "ssgdqy"),

                    // 直接保存当前 JSON 对象
                    RawJson = JsonDocument.Parse(element.GetRawText()),

                    CreateTime = DateTime.UtcNow
                };

                balanceEntities.Add(entity);
            }

            //4. 现金流量表：字典映射到EF实体
            List<CashFlowItemDto> cashEntities = new();
            foreach (var element in cashDocument.RootElement.EnumerateArray())
            {
                var jzrq = MairuiDictHelper.GetString(element, "jzrq")!;
                var plrq = MairuiDictHelper.GetString(element, "plrq");

                var entity = new CashFlowItemDto
                {
                    StockCode = sm[0],
                    Market = sm[1],
                    ReportDate = jzrq,
                    PublishDate = plrq,
                    ReportType = MairuiDictHelper.GetReportType(jzrq),

                    // ===== 经营活动 =====
                    OperateCashIn =
                        MairuiDictHelper.GetDecimal(element, "jyhdxjlrxj"),

                    OperateCashOut =
                        MairuiDictHelper.GetDecimal(element, "jyhdxjlcxj"),

                    NetOperateCashFlow =
                        MairuiDictHelper.GetDecimal(element, "jyhdcsdxjlxj"),

                    CashFromSales =
                        MairuiDictHelper.GetDecimal(element, "xssptglwsddxj"),

                    TaxRefundReceived =
                        MairuiDictHelper.GetDecimal(element, "sddsfyfh"),

                    OtherOperateCashIn =
                        MairuiDictHelper.GetDecimal(element, "sdqtyjyghdxj"),

                    CashPayForGoods =
                        MairuiDictHelper.GetDecimal(element, "gmspjslwzfdxj"),

                    CashPayToStaff =
                        MairuiDictHelper.GetDecimal(element, "zfgzyjwzgzfdxj"),

                    TaxPaid =
                        MairuiDictHelper.GetDecimal(element, "zfdgxsf"),

                    OtherOperateCashOut =
                        MairuiDictHelper.GetDecimal(element, "zfqtyjyghdxj"),

                    // ===== 投资活动 =====
                    InvestCashIn =
                        MairuiDictHelper.GetDecimal(element, "tzhdxjlrxj"),

                    InvestCashOut =
                        MairuiDictHelper.GetDecimal(element, "tzhdxjlcxj"),

                    NetInvestCashFlow =
                        MairuiDictHelper.GetDecimal(element, "tzhdcsdxjlxj"),

                    CashFromInvestRecall =
                        MairuiDictHelper.GetDecimal(element, "shtzssddxj"),

                    CashFromInvestIncome =
                        MairuiDictHelper.GetDecimal(element, "qdtzsysddxj"),

                    CashFromDisposeLongAsset =
                        MairuiDictHelper.GetDecimal(
                            element,
                            "czgdzcwxzhqtqctzssddxj"),

                    Capex =
                        MairuiDictHelper.GetDecimal(
                            element,
                            "gjgdzcwxzhqtqctzzfdxj"),

                    CashPayForInvest =
                        MairuiDictHelper.GetDecimal(element, "tzszfdxj"),

                    // ===== 筹资活动 =====
                    FinanceCashIn =
                        MairuiDictHelper.GetDecimal(element, "czhdxjlrxj"),

                    FinanceCashOut =
                        MairuiDictHelper.GetDecimal(element, "czhdxjlcxj"),

                    NetFinanceCashFlow =
                        MairuiDictHelper.GetDecimal(element, "czhdcsdxjlxj"),

                    CashFromEquity =
                        MairuiDictHelper.GetDecimal(element, "xstzsdj"),

                    CashFromBorrow =
                        MairuiDictHelper.GetDecimal(element, "qdjkjddxj"),

                    CashRepayDebt =
                        MairuiDictHelper.GetDecimal(element, "chzwzfxj"),

                    CashPayDividendInterest =
                        MairuiDictHelper.GetDecimal(
                            element,
                            "fpglrlhcllxzfdxj"),

                    // ===== 现金及现金等价物 =====
                    ForexEffectOnCash =
                        MairuiDictHelper.GetDecimal(element, "hlbddxjdxy"),

                    NetIncreaseCash =
                        MairuiDictHelper.GetDecimal(element, "xjxjdhwjzje"),

                    BeginCashBalance =
                        MairuiDictHelper.GetDecimal(element, "qcxjjxjdhwye"),

                    EndCashBalance =
                        MairuiDictHelper.GetDecimal(element, "qmxjjxjdhwye"),

                    // 直接保存当前 JSON 对象
                    RawJson = JsonDocument.Parse(element.GetRawText()),

                    CreateTime = DateTime.UtcNow
                };

                cashEntities.Add(entity);
            }

            //对三个财务报表进行去重，按ReportDate分组，保留PublishDate最新的一条
            var qcIncomeList = DistinctKeepMaxDateString(incomeEntities,
    groupKeySelector: x => new { x.StockCode, x.ReportDate, x.Market },
    dateStrSelector: x => x.PublishDate
);
            var qcBalanceList = DistinctKeepMaxDateString(balanceEntities, groupKeySelector: x => new { x.StockCode, x.ReportDate, x.Market },
    dateStrSelector: x => x.PublishDate);
            var qcCashList = DistinctKeepMaxDateString(cashEntities, groupKeySelector: x => new { x.StockCode, x.ReportDate, x.Market },
    dateStrSelector: x => x.PublishDate);

            var stockFinancialReportList = MergeToStockFinancialReport(qcIncomeList,qcBalanceList, qcCashList);

            // ========== Upsert批量入库 ==========
            await _baseService.TransactionCallAsync(-1, async () => {
                await UpsertIncome(qcIncomeList);
                await UpsertBalance(qcBalanceList);
                await UpsertCashFlow(qcCashList);
                //await _baseService.AddObjectRangeAsync(stockFinancialReportList);
            });
        }


        /*
         数据特点：盘中实时变动；收盘后不再变化。

> 
> A 股交易时间：周一～周五 09:30–11:30，13:00–15:00；节假日休市。

### Quartz 调度方案

1. **交易日盘中：每 5 分钟拉取一次**（09:30 ~11:30，13:00~15:00）
   - 适合做盘中指标、涨速、监控；
   - 批量分组，每组 20 支股票；
   - 休市时间不执行任务，节省额度。
2. **收盘快照（强烈建议单独任务）：交易日 15:05 执行一次**
> 
> 收盘后固定拉一版当日最终行情，用于每日指标计算、复盘。
3. 周末、节假日：**不执行行情任务**。

> 
> 业务说明：行情快照表每次拉取直接 Add 新增记录，**不 Upsert**，保留时序历史
         */
        public async Task SaveStockSsjy4ManyStkData(
    List<string> stockCodeWithMarketList,
    string stockQuoteJsonArray)
        {
            using var document = JsonDocument.Parse(stockQuoteJsonArray);

            var snapshotEntities = new List<StockQuoteSnapshot>();

            var elements = document.RootElement.EnumerateArray();

            int i = 0;

            foreach (var element in elements)
            {
                if (i >= stockCodeWithMarketList.Count)
                    break;

                var sm = stockCodeWithMarketList[i].Split(".");

                var pullTime = DateTime.UtcNow;

                var entity = new StockQuoteSnapshot
                {
                    StockCode = sm[0],
                    Market = sm[1],

                    PullTime = pullTime,

                    Price = MairuiDictHelper.GetDecimal(element, "p"),
                    YesterdayClose = MairuiDictHelper.GetDecimal(element, "yc"),
                    Open = MairuiDictHelper.GetDecimal(element, "o"),
                    High = MairuiDictHelper.GetDecimal(element, "h"),
                    Low = MairuiDictHelper.GetDecimal(element, "l"),

                    ChangePercent = MairuiDictHelper.GetDecimal(element, "pc"),
                    ChangeAmount = MairuiDictHelper.GetDecimal(element, "ud"),

                    Volume = MairuiDictHelper.GetLong(element, "v"),
                    Turnover = MairuiDictHelper.GetDecimal(element, "cje"),

                    TotalMarketValue = null,

                    TvVolume = MairuiDictHelper.GetLong(element, "tv"),
                    PvVolume = MairuiDictHelper.GetLong(element, "pv"),

                    Pe = MairuiDictHelper.GetDecimal(element, "pe"),
                    PbRatio = MairuiDictHelper.GetDecimal(element, "pb_ratio"),
                    TurnoverRate = MairuiDictHelper.GetDecimal(element, "tr"),
                    Amplitude = MairuiDictHelper.GetDecimal(element, "zf"),

                    FiveMinChange = null,

                    SnapshotTime =
                        MairuiDictHelper.GetDateTime(element, "t")
                        ?? DateTime.UtcNow,

                    // 当前这一条行情的完整 JSON
                    RawJson = JsonDocument.Parse(element.GetRawText()),

                    CreateTime = pullTime
                };

                snapshotEntities.Add(entity);

                i++;
            }

            await _baseService.AddObjectRangeAsync(snapshotEntities);
        }

        /// <summary>
        /// 单只个股计算估值指标，Upsert入库
        /// </summary>
        public async Task CalcAndUpsertOneAsync(string stockCode, DateOnly tradeDate, CancellationToken ct)
        {
            // 1. 根据SnapshotTime日期取当日行情快照
            var dayStart = tradeDate.ToDateTime(TimeOnly.MinValue);
            var dayEnd = tradeDate.ToDateTime(TimeOnly.MaxValue);
            var sm = stockCode.Split(".");
            // 1. 获取当日行情快照
            var quote = await _db.StockQuoteSnapshots
                .FirstOrDefaultAsync(q => q.StockCode == sm[0] && q.Market == sm[1] && q.SnapshotTime >= dayStart
                && q.SnapshotTime <= dayEnd, ct);
            if (quote == null || !quote.TotalMarketValue.HasValue)
                return;

            // 总市值：元 → 转为【万元】和财报口径对齐
            decimal totalMarketCapWan = quote.TotalMarketValue.Value / 10000;

            // 2. 取最近4个季度财报（按报告日期倒序）
            var last4Reports = await _db.StockFinancialReports
                .Where(f => f.StockCode == sm[0] && f.Market == sm[1])
                .OrderByDescending(f => f.ReportDate)
                .Take(4)
                .ToListAsync(ct);

            if (last4Reports.Count < 4)
                return;

            // TTM：4期归母净利润之和（优先用ParentCompanyNetProfit）
            decimal sumParentNetProfit = last4Reports.Sum(r => r.ParentCompanyNetProfit ?? 0);
            // TTM：4期营业收入之和
            decimal sumIncomeTtm = last4Reports.Sum(r => r.Income ?? 0);

            // 最新一期：股东权益合计（净资产，万元）
            var latestReport = last4Reports[0];
            decimal? shareholdersEquity = latestReport.ShareholdersEquity;

            // 指标计算，负数/缺失直接置null，避免无效值入库
            decimal? peTtm = sumParentNetProfit > 0 ? totalMarketCapWan / sumParentNetProfit : null;
            decimal? pb = (shareholdersEquity.HasValue && shareholdersEquity > 0) ? totalMarketCapWan / shareholdersEquity.Value : null;
            decimal? psTtm = sumIncomeTtm > 0 ? totalMarketCapWan / sumIncomeTtm : null;

            // Upsert
            var existValuation = await _db.StockValuations
                .FirstOrDefaultAsync(v => v.StockCode == sm[0] && v.Market == sm[1] && v.TradeDate == tradeDate, ct);
            
            if (existValuation == null)
            {
                existValuation = new StockValuation
                {
                    StockCode = sm[0],
                    Market = sm[1],
                    TradeDate = tradeDate,
                };
                _db.StockValuations.Add(existValuation);
            }

            existValuation.TotalMarketCap = totalMarketCapWan;
            existValuation.PeTtm = peTtm;
            existValuation.Pb = pb;
            existValuation.PsTtm = psTtm;
            existValuation.CalcTime = DateTime.Now;

            await _db.SaveChangesAsync(ct);
        }

        /// <summary>
        /// 批量计算当日全市场个股估值
        /// </summary>
        public async Task BatchCalcAllStockAsync(DateOnly tradeDate, CancellationToken ct)
        {
            var stockList = await _db.StockBasics
                .Select(s => new
                {
                    s.StockCode,
                    s.Market
                })
                .ToListAsync(ct);

            foreach (var stock in stockList)
            {
                await CalcAndUpsertOneAsync($"{stock.StockCode}.{stock.Market}", tradeDate, ct);
            }
        }

        #region remark
        /*
         * ## 衍生指标计算时机（重点！不要单独 Quartz 任务）

表格

| 指标 | 计算触发时机 |
| --- | --- |
| NetAssetPerShare 每股净资产 | **财报入库合并时，即时计算**（拿到资产负债 TotalEquity + StockBasic 总股本） |
| 毛利率、净利率 | 财报合并入库即时计算（利润表字段） |
| ROE (期末口径) | 财报合并入库即时计算 |

> 
> ❌ 不要单独写 Quartz 任务定时跑一遍全量指标计算。
> ✅ 只有当财报 / 基础股本数据更新的时候，才重新计算，减少计算压力。


        # 汇总总调度清单（直接拿去写 Quartz Cron）

表格

| 任务名称 | 接口 | 推荐 Cron 表达式 | 执行时间说明 |
| --- | --- | --- | --- |
| StockBasic 基础标的更新 | hsstock/instrument | `0 0 2 ? * SUN` | 每周日 02:00，更新总股本 / 流通股本 |
| 财报批量拉取 (利润 + 资产负债 + 现金流) | bj/financial/* | `0 0 1 ? * SUN` | 每周日 01:00；披露期可改成每日凌晨 1 点 |
| 盘中行情快照 | hsrl/ssjy_more | `0 0/5 9-11,13-15 ? * MON-FRI` | 交易日 9:30~11:30、13:00~15:00，每 5 分钟 1 次 |
| 收盘固定快照 | hsrl/ssjy_more | `0 5 15 ? * MON-FRI` | 交易日 15:05，抓取收盘最终行情 |

> 
> Cron 说明：Quartz Cron 格式 `秒 分 时 日 月 星期`
         */
        #endregion

        #region 原生PostgreSQL Upsert（无第三方包，适配新版Npgsql，JsonB修复）
        private async Task UpsertIncome(List<IncomeStatementItemDto> list)
        {
            foreach (var item in list)
            {
                //await _baseService.AddObjectAsync(item);
                await _baseService.UpsertAsync(item, [nameof(IncomeStatementItemDto.StockCode), nameof(IncomeStatementItemDto.Market), nameof(IncomeStatementItemDto.ReportDate)]);
            }
        }

        private async Task UpsertBalance(List<BalanceSheetItemDto> list)
        {
            foreach (var item in list)
            {
                //await _baseService.AddObjectAsync(item);
                await _baseService.UpsertAsync(item, [nameof(BalanceSheetItemDto.StockCode), nameof(BalanceSheetItemDto.Market), nameof(BalanceSheetItemDto.ReportDate)]);
            }
        }

        private async Task UpsertCashFlow(List<CashFlowItemDto> list)
        {
            foreach (var item in list)
            {
                //await _baseService.AddObjectAsync(item);
                await _baseService.UpsertAsync(item, [nameof(CashFlowItemDto.StockCode), nameof(CashFlowItemDto.Market), nameof(CashFlowItemDto.ReportDate)]);
            }
        }
        #endregion

        /// <summary>
        /// 从麦蕊API拉取股票基础列表，批量Upsert到StockBasics
        /// </summary>
        /// <param name="stockList">麦蕊返回的股票基础列表</param>
        private async Task UpsertStockBasics(List<StockBasic> stockList)
        {
            Console.WriteLine($"UpsertStockBasics: {stockList.Count} items");
            foreach (var item in stockList)
            {
                //await _baseService.AddObjectAsync(item);
                await _baseService.UpsertAsync(item, [nameof(StockBasic.StockCode), nameof(StockBasic.Market)]);
            }
        }

        /// <summary>
        /// 通用去重：按分组键分组，每组保留时间字段最大的一条（无反射，高性能）
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="source">数据源</param>
        /// <param name="groupKeySelector">分组联合键</param>
        /// <param name="timeSelector">用来比较的时间字段</param>
        private List<T> DistinctKeepMaxDateString<T, TKey>(
            IEnumerable<T> source,
            Func<T, TKey> groupKeySelector,
            Func<T, string?> dateStrSelector)
        {
            if (source == null)
                return new List<T>();

            return source
                .GroupBy(groupKeySelector)
                .Select(g =>
                {
                    // 每组内部：尝试把字符串转DateTime，解析失败当作最小时间
                    var ordered = g.OrderByDescending(item =>
                    {
                        var dateStr = dateStrSelector(item);
                        if (DateTime.TryParse(dateStr, out var dt))
                        {
                            return dt;
                        }
                        return DateTime.MinValue;
                    });
                    return ordered.First();
                })
                .ToList();
        }

        /// <summary>
        /// 财报单条内部指标计算，存入StockFinancialReport
        /// </summary>
        private static void CalcFinancialInnerIndicator(StockFinancialReport report)
        {
            //毛利率 = (营业收入 - 营业成本)/营业收入 *100
            if (report.Income.HasValue && report.Income > 0 && report.Cost.HasValue)
            {
                report.GrossRate = (report.Income.Value - report.Cost.Value) / report.Income.Value * 100;
            }
            //净利率 = 净利润 / 营业收入 *100
            if (report.Income.HasValue && report.Income > 0 && report.NetProfit.HasValue)
            {
                report.NetRate = report.NetProfit.Value / report.Income.Value * 100;
            }
            //资产负债率 = 总负债 / 总资产 *100
            if (report.TotalAssets.HasValue && report.TotalAssets > 0 && report.TotalLiabilities.HasValue)
            {
                report.DebtRate = report.TotalLiabilities.Value / report.TotalAssets.Value * 100;
            }
            //ROE = 归母净利润 / 股东权益合计 *100
            if (report.ShareholdersEquity.HasValue && report.ShareholdersEquity > 0 && report.ParentCompanyNetProfit.HasValue)
            {
                report.Roe = report.ParentCompanyNetProfit.Value / report.ShareholdersEquity.Value * 100;
            }
        }

        /// <summary>
        /// 合并三份已经解析、去重完成的财报Dto，生成StockFinancialReport列表
        /// </summary>
        /// <param name="incomeItems">利润表Dto集合（已去重）</param>
        /// <param name="balanceItems">资产负债表Dto集合（已去重）</param>
        /// <param name="cashItems">现金流量表Dto集合（已去重）</param>
        /// <returns>合并后的StockFinancialReport列表</returns>
        private List<StockFinancialReport> MergeToStockFinancialReport(
    List<IncomeStatementItemDto> incomeItems,
    List<BalanceSheetItemDto> balanceItems,
    List<CashFlowItemDto> cashItems)
        {
            // key = ReportDate，例如 yyyy-MM-dd
            var incomeDict = incomeItems.ToDictionary(x => x.ReportDate);
            var balanceDict = balanceItems.ToDictionary(x => x.ReportDate);
            var cashDict = cashItems.ToDictionary(x => x.ReportDate);

            var resultList = new List<StockFinancialReport>();

            foreach (var (reportDateStr, incomeDto) in incomeDict)
            {
                // 尝试获取同期资产负债表、现金流量表
                balanceDict.TryGetValue(reportDateStr, out var balanceDto);
                cashDict.TryGetValue(reportDateStr, out var cashDto);

                // 字符串日期转 DateOnly
                if (!DateOnly.TryParse(reportDateStr, out var reportDate))
                {
                    continue;
                }

                var report = new StockFinancialReport
                {
                    StockCode = incomeDto.StockCode,
                    Market = incomeDto.Market,

                    ReportDate = reportDate,
                    ReportType = incomeDto.ReportType,

                    DiscloseDate =
                        DateOnly.TryParse(incomeDto.PublishDate, out var dt)
                            ? dt
                            : null,

                    // ========== 利润表 ==========
                    Income = incomeDto.OperatingRevenue,
                    Cost = incomeDto.OperatingCost,
                    Profit = incomeDto.OperatingProfit,
                    TotalProfit = incomeDto.TotalProfit,
                    NetProfit = incomeDto.NetProfit,
                    DeductedProfit = incomeDto.DeductNonProfit,
                    ParentCompanyNetProfit = incomeDto.NetProfitParent,
                    IncomeTaxExpense = incomeDto.IncomeTaxExpense,
                    BasicEps = incomeDto.BasicEps,
                    DilutedEps = incomeDto.DilutedEps,

                    // ========== 资产负债表 ==========
                    TotalAssets = balanceDto?.TotalAsset,
                    TotalLiabilities = balanceDto?.TotalLiability,
                    ShareholdersEquity = balanceDto?.TotalEquity,

                    // ========== 现金流量表 ==========
                    OperatingCashFlow = cashDto?.NetOperateCashFlow,
                    InvestingCashFlow = cashDto?.NetInvestCashFlow,
                    FinancingCashFlow = cashDto?.NetFinanceCashFlow,

                    PullTime = DateTime.UtcNow,

                    // ========== 原始 JSON ==========
                    RawJson = BuildRawJson(
                        incomeDto.RawJson,
                        balanceDto?.RawJson,
                        cashDto?.RawJson)
                };

                // shareholdersEquity 来自资产负债表，
                // totalShare 来自 StockBasic 总股本
                //
                // if (report.ShareholdersEquity.HasValue &&
                //     totalShare.HasValue &&
                //     totalShare != 0)
                // {
                //     report.NetAssetPerShare =
                //         report.ShareholdersEquity / totalShare;
                // }
                // else
                // {
                //     report.NetAssetPerShare = null;
                // }

                // 计算衍生指标：
                // 毛利率、净利率、资产负债率、ROE
                CalcFinancialInnerIndicator(report);

                resultList.Add(report);
            }

            return resultList;
        }

        private static JsonDocument BuildRawJson(
    JsonDocument? incomeJson,
    JsonDocument? balanceJson,
    JsonDocument? cashFlowJson)
        {
            using var stream = new MemoryStream();
            using var writer = new Utf8JsonWriter(stream);

            writer.WriteStartObject();

            writer.WritePropertyName("income");

            if (incomeJson != null)
                incomeJson.RootElement.WriteTo(writer);
            else
                writer.WriteNullValue();

            writer.WritePropertyName("balance");

            if (balanceJson != null)
                balanceJson.RootElement.WriteTo(writer);
            else
                writer.WriteNullValue();

            writer.WritePropertyName("cashflow");

            if (cashFlowJson != null)
                cashFlowJson.RootElement.WriteTo(writer);
            else
                writer.WriteNullValue();

            writer.WriteEndObject();
            writer.Flush();

            return JsonDocument.Parse(stream.ToArray());
        }
    }
}
