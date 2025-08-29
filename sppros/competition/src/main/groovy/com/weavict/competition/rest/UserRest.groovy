package com.weavict.competition.rest

import cn.hutool.core.util.IdUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.competition.entity.Buyer
import com.weavict.competition.entity.BuyerAppInfo
import com.weavict.competition.entity.BuyerAppInfoPK
import com.weavict.competition.entity.Judge
import com.weavict.competition.module.UserBean
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

@Path("/user")
class UserRest extends BaseRest
{
    @Autowired
    UserBean userBean;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/registBuyer")
    String registBuyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            Buyer buyer = objToBean(query.buyer, Buyer.class, objectMapper);
            if (userBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(query.appId,buyer.phone))!=null)
            {
                return """{"status":"ER_HAS"}""";
            }
            buyer.tempMap = [:];
            buyer.tempMap["appId"] = query.appId;
            userBean.registBuyer(buyer);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "buyerInfo":({
                         return buyer;
                     }).call(),
                     "loginToken":({
//                         redis
//                         redisUtil.hPut("buyer_${buyer.phone}","token",MathUtil.getPNewId());
//                         return redisUtil.hGet("buyer_${buyer.phone}","token");
//                         redis end
                         return IdUtil.randomUUID();
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
    @Path("/buyerLogin")
    String buyerLogin(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            BuyerAppInfo buyerAppInfo = userBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(query.appId,query.userId));
            if (buyerAppInfo==null)
            {
                return """{"status":"ER_NOHAS"}""";
            }
            if (!userBean.buildPasswordCode(query.password).equals(buyerAppInfo.password))
            {
                return """{"status":"ER_PW"}""";
            }
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "buyerAppInfo":({
                         return buyerAppInfo;
                     }).call(),
                     "loginToken":({
                     //redis
//                         redisUtil.hPut("buyer_${buyer.phone}","token",MathUtil.getPNewId());
//                         return redisUtil.hGet("buyer_${buyer.phone}","token");
                     //redis end
                         return IdUtil.randomUUID();
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
    @Path("/resetBuyerPassword")
    String resetBuyerPassword(@RequestBody Map<String,Object> query)
    {
        try
        {
            BuyerAppInfo buyerAppInfo = userBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(query.appId,query.userId));
            if (buyerAppInfo==null)
            {
                return """{"status":"ER_NOHAS"}""";
            }
            userBean.ressetBuyerPassword(query.appId,query.userId,query.password);
            return """{"status":"OK"}""";
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
    @Path("/queryJudgeList")
    String queryJudgeList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = this.buildObjectMapper();
            Map map = this.objToBean(query.queryJudge,Map.class,objectMapper);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "judgePageUtil":({
                         userBean.newQueryUtils(false).masterTable("Judge",null,null)
                                .where("name like :name",["name":"%${map.name}%".toString()],null,{return !(map.name in [null,""])})
                                 .where("engName like :engName",["engName":"%${map.engName}%".toString()],"and",{return !(map.engName in [null,""])})
                                 .where("phone like :phone",["phone":"%${map.phone}%".toString()],"and",{return !(map.phone in [null,""])})
                                .orderBy("createDate desc")
                                .pageLimit(query.pageSize as int,query.currentPage as int,"id").buildSql().run();
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
