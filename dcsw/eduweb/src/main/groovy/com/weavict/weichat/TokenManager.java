package com.weavict.weichat;

import com.weavict.common.util.DateUtil;
import com.weavict.website.common.OtherUtils;
import weixin.popular.api.TicketAPI;
import weixin.popular.api.TokenAPI;
import weixin.popular.bean.ticket.Ticket;
import weixin.popular.bean.token.Token;
import weixin.popular.client.LocalHttpClient;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * TokenManager token 自动刷新
 * @author LiYi
 *
 */
public class TokenManager{

	private static ScheduledExecutorService scheduledExecutorService;

	private static Map<String,String> tokenMap = new LinkedHashMap<String,String>();
	
	private static Map<String,String> ticketMap = new LinkedHashMap<String,String>();

	private static Map<String,ScheduledFuture<?>> futureMap = new HashMap<String, ScheduledFuture<?>>();

	private static int poolSize = 2;
	
	private static boolean daemon = Boolean.TRUE;

	/**
	 * 初始化 scheduledExecutorService
	 */
	private static void initScheduledExecutorService(){
		scheduledExecutorService =  Executors.newScheduledThreadPool(poolSize,new ThreadFactory() {

			@Override
			public Thread newThread(Runnable arg0) {
				Thread thread = Executors.defaultThreadFactory().newThread(arg0);
				//设置守护线程
				thread.setDaemon(daemon);
				return thread;
			}
		});
	}

	/**
	 * 设置线程池
	 * @param poolSize
	 */
	public static void setPoolSize(int poolSize){
		TokenManager.poolSize = poolSize;
	}
	
	/**
	 * 设置线程方式
	 * @param daemon
	 */
	public static void setDaemon(boolean daemon) {
		TokenManager.daemon = daemon;
	}

	/**
	 * 初始化token 刷新，每118分钟刷新一次。
	 * @param appid
	 * @param secret
	 */
	public static void init(final String appid,final String secret){
		if(scheduledExecutorService == null){
			initScheduledExecutorService();
		}
		if(futureMap.containsKey(appid)){
			futureMap.get(appid).cancel(true);
		}
		ScheduledFuture<?> scheduledFuture =  scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				Token token = TokenAPI.token(appid,secret);
				System.out.println(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"get token:" + token.getAccess_token());
				tokenMap.put(appid,token.getAccess_token());
				Ticket t = TicketAPI.ticketGetticket(TokenManager.getToken(OtherUtils.givePropsValue("appid")));
				System.out.println(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"getticket:" + t.getTicket());
				ticketMap.put(appid,t.getTicket());
				System.out.println("-------------initMchKeyStore begin-----------------");
				LocalHttpClient.initMchKeyStore(OtherUtils.givePropsValue("mchid"),OtherUtils.givePropsValue("wxRefundKeyPath"));
				System.out.println("-------------initMchKeyStore end-----------------");
//				小程序的access_token begin
//				tokenMap.put("mp1",Tokens.getToken8WxMp("mp1"));
//				System.out.println(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"mp1:"+tokenMap.get("mp1"));
//				小程序的access_token end
			}
		},0,118,TimeUnit.MINUTES);
		futureMap.put(appid, scheduledFuture);
	}

	/**
	 * 取消 token 刷新
	 */
	public static void destroyed(){
		scheduledExecutorService.shutdownNow();
	}

	/**
	 * 获取 access_token
	 * @param appid
	 * @return
	 */
	public static String getToken(String appid){
		return tokenMap.get(appid);
	}
	
	
	public static String getTicket(String appid)
	{
		return ticketMap.get(appid);
	}

	/**
	 * 获取第一个appid 的 access_token
	 * 适用于单一微信号
	 * @return
	 */
	public static String getDefaultToken(){
		Object[] objs = tokenMap.values().toArray();
		return objs.length>0?objs[0].toString():null;
	}

}
