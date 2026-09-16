using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Http.Timeouts;
using Microsoft.AspNetCore.Mvc;
using System.Diagnostics;
using tumaiWeb.Data.Entities;
using tumaiWeb.Data.Repository;
using tumaiWeb.StockService;
using tumaiWeb.StockService.Mairui;


namespace tumaiWeb.Controller
{
    [ApiController]
    [Route("api/debug/mairui")]
    public class MairuiDebugController : ControllerBase
    {
        private readonly MairuiDataService _mairuiDataService;
        private readonly MairuiFinancialService _mairuiFinancialService;
        private readonly IBaseService _baseService;
        public MairuiDebugController(MairuiDataService mairuiDataService, MairuiFinancialService mairuiFinancialService,IBaseService baseService)
        {
            _mairuiDataService = mairuiDataService;
            _mairuiFinancialService = mairuiFinancialService;
            _baseService = baseService;
        }

        [HttpGet("mystocklist")]
        public async Task<IActionResult> GetMyStockList()
        {
            try
            {
                var stockList = await _mairuiFinancialService.QuerySelfStockListAsync();
                var tsCodes = stockList.Select(x => new { x.StockCode, x.Market }).ToArray();
                var groups = tsCodes.Chunk(20);
                foreach (var group in groups)
                {
                    // group 是数组，每组最多20个
                    foreach (var item in group)
                    {
                        Console.WriteLine($"{item.StockCode}.{item.Market}");
                    }
                }
                return Ok(groups);
            }
            catch (Exception ex)
            {
                return BadRequest($"获取股票基础信息失败：{ex.Message}");
            }
        }

