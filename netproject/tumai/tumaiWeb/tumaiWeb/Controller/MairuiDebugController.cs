using Microsoft.AspNetCore.Mvc;
using tumaiWeb.StockService.Mairui;


namespace tumaiWeb.Controller
{
    [ApiController]
    [Route("api/debug/mairui")]
    public class MairuiDebugController : ControllerBase
    {
        private readonly MairuiDataService _mairuiDataService;
        public MairuiDebugController(MairuiDataService mairuiDataService)
        {
            _mairuiDataService = mairuiDataService;
        }

        /// <summary>
        /// 调试接口：获取沪深三大财报，入参直接传 ts_code（带.SZ/.SH）
        /// GET api/debug/mairui/finance/000001.SZ?st=20240101&et=20260630
        /// </summary>
        [HttpGet("finance/{tsCode}")]
        public async Task<IActionResult> GetFinanceRawJson(string tsCode)
        {
            try
            {
                Console.WriteLine($"==== 请求麦蕊财报 tsCode:{tsCode} ====");

                var profitJson = await _mairuiDataService.GetProfitStatementRawAsync(tsCode);
                var balanceJson = await _mairuiDataService.GetBalanceSheetRawAsync(tsCode);
                var cashJson = await _mairuiDataService.GetCashflowRawAsync(tsCode);

                Console.WriteLine("【利润表JSON】\n" + profitJson);
                Console.WriteLine("\n【资产负债表JSON】\n" + balanceJson);
                Console.WriteLine("\n【现金流量表JSON】\n" + cashJson);


                var result = new
                {
                    TsCode = tsCode,
                    ProfitStatement = profitJson,
                    BalanceSheet = balanceJson,
                    CashFlow = cashJson
                };
                return Ok(result);
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
    }
}
