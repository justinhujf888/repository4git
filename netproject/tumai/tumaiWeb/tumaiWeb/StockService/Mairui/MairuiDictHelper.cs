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

        public static long? GetLong(Dictionary<string, object?> dict, string key)
        {
            if (!dict.TryGetValue(key, out var val) || val == null) return null;
            var s = val.ToString()?.Trim();
            if (s == "-" || string.IsNullOrWhiteSpace(s)) return null;
            if (long.TryParse(s, out var l)) return l;
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
        /// 根据财报截止日期字符串，获取报告类型
        /// </summary>
        public static string GetReportType(string jzrq)
        {
            if (!DateOnly.TryParse(jzrq, out var dt))
                return "Other";

            return dt.Month switch
            {
                3 => "一季报",
                6 => "中报",
                9 => "三季报",
                12 => "年报",
                _ => "Other"
            };
        }
    }
}
