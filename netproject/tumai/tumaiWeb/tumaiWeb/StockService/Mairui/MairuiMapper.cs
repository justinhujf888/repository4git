namespace tumaiWeb.StockService.Mairui
{
    public static class MairuiMapper
    {
        /// <summary>
        /// 麦蕊数值清洗："-"转为null，数字转decimal?
        /// </summary>
        public static decimal? SafeConvertDecimal(object? val)
        {
            if (val == null) return null;
            string str = val.ToString()!.Trim();
            if (str == "-") return null;
            if (decimal.TryParse(str, out var num))
                return num;
            return null;
        }

        /// <summary>
        /// 解析日期 yyyy-MM-dd
        /// </summary>
        public static DateTime? SafeParseDate(string? dateStr)
        {
            if (string.IsNullOrWhiteSpace(dateStr)) return null;
            if (DateTime.TryParseExact(dateStr, "yyyy-MM-dd", null, System.Globalization.DateTimeStyles.None, out var dt))
                return dt;
            return null;
        }

        /// <summary>
        /// tsCode剥离交易所后缀，获取纯6位stockCode
        /// </summary>
        public static string ExtractPureStockCode(string tsCode)
        {
            return tsCode.Split('.')[0];
        }
    }
}
