package com.weavict.carshopapp.module

import com.weavict.common.util.DateUtil
import com.weavict.weichat.TokenManager
import com.weavict.weichat.Tokens
import jodd.datetime.JDateTime
//import org.quartz.Job
//import org.quartz.JobExecutionContext
//import org.quartz.JobExecutionException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import jakarta.inject.Inject
import jakarta.transaction.Transactional

@Service("scheduledBean")
//@Repository("ordersBean")
class ScheduledBean extends ModuleBean// implements Job
{
	@Inject
	OrderService orderService;
//	@Inject
//	RedisUtil redisUtil;

	//Seconds Minutes Hours DayofMonth Month DayofWeek Year或 Seconds Minutes Hours DayofMonth Month DayofWeek
		// 0 0/5 14,18 * * ? 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
//	@Scheduled(cron = "0/10 * 3 * * ?")    //每5秒执行一次     0 0/58 0/2 * * ? *

	@Scheduled(fixedDelay = 7080000L)
//	@PostConstruct
	void wxInit()
	{
		println "wxInit ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} begin---------------------------------";
		TokenManager.initKey(Tokens.getTheAppid(""), Tokens.getTheSecret(""));
		println "wxInit ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} end---------------------------------";
	}


//	@Scheduled(cron = "59 59 23 * * ?")//0 0 24 * * ?
	@Transactional
	void endShopService()
	{
		orderService.queryObject("select shop from Shop shop").each {shop->
			if ( (new JDateTime(new Date())).compareDateTo((new JDateTime(shop.nextDate))) > (-1 as int) )
			{
				orderService.executeEQL("update Shop set status = :status where id = :id",["id":shop.id,"status":0 as byte]);
			}
		}
	}


//	@Override
//	void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
//	{
//
//	}
}
