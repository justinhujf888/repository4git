package com.weavict.light.module

import cn.hutool.core.date.DateUtil
import com.weavict.common.aliyun.AliyunStsFactory
import com.weavict.common.wechat.WxMaDynamicServiceFactory
import com.weavict.light.entity.PayWayInfoEntity
import com.weavict.light.redis.RedisUtil
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

//import org.quartz.Job
//import org.quartz.JobExecutionContext
//import org.quartz.JobExecutionException

import jakarta.inject.Inject

@Service("scheduledBean")
//@Repository("ordersBean")
class ScheduledBean extends ModuleBean// implements Job
{
	@Inject
	RedisUtil redisUtil;

	@Inject
	RedisApi redisApi;

    @Inject
    AliyunStsFactory aliyunStsFactory;

    @Inject
    WxMaDynamicServiceFactory wxMaDynamicServiceFactory;

    @PostConstruct
    void init()
    {
        redisApi.buildToken2Redis();
    }

	@Scheduled(fixedDelay = 7080000L)
	void wxInit()
	{
		println "wxSts ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} begin---------------------------------";
        for(entry in wxMaDynamicServiceFactory.getStsApps().entrySet())
        {
            PayWayInfoEntity payWayInfoEntity = entry.value as PayWayInfoEntity;
            Map map = payWayInfoEntity.mapJson.wechat;
            map["appId"] = payWayInfoEntity.payWayInfoEntityPK.appId;
            redisApi.buildWechatSts2Redis(map);
        }
		println "wxSts ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} end---------------------------------";
	}

    @Scheduled(fixedDelay = 880000L)
    void aliYunSts()
    {
        println "AliYunSts ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} begin--------------------------------";
//		println redisUtil.lLen("apps");
        for(entry in aliyunStsFactory.getStsApps().entrySet())
        {
//			println redisUtil.lIndex("ossApps",i as long);
//			println redisUtil.hGet("""appToken_${redisUtil.lIndex("ossApps",i as long)}_9""" as String,"mapJson");
            PayWayInfoEntity payWayInfoEntity = entry.value as PayWayInfoEntity;
            Map map = payWayInfoEntity.mapJson.aliyun;
//			println map;
            map["appId"] = payWayInfoEntity.payWayInfoEntityPK.appId;
            redisApi.buildAliYunSts2Redis(map);
        }
        println "AliYunSts ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} end---------------------------------";
    }
}
