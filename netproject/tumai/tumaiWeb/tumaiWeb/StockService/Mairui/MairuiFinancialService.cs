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
                    UpdateTime = now,
                    Deleted = false
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
            await UpsertIncome(incomeEntities);
            await UpsertBalance(balanceEntities);
            await UpsertCashFlow(cashEntities);
        }

        public async Task SaveStockQuoteSnapshotData(string stockCode, string stockQuoteSnapshotJson)
        {
            var rawDict = JsonSerializer.Deserialize<Dictionary<string, object>>(stockQuoteSnapshotJson) ?? new();

        }

        #region 原生PostgreSQL Upsert（无第三方包，适配新版Npgsql，JsonB修复）
        private async Task UpsertIncome(List<IncomeStatementItemDto> list)
        {
            foreach (var item in list)
            {
                const string sql = @"
INSERT INTO ""stock_income_statement"" (
    ""StockCode"", ""ReportDate"", ""PublishDate"", ""ReportType"",
    ""TotalRevenue"", ""OperatingRevenue"", ""OperatingCost"", ""TaxAndSurcharges"",
    ""SellingExpense"", ""AdminExpense"", ""RndExpense"", ""FinanceExpense"",
    ""InterestExpense"", ""InterestIncome"", ""OperatingProfit"", ""NonOperatingIncome"",
    ""NonOperatingExpense"", ""TotalProfit"", ""IncomeTaxExpense"", ""NetProfit"",
    ""NetProfitParent"", ""MinorityProfit"", ""DeductNonProfit"", ""BasicEps"",
    ""DilutedEps"", ""RawJson"", ""CreateTime""
)
VALUES (
    @StockCode, @ReportDate, @PublishDate, @ReportType,
    @TotalRevenue, @OperatingRevenue, @OperatingCost, @TaxAndSurcharges,
    @SellingExpense, @AdminExpense, @RndExpense, @FinanceExpense,
    @InterestExpense, @InterestIncome, @OperatingProfit, @NonOperatingIncome,
    @NonOperatingExpense, @TotalProfit, @IncomeTaxExpense, @NetProfit,
    @NetProfitParent, @MinorityProfit, @DeductNonProfit, @BasicEps,
    @DilutedEps, @RawJson, @CreateTime
)
ON CONFLICT (""StockCode"", ""ReportDate"") DO UPDATE
SET
    ""PublishDate"" = EXCLUDED.""PublishDate"",
    ""ReportType"" = EXCLUDED.""ReportType"",
    ""TotalRevenue"" = EXCLUDED.""TotalRevenue"",
    ""OperatingRevenue"" = EXCLUDED.""OperatingRevenue"",
    ""OperatingCost"" = EXCLUDED.""OperatingCost"",
    ""TaxAndSurcharges"" = EXCLUDED.""TaxAndSurcharges"",
    ""SellingExpense"" = EXCLUDED.""SellingExpense"",
    ""AdminExpense"" = EXCLUDED.""AdminExpense"",
    ""RndExpense"" = EXCLUDED.""RndExpense"",
    ""FinanceExpense"" = EXCLUDED.""FinanceExpense"",
    ""InterestExpense"" = EXCLUDED.""InterestExpense"",
    ""InterestIncome"" = EXCLUDED.""InterestIncome"",
    ""OperatingProfit"" = EXCLUDED.""OperatingProfit"",
    ""NonOperatingIncome"" = EXCLUDED.""NonOperatingIncome"",
    ""NonOperatingExpense"" = EXCLUDED.""NonOperatingExpense"",
    ""TotalProfit"" = EXCLUDED.""TotalProfit"",
    ""IncomeTaxExpense"" = EXCLUDED.""IncomeTaxExpense"",
    ""NetProfit"" = EXCLUDED.""NetProfit"",
    ""NetProfitParent"" = EXCLUDED.""NetProfitParent"",
    ""MinorityProfit"" = EXCLUDED.""MinorityProfit"",
    ""DeductNonProfit"" = EXCLUDED.""DeductNonProfit"",
    ""BasicEps"" = EXCLUDED.""BasicEps"",
    ""DilutedEps"" = EXCLUDED.""DilutedEps"",
    ""RawJson"" = EXCLUDED.""RawJson"",
    ""CreateTime"" = EXCLUDED.""CreateTime"";
";
                await _db.Database.ExecuteSqlRawAsync(sql,
                    new NpgsqlParameter("@StockCode", item.StockCode),
                    new NpgsqlParameter("@ReportDate", item.ReportDate),
                    new NpgsqlParameter("@PublishDate", item.PublishDate ?? (object)DBNull.Value),
                    new NpgsqlParameter("@ReportType", item.ReportType),
                    new NpgsqlParameter("@TotalRevenue", item.TotalRevenue ?? (object)DBNull.Value),
                    new NpgsqlParameter("@OperatingRevenue", item.OperatingRevenue ?? (object)DBNull.Value),
                    new NpgsqlParameter("@OperatingCost", item.OperatingCost ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TaxAndSurcharges", item.TaxAndSurcharges ?? (object)DBNull.Value),
                    new NpgsqlParameter("@SellingExpense", item.SellingExpense ?? (object)DBNull.Value),
                    new NpgsqlParameter("@AdminExpense", item.AdminExpense ?? (object)DBNull.Value),
                    new NpgsqlParameter("@RndExpense", item.RndExpense ?? (object)DBNull.Value),
                    new NpgsqlParameter("@FinanceExpense", item.FinanceExpense ?? (object)DBNull.Value),
                    new NpgsqlParameter("@InterestExpense", item.InterestExpense ?? (object)DBNull.Value),
                    new NpgsqlParameter("@InterestIncome", item.InterestIncome ?? (object)DBNull.Value),
                    new NpgsqlParameter("@OperatingProfit", item.OperatingProfit ?? (object)DBNull.Value),
                    new NpgsqlParameter("@NonOperatingIncome", item.NonOperatingIncome ?? (object)DBNull.Value),
                    new NpgsqlParameter("@NonOperatingExpense", item.NonOperatingExpense ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TotalProfit", item.TotalProfit ?? (object)DBNull.Value),
                    new NpgsqlParameter("@IncomeTaxExpense", item.IncomeTaxExpense ?? (object)DBNull.Value),
                    new NpgsqlParameter("@NetProfit", item.NetProfit ?? (object)DBNull.Value),
                    new NpgsqlParameter("@NetProfitParent", item.NetProfitParent ?? (object)DBNull.Value),
                    new NpgsqlParameter("@MinorityProfit", item.MinorityProfit ?? (object)DBNull.Value),
                    new NpgsqlParameter("@DeductNonProfit", item.DeductNonProfit ?? (object)DBNull.Value),
                    new NpgsqlParameter("@BasicEps", item.BasicEps ?? (object)DBNull.Value),
                    new NpgsqlParameter("@DilutedEps", item.DilutedEps ?? (object)DBNull.Value),
                    new NpgsqlParameter("@RawJson", JsonSerializer.Serialize(item.RawJson, _jsonOpts))
                    {
                        NpgsqlDbType = NpgsqlDbType.Jsonb
                    },
                    new NpgsqlParameter("@CreateTime", item.CreateTime)
                );
            }
        }

        private async Task UpsertBalance(List<BalanceSheetItemDto> list)
        {
            foreach (var item in list)
            {
                const string sql = @"
INSERT INTO ""stock_balance_sheet"" (
    ""StockCode"", ""ReportDate"", ""PublishDate"", ""ReportType"",
    ""TotalCurrentAsset"", ""MonetaryFund"", ""TradingFinancialAsset"", ""BillReceivable"",
    ""AccountReceivable"", ""Prepayment"", ""Inventory"", ""OtherCurrentAsset"",
    ""TotalNonCurrentAsset"", ""FixedAssetOriginal"", ""FixedAssetNet"", ""IntangibleAsset"",
    ""Goodwill"", ""LongTermEquityInvest"", ""TotalAsset"", ""TotalCurrentLiability"",
    ""ShortTermLoan"", ""BillPayable"", ""AccountPayable"", ""AdvanceReceived"",
    ""SalaryPayable"", ""TaxPayable"", ""TotalNonCurrentLiability"", ""LongTermLoan"",
    ""BondPayable"", ""TotalLiability"", ""TotalEquity"", ""PaidInCapital"",
    ""CapitalReserve"", ""SurplusReserve"", ""UndistributedProfit"", ""ParentEquity"",
    ""MinorityEquity"", ""RawJson"", ""CreateTime""
)
VALUES (
    @StockCode, @ReportDate, @PublishDate, @ReportType,
    @TotalCurrentAsset, @MonetaryFund, @TradingFinancialAsset, @BillReceivable,
    @AccountReceivable, @Prepayment, @Inventory, @OtherCurrentAsset,
    @TotalNonCurrentAsset, @FixedAssetOriginal, @FixedAssetNet, @IntangibleAsset,
    @Goodwill, @LongTermEquityInvest, @TotalAsset, @TotalCurrentLiability,
    @ShortTermLoan, @BillPayable, @AccountPayable, @AdvanceReceived,
    @SalaryPayable, @TaxPayable, @TotalNonCurrentLiability, @LongTermLoan,
    @BondPayable, @TotalLiability, @TotalEquity, @PaidInCapital,
    @CapitalReserve, @SurplusReserve, @UndistributedProfit, @ParentEquity,
    @MinorityEquity, @RawJson, @CreateTime
)
ON CONFLICT (""StockCode"", ""ReportDate"") DO UPDATE
SET
    ""PublishDate"" = EXCLUDED.""PublishDate"",
    ""ReportType"" = EXCLUDED.""ReportType"",
    ""TotalCurrentAsset"" = EXCLUDED.""TotalCurrentAsset"",
    ""MonetaryFund"" = EXCLUDED.""MonetaryFund"",
    ""TradingFinancialAsset"" = EXCLUDED.""TradingFinancialAsset"",
    ""BillReceivable"" = EXCLUDED.""BillReceivable"",
    ""AccountReceivable"" = EXCLUDED.""AccountReceivable"",
    ""Prepayment"" = EXCLUDED.""Prepayment"",
    ""Inventory"" = EXCLUDED.""Inventory"",
    ""OtherCurrentAsset"" = EXCLUDED.""OtherCurrentAsset"",
    ""TotalNonCurrentAsset"" = EXCLUDED.""TotalNonCurrentAsset"",
    ""FixedAssetOriginal"" = EXCLUDED.""FixedAssetOriginal"",
    ""FixedAssetNet"" = EXCLUDED.""FixedAssetNet"",
    ""IntangibleAsset"" = EXCLUDED.""IntangibleAsset"",
    ""Goodwill"" = EXCLUDED.""Goodwill"",
    ""LongTermEquityInvest"" = EXCLUDED.""LongTermEquityInvest"",
    ""TotalAsset"" = EXCLUDED.""TotalAsset"",
    ""TotalCurrentLiability"" = EXCLUDED.""TotalCurrentLiability"",
    ""ShortTermLoan"" = EXCLUDED.""ShortTermLoan"",
    ""BillPayable"" = EXCLUDED.""BillPayable"",
    ""AccountPayable"" = EXCLUDED.""AccountPayable"",
    ""AdvanceReceived"" = EXCLUDED.""AdvanceReceived"",
    ""SalaryPayable"" = EXCLUDED.""SalaryPayable"",
    ""TaxPayable"" = EXCLUDED.""TaxPayable"",
    ""TotalNonCurrentLiability"" = EXCLUDED.""TotalNonCurrentLiability"",
    ""LongTermLoan"" = EXCLUDED.""LongTermLoan"",
    ""BondPayable"" = EXCLUDED.""BondPayable"",
    ""TotalLiability"" = EXCLUDED.""TotalLiability"",
    ""TotalEquity"" = EXCLUDED.""TotalEquity"",
    ""PaidInCapital"" = EXCLUDED.""PaidInCapital"",
    ""CapitalReserve"" = EXCLUDED.""CapitalReserve"",
    ""SurplusReserve"" = EXCLUDED.""SurplusReserve"",
    ""UndistributedProfit"" = EXCLUDED.""UndistributedProfit"",
    ""ParentEquity"" = EXCLUDED.""ParentEquity"",
    ""MinorityEquity"" = EXCLUDED.""MinorityEquity"",
    ""RawJson"" = EXCLUDED.""RawJson"",
    ""CreateTime"" = EXCLUDED.""CreateTime"";
";
                await _db.Database.ExecuteSqlRawAsync(sql,
                    new NpgsqlParameter("@StockCode", item.StockCode),
                    new NpgsqlParameter("@ReportDate", item.ReportDate),
                    new NpgsqlParameter("@PublishDate", item.PublishDate ?? (object)DBNull.Value),
                    new NpgsqlParameter("@ReportType", item.ReportType),
                    new NpgsqlParameter("@TotalCurrentAsset", item.TotalCurrentAsset ?? (object)DBNull.Value),
                    new NpgsqlParameter("@MonetaryFund", item.MonetaryFund ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TradingFinancialAsset", item.TradingFinancialAsset ?? (object)DBNull.Value),
                    new NpgsqlParameter("@BillReceivable", item.BillReceivable ?? (object)DBNull.Value),
                    new NpgsqlParameter("@AccountReceivable", item.AccountReceivable ?? (object)DBNull.Value),
                    new NpgsqlParameter("@Prepayment", item.Prepayment ?? (object)DBNull.Value),
                    new NpgsqlParameter("@Inventory", item.Inventory ?? (object)DBNull.Value),
                    new NpgsqlParameter("@OtherCurrentAsset", item.OtherCurrentAsset ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TotalNonCurrentAsset", item.TotalNonCurrentAsset ?? (object)DBNull.Value),
                    new NpgsqlParameter("@FixedAssetOriginal", item.FixedAssetOriginal ?? (object)DBNull.Value),
                    new NpgsqlParameter("@FixedAssetNet", item.FixedAssetNet ?? (object)DBNull.Value),
                    new NpgsqlParameter("@IntangibleAsset", item.IntangibleAsset ?? (object)DBNull.Value),
                    new NpgsqlParameter("@Goodwill", item.Goodwill ?? (object)DBNull.Value),
                    new NpgsqlParameter("@LongTermEquityInvest", item.LongTermEquityInvest ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TotalAsset", item.TotalAsset ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TotalCurrentLiability", item.TotalCurrentLiability ?? (object)DBNull.Value),
                    new NpgsqlParameter("@ShortTermLoan", item.ShortTermLoan ?? (object)DBNull.Value),
                    new NpgsqlParameter("@BillPayable", item.BillPayable ?? (object)DBNull.Value),
                    new NpgsqlParameter("@AccountPayable", item.AccountPayable ?? (object)DBNull.Value),
                    new NpgsqlParameter("@AdvanceReceived", item.AdvanceReceived ?? (object)DBNull.Value),
                    new NpgsqlParameter("@SalaryPayable", item.SalaryPayable ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TaxPayable", item.TaxPayable ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TotalNonCurrentLiability", item.TotalNonCurrentLiability ?? (object)DBNull.Value),
                    new NpgsqlParameter("@LongTermLoan", item.LongTermLoan ?? (object)DBNull.Value),
                    new NpgsqlParameter("@BondPayable", item.BondPayable ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TotalLiability", item.TotalLiability ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TotalEquity", item.TotalEquity ?? (object)DBNull.Value),
                    new NpgsqlParameter("@PaidInCapital", item.PaidInCapital ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CapitalReserve", item.CapitalReserve ?? (object)DBNull.Value),
                    new NpgsqlParameter("@SurplusReserve", item.SurplusReserve ?? (object)DBNull.Value),
                    new NpgsqlParameter("@UndistributedProfit", item.UndistributedProfit ?? (object)DBNull.Value),
                    new NpgsqlParameter("@ParentEquity", item.ParentEquity ?? (object)DBNull.Value),
                    new NpgsqlParameter("@MinorityEquity", item.MinorityEquity ?? (object)DBNull.Value),
                    new NpgsqlParameter("@RawJson", JsonSerializer.Serialize(item.RawJson, _jsonOpts))
                    {
                        NpgsqlDbType = NpgsqlDbType.Jsonb
                    },
                    new NpgsqlParameter("@CreateTime", item.CreateTime)
                );
            }
        }

        private async Task UpsertCashFlow(List<CashFlowItemDto> list)
        {
            foreach (var item in list)
            {
                const string sql = @"
INSERT INTO ""stock_cash_flow"" (
    ""StockCode"", ""ReportDate"", ""PublishDate"", ""ReportType"",
    ""OperateCashIn"", ""OperateCashOut"", ""NetOperateCashFlow"", ""CashFromSales"",
    ""TaxRefundReceived"", ""OtherOperateCashIn"", ""CashPayForGoods"", ""CashPayToStaff"",
    ""TaxPaid"", ""OtherOperateCashOut"", ""InvestCashIn"", ""InvestCashOut"",
    ""NetInvestCashFlow"", ""CashFromInvestRecall"", ""CashFromInvestIncome"", ""CashFromDisposeLongAsset"",
    ""Capex"", ""CashPayForInvest"", ""FinanceCashIn"", ""FinanceCashOut"",
    ""NetFinanceCashFlow"", ""CashFromEquity"", ""CashFromBorrow"", ""CashRepayDebt"",
    ""CashPayDividendInterest"", ""ForexEffectOnCash"", ""NetIncreaseCash"", ""BeginCashBalance"",
    ""EndCashBalance"", ""RawJson"", ""CreateTime""
)
VALUES (
    @StockCode, @ReportDate, @PublishDate, @ReportType,
    @OperateCashIn, @OperateCashOut, @NetOperateCashFlow, @CashFromSales,
    @TaxRefundReceived, @OtherOperateCashIn, @CashPayForGoods, @CashPayToStaff,
    @TaxPaid, @OtherOperateCashOut, @InvestCashIn, @InvestCashOut,
    @NetInvestCashFlow, @CashFromInvestRecall, @CashFromInvestIncome, @CashFromDisposeLongAsset,
    @Capex, @CashPayForInvest, @FinanceCashIn, @FinanceCashOut,
    @NetFinanceCashFlow, @CashFromEquity, @CashFromBorrow, @CashRepayDebt,
    @CashPayDividendInterest, @ForexEffectOnCash, @NetIncreaseCash, @BeginCashBalance,
    @EndCashBalance, @RawJson, @CreateTime
)
ON CONFLICT (""StockCode"", ""ReportDate"") DO UPDATE
SET
    ""PublishDate"" = EXCLUDED.""PublishDate"",
    ""ReportType"" = EXCLUDED.""ReportType"",
    ""OperateCashIn"" = EXCLUDED.""OperateCashIn"",
    ""OperateCashOut"" = EXCLUDED.""OperateCashOut"",
    ""NetOperateCashFlow"" = EXCLUDED.""NetOperateCashFlow"",
    ""CashFromSales"" = EXCLUDED.""CashFromSales"",
    ""TaxRefundReceived"" = EXCLUDED.""TaxRefundReceived"",
    ""OtherOperateCashIn"" = EXCLUDED.""OtherOperateCashIn"",
    ""CashPayForGoods"" = EXCLUDED.""CashPayForGoods"",
    ""CashPayToStaff"" = EXCLUDED.""CashPayToStaff"",
    ""TaxPaid"" = EXCLUDED.""TaxPaid"",
    ""OtherOperateCashOut"" = EXCLUDED.""OtherOperateCashOut"",
    ""InvestCashIn"" = EXCLUDED.""InvestCashIn"",
    ""InvestCashOut"" = EXCLUDED.""InvestCashOut"",
    ""NetInvestCashFlow"" = EXCLUDED.""NetInvestCashFlow"",
    ""CashFromInvestRecall"" = EXCLUDED.""CashFromInvestRecall"",
    ""CashFromInvestIncome"" = EXCLUDED.""CashFromInvestIncome"",
    ""CashFromDisposeLongAsset"" = EXCLUDED.""CashFromDisposeLongAsset"",
    ""Capex"" = EXCLUDED.""Capex"",
    ""CashPayForInvest"" = EXCLUDED.""CashPayForInvest"",
    ""FinanceCashIn"" = EXCLUDED.""FinanceCashIn"",
    ""FinanceCashOut"" = EXCLUDED.""FinanceCashOut"",
    ""NetFinanceCashFlow"" = EXCLUDED.""NetFinanceCashFlow"",
    ""CashFromEquity"" = EXCLUDED.""CashFromEquity"",
    ""CashFromBorrow"" = EXCLUDED.""CashFromBorrow"",
    ""CashRepayDebt"" = EXCLUDED.""CashRepayDebt"",
    ""CashPayDividendInterest"" = EXCLUDED.""CashPayDividendInterest"",
    ""ForexEffectOnCash"" = EXCLUDED.""ForexEffectOnCash"",
    ""NetIncreaseCash"" = EXCLUDED.""NetIncreaseCash"",
    ""BeginCashBalance"" = EXCLUDED.""BeginCashBalance"",
    ""EndCashBalance"" = EXCLUDED.""EndCashBalance"",
    ""RawJson"" = EXCLUDED.""RawJson"",
    ""CreateTime"" = EXCLUDED.""CreateTime"";
";
                await _db.Database.ExecuteSqlRawAsync(sql,
                    new NpgsqlParameter("@StockCode", item.StockCode),
                    new NpgsqlParameter("@ReportDate", item.ReportDate),
                    new NpgsqlParameter("@PublishDate", item.PublishDate ?? (object)DBNull.Value),
                    new NpgsqlParameter("@ReportType", item.ReportType),
                    new NpgsqlParameter("@OperateCashIn", item.OperateCashIn ?? (object)DBNull.Value),
                    new NpgsqlParameter("@OperateCashOut", item.OperateCashOut ?? (object)DBNull.Value),
                    new NpgsqlParameter("@NetOperateCashFlow", item.NetOperateCashFlow ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashFromSales", item.CashFromSales ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TaxRefundReceived", item.TaxRefundReceived ?? (object)DBNull.Value),
                    new NpgsqlParameter("@OtherOperateCashIn", item.OtherOperateCashIn ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashPayForGoods", item.CashPayForGoods ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashPayToStaff", item.CashPayToStaff ?? (object)DBNull.Value),
                    new NpgsqlParameter("@TaxPaid", item.TaxPaid ?? (object)DBNull.Value),
                    new NpgsqlParameter("@OtherOperateCashOut", item.OtherOperateCashOut ?? (object)DBNull.Value),
                    new NpgsqlParameter("@InvestCashIn", item.InvestCashIn ?? (object)DBNull.Value),
                    new NpgsqlParameter("@InvestCashOut", item.InvestCashOut ?? (object)DBNull.Value),
                    new NpgsqlParameter("@NetInvestCashFlow", item.NetInvestCashFlow ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashFromInvestRecall", item.CashFromInvestRecall ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashFromInvestIncome", item.CashFromInvestIncome ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashFromDisposeLongAsset", item.CashFromDisposeLongAsset ?? (object)DBNull.Value),
                    new NpgsqlParameter("@Capex", item.Capex ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashPayForInvest", item.CashPayForInvest ?? (object)DBNull.Value),
                    new NpgsqlParameter("@FinanceCashIn", item.FinanceCashIn ?? (object)DBNull.Value),
                    new NpgsqlParameter("@FinanceCashOut", item.FinanceCashOut ?? (object)DBNull.Value),
                    new NpgsqlParameter("@NetFinanceCashFlow", item.NetFinanceCashFlow ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashFromEquity", item.CashFromEquity ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashFromBorrow", item.CashFromBorrow ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashRepayDebt", item.CashRepayDebt ?? (object)DBNull.Value),
                    new NpgsqlParameter("@CashPayDividendInterest", item.CashPayDividendInterest ?? (object)DBNull.Value),
                    new NpgsqlParameter("@ForexEffectOnCash", item.ForexEffectOnCash ?? (object)DBNull.Value),
                    new NpgsqlParameter("@NetIncreaseCash", item.NetIncreaseCash ?? (object)DBNull.Value),
                    new NpgsqlParameter("@BeginCashBalance", item.BeginCashBalance ?? (object)DBNull.Value),
                    new NpgsqlParameter("@EndCashBalance", item.EndCashBalance ?? (object)DBNull.Value),
                    new NpgsqlParameter("@RawJson", JsonSerializer.Serialize(item.RawJson, _jsonOpts))
                    {
                        NpgsqlDbType = NpgsqlDbType.Jsonb
                    },
                    new NpgsqlParameter("@CreateTime", item.CreateTime)
                );
            }
        }
        #endregion

        /// <summary>
        /// 从麦蕊API拉取股票基础列表，批量Upsert到StockBasics
        /// </summary>
        /// <param name="stockList">麦蕊返回的股票基础列表</param>
        public async Task UpsertStockBasics(List<StockBasic> stockList)
        {
            Console.WriteLine($"UpsertStockBasics: {stockList.Count} items");
            foreach (var item in stockList)
            {
                await _baseService.AddObjectAsync(item);
                //                const string sql = @"
                //INSERT INTO ""StockBasics"" (
                //    ""StockCode"", ""StockName"", ""Exchange"", ""Industry"",
                //    ""ListDate"", ""IsDelist"", ""CreateTime"", ""UpdateTime""
                //)
                //VALUES (
                //    @StockCode, @StockName, @Exchange, @Industry,
                //    @ListDate, @IsDelist, @CreateTime, @UpdateTime
                //)
                //ON CONFLICT (""StockCode"") DO UPDATE
                //SET
                //    ""StockName"" = EXCLUDED.""StockName"",
                //    ""Exchange"" = EXCLUDED.""Exchange"",
                //    ""Industry"" = EXCLUDED.""Industry"",
                //    ""ListDate"" = EXCLUDED.""ListDate"",
                //    ""IsDelist"" = EXCLUDED.""IsDelist"",
                //    ""UpdateTime"" = EXCLUDED.""UpdateTime"";
                //";
                //                int affected = await _db.Database.ExecuteSqlRawAsync(sql,
                //                    new NpgsqlParameter("@StockCode", item.StockCode),
                //                    new NpgsqlParameter("@StockName", item.StockName),
                //                    new NpgsqlParameter("@Exchange", item.Exchange),
                //                    new NpgsqlParameter("@Industry", item.Industry ?? (object)DBNull.Value),
                //                    new NpgsqlParameter("@ListDate", item.ListDate ?? (object)DBNull.Value),
                //                    new NpgsqlParameter("@IsDelist", item.IsDelist),
                //                    new NpgsqlParameter("@CreateTime", item.CreateTime),
                //                    new NpgsqlParameter("@UpdateTime", item.UpdateTime)
                //                );
                //                Console.WriteLine($"StockCode:{item.StockCode}, affected rows:{affected}");
            }
        }

    }
}
