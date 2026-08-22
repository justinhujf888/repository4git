package com.weavict.competition.module

import cn.hutool.core.date.DateUtil
import com.weavict.common.aliyun.AliyunStsFactory
import com.weavict.competition.entity.PayWayInfoEntity
import com.weavict.competition.redis.RedisUtil
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
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

//	@Scheduled(fixedDelay = 7080000L)
	@PostConstruct
	void init()
	{
        redisApi.buildToken2Redis();
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
