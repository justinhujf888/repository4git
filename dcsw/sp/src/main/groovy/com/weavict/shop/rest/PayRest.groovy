package com.weavict.shop.rest

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.bean.DynaBean
import cn.hutool.core.util.RandomUtil
import com.aliyun.oss.OSSClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.shop.entity.PayReturnOrder
import com.weavict.shop.module.OrderBean
import com.weavict.shop.module.RedisApi
import com.weavict.shop.module.ScheduledBean
import com.weavict.shop.module.TwiterBean
import com.weavict.shop.redis.RedisUtil
import com.weavict.website.common.OtherUtils
import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import weixin.popular.api.PayMchAPI
import weixin.popular.bean.paymch.*
import weixin.popular.util.MapUtil
import weixin.popular.util.PayUtil
import weixin.popular.util.SignatureUtil
import weixin.popular.util.StreamUtils
import weixin.popular.util.XMLConverUtil

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import java.nio.charset.Charset

/**
 * Created by Justin on 2018/6/10.
 */

@Path("/pay")
class PayRest extends BaseRest
{
    @Autowired
    RedisApi redisApi

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OrderBean orderBean;

    @Autowired
    TwiterBean twiterBean;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/wxPay4AppUnifiedOrder")
    String wxPay4AppUnifiedOrder(@RequestBody Map<String,Object> query)
    {
        try
        {
            PayReturnOrder payReturnOrder = orderBean.findObjectById(PayReturnOrder.class,query.payReturnOrderId);
            if (payReturnOrder==null)
            {
                return """{"status":"FA_NOORDER"}""";
            }
            if (payReturnOrder.paymentStatus==(1 as byte))
            {
                return """{"status":"FA_PAYED"}""";
            }

            OSSClient ossClient = OtherUtils.genOSSClient();
            ObjectMapper objectMapper = new ObjectMapper();

            Unifiedorder unifiedorder = new Unifiedorder();
            unifiedorder.appid = query.appId;
            unifiedorder.mch_id = redisApi.ganTokenValue(query.appId,1 as byte,"mchId");
            unifiedorder.nonce_str = UUID.randomUUID().toString().toString().replace("-", "");

            if (query.openId != null)
            {
                unifiedorder.openid = query.openId;
            }

            if (!(payReturnOrder.productId in [null,""]) && ossClient.getObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"system/json/product/${payReturnOrder.productId}.json")!=null)
            {
                def jsonSlpuer = new JsonSlurper();
                def obj = jsonSlpuer.parseText(IOUtils.toString(ossClient.getObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"system/json/product/${payReturnOrder.productId}.json").objectContent,"utf-8"));
                unifiedorder.body = obj.productInfo.productName;
            }
            else
            {
                def jsonSlpuer = new JsonSlurper();
                def obj = jsonSlpuer.parseText(payReturnOrder.description);
                unifiedorder.body = "${obj.privaterName}到店消费";
            }
            unifiedorder.out_trade_no = "${payReturnOrder.id}_${RandomUtil.randomString(5)}";
            unifiedorder.attach = """{"appId":"${query.appId}",{"payReturnOrderId":"${payReturnOrder.id}","orderType":"${payReturnOrder.orderType}"}""";
            unifiedorder.total_fee = payReturnOrder.paymentFee as String;//单位分
            unifiedorder.spbill_create_ip = request.getRemoteAddr();//IP
            unifiedorder.notify_url = redisApi.ganTokenValue(query.appId,1 as byte,"doMain");//"""${OtherUtils.givePropsValue("prxurl")}${OtherUtils.givePropsValue("prxconname")}/r/pay/wxPay4AppNotify""";
            if (query.tradeType in [null,""])
            {
                unifiedorder.trade_type = "APP";//JSAPI，NATIVE，APP，WAP
            }
            else
            {
                unifiedorder.trade_type = query.tradeType;
            }

            //统一下单，生成预支付订单
//            ["appid":unifiedorder.appid,"mch_id":unifiedorder.mch_id,"nonce_str":unifiedorder.nonce_str,"sign":unifiedorder.sign,"body":unifiedorder.body,"out_trade_no":unifiedorder.out_trade_no,"total_fee":unifiedorder.total_fee,"spbill_create_ip":unifiedorder.spbill_create_ip,"notify_url":unifiedorder.notify_url,"trade_type":unifiedorder.trade_type,"openid":unifiedorder.openid]

