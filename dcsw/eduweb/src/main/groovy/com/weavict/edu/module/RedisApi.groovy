package com.weavict.edu.module

import com.aliyun.oss.OSSClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import com.weavict.edu.entity.Buyer
import com.weavict.edu.entity.BuyerAppInfo
import com.weavict.edu.entity.BuyerAppInfoPK
import com.weavict.edu.entity.BuyerOrgration
import com.weavict.edu.entity.BuyerOrgrationPK
import com.weavict.edu.entity.Order
import com.weavict.edu.entity.OrderBuyers
import com.weavict.edu.entity.OrderItem
import com.weavict.edu.entity.Orgration
import com.weavict.edu.entity.PayReturnOrder
import com.weavict.edu.entity.Product
import com.weavict.edu.entity.ProductsPrivater
import com.weavict.edu.entity.TuanInfo
import com.weavict.edu.entity.Twiter
import com.weavict.edu.entity.TwiterImages
import com.weavict.edu.redis.RedisUtil
import com.weavict.website.common.OtherUtils
import groovy.json.JsonSlurper
import jodd.datetime.JDateTime
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import weixin.popular.api.TicketAPI
import weixin.popular.api.TokenAPI
import weixin.popular.bean.ticket.Ticket
import weixin.popular.bean.token.Token
import weixin.popular.client.LocalHttpClient

import jakarta.inject.Inject
import java.util.concurrent.TimeUnit

//import com.weavict.website.common.OtherUtils
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig
//import redis.clients.jedis.Jedis
//import redis.clients.jedis.JedisPool

/**
 * Created by Justin on 2018/6/10.
 */
@Service("redisService")
class RedisApi
{
    @Inject
    RedisUtil redisUtil;

    @Inject
    UserBean userBean;

    @Inject
    OrderService orderBean;

    @Inject
    ProductsService productsBean;

    @Inject
    TwiterBean twiterBean;

    void buildRedisBuyer(ObjectMapper objectMapper,String buyerId,String field)
    {
        Buyer buyer = userBean.findObjectById(Buyer.class,buyerId);
        if (buyer==null)
        {
            return;
        }
        buyer.cancelLazyEr();
        switch (field)
        {
            case "bean":
//        String jsonStr = redisUtil.hGet("buyer_${buyerId}","bean");
//        buyer = objectMapper.readValue(jsonStr,Buyer.class);
                buyer.orgrationList = new ArrayList<Orgration>();
                redisUtil.hPut("buyer_${buyerId}","bean",objectMapper.writeValueAsString(
                        ({
                            return buyer;
                        }).call()
                ));
                break;
            case "buyerAppInfo":
                userBean.queryObject("select ba from BuyerAppInfo ba where ba.buyerAppInfoPK.buyerId = :buyerId",["buyerId":buyerId])?.each {ba->
                    ba.cancelLazyEr();
                    redisUtil.hPut("buyer_${buyer.phone}","appInfo_${ba.buyerAppInfoPK.appId}",objectMapper.writeValueAsString(ba));
                }
                break;
            case "buyerOrgration":
                buyer.orgrationList = new ArrayList<Orgration>();
                List<BuyerOrgration> boList = new ArrayList();
                userBean.queryOrgrationList8Buyer(buyerId).each {o->
                    Orgration orgration = new Orgration();
                    orgration.id = o[6];
                    orgration.name = o[7];
                    buyer.orgrationList.add(orgration);
                    BuyerOrgration buyerOrgration = new BuyerOrgration();
                    buyerOrgration.buyerOrgrationPK = new BuyerOrgrationPK(orgration.id,buyer.phone);
                    buyerOrgration.headImg = o[2];
                    buyerOrgration.niName = o[1];
                    buyerOrgration.status = o[3] as byte;
                    buyerOrgration.isManager = o[4];
                    buyerOrgration.isCreater = o[5];
                    buyerOrgration.createDate = DateUtil.parse(o[16],"yyyy-MM-dd HH:mm:ss");
                    buyerOrgration.description = o[17] ?: "";
                    boList << buyerOrgration;
                    redisUtil.hPut("buyer_${buyer.phone}","obBean_${orgration.id}",objectMapper.writeValueAsString(buyerOrgration));
                    redisUtil.lRemove("buyerOrgrationList_${buyerId}",0,orgration.id);
                    redisUtil.lRightPush("buyerOrgrationList_${buyerId}",orgration.id);
                };
                break;
        }
    }

