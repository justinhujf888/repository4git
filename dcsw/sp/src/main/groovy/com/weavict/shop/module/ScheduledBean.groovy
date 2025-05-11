package com.weavict.shop.module

import com.aliyun.oss.OSSClient
import com.aliyun.oss.model.CannedAccessControlList
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import com.weavict.shop.entity.Orgration
import com.weavict.shop.entity.Product
import com.weavict.shop.entity.Twiter
import com.weavict.shop.entity.TwiterZan
import com.weavict.shop.redis.RedisUtil
import com.weavict.website.common.OtherUtils
import com.weavict.weichat.notifies.WxNotifiesFun
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

	@Inject
	ProductsBean productsBean;

	@Inject
	OrderBean orderBean;

	@Inject
	TwiterBean twiterBean;

	@Scheduled(fixedDelay = 7080000L)
//	@PostConstruct
	void wxInit()
	{
		println "wxInit ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} begin---------------------------------";
		redisApi.buildToken2Redis();
		println "wxInit ${DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")} end---------------------------------";
	}

	static boolean runPayOrderFlag = false;
	@Scheduled(fixedDelay = 7080000L)
	void payRunInit()
	{
		if (!runPayOrderFlag)
		{
			println "pay 队列启动";
			redisApi.runPayOrderArray();
		}
	}

	//Seconds Minutes Hours DayofMonth Month DayofWeek Year或 Seconds Minutes Hours DayofMonth Month DayofWeek
		// 0 0/5 14,18 * * ? 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
//	@Scheduled(cron = "0/10 * 3 * * ?")    //每5秒执行一次     0 0/58 0/2 * * ? *
	@Scheduled(cron = "0 00 23 * * ?")//0 0 24 * * ?
	void productsIntoRedis()
	{
		ObjectMapper objectMapper = new ObjectMapper();
		List<Product> productList = new ArrayList();
		this.queryObject("select p from Product as p where p.isMarketable = :isMarketable",["isMarketable":true])?.each {p->
//			Product product = new Product();
//			BeanUtils.copyProperties(product,p);
//			product.cancelLazyEr();
//			product.productPrivater = null;
//			productList << product;

			p.cancelLazyEr();
			p.productPrivater = null;
			productList << p;
		};
		// oss
		OSSClient ossClient = OtherUtils.genOSSClient();
		ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "system/json/product/indexPage.json", new ByteArrayInputStream(objectMapper.writeValueAsString(productList).getBytes("UTF-8")));
		ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "system/json/product/indexPage.json", CannedAccessControlList.PublicRead);
		ossClient.shutdown();
		// oss end
	}

	@Scheduled(cron = "0 00 00 * * ?")//0 0 24 * * ?
	@Transactional
	void couponProcess()
	{
		orderBean.createNativeQuery("update buyercoupon set usedate = now(),shiguoqi = true where enddate < now()").executeUpdate();
	}

	@Scheduled(cron = "0 59 23 * * ?")//0 0 24 * * ?
	@Transactional
	void endTuan()
	{
		List<Orgration> orgList = orderBean.queryObject("select o from Orgration o");
		orderBean.queryTuanList(null,true,false)?.each {
			if ( (new JDateTime(new Date())).compareDateTo((new JDateTime(it.endDate))) > (-1 as int) )
			{
				it.ended = true;
				orderBean.updateTheObject(it);
				if (redisUtil.hasKey("tuan_${it.productId}"))
				{
					redisUtil.delete("tuan_${it.productId}");
				}
				orgList.each {org->
					if (redisUtil.hasKey("tuanOrder_${it.productId}_${org.id}"))
					{
						redisUtil.delete("tuanOrder_${it.productId}_${org.id}");
					}
					if (redisUtil.hasKey("tuanBuyers_${it.productId}_${org.id}"))
					{
						redisUtil.delete("tuanBuyers_${it.productId}_${org.id}");
					}
				}
//				最小团购量是否达到
//				select sum(pr.deliveryquantity) from payreturnorder pr left join orderbuyers ob on pr.id = ob.id left join orders o on o.id = ob.order_id left join tuaninfo tuan on tuan.id = o.tuaninfoid where tuan.id = '202007071730197687230'

//				小拼团的处理，1、对已经满足最小团购量的拼团进行抽奖

			}
		}
	}

	@Scheduled(cron = "0 00 23 * * ?")//0 0 24 * * ?
	@Transactional
	void dianZanFromRedis()
	{
		writeDianZanFromRedis();
	}

