package com.weavict.edu.module

import cn.hutool.core.date.DateField
import com.weavict.edu.entity.Buyer
import com.weavict.edu.entity.BuyerBook
import com.weavict.edu.entity.BuyerBookPK
import com.weavict.edu.entity.Order
import com.weavict.edu.entity.OrderBuyers
import com.weavict.edu.entity.OrderItem
import com.weavict.edu.entity.Orgration
import com.weavict.edu.entity.PayReturnOrder
import com.weavict.edu.entity.Product
import com.weavict.edu.entity.TuanInfo
import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import groovy.json.JsonSlurper
import jodd.datetime.JDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import weixin.popular.bean.paymch.RefundNotifyReqInfo

@Service("orderBean")
class OrderService extends ModuleBean
{
    List queryOrgTuanOrder8Product(String productId,String orgrationId,boolean ended)
    {
        return this.createNativeQuery("select ti.productid,to_char(ti.createdate,'yyyy-MM-dd hh24:mi:ss') as createdate,to_char(ti.enddate,'yyyy-MM-dd hh24:mi:ss') as enddate,ti.ordertotalbuyernum,ti.ordertotalamount,o.id from tuaninfo as ti left join orders as o on ti.id = o.tuaninfoid left join orderitem as oi on o.id = oi.order_id where o.ordertype = 1 and ti.ended = ${ended} and ti.productid = '${productId}' and o.orgration_id = '${orgrationId}'").getResultList();
    }

    List<TuanInfo> queryTuan8Product(String productId,boolean ended)
    {
        return this.queryObject("select t from TuanInfo as t where t.productId = :productId and t.ended = :ended",["productId":productId,"ended":ended]);
    }

