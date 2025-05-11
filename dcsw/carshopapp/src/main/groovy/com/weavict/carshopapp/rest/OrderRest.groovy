package com.weavict.carshopapp.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.carshopapp.entity.Order
import com.weavict.carshopapp.entity.PayReturnOrder
import com.weavict.carshopapp.module.OrderService
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
    @Context
    HttpServletRequest request;

    @Autowired
    OrderService orderService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryShopOrderList")
    String queryShopOrderList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "ordersInfo":({
                         def map = orderService.queryShopOrderList(query.shopId,query.buyerId,query.buyerName,query.carNumber,query.companyName,query.brand,query.xingHao,query.runedKl as int,query.type as byte,query.status as byte,
                                 (query.beginDate in [null,""]) ? null : DateUtil.parse("${query.beginDate} 00:00:00","yyyy-MM-dd HH:mm:ss"),
                                 (query.endDate in [null,""]) ? null : DateUtil.parse("${query.endDate} 23:59:59","yyyy-MM-dd HH:mm:ss"));
                         map["orderList"]?.each {
                             it.cancelLazyEr();
                             it.tempMap = [:];
                             if (it.companyId in [null,""] && it.companyName in [null,""])
                             {
                                 it.tempMap.buyerType = 0 as byte;
                             }
                             else
                             {
                                 it.tempMap.buyerType = 1 as byte;
                             }
//                             it.orderPayAmount = it.orderPayAmount / 100;
//                             it.orderTotalAmount = it.orderTotalAmount / 100;
//                             it.orderTotalCbAmount = it.orderTotalCbAmount / 100;
                         }
//                         if (map["orderSum"][0][0]!=null)
//                         {
//                             map["orderSum"][0][0] = (map["orderSum"][0][0] as int) / 100;
//                             map["orderSum"][0][1] = (map["orderSum"][0][1] as int) / 100;
//                             map["orderSum"][0][2] = (map["orderSum"][0][2] as int) / 100;
//                         }
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryOrderItemList8Order")
    String queryOrderItemList8Order(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "orderItemsInfo":({
                         return orderService.queryOrderItemList8Order(query.orderId)?.each {
                             it.cancelLazyEr();
                             it.order = null;
//                             it.price = it.price / 100;
//                             it.cbPrice = it.cbPrice / 100;
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
    @Path("/queryTheOrder")
    String queryTheOrder(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "orderInfo":({
                         Order order = orderService.findObjectById(Order.class,query.orderId);
//                         order.orderTotalAmount = order.orderTotalAmount / 100;
//                         order.orderTotalCbAmount = order.orderTotalCbAmount / 100;
//                         order.orderPayAmount = order.orderPayAmount / 100;
                         order.cancelLazyEr();
                         order.orderItemList = orderService.queryOrderItemList8Order(query.orderId)?.each {
                             it.cancelLazyEr();
                             it.order = null;
//                             it.price = it.price / 100;
//                             it.cbPrice = it.cbPrice / 100;
                         }
                         return order;
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
    @Path("/updateTheOrder")
    String updateTheOrder(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "orderInfo":({
                         Order order = orderService.updateTheOrder(objToBean(query.order, Order.class,objectMapper));
//                         order.orderPayAmount = order.orderPayAmount / 100;
//                         order.orderTotalCbAmount = order.orderTotalCbAmount / 100;
//                         order.orderTotalAmount = order.orderTotalAmount / 100;
                         order.cancelLazyEr();
                         return order;
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
    @Path("/queryLikeWord4Choice")
    String queryLikeWord4Choice(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "dataInfo":({
                         return orderService.queryLikeWord4Choice(query.shopId,query.word);
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
    @Path("/queryReportYear8Shop")
    String queryReportYear8Shop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "reportInfo":({
                         return orderService.queryReportYear8Shop(query.shopId,query.year,query.month)?.each {r->
                             r.cancelLazyEr();
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
}
