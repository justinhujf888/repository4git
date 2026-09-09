using System.Text;

namespace tumaiWeb.Utils
{
    public class ToFile
    {
        /// <summary>
        /// 将JSON字符串保存到本地文件
        /// </summary>
        /// <param name="str">文本，可以很长</param>
        /// <param name="filePath">完整路径，例如 @"/data/json/result.json"</param>
        /// <returns></returns>
        public static async Task SaveJsonToFileAsync(string str, string filePath)
        {
            // 获取目录路径
            string? dir = Path.GetDirectoryName(filePath);
            if (!Directory.Exists(dir))
            {
                Directory.CreateDirectory(dir); // 不存在则自动创建文件夹
            }

            // UTF8 带BOM可选：new UTF8Encoding(true)
            await File.WriteAllTextAsync(filePath, str, Encoding.UTF8);
        }

        //同步版本（控制台 / 简单后台任务使用，Controller 尽量不要用阻塞 IO）
        public static void SaveJsonToFile(string str, string filePath)
        {
            string? dir = Path.GetDirectoryName(filePath);
            if (!Directory.Exists(dir))
            {
                Directory.CreateDirectory(dir);
            }
            File.WriteAllText(filePath, str, Encoding.UTF8);
        }

        //超大 JSON 场景：流式写入（内容几 MB~ 几十 MB 推荐，减少内存占用）
        public static async Task SaveLargeJsonToFileAsync(string str, string filePath)
        {
            string? dir = Path.GetDirectoryName(filePath);
            if (!Directory.Exists(dir))
            {
                Directory.CreateDirectory(dir);
            }

            await using var fs = new FileStream(filePath, FileMode.Create, FileAccess.Write, FileShare.None);
            await using var writer = new StreamWriter(fs, Encoding.UTF8);
            await writer.WriteAsync(str);
        }

        /// <summary>
        /// 保存json字符串到本地磁盘，自动创建目录，null安全
        /// </summary>
        /// <param name="jsonString">json字符串，允许null</param>
        /// <param name="filePath">完整文件路径</param>
        /// <exception cref="IOException">IO异常</exception>
        public static async Task SaveJsonToFileSafeAsync(string? jsonString, string filePath)
        {
            jsonString ??= "{}";
            string? dir = Path.GetDirectoryName(filePath);
            if (!string.IsNullOrEmpty(dir) && !Directory.Exists(dir))
            {
                Directory.CreateDirectory(dir);
            }
            await File.WriteAllTextAsync(filePath, jsonString, Encoding.UTF8);
        }


        //补充小工具：读取文件回到 JSON 字符串
        public static async Task<string> ReadJsonFromFileAsync(string filePath)
        {
            if (!File.Exists(filePath))
            {
                return string.Empty;
            }
            return await File.ReadAllTextAsync(filePath, Encoding.UTF8);
        }
    }
}