    List<TuanInfo> queryTuanList(String productId,boolean hasEnd,boolean ended)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select t from TuanInfo as t where 1=1";
        if (!(productId in [null,""]))
        {
            sbf << " and t.productId = :productId";
            paramsMap["productId"] = productId;
        }
        if (hasEnd)
        {
            sbf << " and t.ended = :ended";
            paramsMap["ended"] = ended;
        }
        return this.queryObject(sbf.toString(),paramsMap);
    }

    List<Order> queryTuan8ProductOrders(String tuanInfoId,String orgrationId,byte orderType,byte orderStatus)
    {
        StringBuffer sbf = new StringBuffer();
        sbf << "select o from Order as o where 1=1";
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        if (!(tuanInfoId in [null,""]))
        {
            sbf << " and o.tuanInfoId = :tuanInfoId";
            paramsMap["tuanInfoId"] = tuanInfoId;
        }
        if (!(orgrationId in [null,""]))
        {
            sbf << " and orgration.id = :orgrationId";
            paramsMap["orgrationId"] = orgrationId;
        }
        if (orderType > (-1 as byte))
        {
            sbf << " and o.orderType = :orderType";
            paramsMap["orderType"] = orderType;

        }
        if (orderStatus > (-1 as byte))
        {
            sbf << " and o.orderStatus = :orderStatus";
            paramsMap["orderStatus"] = orderStatus;

        }
        return this.queryObject(sbf.toString(),paramsMap);
    }

    List<OrderItem> queryOrderItems8Order(String orderId)
    {
        return this.queryObject("select oi from OrderItem as oi where oi.order.id = :orderId",["orderId":orderId]);
    }

    List<OrderBuyers> queryOrderBuyers8Order(String orderId)
    {
        return this.queryObject("select ob from OrderBuyers as ob where ob.order.id = :orderId",["orderId":orderId]);
    }

    Map queryTuanOrders8Buyer(String buyerId,String orgrationId,byte orderStatus)
    {
        StringBuffer sbf = new StringBuffer();
        sbf << "select ob from OrderBuyers as ob where 1=1";
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        if (!(buyerId in ["",null]))
        {
            sbf << " and ob.buyer.phone = :buyerId";
            paramsMap["buyerId"] = buyerId;
        }
        if (!(orgrationId in ["",null]))
        {
            sbf << " and ob.orgrationId = :orgrationId";
            paramsMap["orgrationId"] = orgrationId;
        }
        if (orderStatus > (-1 as byte))
        {
            sbf << " and ob.orderStatus = :orderStatus";
            paramsMap["orderStatus"] = orderStatus;
        }
        sbf << " order by ob.createDate desc";
        Map map = [:];
        this.queryObject(sbf.toString(),paramsMap)?.each {
            it.cancelLazyEr();
            Product product = new Product();
            if (it.product!=null)
            {
                product.id = it.product.id;
                product.name = it.product.name;
                product.productPrivater = it.product.productPrivater;
                it.product = product;
            }
            else
            {

            }
            it.buyer.description = "";
            Order order = new Order();
            order.id = it.order.id;
            order.orderStatus = it.orderStatus;
            order.orderType = it.order.orderType as byte;
            order.createDate = it.order.createDate;
            order.orgration = new Orgration();
            order.buyer = new Buyer();
            if (!(it.orgrationId in [null,""]))
            {
                order.orgration = this.findObjectById(Orgration.class,it.orgrationId);
                order.orgration.cancelLazyEr();
            }
            order.buyer.phone = it.order.buyer.phone;
            order.description = it.order.description;
            order.orderBuyersList = new ArrayList();
            order.orderBuyersList << it;
            it.order = null;
            if (map[order.orderStatus]==null)
            {
                map[order.orderStatus] = new ArrayList();
            }
            map[order.orderStatus] << order;
        }
        return map;
    }

    List<Order> queryOrders8Buyer(String buyerId,String orgrationId,byte orderType,byte orderStatus,Date beginDate,Date endDate)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select o from Order o where 1=1";
        if (!(buyerId in ["",null]))
        {
            sbf << " and o.buyer.phone = :buyerId";
            paramsMap["buyerId"] = buyerId;
        }
        if (!(orgrationId in ["",null]))
        {
            sbf << " and o.orgration.id = :orgrationId";
            paramsMap["orgrationId"] = orgrationId;
        }
        if (orderType > (-1 as byte))
        {
            sbf << " and o.orderType = :orderType";
            paramsMap["orderType"] = orderType;
        }
        if (orderStatus > (-1 as byte))
        {
            sbf << " and o.orderStatus = :orderStatus";
            paramsMap["orderStatus"] = orderStatus;
        }
        if (beginDate!=null && endDate!=null)
        {
            sbf << " and o.createDate between :beginDate and :endDate";
            paramsMap["beginDate"] = beginDate;
            paramsMap["endDate"] = endDate;
        }
        return this.queryObject(sbf.toString(),paramsMap);
    }

    @Transactional
    PayReturnOrder preOrderGoingToPay(PayReturnOrder payReturnOrder,Order order)
    {
        payReturnOrder.createDate = new Date();
        payReturnOrder.paymentStatus = 0 as byte;
        if (payReturnOrder.orderType == 0 as byte)
        {
            if (payReturnOrder.id in [null,""])
            {
                payReturnOrder.id = MathUtil.getPNewId();
            }
            if (order.id in [null,""])
            {
                order.id = payReturnOrder.id;
            }
            order.createDate = payReturnOrder.createDate;
            List<OrderItem> orderItemList = order.orderItemList;
            order.orderItemList = null;
            this.updateObject(order);
            orderItemList.each {orderItem->
                orderItem.id = MathUtil.getPNewId();
                orderItem.createDate = order.createDate;
                orderItem.order = order;
                this.updateObject(orderItem);
            }
        }
//      1：大团购；10：个人购，2,3拼团购；用于不提前生成订单的情况
        else if (payReturnOrder.orderType in [1 as byte,2 as byte,3 as byte,10 as byte])
        {
            payReturnOrder.id = MathUtil.getPNewId();
            payReturnOrder.paymentFee = payReturnOrder.deliveryQuantity * payReturnOrder.price;
            if (payReturnOrder.orderType in [1 as byte,2 as byte,10 as byte])
            {
                payReturnOrder.orderId = payReturnOrder.id;
            }
        }
        return this.updateObject(payReturnOrder);
    }

    @Transactional
    void payedReturnBookOrder(PayReturnOrder payReturnOrder)
    {
        this.executeEQL("update PayReturnOrder set paymentStatus = :paymentStatus,tradeNo = :tradeNo,payReturnDatas = :payReturnDatas where id = :id",["id":payReturnOrder.id,"tradeNo":payReturnOrder.tradeNo,"paymentStatus": (1 as byte),"payReturnDatas":payReturnOrder.payReturnDatas]);

        def jsonSlpuer = new JsonSlurper();
        def obj = jsonSlpuer.parseText(payReturnOrder.description);
        println obj;

        //因为可以替其他用户购买，所以需要检查这个用户是否登录注册（注意是obj.useBuyerId而不是obj.buyerId,obj.useBuyerId是使用者，obj.buyerId是支付者）
        if (this.findObjectById(Buyer.class,obj.useBuyerId)==null)
        {
            Buyer buyer = new Buyer();
            buyer.phone = payReturnOrder.buyerId;
            buyer.createDate = new Date();
            this.updateObject(buyer);
        }

        if (payReturnOrder.orderType in [10 as byte,2 as byte])
        {
            Order order = new Order();
            order.id = payReturnOrder.id;
            order.appId = payReturnOrder.appId;
            order.privaterId = obj.privaterId;
            order.deliveryType = 1 as byte;
            order.createDate = payReturnOrder.createDate;
//        order.orgration = this.findObjectById(Orgration.class,orgrationId);
            order.buyer = new Buyer();
            //obj.buyerId：实际付款的账号；payReturnOrder.buyerId or obj.useBuyerId：使用产品的账号
            order.buyer.phone = obj.buyerId;
            order.dailiNo = payReturnOrder.dailiNo;
            order.paymentStatus = 1 as byte;
            order.orderType = payReturnOrder.orderType;
            order.tuanInfoId = null;
            order.orderTotalQutityNum = payReturnOrder.deliveryQuantity;
            order.orderTotalBuyerNum = 1 as int;
            order.orderTotalAmount = payReturnOrder.paymentFee;
            order.description = payReturnOrder.description;
            if (payReturnOrder.orderType == 2 as byte)
            {
                order.paymentFee = obj.tuanPrice;
                if (obj.isBook == "true")
                {
                    order.orderStatus = 2 as byte;//原本应该是1，要等拼团完成，但目前设置为付款就可以使用
                    order.deliveryId = "book";//暂用标志，表示购买的是书
                    order.deliveryName = obj.bookId;
                }
                else
                {
                    order.orderStatus = 1 as byte;
                }
            }
            else if (payReturnOrder.orderType == 10 as byte)
            {
                order.paymentFee = payReturnOrder.paymentFee;
                if (obj.isBook == "true")
                {
                    //如果是虚拟产品，并且是单独购买，则付款即完成
                    order.orderStatus = 2 as byte;
                    order.deliveryId = "book";//暂用标志，表示购买的是书
                    order.deliveryName = obj.bookId;
                }
                else
                {
                    order.orderStatus = 1 as byte;
                }
            }

            OrderItem orderItem = new OrderItem();
            orderItem.createDate = order.createDate;
            orderItem.id = MathUtil.getPNewId();
            orderItem.deliveryQuantity = payReturnOrder.deliveryQuantity;
            orderItem.ids = payReturnOrder.ids;
            orderItem.order = order;
            orderItem.price = payReturnOrder.paymentFee;
            orderItem.specImg = payReturnOrder.specImg;

            this.updateObject(order);
            this.updateObject(orderItem);
        }

        if (payReturnOrder.orderType in [2 as byte,3 as byte])
        {
            OrderBuyers orderBuyers = new OrderBuyers();
            orderBuyers.product = null;
            orderBuyers.specImg = payReturnOrder.specImg;
            orderBuyers.price = payReturnOrder.price;
            orderBuyers.paymentFee = payReturnOrder.paymentFee;
            orderBuyers.paymentStatus = 1 as byte;
            if (obj.isBook == "true")
            {
                //如果是虚拟产品，并且是单独购买，则付款即完成
                orderBuyers.orderStatus = 2 as byte;
                orderBuyers.deliveryId = "book";
                orderBuyers.deliveryName = obj.bookId;
            }
            else
            {
                orderBuyers.orderStatus = 1 as byte;
            }
            orderBuyers.deliveryQuantity = payReturnOrder.deliveryQuantity;
            orderBuyers.id = payReturnOrder.id;
            orderBuyers.ids = payReturnOrder.ids;
            orderBuyers.buyer = new Buyer();
            orderBuyers.buyer.phone = obj.buyerId;
            orderBuyers.deliveryNo = obj.useBuyerId;//暂用，记录使用者账号
            orderBuyers.order = new Order();
            if (payReturnOrder.orderType == 2 as byte)
            {
                orderBuyers.order.id = payReturnOrder.id;
            }
            else if (payReturnOrder.orderType == 3 as byte)
            {
                orderBuyers.order.id = obj.orderId;
            }

            orderBuyers.createDate = new Date();
            this.updateObject(orderBuyers);

            if (payReturnOrder.orderType == 3 as byte)
            {
                this.executeEQL("update Order set orderTotalAmount = orderTotalAmount + :orderTotalAmount,orderTotalBuyerNum = orderTotalBuyerNum + 1,orderTotalQutityNum = orderTotalQutityNum + :orderTotalQutityNum where id = :id",["id":obj.orderId,"orderTotalAmount":payReturnOrder.paymentFee,"orderTotalQutityNum":payReturnOrder.deliveryQuantity]);
                //检查是否满足最小拼团数量，如果满足就把该拼团下所有用户的数据加入bookBuyers，并修改订单表状态
                if (true || (this.createNativeQuery4Params("select ordertotalqutitynum from orders where id = :id",["id":obj.orderId]).getSingleResult() as int) >= (obj.minCount as int))
                {
                    this.executeEQL("update OrderBuyers set orderStatus = :orderStatus where order.id = :orderId",["orderId":obj.orderId,"orderStatus":2 as byte]);
                    this.queryObject("select ob from OrderBuyers ob where ob.order.id = :orderId",["orderId":obj.orderId])?.each {ob->
                        if (obj.isBook=="true")
                        {
                            BuyerBook buyerBook = new BuyerBook();
                            buyerBook.buyerBookPK = new BuyerBookPK(obj.bookId,ob.deliveryNo);
                            buyerBook.beginDate = new Date();
                            if ((obj.bookYxDays as int) > 0)
                            {
                                buyerBook.endDate = cn.hutool.core.date.DateUtil.endOfDay(cn.hutool.core.date.DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, obj.bookYxDays as int)).toJdkDate();
                            }
                            this.updateObject(buyerBook);
                        }
                    }
                    this.executeEQL("update Order set orderStatus = :orderStatus where id = :id",["id":obj.orderId,"orderStatus":2 as byte]);
                }
            }
        }

        if (obj.isBook=="true" && payReturnOrder.orderType == 10 as byte)
        {
            BuyerBook buyerBook = new BuyerBook();
            buyerBook.buyerBookPK = new BuyerBookPK(obj.bookId,obj.useBuyerId);
            buyerBook.beginDate = new Date();
            if ((obj.bookYxDays as int) > 0)
            {
                buyerBook.endDate = cn.hutool.core.date.DateUtil.endOfDay(cn.hutool.core.date.DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, obj.bookYxDays as int)).toJdkDate();
            }
            else
            {
                buyerBook.endDate = cn.hutool.core.date.DateUtil.endOfDay(cn.hutool.core.date.DateUtil.offset(new Date(), DateField.YEAR, 1)).toJdkDate();
            }
            this.updateObject(buyerBook);
        }
    }

