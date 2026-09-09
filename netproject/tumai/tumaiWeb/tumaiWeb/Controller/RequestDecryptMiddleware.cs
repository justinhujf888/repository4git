using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using Masuit.Tools.Security;
using tumaiWeb.Utils;

namespace tumaiWeb.Controller;

public class RequestDecryptMiddleware
{
    private readonly RequestDelegate _next;
    
    // 和前端约定的32字节AES256密钥，生产环境放到配置中心，不要硬编码
    private const string AesSecretKey = "12345678901234561234567890123456";
    private const string AadConst = "api_req_2026"; //固定AAD，前后端一致
    // 有效期60秒
    private const long ExpireMs = 60 * 1000;

    public RequestDecryptMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        // 1. 过滤：只处理POST、排除上传文件、排除swagger、健康检查等不需要解密的接口
        var path = context.Request.Path.Value;
        if (!context.Request.Method.Equals("POST", StringComparison.OrdinalIgnoreCase)
            || path.StartsWith("/swagger")
            || path.StartsWith("/health"))
        {
            await _next(context);
            return;
        }

        #region ===== 请求解密阶段：读取密文 → AES-GCM解密 → 校验timestamp → 替换Request.Body =====
        // 开启允许读取request.body
        context.Request.EnableBuffering();

        // 读取原始body（前端传过来的密文字符串）
        using var reader = new StreamReader(context.Request.Body);
        var cipherBase64 = await reader.ReadToEndAsync();
        // 重置流位置，备用
        context.Request.Body.Position = 0;

        if (string.IsNullOrWhiteSpace(cipherBase64))
        {
            await _next(context);
            return;
        }

        string plainJson;
        try
        {
            plainJson = CryptoHelper.AESGCMDecrypt(cipherBase64, AesSecretKey, AadConst);
        }
        catch (CryptographicException)
        {
            context.Response.StatusCode = StatusCodes.Status400BadRequest;
            await context.Response.WriteAsJsonAsync(new { code = 400, msg = "解密失败，数据篡改或密钥错误" });
            return;
        }
        catch (Exception)
        {
            context.Response.StatusCode = StatusCodes.Status400BadRequest;
            await context.Response.WriteAsJsonAsync(new { code = 400, msg = "请求解析异常" });
            return;
        }
        #endregion
        
        // 校验时间戳防重放
        JsonElement root;
        try
        {
            using var doc = JsonDocument.Parse(plainJson);
            root = doc.RootElement.Clone();
        }
        catch
        {
            context.Response.StatusCode = StatusCodes.Status400BadRequest;
            await context.Response.WriteAsJsonAsync(new { code = 400, msg = "明文JSON格式错误" });
            return;
        }

        if (!root.TryGetProperty("timestamp", out var tsEl) || !tsEl.TryGetInt64(out long clientTs))
        {
            context.Response.StatusCode = StatusCodes.Status400BadRequest;
            await context.Response.WriteAsJsonAsync(new { code = 400, msg = "缺少timestamp字段" });
            return;
        }

        long nowUtcMs = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
        if (Math.Abs(nowUtcMs - clientTs) > ExpireMs)
        {
            context.Response.StatusCode = StatusCodes.Status400BadRequest;
            await context.Response.WriteAsJsonAsync(new { code = 400, msg = "请求过期，拒绝重放" });
            return;
        }

        // 完整原始JSON直接给到Controller，包含timestamp,q1,q2
        var plainStream = new MemoryStream(Encoding.UTF8.GetBytes(plainJson));
        plainStream.Position = 0;
        context.Request.Body = plainStream;

        #region 响应加密
        var originalResponseBody = context.Response.Body;
        using var responseMs = new MemoryStream();
        context.Response.Body = responseMs;

        await _next(context);

        responseMs.Position = 0;
        string responsePlainText = await new StreamReader(responseMs).ReadToEndAsync();
        string respCipherBase64 = CryptoHelper.AESGCMEncrypt(responsePlainText, AesSecretKey, AadConst);

        context.Response.Body = originalResponseBody;
        context.Response.ContentType = "text/plain";
        await context.Response.WriteAsync(respCipherBase64);
        #endregion
    }
}

/*
// 从请求头读取AAD，不再写死常量
   string? aadHeader = context.Request.Headers["X-Aad-Token"];
   if (string.IsNullOrWhiteSpace(aadHeader))
   {
       context.Response.StatusCode = StatusCodes.Status400BadRequest;
       await context.Response.WriteAsJsonAsync(new { code = 400, msg = "缺少X-Aad-Token请求头" });
       return;
   }
   string AadConst = aadHeader;
   


// 获取AAD（明文接口获取一次，缓存在内存，不要写死在源码）
   async function fetchAadToken() {
       const res = await fetch("/api/crypto/getAad", { method:"GET" });
       const json = await res.json();
       return json.aad;
   }
   
   async function aesGcmEncryptRequest(bizData, cryptoKey, aadStr) {
       const encoder = new TextEncoder();
       const payload = {
           timestamp: Date.now(),
           ...bizData
       };
       const plainText = JSON.stringify(payload);
       const plainBytes = encoder.encode(plainText);
       const aadBytes = encoder.encode(aadStr); // 使用接口动态拿到的aad
   
       const nonce = crypto.getRandomValues(new Uint8Array(12));
       const encResult = await crypto.subtle.encrypt({
           name: "AES-GCM",
           iv: nonce,
           additionalData: aadBytes,
           tagLength: 128
       }, cryptoKey, plainBytes);
   
       const encBuf = new Uint8Array(encResult);
       const cipher = encBuf.slice(0, encBuf.length - 16);
       const tag = encBuf.slice(encBuf.length - 16);
       const combined = new Uint8Array([...nonce, ...cipher, ...tag]);
       return btoa(String.fromCharCode(...combined));
   }
   
   // 使用示例
   (async ()=>{
       const rawKey = "12345678901234561234567890123456";
       const key = await importAesKey(rawKey);
       const aad = await fetchAadToken(); // 动态拉取AAD，js源码不写死
       const biz = {q1:"abc",q2:"def"};
       const cipherBody = await aesGcmEncryptRequest(biz, key, aad);
   
       const resp = await fetch("/api/quote/test", {
           method: "POST",
           headers: {
               "Content-Type": "text/plain",
               "X-Aad-Token": aad // 把AAD放到header传给后端
           },
           body: cipherBody
       });
   })();
   
   

*/