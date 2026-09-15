using System.Globalization;
using System.Text.Json;

namespace tumaiWeb.StockService.Mairui
{
    public static class MairuiDictHelper
    {
        /// <summary>
        /// 从 JsonElement 安全读取 decimal?，麦蕊 "-" 转为 null
        /// </summary>
        public static decimal? GetDecimal(JsonElement element, string key)
        {
            if (!element.TryGetProperty(key, out var val))
                return null;

            if (val.ValueKind == JsonValueKind.Null ||
                val.ValueKind == JsonValueKind.Undefined)
                return null;

            // JSON 本身就是 number
            if (val.ValueKind == JsonValueKind.Number &&
                val.TryGetDecimal(out var number))
            {
                return number;
            }

            // 麦蕊接口有些数字可能是字符串
            var str = GetValueString(val);

            if (string.IsNullOrWhiteSpace(str) || str == "-")
                return null;

            if (decimal.TryParse(
                    str,
                    NumberStyles.Any,
                    CultureInfo.InvariantCulture,
                    out var num))
            {
                return num;
            }

            return null;
        }


        /// <summary>
        /// 从 JsonElement 安全读取 long?，麦蕊 "-" 转为 null
        /// </summary>
        public static long? GetLong(JsonElement element, string key)
        {
            if (!element.TryGetProperty(key, out var val))
                return null;

            if (val.ValueKind == JsonValueKind.Null ||
                val.ValueKind == JsonValueKind.Undefined)
                return null;

            // JSON 本身就是整数
            if (val.ValueKind == JsonValueKind.Number &&
                val.TryGetInt64(out var number))
            {
                return number;
            }

            var str = GetValueString(val);

            if (string.IsNullOrWhiteSpace(str) || str == "-")
                return null;

            if (long.TryParse(
                    str,
                    NumberStyles.Any,
                    CultureInfo.InvariantCulture,
                    out var result))
            {
                return result;
            }

            return null;
        }


        /// <summary>
        /// 从 JsonElement 安全读取字符串
        /// </summary>
        public static string? GetString(JsonElement element, string key)
        {
            if (!element.TryGetProperty(key, out var val))
                return null;

            if (val.ValueKind == JsonValueKind.Null ||
                val.ValueKind == JsonValueKind.Undefined)
                return null;

            return GetValueString(val)?.Trim();
        }


        /// <summary>
        /// 将 JsonElement 转换成字符串
        /// </summary>
        private static string? GetValueString(JsonElement value)
        {
            return value.ValueKind switch
            {
                JsonValueKind.String => value.GetString(),

                JsonValueKind.Number => value.ToString(),

                JsonValueKind.True => "true",

                JsonValueKind.False => "false",

                JsonValueKind.Null => null,

                JsonValueKind.Undefined => null,

                // 如果未来 JSON 中出现对象/数组
                _ => value.ToString()
            };
        }


        /// <summary>
        /// 读取麦蕊日期字符串，转为 DateOnly
        /// 用于 jzrq 财报截止日 yyyy-MM-dd
        /// </summary>
        public static DateOnly? GetDateOnly(
            JsonElement element,
            string key)
        {
            var str = GetString(element, key);

            if (string.IsNullOrWhiteSpace(str) || str == "-")
                return null;

            if (DateOnly.TryParseExact(
                    str,
                    "yyyy-MM-dd",
                    CultureInfo.InvariantCulture,
                    DateTimeStyles.None,
                    out var date))
            {
                return date;
            }

            return null;
        }


        /// <summary>
        /// 读取麦蕊时间字符串（北京时间）转 UTC DateTime
        /// 存入 PostgreSQL timestamptz
        ///
        /// 兼容：
        /// yyyy-MM-dd
        /// yyyy-MM-dd HH:mm:ss
        ///
        /// 自动跨平台识别上海时区：
        /// Mac/Linux: Asia/Shanghai
        /// Windows: China Standard Time
        /// </summary>
        public static DateTime? GetDateTime(
            JsonElement element,
            string key)
        {
            var str = GetString(element, key);

            if (string.IsNullOrWhiteSpace(str) || str == "-")
                return null;

            // 跨平台获取中国时区
            TimeZoneInfo cstTz;

            try
            {
                cstTz = TimeZoneInfo.FindSystemTimeZoneById(
                    "Asia/Shanghai");
            }
            catch
            {
                cstTz = TimeZoneInfo.FindSystemTimeZoneById(
                    "China Standard Time");
            }

            // yyyy-MM-dd HH:mm:ss
            if (DateTime.TryParseExact(
                    str,
                    "yyyy-MM-dd HH:mm:ss",
                    CultureInfo.InvariantCulture,
                    DateTimeStyles.None,
                    out var dtFull))
            {
                var utc = TimeZoneInfo.ConvertTimeToUtc(
                    dtFull,
                    cstTz);

                return DateTime.SpecifyKind(
                    utc,
                    DateTimeKind.Utc);
            }

            // yyyy-MM-dd
            if (DateTime.TryParseExact(
                    str,
                    "yyyy-MM-dd",
                    CultureInfo.InvariantCulture,
                    DateTimeStyles.None,
                    out var dtDateOnly))
            {
                var utc = TimeZoneInfo.ConvertTimeToUtc(
                    dtDateOnly,
                    cstTz);

                return DateTime.SpecifyKind(
                    utc,
                    DateTimeKind.Utc);
            }

            return null;
        }


        /// <summary>
        /// 根据财报截止日期字符串，返回标准化报告编码：
        /// Q1 / H1 / Q3 / Annual
        ///
        /// jzrq: yyyy-MM-dd
        /// </summary>
        public static string GetReportType(string? jzrq)
        {
            if (string.IsNullOrWhiteSpace(jzrq))
                return "Other";

            if (!DateOnly.TryParse(
                    jzrq,
                    CultureInfo.InvariantCulture,
                    DateTimeStyles.None,
                    out var dt))
            {
                return "Other";
            }

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
