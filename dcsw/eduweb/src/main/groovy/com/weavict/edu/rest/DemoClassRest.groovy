package com.weavict.edu.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.MathUtil
import com.weavict.edu.module.DemoClassService
import com.weavict.edu.module.RedisApi
import com.weavict.website.common.OtherUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import weixin.popular.api.PayMchAPI
import weixin.popular.bean.paymch.Unifiedorder
import weixin.popular.bean.paymch.UnifiedorderResult
import weixin.popular.util.PayUtil

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

@Path("/demoClass")
class DemoClassRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    DemoClassService demoClassService;

    @Autowired
    RedisApi redisApi;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryDemoClassSchool")
    String queryDemoClassSchool(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "schoolList": ({
                         return demoClassService.queryDemoClassSchools().each {school->
                             school?.cancelLazyEr();
                         }
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
    @Path("/queryDemoClass")
    String queryDemoClass(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "demoClassList": ({
                         Map kes = [:];
                         demoClassService.queryDemoClassSchools().each {school->
                             school.cancelLazyEr();
                             school.description = null;
                             school.area = null;
                             school.address = null;
                             kes[school.id] = school;
                             school?.demoClassList = new ArrayList();
                             demoClassService.queryDemoClass8TheSchool(school.id).each {dc->
                                 dc?.cancelLazyEr();
                                 dc?.demoClassSchool = null;
                                 school.demoClassList << dc;
                             }
                         }
                         return kes;
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
    @Path("/queryRunningDemoClasses8Buyer")
    String queryRunningDemoClasses8Buyer(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "demoClassList8Buyer": ({
                         List keList = new ArrayList();
                         demoClassService.queryRunningDemoClasses8Buyer(query.buyerId).each {ke->
                             keList << ["buyerId":ke[0],"buyerName":ke[1],"wxNickName":ke[2],
                                "schoolId":ke[3],"schoolName":ke[4],
                                "domeClassId":ke[5],"demoClassName":ke[6],
                                "createDate":ke[7],"tradeNo":ke[8],"dailiNo":ke[9],
                                "payStatus":ke[10],"price":ke[11],
                                "schoolArea":ke[12],"schoolAddress":ke[13],"payId":ke[14]
                             ];
                         }
                         return keList;
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
    @Path("/addDemoClassPersonKes")
    String addDemoClassPersonKes(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
//            List keList = objToBean(query.keList, List.class, objectMapper);
//            DemoClassMaster demoClassMaster = demoClassService.queryTheRunningDemoClassMaster();
//            demoClassMaster.cancelLazyEr();
            String orderNo = MathUtil.getPNewId();
//            demoClassService.addDemoClasses4TheParent(query.buyerId,query.dailiNo,(query.fee as int),keList,orderNo,demoClassMaster);
            Unifiedorder unifiedorder = new Unifiedorder();
            unifiedorder.appid = OtherUtils.givePropsValue("appid");
            unifiedorder.mch_id = OtherUtils.givePropsValue("mchid");
            unifiedorder.nonce_str = UUID.randomUUID().toString().toString().replace("-", "");
            unifiedorder.openid = query.buyerId;
            unifiedorder.body = demoClassMaster.description;
            unifiedorder.out_trade_no = orderNo;
            unifiedorder.attach = """{"payReturnOrderId":"${orderNo}","orderType":"9","dailiNo":"${query.dailiNo}"}""";
            unifiedorder.total_fee = query.fee;//单位分
            unifiedorder.spbill_create_ip = request.getRemoteAddr();//IP
            unifiedorder.notify_url = """${OtherUtils.givePropsValue("prxurl")}${OtherUtils.givePropsValue("prxconname")}/r/pay/wxPay4WxNotify""";
            unifiedorder.trade_type = "JSAPI";//JSAPI，NATIVE，APP，WAP
            //统一下单，生成预支付订单
            UnifiedorderResult unifiedorderResult = PayMchAPI.payUnifiedorder(unifiedorder,OtherUtils.givePropsValue("mchkey"));
            if(unifiedorderResult.getSign_status() !=null && unifiedorderResult.getSign_status())
            {
                return objectMapper.writeValueAsString(
                        ["status": "OK",
                         "payJson":({
                             return PayUtil.generateMchPayJsRequestJson(unifiedorderResult.getPrepay_id(), unifiedorder.appid,OtherUtils.givePropsValue("mchkey"));
                         }).call()
                        ]);
            }
            else
            {
                return """{"status":"FA_Unifiedorder"}""";
            }
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
