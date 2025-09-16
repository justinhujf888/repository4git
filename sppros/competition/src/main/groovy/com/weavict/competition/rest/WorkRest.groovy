package com.weavict.competition.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.competition.entity.Judge
import com.weavict.competition.entity.MasterCompetition
import com.weavict.competition.entity.OrgHuman
import com.weavict.competition.entity.SiteCompetition
import com.weavict.competition.entity.SiteWorkItem
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
                         .orderBy("createDate")
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
    @Path("/updateSiteWorkItem")
    String updateSiteWorkItem (@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.updateTheObject(this.objToBean(query.siteWorkItem, SiteWorkItem.class,null));
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
    @Path("/deleteSiteWorkItem")
    String deleteSiteWorkItem (@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.deleteTheObject8Fields(SiteWorkItem.class.name,"id=:id",["id":query.id],false);
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/qyOrgHumanList")
    String qyOrgHumanList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         return workService.newQueryUtils(false,false).masterTable(OrgHuman.class.name,"j",null)
                                .where("j.appId = :appId",["appId":query.appId],null,{return true})
                                 .where("j.sourceType = :sourceType",["sourceType":query.sourceType],"and",{return true})
                                 .where("j.sourceId = :sourceId",["sourceId":query.sourceId],"and",{return true})
                                 .where("j.name like :name",["name":"%${query.name}%"],"and",{return !(query.name in [null,""])})
                                 .where("j.engName like :engName",["engName":"%${query.engName}%"],"and",{return !(query.engName in [null,""])})
                                .orderBy("createDate")
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
    @Path("/updateOrgHuman")
    String updateOrgHuman (@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.updateTheObject(this.objToBean(query.orgHuman, OrgHuman.class,null));
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
    @Path("/qyMasterSiteCompetition")
    String qyMasterSiteCompetition(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper4DateTime("yyyy-MM-dd",null);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         List<MasterCompetition> masterCompetitionList = workService.newQueryUtils(false).masterTable("MasterCompetition",null,null)
                            .where("appId = :appId",["appId":query.appId],null,{return true})
                            .where("siteCompetition.id = :siteCompetitionId",["siteCompetitionId":query.siteCompetitionId],"and",{return true})
                            .where("cptDate = :cptDate",["cptDate":query.cptDate],"and",{return query.cptDate!=null})
                            .orderBy("cptDate,beginDate")
                                 .buildSql().run().content;
                         for(MasterCompetition masterCompetition in masterCompetitionList)
                         {
                             masterCompetition.cancelLazyEr();
                         }
                         return masterCompetitionList;
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
    @Path("/updateMasterCompetition")
    String updateMasterCompetition(@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.updateTheObject(this.objToBean(query.masterCompetition, MasterCompetition.class,null));
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
    @Path("/updateMasterCompetitionDescription")
    String updateMasterCompetitionDescription(@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.updateTheObjectFilds(MasterCompetition.class.name,"id=:id", [description:query.description],["id":query.id],false);
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
    @Path("/updateMasterCompetitionSetupFields")
    String updateMasterCompetitionSetupFields(@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.updateTheObjectFilds(MasterCompetition.class.name,"id=:id", [setupFields:query.setupFields],["id":query.id],false);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
