package com.weavict.carshopapp.module


import com.weavict.common.util.MathUtil
import com.weavict.carshopapp.entity.Shop
import com.weavict.carshopapp.entity.ShopImages
import com.weavict.carshopapp.entity.User
import com.weavict.carshopapp.entity.UserShop
import com.weavict.carshopapp.entity.UserShopPK
import jodd.datetime.JDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("shopService")
class ShopService extends ModuleBean
{
    @Transactional
    Shop registTheShop(User user, Shop shop)
    {
//        user.createDate = new Date();
        if (shop.id in [null,""])
        {
            shop.id = MathUtil.getPNewId();
        }
        if (shop.zxId in [null,""])
        {
            shop.zxId = shop.id;
        }
        shop.createDate = new Date();
        shop.preDate = shop.createDate;

        JDateTime endJDate = (new JDateTime(shop.preDate)).addDay(92);
        shop.nextDate = endJDate.convertToDate();

        shop.status = 2 as byte;//试用
        UserShop userShop = new UserShop();
        userShop.userShopPK = new UserShopPK(user.phone,shop.id);
        userShop.status = 2 as byte;//注册管理员
        userShop.ruleStatus = 2 as byte;//老板
        userShop.zhiWei = "管理员";
        this.addObject(shop);
//        this.updateObject(user);
        this.addObject(userShop);
        return shop;
    }

    List<Shop> queryShops8TheUser(String userId)
    {
        return this.createNativeQuery4Params("""
select s.id,s.name,s.area,s.address,s.createdate,s.description,s.imgpath,s.phone,s.predate,s.status,s.tel,s.zxid,us.status as usstatus,us.rulestatus from usershop us left join shop s on us.shop_id = s.id where us.status != 1 and us.user_id = :userId
union
select s.id,s.name,s.area,s.address,s.createdate,s.description,s.imgpath,s.phone,s.predate,s.status,s.tel,s.zxid,2 as usstatus,2 as rulestatus from shop s where s.zxid in (select ss.zxid from usershop uus left join shop ss on uus.shop_id = ss.id where uus.status = 2 and uus.user_id = :userId)
""",["userId":userId]).getResultList();
    }

    List<ShopImages> queryShopImages8Shop(String shopId)
    {
        return this.queryObject("select si from ShopImages si where si.shop.id = :shopId",["shopId":shopId]);
    }

    List queryShops4Map(String lat,String lng,String fw)
    {
        return this.createNativeQuery4Params("""
select id,address,area,createdate,description,imgpath,lat,lng,name,nextdate,phone,predate,status,tel,zxid,dailino from shop where sqrt( ( ((:lng-to_number(lng,'999.9999999999999999'))*PI()*12656*cos(((:lat+to_number(lat,'999.9999999999999999'))/2)*PI()/180)/180) * ((:lng-to_number(lng,'999.9999999999999999'))*PI()*12656*cos (((:lat+to_number(lat,'999.9999999999999999'))/2)*PI()/180)/180) ) + ( ((:lat-to_number(lat,'999.9999999999999999'))*PI()*12656/180) * ((:lat-to_number(lat,'999.9999999999999999'))*PI()*12656/180) ) ) < :fw
""",["lat":lat as double,"lng":lng as double,"fw":fw as int]).getResultList();
    }

    @Transactional
    void updateShopImages(List<ShopImages> shopImagesList)
    {
        shopImagesList?.each {
            this.updateObject(it);
        }
    }

    @Transactional
    void delShopImages(String imageId)
    {
        this.executeEQL("delete ShopImages where id=:id", ["id":imageId]);
    }
}
