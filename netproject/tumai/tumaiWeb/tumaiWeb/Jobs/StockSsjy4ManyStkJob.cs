using Quartz;
using tumaiWeb.StockService.Mairui;

namespace tumaiWeb.Jobs
{
    public class StockSsjy4ManyStkJob : IJob
    {
        private readonly IServiceProvider _serviceProvider;
        private readonly ILogger<StockSsjy4ManyStkJob> _logger;

        public StockSsjy4ManyStkJob(IServiceProvider serviceProvider, ILogger<StockSsjy4ManyStkJob> logger)
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
                var _mairuiDataService = scope.ServiceProvider.GetRequiredService<MairuiDataService>();
                var _mairuiFinancialService = scope.ServiceProvider.GetRequiredService<MairuiFinancialService>();
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

                _logger.LogInformation("StockSsjy4ManyStkJob 执行完毕");
            }
            catch (OperationCanceledException)
            {
                _logger.LogInformation("StockSsjy4ManyStkJob 收到取消信号，任务终止");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "StockSsjy4ManyStkJob 任务异常");
                throw;
            }
        }
    }
}
