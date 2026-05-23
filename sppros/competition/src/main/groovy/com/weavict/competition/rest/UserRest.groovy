package com.weavict.competition.rest

import cn.hutool.core.util.IdUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.competition.entity.Buyer
import com.weavict.competition.entity.BuyerAppInfo
import com.weavict.competition.entity.BuyerAppInfoPK
import com.weavict.competition.entity.Judge
import com.weavict.competition.entity.Manager
import com.weavict.competition.entity.ManagerPK
import com.weavict.competition.entity.ManagerRule
import com.weavict.competition.entity.ManagerRulePK
import com.weavict.competition.entity.MasterCompetition
import com.weavict.competition.entity.Rule
import com.weavict.competition.entity.RulePermission
import com.weavict.competition.entity.RulePermissionPK
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
                         buyerAppInfo.password = "";
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
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         return userBean.queryJudgeList(query);
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
    @Path("/updateJudge")
    String updateJudge(@RequestBody Map<String,Object> query)
    {
        try
        {
            Judge judge = this.objToBean(query.judge, Judge.class,null);
//            println judge.dump();
            if (judge.temp==true)
            {
                judge.password = userBean.buildPasswordCode(judge.password);
            }
            userBean.updateTheObject(judge);
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
    @Path("/managerLogin")
    String managerLogin(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            Manager manager = userBean.findObjectById(Manager.class,new ManagerPK(query.appId,query.managerId));
            if (manager==null)
            {
                return """{"status":"ER_NOHAS"}""";
            }
            if (!userBean.buildPasswordCode(query.password).equals(manager.password))
            {
                return """{"status":"ER_PW"}""";
            }
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "manager":({
                         manager.password = "";
                         return manager;
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
    @Path("/judgeLogin")
    String judgeLogin(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            Judge judge = userBean.queryJudge8PhonePw(query);
            if (judge==null)
            {
                return """{"status":"ER_NOHAS"}""";
            }
            if (!userBean.buildPasswordCode(query.password).equals(judge.password))
            {
                return """{"status":"ER_PW"}""";
            }
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "judge":({
                         judge.password = "";
                         return judge;
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
    @Path("/queryRuleList")
    String queryRuleList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = this.buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         return userBean.queryRuleList(query);
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
    @Path("/updateRule")
    String updateRule(@RequestBody Map<String,Object> query)
    {
        try
        {
            Rule rule = this.objToBean(query.rule, Rule.class,null);
//            println rule.dump();
            userBean.transactionCall(-1,{
                userBean.updateObject(rule);
                userBean.updateTheObjectFilds(Rule.simpleName,"rulePK.appId = :appId and rulePK.ruleId = :ruleId",[name:rule.name],[appId:query.appId,ruleId:rule.rulePK.ruleId],false);
                userBean.deleteTheObject8Fields(RulePermission.simpleName,"rulePermissionPK.appId = :appId and rulePermissionPK.ruleId = :ruleId",[appId:query.appId,ruleId:rule.rulePK.ruleId],false);
                rule.ruleJson.each {k,v ->
                    if (v.pmenu==null || !(v.pmenu as boolean))
                    {
                        RulePermission rulePermission = new RulePermission();
                        rulePermission.rulePermissionPK = new RulePermissionPK(query.appId as String,rule.rulePK.ruleId,k as String);
                        rulePermission.permissionName = v.label;
                        userBean.updateObject(rulePermission);
                    }
                }
            });

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
    @Path("/deleteRule")
    String deleteRule(@RequestBody Map<String,Object> query)
    {
        try
        {
            userBean.transactionCall(-1,{
                userBean.deleteTheObject8Fields(RulePermission.simpleName,"rulePermissionPK.appId = :appId and rulePermissionPK.ruleId = :ruleId",[appId:query.appId,ruleId:query.ruleId],false);
                userBean.deleteTheObject8Fields(Rule.simpleName,"rulePK.appId = :appId and rulePK.ruleId = :ruleId",[appId:query.appId,ruleId:query.ruleId],false);
            });
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
    @Path("/queryManagerList")
    String queryManagerList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = this.buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         return userBean.queryManagerList(query);
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
    @Path("/updateManager")
    String updateManager(@RequestBody Map<String,Object> query)
    {
        try
        {
            Manager manager = this.objToBean(query.manager, Manager.class,null);
            if (manager.temp==true)
            {
                manager.password = userBean.buildPasswordCode(manager.password);
            }
            userBean.transactionCall(-1,{
                userBean.updateObject(manager);
                userBean.deleteTheObject8Fields("managerrule","appid=:appId and managerid=:managerId",[appId:manager.managerPK.appId,managerId: manager.managerPK.managerId],true);
                if (manager.tempMap?.selRules!=null && manager.tempMap?.selRules?.size()>0)
                {
                    for(r in manager.tempMap?.selRules)
                    {
                        ManagerRule managerRule = new ManagerRule();
                        managerRule.managerRulePK = new ManagerRulePK(query.appId as String,manager.managerPK.managerId,r.ruleId);
                        userBean.updateObject(managerRule);
                    }
                }
            });
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
    @Path("/deleteManager")
    String deleteManager(@RequestBody Map<String,Object> query)
    {
        try
        {
            userBean.deleteTheObject8Fields(Manager.simpleName,"managerPK.appId = :appId and managerPK.managerId = :managerId",[appId:query.appId,managerId:query.managerId],false);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