            UnifiedorderResult unifiedorderResult = PayMchAPI.payUnifiedorder(unifiedorder,redisApi.ganTokenValue(query.appId,1 as byte,"partnerKey"));

//            Map<String,String> params = UnifiedOrderModel.builder()
//                    .appid(OtherUtils.givePropsValue("payappid"))
//                    .mch_id(OtherUtils.givePropsValue("mchid"))
//                    .nonce_str(UUID.randomUUID().toString().toString().replace("-", ""))
//                    .openid((query.openId != null) ? query.openId : null)
//                    .body(({
//                        if (!(payReturnOrder.productId in [null,""]) && ossClient.getObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"system/json/product/${payReturnOrder.productId}.json")!=null)
//                        {
//                            def jsonSlpuer = new JsonSlurper();
//                            def obj = jsonSlpuer.parseText(IOUtils.toString(ossClient.getObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"system/json/product/${payReturnOrder.productId}.json").objectContent,"utf-8"));
//                            return obj.productInfo.productName;
//                        }
//                        else
//                        {
//                            def jsonSlpuer = new JsonSlurper();
//                            def obj = jsonSlpuer.parseText(payReturnOrder.description);
//                            return "${obj.privaterName}到店消费";
//                        }
//                    }).call())
//                    .attach("""{"payReturnOrderId":"${payReturnOrder.id}","orderType":"${payReturnOrder.orderType}"}""")
//                    .out_trade_no("${payReturnOrder.id}_${RandomUtil.randomString(5)}")
//                    .total_fee(payReturnOrder.paymentFee as String)
//                    .spbill_create_ip(request.getRemoteAddr())
//                    .notify_url("""${OtherUtils.givePropsValue("prxurl")}${OtherUtils.givePropsValue("prxconname")}/r/pay/wxPay4AppNotify""")
//                    .trade_type((query.tradeType in [null,""]) ? "APP" : query.tradeType)
//                    .build().createSign(OtherUtils.givePropsValue("mchapikey"), SignType.MD5);
//            println params.dump();
//            println WxPayApi.pushOrder(false,params);

            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "unifiedOrderResult":({
                         if (unifiedorder.trade_type=="APP")
                         {
                             //@since 2.8.5  API返回数据签名验证
                             MchPayApp mchPayApp = null;
                             if(unifiedorderResult.getSign_status() !=null && unifiedorderResult.getSign_status())
                             {
                                 mchPayApp = PayUtil.generateMchAppData(unifiedorderResult.getPrepay_id(), unifiedorder.appid,redisApi.ganTokenValue(query.appId,1 as byte,"mchId"),redisApi.ganTokenValue(query.appId,1 as byte,"partnerKey"));
                             }
                             return mchPayApp;
                         }
                         else if (unifiedorder.trade_type=="JSAPI")
                         {
                             return PayUtil.generateMchPayJsRequestJson(unifiedorderResult.prepay_id,unifiedorderResult.appid,redisApi.ganTokenValue(query.appId,1 as byte,"partnerKey"));
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

    //用于测试 begin
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
    //用于测试 end

//    正式，非测试环境 begin
    @POST
//    正式，非测试环境 end

//    查到的另外一种格式声明，不用于下面的支付
//    @Consumes("text/plain")//application/xml
//    @Produces(MediaType.TEXT_PLAIN)//text/plain
    @Path("/wxPay4AppNotify")
    String wxPay4AppNotify()
    {
        try
        {
            // 解析微信支付异步通知请求参数;
            String xml = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));

//            String xml = """
//<xml>
//    <appid>wx541c7422110ff0ed</appid>
//    <attach><![CDATA[{"appId":"wx541c7422110ff0ed",{"payReturnOrderId":"202303281109286805490","orderType":"1"}]]></attach>
//    <bank_type><![CDATA[BOC_DEBIT]]></bank_type>
//    <cash_fee>200</cash_fee>
//    <fee_type>CNY</fee_type>
//    <is_subscribe><![CDATA[N]]></is_subscribe>
//    <mch_id>1561173361</mch_id>
//    <nonce_str>787faa6b73de486fbd95dae08cc9ad74</nonce_str>
//    <openid>oz6P8v3gBTZ-LGLdi4QPq16WjbWE</openid>
//    <out_trade_no>202303281109286805490_mts22</out_trade_no>
//    <result_code>SUCCESS</result_code>
//    <return_code>SUCCESS</return_code>
//    <sign>052D78C5373D16E71297E70505038A45</sign>
//    <time_end>20221108221957</time_end>
//    <total_fee>200</total_fee>
//    <trade_type>APP</trade_type>
//    <transaction_id>4200001649202211089415818594</transaction_id>
//</xml>
//""";
            println xml;
            Map<String, String> params = XMLConverUtil.convertToMap(xml);
            MchPayNotify payNotify = XMLConverUtil.convertToObject(MchPayNotify.class, xml);
//            def jsonSlpuer = new JsonSlurper();
//            def obj = jsonSlpuer.parseText(payNotify.attach);

//            if(true)//用于测试
            if("SUCCESS".equals(payNotify.getReturn_code()))
            {
//                if (true)//用于测试
                if(SignatureUtil.validateSign(params,redisApi.ganTokenValue(payNotify.appid,1 as byte,"partnerKey")) && "SUCCESS".equals(payNotify.getResult_code()))
                {
                    /** 处理业务 */
                    PayReturnOrder payReturnOrder = orderBean.findObjectById(PayReturnOrder.class,payNotify.out_trade_no.split("_")[0]);
                    payReturnOrder.refundId = payNotify.out_trade_no;

                    if (payReturnOrder.paymentStatus!=1 as byte)
                    {
                        payReturnOrder.tradeNo = payNotify.transaction_id;
                        payReturnOrder.payReturnDatas = xml.replaceAll("\n","").replaceAll("\r","");
                        switch (payReturnOrder.orderType as byte)
                        {
                            case 0 as byte:
                                //修改库存以及支付状态
                                orderBean.payedReturnOrderProcess(payReturnOrder);
                                break;
                            case 2 as byte://发起拼团
                                //修改库存以及支付状态
                                orderBean.payedReturnBookOrder(payReturnOrder);
                                break;
                            case 10 as byte://不提前生成订单的单独购买；仅用于课程
                                orderBean.payedReturnBookOrder(payReturnOrder);
                                break;
                            case 4 as byte://到店消费
                                orderBean.payedReturnBookOrder(payReturnOrder);
                                break;
                            case 3 as byte://跟随拼团
                                orderBean.payedReturnBookOrder(payReturnOrder);
                                break;
                            case 1 as byte:
                                //修改库存以及支付状态
                                orderBean.createOrderBuyers4BigTuan(payReturnOrder);

//                                Product product = orderBean.findObjectById(Product.class,payReturnOrder.productId);
//                                product.productPrivater = null;
//                                product.brand = null;
                                //redis begin
                                redisUtil.lLeftPush("pay_array",payReturnOrder.id);
                                if (!ScheduledBean.runPayOrderFlag)
                                {
                                    redisApi.runPayOrderArray();
                                }
                                //redis end
                                break;
                        }
                    }

                    /** 处理业务 */
                    /** 封装通知信息,给微信做应答的 */
                    MchBaseResult baseResult = new MchBaseResult();
                    baseResult.return_code = "SUCCESS";
                    baseResult.return_msg = "OK";
                    return XMLConverUtil.convertToXML(baseResult);
                }
                else
                {
                    MchBaseResult baseResult = new MchBaseResult();
                    baseResult.return_code = "FAIL";
                    baseResult.return_msg = "FAIL";
                    return XMLConverUtil.convertToXML(baseResult);
                }
            }
        }
        catch (Exception e)
        {
            processExcetion(e);
            MchBaseResult baseResult = new MchBaseResult();
            baseResult.return_code = "FAIL";
            baseResult.return_msg = "FAIL";
            return XMLConverUtil.convertToXML(baseResult);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/runPayOrderArray")
    String runPayOrderArray()
    {
        redisApi.runPayOrderArray();
        return """{"status":"OK"}""";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/wxRefundPay4AppUnifiedOrder")
    String wxRefundPay4AppUnifiedOrder(@RequestBody Map<String,Object> query)
    {
        try
        {
//            SecapiPayRefund secapiPayRefund = new SecapiPayRefund();
//            secapiPayRefund.appid = redisApi.ganTokenValue(query.appId,1 as byte,"appId");
//            secapiPayRefund.mch_id = redisApi.ganTokenValue(query.appId,1 as byte,"mchId");
//            secapiPayRefund.notify_url = redisApi.ganTokenValue(query.appId,1 as byte,"returnDoMain");;
//            secapiPayRefund.nonce_str = UUID.randomUUID().toString().toString().replace("-", "");
//            secapiPayRefund.transaction_id = "4200000573202008150109553374";
//            secapiPayRefund.out_trade_no = "202008151414314796757";
//            secapiPayRefund.out_refund_no = "202008151414314796757";
//            secapiPayRefund.total_fee = 1;
//            secapiPayRefund.refund_fee = 1;
//            secapiPayRefund.refund_desc = "";
//            SecapiPayRefundResult secapiPayRefundResult = PayMchAPI.secapiPayRefund(secapiPayRefund,OtherUtils.givePropsValue("mchapikey"));
//            ObjectMapper objectMapper = new ObjectMapper();
//            return objectMapper.writeValueAsString([
//                    "status":"OK",
//                    "secapiPayRefundResult":({return secapiPayRefundResult}).call()
//            ]);

//            redisUtil.lLeftPush("pay_array",query.id);
//            if (!ScheduledBean.runPayOrderFlag)
//            {
//                redisApi.runPayOrderArray();
//            }
//            return """{"status":"OK"}""";

            redisApi.test();
            return """{"status":"OK"}""";



            orderBean.createNativeQuery("""select pr.tradeno,ob.id,ob.deliveryquantity,ob.price,pr.paymentfee,pr.appid,pr.paytype,pr.refundid from orderbuyers ob left join payreturnorder pr on pr.id = ob.id where ob.orderstatus = 11
union
select pr.tradeno,o.id,o.ordertotalqutitynum,o.ordertotalamount,o.paymentfee,pr.appid,pr.paytype,pr.refundid from orders o left join payreturnorder pr on pr.id = o.payreturnorderid where o.ordertype = 0 and o.orderstatus = 11
""").getResultList()?.each {data->
                if (data[6] as byte == 1 as byte)
                {
                    SecapiPayRefund secapiPayRefund = new SecapiPayRefund();
                    secapiPayRefund.appid = redisApi.ganTokenValue(data[5],data[6] as byte,"appId");//"wx541c7422110ff0ed";//
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
                            //orderBean.createNativeQuery4Params("update payreturnorder set payreturndatas = :xml where id = :id",["id":data[1],"xml":"""微信退款接口--接口请求状态 (result_code):${secapiPayRefundResult.result_code};(err_code):${secapiPayRefundResult.err_code};(err_code_des):${secapiPayRefundResult.err_code_des}""".toString()]).executeUpdate();
                        }
                        else
                        {
                            println "微信退款接口--接口请求状态(result_code):${secapiPayRefundResult.result_code}";
                            println "微信退款接口--接口请求状态(err_code):${secapiPayRefundResult.err_code}";
                            println "微信退款接口--接口请求状态(err_code_des):${secapiPayRefundResult.err_code_des}";
                            //orderBean.createNativeQuery4Params("update payreturnorder set payreturndatas = :xml where id = :id",["id":data[1],"xml":"""微信退款接口--接口请求状态 (result_code):${secapiPayRefundResult.result_code};(err_code):${secapiPayRefundResult.err_code};(err_code_des):${secapiPayRefundResult.err_code_des}""".toString()]).executeUpdate();
                        }
                    }
                }
            }

//            redisUtil.lLeftPush("test","5555");
//            redisUtil.lBRightPopAndLeftPush("test","testbak",0 as long, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
//    @Consumes("text/plain")//application/xml
//    @Produces(MediaType.TEXT_PLAIN)//text/plain
    @Path("/wxRefundPay4AppNotify")
    String wxRefundPay4AppNotify()
    {
        String xml = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));
//        xml = """<xml><return_code>SUCCESS</return_code><appid><![CDATA[wx8b9af159c4490c3b]]></appid><mch_id><![CDATA[1383399602]]></mch_id><nonce_str><![CDATA[f3003d18d70eb7f23a768c531017b14b]]></nonce_str><req_info><![CDATA[zMx4p8hs04QQIu+TbfgQg/Dtts0N9ByV72iJr+n45Co/7m1fg9TwcjGN4K/gvC7XfQQEKCUtO+UGjo2PXVqEa6liQFq7aQa9oVRsH8Rt0jYa1avNgoO9qKJPwXJkwUecP+5tX4PU8HIxjeCv4Lwu1yo1cElLX6U6E5iamTfU6uJ4cdFJO3qzDUfPzcnnozZKywTb8irTHcmHBPqw+nMPoID9MbvlxCgm/rL6JuP/7hrAPxlLgGaY18i0hCKphUku4XBApjCfwpAAn2uVLqxVw2ub1PlVR+dF1vvSU0IIq6xL5xgzwa48n1PzAmX1YCek/ohxew3OoE/FjQLWI1hBh58WpzoQLlwmV+ahIVoHwfs3jQlLXJ+OX3UoqZVwxjkE4u8wXKkkPgE4apHSZ1GHADS+XpAvb3OnaDLQ/yZ0TbmSclgHt7/pHAlMYiKcG/wi1DGQM4N5dePsU4B3zPZiJyyvoZfK9n1np7E9mlZB8h4IT1RscEB6ab+ICO1Y2TqR4Q1AzLCJtYtMB70b41IcfbUlu0fMuStPQdDBhIFDYJ1v7ps3+4Cfb4MuJjZovpHmotaOUlHTr4w+3wD2hfdAJoC62NTXaWsrxlwpIXACVYzZ47R4nUAuWCkoO6kemOtekqGMoBg/re9fZi/SFqRTiytapOmrZAIU1tB+qwIwvMHBsnOeJF7ALIZY3qiVdfhF7Q5vY0xKqOLxEsWoBhmZgo+e1L53/J0yxVpywiMX5QudpU1Qn7Zvfm1yiDktERAyh4oE//fH5kiVEAAEuzV07NZINpNnlkq2dEI4jqyeDKPZQkV/4bMtg02ubxXFS8JE1qAYoFg1S7jwmhL7zD5HDdkAVMX7l5WwQwlmDL6JnqQPA8hl7jNWAmzbmAEBmSKJ7s9LyKId6PP2WrpsYcG0Q7fJlQhFiOJYc8KuSy+7g09dv7rJIP5EEcNgeiuCnw8a/XnxrY4IBYd7LtOOFU4J6oYG/KdPKX3lPpgvHouHjKU3Qd8WxnXQ1ZAVZ+yJ/MiCR209LU2p5Dgg8tHt67kfQWpx+K7FbVI8iVjH+G/rXmazoSD5YBfzxSVDQg43EX+1]]></req_info></xml>""";
        println xml;
        SecapiPayRefundNotify refundNotify = XMLConverUtil.convertToObject(SecapiPayRefundNotify.class, xml);
        if (refundNotify != null && "SUCCESS".equals(refundNotify.getReturn_code()))
        {
            RefundNotifyReqInfo refundNotifyReqInfo = PayUtil.decryptRefundNotifyReqInfo(refundNotify.getReq_info(), redisApi.ganTokenValue(refundNotify.appid,1 as byte,"partnerKey"));
            if (refundNotifyReqInfo == null) {
                MchBaseResult baseResult = new MchBaseResult();
                baseResult.setReturn_code("FAIL");
                baseResult.setReturn_msg("ERROR");
                return XMLConverUtil.convertToXML(baseResult);
            }

            orderBean.tuiKuanReturnCall(refundNotifyReqInfo,xml);

            MchBaseResult baseResult = new MchBaseResult();
            baseResult.setReturn_code("SUCCESS");
            baseResult.setReturn_msg("OK");
            return XMLConverUtil.convertToXML(baseResult);
        }
        else
        {
            MchBaseResult baseResult = new MchBaseResult();
            baseResult.setReturn_code("FAIL");
            baseResult.setReturn_msg("ERROR");
            return XMLConverUtil.convertToXML(baseResult);
        }
    }
}