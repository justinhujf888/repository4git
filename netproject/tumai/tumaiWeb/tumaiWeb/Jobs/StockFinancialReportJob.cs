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
                using var scope = _serviceProvider.CreateScope();
                var dataService = scope.ServiceProvider.GetRequiredService<MairuiDataService>();
                var financialService = scope.ServiceProvider.GetRequiredService<MairuiFinancialService>();
                var stockList = await financialService.QuerySelfStockListAsync();
                //var tsCode = context.MergedJobDataMap.GetString("ts_code");
                var tsCodes = stockList.Select(x => new {x.StockCode,x.Market}).ToArray();
                foreach (var ts in tsCodes)
                {
                    var profitJson = await dataService.GetProfitStatementRawAsync($"{ts.StockCode}.{ts.Market}");
                    var balanceJson = await dataService.GetBalanceSheetRawAsync($"{ts.StockCode}.{ts.Market}");
                    var cashJson = await dataService.GetCashflowRawAsync($"{ts.StockCode}.{ts.Market}");
                    await Utils.ToFile.SaveLargeStrToFileAsync(profitJson, $"{AppContext.BaseDirectory}/json/fr/profit_{ts.StockCode}.{ts.Market}.json");
                    await Utils.ToFile.SaveLargeStrToFileAsync(balanceJson, $"{AppContext.BaseDirectory}/json/fr/balance_{ts.StockCode}.{ts.Market}.json");
                    await Utils.ToFile.SaveLargeStrToFileAsync(cashJson, $"{AppContext.BaseDirectory}/json/fr/cash_{ts.StockCode}.{ts.Market}.json");
                    await financialService.SaveFinancialData($"{ts.StockCode}.{ts.Market}", profitJson, balanceJson, cashJson);
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
