package com.weavict.carshopapp.module

import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import com.weavict.carshopapp.entity.BuyerShop
import com.weavict.carshopapp.entity.BuyerShopPK
import com.weavict.carshopapp.entity.Car
import com.weavict.carshopapp.entity.Order
import com.weavict.carshopapp.entity.OrderItem
import com.weavict.carshopapp.entity.PayReturnOrder
import com.weavict.carshopapp.entity.ReportYear
import com.weavict.carshopapp.entity.ReportYearPK
import com.weavict.carshopapp.entity.Shop
import com.weavict.carshopapp.entity.User
import jodd.datetime.JDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("orderService")
class OrderService extends ModuleBean
{
    Map queryShopOrderList(String shopId,String buyerId,String buyerName,String carNumber,String companyName,String brand, String xingHao,int runedKl,byte type, byte status,Date beginDate, Date endDate)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
//        if (!(shopId in [null,""]))
//        {
//            sbf << " and o.shop.id = :shopId";
//            paramsMap["shopId"] = shopId;
//        }
        sbf << " and o.shop.id = :shopId";
        paramsMap["shopId"] = shopId;
        if (!(buyerId in [null,""]))
        {
            sbf << " and o.user.phone = :buyerId";
            paramsMap["buyerId"] = buyerId;
        }
        if (!(buyerName in [null,""]))
        {
            sbf << " and o.buyerName like :buyerName";
            paramsMap["buyerName"] = "%$buyerName%" as String;;
        }
        if (!(carNumber in [null,""]))
        {
            sbf << " and o.carNumber like :carNumber";
            paramsMap["carNumber"] = "%$carNumber%" as String;
        }
        if (!(companyName in [null,""]))
        {
            sbf << " and o.companyName like :companyName";
            paramsMap["companyName"] = "%$companyName%" as String;
        }
        if (!(brand in [null,""]))
        {
            sbf << " and o.brand = :brand";
            paramsMap["brand"] = brand;
        }
        if (!(xingHao in [null,""]))
        {
            sbf << " and o.xingHao = :xingHao";
            paramsMap["xingHao"] = xingHao;
        }
        if (runedKl > 0)
        {
            sbf << " and o.runedKl >= :runedKl";
            paramsMap["runedKl"] = runedKl;
        }
        if (type > -1)
        {
            sbf << " and o.type = :type";
            paramsMap["type"] = type;
        }
        if (status > -1)
        {
            sbf << " and o.status = :status";
            paramsMap["status"] = status;
        }
        if (beginDate!=null && endDate!=null)
        {
            sbf << " and o.inDate between :beginDate and :endDate";
            paramsMap["beginDate"] = beginDate;
            paramsMap["endDate"] = endDate;
        }
        return ["orderList":this.queryObject("select o from Order o where 1=1 ${sbf.toString()} order by o.createDate desc",paramsMap),
                "orderSum":this.queryObject("select sum(orderTotalCbAmount),sum(orderTotalAmount),sum(orderPayAmount) from Order o where 1=1 ${sbf.toString()}",paramsMap)];
    }

    List<OrderItem> queryOrderItemList8Order(String orderId)
    {
        return this.queryObject("select oi from OrderItem oi where oi.order.id = :orderId",["orderId":orderId]);
    }

    @Transactional
    Order updateTheOrder(Order order)
    {
        try
        {
            if (order.id in [null,""])
            {
                order.id = MathUtil.getPNewId();
                order.createDate = new Date();
            }
            Order o = new Order();
            o.id = order.id;
            o.description = order.description;
            o.createDate = order.createDate;
            o.inDate = order.inDate;
            o.outDate = order.outDate;
            o.brand = order.brand;
            o.xingHao = order.xingHao;
            o.carNumber = order.carNumber;
            o.buyerName = order.buyerName;
            o.companyId = order.companyId;
            o.companyName = order.companyName;
            o.runedKl = order.runedKl;
            o.wxName = order.wxName;
            o.wxPhone = order.wxPhone;
            o.wxZhiWei = order.wxZhiWei;
            o.status = order.status;
            o.type = order.type;
            if (this.findObjectById(User.class,order.user.phone)==null)
            {
                order.user.createDate = new Date();
                order.user = this.updateObject(order.user);
            }
            BuyerShop buyerShop = new BuyerShop();
            buyerShop.buyerShopPK = new BuyerShopPK(order.user.phone,order.shop.id,order.tempMap.buyerType as byte);
            buyerShop.name = order.buyerName;
            buyerShop.contectPhone = order.companyId;
            buyerShop.contectName = order.companyName;
            this.updateObject(buyerShop);

            List<Car> carList = this.queryObject("select car from Car car where car.carNumber = :carNumber",["carNumber":o.carNumber]);
            if (carList==null || carList.size()<1)
            {
                Car car = new Car();
                car.id = MathUtil.getPNewId();
                car.carNumber = o.carNumber;
                car.brand = o.brand;
                car.xingHao = o.xingHao;
                car.runedKl = o.runedKl;
                car.user = order.user;
                if (o.type==1)
                {
                    car.preRunedKl = o.runedKl;
                    car.preByDate = new Date();
                }
                else
                {
                    car.preRunedKl = 0;
                }
                this.updateObject(car);
            }
            else
            {
                carList?.each {car->
                    if (o.type==1)
                    {
                        car.preRunedKl = o.runedKl;
                        car.preByDate = new Date();
                    }
                    this.updateObject(car);
                }
            }
            o.user = order.user;
            o.shop = order.shop;
            o.orderPayAmount = order.orderPayAmount;
            o.orderTotalAmount = 0;
            o.orderTotalCbAmount = 0;
            order.orderItemList?.each {oil->
                if (oil.temp!="deled")
                {
                    o.orderTotalAmount = o.orderTotalAmount + (oil.deliveryQuantity * oil.price);
                    o.orderTotalCbAmount = o.orderTotalCbAmount + (oil.deliveryQuantity * oil.cbPrice);
                }
            }
            o =this.updateObject(o);
            order.orderItemList?.each {oil->
                if (oil.temp!="deled")
                {
                    oil.price = oil.price;
                    oil.cbPrice = oil.cbPrice;
                    oil.order = o;
                    this.updateObject(oil);
                }
                else
                {
                    if (this.findObjectById(OrderItem.class,oil.id)!=null)
                    {
                        this.executeEQL("delete OrderItem where id = :id",["id":oil.id]);
                    }
                }
            }
            if (this.findObjectById(ReportYear.class,new ReportYearPK(o.shop.id,DateUtil.format(o.inDate,"yyyy"),DateUtil.format(o.inDate,"MM")))==null)
            {
                this.createNativeQuery4Params("""
insert into reportyear (shop_id,year,month,ordertotalcbamount,ordertotalamount,unorderpaycount) values (
  :shopId,:year,:month,
  (select coalesce(sum(ordertotalcbamount),0) from orders where shop_id = :shopId and status=1 and to_char(indate,'YYYY') = :year and to_char(indate,'MM') = :month),
  (select coalesce(sum(ordertotalamount),0) from orders where shop_id = :shopId and status=1 and to_char(indate,'YYYY') = :year and to_char(indate,'MM') = :month),
  (select count(id) from orders where shop_id = :shopId and status=0 and to_char(indate,'YYYY') = :year and to_char(indate,'MM') = :month)
)
""",["shopId":o.shop.id,"year":DateUtil.format(o.inDate,"yyyy"),"month":DateUtil.format(o.inDate,"MM")]).executeUpdate();
            }
            else
            {
                this.createNativeQuery4Params("""
update reportyear set ordertotalcbamount = (select coalesce(sum(ordertotalcbamount),0) from orders where shop_id = :shopId and status=1 and to_char(indate,'YYYY') = :year and to_char(indate,'MM') = :month),
  ordertotalamount = (select coalesce(sum(ordertotalamount),0) from orders where shop_id = :shopId and status=1 and to_char(indate,'YYYY') = :year and to_char(indate,'MM') = :month),
  unorderpaycount = (select count(id) from orders where shop_id = :shopId and status=0 and to_char(indate,'YYYY') = :year and to_char(indate,'MM') = :month)
  where shop_id = :shopId and year = :year and month = :month
""",["shopId":o.shop.id,"year":DateUtil.format(o.inDate,"yyyy"),"month":DateUtil.format(o.inDate,"MM")]).executeUpdate();
            }
            return o;
        }
        catch (e)
        {
            e.printStackTrace()
            throw e;
        }
    }

    List<ReportYear> queryReportYear8Shop(String shopId,String year,String month)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select r from ReportYear r where r.reportYearPK.shopId = :shopId";
        paramsMap["shopId"] = shopId;
        if (!(year in [null,""]))
        {
            sbf << " and r.reportYearPK.year = :year";
            paramsMap["year"] = year;
        }
        if (!(month in [null,""]))
        {
            sbf << " and r.reportYearPK.month = :month";
            paramsMap["month"] = month;
        }
        sbf << " order by r.reportYearPK.month";
        this.queryObject(sbf.toString(),paramsMap);
    }

    List queryLikeWord4Choice(String shopId,String word)
    {
        return this.createNativeQuery4Params("""
select distinct * from (select b.phone,b.name,us.name as usname,us.type as ustype,us.buyer_id as buyerid,us.contectPhone,us.contectName,c.carnumber,c.brand,c.xinghao from buyershop us left join users b  on us.buyer_id = b.phone left join car c on c.user_phone = b.phone where us.shop_id = :shopId and (c.carnumber like upper(:word) or us.buyer_id like :word or b.phone like :word or us.contectphone like :word or b.name like :word or us.name like :word)
union
select b.phone,b.name,b.name,-1,b.phone,o.companyId,o.companyName,o.carnumber,o.brand,o.xinghao from orders o left join users b on b.phone = o.user_phone where shop_id = :shopId and (carnumber like upper(:word) or b.phone like :word or b.name like :word)) as t
""",["shopId":shopId,"word":"%${word}%" as String]).getResultList();
    }

    @Transactional
    PayReturnOrder preOrderGoingToPay(PayReturnOrder payReturnOrder)
    {
        payReturnOrder.createDate = new Date();
        payReturnOrder.paymentStatus = 0 as byte;
        payReturnOrder.id = MathUtil.getPNewId();
        payReturnOrder.paymentFee = payReturnOrder.price;
        return this.updateObject(payReturnOrder);
    }

    @Transactional
    void payedReturnOrderProcess(PayReturnOrder payReturnOrder)
    {
        JDateTime beginDate = new JDateTime(new Date());
        this.executeEQL("update Shop set preDate = :preDate,nextDate = :nextDate where id = :id",["id":payReturnOrder.productId,"preDate":beginDate.convertToDate(),"nextDate":beginDate.addDay((payReturnOrder.specId as int)*366).convertToDate()]);
        this.executeEQL("update PayReturnOrder set paymentStatus = :paymentStatus,tradeNo = :tradeNo,payReturnDatas = :payReturnDatas where id = :id",["id":payReturnOrder.id,"tradeNo":payReturnOrder.tradeNo,"paymentStatus": (1 as byte),"payReturnDatas":payReturnOrder.payReturnDatas]);
    }
}
