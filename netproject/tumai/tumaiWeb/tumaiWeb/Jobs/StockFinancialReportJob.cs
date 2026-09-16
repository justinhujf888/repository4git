using Quartz;
using tumaiWeb.StockService.Mairui;

namespace tumaiWeb.Jobs
{
    public class StockFinancialReportJob : IJob
    {
        private readonly IServiceProvider _serviceProvider;
        private readonly ILogger<StockFinancialReportJob> _logger;

        public StockFinancialReportJob(IServiceProvider serviceProvider, ILogger<StockFinancialReportJob> logger)
        {
            _serviceProvider = serviceProvider;
            _logger = logger;
        }

        public async ValueTask Execute(IJobExecutionContext context, CancellationToken cancellationToken = default)
        {
            _logger.LogInformation("StockFinancialReportJob 任务开始执行");
            try
            {
                TimeZoneInfo cnZone = TimeZoneInfo.FindSystemTimeZoneById("Asia/Shanghai");
                DateTime cnNow = TimeZoneInfo.ConvertTime(DateTime.UtcNow, cnZone);
                int m = cnNow.Month;
                if (new[] { 1, 4, 7, 10 }.Contains(m) == false)
                {
                    _logger.LogInformation("StockFinancialReportJob 当前月份不是报表月份");
                    return;
                }

                using var scope = _serviceProvider.CreateScope();
                var _mairuiDataService = scope.ServiceProvider.GetRequiredService<MairuiDataService>();
                var _mairuiFinancialService = scope.ServiceProvider.GetRequiredService<MairuiFinancialService>();
                var stockList = await _mairuiFinancialService.QuerySelfStockListAsync();
                //var tsCode = context.MergedJobDataMap.GetString("ts_code");
                var tsCodes = stockList.Select(x => new {x.StockCode,x.Market}).ToArray();
                foreach (var ts in tsCodes)
                {
                    var profitJson = await _mairuiDataService.GetProfitStatementRawAsync($"{ts.StockCode}.{ts.Market}");
                    var balanceJson = await _mairuiDataService.GetBalanceSheetRawAsync($"{ts.StockCode}.{ts.Market}");
                    var cashJson = await _mairuiDataService.GetCashflowRawAsync($"{ts.StockCode}.{ts.Market}");
                    await Utils.ToFile.SaveLargeStrToFileAsync(profitJson, $"{AppContext.BaseDirectory}/json/fr/profit_{ts.StockCode}.{ts.Market}.json");
                    await Utils.ToFile.SaveLargeStrToFileAsync(balanceJson, $"{AppContext.BaseDirectory}/json/fr/balance_{ts.StockCode}.{ts.Market}.json");
                    await Utils.ToFile.SaveLargeStrToFileAsync(cashJson, $"{AppContext.BaseDirectory}/json/fr/cash_{ts.StockCode}.{ts.Market}.json");
                    await _mairuiFinancialService.SaveFinancialData($"{ts.StockCode}.{ts.Market}", profitJson, balanceJson, cashJson);
                }
                
                _logger.LogInformation("StockFinancialReportJob 执行完毕");

            }
            catch (OperationCanceledException)
            {
                _logger.LogInformation("StockFinancialReportJob 收到取消信号，任务终止");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "StockFinancialReportJob 任务异常");
                throw;
            }
        }
    }
}
