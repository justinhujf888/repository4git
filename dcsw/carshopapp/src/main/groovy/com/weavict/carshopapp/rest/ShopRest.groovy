package com.weavict.carshopapp.rest

import com.aliyun.oss.OSSClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.DateUtil
import com.weavict.carshopapp.entity.Car
import com.weavict.carshopapp.entity.Shop
import com.weavict.carshopapp.entity.User
import com.weavict.carshopapp.module.ShopService
import com.weavict.website.common.OtherUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

@Path("/shop")
class ShopRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    ShopService shopService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/registTheShop")
    String registTheShop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            User user = this.objToBean(query.user,User.class,objectMapper);
            Shop shop = this.objToBean(query.shop,Shop.class,objectMapper);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "shopInfo":({
                         Shop s = shopService.registTheShop(user,shop);
                         s.cancelLazyEr();
                         return s;
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
    @Path("/shops8TheUser")
    String shops8TheUser(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     shopList:(
                             {
                                 List<Shop> shopList = new ArrayList();
                                shopService.queryShops8TheUser(query.userId)?.each {
                                    Shop shop = new Shop();
                                    shop.id = it[0];
                                    shop.name = it[1];
                                    shop.area = it[2];
                                    shop.address = it[3];
                                    shop.createDate = it[4];
                                    shop.description = it[5];
                                    shop.imgPath = it[6];
                                    shop.phone = it[7];
                                    shop.preDate = it[8];
                                    shop.status = it[9];
                                    shop.tel = it[10];
                                    shop.zxId = it[11];
                                    shop.temp = """{"usStatus":${it[12]},"usRuleStatus":${it[13]}}""";
                                    shopList << shop;
                                }
                                 return shopList;
                             }
                     ).call()
                    ]
            );
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
    @Path("/theShopInfo")
    String theShopInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     shopInfo:(
                             {
                                 Shop shop = shopService.findObjectById(Shop.class,query.shopId);
                                 shop.cancelLazyEr();
                                 return shop;
                             }
                     ).call()
                    ]
            );
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
    @Path("/queryShops4Map")
    String queryShops4Map(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     shopList:(
                             {
                                 List<Shop> shopList = new ArrayList();
                                 shopService.queryShops4Map(query.lat as String,query.lng as String,query.fw as String)?.each {
                                    Shop shop = new Shop();
                                     shop.id = it[0];
                                     shop.address = it[1];
                                     shop.area = it[2];
                                     shop.createDate = null;
                                     shop.description = it[4];
                                     shop.imgPath = it[5];
                                     shop.lat = it[6];
                                     shop.lng = it[7];
                                     shop.name = it[8];
                                     shop.nextDate = null;
                                     shop.phone = it[10];
                                     shop.preDate = null;
                                     shop.status = it[12] as byte;
                                     shop.tel = it[13];
                                     shop.zxId = it[14];
                                     shop.dailiNo = it[15];
                                     shopList << shop;
                                 }
                                 return shopList;
                             }
                     ).call()
                    ]
            );
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
    @Path("/editTheShop")
    String editTheShop(@RequestBody Map<String,Object> query)
    {
        try
        {
            shopService.updateTheObject(objToBean(query.shop,Shop.class,null));
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
    @Path("/editTheCar")
    String editTheCar(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     carInfo:(
                             {
                                 Car car = shopService.updateTheObject(objToBean(query.car, Car.class,null));
                                 car.cancelLazyEr();
                                 return car;
                             }
                     ).call()
                    ]
            );
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
    @Path("/queryShopImages8Shop")
    String queryShopImages8Shop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     shopImagesList:(
                             {
                                 return shopService.queryShopImages8Shop(query.shopId)?.each {
                                    it.cancelLazyEr();
                                 }
                             }
                     ).call()
                    ]
            );
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
    @Path("/updateShopImages")
    String updateShopImages(@RequestBody Map<String,Object> query)
    {
        try
        {
            Shop shop = objToBean(query.shop,Shop.class,null);
            shopService.updateShopImages(shop.shopImagesList);
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
    @Path("/delShopImages")
    String delShopImages(@RequestBody Map<String,Object> query)
    {
        try
        {
            //oss
            OSSClient ossClient = OtherUtils.genOSSClient();
            ossClient.deleteObject(OtherUtils.givePropsValue("ali_oss_bucketName"), query.imagePath);
            ossClient.shutdown();
            //oss end
            shopService.delShopImages(query.imageId);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