//	@Scheduled(cron = "0 00 23 * * *")//0 0 24 * * ?
	void petsFangyu()
	{
		this.queryObject("select pets from Pets pets where pets.petsStatus <> :petsStatus",["petsStatus":1 as byte])?.each {pets->
			JDateTime jd = new JDateTime(new Date());
			JDateTime jd2 = null;
			if (pets.vaccineDate!=null)
			{
				jd2 = new JDateTime(pets.vaccineDate);
				println "================1===============";
				println "jd:" + jd.getMonth();
				println "jd2:" + jd2.getMonth();
				println jd.getMonth()==jd2.getMonth();
				println jd2.getDay()-jd.getDay() in [1,0];
				if (jd.getMonth()==jd2.getMonth() && jd2.getDay()-jd.getDay() in [1,0])
				{
					WxNotifiesFun.send_petsYiMiao_Message([
							"userOpenId":pets.buyer.wxid,"mxPage":"/pages/pet/petsList","petsName":pets.name,
							"date": DateUtil.format(new Date(),"yyyy-MM-dd HH:mm"),
							"remark": "${pets.name}该打疫苗了，时间：${jd2.getMonth()}月${jd2.getDay()}日"
					]);
				}
			}
			if (pets.clearBugDate!=null)
			{
				jd2 = new JDateTime(pets.clearBugDate);
				if (pets.geMoth < 0)
				{
					pets.geMoth = 0;
				}
				println "================2===============";
				println "jd:" + jd.getMonth();
				println "jd2:" + jd2.getMonth();
				println jd2.getDay()-jd.getDay() in [1,0];

				if (jd.getMonth()>=jd2.getMonth() && jd2.getDay()-jd.getDay() in [1,0])
				{
					if (pets.geMoth==0)
					{
						WxNotifiesFun.send_petsQuChong_Message([
								"userOpenId":pets.buyer.wxid,"mxPage":"/pages/pet/petsList","petsName":pets.name,
								"date": DateUtil.format(new Date(),"yyyy-MM-dd HH:mm"),
								"remark": "${pets.name}该驱虫了，时间：${jd2.getDay()}日"
						]);
					}
					else
					{
						int fadd = jd2.getMonth() + pets.geMoth + 1;
						while (fadd<13)
						{
							if (fadd==jd.getMonth())
							{
								WxNotifiesFun.send_petsQuChong_Message([
										"userOpenId":pets.buyer.wxid,"mxPage":"/pages/pet/petsList","petsName":pets.name,
										"date": DateUtil.format(new Date(),"yyyy-MM-dd HH:mm"),
										"remark": "${pets.name}该驱虫了，上次时间：${jd2.getDay()}日"
								]);
								break;
							}
							fadd = fadd + pets.geMoth + 1;
						}
					}
				}
			}
		}
	}
	
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

	private writeDianZanFromRedis()
	{
		JDateTime bjt = new JDateTime(new Date());
		bjt.addMonth(-90);
		JDateTime ejt = new JDateTime(new Date());
		twiterBean.queryObject("select tw from Twiter as tw where tw.twiterType = 0 and tw.createDate between :beginDate and :endDate order by tw.createDate desc",
				["beginDate":bjt.convertToDate(),"endDate":ejt.convertToDate()])?.each {tw->
			if (redisUtil.hasKey("twiterZanSet_${tw.id}"))
			{
				redisUtil.setMembers("twiterZanSet_${tw.id}").each {buyerId->
					def c = twiterBean.querySingleObject("select count(tz) from TwiterZan as tz where tz.buyerId = :buyerId and tz.twiter.id = :twiterId",["twiterId":tw.id,"buyerId":buyerId]);
					if ((c as int) < 1)
					{
						TwiterZan twiterZan = new TwiterZan();
						twiterZan.id = MathUtil.getPNewId();
						twiterZan.buyerId = buyerId;
						twiterZan.twiter = new Twiter();
						twiterZan.twiter.id = tw.id;
						twiterZan.createDate = new Date();
						twiterBean.addObject(twiterZan);
					}
				}
			}
		}
	}

