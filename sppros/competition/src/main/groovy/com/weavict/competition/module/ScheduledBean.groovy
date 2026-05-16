package com.weavict.competition.module

import com.alibaba.fastjson.JSON
import com.aliyun.oss.OSSClient
import com.aliyun.oss.model.CannedAccessControlList
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import com.weavict.competition.redis.RedisUtil
import com.weavict.website.common.OtherUtils
import com.weavict.weichat.notifies.WxNotifiesFun
import jakarta.annotation.PostConstruct
import jodd.datetime.JDateTime
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

//import org.quartz.Job
//import org.quartz.JobExecutionContext
//import org.quartz.JobExecutionException

import org.springframework.transaction.annotation.Transactional
import weixin.popular.api.PayMchAPI
import weixin.popular.bean.paymch.SecapiPayRefund
import weixin.popular.bean.paymch.SecapiPayRefundResult

import jakarta.inject.Inject

@Service("scheduledBean")
//@Repository("ordersBean")
class ScheduledBean extends ModuleBean// implements Job
{
	@Inject
	RedisUtil redisUtil;

	@Inject
	RedisApi redisApi;

//	@Scheduled(fixedDelay = 7080000L)
	@PostConstruct
	void wxInit()
	{
		println "wxInit ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} begin---------------------------------";
		redisUtil.delete("ossApps");
//		redisApi.buildToken2Redis();
		println "wxInit ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} end---------------------------------";
	}

	@Scheduled(fixedDelay = 880000L)
	void aliYunSts()
	{
		if (!redisUtil.hasKey("ossApps"))
		{
			redisApi.buildToken2Redis();
		}
		println "AliYunSts ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} begin--------------------------------";
//		println redisUtil.lLen("apps");
		for(int i=0; i<(redisUtil.lLen("ossApps")) as int;i++)
		{
//			println redisUtil.lIndex("ossApps",i as long);
//			println redisUtil.hGet("""appToken_${redisUtil.lIndex("ossApps",i as long)}_9""" as String,"mapJson");
			Map map = JSON.parse(redisUtil.hGet("""appToken_${redisUtil.lIndex("ossApps",i as long)}_9""" as String,"mapJson")) as Map;
//			println map;
			map["appId"] = redisUtil.lIndex("ossApps",i as long);
			redisApi.buildAliYunSts2Redis(map);
		}
		println "AliYunSts ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} end---------------------------------";
	}
}
