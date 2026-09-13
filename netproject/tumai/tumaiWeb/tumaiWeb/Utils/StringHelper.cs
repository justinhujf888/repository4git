namespace tumaiWeb.Utils
{
    public class StringHelper
    {
        // 蛇形命名辅助方法
        public static string ToSnakeCase(string name)
        {
            return System.Text.RegularExpressions.Regex.Replace(name, @"([a-z0-9])([A-Z])", "$1_$2").ToLower();
        }
    }
}