    void buildRedisOrgration(ObjectMapper objectMapper,String orgrationId,String field)
    {
        switch (field)
        {
            case "bean":
                Orgration orgration = userBean.findObjectById(Orgration.class,orgrationId);
                println orgration.dump();
                orgration?.cancelLazyEr();
                if (orgration!=null)
                {
                    redisUtil.hPut("orgration_${orgrationId}","bean",objectMapper.writeValueAsString(
                            ({
                                return orgration;
                            }).call()
                    ));
                }
                break;
        }
    }

    void buildRedisBuyers8Orgration(ObjectMapper objectMapper,String orgrationId)
    {
        userBean.queryBuyers8Orgration(orgrationId,1 as byte).each {b->
            redisUtil.lRemove("orgrationBuyers_${orgrationId}",0 as long,b[4]);
            redisUtil.lRightPush("orgrationBuyers_${orgrationId}",b[4]);
//            Buyer buyer = userBean.findObjectById(Buyer.class,b[4]);
//            buyer.cancelLazyEr();
//            BuyerOrgration buyerOrgration = userBean.findObjectById(BuyerOrgration.class,new BuyerOrgrationPK(orgrationId,b[4]));
//            buyerOrgration.cancelLazyEr();
//            redisUtil.hPut("buyer_${b[4]}","bean",objectMapper.writeValueAsString(buyer));
//            redisUtil.hPut("buyer_${b[4]}","obBean_${orgrationId}",objectMapper.writeValueAsString(buyerOrgration));
        }
    }

    //很莫名其妙的错误，用Product prduct会类型不一样，所以用Object product代替
    void buildProductStore(ObjectMapper objectMapper, Object product,List l)
    {
        if (product.name in [null,""])
        {
            product = productsBean.findObjectById(Product.class,product.id);
        }
        if (l==null)
        {
            l = new ArrayList();
            productsBean.queryProductSpecSetupList8Product(product.id)?.each {
                it.cancelLazyEr();
                l << ["id":it.id,"ids":it.ids,"price":it.price,"tuanPrice":it.tuanPrice,"cbPrice":it.cbPrice,"store":it.store as int,"imgPath":it.specImg,"used":it.used];
            };
        }

        redisUtil.hPut("product_${product.id}","store",product.store as String);
        l?.each {
            redisUtil.hPut("product_${product.id}","store_${it.id}","${it.store}");
        };
        redisUtil.hPut("product_${product.id}","specSetupList",objectMapper.writeValueAsString(
                ({
                    List lr = new ArrayList();
                    l?.each {
                        lr << ["id":it.id,"store":it.store as int];
                    };
                    return lr;
                }).call()
        ));
    }

    //现在正在团购该商品的团购汇总值 总件数，总金额等
    void buildRedisOrderTuan(ObjectMapper objectMapper, TuanInfo tuanInfo)
    {
        Date exDate = (new JDateTime(tuanInfo.endDate)).addDay(1).convertToDate();
        redisUtil.hPut("tuan_${tuanInfo.productId}","bean",objectMapper.writeValueAsString(tuanInfo));
        redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalBuyerNum",tuanInfo.orderTotalBuyerNum as String);
        redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalAmount",tuanInfo.orderTotalAmount as String);
        redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalQutityNum",tuanInfo.orderTotalQutityNum as String);
        redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalOrgNum",tuanInfo.orderTotalOrgNum as String);
        redisUtil.expireAt("tuan_${tuanInfo.productId}",exDate);
    }

