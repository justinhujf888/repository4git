package com.weavict.shop.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.shop.entity.Twiter
import com.weavict.shop.entity.TwiterDetails
import com.weavict.shop.entity.TwiterZan
import com.weavict.shop.module.PageUtil
import com.weavict.shop.module.RedisApi
import com.weavict.shop.module.TwiterBean
import com.weavict.shop.redis.RedisUtil
import jodd.datetime.JDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

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

@Path("/twiter")
class TwiterRest extends BaseRest
{
    @Autowired
    TwiterBean twiterBean;

    @Autowired
    RedisApi redisApi

    @Autowired
    RedisUtil redisUtil;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/shareTwiter")
    @LoginToken
    String shareTwiter(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Twiter twiter = objToBean(query.twiter,Twiter.class,objectMapper);
            twiter = twiterBean.updateTwiter(twiter);
            if (twiter.twiterType == 11 as byte)
            {
                return """{"status":"OK","twiterId":"${twiter.id}","hasLogin":${query.loginState.hasLogin}}""";
            }
            //redis
            if (!redisUtil.hasKey("twiter4OrgZset_${twiter.buyerOrgration.buyerOrgrationPK.orgrationId}"))
            {
                redisApi.buildRedisTwiterList(objectMapper,twiter.buyerOrgration.buyerOrgrationPK.orgrationId);
            }
            Date exDate = (new JDateTime(new Date())).addDay(10).convertToDate();
            double scb = twiter.createDate.time / 1000000000000;
            if (twiter.twiterType==(1 as byte))
            {
                scb = scb * 2;
            }
            redisUtil.zAdd("twiter4OrgZset_${twiter.buyerOrgration.buyerOrgrationPK.orgrationId}",twiter.id,scb);
            redisUtil.hPut("twiter_${twiter.id}","imgs",objectMapper.writeValueAsString(({
                return twiterBean.queryImages8Twiter(twiter.id)?.each {img->
                    img.cancelLazyEr();
                }
            }).call()));
            redisUtil.hPut("twiter_${twiter.id}","twiterDetailCount","0");
            twiter.twiterImagesList = null;
            redisUtil.hPut("twiter_${twiter.id}","bean",objectMapper.writeValueAsString(twiter));
            redisUtil.expireAt("twiter_${twiter.id}",exDate);
            //redis end
            return """{"status":"OK","twiterId":"${twiter.id}","hasLogin":${query.loginState.hasLogin}}""";
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
    @Path("/queryTwiters4Orgration")
    @LoginToken
    String queryTwiters4Orgration(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "hasLogin":query.loginState.hasLogin,
                    "twiterPageUtil":({
                        long start = 0l;
                        long end = -1l;
                        if (!(query.page in [null,""]) && !(query.size in [null,""]))
                        {
                            start = (query.page as long) * (query.size as long);
                            end = start + (query.size as long) - 1l;
                        }
                        //redis
                        if (!redisUtil.hasKey("twiter4OrgZset_${query.orgrationId}"))
                        {
                            redisApi.buildRedisTwiterList(objectMapper,query.orgrationId);
                        }
                        List<Twiter> twiterList = new ArrayList();
                        Date exDate = (new JDateTime(new Date())).addDay(10).convertToDate();
                        redisUtil.zReverseRange("twiter4OrgZset_${query.orgrationId}",start,end).each {zit->
                            if (!redisUtil.hasKey("twiter_${zit}"))
                            {
                                redisUtil.hPut("twiter_${zit}","imgs",objectMapper.writeValueAsString(({
                                    return twiterBean.queryImages8Twiter(zit)?.each {img->
                                        img.cancelLazyEr();
                                        img.twiter = null;
                                    }
                                }).call()));
                                Twiter twt = twiterBean.findObjectById(Twiter.class,zit);
                                twt?.cancelLazyEr();
                                redisUtil.hPut("twiter_${zit}","bean",objectMapper.writeValueAsString(twt));
                                redisUtil.hPut("twiter_${zit}","twiterDetailCount",twt?.twiterDetailCount?.toString());
                                redisUtil.expireAt("twiter_${zit}",exDate);
                            }
//                            避免总是要查询数据库，所以注释掉，依赖redis数据不会被清掉
//                            if (!redisUtil.hasKey("twiterPlList_${zit}"))
//                            {
//                                twiterBean.queryTwiterDetailsList(zit,0,500)?.content?.each{td->
//                                    td.twiterDetailsImagesList = null;
//                                    td.twiter = null;
//                                    td.buyerOrgration?.cancelLazyEr();
//                                    td.distBuyerOrgration?.cancelLazyEr();
//                                    redisUtil.lRightPush("twiterPlList_${zit}",objectMapper.writeValueAsString(td));
//                                    redisUtil.expireAt("twiterPlList_${zit}",exDate);
//                                }
//                            }
                            if (redisUtil.hasKey("twiter_${zit}"))
                            {
                                Twiter twiter = objectMapper.readValue(redisUtil.hGet("twiter_${zit}","bean"),Twiter.class);
                                twiter.twiterImagesList = objectMapper.readValue(redisUtil.hGet("twiter_${zit}","imgs"),List.class);

//                                贴子列表不再显示评论
//                                twiter.twiterDetailsList = new ArrayList();
//                                redisUtil.lRange("twiterPlList_${zit}",0l,-1l).each {td->
//                                    twiter.twiterDetailsList << objectMapper.readValue(td,TwiterDetails.class);
//                                }
                                twiter.twiterDetailCount = redisUtil.hGet("twiter_${zit}","twiterDetailCount") as int;
                                if (redisUtil.hasKey("twiterZanSet_${zit}"))
                                {
                                    twiter.twiterZanList = new ArrayList();
                                    redisUtil.setMembers("twiterZanSet_${zit}").each {z->
                                        TwiterZan twiterZan = new TwiterZan();
                                        twiterZan.buyerId = z;
                                        twiter.twiterZanList << twiterZan;
                                    }
                                }

                                twiterList << twiter;
                            }
                        }
                        PageUtil pageUtil = new PageUtil(query.size as int,query.page as int,redisUtil.zSize("twiter4OrgZset_${query.orgrationId}") as int);
                        pageUtil.content = twiterList;
                        return pageUtil;
                        //redis end
                    }).call()
            ]);
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
    @Path("/queryTwiterDetails4Twiter")
    @LoginToken
    String queryTwiterDetails4Twiter(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "hasLogin":query.loginState.hasLogin,
                    "twiterDetailList":({
                        //为了更新twiter的评论数，临时加的，更新完数据库后要删除这一段
//                        println query.twiterDetailCount;
//                        twiterBean.createNativeQuery("select id from twiter").getResultList().each {tid->
//                            int count = twiterBean.createNativeQuery("select count(*) from twiterdetails where twiter_id = '${tid}'").getSingleResult();
//                            Twiter twiter = twiterBean.findObjectById(Twiter.class,tid);
//                            twiter.twiterDetailCount = count;
//                            twiterBean.updateTheObject(twiter);
//                            redisUtil.hPut("twiter_${tid}","twiterDetailCount","${count}");
//                        }
                        //
                        //redis
                        if (!redisUtil.hasKey("twiterPlList_${query.twiterId}") && query.twiterDetailCount > 0)
                        {
                            twiterBean.queryTwiterDetailsList(query.twiterId,0,500)?.content?.each{td->
                                td.twiterDetailsImagesList = null;
                                td.twiter = null;
                                td.buyerOrgration?.cancelLazyEr();
                                td.distBuyerOrgration?.cancelLazyEr();
                                redisUtil.lRightPush("twiterPlList_${query.twiterId}",objectMapper.writeValueAsString(td));
                            }
                            Date exDate = (new JDateTime(new Date())).addDay(10).convertToDate();
                            redisUtil.expireAt("twiterPlList_${query.twiterId}",exDate);
                        }
                        List twiterDetailsList = new ArrayList();
                        redisUtil.lRange("twiterPlList_${query.twiterId}",0l,-1l).each {td->
                            twiterDetailsList << objectMapper.readValue(td,TwiterDetails.class);
                        }
                        return twiterDetailsList;
                        //redis end
                    }).call()
            ]);
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
    @Path("/queryTwiters4OrgNotice")
    @LoginToken
    String queryTwiters4OrgNotice(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "hasLogin":query.loginState.hasLogin,
                    "twiterList":({
                        //redis
                        long totalSize = redisUtil.zSize("twiter4OrgZset_${query.orgrationId}");
                        List<Twiter> twiterList = new ArrayList();
                        for(def zit in redisUtil.zReverseRange("twiter4OrgZset_${query.orgrationId}",0l,totalSize-1l))
                        {
                            if (redisUtil.hasKey("twiter_${zit}") && twiterList.size()<=(query.rSize as int))
                            {
                                Twiter twiter = objectMapper.readValue(redisUtil.hGet("twiter_${zit}","bean"),Twiter.class);
                                if (twiter.twiterType==1 as byte)
                                {
                                    twiter.twiterImagesList = objectMapper.readValue(redisUtil.hGet("twiter_${zit}","imgs"),List.class);
                                    twiterList << twiter;
                                }
                            }
                        }
                        return twiterList;
                        //redis end
                    }).call()
            ]);
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
    @Path("/shareTwiterDetails")
    @LoginToken
    String shareTwiterDetails(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            TwiterDetails twiterDetails = objToBean(query.twiterDetails,TwiterDetails.class,objectMapper);
            twiterDetails = twiterBean.updateTwiterDetails(twiterDetails);
            //redis
            Date exDate = (new JDateTime(new Date())).addDay(10).convertToDate();
            //把redis中的评论数+1
            if (redisUtil.hasKey("twiter_${twiterDetails.twiter.id}"))
            {
                redisUtil.hPut("twiter_${twiterDetails.twiter.id}","twiterDetailCount","${(redisUtil.hGet("twiter_${twiterDetails.twiter.id}","twiterDetailCount") as int)+1}");
            }
            if (!redisUtil.hasKey("twiterPlList_${twiterDetails.twiter.id}"))
            {
                twiterBean.queryTwiterDetailsList(twiterDetails.twiter.id,0,500)?.content?.each{td->
                    td.twiterDetailsImagesList = null;
                    td.twiter = null;
                    td.buyerOrgration?.cancelLazyEr();
                    td.distBuyerOrgration?.cancelLazyEr();
                    td.buyerOrgration?.description = null;
                    td.distBuyerOrgration?.description = null;
                    //redis
//                    if(redisUtil.hExists("buyer_${td.buyerOrgration.buyerOrgrationPK.buyerId}","obBean_${td.buyerOrgration.buyerOrgrationPK.orgrationId}"))
//                    {
//                        td.buyerOrgration = objectMapper.readValue(redisUtil.hGet("buyer_${td.buyerOrgration.buyerOrgrationPK.buyerId}","obBean_${td.buyerOrgration.buyerOrgrationPK.orgrationId}"));
//                    }
//                    if(redisUtil.hExists("buyer_${td.distBuyerOrgration.buyerOrgrationPK.buyerId}","obBean_${td.distBuyerOrgration.buyerOrgrationPK.orgrationId}"))
//                    {
//                        td.distBuyerOrgration = objectMapper.readValue(redisUtil.hGet("buyer_${td.distBuyerOrgration.buyerOrgrationPK.buyerId}","obBean_${td.distBuyerOrgration.buyerOrgrationPK.orgrationId}"));
//                    }

                    redisUtil.lRightPush("twiterPlList_${twiterDetails.twiter.id}",objectMapper.writeValueAsString(td));
                    redisUtil.expireAt("twiterPlList_${twiterDetails.twiter.id}",exDate);
                    //redis end
                }
            }
            else
            {
                //redis
//                if(redisUtil.hExists("buyer_${twiterDetails.buyerOrgration.buyerOrgrationPK.buyerId}","obBean_${twiterDetails.buyerOrgration.buyerOrgrationPK.orgrationId}"))
//                {
//                    twiterDetails.buyerOrgration = objectMapper.readValue(redisUtil.hGet("buyer_${twiterDetails.buyerOrgration.buyerOrgrationPK.buyerId}","obBean_${twiterDetails.buyerOrgration.buyerOrgrationPK.orgrationId}"));
//                }
//                if(redisUtil.hExists("buyer_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.buyerId}","obBean_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.orgrationId}"))
//                {
//                    twiterDetails.distBuyerOrgration = objectMapper.readValue(redisUtil.hGet("buyer_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.buyerId}","obBean_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.orgrationId}"));
//                }
                //redis end
                redisUtil.lRightPush("twiterPlList_${twiterDetails.twiter.id}",objectMapper.writeValueAsString(twiterDetails));
                redisUtil.expireAt("twiterPlList_${twiterDetails.twiter.id}",exDate);
            }

            if (redisUtil.hasKey("buyer_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.buyerId}"))
            {
                if(true || !twiterDetails.distBuyerOrgration.buyerOrgrationPK.buyerId.equals(twiterDetails.buyerOrgration.buyerOrgrationPK.buyerId))
                {
                    redisUtil.lLeftPush("notice_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.orgrationId}_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.buyerId}","${DateUtil.format(twiterDetails.createDate,"yyyy年MM月dd日HH时mm分")}，${twiterDetails.buyerOrgration.niName}评论了您的贴子。");
                    if (redisUtil.lLen("notice_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.orgrationId}_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.buyerId}")>100L)
                    {
                        redisUtil.lRightPop("notice_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.orgrationId}_${twiterDetails.distBuyerOrgration.buyerOrgrationPK.buyerId}");
                    }
                }
            }
            //redis end
            return """{"status":"OK","hasLogin":${query.loginState.hasLogin}}""";
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
    @Path("/deleteTwiterDetail")
    @LoginToken
    String deleteTwiterDetail(@RequestBody Map<String,Object> query)
    {
        try
        {
            twiterBean.delTwiterDetail(query.twiterDetailId);
            //redis
            ObjectMapper objectMapper = new ObjectMapper();
            redisUtil.lRange("twiterPlList_${query.twiterId}",0l,-1l).eachWithIndex {td,index ->
                TwiterDetails twiterDetails = objectMapper.readValue(td,TwiterDetails.class);
                if (twiterDetails.id==query.twiterDetailId)
                {
                    redisUtil.lRemove("twiterPlList_${query.twiterId}",0 as long,td);
                    redisUtil.hPut("twiter_${query.twiterId}","twiterDetailCount",redisUtil.lLen("twiterPlList_${query.twiterId}") as String);
                }
            }
            //redis end
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "hasLogin":query.loginState.hasLogin,
                    "twiterDetailCount":redisUtil.hGet("twiter_${query.twiterId}","twiterDetailCount")
            ]);
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
    @Path("/deleteTwiter")
    @LoginToken
    String deleteTwiter(@RequestBody Map<String,Object> query)
    {
        try
        {
            twiterBean.delTwite(query.twiterId,query.twiterType as byte);
            //redis
            redisUtil.zRemove("twiter4OrgZset_${query.orgrationId}",query.twiterId);
            redisUtil.delete("twiterZanSet_${query.twiterId}");
            redisUtil.delete("twiter_${query.twiterId}");
            redisUtil.delete("twiterPlList_${query.twiterId}");
            //redis end
            return """{"status":"OK","hasLogin":${query.loginState.hasLogin}}""";
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
    @Path("/dianZanTwiter")
    @LoginToken
    String dianZanTwiter(@RequestBody Map<String,Object> query)
    {
        try
        {
            //redis
            Date exDate = (new JDateTime(new Date())).addDay(10).convertToDate();
            if (!redisUtil.sIsMember("twiterZanSet_${query.twiterId}",query.buyerId))
            {
                redisUtil.sAdd("twiterZanSet_${query.twiterId}",query.buyerId);
            }
            else
            {
                redisUtil.sRemove("twiterZanSet_${query.twiterId}",query.buyerId);
            }
            redisUtil.expireAt("twiterZanSet_${query.twiterId}",exDate);
            //redis end
            return """{"status":"OK","hasLogin":${query.loginState.hasLogin}}""";
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
    @Path("/queryTheTwiter")
    String queryTheTwiter(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "twiter":({
                        //redis begin
                        if (!redisUtil.hasKey("twiter_${query.twiterId}"))
                        {
                            Twiter twt = twiterBean.findObjectById(Twiter.class,query.twiterId);
                            twt?.cancelLazyEr();
                            if (twt==null)
                            {
                                return null;
                            }
                            redisUtil.hPut("twiter_${query.twiterId}","imgs",objectMapper.writeValueAsString(({
                                return twiterBean.queryImages8Twiter(query.twiterId)?.each {img->
                                    img.cancelLazyEr();
                                    img.twiter = null;
                                }
                            }).call()));
                            redisUtil.hPut("twiter_${twt.id}","bean",objectMapper.writeValueAsString(twt));
                            redisUtil.hPut("twiter_${twt.id}","twiterDetailCount",twt.twiterDetailCount.toString());
                            Date exDate = (new JDateTime(new Date())).addDay(10).convertToDate();
                            redisUtil.expireAt("twiter_${twt.id}",exDate);
                        }
                        Twiter twiter = objectMapper.readValue(redisUtil.hGet("twiter_${query.twiterId}","bean"),Twiter.class);
                        twiter.twiterImagesList = objectMapper.readValue(redisUtil.hGet("twiter_${query.twiterId}","imgs"),List.class);
                        twiter.twiterDetailCount = redisUtil.hGet("twiter_${query.twiterId}","twiterDetailCount") as int;
                        if (redisUtil.hasKey("twiterZanSet_${query.twiterId}"))
                        {
                            twiter.twiterZanList = new ArrayList();
                            redisUtil.setMembers("twiterZanSet_${query.twiterId}").each {z->
                                TwiterZan twiterZan = new TwiterZan();
                                twiterZan.buyerId = z;
                                twiter.twiterZanList << twiterZan;
                            }
                        }
                        return twiter;
                        //redis end
                    }).call()
            ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
