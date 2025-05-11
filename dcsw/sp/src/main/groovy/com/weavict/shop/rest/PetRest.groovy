package com.weavict.shop.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.MathUtil
import com.weavict.shop.entity.*
import com.weavict.shop.module.PageUtil
import com.weavict.shop.module.PetBean
import com.weavict.shop.module.RedisApi
import com.weavict.shop.module.TwiterBean
import com.weavict.shop.redis.RedisUtil
import com.weavict.website.common.GPSHelper
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

import java.text.DateFormat
import java.text.FieldPosition
import java.text.ParsePosition
import java.text.SimpleDateFormat

/**
 * Created by Justin on 2018/6/10.
 */

@Path("/pet")
class PetRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    com.weavict.shop.module.ScheduledBean scheduledBean;

    @Autowired
    PetBean petService;

    @Autowired
    TwiterBean twiterService;

    @Autowired
    RedisApi redisApi

    @Autowired
    RedisUtil redisUtil;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/joinOrgration")
    String joinOrgration(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Orgration orgration = objToBean(query.orgration, Orgration.class, objectMapper);
            Orgration org = petService.findObjectById(Orgration.class,orgration.id);
            if (org!=null)
            {
                orgration = org;
            }
            else
            {
                petService.updateTheObject(orgration);
            }
            BuyerOrgration buyerOrgration = objToBean(query.buyerOrgration, BuyerOrgration.class, objectMapper);
            buyerOrgration.status = 1 as byte;//如果不用审核，直接通过，需要修改状态

            petService.updateTheObject(buyerOrgration);
            //redis 会员通过扫码加入社群，还需要审核通过才能加入社群，以下注释为不需要审核，直接加入社群，论需求而定
            if(!redisUtil.hasKey("orgrationBuyers_${buyerOrgration.buyerOrgrationPK.orgrationId}"))
            {
                redisApi.buildRedisBuyers8Orgration(objectMapper,buyerOrgration.buyerOrgrationPK.orgrationId);
            }
            redisUtil.lRightPush("orgrationBuyers_${buyerOrgration.buyerOrgrationPK.orgrationId}",buyerOrgration.buyerOrgrationPK.buyerId);
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
    @Path("/queryPetsList8Buyer")
    String queryPetsList8Buyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return objectMapper.writeValueAsString(
                    ["status"  : "OK",
                     "petsList": (
                             {
//                                 println objectMapper.writeValueAsString(petService.queryPetTypes(null));
                                 return petService.queryPetsList8Buyer(query.buyerId)?.each { p ->
                                     p.cancelLazyEr();
                                 };
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
    @Path("/queryPetsInfo")
    String queryPetsInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status"  : "OK",
                     "petsInfo": (
                             {
                                 Pets pets = petService.findObjectById(Pets.class,query.persId);
                                 pets.cancelLazyEr();
                                 return pets;
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
    @Path("/queryPetsMedicalInfo")
    String queryPetsMedicalInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status"  : "OK",
                     "petsMedicalInfo": (
                             {
                                 PetsMedical petsMedical = petService.findObjectById(PetsMedical.class,query.petsMedicalId);
                                 petsMedical.cancelLazyEr();
                                 petsMedical.petsMedicalImagesList = new ArrayList();
                                 petService.queryPetsMedicalImagesList(petsMedical.id)?.each {o->
                                     o.cancelLazyEr();
                                     petsMedical.petsMedicalImagesList << o;
                                 }
                                 return petsMedical;
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
    @Path("/queryPetsMedicalList8Pet")
    String queryPetsMedicalList8Pet(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status"         : "OK",
                     "petsMedicalList": (
                             {
                                 return petService.queryPetsMedicalList8Pet(query.petId)?.each { pm ->
                                     pm.cancelLazyEr();
                                     pm.petsMedicalImagesList = new ArrayList();
                                     petService.queryPetsMedicalImagesList(pm.id)?.each {img ->
                                         img.cancelLazyEr();
                                         pm.petsMedicalImagesList << img;
                                     }
                                 };
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
    @Path("/queryPetTypeList")
    String queryPetTypeList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
//            println objectMapper.writeValueAsString(petService.queryPetTypes(null));
            return objectMapper.writeValueAsString(
                    ["status"  : "OK",
                     "petTypes": (
                             {
                                 return petService.queryPetTypes(query.bType);
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
    @Path("/updatePet")
    String updatePet(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Pets pets = objToBean(query.pets, Pets.class, objectMapper);
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "pet"   : (
                             {
                                 if (pets.id in [null, ""])
                                 {
                                     pets.id = MathUtil.getPNewId();
                                     pets.createDate = new Date();
                                 }
                                 pets = petService.updateTheObject(pets);
                                 pets.cancelLazyEr();
                                 return pets;
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
    @Path("/updatePetsMedical")
    String updatePetsMedical(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            PetsMedical petsMedical = objToBean(query.petsMedical, PetsMedical.class, objectMapper);
            return objectMapper.writeValueAsString(
                    ["status"     : "OK",
                     "petsMedical": (
                             {
                                 petsMedical = petService.updatePetsMedical(petsMedical);
                                 petsMedical.cancelLazyEr();
                                 petsMedical.petsMedicalImagesList = new ArrayList();
                                 petService.queryPetsMedicalImagesList(petsMedical.id).each {img->
                                     img.cancelLazyEr();
                                     petsMedical.petsMedicalImagesList << img;
                                 }
                                 return petsMedical;
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
    @Path("/deleteThePetsMedicalImages")
    String deleteThePetsMedicalImages(@RequestBody Map<String,Object> query)
    {
        try
        {
            petService.deleteThePetsMedicalImages(query.imageId);
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
    @Path("/addThePetsMedicalImages")
    String addThePetsMedicalImages(@RequestBody Map<String,Object> query)
    {
        try
        {
            PetsMedicalImages petsMedicalImages = objToBean(query.petsMedicalImages,PetsMedicalImages.class,null);
            petsMedicalImages.id = MathUtil.getPNewId();
            petService.addTheObject(petsMedicalImages);
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
    @Path("/queryTwiterList4Pets")
    String queryTwiterList4Pets(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status"  : "OK",
                     "petsTwiterPageUtil": (
                             {
                                 List l = new ArrayList();
                                 PageUtil pageUtil = petService.queryTwiterList4Pets(query.petsId,query.currentPage,query.pageSize);
                                 pageUtil?.content?.each { pt ->
                                    l << ["twiterId":pt[0],"title":pt[1],"description":pt[2],"twiterType":pt[3],"createDate":pt[4],
                                    "orgId":pt[5],"twiterImagesList":({
                                        return twiterService.queryImages8Twiter(pt[0])?.each {o->
                                            o.cancelLazyEr();
                                        }
                                    }).call()];
                                 };
                                 pageUtil.content = l;
                                 return pageUtil;
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
    @Path("/addPetsTwiter")
    String addPetsTwiter(@RequestBody Map<String,Object> query)
    {
        try
        {
            PetsTwiter petsTwiter = objToBean(query.petsTwiter,PetsTwiter.class,null);
            petService.addTheObject(petsTwiter);
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
    @Path("/delPetsTwiter")
    String delPetsTwiter(@RequestBody Map<String,Object> query)
    {
        try
        {
            petService.deletePetsTwiter(query.twiterId);
            twiterService.delTwite(query.twiterId,query.twiterType as byte);
            //redis
            if (query.twiterType as byte != 11 as byte)
            {
                if (redisUtil.hasKey("twiter4OrgZset_${query.orgrationId}"))
                {
                    redisUtil.zRemove("twiter4OrgZset_${query.orgrationId}",query.twiterId);
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
    @Path("/editTwiterType2Privater")
    String editTwiterType2Privater(@RequestBody Map<String,Object> query)
    {
        try
        {
            petService.editTwiterType(query.twiterId,11 as byte);
            //redis
            redisUtil.zRemove("twiter4OrgZset_${query.orgrationId}",query.twiterId);
            redisUtil.delete("twiterZanSet_${query.twiterId}");
            redisUtil.delete("twiter_${query.twiterId}");
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
    @Path("/queryPetsGPS4Map")
    String queryPetsGPS4Map(@RequestBody Map<String,Object> query)
    {
        try
        {
            def jsonSlpuer = new JsonSlurper();
            def obj = jsonSlpuer.parseText(query.currentGps);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status"  : "OK",
                     "petsGPSInfo": (
                         {
                             List l = new ArrayList();
                             if (redisUtil.hasKey("pets_map"))
                             {
                                 redisUtil.hGetAll("pets_map").each {k,v->
                                     def o = jsonSlpuer.parseText(v);
                                     println GPSHelper.GetDistance(obj.longitude as double,obj.latitude as double,o.longitude as double,o.latitude as double);
                                     if (GPSHelper.GetDistance(obj.longitude as double,obj.latitude as double,o.longitude as double,o.latitude as double) <= 5000)
                                     {
                                         l << v;
                                     }
                                 }
                             }
                             return l;
                         }
                     ).call()
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
    @Path("/sendPetsGPS4Map")
    String sendPetsGPS4Map(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objToBean(query.petMaps,List.class,objectMapper)?.each {info->
                redisUtil.hPut("pets_map",info.id,objectMapper.writeValueAsString(info));
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
    @Path("/removePetsGPS4Map")
    String removePetsGPS4Map(@RequestBody Map<String,Object> query)
    {
        try
        {
            redisUtil.hDelete("pets_map",query.petMaps);
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
    @Path("/sendNotice")
    String sendNotice(@RequestBody Map<String,Object> query)
    {
        try
        {
            scheduledBean.petsFangyu();
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
    @Path("/queryPetsRecordInfoCurrentDay")
    String queryPetsRecordInfoCurrentDay(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Date currentDate = new Date();
            return objectMapper.writeValueAsString(
                    ["status"  : "OK",
                     "info": (
                             {
                                 return ["imageCount":petService.queryPetsRecordInfoCurrentDay(query.petsId,0 as byte,currentDate),
                                    "videoCount":petService.queryPetsRecordInfoCurrentDay(query.petsId,1 as byte,currentDate),
                                    "maxImage":0,"maxVideo":0
                                 ];
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
}
