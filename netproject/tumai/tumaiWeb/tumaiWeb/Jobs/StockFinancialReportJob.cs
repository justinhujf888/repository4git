using Quartz;
using tumaiWeb.StockService;
using tumaiWeb.StockService.Mairui;

namespace tumaiWeb.Jobs
{
    public class StockFinancialReportJob : IJob
    {
        private readonly IServiceProvider _serviceProvider;
        private readonly ILogger<StockFinancialReportJob> _logger;

        private const int MaxConcurrent = 2;

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
                // 信号量，放在最外层
                using var semaphore = new SemaphoreSlim(MaxConcurrent, MaxConcurrent);
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

                var taskList = new List<Task>();
                var reportDates = StockUtil.GetRecentFinancialReportDates(DateTime.Now, 4);

                foreach (var ts in tsCodes)
                {
                    // 等待获取信号量，占用一个并发槽位
                    await semaphore.WaitAsync(cancellationToken);
                    var task = process(ts.StockCode, ts.Market, _mairuiDataService, _mairuiFinancialService, semaphore, reportDates,cancellationToken);
                    taskList.Add(task);

                    //var profitJson = await _mairuiDataService.GetProfitStatementRawAsync($"{ts.StockCode}.{ts.Market}");
                    //var balanceJson = await _mairuiDataService.GetBalanceSheetRawAsync($"{ts.StockCode}.{ts.Market}");
                    //var cashJson = await _mairuiDataService.GetCashflowRawAsync($"{ts.StockCode}.{ts.Market}");
                    //await Utils.ToFile.SaveLargeStrToFileAsync(profitJson, $"{AppContext.BaseDirectory}/json/fr/profit_{ts.StockCode}.{ts.Market}.json");
                    //await Utils.ToFile.SaveLargeStrToFileAsync(balanceJson, $"{AppContext.BaseDirectory}/json/fr/balance_{ts.StockCode}.{ts.Market}.json");
                    //await Utils.ToFile.SaveLargeStrToFileAsync(cashJson, $"{AppContext.BaseDirectory}/json/fr/cash_{ts.StockCode}.{ts.Market}.json");
                    //await _mairuiFinancialService.SaveFinancialData($"{ts.StockCode}.{ts.Market}", profitJson, balanceJson, cashJson);
                }
                await Task.WhenAll(taskList);
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

        private async Task process(string stockCode,string market, MairuiDataService _mairuiDataService, MairuiFinancialService _mairuiFinancialService, SemaphoreSlim semaphore, List<DateOnly> reportDates, CancellationToken ct)
        {
            try
            {
                var profitJson = await _mairuiDataService.GetProfitStatementRawAsync($"{stockCode}.{market}");
                var balanceJson = await _mairuiDataService.GetBalanceSheetRawAsync($"{stockCode}.{market}");
                var cashJson = await _mairuiDataService.GetCashflowRawAsync($"{stockCode}.{market}");
                await Utils.ToFile.SaveLargeStrToFileAsync(profitJson, $"{AppContext.BaseDirectory}/json/fr/profit_{stockCode}.{market}.json");
                await Utils.ToFile.SaveLargeStrToFileAsync(balanceJson, $"{AppContext.BaseDirectory}/json/fr/balance_{stockCode}.{market}.json");
                await Utils.ToFile.SaveLargeStrToFileAsync(cashJson, $"{AppContext.BaseDirectory}/json/fr/cash_{stockCode}.{market}.json");
                await _mairuiFinancialService.SaveFinancialData($"{stockCode}.{market}", profitJson, balanceJson, cashJson);
                foreach (var rDate in reportDates)
                {
                    await _mairuiFinancialService.CalcAndUpsertOneAsync($"{stockCode}.{market}", rDate, CancellationToken.None);
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, $"StockFinancialReportJob 计算财务指标异常: {stockCode}.{market}");
            }
            finally
            {
                // 释放信号量，允许其他任务继续执行
                semaphore.Release();
            }
        }
    }
}
