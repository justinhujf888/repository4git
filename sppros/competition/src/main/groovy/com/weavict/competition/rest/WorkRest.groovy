package com.weavict.competition.rest

import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.file.FileWriter
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.competition.entity.Buyer
import com.weavict.competition.entity.Competition
import com.weavict.competition.entity.CompetitionJudge
import com.weavict.competition.entity.CompetitionJudgePK
import com.weavict.competition.entity.CurrentMasterCompetitionSetup
import com.weavict.competition.entity.CurrentMasterCompetitionSetupPK
import com.weavict.competition.entity.GuiGe
import com.weavict.competition.entity.Judge
import com.weavict.competition.entity.JudgeWork
import com.weavict.competition.entity.JudgeWorkPK
import com.weavict.competition.entity.MCPageSetup
import com.weavict.competition.entity.MasterCompetition
import com.weavict.competition.entity.OrgHuman
import com.weavict.competition.entity.SiteCompetition
import com.weavict.competition.entity.SiteWorkItem
import com.weavict.competition.entity.Work
import com.weavict.competition.entity.WorkItem
import com.weavict.competition.entity.WorkLog
import com.weavict.competition.module.PageUtil
import com.weavict.competition.module.QueryUtils
import com.weavict.competition.module.UserBean
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

    @Autowired
    UserBean userService;

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
            workService.updateTheObjectFilds(MasterCompetition.class.name,"id=:id", ["${query.typeId}":query.data],["id":query.id],false);
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
                         PageUtil pageUtil = workService.qyWorks(query);
                         for(Work work in pageUtil.content as List<Work>)
                         {
                             work.cancelLazyEr();
                             if (query.shiWorkItemList==true)
                             {
                                 work.workItemList = workService.newQueryUtils(false).masterTable(WorkItem.class.simpleName,null,null)
                                         .where("work.id = :workId",["workId":work.id],null,{return true})
                                         .orderBy("mediaType,type")
                                         .buildSql().run().content;
                                 for(WorkItem workItem in work.workItemList)
                                 {
                                     workItem.cancelLazyEr();
                                     workItem.work = null;
                                 }
                             }
                         }
                         if (query.pageSize!=null)
                         {
                             return pageUtil;
                         }
                         else
                         {
                             return pageUtil.content;
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
    @Path("/qyWorkLog8Work")
    String qyWorkLog8Work(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper4DateTime("yyyy-MM-dd",null);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         return workService.qyWorkLog8Work(query);
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
    @Path("/saveWorkLog")
    String saveWorkLog(@RequestBody Map<String,Object> query)
    {
        try
        {
            WorkLog workLog = this.objToBean(query.workLog, WorkLog.class,null);
            workLog.createDate = new Date();
            workService.updateTheObject(workLog);
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
    @Path("/delTheWorkLog")
    String delTheWorkLog(@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.deleteTheObject8Fields(WorkLog.simpleName,"id = :id",[id:query.id],false);
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
    @Path("/qyPageSetup")
    String qyPageSetup(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         return workService.qyPageSetup(query.competitionId,query.key,query.appId);
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
    @Path("/savePageSetup")
    String savePageSetup(@RequestBody Map<String,Object> query)
    {
        try
        {
            MCPageSetup mcPageSetup = this.objToBean(query.mcPageSetup, MCPageSetup.class,null);
            workService.updateTheObject(mcPageSetup);
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
                    }).call(),
                    siteJudgeList:({
                        return userService.queryJudgeList(appId:query.appId,pageSize:10,currentPage:0).content;
                    }).call()
            ]));

            MasterCompetition masterCompetition = workService.qyMasterSiteCompetitionList([appId:query.appId,id:query.masterCompetitionId,siteCompetitionId:query.siteCompetitionId])[0];
            for(def item in [[key:"masterCompetitionId",value:masterCompetition.id],[key:"masterCompetitionStatus",value:-1 as byte]])
            {
                CurrentMasterCompetitionSetup currentMasterCompetitionSetup = new CurrentMasterCompetitionSetup();
                CurrentMasterCompetitionSetupPK currentMasterCompetitionSetupPK = new CurrentMasterCompetitionSetupPK(query.appId as String,item.key);
                currentMasterCompetitionSetup.currentMasterCompetitionSetupPK = currentMasterCompetitionSetupPK;
                currentMasterCompetitionSetup.value = item.value;
                workService.updateTheObject(currentMasterCompetitionSetup);
            }
            workService.pingShenJudgesInit(query.appId as String,masterCompetition.id,0 as byte);

            writer = new FileWriter("""${OtherUtils.givePropsValue("json_files_dir")}/${query.host}/worksetup.json""".toString(),"utf8");
            writer.write(buildObjectMapper().writeValueAsString(masterCompetition.workSetup));

            writer = new FileWriter("""${OtherUtils.givePropsValue("json_files_dir")}/${query.host}/masterCompetition.json""".toString(),"utf8");
            writer.write(buildObjectMapper4DateTime(null,null).writeValueAsString([
                     masterCompetitionInfo:({
                        masterCompetition = workService.qyMasterSiteCompetitionList([appId:query.appId,id:query.masterCompetitionId,siteCompetitionId:query.siteCompetitionId])[0];
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


            //
            List<MCPageSetup> mcPageSetupList = workService.qyPageSetup(query.masterCompetitionId,null,query.appId);
            for(MCPageSetup mcPageSetup in mcPageSetupList)
            {
                writer = new FileWriter("""${OtherUtils.givePropsValue("json_files_dir")}/${query.host}/${mcPageSetup.mcPageSetupPK.key}.json""".toString(),"utf8");
                writer.write(buildObjectMapper4DateTime(null,null).writeValueAsString(mcPageSetup.setupJson));
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteWorkItem")
    String deleteWorkItem (@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.deleteTheObject8Fields(WorkItem.class.name,"id=:id",["id":query.id],false);
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
    @Path("/updateJudgeSetup")
    String updateJudgeSetup (@RequestBody Map<String,Object> query)
    {
        try
        {
            Map<String,Object> judgeSetup = objToBean(query.judgeSetup,Map.class,null);
            workService.updateTheObjectFilds(MasterCompetition.class.name,"id=:id",[judgeSetup:judgeSetup],[id:query.id],false);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

//    workService.pingShenJudgesInit("localhost","localhost",2 as byte)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/qyPingShenFlow")
    String qyPingShenFlow(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         return workService.qyPingShenFlow(query);
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
    @Path("/qyPingShenJudgeList")
    String qyPingShenJudgeList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         return workService.qyPingShenJudgeList(query);
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
    @Path("/pingShenWorksInit")
    String pingShenWorksInit(@RequestBody Map<String,Object> query)
    {
        try
        {
            workService.pingShenWorksInit(query.appId as String,query.masterCompetitionId as String,query.pingShenStepId as byte);
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
    @Path("/saveSubmitJudgeWorks")
    String saveSubmitJudgeWorks(@RequestBody Map<String,Object> query)
    {
        try
        {
            List workList = objToBean(query.selWork,List.class,null);
            workService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                for(def item in workList)
                {
//                    JudgeWork judgeWork = new JudgeWork();
//                    judgeWork.judgeWorkPK = new JudgeWorkPK(query.appId as String,query.judgeId as String,item.id as String,query.stepStatus as byte);
//                    judgeWork.shiPass
                    workService.updateTheObjectFilds(JudgeWork.simpleName,"judgeWorkPK = :judgeWorkPK",[shiPass:(item.fg as byte)==1 ? true : false],[judgeWorkPK:new JudgeWorkPK(query.appId as String,query.judgeId as String,item.id as String,query.stepStatus as byte)],false);
                }
            });
            if ((query.shiPass as boolean) == true)//query.shiPass==true;评委提交了最终结果
            {
                workService.updateTheObjectFilds(CompetitionJudge.simpleName,"competitionJudgePK = :competitionJudgePK",[pingShenStatus:1 as byte],[competitionJudgePK:new CompetitionJudgePK(query.masterCompetitionId as String,query.competitionId as String,query.guiGeId as String,query.judgeId as String,query.stepStatus as byte,query.appId as String)],false);
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
    @Path("/qyJudgeWorks")
    String qyJudgeWorks(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            QueryUtils queryUtils = workService.newQueryUtils(true,true);
            queryUtils.masterTable("work","w",[
                    [sf:"name",bf:"name"],
                    [sf:"id",bf:"id"],
                    [sf:"competition_id",bf:"competition.id"],
                    [sf:"guige_id",bf:"guiGe.id"],
                    [sf:"guigeid",bf:"guiGeId"],
                    [sf:"gousidescription",bf:"gousiDescription"],
                    [sf:"mymeandescription",bf:"myMeanDescription"],
                    [sf:"hangyefields",bf:"hangyeFields",convertType:"json"],
                    [sf:"otherfields",bf:"otherFields",convertType:"json"],
                    [sf:"buyer_phone",bf:"buyer.phone"],
                    [sf:"appid",bf:"appId"],
                    [sf:"status",bf:"status"],
                    [sf:"lat",bf:"lat"],
                    [sf:"lng",bf:"lng"],
                    [sf:"createdate",bf:"createDate"],
                    [sf:"mastercompetitionid",bf:"masterCompetitionId"]
            ])
                    .joinTable("judgework","jw","right join","w.id=jw.workid",[
                    [sf:"judgeid",bf:"tempMap.judgeId"],
                    [sf:"stepstatus",bf:"tempMap.stepStatus"],
                    [sf:"fenJson",bf:"tempMap.fenJson"],
                    [sf:"fen",bf:"tempMap.fen"],
                    [sf:"shipass",bf:"tempMap.shiPass"]
            ])
                    .joinTable("mastercompetition","mc","left join","w.mastercompetitionid=mc.id",[
                            [sf:"name",bf:"tempMap.masterCompetitionName"]
                    ])
                    .where("w.appid = :appId",[appId:query.appId],null,{return true})
                    .where("jw.stepstatus = :stepStatus",[stepStatus:query.stepStatus],"and",{return true})
                    .where("w.competitionid = :competitionId",[competitionId:query.competitionId],"and",{return !(query.competitionId in [null,""])})
                    .where("w.guige_id = :guiGeId",[guiGeId:query.guiGeId],"and",{return !(query.guiGeId in [null,""])})
                    .where("jw.judgeid = :judgeId",[judgeId:query.judgeId],"and",{return !(query.judgeId in [null,""])})
                    .where("w.mastercompetitionid = :masterCompetitionId",[masterCompetitionId:query.masterCompetitionId],"and",{return !(query.masterCompetitionId in [null,""])})
                    .beanSetup(Work.class,null,null)
                    .saveSql();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         PageUtil pageUtil = null;
                         if (query.pageSize != null) {
                             pageUtil = queryUtils.pageLimit(query.pageSize as int, query.currentPage as int, "w.id")
                                     .buildSql().run();
                         } else {
                             pageUtil = queryUtils.buildSql().run();
                         }
                         for(Work work in pageUtil.content as List<Work>)
                         {
                             work.cancelLazyEr();
                             if (query.shiWorkItemList==true)
                             {
                                 work.workItemList = workService.newQueryUtils(false).masterTable(WorkItem.class.simpleName,null,null)
                                         .where("work.id = :workId",["workId":work.id],null,{return true})
                                         .orderBy("mediaType,type")
                                         .buildSql().run().content;
                                 for(WorkItem workItem in work.workItemList)
                                 {
                                     workItem.cancelLazyEr();
                                     workItem.work = null;
                                 }
                             }
                         }
                         if ((query.qyPassCount as boolean) == true)
                         {
                             pageUtil.tempMap["shiPassList"] = workService.createNativeQuery4Params("select ${queryUtils.sqlMaps.sqlParse.count} ${queryUtils.sqlMaps.sqlParse.fromJoin} ${queryUtils.sqlMaps.sqlParse.where} and jw.shipass=true",queryUtils.sqlMaps.params).resultList;
                         }
                         queryUtils.sqlMaps = null;
                         return pageUtil;
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
    @Path("/updateCurrentMasterCompetitionSetup")
    String updateCurrentMasterCompetitionSetup(@RequestBody Map<String,Object> query)
    {
        try
        {
            CurrentMasterCompetitionSetup currentMasterCompetitionSetup = new CurrentMasterCompetitionSetup();
            CurrentMasterCompetitionSetupPK currentMasterCompetitionSetupPK = new CurrentMasterCompetitionSetupPK();
            currentMasterCompetitionSetupPK.key = query.key
            currentMasterCompetitionSetup.currentMasterCompetitionSetupPK = currentMasterCompetitionSetupPK;
            currentMasterCompetitionSetup.value = query.value;
            workService.updateTheObject(currentMasterCompetitionSetup);
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
    @Path("/giveCurrentMasterCompetitionSetup")
    String giveCurrentMasterCompetitionSetup(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            List<CurrentMasterCompetitionSetup> currentMasterCompetitionSetupList = null;
            currentMasterCompetitionSetupList = workService.newQueryUtils(false,false).masterTable(CurrentMasterCompetitionSetup.simpleName,null,null)
                    .where("currentMasterCompetitionSetupPK.appId = :appId",[appId:query.appId],null,{return true})
                    .where("currentMasterCompetitionSetupPK.key in :keys",[keys:objToBean(query.keys,List.class,objectMapper)],"and",{return true})
                    .buildSql().run().content;
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "data":({
                         return currentMasterCompetitionSetupList;
                     }).call(),
                     "map":({
                                Map maps = new HashMap();
                                for(CurrentMasterCompetitionSetup cm in currentMasterCompetitionSetupList)
                                {
                                    maps[cm.currentMasterCompetitionSetupPK.key] = cm.value;
                                }
                                return maps;
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