    //该商品的社群团购订单信息
    void buildTheTuanOrgrationOrder(ObjectMapper objectMapper,TuanInfo tuanInfo,Order order)
    {
        Date exDate = (new JDateTime(tuanInfo.endDate)).addDay(1).convertToDate();

        //减少json的大小
        order.orgration.description = "";
        order.orgration.manageDesc = "";
        order.orgration.buyDesc = "";

        order.orderItemList = new ArrayList<OrderItem>();
        orderBean.queryOrderItems8Order(order.id)?.each {oi->
            oi.cancelLazyEr();
            oi.order = null;
            oi.product = null;
            order.orderItemList << oi;
        };
        redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${order.orgration.id}","bean",objectMapper.writeValueAsString(order));
        redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${order.orgration.id}","orderTotalBuyerNum",order.orderTotalBuyerNum as String);
        redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${order.orgration.id}","orderTotalAmount",order.orderTotalAmount as String);
        redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${order.orgration.id}","orderTotalQutityNum",order.orderTotalQutityNum as String);
        redisUtil.expireAt("tuanOrder_${tuanInfo.productId}_${order.orgration.id}",exDate);
    }

    //一个社群下购买该产品的会员
    void buildTheTuanOrgrationBuyers(ObjectMapper objectMapper,TuanInfo tuanInfo,Order order)
    {
        Date exDate = (new JDateTime(tuanInfo.endDate)).addDay(1).convertToDate();
        orderBean.queryOrderBuyers8Order(order.id)?.each {ob->
            ob.cancelLazyEr();
            ob.product = null;
            ob.order = null;
            ob.order.orgration = null;
            ob.buyer.description = null;
            redisUtil.lRightPush("tuanBuyers_${tuanInfo.productId}_${order.orgration.id}",objectMapper.writeValueAsString(ob));
        }
        redisUtil.expireAt("tuanBuyers_${tuanInfo.productId}_${order.orgration.id}",exDate);
    }

    //在redis中存储这个社群正有哪些商品在参与团购
    void buildProductsTuaning8TheOrgration(ObjectMapper objectMapper,String orgrationId)
    {
        Date exDate = (new JDateTime(new Date())).addDay(1).convertToDate();
        orderBean.products8Tuan(orgrationId,null,null,true,false)?.each {
            redisUtil.zAdd("orgProductTuaning_${orgrationId}",objectMapper.writeValueAsString(["productId":it[2],"productName":it[3],"productImg":it[4],"orderTotalOrgNum":it[5],"orderTotalQutityNum":it[6]]),0 as double);
        }
        redisUtil.expireAt("orgProductTuaning_${orgrationId}",exDate);
    }

    void buildTuanOrgrationOrderBuyers(ObjectMapper objectMapper,TuanInfo tuanInfo)
    {
        Date exDate = (new JDateTime(tuanInfo.endDate)).addDay(1).convertToDate();
        orderBean.queryTuan8ProductOrders(tuanInfo.id,null)?.each {o->
            o.cancelLazyEr();
            o.orderBuyersList = new ArrayList<OrderBuyers>();
            orderBean.queryOrderBuyers8Order(o.id)?.each {ob->
                ob.cancelLazyEr();
                redisUtil.lRightPush("tuanBuyers_${tuanInfo.productId}_${o.orgration.id}",objectMapper.writeValueAsString(ob));
            }
            redisUtil.expireAt("tuanBuyers_${tuanInfo.productId}_${o.orgration.id}",exDate);
            redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${order.orgration.id}",objectMapper.writeValueAsString(o));
            redisUtil.expireAt("tuanOrder_${tuanInfo.productId}_${o.orgration.id}",exDate);
        };
    }

