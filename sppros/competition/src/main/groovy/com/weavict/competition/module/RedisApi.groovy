package com.weavict.competition.module


import com.alibaba.fastjson2.JSON
import com.fasterxml.jackson.databind.ObjectMapper
import cn.hutool.core.date.DateUtil
import com.weavict.common.aliyun.AliyunStsFactory
import com.weavict.competition.entity.*
import com.weavict.competition.redis.RedisUtil
import com.weavict.common.OtherUtils
import org.camunda.feel.syntaxtree.In
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.inject.Inject;

//import com.weavict.common.OtherUtils
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig
//import redis.clients.jedis.Jedis
//import redis.clients.jedis.JedisPool

/**
 * Created by Justin on 2018/6/10.
 */
@Component
class RedisAliYunStsHandler implements AliyunStsFactory.AliYunStsHandler
{
    @Autowired
    RedisUtil redisUtil;

    @Override
    String ganAliYunStsValue(String appId,String field)
    {
        return redisUtil.hGet("${appId}_aliyun_sts",field) as String;
    }
}

@Service("redisService")
class RedisApi
{
    @Inject
    RedisUtil redisUtil;

    @Inject
    UserBean userBean;

    @Inject
    AliyunStsFactory aliyunStsFactory;

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
            if (pw.mapJson.aliyun)
            {
                aliyunStsFactory.createAndCacheService(pw,pw.payWayInfoEntityPK.appId);
            }
        }
    }

    //Map map是数据库的mapJson设置的值
    void buildAliYunSts2Redis(Map map)
    {
        Map stsMap = aliyunStsFactory.genOssAccessKey(map);
//        println stsMap;
        map.each{k,v->
            redisUtil.hPut("${map.appId}_aliyun_sts",k as String,v as String);
        };

        redisUtil.hPut("${map.appId}_aliyun_sts","expiration",stsMap["expiration"]);
        redisUtil.hPut("${map.appId}_aliyun_sts","accessId",stsMap["accessId"]);
        redisUtil.hPut("${map.appId}_aliyun_sts","accessKey",stsMap["accessKey"]);
        redisUtil.hPut("${map.appId}_aliyun_sts","securityToken",stsMap["securityToken"]);
        redisUtil.hPut("${map.appId}_aliyun_sts","requestId",stsMap["requestId"]);
        redisUtil.hPut("${map.appId}_aliyun_sts","bucketUrl","https://${map["ali_oss_bucketName"]}.${map["ali_oss_endPoint"]}");
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
