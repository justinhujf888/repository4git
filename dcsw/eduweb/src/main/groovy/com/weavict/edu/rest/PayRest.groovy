package com.weavict.edu.rest

import com.aliyun.oss.OSSClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.edu.entity.PayReturnOrder
import com.weavict.edu.module.OrderService
import com.weavict.edu.module.PayService
import com.weavict.edu.module.RedisApi
import com.weavict.website.common.OtherUtils
import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import weixin.popular.api.PayMchAPI
import weixin.popular.bean.paymch.MchBaseResult
import weixin.popular.bean.paymch.MchPayApp
import weixin.popular.bean.paymch.MchPayNotify
import weixin.popular.bean.paymch.Unifiedorder
import weixin.popular.bean.paymch.UnifiedorderResult
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
    @Context
    HttpServletRequest request;

    @Autowired
    PayService payService;

    @Autowired
    OrderService orderBean;

    @Autowired
    RedisApi redisApi;

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
            unifiedorder.appid = query.appId;//OtherUtils.givePropsValue("payappid");
            unifiedorder.mch_id = redisApi.ganTokenValue(query.appId,query.appType as byte,"mchId");
            unifiedorder.nonce_str = UUID.randomUUID().toString().toString().replace("-", "");

            if (query.openId != null)
            {
                unifiedorder.openid = query.openId;
            }

            if (!(payReturnOrder.productId in [null,""]))
            {
                unifiedorder.attach = """{"payReturnOrderId":"${payReturnOrder.id}","orderType":"${payReturnOrder.orderType}","isBook":"false"}""";
                if (ossClient.getObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"system/json/product/${payReturnOrder.productId}.json")!=null)
                {
                    def jsonSlpuer = new JsonSlurper();
                    def obj = jsonSlpuer.parseText(IOUtils.toString(ossClient.getObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"system/json/product/${payReturnOrder.productId}.json").objectContent,"utf-8"));
                    unifiedorder.body = obj.productInfo.productName;
                }
            }
            else
            {
                unifiedorder.attach = """{"payReturnOrderId":"${payReturnOrder.id}","orderType":"${payReturnOrder.orderType}","isBook":"true"}""";
                unifiedorder.body = payReturnOrder.ids;
            }
            unifiedorder.out_trade_no = payReturnOrder.id;
            unifiedorder.total_fee = payReturnOrder.paymentFee as String;//单位分
            unifiedorder.spbill_create_ip = request.getRemoteAddr();//IP
            unifiedorder.notify_url = redisApi.ganTokenValue(query.appId,query.appType as byte,"doMain");
            if (query.tradeType in [null,""])
            {
                unifiedorder.trade_type = "APP";//JSAPI，NATIVE，APP，WAP
            }
            else
            {
                unifiedorder.trade_type = query.tradeType;
            }

            //统一下单，生成预支付订单
            UnifiedorderResult unifiedorderResult = PayMchAPI.payUnifiedorder(unifiedorder,redisApi.ganTokenValue(query.appId,query.appType as byte,"partnerKey"));

            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "unifiedOrderResult":({
                         if (unifiedorder.trade_type=="APP")
                         {
                             //@since 2.8.5  API返回数据签名验证
                             MchPayApp mchPayApp = null;
                             if(unifiedorderResult.getSign_status() !=null && unifiedorderResult.getSign_status())
                             {
                                 mchPayApp = PayUtil.generateMchAppData(unifiedorderResult.getPrepay_id(), unifiedorder.appid,redisApi.ganTokenValue(query.appId,query.appType as byte,"mchId"),redisApi.ganTokenValue(query.appId,query.appType as byte,"partnerKey"));
                             }
                             return mchPayApp;
                         }
                         else if (unifiedorder.trade_type=="JSAPI")
                         {
                             return PayUtil.generateMchPayJsRequestJson(unifiedorderResult.prepay_id,unifiedorderResult.appid,redisApi.ganTokenValue(query.appId,query.appType as byte,"partnerKey"));
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
    @Path("/wxPay4WxNotify")
    String wxPay4WxNotify()
    {
        try
        {
            // 解析微信支付异步通知请求参数;
            String xml = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));

//            String xml = """
//            <xml>
//            <appid><![CDATA[wxa0f11cfb195dab99]]></appid>
//            <attach><![CDATA[{"payReturnOrderId":"202306201811490432760","orderType":"2","isBook":"true"}]]></attach>
//            <bank_type><![CDATA[OTHERS]]></bank_type>
//            <cash_fee><![CDATA[499]]></cash_fee>
//            <fee_type><![CDATA[CNY]]></fee_type>
//            <is_subscribe><![CDATA[N]]></is_subscribe>
//            <mch_id><![CDATA[1543429631]]></mch_id>
//            <nonce_str><![CDATA[a3cdb9028e2f404abd9f98dd20a01a7f]]></nonce_str>
//            <openid><![CDATA[o2ITN4kDHklEXchE-DxgGq29Uw3Q]]></openid>
//            <out_trade_no><![CDATA[202306201811490432760]]></out_trade_no>
//            <result_code><![CDATA[SUCCESS]]></result_code>
//            <return_code><![CDATA[SUCCESS]]></return_code>
//            <sign><![CDATA[F27A21FF855DB6EFCF9EBFA1C8946F8B]]></sign>
//            <time_end><![CDATA[20230620181226]]></time_end>
//            <total_fee>499</total_fee>
//            <trade_type><![CDATA[JSAPI]]></trade_type>
//            <transaction_id><![CDATA[4200001845202306208220802109]]></transaction_id>
//            </xml>""";
            println xml;
            Map<String, String> params = XMLConverUtil.convertToMap(xml);
            MchPayNotify payNotify = XMLConverUtil.convertToObject(MchPayNotify.class, xml);

//            if(true)//用于测试
            if("SUCCESS".equals(payNotify.getReturn_code()))
            {
//                if (true)//用于测试
                if(SignatureUtil.validateSign(params,OtherUtils.givePropsValue("mchkey")) && "SUCCESS".equals(payNotify.getResult_code()))
                {
                    /** 处理业务 */
                    PayReturnOrder payReturnOrder = orderBean.findObjectById(PayReturnOrder.class,payNotify.out_trade_no);
                    if (payReturnOrder.paymentStatus!=1 as byte)
                    {
                        payReturnOrder.tradeNo = payNotify.transaction_id;
                        payReturnOrder.payReturnDatas = xml.replaceAll("\n","").replaceAll("\r","");
                        switch (payReturnOrder.orderType as byte)
                        {
                            case 0 as byte://先生成订单的单独购买
                                //修改库存以及支付状态
                                orderBean.payedReturnOrderProcess(payReturnOrder);
                                break;
                            case 2 as byte://发起拼团
                                //修改库存以及支付状态
                                orderBean.payedReturnBookOrder(payReturnOrder);
                                break;
                            case 9 as byte:
                                //支付表中是否已经是已经支付的状态，是就不在往下处理（已经处理过）
                                //修改库存以及支付状态
                                payService.payedReturn(payReturnOrder);
                                break;
                            case 10 as byte://不提前生成订单的单独购买
                                orderBean.payedReturnBookOrder(payReturnOrder);
                                break;
                            case 3 as byte://跟随拼团
                                orderBean.payedReturnBookOrder(payReturnOrder);
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
}
