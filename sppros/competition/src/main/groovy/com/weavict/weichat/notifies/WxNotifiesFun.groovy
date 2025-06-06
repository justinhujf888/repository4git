package com.weavict.weichat.notifies

import com.weavict.website.common.OtherUtils
import com.weavict.weichat.TokenManager
import com.weavict.weichat.Tokens
import jodd.http.HttpRequest
import jodd.util.Base64
import weixin.popular.api.MessageAPI
import weixin.popular.bean.message.templatemessage.TemplateMessage
import weixin.popular.bean.message.templatemessage.TemplateMessageItem

class WxNotifiesFun
{
	static String default_color = "#173177";
	
	static String title_color = "#FC0004";
	
	static String appurl = """${OtherUtils.givePropsValue("prxurl")}${(!OtherUtils.givePropsValue("prxconname").equals("null")) ? OtherUtils.givePropsValue("prxconname") : ""}""";
	
	static String riurl = """${appurl}/usersns.groovy?gourl=${appurl}""";

	static String publicURL = """http://s.hui2life.com""";
	
	static void send_ClassReport_Message(Map mp)
	{
		TemplateMessage templateMessage = new TemplateMessage();
		templateMessage.template_id = "4gFcZHCDgUf83ZQx9y8s66KO4WPBzTHArY6RN9mPDSo";
		templateMessage.touser = mp["parentId"];
		templateMessage.data = [];
		templateMessage.data << ["first":new TemplateMessageItem("您好，您的孩子【${mp["studentName"]}】已成功完成一次课堂学习",title_color)];
		templateMessage.data << ["keyword1":new TemplateMessageItem(mp["studentName"],default_color)];
		templateMessage.data << ["keyword2":new TemplateMessageItem(mp["studyType"],default_color)];
		templateMessage.data << ["keyword3":new TemplateMessageItem(mp["xiao"],default_color)];
		templateMessage.data << ["keyword4":new TemplateMessageItem(mp["dateStr"],default_color)];
		templateMessage.data << ["keyword5":new TemplateMessageItem(mp["teacherName"],default_color)];
		templateMessage.data << ["remark":new TemplateMessageItem("具体信息请点击查看学习详情报告",title_color)];
		templateMessage.url = "${publicURL}/?param=${URLEncoder.encode(Base64.encodeToString("""{"url":"./school/student/studydayreport","param":{"xiaoId":"${mp['xiaoId']}","studentId":"${mp['studentId']}","groupId":"${mp['groupId']}","teacherId":"${mp['teacherId']}","dateStr":"${mp['dateStr']}"}}"""),"utf-8")}";
		println templateMessage.url;
		MessageAPI.messageTemplateSend(TokenManager.getToken(Tokens.getTheAppid(null)),templateMessage);
	}

//	WxNotifiesFun.send_getProductFromShop_Message(["mxPage":"index","shopName":"中信东泰花园荣华园菜鸟驿站",
//            "productName":"威尔吸尘器","phone_number3":"13268990066","shopAddress":"中信东泰花园荣华园155号"]);
	static void send_getProductFromShop_Message(Map mp)
	{
		println HttpRequest.post("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=${TokenManager.getToken("mp1")}")
				.bodyText("""{
"touser":"${mp["userOpenId"]}",
"template_id":"GL0V4LeBdgmdJMC6XK7JibhtGgY-6em3L41CGknWro8",
"page":"${mp["mxPage"]}",
"data":{
"thing1":{"value":"${mp["shopName"]}"},
"thing4":{"value":"${mp["productName"]}"},
"phone_number3":{"value":"${mp["phone_number3"]}"},
"thing8":{"value":"${mp["shopAddress"]}"}
}}""","json","utf-8").send().body();
	}

	static void send_petsYiMiao_Message(Map mp)
	{
		println HttpRequest.post("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=${TokenManager.getToken("mp1")}")
				.bodyText("""{
"touser":"${mp["userOpenId"]}",
"template_id":"_z3xiIT0LAH5biw9Lcpjc4SKcx70GbXTxwSoHmuRs4Q",
"page":"${mp["mxPage"]}",
"data":{
"name1":{"value":"${mp["petsName"]}"},
"date2":{"value":"${mp["date"]}"},
"thing3":{"value":"${mp["remark"]}"}
}}""","json","utf-8").send().body();
	}

	static void send_petsQuChong_Message(Map mp)
	{
		println HttpRequest.post("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=${TokenManager.getToken("mp1")}")
				.bodyText("""{
"touser":"${mp["userOpenId"]}",
"template_id":"gxNwvJ3zqCCTmSziGr1MnCU1-6wpwmaY7xzJvrGrZlY",
"page":"${mp["mxPage"]}",
"data":{
"name1":{"value":"${mp["petsName"]}"},
"date2":{"value":"${mp["date"]}"},
"thing3":{"value":"${mp["remark"]}"}
}}""","json","utf-8").send().body();
	}
}
