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

                    TotalRevenue = MairuiDictHelper.GetDecimal(dict, "yysr"),
                    OperatingRevenue = MairuiDictHelper.GetDecimal(dict, "yyjr"),
                    OperatingCost = MairuiDictHelper.GetDecimal(dict, "yycb"),
                    TaxAndSurcharges = MairuiDictHelper.GetDecimal(dict, "sjjfj"),
                    SellingExpense = MairuiDictHelper.GetDecimal(dict, "xsfy"),
                    AdminExpense = MairuiDictHelper.GetDecimal(dict, "glfy"),
                    RndExpense = MairuiDictHelper.GetDecimal(dict, "yffy"),
                    FinanceExpense = MairuiDictHelper.GetDecimal(dict, "cwfy"),
                    InterestExpense = MairuiDictHelper.GetDecimal(dict, "lxfy"),
                    InterestIncome = MairuiDictHelper.GetDecimal(dict, "lxsr"),

                    OperatingProfit = MairuiDictHelper.GetDecimal(dict, "yylr"),
                    NonOperatingIncome = MairuiDictHelper.GetDecimal(dict, "yywsr"),
                    NonOperatingExpense = MairuiDictHelper.GetDecimal(dict, "yywzc"),
                    TotalProfit = MairuiDictHelper.GetDecimal(dict, "lrze"),
                    IncomeTaxExpense = MairuiDictHelper.GetDecimal(dict, "sdsfy"),
                    NetProfit = MairuiDictHelper.GetDecimal(dict, "jlr"),
                    NetProfitParent = MairuiDictHelper.GetDecimal(dict, "gsmgsyzzdjlr"),
                    MinorityProfit = MairuiDictHelper.GetDecimal(dict, "sdsgsy"),
                    DeductNonProfit = MairuiDictHelper.GetDecimal(dict, "jlrhfcjcx"),

                    BasicEps = MairuiDictHelper.GetDecimal(dict, "jbmgsy"),
                    DilutedEps = MairuiDictHelper.GetDecimal(dict, "xsmgsy"),

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

                    TotalCurrentAsset = MairuiDictHelper.GetDecimal(dict, "ldzczhj"),
                    MonetaryFund = MairuiDictHelper.GetDecimal(dict, "hbzj"),
                    TradingFinancialAsset = MairuiDictHelper.GetDecimal(dict, "jyjxjrzzc"),
                    BillReceivable = MairuiDictHelper.GetDecimal(dict, "yspj"),
                    AccountReceivable = MairuiDictHelper.GetDecimal(dict, "yszk"),
                    Prepayment = MairuiDictHelper.GetDecimal(dict, "yfkx"),
                    Inventory = MairuiDictHelper.GetDecimal(dict, "ch"),
                    OtherCurrentAsset = MairuiDictHelper.GetDecimal(dict, "qtldzc"),
                    TotalNonCurrentAsset = MairuiDictHelper.GetDecimal(dict, "fldzczhj"),
                    FixedAssetOriginal = MairuiDictHelper.GetDecimal(dict, "gdzcyz"),
                    FixedAssetNet = MairuiDictHelper.GetDecimal(dict, "gdzcjingzhi"),
                    IntangibleAsset = MairuiDictHelper.GetDecimal(dict, "wxzc"),
                    Goodwill = MairuiDictHelper.GetDecimal(dict, "sy"),
                    LongTermEquityInvest = MairuiDictHelper.GetDecimal(dict, "cqgqtz"),
                    TotalAsset = MairuiDictHelper.GetDecimal(dict, "zczj"),

                    TotalCurrentLiability = MairuiDictHelper.GetDecimal(dict, "ldfzhj"),
                    ShortTermLoan = MairuiDictHelper.GetDecimal(dict, "dqjk"),
                    BillPayable = MairuiDictHelper.GetDecimal(dict, "yfpj"),
                    AccountPayable = MairuiDictHelper.GetDecimal(dict, "yfzk"),
                    AdvanceReceived = MairuiDictHelper.GetDecimal(dict, "yskx"),
                    SalaryPayable = MairuiDictHelper.GetDecimal(dict, "yfzgxc"),
                    TaxPayable = MairuiDictHelper.GetDecimal(dict, "yjsf"),
                    TotalNonCurrentLiability = MairuiDictHelper.GetDecimal(dict, "fldfzhj"),
                    LongTermLoan = MairuiDictHelper.GetDecimal(dict, "cqjk"),
                    BondPayable = MairuiDictHelper.GetDecimal(dict, "yfzhai"),
                    TotalLiability = MairuiDictHelper.GetDecimal(dict, "fzhj"),

                    TotalEquity = MairuiDictHelper.GetDecimal(dict, "syzqyhj"),
                    PaidInCapital = MairuiDictHelper.GetDecimal(dict, "sszb"),
                    CapitalReserve = MairuiDictHelper.GetDecimal(dict, "cbgj"),
                    SurplusReserve = MairuiDictHelper.GetDecimal(dict, "yygj"),
                    UndistributedProfit = MairuiDictHelper.GetDecimal(dict, "wfplr"),
                    ParentEquity = MairuiDictHelper.GetDecimal(dict, "gsmgsyqy"),
                    MinorityEquity = MairuiDictHelper.GetDecimal(dict, "sdsgsyqy"),

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

                    OperateCashIn = MairuiDictHelper.GetDecimal(dict, "jyhdxjlrxj"),
                    OperateCashOut = MairuiDictHelper.GetDecimal(dict, "jyhdxjlcxj"),
                    NetOperateCashFlow = MairuiDictHelper.GetDecimal(dict, "jyhdcsdxjlxj"),
                    CashFromSales = MairuiDictHelper.GetDecimal(dict, "spspsdlsdxd"),
                    TaxRefundReceived = MairuiDictHelper.GetDecimal(dict, "sdshsdfh"),
                    OtherOperateCashIn = MairuiDictHelper.GetDecimal(dict, "sqtyjyhdgdcash"),
                    CashPayForGoods = MairuiDictHelper.GetDecimal(dict, "gmspjslwzfdxj"),
                    CashPayToStaff = MairuiDictHelper.GetDecimal(dict, "zfzgjywzgzfdxj"),
                    TaxPaid = MairuiDictHelper.GetDecimal(dict, "zfgxgsf"),
                    OtherOperateCashOut = MairuiDictHelper.GetDecimal(dict, "zfqtjyhdgdcash"),

                    InvestCashIn = MairuiDictHelper.GetDecimal(dict, "tzhdxjlrxj"),
                    InvestCashOut = MairuiDictHelper.GetDecimal(dict, "tzhdxjlcxj"),
                    NetInvestCashFlow = MairuiDictHelper.GetDecimal(dict, "tzhdcsdxjlxj"),
                    CashFromInvestRecall = MairuiDictHelper.GetDecimal(dict, "shtzsdxd"),
                    CashFromInvestIncome = MairuiDictHelper.GetDecimal(dict, "qdtzsyjsdxd"),
                    CashFromDisposeLongAsset = MairuiDictHelper.GetDecimal(dict, "czgdzcwxzcqtcqzczcsdxjje"),
                    Capex = MairuiDictHelper.GetDecimal(dict, "gjgdzcwxzcqtcqzczzfdxj"),
                    CashPayForInvest = MairuiDictHelper.GetDecimal(dict, "tzzfdxj"),

                    FinanceCashIn = MairuiDictHelper.GetDecimal(dict, "czhdxjlrxj"),
                    FinanceCashOut = MairuiDictHelper.GetDecimal(dict, "czhdxjlcxj"),
                    NetFinanceCashFlow = MairuiDictHelper.GetDecimal(dict, "czhdcsdxjlxj"),
                    CashFromEquity = MairuiDictHelper.GetDecimal(dict, "xstzsdxd"),
                    CashFromBorrow = MairuiDictHelper.GetDecimal(dict, "qdjkjsdxd"),
                    CashRepayDebt = MairuiDictHelper.GetDecimal(dict, "chzwzfdxj"),
                    CashPayDividendInterest = MairuiDictHelper.GetDecimal(dict, "fpglrlshlxzfdxj"),

                    ForexEffectOnCash = MairuiDictHelper.GetDecimal(dict, "hlbddxjjjdjwdyx"),
                    NetIncreaseCash = MairuiDictHelper.GetDecimal(dict, "xjjjdjwdjzjje"),
                    BeginCashBalance = MairuiDictHelper.GetDecimal(dict, "qcxjjjdjwye"),
                    EndCashBalance = MairuiDictHelper.GetDecimal(dict, "qmxjjjdjwye"),

                    RawJson = dict,
                    CreateTime = DateTime.UtcNow
                };
                cashEntities.Add(entity);
            }

            // ========== Upsert批量入库 ==========
            await UpsertIncome(DistinctKeepMaxDateString(incomeEntities,
    groupKeySelector: x => new { x.StockCode, x.ReportDate, x.Market },
    dateStrSelector: x => x.PublishDate
));
            await UpsertBalance(DistinctKeepMaxDateString(balanceEntities, groupKeySelector: x => new { x.StockCode, x.ReportDate, x.Market },
    dateStrSelector: x => x.PublishDate));
            await UpsertCashFlow(DistinctKeepMaxDateString(cashEntities, groupKeySelector: x => new { x.StockCode, x.ReportDate, x.Market },
    dateStrSelector: x => x.PublishDate));
        }

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
                    CreateTime = DateTime.UtcNow,

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
                snapshotEntities.Add(entity);
            }
            await _baseService.AddObjectRangeAsync(snapshotEntities);
        }

        #region 原生PostgreSQL Upsert（无第三方包，适配新版Npgsql，JsonB修复）
        private async Task UpsertIncome(List<IncomeStatementItemDto> list)
        {
            foreach (var item in list)
            {
                await _baseService.UpdateObjectAsync(item);
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
    }
}
