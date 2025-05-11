package com.weavict.carshopapp.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.carshopapp.entity.User
import com.weavict.carshopapp.module.UserService
import com.weavict.website.common.OtherUtils
import com.weavict.weichat.Sign
import com.weavict.weichat.StoreProperty
import com.weavict.weichat.Tokens
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
    UserService userService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/config")
    String getMessage(@RequestBody Map<String,Object> query)
    {
        def m = Sign.sign(Tokens.getTheJSTicket(query.doman),query.url);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(
            ["status":"OK",
                wx:(
                    {
                        [appid:OtherUtils.givePropsValue("appid") as String,
                        appname:OtherUtils.givePropsValue("appname") as String,
                        timestamp:m["timestamp"] as String,
                        nonceStr:m["nonceStr"] as String,
                        signature:m["signature"] as String]
                    }
                ).call(),
                web:(
                    {
                        [prxurl:WxNotifiesFun.appurl,
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
            SnsToken sk = SnsAPI.oauth2AccessToken(Tokens.getTheAppid(request.getServerName()), Tokens.getTheSecret(request.getServerName()), query.code);
            println """
access_token:${sk.access_token}<br/>
expires_in:${sk.expires_in}<br/>
openid:${sk.openid}<br/>
refresh_token:${sk.refresh_token}<br/>
state:${request.getParameter("state").toString()}<br/><br/>
""";
            ObjectMapper objectMapper = new ObjectMapper();
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
                                    sk = SnsAPI.oauth2RefreshToken(Tokens.getTheAppid(request.getServerName()), sk.refresh_token);
                                    println """
								access_token:${sk.access_token}<br/>
								expires_in:${sk.expires_in}<br/>
								openid:${sk.openid}<br/>
								refresh_token:${sk.refresh_token}<br/>
								""";
                                }
                                return SnsAPI.userinfo(sk.access_token, Tokens.getTheAppid(request.getServerName()), "ZH-CN");
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
            def jsonSlpuer = new JsonSlurper()
            def obj = jsonSlpuer.parseText(OtherUtils.givePropsValue(query.mp));
            println obj.dump();
            Jscode2sessionResult jr = SnsAPI.jscode2session(obj.appId, obj.key, query.code);
            println jr.dump();
            ObjectMapper objectMapper = new ObjectMapper();
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
            ObjectMapper objectMapper = new ObjectMapper();
            def jsonSlpuer = new JsonSlurper();
            def obj = jsonSlpuer.parseText(OtherUtils.decodeUserInfo4WxMp(query.encryptedData,query.sessionKey,query.iv));
            User u = objToBean(query.user, User.class,objectMapper);
            u.phone = obj.purePhoneNumber;
            User user8Phone = userService.findObjectById(User.class,obj.purePhoneNumber);
//            println b.dump();
//            println buyer8Phone.dump();
//            return;

            if (user8Phone!=null && user8Phone.wxid!=u.wxid && !(user8Phone.wxid in [null,""]))
            {
                return """{"status":"FA_HASPHONE"}""";
            }
            else if (user8Phone!=null && user8Phone.wxid==u.wxid)
            {
                return objectMapper.writeValueAsString(
                        ["status":"OK",
                         "user":({
                             user8Phone.cancelLazyEr();
                             return user8Phone;
                         }).call(),
                         "decodeInfo":(
                                 {
                                     return ["phoneNumber":obj.phoneNumber,"purePhoneNumber":obj.purePhoneNumber,"countryCode":obj.countryCode];
                                 }
                         ).call()
                        ]
                );
            }
            else
            {
                u.createDate = new Date();
                userService.updateTheObject(u);
                return objectMapper.writeValueAsString(
                        ["status":"OK",
                         "user":({
                             if (query.user)
                             {
                                 User user = userService.queryTheUser8WxId(u.wxid);
                                 if (user==null)
                                 {
                                     u.phone = obj.purePhoneNumber;
                                     u.createDate = new Date();
                                     userService.addTheObject(u);
                                     return u;
                                 }
                                 user.cancelLazyEr();
                                 return user;
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
            WxNotifiesFun.send_baoYang_Message(["userOpenId":query.userOpenId,"mxPage":"index","carNumber":"粤SY287J",
            "baoyangDesc":"您的爱车快到保养时间了","carType":"福特 福克斯12","baoyangDate": DateUtil.format(new Date(),"yyyy-MM-dd"),"remark":"小汽修温馨提醒"]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
