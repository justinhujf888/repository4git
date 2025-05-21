package com.weavict.light.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.light.entity.Buyer
import com.weavict.light.entity.BuyerAppInfo
import com.weavict.light.entity.BuyerAppInfoPK
import com.weavict.light.module.RedisApi
import com.weavict.light.module.UserBean
import com.weavict.light.redis.RedisUtil
import com.weavict.website.common.OtherUtils
import com.weavict.weichat.Sign
import com.weavict.weichat.StoreProperty
import com.weavict.weichat.notifies.WxNotifiesFun
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import weixin.popular.api.SnsAPI
import weixin.popular.bean.sns.Jscode2sessionResult
import weixin.popular.bean.sns.SnsToken

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

/**
 * Created by Justin on 2018/6/10.
 */

@Path("/wx")
class WxRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    UserBean userBean;

    @Autowired
    RedisApi redisApi

    @Autowired
    RedisUtil redisUtil;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/config")
    String getMessage(@RequestBody Map<String,Object> query)
    {
        def m = [:];
        if (!(query.url in [null,""]))
        {
            m = Sign.sign(redisApi.ganTokenValue(query.appId,0 as byte,"jsTicket"),query.url);
        }
        ObjectMapper objectMapper = buildObjectMapper();
        return objectMapper.writeValueAsString(
            ["status":"OK",
                wx:(
                    {
                        [timestamp:m["timestamp"] as String,
                        nonceStr:m["nonceStr"] as String,
                        signature:m["signature"] as String]
                    }
                ).call(),
                web:(
                    {
                        [prxurl:redisApi.ganTokenValue(query.appId,0 as byte,"notiUrl"),//WxNotifiesFun.appurl,
                        sessid:request.getSession().getId()]
                    }
                ).call()
            ]
        );
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/wxloginsec")
    String getGroupInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            SnsToken sk = SnsAPI.oauth2AccessToken(query.appId, redisApi.ganTokenValue(query.appId,0 as byte,"appSecret"), query.code);
            println """
access_token:${sk.access_token}<br/>
expires_in:${sk.expires_in}<br/>
openid:${sk.openid}<br/>
refresh_token:${sk.refresh_token}<br/>
state:${request.getParameter("state").toString()}<br/><br/>
""";
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                ["status":"OK",
                    "sk":(
                        {
                            return ["access_token":sk.access_token,
                            "expires_in":sk.expires_in,
                            "openid":sk.openid,
                            "refresh_token":sk.refresh_token]
                        }
                    ).call(),
                    "userinfo":(
                        {
                            if (query.userinfo!=null)
                            {
                                if (!StoreProperty.checkTokenVaild(sk.access_token, sk.openid))
                                {
                                    sk = SnsAPI.oauth2RefreshToken(query.appId, sk.refresh_token);
                                    println """
								access_token:${sk.access_token}<br/>
								expires_in:${sk.expires_in}<br/>
								openid:${sk.openid}<br/>
								refresh_token:${sk.refresh_token}<br/>
								""";
                                }
                                return SnsAPI.userinfo(sk.access_token, query.appId, "ZH-CN");
                            }
                        }
                    ).call()
                ]
            );
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/wxLoginSec4WxMp")
    String getGroupInfo4WxMp(@RequestBody Map<String,Object> query)
    {
        try
        {
            println query.code;
            Jscode2sessionResult jr = SnsAPI.jscode2session(query.appId, redisApi.ganTokenValue(query.appId,1 as byte,"appSecret"), query.code);
            println jr.dump();
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "jr":(
                             {
                                 return ["session_key":jr.session_key,
                                         "expires_in":jr.expires_in,
                                         "openid":jr.openid,
                                         "unionid":jr.unionid]
                             }
                     ).call()
                    ]
            );
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @Path("/decodeUserInfo4WxMp")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    String decodeUserInfo4WxMp(@RequestBody Map<String,Object> query)
    {
        try
        {
            println query.encryptedData;
            println query.sessionKey;
            println query.iv;
            ObjectMapper objectMapper = buildObjectMapper();
            def jsonSlpuer = new JsonSlurper();
            def obj = jsonSlpuer.parseText(OtherUtils.decodeUserInfo4WxMp(query.encryptedData,query.sessionKey,query.iv));
            Buyer b = objToBean(query.buyer,Buyer.class,objectMapper);
            b.phone = obj.purePhoneNumber;
            Buyer buyer8Phone = userBean.findObjectById(Buyer.class,b.phone);
//            println b.dump();
//            println buyer8Phone.dump();
//            return;

            if (buyer8Phone!=null)
            {
                BuyerAppInfo buyerAppInfo = userBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(query.appId,obj.purePhoneNumber));
                if (buyerAppInfo!=null && (!(buyerAppInfo.wxid in [null,""]) && buyerAppInfo.wxid!=b.wxid ))
                {
                    return """{"status":"FA_HASPHONE"}""";
                }
                else
                {
                    if (buyerAppInfo==null)
                    {
                        buyerAppInfo = new BuyerAppInfo();
                        buyerAppInfo.buyerAppInfoPK = new BuyerAppInfoPK(query.appId,buyer8Phone.phone);
                        buyerAppInfo.password = userBean.buildPasswordCode(buyer8Phone.phone[buyer8Phone.phone.length()-6..buyer8Phone.phone.length()-1]);
                    }
                    else if (buyerAppInfo.password in [null,""])
                    {
                        buyerAppInfo.password = userBean.buildPasswordCode(buyer8Phone.phone[buyer8Phone.phone.length()-6..buyer8Phone.phone.length()-1]);
                    }
                    buyer8Phone.wxopenid = b.wxopenid;
                    buyer8Phone.wxid = b.wxid;
                    //wxopenid,wxid保存在buyerAppInfo，为了适应多个APP
                    buyerAppInfo.wxopenid = b.wxopenid;
                    buyerAppInfo.wxid = b.wxid;
                    userBean.updateTheObject(buyerAppInfo);
                    if (!redisUtil.hExists("buyer_${obj.purePhoneNumber}","bean"))
                    {
                        redisApi.buildRedisBuyer(objectMapper,obj.purePhoneNumber,"bean");
                    }
                    redisApi.buildRedisBuyer(objectMapper,obj.purePhoneNumber,"buyerAppInfo");
                    return objectMapper.writeValueAsString(
                            ["status":"OK",
                             "buyer":({
                                 buyer8Phone.cancelLazyEr();
                                 return buyer8Phone;
                             }).call(),
                             "decodeInfo":(
                                     {
                                         return ["phoneNumber":obj.phoneNumber,"purePhoneNumber":obj.purePhoneNumber,"countryCode":obj.countryCode];
                                     }
                             ).call()
                            ]
                    );
                }
            }
            else
            {
                b.createDate = new Date();
                b.password = userBean.buildPasswordCode(b.phone[b.phone.length()-6..b.phone.length()-1]);
                BuyerAppInfo buyerAppInfo = new BuyerAppInfo();
                buyerAppInfo.buyerAppInfoPK = new BuyerAppInfoPK(query.appId,b.phone);
                buyerAppInfo.wxopenid = b.wxopenid;
                buyerAppInfo.wxid = b.wxid;
                buyerAppInfo.password = b.password;
                userBean.updateTheObject(b);
                userBean.updateTheObject(buyerAppInfo);
                if (!redisUtil.hExists("buyer_${obj.purePhoneNumber}","bean"))
                {
                    redisApi.buildRedisBuyer(objectMapper,obj.purePhoneNumber,"bean");
                }
                redisApi.buildRedisBuyer(objectMapper,obj.purePhoneNumber,"buyerAppInfo");
                return objectMapper.writeValueAsString(
                        ["status":"OK",
                         "buyer":({
                             if (query.buyer)
                             {
                                 Buyer buyer = userBean.queryTheBuyer8WxId(b.wxid);
                                 if (buyer==null)
                                 {
                                     b.phone = obj.purePhoneNumber;
                                     b.createDate = new Date();
                                     userBean.addTheObject(b);
                                     return b;
                                 }
                                 buyer.cancelLazyEr();
                                 return buyer;
                             }
                             else
                             {
                                 return null;
                             }
                         }).call(),
                         "decodeInfo":(
                                 {
                                     return ["phoneNumber":obj.phoneNumber,"purePhoneNumber":obj.purePhoneNumber,"countryCode":obj.countryCode];
                                 }
                         ).call()
                        ]
                );
            }
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/sendSubscribeMsg4WxMp")
    String sendSubscribeMsg4WxMp(@RequestBody Map<String,Object> query)
    {
        try
        {
            WxNotifiesFun.send_getProductFromShop_Message(["userOpenId":query.userOpenId,"mxPage":"index","shopName":"中信东泰花园荣华园菜鸟驿站",
            "productName":"威尔吸尘器","phone_number3":"13268990066","shopAddress":"中信东泰花园荣华园155号"]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
