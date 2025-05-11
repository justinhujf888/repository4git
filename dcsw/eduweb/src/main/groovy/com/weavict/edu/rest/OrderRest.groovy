package com.weavict.edu.rest

import cn.hutool.core.date.DateUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.edu.entity.Order
import com.weavict.edu.entity.PayReturnOrder
import com.weavict.edu.module.OrderService
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

@Path("/order")
class OrderRest extends BaseRest
{
    @Autowired
    OrderService orderBean;

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
            if (query.order==null)
            {
                payReturnOrder = orderBean.preOrderGoingToPay(payReturnOrder,null);
            }
            else
            {
                Order order = objToBean(query.order,Order.class,objectMapper) as Order;
                payReturnOrder = orderBean.preOrderGoingToPay(payReturnOrder,order);
            }
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "payReturnOrder":({
                        return payReturnOrder;
                    }).call(),
                    "orderId":({
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
    @Path("/buyerOrders")
    String buyerOrders(@RequestBody Map<String,Object> query)
    {
        try
        {
            Map map = orderBean.queryTuanOrders8Buyer(query.buyerId,query.orgrationId,query.orderStatus as byte);
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

//    查询个人购买订单、拼团订单
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
                         return orderBean.queryOrders8Buyer(query.buyerId,query.orgrationId,query.orderType as byte,query.orderStatus as byte,
                                 query.beginDate in [null,""] ? null : DateUtil.parse(query.beginDate),query.endDate in [null,""] ? null : DateUtil.parse(query.endDate))?.each {
                             it.cancelLazyEr();
                             it.buyer = null;
                             it.orderItemList = [];
                             if ((query.orderType as byte) in [0 as byte,10 as byte])
                             {
                                 orderBean.queryOrderItems8Order(it.id)?.each {oi->
                                     oi.cancelLazyEr();
                                     oi.order = null;
                                     it.orderItemList << oi;
                                 }
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
    @Path("/queryTheOrder")
    String queryTheOrder(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "order":({
                         Order order = orderBean.findObjectById(Order.class,query.orderId);
                         order.cancelLazyEr();
                         order.orderItemList = new ArrayList();
                         orderBean.queryOrderItems8Order(order.id).each {oi->
                             oi.cancelLazyEr();
                             oi.order = null;
                             order.orderItemList << oi;
                         }
                         return order;
                     }).call(),
                     "payReturnOrder":({
                         PayReturnOrder payReturnOrder = orderBean.findObjectById(PayReturnOrder.class,query.orderId);
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
            orderBean.updateOrderKdInfo(query.deliveryId,query.deliveryName,query.deliveryNo,query.orderId,query.orgId,query.orderType as byte);
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
                        return orderBean.orderTuanProductList(query.privaterId);
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
                        return orderBean.orderReport8Privater(query.privaterId,query.productId,query.tuanId);
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
                                                            "beginDate":(query.beginDate in [null,""]) ? null : com.weavict.common.util.DateUtil.parse("${query.beginDate}","yyyy-MM-dd HH:mm:ss"),
                                                            "endDate":(query.endDate in [null,""]) ? null : com.weavict.common.util.DateUtil.parse("${query.endDate}","yyyy-MM-dd HH:mm:ss")]);
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
                        return orderBean.orderReportGeRenDetail(query.productId);
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
    @Path("/reportDatas8Date")
    String reportDatas8Date(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.reportDatas8Date(query.appId,query.privaterId,
                                com.weavict.common.util.DateUtil.parse(query.beginDate,"yyyy-MM-dd HH:mm:ss"),
                                com.weavict.common.util.DateUtil.parse(query.endDate,"yyyy-MM-dd HH:mm:ss"));
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
    @Path("/reportBookStudy8Date")
    String reportBookStudy8Date(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas":({
                        return orderBean.reportBookStudy8Date(query.queryType as byte,query.privaterId,
                                com.weavict.common.util.DateUtil.parse(query.beginDate,"yyyy-MM-dd HH:mm:ss"),
                                com.weavict.common.util.DateUtil.parse(query.endDate,"yyyy-MM-dd HH:mm:ss"));
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