    @Transactional
    void processTuanOrder(Map<String,Object> query)
    {
        Product product = orderBean.findObjectById(Product.class,query.productId);
        ObjectMapper objectMapper = new ObjectMapper();

        //从redis是否有商品的TuanInfo，有就从redis读取，没有就查询数据库（数据库没有就建立，写进redis）
        TuanInfo tuanInfo = null;
        if (redisUtil.hExists("tuan_${query.productId}","bean"))
        {
            tuanInfo = objectMapper.readValue(redisUtil.hGet("tuan_${query.productId}","bean"),TuanInfo.class);
        }
        else
        {
            List<TuanInfo> tuanInfoList = orderBean.queryTuan8Product(query.productId,false);
            if (tuanInfoList==null || tuanInfoList.size()<1)
            {
                tuanInfo = orderBean.createTuanInfo(product);
            }
            else
            {
                tuanInfo = tuanInfoList[0];
            }
            //redis
            buildRedisOrderTuan(objectMapper,tuanInfo);
            //redis end
        }

        //从redis是否有商品的Order，有就从redis读取，没有就查询数据库（数据库没有就建立，写进redis）
        Order order = null;
        if(redisUtil.hExists("tuanOrder_${tuanInfo.productId}_${query.orgrationId}","bean"))
        {
            order = objectMapper.readValue(redisUtil.hGet("tuanOrder_${tuanInfo.productId}_${query.orgrationId}","bean"),Order.class);
        }
        else
        {
            List<Order> orderList = orderBean.queryTuan8ProductOrders(tuanInfo.id,query.orgrationId);
            if (orderList==null || orderList.size()<1)
            {
                order = orderBean.createTuanOrder(tuanInfo.productId,tuanInfo.id,query.buyerId,query.orgrationId,query.dailiNo,query.ids,query.specImg,query.price as int,query.deliveryQuantity as int);
            }
            else
            {
                order = orderList[0];
            }
            //redis
            buildTheTuanOrgrationOrder(objectMapper,tuanInfo,order);
            //redis end
        }

        if (!redisUtil.hasKey("tuanBuyers_${tuanInfo.productId}_${order.orgration.id}"))
        {
            buildTheTuanOrgrationBuyers(new ObjectMapper(),tuanInfo,order);
        }
        //redis
        redisUtil.lRightPush("tuanBuyers_${tuanInfo.productId}_${order.orgration.id}",objectMapper.writeValueAsString(order.orderBuyersList[0]));
        tuanInfo.orderTotalBuyerNum = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalBuyerNum");
        tuanInfo.orderTotalAmount = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalAmount");
        tuanInfo.orderTotalQutityNum = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalQutityNum");
        redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalBuyerNum",(tuanInfo.orderTotalBuyerNum + 1) as String);
        redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalAmount",(tuanInfo.orderTotalAmount + ((query.deliveryQuantity as int) * (query.price as int))) as String);
        redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalQutityNum",(tuanInfo.orderTotalQutityNum + (query.deliveryQuantity as int)) as String);
        //redis end
    }

    void buildRedisTwiterList(ObjectMapper objectMapper,String orgrationId)
    {
        Date exDate = (new JDateTime(new Date())).addDay(10).convertToDate();
        twiterBean.queryTwiterList(orgrationId,0,500)?.content?.each{t->
            redisUtil.zAdd("twiter4OrgZset_${orgrationId}",t.id,t.createDate.time / 1000000000000);
            t.twiterImagesList.each{tm->
                tm.twiter = null;
            }
            if (!redisUtil.hasKey("twiter_${t.id}"))
            {
                redisUtil.hPut("twiter_${t.id}","imgs",objectMapper.writeValueAsString(t.twiterImagesList));
                t.twiterImagesList = null;
                redisUtil.hPut("twiter_${t.id}","bean",objectMapper.writeValueAsString(t));
                redisUtil.hPut("twiter_${t.id}","twiterDetailCount","${t.twiterDetailCount}");
                redisUtil.expireAt("twiter_${t.id}",exDate);
            }
            if (!redisUtil.hasKey("twiterPlList_${t.id}"))
            {
                twiterBean.queryTwiterDetailsList(t.id,0,500)?.content?.each{td->
                    td.twiterDetailsImagesList = null;
                    td.twiter = null;
                    td.buyerOrgration?.cancelLazyEr();
                    td.distBuyerOrgration?.cancelLazyEr();
                    redisUtil.lRightPush("twiterPlList_${t.id}",objectMapper.writeValueAsString(td));
                }
                redisUtil.expireAt("twiterPlList_${t.id}",exDate);
            }
            if (!redisUtil.hasKey("twiterZanSet_${t.id}"))
            {
                twiterBean.queryTwiterZanList(t.id).each {zan->
                    redisUtil.sAdd("twiterZanSet_${t.id}",zan.buyerId);
                }
                redisUtil.expireAt("twiterZanSet_${t.id}",exDate);
            }
        };
        redisUtil.expireAt("twiter4OrgZset_${orgrationId}",exDate);
    }

