package com.weavict.edu.module

import com.weavict.edu.redis.RedisUtil
import com.weavict.common.util.DateUtil
import com.weavict.weichat.notifies.WxNotifiesFun

//import org.quartz.Job
//import org.quartz.JobExecutionContext
//import org.quartz.JobExecutionException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import jakarta.inject.Inject

@Service("scheduledBean")
//@Repository("ordersBean")
class ScheduledBean extends ModuleBean// implements Job
{

	@Inject
	SchoolService schoolService;

	@Inject
	RedisUtil redisUtil;

	@Inject
	RedisApi redisApi;

	@Scheduled(fixedDelay = 7080000L)
//	@PostConstruct
	void wxInit()
	{
		println "wxInit ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} begin---------------------------------";
		redisApi.buildToken2Redis();
		println "wxInit ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} end---------------------------------";
	}

	//Seconds Minutes Hours DayofMonth Month DayofWeek Year或 Seconds Minutes Hours DayofMonth Month DayofWeek
		// 0 0/5 14,18 * * ? 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
//	@Scheduled(cron = "0/10 * 3 * * ?")    //每5秒执行一次     0 0/58 0/2 * * ? *
	@Scheduled(cron = "0 00 09 * * ?")//0 0 24 * * ?
	@Transactional
	void noticeToWeixin()
	{
//		and to_char(sd.nextdate,'yyyy-mm-dd') = '${DateUtil.format(new Date(),"yyyy-MM-dd")}'
		WxNotifiesFun.redisApi = redisApi;
		this.createNativeQuery("select DISTINCT sd.intention,sd.description,t.name as tName,t.name as tEngName,to_char(sd.modifydate,'yyyy-mm-dd') as modifydate,'' as tHeadImg,sd.id sdid,to_char(sd.nextdate,'yyyy-mm-dd') as nextdate,sd.nextdescription,t.user_id,s.name sname,s.engname sengname,pp.name ppname,pp.id ppid,bai.wxid,bai.wxopenid,s.id sid from studentsalsdetail as sd left join usershop as t on t.user_id = sd.nexthumanid left join studentsals as s on s.id = sd.studentsals_id left join productsprivater as pp on pp.id = s.xiaoid left join buyerappinfo as bai on bai.buyerid = sd.nexthumanid where s.status != 3 and bai.appid = '${WxNotifiesFun.appId}' and to_char(sd.nextdate,'yyyy-mm-dd') = '${DateUtil.format(new Date(),"yyyy-MM-dd")}' ").getResultList()?.each { o->
			WxNotifiesFun.send_workNotice_Message(["humanId"     :"${o[14]}",
												   "humanEngName":"${o[2]}",
												   "workName"    :"约单-【${o[10]} ${o[11]}】",
												   "workDate":"${o[7]}",
												   "xiaoName"    :"${o[12]}",
												   "description":"${o[8]}",
													"studentSalsId":"${o[16]}",
													"appId":"wx6312b87f099a2366"
			]);
		};
	}

	@Scheduled(fixedDelay = 880000L)
	void aliYunSts()
	{
		println "AliYunSts ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} begin---------------------------------";
		redisApi.buildAliYunSts2Redis();
		println "AliYunSts ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} end---------------------------------";
	}

//	@Scheduled(cron = "0 05 00 * * ?")//0 0 24 * * ?
//	@Transactional
//	void dianZanFromRedis()
//	{
//
//	}
	
//	@Override
//	void execute(JobExecutionContext context) throws JobExecutionException
//	{
//		try
//		{
//			println("任务成功运行");
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
}
