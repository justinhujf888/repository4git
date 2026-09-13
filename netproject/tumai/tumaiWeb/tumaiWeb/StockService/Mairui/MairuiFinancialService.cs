using Microsoft.EntityFrameworkCore;
using System.Text.Json;
using tumaiWeb.Data.Entities;
using Npgsql;
using NpgsqlTypes;
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


        /*
         - **推荐：每周 1 次，周日凌晨 02:00 执行**
- 可选保守方案：每 10 天一次
- 不建议：每日拉取，极度浪费额度
- 执行逻辑：批量遍历股票列表，Upsert 更新 StockBasic；失败股票记录日志，重试。
         */
        public async Task SaveStockBasicsData(string stockBasicsJson)
        {
            var stockDictList = JsonSerializer.Deserialize<List<Dictionary<string, object>>>(stockBasicsJson) ?? new();
            List<StockBasic> stockBasicEntities = new();
            var now = DateTime.UtcNow;
            foreach (var dict in stockDictList)
            {
                var dm = MairuiDictHelper.GetString(dict,"dm");
                var mc = MairuiDictHelper.GetString(dict,"mc");
                var jys = MairuiDictHelper.GetString(dict!,"jys").Trim().ToUpper(); // 接口返回小写sh/sz，统一转为大写 SH/SZ

                var entity = new StockBasic
                {
                    StockCode = dm.Split(".")[0],
                    StockName = mc,
                    Market = jys,
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
            var incomeDictList = JsonSerializer.Deserialize<List<Dictionary<string, object>>>(profitJson) ?? new();
            var balanceDictList = JsonSerializer.Deserialize<List<Dictionary<string, object>>>(balanceJson) ?? new();
            var cashDictList = JsonSerializer.Deserialize<List<Dictionary<string, object>>>(cashJson) ?? new();

            // 2. 利润表：字典映射到EF实体
            List<IncomeStatementItemDto> incomeEntities = new();
            foreach (var dict in incomeDictList)
            {
                var jzrq = MairuiDictHelper.GetString(dict, "jzrq")!;
                var plrq = MairuiDictHelper.GetString(dict, "plrq");

                var entity = new IncomeStatementItemDto
                {
                    StockCode = sm[0],
                    Market = sm[1],
                    ReportDate = jzrq,
                    PublishDate = plrq,
                    ReportType = MairuiDictHelper.GetReportType(jzrq),

                    // 收入
                    TotalRevenue = MairuiDictHelper.GetDecimal(dict, "yyzsr"),        // 营业总收入
                    OperatingRevenue = MairuiDictHelper.GetDecimal(dict, "yysr"),     // 营业收入

                    // 成本费用
                    OperatingCost = MairuiDictHelper.GetDecimal(dict, "yycb"),        // 营业成本
                    TaxAndSurcharges = MairuiDictHelper.GetDecimal(dict, "yysjjfj"),  // 营业税金及附加
                    SellingExpense = MairuiDictHelper.GetDecimal(dict, "xsfy"),       // 销售费用
                    AdminExpense = MairuiDictHelper.GetDecimal(dict, "glfy"),         // 管理费用
                    RndExpense = MairuiDictHelper.GetDecimal(dict, "yffy"),           // 研发费用
                    FinanceExpense = MairuiDictHelper.GetDecimal(dict, "cwfy"),       // 财务费用
                    InterestExpense = MairuiDictHelper.GetDecimal(dict, "lxzc"),      // 利息支出
                    InterestIncome = MairuiDictHelper.GetDecimal(dict, "lxsr"),       // 利息收入

                    // 利润
                    OperatingProfit = MairuiDictHelper.GetDecimal(dict, "yylr"),      // 营业利润
                    NonOperatingIncome = MairuiDictHelper.GetDecimal(dict, "ywsr"),   // 营业外收入
                    NonOperatingExpense = MairuiDictHelper.GetDecimal(dict, "ywzc"),  // 营业外支出
                    TotalProfit = MairuiDictHelper.GetDecimal(dict, "lrzef")          // 利润总额（带 f）
                 ?? MairuiDictHelper.GetDecimal(dict, "lrze"),        // 双 key 兜底
                    IncomeTaxExpense = MairuiDictHelper.GetDecimal(dict, "sdsfy"),    // 所得税费用

                    // 净利润
                    NetProfit = MairuiDictHelper.GetDecimal(dict, "jlr"),             // 净利润
                    NetProfitParent = MairuiDictHelper.GetDecimal(dict, "gsmgsyzzdjlr"), // 归母净利润
                    MinorityProfit = MairuiDictHelper.GetDecimal(dict, "ssgdsy"),     // 少数股东损益
                    DeductNonProfit = MairuiDictHelper.GetDecimal(dict, "jlrhfcjcx"), // 扣非净利润

                    // 每股收益
                    BasicEps = MairuiDictHelper.GetDecimal(dict, "jbmgsy"),           // 基本每股收益
                    DilutedEps = MairuiDictHelper.GetDecimal(dict, "xsmgsy"),         // 稀释每股收益

                    RawJson = dict,
                    CreateTime = DateTime.UtcNow
                };
                incomeEntities.Add(entity);
            }

            // 3. 资产负债表：字典映射到EF实体
            List<BalanceSheetItemDto> balanceEntities = new();
            foreach (var dict in balanceDictList)
            {
                var jzrq = MairuiDictHelper.GetString(dict, "jzrq")!;
                var plrq = MairuiDictHelper.GetString(dict, "plrq");

                var entity = new BalanceSheetItemDto
                {
                    StockCode = sm[0],
                    Market = sm[1],
                    ReportDate = jzrq,
                    PublishDate = plrq,
                    ReportType = MairuiDictHelper.GetReportType(jzrq),

                    //===== 资产部分 =====
                    TotalCurrentAsset = MairuiDictHelper.GetDecimal(dict, "ldzchj"),           //流动资产合计
                    MonetaryFund = MairuiDictHelper.GetDecimal(dict, "hbzj"),                  //货币资金
                    TradingFinancialAsset = MairuiDictHelper.GetDecimal(dict, "jyxjrzcf"),    //交易性金融资产
                    BillReceivable = MairuiDictHelper.GetDecimal(dict, "yspj"),               //应收票据
                    AccountReceivable = MairuiDictHelper.GetDecimal(dict, "yszk"),            //应收账款
                    Prepayment = MairuiDictHelper.GetDecimal(dict, "yfkx"),                   //预付款项
                    Inventory = MairuiDictHelper.GetDecimal(dict, "ch"),                      //存货
                    OtherCurrentAsset = MairuiDictHelper.GetDecimal(dict, "qtldzcf"),          //其他流动资产

                    TotalNonCurrentAsset = MairuiDictHelper.GetDecimal(dict, "fldzchj"),      //非流动资产合计
                    FixedAssetOriginal = MairuiDictHelper.GetDecimal(dict, "gdzcyz"),         //固定资产原值
                    FixedAssetNet = MairuiDictHelper.GetDecimal(dict, "gdzcjz"),              //固定资产净值
                    IntangibleAsset = MairuiDictHelper.GetDecimal(dict, "wxzcf"),             //无形资产
                    Goodwill = MairuiDictHelper.GetDecimal(dict, "sy"),                       //商誉
                    LongTermEquityInvest = MairuiDictHelper.GetDecimal(dict, "cqgqtz"),       //长期股权投资
                    TotalAsset = MairuiDictHelper.GetDecimal(dict, "zczj"),                   //资产总计

                    //===== 负债部分 =====
                    TotalCurrentLiability = MairuiDictHelper.GetDecimal(dict, "ldfzhj"),      //流动负债合计
                    ShortTermLoan = MairuiDictHelper.GetDecimal(dict, "dqjk"),                 //短期借款
                    BillPayable = MairuiDictHelper.GetDecimal(dict, "yfpj"),                  //应付票据
                    AccountPayable = MairuiDictHelper.GetDecimal(dict, "yfzk"),               //应付账款
                    AdvanceReceived = MairuiDictHelper.GetDecimal(dict, "ysk"),               //预收账款
                    SalaryPayable = MairuiDictHelper.GetDecimal(dict, "yfgzxcf"),             //应付职工薪酬
                    TaxPayable = MairuiDictHelper.GetDecimal(dict, "yjsf"),                   //应交税费

                    TotalNonCurrentLiability = MairuiDictHelper.GetDecimal(dict, "fldfzhj"),  //非流动负债合计
                    LongTermLoan = MairuiDictHelper.GetDecimal(dict, "cqjk"),                 //长期借款
                    BondPayable = MairuiDictHelper.GetDecimal(dict, "yfzq"),                  //应付债券
                    TotalLiability = MairuiDictHelper.GetDecimal(dict, "fzhj"),              //负债合计

                    //===== 所有者权益 =====
                    TotalEquity = MairuiDictHelper.GetDecimal(dict, "syzqyhj"),               //所有者权益合计
                    PaidInCapital = MairuiDictHelper.GetDecimal(dict, "sszbf"),               //实收资本(或股本)
                    CapitalReserve = MairuiDictHelper.GetDecimal(dict, "zbgj"),               //资本公积
                    SurplusReserve = MairuiDictHelper.GetDecimal(dict, "ylgj"),               //盈余公积
                    UndistributedProfit = MairuiDictHelper.GetDecimal(dict, "wfplr"),         //未分配利润
                    ParentEquity = MairuiDictHelper.GetDecimal(dict, "gsmgdqsyhj"),           //归属于母公司股东权益合计
                    MinorityEquity = MairuiDictHelper.GetDecimal(dict, "ssgdqy"),             //少数股东权益

                    RawJson = dict,
                    CreateTime = DateTime.UtcNow
                };
                balanceEntities.Add(entity);
            }

            //4. 现金流量表：字典映射到EF实体
            List<CashFlowItemDto> cashEntities = new();
            foreach (var dict in cashDictList)
            {
                var jzrq = MairuiDictHelper.GetString(dict, "jzrq")!;
                var plrq = MairuiDictHelper.GetString(dict, "plrq");

                var entity = new CashFlowItemDto
                {
                    StockCode = sm[0],
                    Market = sm[1],
                    ReportDate = jzrq,
                    PublishDate = plrq,
                    ReportType = MairuiDictHelper.GetReportType(jzrq),

                    // ===== 经营活动 =====
                    OperateCashIn = MairuiDictHelper.GetDecimal(dict, "jyhdxjlrxj"),       // 经营活动现金流入小计
                    OperateCashOut = MairuiDictHelper.GetDecimal(dict, "jyhdxjlcxj"),      // 经营活动现金流出小计
                    NetOperateCashFlow = MairuiDictHelper.GetDecimal(dict, "jyhdcsdxjlxj"),// 经营净额（JSON实际有值，优先用它）
                    CashFromSales = MairuiDictHelper.GetDecimal(dict, "xssptglwsddxj"),    // 销售商品、提供劳务收到的现金
                    TaxRefundReceived = MairuiDictHelper.GetDecimal(dict, "sddsfyfh"),     // 收到的税费返还
                    OtherOperateCashIn = MairuiDictHelper.GetDecimal(dict, "sdqtyjyghdxj"),// 收到其他与经营活动有关的现金
                    CashPayForGoods = MairuiDictHelper.GetDecimal(dict, "gmspjslwzfdxj"),  // 购买商品、接受劳务支付的现金
                    CashPayToStaff = MairuiDictHelper.GetDecimal(dict, "zfgzyjwzgzfdxj"),  // 支付给职工以及为职工支付的现金
                    TaxPaid = MairuiDictHelper.GetDecimal(dict, "zfdgxsf"),                // 支付的各项税费
                    OtherOperateCashOut = MairuiDictHelper.GetDecimal(dict, "zfqtyjyghdxj"),// 支付其他与经营活动有关的现金

                    // ===== 投资活动 =====
                    InvestCashIn = MairuiDictHelper.GetDecimal(dict, "tzhdxjlrxj"),        // 投资活动现金流入小计
                    InvestCashOut = MairuiDictHelper.GetDecimal(dict, "tzhdxjlcxj"),       // 投资活动现金流出小计
                    NetInvestCashFlow = MairuiDictHelper.GetDecimal(dict, "tzhdcsdxjlxj"), // 投资净额
                    CashFromInvestRecall = MairuiDictHelper.GetDecimal(dict, "shtzssddxj"),// 收回投资收到的现金
                    CashFromInvestIncome = MairuiDictHelper.GetDecimal(dict, "qdtzsysddxj"),// 取得投资收益收到的现金
                    CashFromDisposeLongAsset = MairuiDictHelper.GetDecimal(dict, "czgdzcwxzhqtqctzssddxj"), // 处置长期资产收到的现金
                    Capex = MairuiDictHelper.GetDecimal(dict, "gjgdzcwxzhqtqctzzfdxj"),    // 资本开支
                    CashPayForInvest = MairuiDictHelper.GetDecimal(dict, "tzszfdxj"),      // 投资支付的现金（JSON里有值）

                    // ===== 筹资活动 =====
                    FinanceCashIn = MairuiDictHelper.GetDecimal(dict, "czhdxjlrxj"),       // 筹资活动现金流入小计
                    FinanceCashOut = MairuiDictHelper.GetDecimal(dict, "czhdxjlcxj"),      // 筹资活动现金流出小计
                    NetFinanceCashFlow = MairuiDictHelper.GetDecimal(dict, "czhdcsdxjlxj"),// 筹资净额
                    CashFromEquity = MairuiDictHelper.GetDecimal(dict, "xstzsdj"),         // 吸收投资收到的现金
                    CashFromBorrow = MairuiDictHelper.GetDecimal(dict, "qdjkjddxj"),       // 取得借款收到的现金
                    CashRepayDebt = MairuiDictHelper.GetDecimal(dict, "chzwzfxj"),         // 偿还债务支付的现金
                    CashPayDividendInterest = MairuiDictHelper.GetDecimal(dict, "fpglrlhcllxzfdxj"), // 分配股利、利润或偿付利息

                    // ===== 现金及现金等价物 =====
                    ForexEffectOnCash = MairuiDictHelper.GetDecimal(dict, "hlbddxjdxy"),   // 汇率变动对现金的影响
                    NetIncreaseCash = MairuiDictHelper.GetDecimal(dict, "xjxjdhwjzje"),    // 现金及现金等价物净增加额
                    BeginCashBalance = MairuiDictHelper.GetDecimal(dict, "qcxjjxjdhwye"),  // 期初余额
                    EndCashBalance = MairuiDictHelper.GetDecimal(dict, "qmxjjxjdhwye"),    // 期末余额

                    RawJson = dict,
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
                await _baseService.AddObjectRangeAsync(stockFinancialReportList);
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
        public async Task SaveStockSsjy4ManyStkData(List<string> stockCodeWithMarketList, string stockQuoteJsonArray)
        {
            var rawDataArray = JsonSerializer.Deserialize<List<Dictionary<string, object?>>>(stockQuoteJsonArray) ?? new();
            var snapshotEntities = new List<StockQuoteSnapshot>();
            for (int i = 0; i < rawDataArray.Count; i++)
            {
                var dict = rawDataArray[i];
                var sm = stockCodeWithMarketList[i].Split(".");
                var entity = new StockQuoteSnapshot
                {
                    StockCode = sm[0],
                    Market = sm[1],
                    PullTime = DateTime.UtcNow,

                    Price = MairuiDictHelper.GetDecimal(dict, "p"),
                    YesterdayClose = MairuiDictHelper.GetDecimal(dict, "yc"),
                    Open = MairuiDictHelper.GetDecimal(dict, "o"),
                    High = MairuiDictHelper.GetDecimal(dict, "h"),
                    Low = MairuiDictHelper.GetDecimal(dict, "l"),
                    ChangePercent = MairuiDictHelper.GetDecimal(dict, "pc"),
                    ChangeAmount = MairuiDictHelper.GetDecimal(dict, "ud"),

                    Volume = MairuiDictHelper.GetLong(dict, "v"),
                    Turnover = MairuiDictHelper.GetDecimal(dict, "cje"),
                    TotalMarketValue = null,
                    TvVolume = MairuiDictHelper.GetLong(dict, "tv"),
                    PvVolume = MairuiDictHelper.GetLong(dict, "pv"),

                    Pe = MairuiDictHelper.GetDecimal(dict, "pe"),
                    PbRatio = MairuiDictHelper.GetDecimal(dict, "pb_ratio"),
                    TurnoverRate = MairuiDictHelper.GetDecimal(dict, "tr"),
                    Amplitude = MairuiDictHelper.GetDecimal(dict, "zf"),
                    FiveMinChange = null,

                    SnapshotTime = MairuiDictHelper.GetDateTime(dict, "t") ?? DateTime.UtcNow,
                    RawJson = dict
                };
                entity.CreateTime = entity.PullTime;
                snapshotEntities.Add(entity);
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
                await _baseService.AddObjectAsync(item);
            }
        }

        private async Task UpsertBalance(List<BalanceSheetItemDto> list)
        {
            foreach (var item in list)
            {
                await _baseService.AddObjectAsync(item);
            }
        }

        private async Task UpsertCashFlow(List<CashFlowItemDto> list)
        {
            foreach (var item in list)
            {
                await _baseService.AddObjectAsync(item);
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
                await _baseService.AddObjectAsync(item);
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
            // 1. 构建字典：key=ReportDate字符串 yyyy-MM-dd
            var incomeDict = incomeItems.ToDictionary(x => x.ReportDate);
            var balanceDict = balanceItems.ToDictionary(x => x.ReportDate);
            var cashDict = cashItems.ToDictionary(x => x.ReportDate);

            var resultList = new List<StockFinancialReport>();

            foreach (var (reportDateStr, incomeDto) in incomeDict)
            {
                // 尝试取出同期资产负债、现金流
                balanceDict.TryGetValue(reportDateStr, out var balanceDto);
                cashDict.TryGetValue(reportDateStr, out var cashDto);

                // 字符串日期转为DateOnly
                if (!DateOnly.TryParse(reportDateStr, out DateOnly reportDate))
                {
                    continue;
                }

                var report = new StockFinancialReport
                {
                    StockCode = incomeDto.StockCode,
                    Market = incomeDto.Market,
                    ReportDate = reportDate,
                    ReportType = incomeDto.ReportType,
                    DiscloseDate = DateOnly.TryParse(incomeDto.PublishDate, out var dt) ? dt : null,

                    // ========== 利润表映射（IncomeStatementItemDto） ==========
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

                    // ========== 资产负债表映射（BalanceSheetItemDto） ==========
                    TotalAssets = balanceDto?.TotalAsset,
                    TotalLiabilities = balanceDto?.TotalLiability,
                    ShareholdersEquity = balanceDto?.TotalEquity,

                    // ========== 现金流量表映射（CashFlowItemDto） ==========
                    OperatingCashFlow = cashDto?.NetOperateCashFlow,
                    InvestingCashFlow = cashDto?.NetInvestCashFlow,
                    FinancingCashFlow = cashDto?.NetFinanceCashFlow,

                    PullTime = DateTime.UtcNow,

                    // 原始jsonb合并存储
                    RawJson = new Dictionary<string, object?>
                    {
                        ["income"] = incomeDto.RawJson,
                        ["balance"] = balanceDto?.RawJson,
                        ["cashflow"] = cashDto?.RawJson
                    }
                };

                // shareholdersEquity 来自资产负债表，totalShare 来自StockBasic总股本
                //if (report.ShareholdersEquity.HasValue && totalShare.HasValue && totalShare != 0)
                //{
                //    report.NetAssetPerShare = report.ShareholdersEquity / totalShare;
                //}
                //else
                //{
                //    report.NetAssetPerShare = null;
                //}
                // 计算衍生指标：毛利率、净利率、资产负债率、ROE
                CalcFinancialInnerIndicator(report);
                resultList.Add(report);
            }

            return resultList;
        }
    }
}
