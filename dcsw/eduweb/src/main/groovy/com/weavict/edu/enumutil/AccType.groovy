package com.weavict.edu.enumutil

import com.weavict.common.util.DateUtil

class AccType
{
	static bookTypes = [0 as byte,1 as byte,2 as byte,3 as byte,4 as byte];

	static bookCates = [0 as byte,1 as byte,2 as byte,3 as byte];

	static orderStatus = [0:"未付款",1:"待发货",
		2:"已发货",3:"已完成",4:"已取消",5:"已退款"];
	
	static orderTypes = [0:"普通订单",1:"队列拼团",
		2:"建组拼团",3:"生日优惠订单",4:"优惠活动订单",5:"一元购",6:"0元抢购",7:"充值订单"];
	
	static paymentconfig = ["wxpay":["402881862b9f2bff012b9f2d616a0001","微信支付"],"yepay":["1","余额支付"]];
	
	static paymentType = [0:"购物金额",1:"充值金额"];
	
	static wxpayconfig = [(0 as byte):"微信在线支付",(2 as byte):"扫码支付",(3 as byte):"刷卡支付"];
	
	static buycardtype = [(0 as byte):"积分优惠",(1 as byte):"生日优惠",(2 as byte):"代金券",(3 as byte):"打折券"];

	static xiaoArea = [
			0:["name":"白天鹅分校","area":"","address":""],
			1:["name":"丽江花园分校","area":"","address":""]
	];
	
	String receorderStatus(int v)
	{
		return orderStatus[v];
	}
	
	String receorderType(int v)
	{
		return orderTypes[v];
	}
	
	String receBuyCardType(byte v)
	{
		return buycardtype[v];
	}
	
	String rnSpecName(String v)
	{
		return (v==null || v.equals("")) ? "" : "【${v}】";
	}
	
	String showProNameSame(String v,int i)
	{
		if (v==null || v.equals(""))
		{
			return "";
		}
		else if (v.length()>i)
		{
			return "${v[0..i-1]}...";
		}
		else
		{
			return v;
		}
	}
	
	String receMemberSex(byte v)
	{
		return (v==(0 as byte)) ? "先生" : "女士";
	}
	
	String receWxpayconfig(byte v)
	{
		return wxpayconfig[v];
	}

	String formatDate(String dateStr)
	{
		return DateUtil.format(DateUtil.parse(dateStr));
	}

	String formatDate(String dateStr,String fs)
	{
		return DateUtil.format(DateUtil.parse(dateStr),fs);
	}

	String formatDate(Date date)
	{
		return DateUtil.format(date);
	}

	String formatDate(Date date,String fs)
	{
		return DateUtil.format(date,fs);
	}
}
