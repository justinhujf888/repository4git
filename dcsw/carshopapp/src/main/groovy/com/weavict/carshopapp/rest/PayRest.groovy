package com.weavict.carshopapp.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.carshopapp.entity.PayReturnOrder
import com.weavict.carshopapp.module.OrderService
import com.weavict.website.common.OtherUtils
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import weixin.popular.api.PayMchAPI
import weixin.popular.bean.paymch.*
import weixin.popular.util.PayUtil
import weixin.popular.util.SignatureUtil
import weixin.popular.util.StreamUtils
import weixin.popular.util.XMLConverUtil

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
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
    OrderService orderService;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/preOrderGoingToPay")
    String preOrderGoingToPay(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            PayReturnOrder payReturnOrder = objToBean(query.payReturnOrder,PayReturnOrder.class,objectMapper);
            payReturnOrder = orderService.preOrderGoingToPay(payReturnOrder);
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "payReturnOrder":({
                        return payReturnOrder;
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
    @Path("/wxPay4AppUnifiedOrder")
    String wxPay4AppUnifiedOrder(@RequestBody Map<String,Object> query)
    {
        try
        {
            PayReturnOrder payReturnOrder = orderService.findObjectById(PayReturnOrder.class,query.payReturnOrderId);
            if (payReturnOrder==null)
            {
                return """{"status":"FA_NOORDER"}""";
            }
            if (payReturnOrder.paymentStatus==(1 as byte))
            {
                return """{"status":"FA_PAYED"}""";
            }

            ObjectMapper objectMapper = new ObjectMapper();

            Unifiedorder unifiedorder = new Unifiedorder();
            unifiedorder.appid = OtherUtils.givePropsValue("payappid");
            unifiedorder.mch_id = OtherUtils.givePropsValue("mchid");
            unifiedorder.nonce_str = UUID.randomUUID().toString().toString().replace("-", "");

            if (query.openId != null)
            {
                unifiedorder.openid = query.openId;
            }

            unifiedorder.body = payReturnOrder.id;
            unifiedorder.out_trade_no = payReturnOrder.id;
            unifiedorder.attach = """{"payReturnOrderId":"${payReturnOrder.id}","orderType":"${payReturnOrder.orderType}"}""";
            unifiedorder.total_fee = payReturnOrder.paymentFee as String;//单位分
            unifiedorder.spbill_create_ip = request.getRemoteAddr();//IP
            unifiedorder.notify_url = """${OtherUtils.givePropsValue("prxurl")}${OtherUtils.givePropsValue("prxconname")}/r/pay/wxPay4AppNotify""";
            println unifiedorder.dump();
            if (query.tradeType in [null,""])
            {
                unifiedorder.trade_type = "APP";//JSAPI，NATIVE，APP，WAP
            }
            else
            {
                unifiedorder.trade_type = query.tradeType;
            }

            //统一下单，生成预支付订单
            UnifiedorderResult unifiedorderResult = PayMchAPI.payUnifiedorder(unifiedorder,OtherUtils.givePropsValue("mchapikey"));

            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "unifiedOrderResult":({
                         if (unifiedorder.trade_type=="APP")
                         {
                             //@since 2.8.5  API返回数据签名验证
                             MchPayApp mchPayApp = null;
                             if(unifiedorderResult.getSign_status() !=null && unifiedorderResult.getSign_status())
                             {
                                 mchPayApp = PayUtil.generateMchAppData(unifiedorderResult.getPrepay_id(), unifiedorder.appid,OtherUtils.givePropsValue("mchid"),OtherUtils.givePropsValue("mchapikey"));
                             }
                             return mchPayApp;
                         }
                         else if (unifiedorder.trade_type=="JSAPI")
                         {
                             return PayUtil.generateMchPayJsRequestJson(unifiedorderResult.prepay_id,unifiedorderResult.appid,OtherUtils.givePropsValue("mchapikey"));
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
//    @Consumes("text/plain")//application/xml
//    @Produces(MediaType.TEXT_PLAIN)//text/plain
    @Path("/wxPay4AppNotify")
    String wxPay4AppNotify()
    {
        try
        {
            // 解析微信支付异步通知请求参数;
            String xml = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));

//            String xml = """<xml><appid><![CDATA[wx541c7422110ff0ed]]></appid>
//<attach><![CDATA[{"payReturnOrderId":"201912041608241687978","orderType":"1"}]]></attach>
//<bank_type><![CDATA[PAB_CREDIT]]></bank_type>
//<cash_fee><![CDATA[1]]></cash_fee>
//<fee_type><![CDATA[CNY]]></fee_type>
//<is_subscribe><![CDATA[N]]></is_subscribe>
//<mch_id><![CDATA[1561173361]]></mch_id>
//<nonce_str><![CDATA[4204448e939147d69bd4b6406306ee73]]></nonce_str>
//<openid><![CDATA[oz6P8v_YSnxI-XcQoHrLzOn9RQiA]]></openid>
//<out_trade_no><![CDATA[201912231456049399648]]></out_trade_no>
//<result_code><![CDATA[SUCCESS]]></result_code>
//<return_code><![CDATA[SUCCESS]]></return_code>
//<sign><![CDATA[4EC1769A5CA7380036C3729E33748FE7]]></sign>
//<time_end><![CDATA[20191104160840]]></time_end>
//<total_fee>1</total_fee>
//<trade_type><![CDATA[APP]]></trade_type>
//<transaction_id><![CDATA[4200000414201911041119680478]]></transaction_id>
//</xml>""";
            println xml;
            Map<String, String> params = XMLConverUtil.convertToMap(xml);
            MchPayNotify payNotify = XMLConverUtil.convertToObject(MchPayNotify.class, xml);
            if("SUCCESS".equals(payNotify.getReturn_code()))
            {
                if(SignatureUtil.validateSign(params,OtherUtils.givePropsValue("mchapikey")) && "SUCCESS".equals(payNotify.getResult_code()))
                {
                    /** 处理业务 */
                    def jsonSlpuer = new JsonSlurper()
                    def obj = jsonSlpuer.parseText(payNotify.attach);
                    switch (obj.orderType as byte)
                    {
                        case 0 as byte:
                            PayReturnOrder payReturnOrder = orderService.findObjectById(PayReturnOrder.class,payNotify.out_trade_no);
                            if (payReturnOrder.paymentStatus==1 as byte)
                            {
                                MchBaseResult baseResult = new MchBaseResult();
                                baseResult.return_code = "SUCCESS";
                                baseResult.return_msg = "OK";
                                println XMLConverUtil.convertToXML(baseResult);
                                return XMLConverUtil.convertToXML(baseResult);
                            }
                            //修改库存以及支付状态
                            payReturnOrder.tradeNo = payNotify.transaction_id;
                            payReturnOrder.payReturnDatas = xml.replaceAll("\n","").replaceAll("\r","");;
                            orderService.payedReturnOrderProcess(payReturnOrder);
                            break;
                        case 1 as byte:
                            break;
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/wxRefundPay4AppUnifiedOrder")
    String wxRefundPay4AppUnifiedOrder(@RequestBody Map<String,Object> query)
    {
        try
        {
            SecapiPayRefund secapiPayRefund = new SecapiPayRefund();
            secapiPayRefund.appid = OtherUtils.givePropsValue("payappid");
            secapiPayRefund.mch_id = OtherUtils.givePropsValue("mchid");
            secapiPayRefund.notify_url = """${OtherUtils.givePropsValue("prxurl")}${OtherUtils.givePropsValue("prxconname")}/r/pay/wxRefundPay4AppNotify""";
            secapiPayRefund.nonce_str = UUID.randomUUID().toString().toString().replace("-", "");
            secapiPayRefund.transaction_id = "4200000573202008150109553374";
            secapiPayRefund.out_trade_no = "202008151414314796757";
            secapiPayRefund.out_refund_no = "202008151414314796757";
            secapiPayRefund.total_fee = 1;
            secapiPayRefund.refund_fee = 1;
            secapiPayRefund.refund_desc = "";
            SecapiPayRefundResult secapiPayRefundResult = PayMchAPI.secapiPayRefund(secapiPayRefund,OtherUtils.givePropsValue("mchapikey"));
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "secapiPayRefundResult":({return secapiPayRefundResult}).call()
            ]);
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
            RefundNotifyReqInfo refundNotifyReqInfo = PayUtil.decryptRefundNotifyReqInfo(refundNotify.getReq_info(), OtherUtils.givePropsValue("mchapikey"));
            if (refundNotifyReqInfo == null) {
                MchBaseResult baseResult = new MchBaseResult();
                baseResult.setReturn_code("FAIL");
                baseResult.setReturn_msg("ERROR");
                return XMLConverUtil.convertToXML(baseResult);
            }

            orderService.tuiKuanReturnCall(refundNotifyReqInfo,xml);

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
