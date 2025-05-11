package com.weavict.shop.rest


import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import com.weavict.shop.entity.*
import com.weavict.shop.module.OrderBean
import com.weavict.shop.module.RedisApi
import com.weavict.shop.redis.RedisUtil
import groovy.json.JsonSlurper
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

@Path("/order")
class OrderRest extends BaseRest
{
    @Autowired
    RedisApi redisApi

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OrderBean orderBean;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/productTuanInfo")
    String productTuanInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "tuanInfo":[
                             "bean":({
                                 //redis
                                 TuanInfo tuanInfo = null;
                                 if (redisUtil.hExists("tuan_${query.productId}","bean"))
                                 {
                                     tuanInfo = objectMapper.readValue(redisUtil.hGet("tuan_${query.productId}","bean"),TuanInfo.class);
                                     tuanInfo.orderTotalBuyerNum = redisUtil.hGet("tuan_${query.productId}","orderTotalBuyerNum") as int;
                                     tuanInfo.orderTotalQutityNum = redisUtil.hGet("tuan_${query.productId}","orderTotalQutityNum") as int;
                                     tuanInfo.orderTotalAmount = redisUtil.hGet("tuan_${query.productId}","orderTotalAmount") as int;
                                     return tuanInfo;
                                 }
                                 else
                                 {
                                     return [:];
                                 }
                                 //redis end
                             }).call(),
                             "orgBuyersNum": redisUtil.lLen("tuanBuyers_${query.productId}_${query.orgrationId}") as int
                             ]
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
    @Path("/preOrderGoingToPay")
    String preOrderGoingToPay(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            PayReturnOrder payReturnOrder = objToBean(query.payReturnOrder,PayReturnOrder.class,objectMapper);
            if (query.orderList==null)
            {
                payReturnOrder = orderBean.preOrderGoingToPay(payReturnOrder,null);
            }
            else
            {
                //此方法List中，不会是Order对象，而是转换成了LinkHashMap对象，要用new TypeReference<List<Order>>() { }才可以。原来的写法是这样的： objToBean(query.orderList,List.class,objectMapper);
                List<Order> orderList = objectMapper.convertValue(query.orderList,new TypeReference<List<Order>>() { });
                payReturnOrder = orderBean.preOrderGoingToPay(payReturnOrder,orderList);
            }
            // update asny alice code
//            if (payReturnOrder.orderType == 1 as byte)
//            {
//                payReturnOrder = orderBean.preOrderGoingToPay(payReturnOrder,null);
//            }
//            else if (payReturnOrder.orderType == 0 as byte)
//            {
//                Order order = objToBean(query.order,Order.class,objectMapper);
//                payReturnOrder = orderBean.preOrderGoingToPay(payReturnOrder,order);
//            }
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "payReturnOrder":({
                        return payReturnOrder;
                    }).call(),
                    "payReturnOrderId":({
                        return payReturnOrder.id;
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
    @Path("/withGoTuan4Product")
//    此方法不使用，调整到了支付接口回调的方法年内，表示支付成功了执行
    String withGoTuan4Product(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            switch (query.orderType as byte)
            {
                case 0 as byte:
                    break;
                case 1 as byte:
                    boolean isCreaterOrder = false;
                    PayReturnOrder payReturnOrder = orderBean.findObjectById(PayReturnOrder.class,query.payOrderId);
                    //支付表中是否已经是已经支付的状态，是就不在往下处理（已经处理过）
                    if (payReturnOrder.paymentStatus==1 as byte)
                    {
                        return """{"status":"OK"}""";
                    }
                    //修改库存以及支付状态
                    orderBean.payedReturnTuanOrder(payReturnOrder);

                    Product product = orderBean.findObjectById(Product.class,payReturnOrder.productId);
                    product.productPrivater = null;
                    product.brand = null;
                    //从redis是否有商品的TuanInfo，有就从redis读取，没有就查询数据库（数据库没有就建立，写进redis）
                    TuanInfo tuanInfo = null;
                    if (redisUtil.hExists("tuan_${payReturnOrder.productId}","bean"))
                    {
                        tuanInfo = objectMapper.readValue(redisUtil.hGet("tuan_${payReturnOrder.productId}","bean"),TuanInfo.class);
                    }
                    else
                    {
                        List<TuanInfo> tuanInfoList = orderBean.queryTuan8Product(payReturnOrder.productId,false);
                        tuanInfoList.each {
                            //如果时间超出或者到了结束时间，就将已经存在的该商品团购订单设定为结束
                            if ( (new JDateTime(new Date())).compareDateTo((new JDateTime(it.endDate))) > (-1 as int) )
                            {
                                it.ended = true;
                                orderBean.updateTheObject(it);
                            }
                            else
                            {
                                tuanInfo = it;
                            }
                        }
                        if (tuanInfo==null)
                        {
                            tuanInfo = orderBean.createTuanInfo(product);
                        }

                        //redis
                        redisApi.buildRedisOrderTuan(objectMapper,tuanInfo);
                        //redis end
                    }

                    //放进redis中存储这个社群正有哪些商品在参与团购
                    if (!redisUtil.hasKey("orgProductTuaning_${payReturnOrder.orgrationId}"))
                    {
                        redisApi.buildProductsTuaning8TheOrgration(objectMapper,payReturnOrder.orgrationId);
                    }
                    if (redisUtil.getExpire("orgProductTuaning_${payReturnOrder.orgrationId}") < 0l)
                    {
                        redisUtil.expireAt("orgProductTuaning_${payReturnOrder.orgrationId}",(new JDateTime(new Date())).addDay(1).convertToDate());
                    }

                    //从redis是否有商品的Order，有就从redis读取，没有就查询数据库（数据库没有就建立，写进redis）
                    Order order = null;
                    if(redisUtil.hExists("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","bean"))
                    {
                        order = objectMapper.readValue(redisUtil.hGet("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","bean"),Order.class);
                    }
                    else
                    {
                        List<Order> orderList = orderBean.queryTuan8ProductOrders(tuanInfo.id,payReturnOrder.orgrationId,1 as byte,1 as byte);
                        //是否存在该商品的社群订单
                        if (orderList==null || orderList.size()<1)
                        {
                            order = orderBean.createTuanOrder(tuanInfo.productId,tuanInfo.id,payReturnOrder.buyerId,payReturnOrder.orgrationId,payReturnOrder.dailiNo);
                            order.cancelLazyEr();
                            order.orgration.buyDesc = "";
                            order.orgration.description = "";
                            order.orgration.manageDesc = "";
                            isCreaterOrder = true;
                            redisUtil.zAdd("orgProductTuaning_${payReturnOrder.orgrationId}",
                                    objectMapper.writeValueAsString(["productId":product.id,"productName":product.name,"productImg":product.masterImg]), 0 as double);
                        }
                        else
                        {
                            order = orderList[0];
                            order.cancelLazyEr();
                        }
                        //redis
                        redisApi.buildTheTuanOrgrationOrder(objectMapper,tuanInfo,order);
                        //redis end
                    }

                    if (!redisUtil.hasKey("tuanBuyers_${tuanInfo.productId}_${order.orgration.id}"))
                    {
                        redisApi.buildTheTuanOrgrationBuyers(new ObjectMapper(),tuanInfo,order);
                    }
                    //redis
                    OrderBuyers orderBuyers = orderBean.joinTuan8Org(tuanInfo.id,product.id,order.id,payReturnOrder.id,payReturnOrder.buyerId,payReturnOrder.ids,payReturnOrder.specImg,payReturnOrder.price as int,payReturnOrder.deliveryQuantity as int);
                    orderBuyers.cancelLazyEr();
                    orderBuyers.order.orgration = null;
                    orderBuyers.order.buyer = null;
                    orderBuyers.product.brand = null;
                    orderBuyers.product.productPrivater = null;
                    redisUtil.lRightPush("tuanBuyers_${tuanInfo.productId}_${order.orgration.id}",objectMapper.writeValueAsString(orderBuyers));
                    redisUtil.expireAt("tuanBuyers_${tuanInfo.productId}_${order.orgration.id}",(new JDateTime(tuanInfo.endDate)).addDay(1).convertToDate());
                    //计算平台该商品团购的汇总
                    tuanInfo.orderTotalBuyerNum = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalBuyerNum") as int;
                    tuanInfo.orderTotalAmount = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalAmount") as int;
                    tuanInfo.orderTotalQutityNum = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalQutityNum") as int;
                    redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalBuyerNum",(tuanInfo.orderTotalBuyerNum + 1) as String);
                    redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalAmount",(tuanInfo.orderTotalAmount + ((payReturnOrder.deliveryQuantity as int) * (payReturnOrder.price as int))) as String);
                    redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalQutityNum",(tuanInfo.orderTotalQutityNum + (payReturnOrder.deliveryQuantity as int)) as String);
                    if (isCreaterOrder)
                    {
                        tuanInfo.orderTotalOrgNum = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalOrgNum") as int;
                        redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalOrgNum",(tuanInfo.orderTotalOrgNum + 1) as String);
                    }

                    //计算当前社群该商品团购的汇总
                    order.orderTotalBuyerNum = redisUtil.hGet("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalBuyerNum") as int;
                    order.orderTotalQutityNum = redisUtil.hGet("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalQutityNum") as int;
                    order.orderTotalAmount = redisUtil.hGet("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalAmount") as int;
                    redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalBuyerNum",(order.orderTotalBuyerNum + 1) as String);
                    redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalAmount",(order.orderTotalAmount + ((payReturnOrder.deliveryQuantity as int) * (payReturnOrder.price as int))) as String);
                    redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalQutityNum",(order.orderTotalQutityNum + (payReturnOrder.deliveryQuantity as int)) as String);

                    //在redis中减少相应的库存
                    if (payReturnOrder.specId in [null,""])
                    {
                        if (redisUtil.hExists("product_${payReturnOrder.productId}","store"))
                        {
                            redisUtil.hPut("product_${payReturnOrder.productId}","store",
                                    ((redisUtil.hGet("product_${payReturnOrder.productId}","store") as int) - (payReturnOrder.deliveryQuantity as int)) as String);
                        }
                    }
                    else
                    {
                        if (redisUtil.hExists("product_${payReturnOrder.productId}","store_${payReturnOrder.specId}"))
                        {
                            redisUtil.hPut("product_${payReturnOrder.productId}","store_${payReturnOrder.specId}",
                                    ((redisUtil.hGet("product_${payReturnOrder.productId}","store_${payReturnOrder.specId}") as int) - (payReturnOrder.deliveryQuantity as int)) as String);
                        }
                    }
                    //redis end
                    break;
            }
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
    @Path("/orgTuaningOrder")
    String orgTuaningOrder(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            if (!redisUtil.hasKey("orgProductTuaning_${query.orgrationId}"))
            {
                redisApi.buildProductsTuaning8TheOrgration(objectMapper,query.orgrationId);
            }
            return objectMapper.writeValueAsString(["status":"OK",
                    "totalNum": redisUtil.zSize("orgProductTuaning_${query.orgrationId}"),
                "datas":({
                    List l = new ArrayList();
                    long start = 0l;
                    long end = -1l;
                    if (!(query.page in [null,""]) && !(query.size in [null,""]))
                    {
                        start = (query.page as long) * (query.size as long);
                        end = start + (query.size as long) - 1l;
                    }
                    redisUtil.zRange("orgProductTuaning_${query.orgrationId}",start,end)?.each {
                        Map map = objectMapper.readValue(it,Map.class);
                        if (redisUtil.hasKey("tuan_${map["productId"]}"))
                        {

                            map["t_orderTotalQutityNum"] = redisUtil.hGet("tuan_${map["productId"]}","orderTotalQutityNum");
                            map["t_orderTotalOrgNum"] = redisUtil.hGet("tuan_${map["productId"]}","orderTotalOrgNum");
                        }
                        if (redisUtil.hasKey("tuanOrder_${map["productId"]}_${query.orgrationId}"))
                        {
                            map["o_orderTotalQutityNum"] = redisUtil.hGet("tuanOrder_${map["productId"]}_${query.orgrationId}","orderTotalQutityNum");
                        }
                        l << map;
                    }
                    return l;
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
    @Path("/productOrgBuyers")
    String productOrgBuyers(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(["status":"OK",
                                                    "totalNum": redisUtil.lLen("tuanBuyers_${query.productId}_${query.orgrationId}"),
                                                    "datas":({
                                                        List l = new ArrayList();
                                                        long start = 0l;
                                                        long end = -1l;
                                                        if (!(query.page in [null,""]) && !(query.size in [null,""]))
                                                        {
                                                            start = (query.page as long) * (query.size as long);
                                                            end = start + (query.size as long) - 1l;
                                                        }
                                                        redisUtil.lRange("tuanBuyers_${query.productId}_${query.orgrationId}",start,end).each {
                                                            OrderBuyers orderBuyers = objectMapper.readValue(it,OrderBuyers.class);
                                                            BuyerOrgration buyerOrgration = objectMapper.readValue(redisUtil.hGet("buyer_${orderBuyers.buyer.phone}","obBean_${query.orgrationId}"),BuyerOrgration.class);
                                                            l << ["orderBuyers":orderBuyers,
                                                                       "buyerOrgInfo":buyerOrgration];
                                                        }
                                                        return l;
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
    @Path("/buyerOrders")
    String buyerOrders(@RequestBody Map<String,Object> query)
    {
        try
        {
            Map map = orderBean.queryTuanOrders8Buyer(query.buyerId,query.privaterId,query.orderStatus as byte);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                    "totalNum": map.size(),
                    "datas":({
                        return map;
                    }).call()
            ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

//    查询个人购买订单
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryOrders8Buyer")
    String queryOrders8Buyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "datas":({
                         return orderBean.queryOrders8Buyer(query.buyerId,query.privaterId,query.orderType as byte,query.orderStatus as byte)?.each {
                             it.cancelLazyEr();
                             it.buyer = null;
                             it.orderItemList = [];
                             orderBean.queryOrderItems8Order(it.id)?.each {oi->
                                 oi?.cancelLazyEr();
                                 oi.order = null;
                                 it.orderItemList << oi;
                             };
                             it.tempMap = [:];
                             if (!(it.privaterId in [null,""]))
                             {
                                 ProductsPrivater privater = orderBean.findObjectById(ProductsPrivater.class,it.privaterId);
                                 privater.cancelLazyEr();
                                 it.tempMap["privater"] = privater;
                             }
                         };
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
    @Path("/queryTuanedOrderByTheProduct")
    String queryTuanedOrderByTheProduct(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "datas":({
                         return orderBean.queryTuanedOrderByTheProduct(query.productId,query.appId);
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
    @Path("/queryOrderBuyers8TheProduct")
    String queryOrderBuyers8TheProduct(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "datas":({
                         return orderBean.queryOrderBuyers8TheProduct(query.productId,query.appId);
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
    @Path("/queryBuyerTuanOrderList")
    String queryBuyerTuanOrderList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "datas":({
                         return orderBean.queryBuyerTuanOrderList(query.appId,query.buyerId);
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
    @Path("/queryTheOrder")
    String queryTheOrder(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Order order = orderBean.findObjectById(Order.class,query.orderId);
            order.cancelLazyEr();
            order.orderItemList = new ArrayList();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                         "order":({
                             orderBean.queryOrderItems8Order(order.id).each {oi->
                                 oi.cancelLazyEr();
                                 oi.order = null;
                                 order.orderItemList << oi;
                             }
                             return order;
                         }).call(),
                            "payReturnOrder":({
                                PayReturnOrder payReturnOrder = orderBean.findObjectById(PayReturnOrder.class,order.payReturnOrderId);
                                payReturnOrder.cancelLazyEr();
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
    @Path("/deleteTheOrder")
    String deleteTheOrder(@RequestBody Map<String,Object> query)
    {
        try
        {
            orderBean.deleteTheOrder(query.orderId,query.orderType as byte);
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
    @Path("/deletePayReturnOrders")
    String deletePayReturnOrders(@RequestBody Map<String,Object> query)
    {
        try
        {
            orderBean.deletePayReturnOrders(query.payReturnOrderId);
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
    @Path("/updateOrderStatus")
    String updateOrderStatus(@RequestBody Map<String,Object> query)
    {
        try
        {
            orderBean.updateOrderStatus(query.orderId,query.orderType as byte,query.orderStatus as byte,query.orderBuyersId,query.allTuanOrder as boolean,query.orgId);
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
    @Path("/updateOrderListStatus")
    String updateOrderListStatus(@RequestBody Map<String,Object> query)
    {
        try
        {
            def jsonSlpuer = new JsonSlurper();
            def obj = jsonSlpuer.parseText(query.selProducts);
            obj?.each {
                orderBean.updateOrderStatus(it.orderId,it.orderType as byte,it.orderStatus as byte,it.orderBuyersId,it.allTuanOrder as boolean,it.orgId);
            }
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
    @Path("/updateOrderKdInfo")
    String updateOrderKdInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            //JT3018450616141
//            def map = [:];
//            map["appId"] = "wx8b9af159c4490c3b";
//            map["payType"] = 1 as byte;
//            map["buyerId"] = "13268990066";
//            map["openId"] = "oVEX15awAJul7iMIBZeM3umFtDEk";
//            map["tradeNo"] = "4200001389202204243822358177";

//            暂时去掉查询物流信息，因为伙大啦小程序没有申请功能
//            Map map = orderBean.queryOrderKdWuLiuInfo(query.orderId);
//            HttpResponse httpResponse = HttpRequest.post("https://api.weixin.qq.com/cgi-bin/express/delivery/open_msg/trace_waybill?access_token=${redisApi.ganTokenValue(map["appId"],map["payType"],"accessToken")}")
//                        .body("""{"openid":"${map["openId"]}","receiver_phone":"${map["buyerId"]}","waybill_id":"${query.deliveryNo}","goods_info":{"detail_list":[{"goods_name":"${query.deliveryNo}","goods_img_url":"https://www.baidu.com/img/flexible/logo/pc/result.png"}]},"trans_id":"${map["tradeNo"]}"}""","application/json;charset=UTF-8")
//                        .executeAsync();
//            def jsonSlpuer = new JsonSlurper();
//            def obj = jsonSlpuer.parseText(httpResponse.body());
//            println "ok:"+obj.dump();
//            if (obj.errcode!=0 || obj.errmsg!="ok")
//            {
//                return """{"status":"FA_WL"}""";
//            }
            String waybillToken = "";//obj.waybill_token;
            orderBean.updateOrderKdInfo(query.deliveryId,query.deliveryName,query.deliveryNo,query.deliveryFee as int,query.orderId,waybillToken,query.orderType as byte);
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
    @Path("/orderTuanProductList")
    String orderTuanProductList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.orderTuanProductList(query.privaterId,query.beginDate,query.endDate);
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
    @Path("/orderReport8Privater")
    String orderReport8Privater(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.orderReport8Privater(query.appId,query.privaterId,query.productId,query.tuanId);
                    }).call()
            ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

//    用于订单发货
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/orderReportOrgDetail")
    String orderReportOrgDetail(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.orderReportOrgDetail(query.tuanInfoId,query.orderId,query.ids);
                    }).call()
            ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    //用于订单查询
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/tuanOrderDetailOrg")
    String tuanOrderDetailOrg(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.tuanOrderDetailOrg(query.orderId);
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
    @Path("/geRenOrderGroupBy")
    String geRenOrderGroupBy(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.geRenOrderGroupBy(["appId":query.appId,"privaterId":query.privaterId,"orderId":query.orderId,"orderType":query.orderType as byte,"orderStatus":query.orderStatus as byte,
                                                            "beginDate":(query.beginDate in [null,""]) ? null : DateUtil.parse("${query.beginDate}","yyyy-MM-dd HH:mm:ss"),
                                                            "endDate":(query.endDate in [null,""]) ? null : DateUtil.parse("${query.endDate}","yyyy-MM-dd HH:mm:ss")]);
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
    @Path("/orderReportGeRenDetail")
    String orderReportGeRenDetail(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.orderReportGeRenDetail(query.orderId);
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
    @Path("/queryCoupon8ThePrivater")
    String queryCoupon8ThePrivater(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.queryCoupon8ThePrivater(query.appId,query.privaterid)?.each {
                            it.cancelLazyEr();
                        };
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
    @Path("/theCoupon")
    String theCoupon(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "coupon":({
                        Coupon coupon = orderBean.findObjectById(Coupon.class,query.couponId);
                        coupon?.cancelLazyEr();
                        return coupon;
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
    @Path("/theBuyerCoupon")
    String theBuyerCoupon(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "buyerCoupon":({
                        BuyerCoupon buyerCoupon = orderBean.findObjectById(BuyerCoupon.class,query.buyerCouponId);
                        buyerCoupon?.cancelLazyEr();
                        return buyerCoupon;
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
    @Path("/updateCoupon")
    String updateCoupon(@RequestBody Map<String,Object> query)
    {
        try
        {
            Coupon coupon = this.objToBean(query.coupon,Coupon.class,null);
            if (coupon.id in [null,""])
            {
                coupon.id = MathUtil.getPNewId();
                coupon.createDate = new Date();
            }
            orderBean.updateTheObject(coupon);
            return """{"status":"OK","couponId":"${coupon.id}"}""";
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
    @Path("/updatebuyerCouponStatus")
    String updatebuyerCouponStatus(@RequestBody Map<String,Object> query)
    {
        try
        {
            orderBean.updatebuyerCouponStatus(query.buyerCouponId);
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
    @Path("/deleteTheCoupon")
    String deleteTheCoupon(@RequestBody Map<String,Object> query)
    {
        try
        {
            orderBean.deleteTheCoupon(query.couponId);
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
    @Path("/queryCouponList4ShiYouXiao8Buyer")
    String queryCouponList4ShiYouXiao8Buyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.queryCouponList4ShiYouXiao8Buyer(query.appId,query.buyerId,query.privaterId,query.orgrationId)?.each {
                            it.cancelLazyEr();
                        };
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
    @Path("/queryCouponBuyerList8Buyer")
    String queryCouponBuyerList8Buyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.queryCouponBuyerList8Buyer(query.appId,query.buyerId,query.shiUsed,query.shiGuoQi)?.each {
                            it.cancelLazyEr();
                        };
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
    @Path("/updateBuyerCoupon")
    String updateBuyerCoupon(@RequestBody Map<String,Object> query)
    {
        try
        {
            BuyerCoupon buyerCoupon = this.objToBean(query.buyerCoupon,BuyerCoupon.class,null);
            if (buyerCoupon.id in [null,""])
            {
                buyerCoupon.id = MathUtil.getPNewId();
                buyerCoupon.beginDate = new Date();
                buyerCoupon.endDate = cn.hutool.core.date.DateUtil.offsetDay(buyerCoupon.beginDate,buyerCoupon.youXiaoDays);
            }
            orderBean.updateTheObject(buyerCoupon);
            return """{"status":"OK","buyerCouponId":"${buyerCoupon.id}"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
