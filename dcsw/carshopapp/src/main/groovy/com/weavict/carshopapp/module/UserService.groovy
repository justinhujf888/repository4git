package com.weavict.carshopapp.module

import com.weavict.carshopapp.entity.BuyerShop
import com.weavict.carshopapp.entity.BuyerShopPK
import com.weavict.carshopapp.entity.User
import com.weavict.carshopapp.entity.UserShop
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("userService")
class UserService extends ModuleBean
{
    User queryTheUser8WxId(String wxId)
    {
        this.queryObject("select u from User u where u.wxid = :wxid",["wxid":wxId])?.get(0);
    }

    @Transactional
    void addUser2Shop(User user,UserShop userShop)
    {
        this.updateObject(userShop);
        User u = this.findObjectById(User.class,user.phone);
        if (u!=null)
        {
            u.name = user.name;
            u.description = user.description;
        }
        this.updateObject(u);
    }

    @Transactional
    void addBuyer2Shop(User user,BuyerShop buyerShop)
    {
        this.updateObject(buyerShop);
    }

    @Transactional
    void delBuyerShop(BuyerShopPK buyerShopPK)
    {
        this.executeEQL("delete BuyerShop where buyerShopPK = :buyerShopPK",["buyerShopPK":buyerShopPK]);
    }

    List queryUsers8TheShop(String shopId,String userId,byte status)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select u.phone,u.name,us.zhiwei,u.description,u.imgPath,us.status,us.rulestatus from usershop us left join users u on us.user_id = u.phone where shop_id = :shopId";
        paramsMap["shopId"] = shopId;
        if (!(userId in [null,""]))
        {
            sbf << " and u.phone = :userId";
            paramsMap["userId"] = userId;
        }
        if (status > (-1 as byte))
        {
            if (status == 0 as byte)
            {
                sbf << " and (us.status = 0 or us.status = 2)";
            }
            else
            {
                sbf << " and us.status = :status";
                paramsMap["status"] = status;
            }
        }
        return this.createNativeQuery4Params(sbf.toString(),paramsMap).getResultList();
    }

    List queryCars8TheBuyer(String buyerId)
    {
        return this.queryObject("select car from Car car where car.user.phone = :buyerId",["buyerId":buyerId]);
    }

    List queryBuyers8TheShop(String shopId)
    {
        return this.createNativeQuery4Params("select b.phone,b.createdate,b.description,b.name,b.wxid,b.wxopenid,us.remark,us.name as usname,us.type as ustype,us.buyer_id as buyerid,us.contectPhone,us.contectName from buyershop us left join users b on us.buyer_id = b.phone where us.shop_id = :shopId",["shopId":shopId]).getResultList();
    }
}
