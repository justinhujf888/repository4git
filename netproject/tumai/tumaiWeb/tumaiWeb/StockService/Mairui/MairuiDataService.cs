using System.ComponentModel;

namespace tumaiWeb.StockService.Mairui
{
    public class MairuiDataService
    {
        private readonly HttpClient _httpClient;
        private readonly string _licence;

        public MairuiDataService(HttpClient httpClient, IConfiguration cfg)
        {
            _httpClient = httpClient;
            _licence = cfg["Mairui:Licence"]!;
        }

        /// <summary>
        /// 麦蕊股票列表接口，同步股票基础清单（仅代码、名称、交易所，消耗1次licence额度）
        /// 接口文档：GET https://api.mairuiapi.com/hslt/list/{licence}
        /// </summary>
        /// <returns>成功入库数量</returns>
        public async Task<string> GetStockBasicRawAsync()
        {
            return await GetRawAsync($"hslt/list/{_licence}", null, null);
        }

        /// <summary>
        /// 利润表 沪深 hsstock【income，不支持st/et参数】
        /// </summary>
        public async Task<string> GetProfitStatementRawAsync(string tsCode)
        {
            // 财务接口，不传 st/et
            return await GetRawAsync($"hsstock/financial/income/{tsCode}/{_licence}", null, null);
        }

        /// <summary>
        /// 资产负债表 沪深 hsstock【不支持st/et参数】
        /// </summary>
        public async Task<string> GetBalanceSheetRawAsync(string tsCode)
        {
            return await GetRawAsync($"hsstock/financial/balance/{tsCode}/{_licence}", null, null);
        }

        /// <summary>
        /// 北交所现金流量表 bj【统一传入带.BJ后缀代码，例：920547.BJ】
        /// </summary>
        public async Task<string> GetBjCashflowRawAsync(string tsCodeBj, string? st = null, string? et = null)
        {
            return await GetRawAsync($"bj/financial/cashflow/{tsCodeBj}/{_licence}", st, et);
        }

        /// <summary>
        /// 现金流量表 沪深 hsstock【不支持st/et参数】
        /// </summary>
        public async Task<string> GetCashflowRawAsync(string tsCode)
        {
            return await GetRawAsync($"hsstock/financial/cashflow/{tsCode}/{_licence}", null, null);
        }

        /// <summary>
        /// GetStockQuoteSnapshotRawAsync 行情快照
        /// </summary>
        public async Task<string> GetStockQuoteSnapshotRawAsync(string tsCode)
        {
            return await GetRawAsync($"hsstock/real/time/{tsCode}/{_licence}", null, null);
        }

        /// <summary>
        /// Pro版沪深K线【必须ts_code，000001.SZ / 600036.SH】
        /// </summary>
        /// <param name="tsCode">带后缀代码</param>
        /// <param name="type">1=日线</param>
        /// <param name="fq">n=不复权 q前复权 h后复权</param>
        /// <param name="limit">lt返回条数</param>
        public async Task<string> GetHsKlineRawAsync(string tsCode, int type = 1, string fq = "n", int limit = 200)
        {
            var path = $"hsstock/pro/latest/{tsCode}/{type}/{fq}/{_licence}";
            var urlBuilder = new UriBuilder("https://api.mairuiapi.com/" + path);
            var query = System.Web.HttpUtility.ParseQueryString(urlBuilder.Query);
            query["lt"] = limit.ToString();
            urlBuilder.Query = query.ToString();

            var resp = await _httpClient.GetAsync(urlBuilder.ToString());
            resp.EnsureSuccessStatusCode();
            return await resp.Content.ReadAsStringAsync();
        }

        // 底层拼接 st et 查询参数
        private async Task<string> GetRawAsync(string path, string? st, string? et)
        {
            var urlBuilder = new UriBuilder("https://api.mairuiapi.com/" + path);
            var query = System.Web.HttpUtility.ParseQueryString(urlBuilder.Query);
            if (!string.IsNullOrWhiteSpace(st)) query["st"] = st;
            if (!string.IsNullOrWhiteSpace(et)) query["et"] = et;
            urlBuilder.Query = query.ToString();
            Console.WriteLine($"麦蕊完整请求URL：{urlBuilder.ToString()}"); // 打印完整URL
            var resp = await _httpClient.GetAsync(urlBuilder.ToString());
            resp.EnsureSuccessStatusCode();
            return await resp.Content.ReadAsStringAsync();
        }
    }
}