//    收到支付接口的异步返回后，把PayReturnOrder的支付状态设定为已支付，存储支付接口回调的信息，并减掉产品的库存；原来是为团购设计，但也可以用在会员单独购买
    @Transactional
    void payedReturnTuanOrder(PayReturnOrder payReturnOrder)
    {
        this.executeEQL("update PayReturnOrder set paymentStatus = :paymentStatus,tradeNo = :tradeNo,payReturnDatas = :payReturnDatas where id = :id",["id":payReturnOrder.id,"tradeNo":payReturnOrder.tradeNo,"paymentStatus": (1 as byte),"payReturnDatas":payReturnOrder.payReturnDatas]);
        if (payReturnOrder.specId in [null,""])
        {
            this.executeEQL("update Product set store = store - :deliveryQuantity where id = :id",["id":payReturnOrder.productId,"deliveryQuantity":payReturnOrder.deliveryQuantity]);
        }
        else
        {
            this.executeEQL("update ProductSpecSetup set store = store - :deliveryQuantity where id = :id",["id":payReturnOrder.specId,"deliveryQuantity":payReturnOrder.deliveryQuantity]);
        }
    }

//    团购是支付成功了才会新增Order表，而单独购买是先生成未支付状态的Order信息，此方法用于单独购买支付成功后调用修改库存以及订单状态
    @Transactional
    void payedReturnOrderProcess(PayReturnOrder payReturnOrder)
    {
        this.payedReturnTuanOrder(payReturnOrder);
        this.executeEQL("update Order set orderStatus = :orderStatus,paymentStatus = :paymentStatus where id = :id",
                ["id":payReturnOrder.id,"orderStatus":1 as byte,"paymentStatus": 1 as byte]);

//        经过考虑，非团购不启用OrderBuyers这个表
//        OrderBuyers orderBuyers = new OrderBuyers();
//        orderBuyers.product = null;
//        orderBuyers.specImg = payReturnOrder.specImg;
//        orderBuyers.price = payReturnOrder.price;
//        orderBuyers.paymentFee = payReturnOrder.price * payReturnOrder.deliveryQuantity;
//        orderBuyers.paymentStatus = 1 as byte;
//        orderBuyers.orderStatus = 1 as byte;
//        orderBuyers.deliveryQuantity = payReturnOrder.deliveryQuantity;
//        orderBuyers.id = payReturnOrder.id;
//        orderBuyers.ids = payReturnOrder.ids;
//        orderBuyers.buyer = new Buyer();
//        orderBuyers.buyer.phone = payReturnOrder.buyerId;
//        orderBuyers.order = new Order();
//        orderBuyers.order.id = payReturnOrder.id;
//        orderBuyers.createDate = new Date();
//        this.updateObject(orderBuyers);
    }

    @Transactional
    TuanInfo createTuanInfo(Product product)
    {
        TuanInfo tuanInfo = new TuanInfo();
        tuanInfo.id = MathUtil.getPNewId();
        tuanInfo.productId = product.id;
        tuanInfo.ended = false;
        tuanInfo.createDate = new Date();
        JDateTime jd = (new JDateTime(tuanInfo.createDate)).addDay(product.tgEndDays);
        tuanInfo.endDate = DateUtil.parse("${jd.getYear()}-${jd.getMonth()}-${jd.getDay()} 23:59:59","yyyy-MM-dd HH:mm:ss");
        tuanInfo.orderTotalAmount = 0;
        tuanInfo.orderTotalBuyerNum = 0;
        tuanInfo.orderTotalQutityNum = 0;
        tuanInfo.orderTotalOrgNum = 0;
        return this.updateObject(tuanInfo);
    }

    @Transactional
    Order createTuanOrder(String productId,String tuanInfoId,String buyerId,String orgrationId,String dailiNo)
    {
        Order order = new Order();
        order.id = MathUtil.getPNewId();
        order.createDate = new Date();
        order.orgration = this.findObjectById(Orgration.class,orgrationId);
        order.buyer = this.findObjectById(Buyer.class,buyerId);
        order.dailiNo = dailiNo;
        order.orderStatus = 1 as byte;
        order.paymentStatus = 1 as byte;
        order.orderType = 1;
        order.tuanInfoId = tuanInfoId;
        order.shipAreaPath = order.orgration.area;
        order.shipAddress = order.orgration.address;
        order.shipMobile = order.buyer.phone;
        order.shipPhone = order.orgration.tel;
        order.shipName = order.buyer.name;
        order.orderTotalQutityNum = 0 as int;
        order.orderTotalBuyerNum = 0 as int;
        order.orderTotalAmount = 0 as int;

        OrderItem orderItem = new OrderItem();
        orderItem.createDate = order.createDate;
        orderItem.id = MathUtil.getPNewId();
        orderItem.deliveryQuantity = 0;
        orderItem.ids = "";
        orderItem.order = order;
        orderItem.price = 0;
        orderItem.product = new Product();
        orderItem.product.id = productId;
        orderItem.specImg = "";

//        OrderBuyers orderBuyers = new OrderBuyers();
//        orderBuyers.product = orderItem.product;
//        orderBuyers.specImg = specImg;
//        orderBuyers.price = price;
//        orderBuyers.deliveryQuantity = deliveryQuantity;
//        orderBuyers.id = MathUtil.getPNewId();
//        orderBuyers.ids = ids;
//        orderBuyers.buyer = order.buyer;
//        orderBuyers.order = order;
//        orderBuyers.createDate = order.createDate;

        this.updateObject(order);
        this.updateObject(orderItem);
        this.executeEQL("update TuanInfo set orderTotalOrgNum = orderTotalOrgNum + 1 where id = :id",["id":tuanInfoId]);
//        this.updateObject(orderBuyers);
//        this.executeEQL("update TuanInfo set orderTotalAmount = orderTotalAmount + :orderTotalAmount,orderTotalQutityNum = orderTotalQutityNum + :orderTotalQutityNum where id = :id",["id":tuanInfoId,"orderTotalQutityNum":deliveryQuantity,"orderTotalAmount":deliveryQuantity * price]);
//        order.orderBuyersList = new ArrayList<OrderBuyers>();
//        order.orderBuyersList.add(orderBuyers);
        order.orderItemList = new ArrayList();
        order.orderItemList.add(orderItem);
        return order;
    }

    @Transactional
    OrderBuyers joinTuan8Org(String tuanInfoId,String productId,String orderId,PayReturnOrder payReturnOrder)
    {
        OrderBuyers orderBuyers = new OrderBuyers();
        orderBuyers.orgrationId = payReturnOrder.orgrationId;
        orderBuyers.product = new Product();
        orderBuyers.product.id = productId;
        orderBuyers.specImg = payReturnOrder.specImg;
        orderBuyers.price = payReturnOrder.price;
        orderBuyers.paymentFee = payReturnOrder.price * payReturnOrder.deliveryQuantity;
        orderBuyers.paymentStatus = 1 as byte;
        orderBuyers.orderStatus = 1 as byte;
        orderBuyers.deliveryQuantity = payReturnOrder.deliveryQuantity;
        orderBuyers.id = payReturnOrder.id;
        orderBuyers.ids = payReturnOrder.ids;
        orderBuyers.buyer = new Buyer();
        orderBuyers.buyer.phone = payReturnOrder.buyerId;
        orderBuyers.order = new Order();
        orderBuyers.order.id = orderId;
        orderBuyers.createDate = new Date();
        orderBuyers = this.updateObject(orderBuyers);
        this.executeEQL("update Order set orderTotalAmount = orderTotalAmount + :orderTotalAmount,orderTotalQutityNum = orderTotalQutityNum + :orderTotalQutityNum where id = :id",["id":orderId,"orderTotalQutityNum":payReturnOrder.deliveryQuantity,"orderTotalAmount":payReturnOrder.paymentFee]);
        this.executeEQL("update TuanInfo set orderTotalAmount = orderTotalAmount + :orderTotalAmount,orderTotalQutityNum = orderTotalQutityNum + :orderTotalQutityNum where id = :id",["id":tuanInfoId,"orderTotalQutityNum":payReturnOrder.deliveryQuantity,"orderTotalAmount":payReturnOrder.paymentFee]);
        return orderBuyers;
    }

    @Transactional
    void deleteTheOrder(String orderId,byte orderType)
    {
        if (orderType==0)
        {
            this.executeEQL("delete OrderItem where order.id = :orderId",["orderId":orderId]);
            this.executeEQL("delete Order where id = :orderId",["orderId":orderId]);
            this.executeEQL("delete PayReturnOrder where id = :id",["id":orderId]);
        }
        else if (orderType==1)
        {
            this.executeEQL("delete PayReturnOrder where id = :id",["id":orderId]);
            this.executeEQL("delete OrderBuyers where id = :id",["id":orderId]);
        }
    }

    @Transactional
    void updateOrderStatus(String orderId,byte orderType,byte orderStatus,String orderBuyersId,boolean allTuanOrder,String orgId)
    {
        if (orderType==0)
        {
            this.executeEQL("update Order set orderStatus = :orderStatus where id = :orderId",["orderId":orderId,"orderStatus":orderStatus]);
        }
        else if (orderType==1)
        {
            if (allTuanOrder)
            {
                this.executeEQL("update OrderBuyers set orderStatus = :orderStatus where order.id = :id",["id":orderId,"orderStatus":orderStatus]);
                this.executeEQL("update Order set orderStatus = :orderStatus where id = :orderId",["orderId":orderId,"orderStatus":orderStatus]);
            }
            else
            {
                if (orgId in [null,""])
                {
                    this.executeEQL("update OrderBuyers set orderStatus = :orderStatus where id = :id",["id":orderBuyersId,"orderStatus":orderStatus]);
                }
                else
                {
                    this.executeEQL("update OrderBuyers set orderStatus = :orderStatus where order.id = :orderId and orgrationId = :orgrationId",["orderId":orderId,"orgrationId":orgId,"orderStatus":orderStatus]);
                }
            }
        }
    }

    @Transactional
    void updateOrderKdInfo(String deliveryId,String deliveryName,String deliveryNo,String orderId,String orgId,byte orderType)
    {
        if (orderType == 0 as byte)
        {
            this.createNativeQuery("update orders set deliveryid='${deliveryId}',deliveryname='${deliveryName}',deliveryno='${deliveryNo}' where id = '${orderId}' and ordertype=0").executeUpdate();
        }
        else if (orderType == 1 as byte)
        {
            this.createNativeQuery("update orders set deliveryid='${deliveryId}',deliveryname='${deliveryName}',deliveryno='${deliveryNo}' where id = '${orderId}' and ordertype=1").executeUpdate();
            this.createNativeQuery("update orderbuyers set deliveryid='${deliveryId}',deliveryname='${deliveryName}',deliveryno='${deliveryNo}' where order_id='${orderId}'").executeUpdate();
        }
    }

    List products8Tuan(String orgrationId,String productId,String tuanId,boolean hasTuanEnd,boolean tuanEnd)
    {
        return this.createNativeQuery("select tuan.id tuanid,orders.orgration_id,p.id productid,p.name,p.masterimg,tuan.ordertotalorgnum,tuan.ordertotalqutitynum from tuaninfo tuan left join product p on p.id = tuan.productid left join orders on orders.tuaninfoid = tuan.id where 1=1 ${!hasTuanEnd ? "" : "and tuan.ended = ${tuanEnd}"} ${orgrationId in [null,""] ? "" : "and orders.orgration_id = '${orgrationId}'"} ${productId in [null,""] ? "" : "and p.id = '${productId}'"} ${tuanId in [null,""] ? "" : "and tuan.id = '${tuanId}'"}").getResultList();
    }

    List orderTuanProductList(String privaterId)
    {
        List l = new ArrayList();
        String sql = "";
//        where tuan.ended = true
        if (privaterId in [null,""])
        {
            sql = "select tuan.id,tuan.productid,p.name,tuan.ended,tuan.createdate,tuan.enddate,p.masterimg,tuan.ordertotalqutitynum,p.tgpersonscount from tuaninfo tuan left join product p on tuan.productid = p.id left join brand brand on brand.id = p.brand_id left join productsprivater privater on privater.id = brand.productsprivater_id order by tuan.createdate";
        }
        else
        {
            sql = "select tuan.id,tuan.productid,p.name,tuan.ended,tuan.createdate,tuan.enddate,p.masterimg,tuan.ordertotalqutitynum,p.tgpersonscount from tuaninfo tuan left join product p on tuan.productid = p.id left join brand brand on brand.id = p.brand_id left join productsprivater privater on privater.id = brand.productsprivater_id where 1=1 and privater.id = '${privaterId}' order by tuan.createdate";
        }
        this.createNativeQuery(sql).getResultList()?.each{tuan->
            l << tuan;
        }
        return l;
    }

