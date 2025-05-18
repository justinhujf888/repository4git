package com.weavict.website.common

import cn.hutool.core.codec.Base64
import cn.hutool.core.io.resource.ClassPathResource
import com.aliyun.oss.OSSClient
import jodd.props.Props
import jodd.util.ClassLoaderUtil
import org.bouncycastle.jce.provider.BouncyCastleProvider

import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.*

class OtherUtils
{
	private static Props props;

	private static List mapDatas;

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

	static Map genOssAccessKey(String ramUser)
	{
		if (OtherUtils.givePropsValue("prxurl").equals("https://service.daxiabang.club"))
		{
			switch (ramUser)
			{
//			侠世代
				case "blogs":
					return ["accessId":"LTAImpSRSRhb6fRY","accessKey":"PTI9EHHLYUTQ4LoWLA9qWyRR2jjBG1"];
				break;
				case "privater":
					return ["accessId":"LTAIp1qNwv7OwwWS","accessKey":"SGa4OLv2bCTVOcO71t0gR2LojJva1g"];
				break;
				case "ross":
					return ["accessId":"LTAIEsEAqNE86mLl","accessKey":"1GUpN6G5BYxZnbJIIHvFtCNLW1QNll"];
				break;
				default:
					return null;
			}
		}
		else if (OtherUtils.givePropsValue("prxurl").equals("https://service.arkydesign.cn"))
		{
			switch (ramUser)
			{
//			Pets
				case "blogs":
					return ["accessId":"LTAI4FtgU7bRJYY96eYKCCAW","accessKey":"44kPcPGXEtAueIrHaXx1nyLhCItAK1"];
					break;
				case "privater":
					return ["accessId":"LTAI4FsA86LBrDwa8zrqrdrk","accessKey":"9O6xfqohiucntEmtURNG0sAKBdbXXD"];
					break;
				case "ross":
					return ["accessId":"LTAI4FpbYmCTuuZPe4fjRL2g","accessKey":"O0l2I0gnNZsWwkkqhSr0ltDibi5r2I"];
					break;
				default:
					return null;
			}
		}
		return null;
	}

	static OSSClient genOSSClient()
	{
		new OSSClient(this.genOosHostUrl(), this.genOssAccessKey("ross").accessId, this.genOssAccessKey("ross").accessKey);
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
