package com.weavict.edu.rest

import com.aliyun.oss.OSSClient
import com.aliyun.oss.model.CannedAccessControlList
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.edu.entity.BuyerBook
import com.weavict.edu.entity.BuyerBookPK
import com.weavict.edu.entity.BuyerStudy
import com.weavict.edu.entity.BuyerStudyPK
import com.weavict.edu.entity.BuyerStudyPlan
import com.weavict.edu.module.UserService
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

@Path("/study")
class StudyRest extends BaseRest
{
    @Autowired
    UserService userService;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryBuyerStudy4Book")
    String queryBuyerStudy4Book(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "study": ({
                        BuyerStudyPK buyerStudyPK = objToBean(query.buyerStudyPK, BuyerStudyPK.class,objectMapper);
                        BuyerStudy buyerStudy = userService.findObjectById(BuyerStudy.class,buyerStudyPK);
                        buyerStudy?.cancelLazyEr();
                        return buyerStudy;
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
    @Path("/updateBuyerStudy")
    String updateBuyerStudy(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            BuyerStudy buyerStudy = objToBean(query.buyerStudy,BuyerStudy.class,null);
            buyerStudy.createDate = new Date();
            // oss
            OSSClient ossClient = OtherUtils.genOSSClient();
            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), buyerStudy.dataUrl, new ByteArrayInputStream(objectMapper.writeValueAsString(query.buyerDataList).getBytes("UTF-8")));
            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), buyerStudy.dataUrl, CannedAccessControlList.PublicRead);
            ossClient.shutdown();
            // oss end
            userService.updateTheObject(buyerStudy);
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "buyerStudy": ({
                        return buyerStudy;
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
    @Path("/queryBuyerStudyPlanList")
    String queryBuyerStudyPlanList(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "planList": ({
                        return userService.queryBuyerStudyPlanList(query.buyerId);
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
    @Path("/updateBuyerStudyPlan")
    String updateBuyerStudyPlan(@RequestBody Map<String, Object> query)
    {
        try
        {
            BuyerStudyPlan buyerStudyPlan = this.objToBean(query.buyerStudyPlan, BuyerStudyPlan.class,null);
            buyerStudyPlan.createDate = new Date();
            userService.updateTheObject(buyerStudyPlan);
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
    @Path("/delTheBuyerStudyPlan")
    String delTheBuyerStudyPlan(@RequestBody Map<String, Object> query)
    {
        try
        {
            userService.delTheBuyerStudyPlan(query.buyerId,query.bookId);
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
    @Path("/queryBuyerStudy8Book4Buyer")
    String queryBuyerStudy8Book4Buyer(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "bookUniStudyList": ({
                        userService.queryBuyerStudy8Book4Buyer(query.buyerId,query.bookId);
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
    @Path("/queryOrderBuyer4Book")
    String queryOrderBuyer4Book(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "buyerBook": ({
                        BuyerBookPK buyerBookPK = objToBean(query.buyerBookPK, BuyerBookPK.class,objectMapper);
                        BuyerBook buyerBook = userService.findObjectById(BuyerBook.class,buyerBookPK);
                        buyerBook?.cancelLazyEr();
                        return buyerBook;
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
    @Path("/addBuyerBookList2Buyer")
    String addBuyerBookList2Buyer(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            List bookList = objToBean(query.bookList, List.class, objectMapper);
            userService.addBuyerBookList2Buyer(query.buyerId,bookList);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
