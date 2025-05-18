package com.weavict.light.module

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.light.entity.*
import com.weavict.light.redis.RedisUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import weixin.popular.api.TicketAPI
import weixin.popular.api.TokenAPI
import weixin.popular.bean.ticket.Ticket
import weixin.popular.bean.token.Token
import weixin.popular.client.LocalHttpClient
import jakarta.inject.Inject;

//import com.weavict.website.common.OtherUtils
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig
//import redis.clients.jedis.Jedis
//import redis.clients.jedis.JedisPool

/**
 * Created by Justin on 2018/6/10.
 */
@Service("redisService")
class RedisApi
{
    @Inject
    RedisUtil redisUtil;

    @Inject
    UserBean userBean;

    void buildRedisBuyer(ObjectMapper objectMapper,String buyerId,String field)
    {
        Buyer buyer = userBean.findObjectById(Buyer.class,buyerId);
        if (buyer==null)
        {
            return;
        }
        buyer.cancelLazyEr();
        switch (field)
        {
            case "bean":
//        String jsonStr = redisUtil.hGet("buyer_${buyerId}","bean");
//        buyer = objectMapper.readValue(jsonStr,Buyer.class);
                redisUtil.hPut("buyer_${buyerId}","bean",objectMapper.writeValueAsString(
                        ({
                            return buyer;
                        }).call()
                ));
                break;
            case "buyerAppInfo":
                userBean.queryObject("select ba from BuyerAppInfo ba where ba.buyerAppInfoPK.buyerId = :buyerId",["buyerId":buyerId])?.each {ba->
                    ba.cancelLazyEr();
                    redisUtil.hPut("buyer_${buyer.phone}","appInfo_${ba.buyerAppInfoPK.appId}",objectMapper.writeValueAsString(ba));
                }
                break;
            case "buyerOrgration":

                break;
        }
    }

    void buildToken2Redis()
    {
        userBean.queryObject("select pw from PayWayInfoEntity as pw")?.each {pw->
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","appId",pw.payWayInfoEntityPK.appId ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","appName",pw.appName ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","doMain",pw.doMain ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","mchId",pw.mchId ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","notiUrl",pw.notiUrl ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","partnerKey",pw.partnerKey ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","appSecret",pw.appSecret ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","certPath",pw.certPath ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","isServer",pw.isServer as String);
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","subAppId",pw.subAppId ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","subMchId",pw.subMchId ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","body",pw.body ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","returnDoMain",pw.returnDoMain ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","type",pw.payWayInfoEntityPK.type as String);
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","createDate",DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));

            if (!(pw.certPath in [null,""]) && pw.payWayInfoEntityPK.type == (1 as byte))
            {
                println "-------------initMchKeyStore ${pw.payWayInfoEntityPK.appId} begin-----------------";
                LocalHttpClient.initMchKeyStore(pw.mchId,pw.certPath);
                println "-------------initMchKeyStore ${pw.payWayInfoEntityPK.appId} end-----------------";
            }

            switch (pw.payWayInfoEntityPK.type as byte)
            {
                case 0 as byte:
                    try
                    {
                        Token token = TokenAPI.token(pw.payWayInfoEntityPK.appId,pw.appSecret);
                        redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","accessToken",token?.getAccess_token());
//                    Ticket t = TicketAPI.ticketGetticket(TokenManager.getToken(pw.payWayInfoEntityPK.appId));
                        Ticket t = TicketAPI.ticketGetticket(token.getAccess_token());
                        redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","jsTicket",t?.getTicket());
                    }
                    catch (e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try
                    {
                        Token token = TokenAPI.token(pw.payWayInfoEntityPK.appId,pw.appSecret);
                        redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","accessToken",token.getAccess_token());
                    }
                    catch (e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    String ganTokenValue(String appId,byte type,String field)
    {
        return redisUtil.hGet("appToken_${appId}_${type}",field) as String;
    }


    @Transactional
    void test()
    {
        redisUtil.lLeftPush("test","ttttt");
        Buyer buyer = orderBean.findObjectById(Buyer.class,"13268990066");
        buyer.loginName = "justin";
        println 100 / 0;
    }

}
