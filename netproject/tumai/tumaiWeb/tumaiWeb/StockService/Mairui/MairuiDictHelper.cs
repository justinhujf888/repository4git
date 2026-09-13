using System.Globalization;

namespace tumaiWeb.StockService.Mairui
{
    public static class MairuiDictHelper
    {
        /// <summary>
        /// 从字典安全读取decimal?，麦蕊 "-" 转为null
        /// </summary>
        public static decimal? GetDecimal(Dictionary<string, object> dict, string key)
        {
            if (!dict.TryGetValue(key, out var val))
                return null;

            var str = val.ToString()?.Trim();
            if (str == "-" || string.IsNullOrWhiteSpace(str))
                return null;

            if (decimal.TryParse(str, NumberStyles.Any, CultureInfo.InvariantCulture, out var num))
                return num;
            return null;
        }

        /// <summary>
        /// 从字典安全读取long?，麦蕊 "-" 转为null
        /// </summary>
        public static long? GetLong(Dictionary<string, object> dict, string key)
        {
            if (!dict.TryGetValue(key, out var val) || val == null)
                return null;

            var s = val.ToString()?.Trim();
            if (s == "-" || string.IsNullOrWhiteSpace(s))
                return null;

            if (long.TryParse(s, out var l))
                return l;
            return null;
        }

        /// <summary>
        /// 从字典安全读取字符串
        /// </summary>
        public static string? GetString(Dictionary<string, object> dict, string key)
        {
            if (!dict.TryGetValue(key, out var val))
                return null;
            return val.ToString()?.Trim();
        }

        /// <summary>
        /// 读取麦蕊日期字符串，转为DateOnly（用于jzrq 财报截止日 yyyy-MM-dd）
        /// </summary>
        public static DateOnly? GetDateOnly(Dictionary<string, object> dict, string key)
        {
            var str = GetString(dict, key);
            if (string.IsNullOrWhiteSpace(str) || str == "-")
                return null;

            if (DateOnly.TryParseExact(str, "yyyy-MM-dd", CultureInfo.InvariantCulture, DateTimeStyles.None, out var date))
            {
                return date;
            }
            return null;
        }

        /// <summary>
        /// 读取麦蕊时间字符串（北京时间）转UTC DateTime，存入PG timestamptz
        /// 兼容两种格式：yyyy-MM-dd  / yyyy-MM-dd HH:mm:ss
        /// 自动跨平台识别上海时区，Mac/Linux/Windows都可用
        /// </summary>
        public static DateTime? GetDateTime(Dictionary<string, object> dict, string key)
        {
            var str = GetString(dict, key);
            if (string.IsNullOrWhiteSpace(str) || str == "-")
                return null;

            // 跨平台获取中国时区
            TimeZoneInfo cstTz;
            try
            {
                cstTz = TimeZoneInfo.FindSystemTimeZoneById("Asia/Shanghai");
            }
            catch
            {
                cstTz = TimeZoneInfo.FindSystemTimeZoneById("China Standard Time");
            }

            // 优先尝试带时分秒
            if (DateTime.TryParseExact(str, "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture, DateTimeStyles.None, out var dtFull))
            {
                var utc = TimeZoneInfo.ConvertTimeToUtc(dtFull, cstTz);
                return DateTime.SpecifyKind(utc, DateTimeKind.Utc);
            }
            // 只有日期的场景，默认0点
            if (DateTime.TryParseExact(str, "yyyy-MM-dd", CultureInfo.InvariantCulture, DateTimeStyles.None, out var dtDateOnly))
            {
                var utc = TimeZoneInfo.ConvertTimeToUtc(dtDateOnly, cstTz);
                return DateTime.SpecifyKind(utc, DateTimeKind.Utc);
            }
            return null;
        }

        /// <summary>
        /// 根据财报截止日期字符串，返回标准化报告编码：Q1 / H1 / Q3 / Annual
        /// jzrq: "yyyy-MM-dd"
        /// </summary>
        public static string GetReportType(string jzrq)
        {
            if (!DateOnly.TryParse(jzrq, out var dt))
                return "Other";

            return dt.Month switch
            {
                3 => "Q1",
                6 => "H1",
                9 => "Q3",
                12 => "Annual",
                _ => "Other"
            };
        }
    }
}