    void buildToken2Redis()
    {
        userBean.queryObject("select pw from PayWayInfoEntity as pw")?.each {pw->
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","appId",pw.payWayInfoEntityPK.appId ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","appName",pw.appName ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","doMain",pw.doMain ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","mchId",pw.mchId ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","notiUrl",pw.notiUrl ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","partnerKey",pw.partnerKey ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","appSecret",pw.appSecret ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","certPath",pw.certPath ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","isServer",pw.isServer as String);
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","subAppId",pw.subAppId ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","subMchId",pw.subMchId ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","body",pw.body ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","returnDoMain",pw.returnDoMain ?: "");
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","type",pw.payWayInfoEntityPK.type as String);
            redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","createDate",DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));

            if (!(pw.certPath in [null,""]) && pw.payWayInfoEntityPK.type == (1 as byte))
            {
                println "-------------initMchKeyStore ${pw.payWayInfoEntityPK.appId} begin-----------------";
                LocalHttpClient.initMchKeyStore(pw.mchId,pw.certPath);
                println "-------------initMchKeyStore ${pw.payWayInfoEntityPK.appId} end-----------------";
            }

            switch (pw.payWayInfoEntityPK.type as byte)
            {
                case 0 as byte:
                    try
                    {
                        Token token = TokenAPI.token(pw.payWayInfoEntityPK.appId,pw.appSecret);
                        redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","accessToken",token?.getAccess_token());
                        Ticket t = TicketAPI.ticketGetticket(token.getAccess_token());
                        redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","jsTicket",t?.getTicket());
                    }
                    catch (e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 1 as byte:
                    try
                    {
                        Token token = TokenAPI.token(pw.payWayInfoEntityPK.appId,pw.appSecret);
                        redisUtil.hPut("appToken_${pw.payWayInfoEntityPK.appId}_${pw.payWayInfoEntityPK.type}","accessToken",token?.getAccess_token());
                    }
                    catch (e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    String ganTokenValue(String appId,byte type,String field)
    {
        return redisUtil.hGet("appToken_${appId}_${type}",field) as String;
    }

    void runPayOrderArray()
    {
        try
        {
            ScheduledBean.runPayOrderFlag = true;
            while (true)
            {
                PayReturnOrder payReturnOrder = orderBean.findObjectById(PayReturnOrder.class,redisUtil.lBRightPopAndLeftPush("pay_array","pay_array_runed",0 as long, TimeUnit.SECONDS));
                processBigTuanPay(payReturnOrder);
                redisUtil.lRemove("pay_array_runed",0 as long,payReturnOrder.id);
            }
        }
        catch(e)
        {
            ScheduledBean.runPayOrderFlag = false;
            e.printStackTrace();
        }
    }

    @Transactional
    void processBigTuanPay(PayReturnOrder payReturnOrder)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        OSSClient ossClient = OtherUtils.genOSSClient();
        def jsonSlpuer = new JsonSlurper();
        def pobj = jsonSlpuer.parseText(IOUtils.toString(ossClient.getObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"system/json/product/${payReturnOrder.productId}.json").objectContent,"utf-8"));
        Product product = new Product();
        product.id = pobj.productInfo.productId;
        product.name = pobj.productInfo.productName;
        product.tgEndDays = pobj.productInfo.tgEndDays;
        product.masterImg = pobj.images.mImg;
        product.productPrivater = new ProductsPrivater();
        product.productPrivater.id = pobj.productInfo.productPrivater.privaterId;
        payReturnOrder.tempMap = [:];
        payReturnOrder.tempMap["privaterId"] = product.productPrivater.id;
        payReturnOrder.tempMap["productName"] = product.name;

        //从redis是否有商品的TuanInfo，有就从redis读取，没有就查询数据库（数据库没有就建立，写进redis）
        TuanInfo tuanInfo = null;
        if (redisUtil.hExists("tuan_${payReturnOrder.productId}","bean"))
        {
            tuanInfo = objectMapper.readValue(redisUtil.hGet("tuan_${payReturnOrder.productId}","bean"),TuanInfo.class);
            tuanInfo.orderTotalBuyerNum = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalBuyerNum") as int;
            tuanInfo.orderTotalAmount = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalAmount") as int;
            tuanInfo.orderTotalQutityNum = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalQutityNum") as int;
            tuanInfo.orderTotalOrgNum = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalOrgNum") as int;
            tuanInfo.orderTotalBuyerNum = tuanInfo.orderTotalBuyerNum + 1;
            tuanInfo.orderTotalAmount = tuanInfo.orderTotalAmount + payReturnOrder.paymentFee;
            tuanInfo.orderTotalQutityNum = tuanInfo.orderTotalQutityNum + payReturnOrder.deliveryQuantity;
            tuanInfo.tempMap = [:];
            tuanInfo.tempMap["updateDatabase"] = false;
            tuanInfo.tempMap["updateRedis"] = false;
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
                    tuanInfo.orderTotalBuyerNum = tuanInfo.orderTotalBuyerNum + 1;
                    tuanInfo.orderTotalAmount = tuanInfo.orderTotalAmount + payReturnOrder.paymentFee;
                    tuanInfo.orderTotalQutityNum = tuanInfo.orderTotalQutityNum + payReturnOrder.deliveryQuantity;
                    tuanInfo.tempMap = [:];
                    tuanInfo.tempMap["updateDatabase"] = false;
                    tuanInfo.tempMap["updateRedis"] = false;
                }
            }
            if (tuanInfo==null)
            {
                product.tempMap = [:];
                product.tempMap["orderTotalAmount"] = payReturnOrder.paymentFee;
                product.tempMap["orderTotalBuyerNum"] = 1;
                product.tempMap["orderTotalQutityNum"] = payReturnOrder.deliveryQuantity;
                product.tempMap["orderTotalOrgNum"] = 1;
                tuanInfo = orderBean.createTuanInfo(product);
                tuanInfo.tempMap = [:];
                tuanInfo.tempMap["updateRedis"] = false;
                tuanInfo.tempMap["updateDatabase"] = true;
            }
        }
        //redis
        this.buildRedisOrderTuan(objectMapper,tuanInfo);
        tuanInfo.tempMap["updateRedis"] = true;
        //redis end

        //放进redis中存储这个社群正有哪些商品在参与团购
        if (!redisUtil.hasKey("orgProductTuaning_${payReturnOrder.orgrationId}"))
        {
            this.buildProductsTuaning8TheOrgration(objectMapper,payReturnOrder.orgrationId);
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
            order.tempMap = [:];
            order.tempMap["isCreate"] = false;
        }
        else
        {
            List<Order> orderList = orderBean.queryTuan8ProductOrders(tuanInfo.id,payReturnOrder.orgrationId,1 as byte,1 as byte);
            //是否存在该商品的社群订单
            if (orderList==null || orderList.size()<1)
            {
                order = orderBean.createTuanOrder(payReturnOrder,tuanInfo.id);
                order.cancelLazyEr();
                order.orgration.buyDesc = "";
                order.orgration.description = "";
                order.orgration.manageDesc = "";
                order.tempMap = [:];
                order.tempMap["isCreate"] = true;
                redisUtil.zAdd("orgProductTuaning_${payReturnOrder.orgrationId}",
                        objectMapper.writeValueAsString(["productId":product.id,"productName":product.name,"productImg":product.masterImg]), 0 as double);
            }
            else
            {
                order = orderList[0];
                order.cancelLazyEr();
                order.tempMap = [:];
            }
            //redis
            this.buildTheTuanOrgrationOrder(objectMapper,tuanInfo,order);
            //redis end
        }

//                        为了减少redis的体积，决定不存放
//                                if (!redisUtil.hasKey("tuanBuyers_${tuanInfo.productId}_${order.orgration.id}"))
//                                {
//                                    redisApi.buildTheTuanOrgrationBuyers(new ObjectMapper(),tuanInfo,order);
//                                }
        //redis
        OrderBuyers orderBuyers = orderBean.joinTuan8Org(tuanInfo.id,order.id,payReturnOrder,order.tempMap["isCreate"]);
        orderBuyers.cancelLazyEr();
        orderBuyers.order.orgration = null;
        orderBuyers.order.buyer = null;
        orderBuyers.product.brand = null;
        orderBuyers.product.productPrivater = null;
//                                redisUtil.lRightPush("tuanBuyers_${tuanInfo.productId}_${order.orgration.id}",objectMapper.writeValueAsString(orderBuyers));
//                                redisUtil.expireAt("tuanBuyers_${tuanInfo.productId}_${order.orgration.id}",(new JDateTime(tuanInfo.endDate)).addDay(1).convertToDate());
        //计算平台该商品团购的汇总
        if (!order.tempMap["isCreate"])
        {
            //计算当前社群该商品团购的汇总
            order.orderTotalBuyerNum = redisUtil.hGet("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalBuyerNum") as int;
            order.orderTotalQutityNum = redisUtil.hGet("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalQutityNum") as int;
            order.orderTotalAmount = redisUtil.hGet("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalAmount") as int;
            //一个没有找到的bug，组织汇总数量会少1，第一个参与团购的信息会没有汇总，用下面的if临时补救
            println """orderTotalQutityNum:${order.orderTotalQutityNum};orderTotalAmount:${order.orderTotalAmount};orderTotalBuyerNum:${order.orderTotalBuyerNum}""";
            if (order.orderTotalQutityNum<0 || order.orderTotalQutityNum==null)
            {
                order.orderTotalBuyerNum = 0;
                order.orderTotalQutityNum = 0;
                order.orderTotalAmount = 0;
            }
            redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalBuyerNum",(order.orderTotalBuyerNum + 1) as String);
            redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalAmount",(order.orderTotalAmount + ((payReturnOrder.deliveryQuantity as int) * (payReturnOrder.price as int))) as String);
            redisUtil.hPut("tuanOrder_${tuanInfo.productId}_${payReturnOrder.orgrationId}","orderTotalQutityNum",(order.orderTotalQutityNum + (payReturnOrder.deliveryQuantity as int)) as String);
        }
        else
        {
            // 如果是社群新开团商品，使参与社群数+1
            if (!tuanInfo.tempMap["updateDatabase"])
            {
                tuanInfo.orderTotalOrgNum = redisUtil.hGet("tuan_${tuanInfo.productId}","orderTotalOrgNum") as int;
                tuanInfo.orderTotalOrgNum = tuanInfo.orderTotalOrgNum + 1;
                redisUtil.hPut("tuan_${tuanInfo.productId}","orderTotalOrgNum",tuanInfo.orderTotalOrgNum as String);
            }

            // 如果是社群新开团商品，发送一个商品的Twiter
            BuyerAppInfo buyerAppInfo = null;
            if (redisUtil.hExists("buyer_${payReturnOrder.buyerId}","appInfo_${payReturnOrder.appId}"))
            {
                buyerAppInfo = objectMapper.readValue(redisUtil.hGet("buyer_${payReturnOrder.buyerId}","appInfo_${payReturnOrder.appId}"),BuyerAppInfo.class);
            }
            else
            {
                buyerAppInfo = orderBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(payReturnOrder.appId,payReturnOrder.buyerId));
            }

            Twiter twiter = new Twiter();
            twiter.id = MathUtil.getPNewId();
            twiter.url = pobj.productInfo.productId;
            twiter.description = pobj.productInfo.productName + "； 我在社群发起了团购，欢迎大家跟进";
            twiter.twiterType = 2 as byte;
            twiter.buyerOrgration = new BuyerOrgration();
            twiter.buyerOrgration.buyerOrgrationPK = new BuyerOrgrationPK(payReturnOrder.orgrationId,payReturnOrder.buyerId);
            twiter.buyerOrgration.headImg = buyerAppInfo.headImgUrl;
            twiter.buyerOrgration.niName = buyerAppInfo.wxNickName;
            twiter.createDate = new Date();
            twiter.modifyDate = twiter.createDate;

            TwiterImages twiterImages = new TwiterImages();
            twiterImages.id = MathUtil.getPNewId();
            twiterImages.twiter = twiter;
            twiterImages.mediaType = 0 as byte;
            twiterImages.path = pobj.images.mImg;
            twiterImages.twiter = twiter;
            twiter.twiterImagesList = new ArrayList();
            List tiList = new ArrayList();
            twiter.twiterImagesList << twiterImages;
            tiList << twiterImages;
            twiterBean.updateTwiter(twiter);
            twiter.cancelLazyEr();
            twiter.twiterImagesList = tiList;

            if (redisUtil.hasKey("twiter4OrgZset_${twiter.buyerOrgration.buyerOrgrationPK.orgrationId}"))
            {
                Date exDate = (new JDateTime(new Date())).addDay(10).convertToDate();
                redisUtil.zAdd("twiter4OrgZset_${twiter.buyerOrgration.buyerOrgrationPK.orgrationId}",twiter.id,twiter.createDate.time / 1000000000000);
                redisUtil.hPut("twiter_${twiter.id}","imgs",objectMapper.writeValueAsString(twiter.twiterImagesList));
                twiter.twiterImagesList = null;
                redisUtil.hPut("twiter_${twiter.id}","bean",objectMapper.writeValueAsString(twiter));
                redisUtil.hPut("twiter_${twiter.id}","twiterDetailCount",twiter.twiterDetailCount as String);
                redisUtil.expireAt("twiter_${twiter.id}",exDate);
            }
        }