//	@Scheduled(cron = "0 00 23 * * *")//0 0 24 * * ?
	void clearMapInfos()
	{
		redisUtil.delete("pets_map");
	}

	@Scheduled(cron = "0 02 02 * * *")//0 0 24 * * ?
	@Transactional
	void tuiKuanProcess()
	{
		this.createNativeQuery("""select pr.tradeno,ob.id,ob.deliveryquantity,ob.price,pr.paymentfee,pr.appid,pr.paytype,pr.refundid from orderbuyers ob left join payreturnorder pr on pr.id = ob.id where ob.orderstatus = 11
union
select pr.tradeno,o.id,o.ordertotalqutitynum,o.ordertotalamount,o.paymentfee,pr.appid,pr.paytype,pr.refundid from orders o left join payreturnorder pr on pr.id = o.payreturnorderid where o.ordertype = 0 and o.orderstatus = 11
""").getResultList()?.each {data->
			if (data[6] as byte == 1 as byte)
			{
				SecapiPayRefund secapiPayRefund = new SecapiPayRefund();
				secapiPayRefund.appid = redisApi.ganTokenValue(data[5],data[6] as byte,"appId");
				secapiPayRefund.mch_id = redisApi.ganTokenValue(data[5],data[6] as byte,"mchId");
				secapiPayRefund.notify_url = redisApi.ganTokenValue(data[5],data[6] as byte,"returnDoMain");
				secapiPayRefund.nonce_str = UUID.randomUUID().toString().toString().replace("-", "");
				secapiPayRefund.transaction_id = data[0];
				secapiPayRefund.out_trade_no = data[7];
				secapiPayRefund.out_refund_no = data[7];
				secapiPayRefund.total_fee = data[4] as int;
				secapiPayRefund.refund_fee = data[4] as int;
				secapiPayRefund.refund_desc = "";
				SecapiPayRefundResult secapiPayRefundResult = PayMchAPI.secapiPayRefund(secapiPayRefund,redisApi.ganTokenValue(data[5],data[6] as byte,"partnerKey"));
				if(secapiPayRefundResult.sign_status != null && secapiPayRefundResult.sign_status)
				{
					// 退款信息提交成功;
					if("SUCCESS".equals(secapiPayRefundResult.return_code))
					{
						println "微信退款接口--接口请求状态(result_code):${secapiPayRefundResult.result_code}";
						println "微信退款接口--接口请求状态(err_code):${secapiPayRefundResult.err_code}";
						println "微信退款接口--接口请求状态(err_code_des):${secapiPayRefundResult.err_code_des}";
						this.createNativeQuery4Params("update payreturnorder set payreturndatas = :xml where id = :id",["id":data[1],"xml":"""微信退款接口--接口请求状态 (result_code):${secapiPayRefundResult.result_code};(err_code):${secapiPayRefundResult.err_code};(err_code_des):${secapiPayRefundResult.err_code_des}""".toString()]).executeUpdate();
					}
					else
					{
						println "微信退款接口--接口请求状态(result_code):${secapiPayRefundResult.result_code}";
						println "微信退款接口--接口请求状态(err_code):${secapiPayRefundResult.err_code}";
						println "微信退款接口--接口请求状态(err_code_des):${secapiPayRefundResult.err_code_des}";
						this.createNativeQuery4Params("update payreturnorder set payreturndatas = :xml where id = :id",["id":data[1],"xml":"""微信退款接口--接口请求状态 (result_code):${secapiPayRefundResult.result_code};(err_code):${secapiPayRefundResult.err_code};(err_code_des):${secapiPayRefundResult.err_code_des}""".toString()]).executeUpdate();
					}
				}
			}
		}
	}
}