        /// <summary>
        /// 调试接口：获取沪深三大财报，入参直接传 ts_code（带.SZ/.SH）
        /// GET api/debug/mairui/finance/000001.SZ?st=20240101&et=20260630
        /// </summary>
        [HttpGet("finance/{tsCode}")]
        [RequestTimeout(1200000)]
        public async Task<IActionResult> GetFinanceRawJson(string tsCode)
        {
            try
            {
                //Console.WriteLine($"==== 请求麦蕊财报 tsCode:{tsCode} ====");

                //var profitJson = await _mairuiDataService.GetProfitStatementRawAsync(tsCode);
                //var balanceJson = await _mairuiDataService.GetBalanceSheetRawAsync(tsCode);
                //var cashJson = await _mairuiDataService.GetCashflowRawAsync(tsCode);

                //Console.WriteLine("【利润表JSON】\n" + profitJson);
                //Console.WriteLine("\n【资产负债表JSON】\n" + balanceJson);
                //Console.WriteLine("\n【现金流量表JSON】\n" + cashJson);

                //await Utils.ToFile.SaveLargeStrToFileAsync(profitJson, $"{AppContext.BaseDirectory}/json/profit.json");
                //await Utils.ToFile.SaveLargeStrToFileAsync(balanceJson, $"{AppContext.BaseDirectory}/json/balance.json");
                //await Utils.ToFile.SaveLargeStrToFileAsync(cashJson, $"{AppContext.BaseDirectory}/json/cash.json");

                //var result = new
                //{
                //    TsCode = tsCode,
                //    ProfitStatement = profitJson,
                //    BalanceSheet = balanceJson,
                //    CashFlow = cashJson
                //};
                //return Ok(result);

                //await _mairuiFinancialService.SaveFinancialData(tsCode, await Utils.ToFile.ReadStrFromFileAsync($"{AppContext.BaseDirectory}/json/profit.json"), await Utils.ToFile.ReadStrFromFileAsync($"{AppContext.BaseDirectory}/json/balance.json"), await Utils.ToFile.ReadStrFromFileAsync($"{AppContext.BaseDirectory}/json/cash.json"));
                

                var stockList = await _mairuiFinancialService.QuerySelfStockListAsync();
                //var tsCode = context.MergedJobDataMap.GetString("ts_code");
                var tsCodes = stockList.Select(x => new { x.StockCode, x.Market }).ToArray();
                var reportDates = StockUtil.GetRecentFinancialReportDates(DateTime.Now, 4);
                foreach (var ts in tsCodes)
                {
                    var profitJson = await _mairuiDataService.GetProfitStatementRawAsync($"{ts.StockCode}.{ts.Market}");
                    var balanceJson = await _mairuiDataService.GetBalanceSheetRawAsync($"{ts.StockCode}.{ts.Market}");
                    var cashJson = await _mairuiDataService.GetCashflowRawAsync($"{ts.StockCode}.{ts.Market}");
                    await Utils.ToFile.SaveLargeStrToFileAsync(profitJson, $"{AppContext.BaseDirectory}/json/fr/profit_{ts.StockCode}.{ts.Market}.json");
                    await Utils.ToFile.SaveLargeStrToFileAsync(balanceJson, $"{AppContext.BaseDirectory}/json/fr/balance_{ts.StockCode}.{ts.Market}.json");
                    await Utils.ToFile.SaveLargeStrToFileAsync(cashJson, $"{AppContext.BaseDirectory}/json/fr/cash_{ts.StockCode}.{ts.Market}.json");
                    await _mairuiFinancialService.SaveFinancialData($"{ts.StockCode}.{ts.Market}", profitJson, balanceJson, cashJson);
                    foreach (var rDate in reportDates)
                    {
                       await _mairuiFinancialService.CalcAndUpsertOneAsync($"{ts.StockCode}.{ts.Market}", rDate, CancellationToken.None);
                    }
                }

                return Ok(new { Message = "财报数据已保存到数据库" });
            }
            catch (HttpRequestException ex)
            {
                Console.WriteLine($"HTTP异常：{ex.Message}");
                return BadRequest($"麦蕊接口调用失败：{ex.Message}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"异常：{ex.Message}");
                return StatusCode(500, $"服务器异常：{ex.Message}");
            }
        }

        /// <summary>
        /// 北交所财报调试，入参 tsCodeBj 如 920547.BJ
        /// GET api/debug/mairui/bj-finance/920547.BJ?st=20240101&et=20260630
        /// </summary>
        [HttpGet("bj-finance/{tsCodeBj}")]
        public async Task<IActionResult> GetBjFinanceRawJson(string tsCodeBj, string? st = null, string? et = null)
        {
            try
            {
                var cashJson = await _mairuiDataService.GetBjCashflowRawAsync(tsCodeBj, st, et);
                Console.WriteLine(cashJson);
                return Ok(new { TsCode = tsCodeBj, Cash = cashJson });
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// K线调试，入参 tsCode 000001.SZ
        /// GET api/debug/mairui/kline/000001.SZ?limit=5
        /// </summary>
        [HttpGet("kline/{tsCode}")]
        public async Task<IActionResult> GetKlineRawJson(string tsCode, int type = 1, string fq = "n", int limit = 5)
        {
            try
            {
                var json = await _mairuiDataService.GetHsKlineRawAsync(tsCode, type, fq, limit);
                Console.WriteLine($"【{tsCode} K线JSON】\n{json}");
                return Ok(new
                {
                    TsCode = tsCode,
                    KlineRaw = json
                });
            }
            catch (Exception ex)
            {
                return BadRequest($"K线接口调用失败：{ex.Message}");
            }
        }

        [HttpGet("stockbasics")]
        [RequestTimeout(1200000)]
        public async Task<IActionResult> GetStockBasicsRawJson()
        {
            try
            {
                //var stockBasicsJson = await _mairuiDataService.GetStockBasicRawAsync();
                //await Utils.ToFile.SaveLargeStrToFileAsync(stockBasicsJson, $"{AppContext.BaseDirectory}/json/stockbasics.json");
                // 放到后台执行，不阻塞http请求

                await _mairuiFinancialService.SaveStockBasicsData(await Utils.ToFile.ReadStrFromFileAsync($"{AppContext.BaseDirectory}/json/stockbasics.json"));
                return Ok(new { Message = "股票基础信息已保存到数据库" });
            }
            catch (Exception ex)
            {
                return BadRequest($"股票接口调用失败：{ex.Message}");
            }
        }

        [HttpGet("stockQuoteSnapshot")]
        [RequestTimeout(1200000)]
        public async Task<IActionResult> GetStockQuoteSnapshotRawJson()
        {
            try
            {
                //var stockQuoteSnapshotJson = await _mairuiDataService.GetStockSsjy4ManyStkRawAsync([tsCode]);
                //await Utils.ToFile.SaveLargeStrToFileAsync(stockQuoteSnapshotJson, $"{AppContext.BaseDirectory}/json/stockquotesnapshot.json");
                //return Ok(new { Message = "获取行情快照" });
                var stockList = await _mairuiFinancialService.QuerySelfStockListAsync();
                var tsCodes = stockList.Select(x => new { x.StockCode, x.Market }).ToArray();
                // 每20条一组
                var groups = tsCodes.Chunk(20);
                foreach (var group in groups)
                {
                    // group 是数组，每组最多20个
                    List<string> sList = group.Select(item => $"{item.StockCode}.{item.Market}").ToList();
                    var stockQuoteSnapshotJson = await _mairuiDataService.GetStockSsjy4ManyStkRawAsync(sList);
                    await _mairuiFinancialService.SaveStockSsjy4ManyStkData(sList, stockQuoteSnapshotJson);
                }
                return Ok(new { Message = "股票实时交易信息已保存到数据库" });
            }
            catch (Exception ex)
            {
                return BadRequest($"股票接口调用失败：{ex.Message}");
            }
        }
    }
}