        //检查redis tuan_xxxxx 中的信息，如果没有存入数据库，则写进数据库
        if (!tuanInfo.tempMap["updateDatabase"])
        {
            orderBean.updateTheObject(tuanInfo);
            tuanInfo.tempMap["updateDatabase"] = true;
        }

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
    }

    void buildAliYunSts2Redis()
    {
        Map stsMap = OtherUtils.genOssAccessKey();
        redisUtil.hPut("aliyun_sts","expiration",stsMap["expiration"]);
        redisUtil.hPut("aliyun_sts","accessId",stsMap["accessId"]);
        redisUtil.hPut("aliyun_sts","accessKey",stsMap["accessKey"]);
        redisUtil.hPut("aliyun_sts","securityToken",stsMap["securityToken"]);
        redisUtil.hPut("aliyun_sts","requestId",stsMap["requestId"]);
        redisUtil.hPut("aliyun_sts","bucketUrl","https://${OtherUtils.givePropsValue( "ali_oss_bucketName")}.${OtherUtils.givePropsValue("ali_oss_endPoint")}");
    }

    String ganAliYunStsValue(String field)
    {
        return redisUtil.hGet("aliyun_sts",field) as String;
    }

    @Transactional
    void test()
    {
        redisUtil.lLeftPush("test","ttttt");
        Buyer buyer = orderBean.findObjectById(Buyer.class,"13268990066");
        buyer.loginName = "justin";
        orderBean.updateObject(buyer);
        println 100 / 0;
    }
}
