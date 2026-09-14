using Quartz;
using tumaiWeb.StockService.Mairui;

namespace tumaiWeb.Jobs
{
    public class StockBasicQuotePullJob : IJob
    {
        private readonly IServiceProvider _serviceProvider;
        private readonly ILogger<StockBasicQuotePullJob> _logger;

        public StockBasicQuotePullJob(IServiceProvider serviceProvider, ILogger<StockBasicQuotePullJob> logger)
        {
            _serviceProvider = serviceProvider;
            _logger = logger;
        }

        public async ValueTask Execute(IJobExecutionContext context, CancellationToken cancellationToken = default)
        {
            _logger.LogInformation("StockBasicQuotePullJob 任务开始执行");
            try
            {
                using var scope = _serviceProvider.CreateScope();
                var dataService = scope.ServiceProvider.GetRequiredService<MairuiDataService>();
                var financialService = scope.ServiceProvider.GetRequiredService<MairuiFinancialService>();

                var stockBasicsJson = await dataService.GetStockBasicRawAsync();
                await Utils.ToFile.SaveLargeStrToFileAsync(stockBasicsJson, $"{AppContext.BaseDirectory}/json/stockbasics.json");
                await financialService.SaveStockBasicsData(stockBasicsJson);

                _logger.LogInformation("StockBasicQuotePullJob 执行完毕");
            }
            catch (OperationCanceledException)
            {
                _logger.LogInformation("StockBasicQuotePullJob 收到取消信号，任务终止");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "StockBasicQuotePullJob 任务异常");
                throw;
            }
        }
    }
}