//    团购订单
    List orderReport8Privater(String privaterId,String productId,String tuanId)
    {
        List l = new ArrayList();
        String sql = "";//tuan.ended = true
        if (privaterId in [null,""])
        {
            sql = "select tuan.id,tuan.productid,p.name,tuan.ended,tuan.createdate,tuan.enddate from tuaninfo tuan left join product p on tuan.productid = p.id left join brand brand on brand.id = p.brand_id left join productsprivater privater on privater.id = brand.productsprivater_id where 1=1 ${tuanId in [null,""] ? "" : "and tuan.id = '${tuanId}'"} ${productId in [null,""] ? "" : "and tuan.productid = '${productId}'"} order by tuan.createdate";
        }
        else
        {
            sql = "select tuan.id,tuan.productid,p.name,tuan.ended,tuan.createdate,tuan.enddate from tuaninfo tuan left join product p on tuan.productid = p.id left join brand brand on brand.id = p.brand_id left join productsprivater privater on privater.id = brand.productsprivater_id where 1=1 ${tuanId in [null, ""] ? "" : "and tuan.id = '${tuanId}'"} ${productId in [null, ""] ? "" : "and tuan.productid = '${productId}'"} and privater.id = '${privaterId}' order by tuan.createdate" ;
        }
        this.createNativeQuery(sql).getResultList()?.each {tuan->
            Map map = ["orderType":1,"tuanInfo":["id":tuan[0],"ended":tuan[3],"productId":tuan[1],"productName":tuan[2],"tuanCreateDate":tuan[4],"tuanEndDate":tuan[5],
                                                 "orgs":[]
            ]];
            List<String> idsArray = new ArrayList();
            this.createNativeQuery("select ids from productspecsetup psp where psp.product_id = '${tuan[1]}'").getResultList()?.each{spec ->
                StringBuffer sbf = new StringBuffer();
                def jsonSlpuer = new JsonSlurper();
                def obj = jsonSlpuer.parseText(spec);
                obj?.each {jo->
                    sbf << "${jo.name},";
                }
                idsArray << sbf.toString()[0..sbf.toString().length()-1-1];
                sbf.delete(0,sbf.length()-1);
            }
            map["tuanInfo"]["orgs"] = [];
            this.createNativeQuery("select distinct org.id,org.name,org.area,org.address,o.deliveryid,o.deliveryname,o.deliveryno,o.id orderId from tuaninfo tuan left join orders o on o.tuaninfoid = tuan.id left join orgration org on org.id = o.orgration_id where tuan.id = '${tuan[0]}'").getResultList()?.each {org->
                Map orgInfoMap = ["orgInfo":["orgId":org[0],"orgName":org[1],"orgArea":org[2],"orgAddress":org[3],"orderId":org[7],
                                             "contect":({
                                                 def contect = this.createNativeQuery("select distinct buyer.name,buyer.phone from orgration org left join orgration_buyer orgbuyer on org.id = orgbuyer.orgration_id left join buyer on buyer.phone = orgbuyer.buyer_id where org.id = '${org[0]}' and orgbuyer.iscreater = true").getResultList()[0];
                                                 return ["phone":contect[1],"name":contect[0]];
                                             }).call()],
                                  "deliveryInfo":["deliveryId":org[4],"deliveryName":org[5],"deliveryNo":org[6]],
                                  "datas":[],"idsDatas":[],"orderStatusInfo":[]]
                if (idsArray.size()>0)
                {
                    idsArray.each {ids->
                        println "________________________";
                        println ids;
                        orgInfoMap["idsDatas"] << ["idsName":"${ids}","quantity":this.createNativeQuery("select sum(pr.deliveryquantity) from payreturnorder pr left join product p on p.id = pr.productid left join orderbuyers ob on pr.id = ob.id left join orders o on o.id = ob.order_id left join tuaninfo tuan on tuan.id = o.tuaninfoid where tuan.id = '${tuan[0]}' and o.id = '${org[7]}' and pr.ids = '${ids}' and pr.paymentstatus = 1").getSingleResult()];
//                        orgInfoMap["orderStatusInfo"] = this.createNativeQuery("select orderstatus from orderbuyers where order_id = '${tuan[0]}' and orgrationid = '${org[0]}' and and ids = '${ids}' group by orderstatus").getSingleResult();
                    };
                }
                else
                {
                    orgInfoMap["datas"] = this.createNativeQuery("select sum(pr.deliveryquantity) from payreturnorder pr left join product p on p.id = pr.productid left join orderbuyers ob on pr.id = ob.id left join orders o on o.id = ob.order_id where o.tuaninfoid = '${tuan[0]}' and o.id = '${org[7]}' and pr.paymentstatus = 1").getSingleResult();
                }
//                List statusList = this.createNativeQuery("select ob.orderstatus from orderbuyers ob left join orders on ob.order_id = orders.id where orders.tuaninfoid = '${tuan[0]}' and ob.orgrationid = '${org[0]}' group by ob.orderstatus").getResultList();
                orgInfoMap["orderStatusInfo"] = this.createNativeQuery("select orderstatus from orders where id = '${org[7]}'").getSingleResult();
                map["tuanInfo"]["orgs"] << orgInfoMap;
            }
            l << map;
        }
        return l;
    }

    List orderReportOrgDetail(String tuanInfoId,String orderId,String ids)
    {
//                this.createNativeQuery("select pr.orgrationid,o.ordertype,ob.order_id tuanorderid,ob.id orderid,pr.productid,p.name,pr.ids,pr.specid, pr.deliveryquantity deliveryquantity,pr.paymentfee from payreturnorder pr left join product p on p.id = pr.productid left join orderbuyers ob on pr.id = ob.id left join orders o on o.id = ob.order_id left join tuaninfo tuan on tuan.id = o.tuaninfoid where tuan.id = '${tuanInfoId}' ${ids in [null,""] ? "" : "and pr.ids = '${ids}'"} and pr.orgrationid = '${orgId}'").getResultList()
        return this.createNativeQuery("select distinct pr.id,buyer.phone,buyer.name,orgbuyer.niname,orgbuyer.headimg,pr.ids,pr.deliveryquantity from payreturnorder pr left join product p on p.id = pr.productid left join orderbuyers ob on pr.id = ob.id left join orders o on o.id = ob.order_id left join tuaninfo tuan on tuan.id = o.tuaninfoid left join orgration_buyer orgbuyer on orgbuyer.buyer_id = pr.buyerid left join buyer on buyer.phone = pr.buyerid where tuan.id = '${tuanInfoId}' ${ids in [null,""] ? "" : "and pr.ids = '${ids}'"} and o.id = '${orderId}'").getResultList();
    }

    Map geRenOrderGroupBy(Map map)
    {
        List orderList = new ArrayList();
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        String sql1 = "select o.id,o.privaterid,o.ordertotalqutitynum,o.paymentfee,o.createdate,o.shipareapath,o.shipaddress,o.shipmobile,o.shipphone,o.shipname,o.shipzipcode,pp.name,pp.id ppid,o.orderstatus,o.ordertype,o.deliverytype,o.deliveryid,o.deliveryname,o.deliveryno,o.deliveryfee,o.ordertotalamount,o.buyer_phone,bai.wxnickname,bai.headimgurl,o.description from orders o left join productsprivater pp on pp.id = o.privaterid left join buyerappinfo bai on bai.buyerid = o.buyer_phone where o.appid = :appId and o.orderstatus > 0";
        String sql2 = "select sum(o.paymentfee) paymentfee,sum(o.ordertotalamount) ordertotalamount from orders o where o.appid = :appId and o.orderstatus > 0";
        paramsMap["appId"] = map["appId"];
        if (!(map["privaterId"] in [null,""]))
        {
            sbf << " and o.privaterid = :privaterId";
            paramsMap["privaterId"] = map["privaterId"];
        }
        if (!(map["orderId"] in [null,""]))
        {
            sbf << " and o.id like :orderId";
            paramsMap["orderId"] = "%${map["orderId"]}%";
        }
        if (map["orderType"] > (-1 as byte))
        {
            sbf << " and o.ordertype = :orderType";
            paramsMap["orderType"] = map["orderType"];
        }
        if (map["orderStatus"] > (-1 as byte))
        {
            sbf << " and o.orderstatus = :orderStatus";
            paramsMap["orderStatus"] = map["orderStatus"];
        }
        if (map["beginDate"]!=null && map["endDate"]!=null)
        {
            sbf << " and (o.createdate between :beginDate and :endDate)";
            paramsMap["beginDate"] = map["beginDate"];
            paramsMap["endDate"] = map["endDate"];
        }

        this.createNativeQuery4Params("${sql1}${sbf.toString()} order by o.createdate desc",paramsMap).getResultList()?.each {
            Order order = new Order();
            order.id = it[0];
            order.privaterId = it[1];
            order.orderTotalQutityNum = it[2];
            order.paymentFee = it[3];
            order.createDate = it[4];
            order.shipAreaPath = it[5];
            order.shipAddress = it[6];
            order.shipMobile = it[7];
            order.shipPhone = it[8];
            order.shipName = it[9];
            order.shipZipCode = it[10];
            order.privaterId = it[12];
            order.orderStatus = it[13] as byte;
            order.orderType = it[14] as byte;
            order.deliveryType = it[15] as byte;
            order.deliveryId = it[16];
            order.deliveryName = it[17];
            order.deliveryNo = it[18];
            order.deliveryFee = it[19];
            order.orderTotalAmount = it[20];
            order.buyer = new Buyer();
            order.buyer.phone = it[21];
            order.buyer.wxNickName = it[22];
            order.buyer.headImgUrl = it[23];
            order.tempMap = [:];
            order.tempMap["privaterName"] = it[11];
            order.description = it[24];
            order.orderItemList = this.queryOrderItems8Order(order.id)?.each {
                it?.cancelLazyEr();
            };
            orderList << order;
        };
        return ["orderList":orderList,"statistics":this.createNativeQuery4Params("${sql2}${sbf.toString()}",paramsMap).getResultList()];
    }
