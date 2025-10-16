package com.weavict.competition.rest

import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.file.FileWriter
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.competition.entity.Buyer
import com.weavict.competition.entity.Competition
import com.weavict.competition.entity.GuiGe
import com.weavict.competition.entity.Judge
import com.weavict.competition.entity.MasterCompetition
import com.weavict.competition.entity.OrgHuman
import com.weavict.competition.entity.SiteCompetition
import com.weavict.competition.entity.SiteWorkItem
import com.weavict.competition.entity.Work
import com.weavict.competition.entity.WorkItem
import com.weavict.competition.module.WorkService
import com.weavict.website.common.OtherUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.TransactionDefinition
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
                         return workService.gainSiteCompetition(query);
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
                         return workService.qySiteWorkItemList(query);
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

//    暂时没用，使用的是qyWork
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
                         return workService.qyOrgHumanList(query);
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
                         return workService.qyMasterSiteCompetitionList(query);
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
    @Path("/qyCompetitionList")
    String qyCompetitionList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper4DateTime(null,null);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         return workService.qyCompetitionList(query);
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/updateCompetition")
    String updateCompetition(@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.updateTheObject(this.objToBean(query.competition, Competition.class,null));
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
    @Path("/updateSiteCompetitionSetupFields")
    String updateSiteCompetitionSetupFields(@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.updateTheObjectFilds(SiteCompetition.class.name,"id=:id", [setupFields:query.setupFields],["id":query.id],false);
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
    @Path("/updateCompetitionList")
    String updateCompetitionList(@RequestBody Map<String,Object> query)
    {
        try
        {
            List<Competition> competitionList = this.objToBean(query.competitionList,List.class,null);
            workService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                for (Competition competition in competitionList)
                {
                    workService.updateObject(competition);
                    for (GuiGe guiGe in competition.tempMap.guiGeList)
                    {
                        workService.updateObject(guiGe);
                    }
                }
            });
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
    @Path("/deleteCompetition")
    String deleteCompetition(@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.deleteTheObject8Fields(Competition.class.simpleName,"id=:id",[id:query.id],false);
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
    @Path("/deleteGuiGe")
    String deleteGuiGe(@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.deleteTheObject8Fields(GuiGe.class.simpleName,"id=:id",[id:query.id],false);
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
    @Path("/qyWorks")
    String qyWorks(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper4DateTime("yyyy-MM-dd",null);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         List<Work> workList = workService.newQueryUtils(false).masterTable(Work.class.simpleName,null,null)
                                 .where("appId = :appId",["appId":query.appId],null,{return true})
                                 .where("guiGe.id = :guiGeId",["guiGeId":query.guiGeId],"and",{return !(query.guiGeId in [null,""])})
                                 .where("guiGeId = :guiGeJsonId",["guiGeJsonId":query.guiGeJsonId],"and",{return !(query.guiGeJsonId in [null,""])})
                                    .where("buyer.phone=:userId",["userId":query.userId],"and",{return !(query.userId in [null,""])})
                                    .where("guiGe.competition.masterCompetition.id = :masterCompetitionId",["masterCompetitionId":query.masterCompetitionId],"and",{return !(query.masterCompetitionId in [null,""])})
                                 .orderBy("createDate")
                                 .buildSql().run().content;
                         for(Work work in workList)
                         {
                             work.cancelLazyEr();
                         }
                         return workList;
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
    @Path("/buildCacheCpt")
    String buildCacheCpt(@RequestBody Map<String,Object> query)
    {
        try
        {
            SiteCompetition siteCompetition = workService.gainSiteCompetition([appId:query.appId,id:query.siteCompetitionId]);
            siteCompetition.cancelLazyEr();
            FileWriter writer = new FileWriter("""${OtherUtils.givePropsValue("json_files_dir")}/${query.host}/siteInfo.json""".toString(),"utf8");
            writer.write(buildObjectMapper4DateTime(null,null).writeValueAsString([
                    siteCompetition:({
                        return siteCompetition;
                    }).call(),
                    siteZuTiMediaList:({
                        return workService.qySiteWorkItemList(appId:query.appId,sourceType:0 as byte,sourceId:query.appId,type:0 as byte);
                    }).call(),
                    siteZuoPingInfo:({
                        return null;
                    }).call(),
                    siteOrgHumanList:({
                        return workService.qyOrgHumanList(appId:query.appId,sourceType: 0 as byte,sourceId: query.appId);
                    }).call()
            ]));

            writer = new FileWriter("""${OtherUtils.givePropsValue("json_files_dir")}/${query.host}/masterCompetition.json""".toString(),"utf8");
            writer.write(buildObjectMapper4DateTime(null,null).writeValueAsString([
                     masterCompetitionInfo:({
                        MasterCompetition masterCompetition = workService.qyMasterSiteCompetitionList([appId:query.appId,id:query.masterCompetitionId,siteCompetitionId:query.siteCompetitionId])[0];
                        masterCompetition.competitionList = workService.qyCompetitionList([appId:query.appId,masterCompetitionId:masterCompetition.id,shiQyGuiGeList:true]);
                        for(Competition competition in masterCompetition.competitionList)
                        {
                            competition.masterCompetition = null;
                            for (GuiGe guiGe in competition.guiGeList)
                            {
                                guiGe.cancelLazyEr();
                                guiGe.competition = null;
                            }
                        }
                        masterCompetition.siteCompetition = null;
                        masterCompetition.tempMap = [:];
                        masterCompetition.tempMap.setupFields = siteCompetition.setupFields;
                        return masterCompetition;
                    }).call()
            ]));
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
    @Path("/updateBuyerWork")
    String updateBuyerWork(@RequestBody Map<String,Object> query)
    {
        try
        {
            Work work = this.objToBean(query.work,Work.class,null);
               workService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                workService.updateObject(work);

                for (def w in work.tempMap.workItemList)
                {
                    w.createDate = work.createDate;
//                    WorkItem workItem = new WorkItem();
//                    workItem.id = w.id;
//                    workItem.createDate = work.createDate;
//                    workItem.exifInfo = w.exifInfo;
//                    workItem.mediaFields = w.mediaFields;
//                    workItem.mediaType = w.mediaType;
//                    workItem.path = w.path;
//                    workItem.type = w.type;
//                    workItem.work = new Work();
//                    workItem.work.id = work.id;
                    workService.updateObject(w as WorkItem);
                }
            });
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
