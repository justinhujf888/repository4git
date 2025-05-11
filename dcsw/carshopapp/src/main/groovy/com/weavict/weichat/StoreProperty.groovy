package com.weavict.weichat

import jodd.datetime.JDateTime
import jodd.util.ClassLoaderUtil

import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder

import weixin.popular.api.TokenAPI
import weixin.popular.bean.BaseResult
import weixin.popular.bean.token.Token
import weixin.popular.client.LocalHttpClient

import com.weavict.website.common.OtherUtils

class StoreProperty
{
	static Token access_token;
	
	static Date date;
	
	static String buildaccessToken(String appid)
	{
		if (access_token == null || new JDateTime(System.currentTimeMillis()).compareTo(new JDateTime(date))>6000)
		{
			date = new Date();
			access_token = TokenAPI.token(OtherUtils.givePropsValue("appid"), OtherUtils.givePropsValue("secret"));
		}
		return access_token.access_token;
	}
	
	static boolean checkTokenVaild(String access_token,String openid)
	{
		try
		{
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri("https://api.weixin.qq.com/sns/auth")
					.addParameter("access_token", access_token)
					.addParameter("openid", openid)
					.build();
			//BaseResult b = LocalHttpClient.executeJsonResult(httpUriRequest,BaseResult.class);
			if (LocalHttpClient.executeJsonResult(httpUriRequest,BaseResult.class).getErrcode().equals("0"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	static void main(String[] args)
	{
		//println OtherUtils.givePropsValue("appid");
		println ClassLoaderUtil.getResourceFile(
						"/config/global.props")
	}
}
