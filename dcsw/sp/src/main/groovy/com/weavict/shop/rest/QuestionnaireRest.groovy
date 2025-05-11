package com.weavict.shop.rest

import com.aliyun.oss.OSSClient
import com.aliyun.oss.model.CannedAccessControlList
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.shop.entity.QuestionnaireReport
import com.weavict.shop.module.QuestionReportBean
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

@Path("/question")
class QuestionnaireRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    QuestionReportBean questionReportBean;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/ganQuestionnaireJson")
    String ganQuestionnaireJson(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            OSSClient ossClient = OtherUtils.genOSSClient();
            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"ad/ad${query.questionnaireId}.json",new ByteArrayInputStream(objectMapper.writeValueAsString(questionReportBean.queryTheQuestionnaireAll(query.questionnaireId)).getBytes("UTF-8")));
            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "ad/ad${query.questionnaireId}.json", CannedAccessControlList.PublicRead);
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
    @Path("/queryQuestionReportCustomerId")
    String queryQuestionReportCustomerId(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "questionnaireReport":({
                         return questionReportBean.queryQuestionReportCustomerId(query.questionaireId,query.customerId);
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
    @Path("/addQuestionaireReport")
    String addQuestionaireReport(@RequestBody Map<String,Object> query)
    {
        try
        {
            questionReportBean.addTheQuesReport(objToBean(query.questionaireReport, QuestionnaireReport.class,null));
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
