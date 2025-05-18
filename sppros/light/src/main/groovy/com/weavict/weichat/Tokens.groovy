package com.weavict.weichat

import com.weavict.website.common.OtherUtils
import groovy.json.JsonSlurper
import jodd.http.HttpRequest

class Tokens
{
	static String getTheAppid(String mname)
	{
		return OtherUtils.givePropsValue("appid");
	}
	static String getTheSecret(String mname)
	{
		return OtherUtils.givePropsValue("secret");
	}
	static String getTheMchId(String mname)
	{
		return OtherUtils.givePropsValue("mchid");
	}
	static String getTheMchKey(String mname)
	{
		return OtherUtils.givePropsValue("mchkey");
	}
	static String getTheAccessToken(String mname)
	{
		return TokenManager.getToken(OtherUtils.givePropsValue("appid"));
	}

	static String getTheJSTicket(String mname)
	{
		return TokenManager.getTicket(OtherUtils.givePropsValue("appid"));
	}

	static String getTheAppName(String mname)
	{
		return OtherUtils.givePropsValue("appname");
	}

	static String getToken8WxMp(String mp)
	{
		def jsonSlpuer = new JsonSlurper();
		def mp1Json = jsonSlpuer.parseText(OtherUtils.givePropsValue(mp));
		return jsonSlpuer.parseText(HttpRequest.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=${mp1Json.appId}&secret=${mp1Json.key}").send().body()).access_token;
	}
}
