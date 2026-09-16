namespace tumaiWeb.StockService
{
    public class StockUtil
    {
        /// <summary>
        /// 获取从当前时间往前推的A股财报报告期（jzrq截止日期）
        /// </summary>
        /// <param name="now">基准时间（一般传DateTime.Now或者UTC时间转本地）</param>
        /// <param name="takeCount">取多少期，一般取4</param>
        /// <returns>报告期截止日期列表，从新到旧排序</returns>
        public static List<DateOnly> GetRecentFinancialReportDates(DateTime now, int takeCount = 4)
        {
            var periods = new List<(int Month, int Day)>
    {
        (12,31),
        (3,31),
        (6,30),
        (9,30)
    };

            var result = new List<DateOnly>();
            var currentYear = now.Year;

            while (result.Count < takeCount)
            {
                foreach (var p in periods)
                {
                    var dt = new DateOnly(currentYear, p.Month, p.Day);
                    // 只保留【小于等于当前日期】的报告期（未来报告期还没出，跳过）
                    if (dt <= DateOnly.FromDateTime(now))
                    {
                        result.Add(dt);
                        if (result.Count >= takeCount)
                            break;
                    }
                }
                currentYear--;
            }
            return result;
        }

        // 全量补数示例（一次性执行，不要定时跑）
        public static List<DateOnly> GetAllReportDateRange(int startYear, int endYear)
        {
            var periods = new List<(int Month, int Day)> { (12, 31), (3, 31), (6, 30), (9, 30) };
            var list = new List<DateOnly>();
            for (int y = startYear; y <= endYear; y++)
            {
                foreach (var p in periods)
                {
                    list.Add(new DateOnly(y, p.Month, p.Day));
                }
            }
            return list;
        }
    }
}
