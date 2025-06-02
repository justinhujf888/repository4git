package com.weavict.website.common

import cn.hutool.core.codec.Base64
import cn.hutool.core.io.resource.ClassPathResource
import com.aliyun.oss.ClientBuilderConfiguration
import com.aliyun.oss.ClientException
import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.common.auth.CredentialsProvider
import com.aliyun.oss.common.auth.DefaultCredentialProvider
import com.aliyun.oss.common.comm.SignVersion
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.auth.sts.AssumeRoleRequest
import com.aliyuncs.auth.sts.AssumeRoleResponse
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.aliyuncs.profile.IClientProfile
import com.weavict.light.module.RedisApi
import jakarta.annotation.PostConstruct
import jodd.props.Props
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.*

@Component
class OtherUtils
{
	@Autowired
	private RedisApi sourceRedisApi;

	private static RedisApi redisApi;

	private static Props props;

	private static List mapDatas;

	@PostConstruct
	void init()
	{
		redisApi = sourceRedisApi;
	}

	static Props giveTheProps()
	{
		try
		{
			if (props == null)
			{
				props = new Props();
//				props.load(new File(ClassLoaderUtil.getResourceUrl(
//						"/config/global.props").getPath()));
				props.load(new ClassPathResource("/config/global.props").getStream());
			}
			return props;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	static String givePropsValue(String key)
	{
		try
		{
			return giveTheProps().getValue(key);
		}
		catch (Exception e)
		{
			return "";
		}
	}

	static String givePropsValue(String key, String key2)
	{
		try
		{
			return giveTheProps().getValue(key, key2);
		}
		catch (Exception e)
		{
			return "";
		}
	}
	
	static void clearTheProps()
	{
		props = null;
	}

	
	static String giveTheRoundNumber(int a, int b)
	{
		return String.valueOf((int) (Math.random() * a + b));
	}

	static List giveMapDatas()
	{
		return mapDatas;
	}

	static void setMapDatas(List m)
	{
		mapDatas = m;
	}

	static String genOosHostUrl()
	{
		return "https://oss-cn-zhangjiakou.aliyuncs.com";
	}

	static Map genOssAccessKey()
	{
		try
		{
			// 发起STS请求所在的地域。建议保留默认值，默认值为空字符串（""）。
			String regionId = "cn-zhangjiakou";
			// 添加endpoint。适用于Java SDK 3.12.0及以上版本。
			DefaultProfile.addEndpoint(regionId, "Sts", givePropsValue("ali_ram_endPoint"));
			// 添加endpoint。适用于Java SDK 3.12.0以下版本。
			// DefaultProfile.addEndpoint("",regionId, "Sts", endpoint);
			// 构造default profile。
			IClientProfile profile = DefaultProfile.getProfile(regionId, givePropsValue("ali_ram_id"), givePropsValue("ali_ram_key"));
			// 构造client。
			DefaultAcsClient client = new DefaultAcsClient(profile);
			final AssumeRoleRequest request = new AssumeRoleRequest();
			// 适用于Java SDK 3.12.0及以上版本。
			request.setSysMethod(MethodType.POST);
			// 适用于Java SDK 3.12.0以下版本。
			// request.setMethod(MethodType.POST);
			request.setRoleArn(givePropsValue("ali_ram_arn"));
			request.setRoleSessionName("ossSts");
			request.setPolicy(null);
			request.setDurationSeconds(900L);
			final AssumeRoleResponse response = client.getAcsResponse(request);
			return ["expiration":response.getCredentials().getExpiration(),"accessId":response.getCredentials().getAccessKeyId(),"accessKey":response.getCredentials().getAccessKeySecret(),"securityToken":response.getCredentials().getSecurityToken(),"requestId":response.getRequestId()];
		}
		catch (ClientException e)
		{
			println("Failed：");
			println("Error code: " + e.getErrCode());
			println("Error message: " + e.getErrMsg());
			println("RequestId: " + e.getRequestId());
		}
	}

	static OSS genOSSClient()
	{
		// 使用DefaultCredentialProvider方法直接设置AK和SK
		CredentialsProvider credentialsProvider = new DefaultCredentialProvider(redisApi.ganAliYunStsValue("accessId"), redisApi.ganAliYunStsValue("accessKey"), redisApi.ganAliYunStsValue("securityToken"));
		// 使用credentialsProvider初始化客户端
		ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
		// 显式声明使用 V4 签名算法
//		clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
		// 创建OSSClient实例。
		// 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
		return OSSClientBuilder.create()
		// 请设置目的OSS访问域名  例如杭州地域：https://oss-cn-hangzhou.aliyuncs.com
				.endpoint(givePropsValue("ali_ram_endPoint"))
				.credentialsProvider(credentialsProvider)
				.clientConfiguration(clientBuilderConfiguration)
		// 请设置为目标Bucket所处region  例如杭州地域：cn-hangzhou
				.region("cn-zhangjiakou")
				.build();
	}

	static String genHMAC(String data, String key) {
		byte[] result = null;
		try {
			//根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
			SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
			//生成一个指定 Mac 算法 的 Mac 对象
			Mac mac = Mac.getInstance("HmacSHA1");
			//用给定密钥初始化 Mac 对象
			mac.init(signinKey);
			//完成 Mac 操作
			byte[] rawHmac = mac.doFinal(data.getBytes());
			result = Base64.encodeBase64(rawHmac);

		} catch (NoSuchAlgorithmException e) {
			println(e.getMessage());
		} catch (InvalidKeyException e) {
			println(e.getMessage());
		}
		if (null != result) {
			return new String(result);
		} else {
			return null;
		}
	}

	/**
	 * 解密用户敏感数据获取用户信息（用于微信小程序）
	 * @param sessionKey 数据进行加密签名的密钥
	 * @param encryptedData 包括敏感数据在内的完整用户信息的加密数据
	 * @param iv 加密算法的初始向量
	 */
	static String decodeUserInfo4WxMp(String encryptedData,String sessionKey,String iv){
		// 被加密的数据
		byte[] dataByte = Base64.decode(encryptedData);
		// 加密秘钥
		byte[] keyByte = Base64.decode(sessionKey);
		// 偏移量
		byte[] ivByte = Base64.decode(iv);
		try {
			// 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
			int base = 16;
			if (keyByte.length % base != 0) {
				int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
				byte[] temp = new byte[groups * base];
				Arrays.fill(temp, (byte) 0);
				System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
				keyByte = temp;
			}
			// 初始化
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
			SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
			AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
			parameters.init(new IvParameterSpec(ivByte));
			cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
			byte[] resultByte = cipher.doFinal(dataByte);
			if (null != resultByte && resultByte.length > 0) {
				return new String(resultByte, "UTF-8");
			}
		}catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return null;
	}

}