//  个人订单明细
    List orderReportGeRenDetail(String productId)
    {
        return this.createNativeQuery("select p.id,p.name,p.masterimg,pr.ids,pr.deliveryquantity,orders.shipname,orders.shipmobile,orders.shipphone,orders.shipareapath,orders.shipaddress,orders.orderstatus,orders.id ordersId,orders.deliveryid,orders.deliveryname,orders.deliveryno from orders left join payreturnorder pr on pr.id = orders.id left join product p on p.id = pr.productid where pr.productid = '${productId}' and pr.paymentstatus = 1 and pr.ordertype = 0 order by p.name,pr.ids").getResultList();
    }

    //    用于订单查询大货团购的明细
    List tuanOrderDetailOrg(String orderId)
    {
        List<PayReturnOrder> payReturnOrderList = new ArrayList();
        this.createNativeQuery4Params("select distinct pr.id,buyer.phone,buyer.name,buyapp.wxnickname,buyapp.headimgurl,pr.ids,pr.deliveryquantity,pr.paymentfee,pr.createdate,p.name productname,ob.orderstatus from payreturnorder pr left join product p on p.id = pr.productid left join orderbuyers ob on pr.id = ob.id left join orders o on o.id = ob.order_id left join buyerappinfo buyapp on buyapp.buyerid = pr.buyerid left join buyer on buyer.phone = pr.buyerid where o.id = :orderId",["orderId":orderId]).getResultList()?.each {
            PayReturnOrder payReturnOrder = new PayReturnOrder();
            payReturnOrder.id = it[0];
            payReturnOrder.buyerId = it[1];
            payReturnOrder.tempMap = [:];
            payReturnOrder.tempMap["buyerName"] = it[2];
            payReturnOrder.tempMap["niName"] = it[3];
            payReturnOrder.tempMap["headImgUrl"] = it[4];
            payReturnOrder.ids = it[5];
            payReturnOrder.deliveryQuantity = it[6] as int;
            payReturnOrder.paymentFee = it[7] as int;
            payReturnOrder.createDate = it[8];
            payReturnOrder.tempMap["productName"] = it[9];
            payReturnOrder.tempMap["orderStatus"] = it[10];
            payReturnOrderList << payReturnOrder;
        };
        return payReturnOrderList;
    }


    @Transactional
    void tuiKuanReturnCall(RefundNotifyReqInfo refundNotifyReqInfo, String xml)
    {
        def rs = this.createNativeQuery4Params("select ordertype,paymentstatus from payreturnorder where id = :id",["id":refundNotifyReqInfo.out_trade_no]).getSingleResult();
        if (rs[1] as byte != 12 as byte)
        {
            this.createNativeQuery4Params("update payreturnorder set paymentstatus = :payMentStatus,refundid = :refundId,refundno = :refundNo,payreturndatas = :xml where id = :id",
                    ["id":refundNotifyReqInfo.out_trade_no,"payMentStatus":12,"refundId":refundNotifyReqInfo.out_refund_no,"refundNo":refundNotifyReqInfo.refund_id,"xml":xml]).executeUpdate();
            if (rs[0] as byte == 0 as byte)
            {
                this.createNativeQuery4Params("update orders set orderstatus = :orderStatus where id = :id",["id":refundNotifyReqInfo.out_trade_no,"orderStatus":12 as byte]).executeUpdate();
            }
            else if (rs[0] as byte == 1 as byte)
            {
                this.createNativeQuery4Params("update orderbuyers set orderstatus = :orderStatus where id = :id",["id":refundNotifyReqInfo.out_trade_no,"orderStatus":12 as byte]).executeUpdate();
            }
        }
    }

    List reportDatas8Date(String appId,String privaterId,Date beginDate,Date endDate)
    {
        return this.createNativeQuery4Params("""
select 'ordertotalamount',sum(ordertotalamount) from orders where paymentstatus=1 and appid=:appId  and privaterid = :privaterId and (createdate between :beginDate and :endDate)
union
select 'buyer',count(phone) from buyer where createdate between :beginDate and :endDate
union
select 'buyerbook',count(buyerid) from buyerbook where begindate between :beginDate and :endDate
""",["appId":appId,"privaterId":privaterId, "beginDate":beginDate, "endDate":endDate]).getResultList();
    }

    List reportBookStudy8Date(byte queryType,String privaterId,Date beginDate,Date endDate)
    {
        if (queryType == 0 as byte)
        {
            return this.createNativeQuery4Params("""
select bs.bookid,b.name,count(bs.bookid) as bookcount from buyerstudy bs left join book b on bs.bookid=b.id where b.zxid = :privaterId and bs.createdate between :beginDate and :endDate group by bs.bookid,b.name order by bookcount desc
""",["privaterId":privaterId,"beginDate":beginDate,"endDate":endDate]).getResultList();
        }
        else if (queryType == 1 as byte)
        {
            return this.createNativeQuery4Params("""
select bb.bookid,b.name,count(bb.bookid) as bookcount from buyerbook bb left join book b on bb.bookid=b.id where b.zxid = :privaterId and bb.begindate between :beginDate and :endDate group by bb.bookid,b.name order by bookcount desc
""",["privaterId":privaterId,"beginDate":beginDate,"endDate":endDate]).getResultList();
        }
        else if (queryType == 2 as byte)
        {
            return this.createNativeQuery4Params("""
select ids,count(ids) from payreturnorder where paymentstatus = 1 and createdate between :beginDate and :endDate group by ids
""",["beginDate":beginDate,"endDate":endDate]).getResultList();
        }
    }
}
