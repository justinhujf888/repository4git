namespace tumaiWeb.StockService.Tushare
{
    public class TushareService
    {
        private readonly HttpClient _httpClient;
        private readonly string _token;
        public TushareService(HttpClient httpClient, IConfiguration cfg)
        {
            _httpClient = httpClient;
            _httpClient.BaseAddress = new Uri("http://api.tushare.pro");
            _token = cfg["Tushare:Token"]!;
        }

        /// <summary>
        /// 获取公告列表（年报、季报）
        /// </summary>
        public async Task<string> GetAnnouncementList(string tsCode)
        {
            var req = new
            {
                api_name = "announcement",
                token = _token,
                @params = new { ts_code = tsCode }
            };
            var json = System.Text.Json.JsonSerializer.Serialize(req);
            var content = new StringContent(json, System.Text.Encoding.UTF8, "application/json");
            var resp = await _httpClient.PostAsync("", content);
            return await resp.Content.ReadAsStringAsync();
        }

        /// <summary>
        /// 根据announcement_id 获取公告原文长文本（最核心）
        /// </summary>
        public async Task<string> GetAnnouncementContent(string annId)
        {
            var req = new
            {
                api_name = "notice_content",
                token = _token,
                @params = new { ann_id = annId }
            };
            var json = System.Text.Json.JsonSerializer.Serialize(req);
            var content = new StringContent(json, System.Text.Encoding.UTF8, "application/json");
            var resp = await _httpClient.PostAsync("", content);
            return await resp.Content.ReadAsStringAsync();
        }
    }
}
