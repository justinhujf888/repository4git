package com.weavict.edu.rest


import com.aliyun.oss.OSSClient
import com.aliyun.oss.model.CannedAccessControlList
import com.aliyuncs.CommonRequest
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.IAcsClient
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.edu.entity.Brand
import com.weavict.edu.entity.Buyer
import com.weavict.edu.entity.BuyerAppInfo
import com.weavict.edu.entity.BuyerAppInfoPK
import com.weavict.edu.entity.BuyerOrgration
import com.weavict.edu.entity.BuyerOrgrationPK
import com.weavict.edu.entity.Orgration
import com.weavict.edu.entity.ProductsPrivater
import com.weavict.edu.entity.ProductsPrivaterImages
import com.weavict.edu.entity.Rules
import com.weavict.edu.entity.UserShop
import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import com.weavict.edu.module.RedisApi
import com.weavict.edu.module.UserBean
import com.weavict.edu.redis.RedisUtil
import com.weavict.website.common.OtherUtils
import com.yicker.utility.DES
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

@Path("/user")
class UserRest extends BaseRest
{
    @Autowired
    UserBean userBean;

    @Autowired
    RedisApi redisApi

    @Autowired
    RedisUtil redisUtil;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/privaterList4User")
    String privaterList4User(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "privaterList":({
                        return userBean.privaterList4User(query.userId)?.each {
                            it.cancelLazyEr();
                        };
                     }).call()]);
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
    @Path("/registerPrivater")
    String registerProductsPrivater(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductsPrivater pp = objToBean(query.productsPrivater, ProductsPrivater.class, objectMapper);
//        println pp.dump();
            pp = userBean.registerTheProductsPrivater(pp);
            return objectMapper.writeValueAsString(
                    ["status"    : "OK",
                     registerInfo: (
                             {
                                 ["productsPrivater": ({
                                     pp.cancelLazyEr();
                                     return pp;
                                 }).call()
                                 ]
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
    @Path("/privaterLogin")
    String productsPrivaterLogin(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "loginInfo":({
                         ProductsPrivater pp = userBean.checkProductsPrivaterLogin(query.loginInfo.loginName,query.loginInfo.password);
                         if (pp!=null)
                         {
                             pp.cancelLazyEr();
                             return [allow:true,privater:pp];
                         }
                         else
                         {
                             return [allow:false,privater:[]];
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
    @Path("/thePrivater")
    String theProductsPrivater(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "privater":({
                         ProductsPrivater privater = userBean.findObjectById(ProductsPrivater.class,query.privaterId);
                         if (!(privater.orgrationId in [null,""]))
                         {
                             Orgration org = userBean.findObjectById(Orgration.class,privater.orgrationId);
                             privater.tempMap = [:];
                             privater.tempMap["orgArea"] = org.area;
                             privater.tempMap["orgAddress"] = org.address;
                             privater.tempMap["orgName"] = org.name;
                         }
                         privater?.cancelLazyEr();
                         return privater;
                     }).call()]);
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
    @Path("/updatePrivater")
    String updateProductsPrivater(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductsPrivater pp = objToBean(query.productsPrivater, ProductsPrivater.class, objectMapper);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "privater":({
                         ProductsPrivater privater = userBean.findObjectById(ProductsPrivater.class,pp.id);
                         privater.name = pp.name;
                         privater.phone = pp.phone;
                         privater.tel = pp.tel;
                         privater.area = pp.area;
                         privater.address = pp.address;
                         privater.description = pp.description;
                         privater.wxid = pp.wxid;
                         privater.lat = pp.lat;
                         privater.lng = pp.lng;
                         privater.orgrationId = pp.orgrationId;
                         privater.productListType = pp.productListType;
                         userBean.updateTheObject(privater);
                         privater.cancelLazyEr();
                         privater.productsPrivaterImagesList = userBean.queryPrivaterImages(privater.id)?.each {pimg->
                             pimg.productsPrivater = null;
                         };
                         privater.tempMap = [:];
                         Orgration org = null;
                         if (!(privater.orgrationId in [null,""]))
                         {
                             org = userBean.findObjectById(Orgration.class,privater.orgrationId);
                             privater.tempMap["orgName"] = org?.name;
                         }
                         // oss
                         OSSClient ossClient = OtherUtils.genOSSClient();
                         ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"privater/${privater.id}/shop.json",new ByteArrayInputStream(objectMapper.writeValueAsString(({
                             return privater;
                         }).call()).getBytes("UTF-8")));
                         ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "privater/${privater.id}/shop.json", CannedAccessControlList.PublicRead);
                         List l = userBean.genOrgPrivaters(privater.orgrationId);
                         if (l!=null && l.size()>0 && org!=null)
                         {
                             ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"org/${privater.orgrationId}/orgPrivaters.json",new ByteArrayInputStream(objectMapper.writeValueAsString(({
                                 return l;
                             }).call()).getBytes("UTF-8")));
                             ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "org/${privater.orgrationId}/orgPrivaters.json", CannedAccessControlList.PublicRead);
                         }
                         ossClient.shutdown();
                         // oss end
                         return privater;
                     }).call()]);
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
    @Path("/addPrivaterImg")
    String addPrivaterImg(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductsPrivaterImages img = objToBean(query.productsPrivaterImages, ProductsPrivaterImages.class, objectMapper);
            userBean.addProductsPrivaterImages(img);
            return objectMapper.writeValueAsString(
                    ["status":"OK"]);
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
    @Path("/privaterImgs")
    String privaterImgs(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "imgList":({
                         List l = userBean.queryPrivaterImages(query.privaterId)?.each {p->
                             p.cancelLazyEr();
                         };
                         if (l==null)
                         {
                             return new ArrayList<ProductsPrivaterImages>();
                         }
                         else
                         {
                             return l;
                         }
                     }).call()]);
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
    @Path("/delPrivaterImg")
    String delPrivaterImg(@RequestBody Map<String,Object> query)
    {
        try
        {
            //oss
            OSSClient ossClient = OtherUtils.genOSSClient();
            ossClient.deleteObject(OtherUtils.givePropsValue("ali_oss_bucketName"), query.imagePath);
            ossClient.shutdown();
            //oss end
            userBean.delThePrivaterImage(query.imgId);
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
    @Path("/shops8TheUser")
    String shops8TheUser(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     privaterList:(
                             {
                                 List<ProductsPrivater> shopList = userBean.queryShops8TheUser(query.appId,query.userId);
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
    @Path("/users8TheShop")
    String users8TheShop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "usersInfo":({
                         return userBean.queryUsers8TheShop(query.privaterId,query.userId,query.status as byte);
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
    @Path("/addUser2TheShop")
    String addUser2TheShop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Buyer buyer = this.objToBean(query.user,Buyer.class,objectMapper);
            buyer = userBean.findObjectById(Buyer.class,buyer.phone);
            if (buyer==null)
            {
                return """{"status":"FA_USERNULL"}""";
            }
            UserShop userShop = this.objToBean(query.userShop,UserShop.class,objectMapper);
            userBean.addUser2Shop(userShop);
            OSSClient ossClient = OtherUtils.genOSSClient();
            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "privater/${userShop.userShopPK.shopId}/shopUsers.json", new ByteArrayInputStream(objectMapper.writeValueAsString(
                    ({
                        return userBean.queryUsers8TheShop(userShop.userShopPK.shopId,null,0 as byte);
                    }).call()
            ).getBytes("UTF-8")));
            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "privater/${userShop.userShopPK.shopId}/shopUsers.json", CannedAccessControlList.PublicRead);
            ossClient.shutdown();
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
    @Path("/rules8TheShop")
    String rules8TheShop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "rulesInfo":({
                         return userBean.queryRuels8TheShop(query.privaterId,query.zxId);
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
    @Path("/updateTheRules")
    String updateTheRules(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Rules rules = this.objToBean(query.rules,Rules.class,objectMapper);
            if (rules.id in [null,""])
            {
                rules.id = MathUtil.getPNewId();
            }
            userBean.updateTheObject(rules);
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
    @Path("/privaterBrands")
    String privaterBrands(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "brandList":({
                         List l = userBean.queryBrands4Privater(query.privaterId)?.each {b->
                             b.cancelLazyEr();
                         };
                         if (l==null)
                         {
                             return new ArrayList<Brand>();
                         }
                         else
                         {
                             return l;
                         }
                     }).call()]);
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
    @Path("/addPrivaterBrand")
    String addPrivaterBrand(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Brand brand = objToBean(query.privaterBrand, Brand.class, objectMapper);
            brand = userBean.updateTheBrand(brand);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "brand":({
                        brand?.cancelLazyEr();
                        return brand;
                    }).call()]);
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
    @Path("/editBrandLogo")
    String editBrandLogo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Brand brand = objToBean(query.privaterBrand, Brand.class, objectMapper);
            if (query.method.equals("del"))
            {
                //oss
                OSSClient ossClient = OtherUtils.genOSSClient();
                ossClient.deleteObject(OtherUtils.givePropsValue("ali_oss_bucketName"), brand.logo);
                ossClient.shutdown();
                //oss end
                brand.logo = "";
            }
            userBean.updateTheBrandLogo(brand.id,brand.logo);
            return objectMapper.writeValueAsString(
                    ["status":"OK"]);
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
    @Path("/thePrivaterBrand")
    String thePrivaterBrand(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "brand":({
                         Brand brand = userBean.findObjectById(Brand.class,query.brandId);
                         brand?.cancelLazyEr();
                         return brand;
                     }).call()]);
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
    @Path("/registBuyer")
    String registBuyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper()
            Buyer buyer = objToBean(query.buyer, Buyer.class, objectMapper);
            if (userBean.findObjectById(Buyer.class,buyer.phone)!=null)
            {
                return """{"status":"ER_HAS"}""";
            }
            userBean.registBuyer(buyer);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "buyerInfo":({
                         return buyer;
                     }).call(),
                     "loginToken":({
                         //redis
                         redisUtil.hPut("buyer_${buyer.phone}","token",MathUtil.getPNewId());
                         return redisUtil.hGet("buyer_${buyer.phone}","token");
                         //redis end
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
    @Path("/resetBuyerPassword")
    String resetBuyerPassword(@RequestBody Map<String,Object> query)
    {
        try
        {
            BuyerAppInfo buyerAppInfo = userBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(query.appId,query.phone));
            if (buyerAppInfo==null)
            {
                return """{"status":"ER_NOHAS"}""";
            }
            userBean.ressetBuyerPassword(query.appId,query.phone,query.password);
            buyerAppInfo.password = query.password;
            buyerAppInfo.cancelLazyEr();
            ObjectMapper objectMapper = new ObjectMapper();
            redisUtil.hPut("buyer_${query.phone}","appInfo_${query.appId}",objectMapper.writeValueAsString(buyerAppInfo));
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
    @Path("/buyerLogin")
    String buyerLogin(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            if (!redisUtil.hExists("buyer_${query.phone}","bean") || !redisUtil.hExists("buyer_${query.phone}","appInfo_${query.appId}") || !redisUtil.hasKey("buyerOrgrationList_${query.phone}"))
            {
                redisUtil.delete("buyer_${query.phone}");
                redisApi.buildRedisBuyer(objectMapper,query.phone,"bean");
                redisApi.buildRedisBuyer(objectMapper,query.phone,"buyerOrgration");
                redisApi.buildRedisBuyer(objectMapper,query.phone,"buyerAppInfo");
            }
            if (!redisUtil.hExists("buyer_${query.phone}","bean"))
            {
                return """{"status":"ER_NOHAS"}""";
            }
//            Buyer buyer = userBean.findObjectById(Buyer.class,query.phone);

            BuyerAppInfo buyerAppInfo = objectMapper.readValue(redisUtil.hGet("buyer_${query.phone}","appInfo_${query.appId}"),BuyerAppInfo.class);
            println query.password;
            println buyerAppInfo.password;
            if (buyerAppInfo==null)
            {
                return """{"status":"ER_NOHAS"}""";
            }
            if (!query.password.equals(buyerAppInfo.password))
            {
                return """{"status":"ER_PW"}""";
            }
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "buyerAppInfo":({
                         return buyerAppInfo;
                     }).call(),
                     "loginToken":({
                         //redis
                         redisUtil.hPut("buyer_${query.phone}","token",MathUtil.getPNewId());
                         return redisUtil.hGet("buyer_${query.phone}","token");
                         //redis end
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
    @Path("/buyerLogin4WxUniId")
    String buyerLogin4WxUniId(@RequestBody Map<String,Object> query)
    {
        try
        {

            ObjectMapper objectMapper = new ObjectMapper();
            if (!redisUtil.hExists("buyer_${query.phone}","bean"))
            {
                redisApi.buildRedisBuyer(objectMapper,query.phone,"bean");
                redisApi.buildRedisBuyer(objectMapper,query.phone,"buyerAppInfo");
                redisApi.buildRedisBuyer(objectMapper,query.phone,"buyerOrgration");
            }
//            Buyer buyer = userBean.findObjectById(Buyer.class,query.phone);
            Buyer buyer = objectMapper.readValue(redisUtil.hGet("buyer_${query.phone}","bean"),Buyer.class);
            if (buyer==null)
            {
                return """{"status":"ER_NOHAS"}""";
            }
            if (!query.password.equals(buyer.password))
            {
                return """{"status":"ER_PW"}""";
            }
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "buyerInfo":({
                         return buyer;
                     }).call(),
                     "loginToken":({
                         //redis
                         redisUtil.hPut("buyer_${query.phone}","token",MathUtil.getPNewId());
                         return redisUtil.hGet("buyer_${query.phone}","token");
                         //redis end
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
    @Path("/checkPhone")
    String checkPhone(@RequestBody Map<String,Object> query)
    {
        try
        {
            Buyer buyer = userBean.findObjectById(Buyer.class,query.phone);
            buyer?.cancelLazyEr();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "buyerInfo":({
                        return buyer;
                     }).call(),
                     "isNull":({
                         return buyer==null;
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
    @Path("/sendSms")
    String sendSms(@RequestBody Map<String,Object> query)
    {
        try
        {
            String vcode = userBean.phoneCode();
            String templateCode = "";
            if (query.accessCode.equals("regist"))
            {
                templateCode = "SMS_169175064";
            }
            ObjectMapper objectMapper = new ObjectMapper();
            DefaultProfile profile = DefaultProfile.getProfile("default", OtherUtils.givePropsValue("ali_sms_AccessKeyId"), OtherUtils.givePropsValue("ali_sms_AccessKeySecret"));
            IAcsClient client = new DefaultAcsClient(profile);
            CommonRequest request = new CommonRequest();
            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("PhoneNumbers", query.phone);
            request.putQueryParameter("SignName", OtherUtils.givePropsValue("ali_sms_SignName"));
            request.putQueryParameter("TemplateCode", "${templateCode}");
            request.putQueryParameter("TemplateParam", """{"code":"${vcode}"}""");
            request.putQueryParameter("SendDate", DateUtil.format(new Date(),"yyyy-MM-dd"));
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "smsInfo":["returnInfo":({
                         return client.getCommonResponse(request);;
                     }).call(),
                    "number":({
                        DES crypt = new DES(OtherUtils.givePropsValue("publickey"));
                        return crypt.encrypt(vcode);
                    }).call()]
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
    @Path("/buyerOrgration8Buyer")
    String buyerOrgration8Buyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "buyerOrgration":({
                         //redis
                         if (!redisUtil.hExists("buyer_${query.buyerId}","bean"))
                         {
                             redisApi.buildRedisBuyer(objectMapper,query.buyerId,"bean");
                             redisApi.buildRedisBuyer(objectMapper,query.buyerId,"buyerAppInfo");
                         }
                         else if (!redisUtil.hExists("buyer_${query.buyerId}","obBean_${query.orgrationId}"))
                         {
                             redisApi.buildRedisBuyer(objectMapper,query.buyerId,"buyerOrgration");
                         }
                         BuyerAppInfo buyerAppInfo = objectMapper.readValue(redisUtil.hGet("buyer_${query.buyerId}","appInfo_${query.appId}"),BuyerAppInfo.class);
                         BuyerOrgration buyerOrgration = objectMapper.readValue(redisUtil.hGet("buyer_${query.buyerId}","obBean_${query.orgrationId}"),BuyerOrgration.class);
                         buyerOrgration.tempMap = [:];
                         buyerOrgration.tempMap["workCompany"] = buyerAppInfo.workCompany;
                         buyerOrgration.niName = buyerAppInfo.wxNickName;
                         buyerOrgration.description = buyerAppInfo.description;
                         buyerOrgration.headImg = buyerAppInfo.headImgUrl;
                         return buyerOrgration;
                         //redis end
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
    @Path("/orgrationList8Buyer")
    String orgrationList8Buyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "orgrationList":({
                         List l = new ArrayList();
                         //redis
                         if (!redisUtil.hExists("buyer_${query.buyerId}","bean"))
                         {
                             redisApi.buildRedisBuyer(objectMapper,query.buyerId,"bean");
                             redisApi.buildRedisBuyer(objectMapper,query.buyerId,"buyerOrgration");
                             redisApi.buildRedisBuyer(objectMapper,query.buyerId,"buyerAppInfo");
                         }
                         Buyer buyer = objectMapper.readValue(redisUtil.hGet("buyer_${query.buyerId}","bean"),Buyer.class);
                         BuyerAppInfo buyerAppInfo = objectMapper.readValue(redisUtil.hGet("buyer_${query.buyerId}","appInfo_${query.appId}"),BuyerAppInfo.class);
                         redisUtil.lRange("buyerOrgrationList_${query.buyerId}",0l,-1l).each {
                             if (!redisUtil.hExists("buyer_${query.buyerId}","obBean_${it}")) {
                                 redisApi.buildRedisBuyer(objectMapper,query.buyerId,"buyerOrgration");
                             }
                             BuyerOrgration o = objectMapper.readValue(redisUtil.hGet("buyer_${query.buyerId}","obBean_${it}"),BuyerOrgration.class);
                             if (!redisUtil.hExists("orgration_${o.buyerOrgrationPK.orgrationId}","bean"))
                             {
                                 redisApi.buildRedisOrgration(objectMapper,o.buyerOrgrationPK.orgrationId,"bean");
                             }
                             Orgration orgration = objectMapper.readValue(redisUtil.hGet("orgration_${o.buyerOrgrationPK.orgrationId}","bean"),Orgration.class);
                             l << ["id":orgration.id,"name":orgration.name,"tel":orgration.tel,"mImage":orgration.mImage,
                                   "area":orgration.area,"address":orgration.address,"buyerOrgArea":o.area,"buyerOrgAddress":o.address,"buyerOrgLatitude":o.latitude,"buyerOrgLongitude":o.longitude,"description":orgration.description,
                                   "createDate":DateUtil.format(orgration.createDate,"yyyy-MM-dd HH:mm:ss"),
                                   "bcreateDate":DateUtil.format(o.createDate,"yyyy-MM-dd HH:mm:ss"),
                                   "buyDesc":orgration.buyDesc,"manageDesc":orgration.manageDesc,"isManager":o.isManager,"isCreater":o.isCreater,
                                   "buyerId":buyer.phone,"niName":buyerAppInfo.wxNickName,"headImg":buyerAppInfo.headImgUrl,"buyerStatus":o.status,"buyerOrgDesc":buyerAppInfo.description,"workCompany":buyerAppInfo.workCompany];
                         }
                         //redis end
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
    @Path("/queryTheBuyer")
    String queryTheBuyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "buyer": ({
                         Buyer buyer = userBean.findObjectById(Buyer.class,query.phone);
                         buyer.cancelLazyEr();
                         buyer.loginName = "";
                         buyer.password = "";
                         BuyerAppInfo buyerAppInfo = userBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(query.appId,buyer.phone));
                         if (buyerAppInfo!=null)
                         {
                             buyerAppInfo.cancelLazyEr();
                             buyer.wxNickEm = buyerAppInfo.wxNickEm;
                             buyer.wxNickName = buyerAppInfo.wxNickName;
                             buyer.wxopenid = buyerAppInfo.wxopenid;
                             buyer.wxid = buyerAppInfo.wxid;
                             buyer.amb = buyerAppInfo.amb;
                             buyer.tempMap = [:];
                             buyer.tempMap["money"] = buyerAppInfo.money;
                         }
                         return buyer;
                     }).call()
                    ]);
        }
        catch(Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryTheBuyerInRs")
    String queryTheBuyerInRs(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            println query.appId;
            println query.buyerId;
            if (!redisUtil.hExists("buyer_${query.buyerId}","appInfo_${query.appId}"))
            {
                redisApi.buildRedisBuyer(objectMapper,query.buyerId,"buyerAppInfo");
            }
            BuyerAppInfo buyerAppInfo = objectMapper.readValue(redisUtil.hGet("buyer_${query.buyerId}","appInfo_${query.appId}"),BuyerAppInfo.class);
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "buyerAppInfo": ({
                         return buyerAppInfo;
                     }).call()
                    ]);
        }
        catch(Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryTheBuyerAppInfo")
    String queryTheBuyerAppInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "buyerAppInfo": ({
                         return userBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(query.appId,query.buyerId));
                     }).call()
                    ]);
        }
        catch(Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryTheBuyerAppInfoByOpenId")
    String queryTheBuyerAppInfoByOpenId(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "buyerAppInfoList": ({
                         return userBean.queryTheBuyer8WxUniId(query.appId,query.openId);
                     }).call()
                    ]);
        }
        catch(Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }

    }



    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/updateTheBuyerAppInfo")
    String updateTheBuyerAppInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            BuyerAppInfo buyerAppInfo = objToBean(query.buyerAppInfo, BuyerAppInfo.class, objectMapper);
            userBean.updateTheObject(buyerAppInfo);
            redisUtil.hPut("buyer_${buyerAppInfo.buyerAppInfoPK.buyerId}","appInfo_${buyerAppInfo.buyerAppInfoPK.appId}",objectMapper.writeValueAsString(buyerAppInfo));
            return """{"status":"OK"}""";
        }
        catch(Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }

    }


//    用于微信小程序
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/editBuyer")
    String editBuyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper()
            Buyer buyer = objToBean(query.buyer, Buyer.class, objectMapper);
            println buyer.dump();
            Buyer by = userBean.findObjectById(Buyer.class,buyer.phone);
            by.area = buyer.area;
            by.address = buyer.address;
            by.sex = buyer.sex;
            by.wxNickName = buyer.wxNickName;
            by.wxNickEm = buyer.wxNickEm;
            by.headImgUrl = "buyer/${by.phone}/m.jpg";
            by.loginName = buyer.loginName;
            by.password = buyer.password;
            by.wxid = buyer.wxid;
            by.name = buyer.name;
            by.tel = buyer.tel;
            by.description = buyer.description;
            by.wxopenid = buyer.wxopenid;
            by.amb = buyer.amb;
            userBean.updateTheObject(by);

            BuyerAppInfo buyerAppInfo = userBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(query.appId,by.phone));
            //按逻辑是不用判断的，因为BuyerAppInfo是后面加的，有些buyer可能没有
            if (buyerAppInfo==null)
            {
                buyerAppInfo.buyerAppInfoPK = new BuyerAppInfoPK(query.appId,by.phone);
                buyerAppInfo.password = by.password;
                buyerAppInfo.loginName = by.loginName;
                buyerAppInfo.wxopenid = by.wxopenid;
                buyerAppInfo.wxid = by.wxid;
                buyerAppInfo.amb = by.amb;
            }


            buyerAppInfo.wxNickName = by.wxNickName;
            buyerAppInfo.wxNickEm = by.wxNickEm;
            buyerAppInfo.headImgUrl = by.headImgUrl;

            userBean.updateTheObject(buyerAppInfo);

            BuyerOrgration buyerOrgration = new BuyerOrgration();
            buyerOrgration.niName = by.wxNickName;
            buyerOrgration.headImg = by.headImgUrl;
            buyerOrgration.buyerOrgrationPK = new BuyerOrgrationPK();
            buyerOrgration.buyerOrgrationPK.buyerId = by.phone;
            userBean.updateOrgrationBuyerBaseInfo(buyerOrgration);
//            不直接update buyer的原因是orgrationList join关系映射造成的错误

//            oss begin 将HTTP的图片保存到OSS
//            微信小程序更新了获取方式，改为从前端上传到oss
//            println buyer.headImgUrl;
//            OSSClient ossClient = OtherUtils.genOSSClient();
//            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), by.headImgUrl, new ByteArrayInputStream(HttpUtil.downloadBytes(buyer.headImgUrl)));
//            ossClient.shutdown();
//            oss end

//            redis begin
//            因为不能删除
//            redisUtil.delete("buyer_${by.phone}");
            redisUtil.hDelete("buyer_${by.phone}","bean");
            redisUtil.hDelete("buyer_${by.phone}","buyerAppInfo");
            redisUtil.delete("buyerOrgrationList_${by.phone}");
            redisApi.buildRedisBuyer(objectMapper,by.phone,"bean");
            redisApi.buildRedisBuyer(objectMapper,by.phone,"buyerAppInfo");
            redisApi.buildRedisBuyer(objectMapper,by.phone,"buyerOrgration");
//            redis end
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
    @Path("/createOrgration")
    String createOrgration(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Orgration orgration = objToBean(query.orgration, Orgration.class, objectMapper);
            BuyerOrgration buyerOrgration = objToBean(query.buyerOrgration, BuyerOrgration.class, objectMapper);
            if (userBean.queryOrgrationCount8Name(orgration.name) > 0)
            {
                return """{"status":"FA_HAS"}""";
            }
            orgration.id = MathUtil.getPNewId();
            orgration.createDate = new Date();
            buyerOrgration.buyerOrgrationPK.orgrationId = orgration.id;
            buyerOrgration.status = 1 as byte;
            buyerOrgration.isCreater = true;
            buyerOrgration.isManager = true;
            buyerOrgration.createDate = orgration.createDate;
            userBean.createOrgration(orgration,buyerOrgration);
            //redis begin
            redisUtil.hPut("orgration_${orgration.id}","bean",objectMapper.writeValueAsString(
                    ({
                        return orgration;
                    }).call()
            ));
            if (!redisUtil.hasKey("orgrationBuyers_${orgration.id}"))
            {
                redisUtil.lRemove("orgrationBuyers_${orgration.id}",0 as long,buyerOrgration.buyerOrgrationPK.buyerId);
                redisUtil.lRightPush("orgrationBuyers_${orgration.id}",buyerOrgration.buyerOrgrationPK.buyerId);
            }
            else
            {
                redisApi.buildRedisBuyers8Orgration(objectMapper,orgration.id);
            }

            if (!redisUtil.hasKey("buyerOrgrationList_${buyerOrgration.buyerOrgrationPK.buyerId}"))
            {
//                obBean_xxxxxxx   在redisApi.buildRedisBuyer中有生成
                redisApi.buildRedisBuyer(objectMapper,buyerOrgration.buyerOrgrationPK.buyerId,"buyerOrgration");
            }
            else
            {
                redisUtil.hPut("buyer_${buyerOrgration.buyerOrgrationPK.buyerId}","obBean_${buyerOrgration.buyerOrgrationPK.orgrationId}",objectMapper.writeValueAsString(buyerOrgration));
                redisUtil.lRemove("buyerOrgrationList_${buyerOrgration.buyerOrgrationPK.buyerId}",0,orgration.id);
                redisUtil.lRightPush("buyerOrgrationList_${buyerOrgration.buyerOrgrationPK.buyerId}",orgration.id);
            }
            //redis end
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
    @Path("/buildOrgrationJson")
    String buildOrgrationJson(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            // oss
            OSSClient ossClient = OtherUtils.genOSSClient();
            userBean.queryOrgrationList()?.each {o->
                ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "org/${o.id}/org.json", new ByteArrayInputStream(objectMapper.writeValueAsString(
                        ({
                            o.cancelLazyEr();
                            return o;
                        }).call()
                ).getBytes("UTF-8")));
                ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "org/${o.id}/org.json", CannedAccessControlList.PublicRead);
            }

            List l = userBean.genOrgPrivaters(query.orgrationId);
            if (l!=null && l.size()>0)
            {
                ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"org/${query.orgrationId}/orgPrivaters.json",new ByteArrayInputStream(objectMapper.writeValueAsString(({
                    return l;
                }).call()).getBytes("UTF-8")));
                ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "org/${query.orgrationId}/orgPrivaters.json", CannedAccessControlList.PublicRead);
            }
            ossClient.shutdown();
            //oss end
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
    @Path("/orgrationList8Area")
    String orgrationList8Area(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "orgrationList":({
                         return userBean.queryOrgration8Area(query.area)?.each {
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
    @Path("/joinOrgration")
    String joinOrgration(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            BuyerOrgration buyerOrgration = objToBean(query.buyerOrgration, BuyerOrgration.class, objectMapper);
            BuyerAppInfo buyerAppInfo = userBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(query.currentAppId,buyerOrgration.buyerOrgrationPK.buyerId));
            buyerOrgration.headImg = buyerAppInfo.headImgUrl;
            buyerOrgration.niName = buyerAppInfo.wxNickName;
            userBean.updateOrgrationBuyerBaseInfo(buyerOrgration);
//            buyerOrgration.status = 0 as byte;//如果不用审核，直接通过，需要修改状态；注释的原因是status由前端定义
            BuyerOrgration bo = userBean.findObjectById(BuyerOrgration.class,new BuyerOrgrationPK(buyerOrgration.buyerOrgrationPK.orgrationId,buyerOrgration.buyerOrgrationPK.buyerId));
            if (bo==null)
            {
                buyerOrgration.createDate = new Date();
            }
            else
            {
                buyerOrgration.isCreater = bo.isCreater;
                buyerOrgration.isManager = bo.isManager;
            }
            userBean.updateTheObject(buyerOrgration);

            //redis 会员通过扫码加入社群，还需要审核通过才能加入社群，以下注释为不需要审核，直接加入社群，论需求而定（已经修改成了直接通过，不需要审核）
            if (buyerOrgration.status==1)
            {
                if (!redisUtil.hasKey("orgrationBuyers_${buyerOrgration.buyerOrgrationPK.orgrationId}"))
                {
                    redisUtil.lRemove("orgrationBuyers_${buyerOrgration.buyerOrgrationPK.orgrationId}",0 as long,buyerOrgration.buyerOrgrationPK.buyerId);
                    redisUtil.lRightPush("orgrationBuyers_${buyerOrgration.buyerOrgrationPK.orgrationId}",buyerOrgration.buyerOrgrationPK.buyerId);
                }
                else
                {
                    redisApi.buildRedisBuyers8Orgration(objectMapper,buyerOrgration.buyerOrgrationPK.orgrationId);
                }

                if (!redisUtil.hasKey("buyerOrgrationList_${buyerOrgration.buyerOrgrationPK.buyerId}"))
                {
//                obBean_xxxxxxx   在redisApi.buildRedisBuyer中有生成
                    redisApi.buildRedisBuyer(objectMapper,buyerOrgration.buyerOrgrationPK.buyerId,"buyerOrgration");
                }
                else
                {
                    redisUtil.hPut("buyer_${buyerOrgration.buyerOrgrationPK.buyerId}","obBean_${buyerOrgration.buyerOrgrationPK.orgrationId}",objectMapper.writeValueAsString(buyerOrgration));
                    redisUtil.lRemove("buyerOrgrationList_${buyerOrgration.buyerOrgrationPK.buyerId}",0,buyerOrgration.buyerOrgrationPK.orgrationId);
                    redisUtil.lRightPush("buyerOrgrationList_${buyerOrgration.buyerOrgrationPK.buyerId}",buyerOrgration.buyerOrgrationPK.orgrationId);
                }
            }
            //redis end
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
    @Path("/buyers8Orgration")
    //用在管理及审核，所以不从redis中读取数据
    String buyers8Orgration(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "buyerList":({
                         if (!(query.page in [null,""]) && !(query.size in [null,""]))
                         {
                             return userBean.queryBuyers8Orgration(query.orgrationId,query.status as byte,query.page as int,query.size as int);
                         }
                         else
                         {
                             return userBean.queryBuyers8Orgration(query.orgrationId,query.status as byte);
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
    @Path("/buyers8OrgrationInRs")
    //从redis中读取数据
    String buyers8OrgrationInRs(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            if (!redisUtil.hasKey("orgrationBuyers_${query.orgrationId}"))
            {
                redisApi.buildRedisBuyers8Orgration(objectMapper,query.orgrationId);
            }
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "totalNum":redisUtil.lLen("orgrationBuyers_${query.orgrationId}"),
                     "buyerList":({
                         List l = new ArrayList();
                         long start = 0l;
                         long end = -1l;
                         if (!(query.page in [null,""]) && !(query.size in [null,""]))
                         {
                             start = (query.page as long) * (query.size as long);
                             end = start + (query.size as long) - 1l;
                         }
                         redisUtil.lRange("orgrationBuyers_${query.orgrationId}",start,end).each {
                             if (redisUtil.hExists("buyer_${it}","obBean_${query.orgrationId}"))
                             {
                                 l << objectMapper.readValue(redisUtil.hGet("buyer_${it}","obBean_${query.orgrationId}"),BuyerOrgration.class);
                             }
                         }
//                         println "orgrationBuyers_${query.orgrationId}";
//                         println "${start}-${end}";
//                         println l.dump();
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
    @Path("/updateOrgrationBuyerStatus")
    String updateOrgrationBuyerStatus(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            List<BuyerOrgration> buyerOrgrationList = objToBean(query.buyerOrgrationList, List.class, null);
            userBean.updateOrgrationBuyerStatus(buyerOrgrationList);
            //redis
            buyerOrgrationList.each {bo->
//                if (!redisUtil.hasKey("buyerOrgrationList_${bo.buyerOrgrationPK.buyerId}"))
//                {
//                    redisApi.buildRedisBuyer(objectMapper,bo.buyerOrgrationPK.buyerId,"buyerOrgration");
//                }
                redisUtil.hPut("buyer_${bo.buyerOrgrationPK.buyerId}","obBean_${bo.buyerOrgrationPK.orgrationId}",objectMapper.writeValueAsString(bo));
                redisUtil.lRemove("buyerOrgrationList_${bo.buyerOrgrationPK.buyerId}",0,bo.buyerOrgrationPK.orgrationId);
                redisUtil.lRightPush("buyerOrgrationList_${bo.buyerOrgrationPK.buyerId}",bo.buyerOrgrationPK.orgrationId);

                if (!redisUtil.hasKey("orgrationBuyers_${bo.buyerOrgrationPK.orgrationId}"))
                {
                    redisApi.buildRedisBuyers8Orgration(objectMapper,bo.buyerOrgrationPK.orgrationId);
                }
                else//因为在redisApi.buildRedisBuyers8Orgration中已经处理了所有status==1的情况，所以用else，
                {
                    redisUtil.lRemove("orgrationBuyers_${bo.buyerOrgrationPK.orgrationId}",0 as long,bo.buyerOrgrationPK.buyerId);
                    redisUtil.lRightPush("orgrationBuyers_${bo.buyerOrgrationPK.orgrationId}", bo.buyerOrgrationPK.buyerId);
                }
//                 经过检查，上面的代码已经放入了，所以下面的代码似乎没有必要了； redisUtil.hPut("buyer_${bo.buyerOrgrationPK.buyerId}","obBean_${bo.buyerOrgrationPK.orgrationId}",objectMapper.writeValueAsString(bo));
//                BuyerOrgration buyerOrgration = objectMapper.readValue(redisUtil.hGet("buyer_${bo.buyerOrgrationPK.buyerId}","obBean_${bo.buyerOrgrationPK.orgrationId}"),BuyerOrgration.class);
//                buyerOrgration.isCreater = bo.isCreater;
//                buyerOrgration.isManager = bo.isManager;
//                buyerOrgration.status = bo.status;
//                redisUtil.hPut("buyer_${bo.buyerOrgrationPK.buyerId}","obBean_${bo.buyerOrgrationPK.orgrationId}",objectMapper.writeValueAsString(buyerOrgration));
            }
            //redis end
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
    @Path("/updateOrgrationBuyer")
    String updateOrgrationBuyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            BuyerOrgration buyerOrgration = objToBean(query.buyerOrgration, BuyerOrgration.class, objectMapper);
            buyerOrgration = userBean.updateOrgrationBuyer(buyerOrgration);
            buyerOrgration.cancelLazyEr();
            //redis
//            redisUtil.delete("orgrationBuyers_${buyerOrgration.buyerOrgrationPK.orgrationId}");
//            redisUtil.hDelete("buyer_${buyerOrgration.buyerOrgrationPK.buyerId}","bean");
//            redisUtil.hDelete("buyer_${buyerOrgration.buyerOrgrationPK.buyerId}","buyerAppInfo");
//            if (!redisUtil.hasKey("buyerOrgrationList_${buyerOrgration.buyerOrgrationPK.buyerId}"))
//            {
//                redisApi.buildRedisBuyer(objectMapper,buyerOrgration.buyerOrgrationPK.buyerId,"buyerOrgration");
//            }
//            if (!redisUtil.hasKey("orgrationBuyers_${buyerOrgration.buyerOrgrationPK.orgrationId}"))
//            {
//                redisApi.buildRedisBuyers8Orgration(objectMapper,buyerOrgration.buyerOrgrationPK.orgrationId);
//            }
//            redisApi.buildRedisBuyer(objectMapper,buyerOrgration.buyerOrgrationPK.buyerId,"bean");
            redisApi.buildRedisBuyer(objectMapper,buyerOrgration.buyerOrgrationPK.buyerId,"buyerAppInfo");
            redisUtil.hPut("buyer_${buyerOrgration.buyerOrgrationPK.buyerId}","obBean_${buyerOrgration.buyerOrgrationPK.orgrationId}",objectMapper.writeValueAsString(buyerOrgration));

            //redis end
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
    @Path("/orgrationInfo")
    String orgrationInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            if (!redisUtil.hExists("orgration_${query.orgrationId}","bean"))
            {
                redisApi.buildRedisOrgration(objectMapper,query.orgrationId,"bean");
            }
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "orgration":({
                         Orgration orgration = objectMapper.readValue(redisUtil.hGet("orgration_${query.orgrationId}","bean"),Orgration.class);
                         return orgration;
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
    @Path("/updateOrgration")
    String updateOrgration(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Orgration orgration = objToBean(query.orgration, Orgration.class, objectMapper);
            userBean.updateTheObject(orgration);
            //redis
            redisUtil.hDelete("orgration_${orgration.id}","bean");
            //redis end
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
    @Path("/updateBuyerAmb")
    String updateBuyerAmb(@RequestBody Map<String,Object> query)
    {
        try
        {
            Buyer buyer = userBean.findObjectById(Buyer.class,query.phone);
            if (buyer==null)
            {
                buyer = new Buyer();
                buyer.phone = query.phone;
                buyer.createDate = new Date();
                userBean.updateTheObject(buyer);
            }
            BuyerAppInfo buyerAppInfo = userBean.findObjectById(BuyerAppInfo.class,new BuyerAppInfoPK(query.appId,query.phone));
            if (buyerAppInfo==null)
            {
                buyerAppInfo = new BuyerAppInfo();
                buyerAppInfo.buyerAppInfoPK = new BuyerAppInfoPK(query.appId,query.phone);
                buyerAppInfo.amb = query.amb as int;
            }
            else
            {
                buyerAppInfo.amb = buyerAppInfo.amb + (query.amb as int);
            }
            userBean.updateTheObject(buyerAppInfo);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
