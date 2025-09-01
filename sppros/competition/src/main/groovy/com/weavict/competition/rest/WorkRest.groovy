package com.weavict.competition.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.competition.entity.SiteCompetition
import com.weavict.competition.entity.Work
import com.weavict.competition.module.WorkService
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

@Path("/work")
class WorkRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    WorkService workService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setupSiteCompetition")
    String setupSiteCompetition(@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.updateTheObject(this.objToBean(query.siteCompetition, SiteCompetition.class,null));
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
    @Path("/qySiteCompetition")
    String qySiteCompetition(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         SiteCompetition siteCompetition = workService.findObjectById(SiteCompetition.class,query.id);
                         siteCompetition?.cancelLazyEr();
                         return siteCompetition;
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
    @Path("/qySiteWorkItemList")
    String qySiteWorkItemList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         workService.newQueryUtils(false).masterTable("SiteWorkItem",null,null)
                         .where("sourceId=:sourceId",["sourceId":query.sourceId],null,{return true})
                         .where("sourceType=:sourceType",["sourceType":query.sourceType as byte],"and",{return true})
                         .where("type=:type",["type":query.type as byte],"and",{return true})
                         .orderBy("createDate desc")
                         .buildSql().run().content;
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
    @Path("/qyBuyerCompetitionWorkList")
    String qyBuyerCompetitionWorkList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                        return workService.qyBuyerCompetitionWorkInfo(query.userId,query.competitionId)?.each {
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

}
