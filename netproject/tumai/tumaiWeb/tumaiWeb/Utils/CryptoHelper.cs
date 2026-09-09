using System.Security.Cryptography;
using System.Text;

namespace tumaiWeb.Utils;

public class CryptoHelper
{
    /// <summary>
    /// AES-GCM 加密
    /// 返回 Base64( nonce(12) + cipher + tag(16) )
    /// </summary>
    /// <param name="plainText">明文</param>
    /// <param name="key">32字节 AES256 UTF8字符串</param>
    /// <param name="aad">附加认证数据</param>
    /// <returns>打包后的base64字符串</returns>
    public static string AESGCMEncrypt(string plainText, string key, string aad)
    {
        byte[] keyBytes = Encoding.UTF8.GetBytes(key);
        byte[] plainBytes = Encoding.UTF8.GetBytes(plainText);
        byte[] aadBytes = Encoding.UTF8.GetBytes(aad);

        // 12字节Nonce
        byte[] nonce = new byte[12];
        RandomNumberGenerator.Fill(nonce);

        using var aesGcm = new AesGcm(keyBytes, 16);
        byte[] cipherText = new byte[plainBytes.Length];
        byte[] tag = new byte[16];

        aesGcm.Encrypt(nonce, plainBytes, tag, cipherText, aadBytes);

        // nonce + cipher + tag
        byte[] combined = new byte[nonce.Length + cipherText.Length + tag.Length];
        Buffer.BlockCopy(nonce, 0, combined, 0, nonce.Length);
        Buffer.BlockCopy(cipherText, 0, combined, nonce.Length, cipherText.Length);
        Buffer.BlockCopy(tag, 0, combined, nonce.Length + cipherText.Length, tag.Length);

        return Convert.ToBase64String(combined);
    }
    
    /// <summary>
    /// AES-GCM解密
    /// </summary>
    /// <param name="combinedBase64">前端传来的base64: nonce(12)+cipher+tag(16)</param>
    /// <param name="aesKey">32字节AES256密钥，UTF8字符串，前后端一致</param>
    /// <param name="aad">附加认证数据，这里是timestamp字符串</param>
    /// <returns>明文JSON字符串</returns>
    public static string AESGCMDecrypt(string combinedBase64, string key, string aad)
    {
        byte[] combined = Convert.FromBase64String(combinedBase64);
        const int NonceSize = 12;
        const int TagSize = 16;

        if (combined.Length < NonceSize + TagSize)
            throw new CryptographicException("密文长度不足");

        Span<byte> nonce = combined.AsSpan(0, NonceSize);
        Span<byte> tag = combined.AsSpan(combined.Length - TagSize, TagSize);
        Span<byte> cipherText = combined.AsSpan(NonceSize, combined.Length - NonceSize - TagSize);

        byte[] keyBytes = Encoding.UTF8.GetBytes(key);
        byte[] aadBytes = Encoding.UTF8.GetBytes(aad);

        using var aesGcm = new AesGcm(keyBytes, TagSize);
        byte[] plainBytes = new byte[cipherText.Length];
        aesGcm.Decrypt(nonce, cipherText, tag, plainBytes, aadBytes);

        return Encoding.UTF8.GetString(plainBytes);
    }
}