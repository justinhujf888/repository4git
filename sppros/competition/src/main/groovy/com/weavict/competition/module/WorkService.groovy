package com.weavict.competition.module

import com.alibaba.fastjson2.JSON
import com.bestvike.linq.Linq
import com.bestvike.tuple.Tuple
import com.bestvike.tuple.Tuple2
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
import jakarta.transaction.Transactional

//import static org.jooq.impl.DSL.*;
//import org.jooq.*;
//import org.jooq.impl.*;
import org.springframework.stereotype.Service

@Service("workService")
class WorkService extends ModuleBean
{
    List<Work> qyBuyerCompetitionWorkInfo(String userId, String competitionId)
    {
        List<Work> workList = this.newQueryUtils(false).masterTable("Work", null, null)
                .where("competition.id = :competitionId", ["competitionId": competitionId], null, null)
                .where("buyer.phone = :userId", ["userId": userId], "and", null)
                .buildSql().run().content;
        for (Work work in workList) {
            work.cancelLazyEr();
            List<WorkItem> workItemList = this.newQueryUtils(false).masterTable("WorkItem", null, null)
                    .where("work.id = :workId", ["workId": work.id], null, null)
                    .buildSql().run().content;
            for (WorkItem workItem in workItemList) {
                workItem.cancelLazyEr();
            }
            work.workItemList = workItemList;
        }
        return workList;
    }

    SiteCompetition gainSiteCompetition(Map query)
    {
        SiteCompetition siteCompetition = this.findObjectById(SiteCompetition.class, query.id);
        siteCompetition?.cancelLazyEr();
        return siteCompetition;
    }

    List<SiteWorkItem> qySiteWorkItemList(Map query)
    {
        this.newQueryUtils(false).masterTable("SiteWorkItem", null, null)
                .where("appId=:appId", ["appId": query.appId], null, { return true })
                .where("sourceId=:sourceId", ["sourceId": query.sourceId], "and", { return true })
                .where("sourceType=:sourceType", ["sourceType": query.sourceType as byte], "and", { return true })
                .where("type=:type", ["type": query.type as byte], "and", { return true })
                .orderBy("createDate")
                .buildSql().run().content;
    }

    List<OrgHuman> qyOrgHumanList(Map query)
    {
        return this.newQueryUtils(false, false).masterTable(OrgHuman.class.name, "j", null)
                .where("j.appId = :appId", ["appId": query.appId], null, { return true })
                .where("j.sourceType = :sourceType", ["sourceType": query.sourceType], "and", { return true })
                .where("j.sourceId = :sourceId", ["sourceId": query.sourceId], "and", { return true })
                .where("j.name like :name", ["name": "%${query.name}%"], "and", { return !(query.name in [null, ""]) })
                .where("j.engName like :engName", ["engName": "%${query.engName}%"], "and", { return !(query.engName in [null, ""]) })
                .orderBy("createDate")
                .buildSql().run().content;
    }

    List<MasterCompetition> qyMasterSiteCompetitionList(Map query)
    {
        List<MasterCompetition> masterCompetitionList = this.newQueryUtils(false).masterTable("MasterCompetition", null, null)
                .where("appId = :appId", ["appId": query.appId], null, { return true })
                .where("id = :id", [id: query.id], "and", { !(query.id in [null, ""]) })
                .where("siteCompetition.id = :siteCompetitionId", ["siteCompetitionId": query.siteCompetitionId], "and", { return true })
                .where("cptDate = :cptDate", ["cptDate": query.cptDate], "and", { return query.cptDate != null })
                .orderBy("cptDate,beginDate")
                .buildSql().run().content;
        for (MasterCompetition masterCompetition in masterCompetitionList) {
            masterCompetition.cancelLazyEr();
        }
        return masterCompetitionList;
    }

    List<Competition> qyCompetitionList(Map query)
    {
        List<Competition> competitionList = this.newQueryUtils(false).masterTable(Competition.class.simpleName, null, null)
                .where("appId = :appId", ["appId": query.appId], null, { return true })
                .where("masterCompetition.id = :masterCompetitionId", ["masterCompetitionId": query.masterCompetitionId], "and", { return true })
                .where("name = :name", ["name": query.name], "and", { return query.name != null })
//                                 .orderBy("id")
                .buildSql().run().content;
        if (query.shiQyGuiGeList != null && query.shiQyGuiGeList as boolean) {
            for (Competition competition in competitionList) {
                competition.cancelLazyEr();
                competition.guiGeList = this.newQueryUtils(false).masterTable(GuiGe.class.simpleName, null, null)
                        .where("competition.id = :competitionId", ["competitionId": competition.id], null, { return true })
                        .buildSql().run().content;
                for (GuiGe guiGe in competition.guiGeList) {
                    guiGe.cancelLazyEr();
                }
            }
        }
        return competitionList;
    }

    List<GuiGe> qyGuiGeList8CompetitionId(String competitionId)
    {
        List<GuiGe> guiGeList = this.newQueryUtils(false).masterTable(GuiGe.class.simpleName, null, null)
                .where("competition.id = :competitionId", ["competitionId": competitionId], null, { return true })
                .buildSql().run().content;
        for (GuiGe guiGe in guiGeList) {
            guiGe.cancelLazyEr();
        }
        return guiGeList;
    }

    PageUtil qyWorks(Map query)
    {
        QueryUtils queryUtils = this.newQueryUtils(false);
        queryUtils.masterTable(Work.class.simpleName, null, null)
                .where("appId = :appId", ["appId": query.appId], null, { return true })
                .where("id = :workId", ["workId": query.workId], "and", { return !(query.workId in [null, ""]) })
                .where("guiGe.id = :guiGeId", ["guiGeId": query.guiGeId], "and", { return !(query.guiGeId in [null, ""]) })
                .where("guiGeId = :guiGeJsonId", ["guiGeJsonId": query.guiGeJsonId], "and", { return !(query.guiGeJsonId in [null, ""]) })
                .where("buyer.phone=:userId", ["userId": query.userId], "and", { return !(query.userId in [null, ""]) })
                .where("competition.id = :competitionId", [competitionId: query.competitionId], "and", { return !(query.competitionId in [null, ""]) })
                .where("competition.masterCompetition.id = :masterCompetitionId", ["masterCompetitionId": query.masterCompetitionId], "and", { return !(query.masterCompetitionId in [null, ""]) })
                .where("status in :statusList", ["statusList": query.statusList], "and", { return query.statusList != null && (query.statusList as byte[]).length > 0 })
                .orderBy("createDate");
        if (query.pageSize != null) {
            return queryUtils.pageLimit(query.pageSize as int, query.currentPage as int, "id")
                    .buildSql().run();
        } else {
            return queryUtils.buildSql().run();
        }
    }

    List<WorkLog> qyWorkLog8Work(Map query)
    {
        List<WorkLog> workLogList = this.newQueryUtils(false).masterTable(WorkLog.simpleName, null, null)
                .where("appId = :appId", ["appId": query.appId], null, { return true })
                .where("id = :id", [id: query.id], "and", { !(query.id in [null, ""]) })
                .where("workId = :workId", ["workId": query.workId], "and", { return true })
                .orderBy("createDate")
                .buildSql().run().content;
        for (WorkLog workLog in workLogList) {
            workLog.cancelLazyEr();
        }
        return workLogList;
    }

    List<MCPageSetup> qyPageSetup(String competitionId, String key, String appId)
    {
        List<MCPageSetup> mcPageSetupList = this.newQueryUtils(false).masterTable(MCPageSetup.class.simpleName, null, null)
                .where("mcPageSetupPK.appId = :appId", ["appId": appId], null, { return true })
                .where("mcPageSetupPK.competitionId = :competitionId", ["competitionId": competitionId], "and", { return true })
                .where("mcPageSetupPK.key = :key", ["key": key], "and", { return !(key in [null, ""]) })
                .buildSql().run().content;
        for (MCPageSetup mcPageSetup in mcPageSetupList) {
            mcPageSetup.cancelLazyEr();
        }
        return mcPageSetupList;
    }

    Map qyPingShenFlow(Map query)
    {
        return [flow: [
                [sort: 0, id: 0, name: "作品初筛", type: 0, data:[judgePassWorkMixCount:200]],
                [sort: 1, id: 1, name: "作品第一轮评分", type: 1, data:[:]],
                [sort: 2, id: 2, name: "作品第二轮评分", type: 1, data:[:]]
        ]];
    }

    @Transactional
    void pingShenJudgesInit(String appId, String masterCompetitionId, byte pingShenStepId)
    {
        this.deleteTheObject8Fields(CompetitionJudge.simpleName,"competitionJudgePK.appId = :appId and competitionJudgePK.masterCompetitionId = :masterCompetitionId and competitionJudgePK.competitionStatus = :competitionStatus",[appId:appId,masterCompetitionId:masterCompetitionId,competitionStatus:pingShenStepId],false);
        List<String> comList = [];
        List<String> ggList = [];
        MasterCompetition masterCompetition = this.findObjectById(MasterCompetition.class, masterCompetitionId);
//        println JSON.toJSONString(masterCompetition.judgeSetup.datas);
        for (def it in masterCompetition.judgeSetup.datas) {//it 为一个类别competition；it.data是评审的n个流程环节
            comList = [];
            for (def cit in it.data) {
                if (cit.id == pingShenStepId) {
                    for (def j in cit.setupData) {
//                        println "cid:${it.id}  jid:${j.id}";
                        comList << j;
                    }
                }
            }

            //如果有分组GuiGe
            if (it.guiGeList.size() > 0) {
                for (def gl in it.guiGeList) {//gl 为一个类别下的某一个guiGe规格；gl.data是评审的n个流程环节
                    ggList = [];
                    for (def glit in gl.data) {
                        if (glit.id == pingShenStepId) {
                            for (def j in glit.setupData) {
//                                println "cid:${it.id} gid:${gl.id}  jid:${j.id}";
                                ggList << j;
                            }
                            //将类别的评委信息与规格进行交集合并
//                            println "======================begin=======================";
//                            println comList;
//                            println ggList;
//                            println Linq.of(comList).where(o -> !(o in ggList) ).toList();
//                            println "======================end==========================";
                            for (def x in Linq.of(comList).where(o -> !(o.id in (Linq.of(ggList).select(tg -> tg.id).toList()))).toList()) {
                                ggList << x;
                            }
                            //ggList就是最后和类评委合并的评委集合，就可以开始进行分配作品的工作。
                            for (def j in ggList) {
                                CompetitionJudge competitionJudge = new CompetitionJudge();
                                competitionJudge.competitionJudgePK = new CompetitionJudgePK(masterCompetitionId, it.id as String, gl.id as String, j.id, pingShenStepId, appId as String);
                                if (pingShenStepId == 0 as byte) {
                                    competitionJudge.pingShenFields = null;
                                } else {
                                    competitionJudge.pingShenFields = ["fields": j.fields];
                                }
                                competitionJudge.pingShenStatus = 0 as byte;
                                this.updateObject(competitionJudge);
                            }
                        }
                    }
                }
            }
            //                查询json是否有遗漏的分组，查询数据库进一步判断
            List<GuiGe> guiGeList = this.qyGuiGeList8CompetitionId(it.id);
            if (guiGeList != null && guiGeList.size() > 0) {
                //数据库有分组，将类别评委设置到分组
                for (def j in comList) {
                    for (GuiGe guiGe in guiGeList) {
                        CompetitionJudge competitionJudge = new CompetitionJudge();
                        competitionJudge.competitionJudgePK = new CompetitionJudgePK(masterCompetitionId, it.id as String, guiGe.id, j.id, pingShenStepId, appId);
                        if (pingShenStepId == 0 as byte) {
                            competitionJudge.pingShenFields = null;
                        } else {
                            competitionJudge.pingShenFields = ["fields": j.fields];
                        }
                        competitionJudge.pingShenStatus = 0 as byte;
                        this.updateObject(competitionJudge);
                    }
                }
            } else {
                //                没有分组，comList里是最后的评委信息
                for (def j in comList) {
                    CompetitionJudge competitionJudge = new CompetitionJudge();
                    competitionJudge.competitionJudgePK = new CompetitionJudgePK(masterCompetitionId, it.id as String, "-1", j.id, pingShenStepId, appId);
                    if (pingShenStepId == 0 as byte) {
                        competitionJudge.pingShenFields = null;
                    } else {
                        competitionJudge.pingShenFields = ["fields": j.fields];
                    }
                    this.updateObject(competitionJudge);
                }
            }
        }

//        select c.name,g.name,j.name from competitionjudge as cj left join competition as c on c.id = cj.competitionid left join guige as g on g.id = cj.guigeid left join judge as j on j.id = cj.judgeid
    }

    List<CompetitionJudge> qyPingShenJudgeList(Map query)
    {
        return this.newQueryUtils(true, true).masterTable("competitionjudge", "cj", [
                [sf: "mastercompetitionid", bf: "competitionJudgePK.masterCompetitionId"],
                [sf: "competitionid", bf: "competitionJudgePK.competitionId"],
                [sf: "guigeid", bf: "competitionJudgePK.guiGeId"],
                [sf: "judgeid", bf: "competitionJudgePK.judgeId"],
                [sf: "competitionstatus", bf: "competitionJudgePK.competitionStatus"],
                [sf: "pingshenfields", bf: "pingShenFields",convertType:"json"],
                [sf:"pingshenstatus",bf:"pingShenStatus"]
        ])
                .where("cj.appid = :appId", [appId: query.appId], null, { return true })
                .where("cj.mastercompetitionid = :masterCompetitionId", [masterCompetitionId: query.masterCompetitionId], "and", { return !(query.masterCompetitionId in [null, ""]) })
                .where("cj.competitionstatus = :competitionStatus", [competitionStatus: query.pingShenStepId as byte], "and", { return true })
                .where("cj.judgeid = :judgeId", [judgeId: query.judgeId], "and", { return !(query.judgeId in [null, ""]) })
                .where("cj.competitionid = :competitionId", [competitionId: query.competitionId], "and", { return !(query.competitionId in [null, ""]) })
                .where("cj.guigeid = :guiGeId", [guiGeId: query.guiGeId], "and", { return !(query.guiGeId in [null, ""]) })
                .beanSetup(CompetitionJudge.class, null, null)
                .buildSql().run().content;
    }

    PageUtil qyJudgeWorks(Map query)
    {
        QueryUtils queryUtils = this.newQueryUtils(true,true);
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
//                .joinTable("competitionjudge","cj","left join","cj.mastercompetitionid=mc.id and cj.competitionid=jw.competitionid and cj.guigeid=jw.guigeid and cj.judgeid=jw.judgeid and cj.competitionstatus=jw.stepstatus",[
//                        [sf:"pingshenfields",bf:"tempMap.pingShenFields"]
//                ])
                .where("w.appid = :appId",[appId:query.appId],null,{return true})
                .where("jw.stepstatus = :stepStatus",[stepStatus:query.stepStatus as byte],"and",{return true})
                .where("jw.competitionid = :competitionId",[competitionId:query.competitionId],"and",{return !(query.competitionId in [null,""])})
                .where("jw.shipass = :shiPass",[shiPass:query.shiPass as boolean],"and",{return query.shiPass !=null})
                .where("w.guige_id = :guiGeId",[guiGeId:query.guiGeId],"and",{return !(query.guiGeId in [null,""])})
                .where("jw.judgeid = :judgeId",[judgeId:query.judgeId],"and",{return !(query.judgeId in [null,""])})
                .where("w.mastercompetitionid = :masterCompetitionId",[masterCompetitionId:query.masterCompetitionId],"and",{return !(query.masterCompetitionId in [null,""])})
                .orderBy("jw.fen desc")
                .beanSetup(Work.class,null,null)
                .saveSql();
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
            this.detach(work);
            if (query.shiWorkItemList==true)
            {
                work.workItemList = this.newQueryUtils(false).masterTable(WorkItem.class.simpleName,null,null)
                        .where("work.id = :workId",["workId":work.id],null,{return true})
                        .orderBy("mediaType,type")
                        .buildSql().run().content;
                for(WorkItem workItem in work.workItemList)
                {
                    this.detach(workItem);
                    workItem.cancelLazyEr();
                    workItem.work = null;
                }
            }
        }
        if ((query.qyPassCount as boolean) == true)
        {
            pageUtil.tempMap["shiPassList"] = this.createNativeQuery4Params("select ${queryUtils.sqlMaps.sqlParse.count} ${queryUtils.sqlMaps.sqlParse.fromJoin} ${queryUtils.sqlMaps.sqlParse.where} and jw.shipass=true",queryUtils.sqlMaps.params).resultList;
        }
        queryUtils.sqlMaps = null;
        return pageUtil;
    }

    @Transactional
    void pingShenWorksInit(String appId, String masterCompetitionId, byte pingShenStepId,Map mapData)
    {
        MasterCompetition masterCompetition = this.findObjectById(MasterCompetition.class,masterCompetitionId);
        masterCompetition.flowSetup = mapData.flowSetup;
        this.updateObject(masterCompetition);
        List<CompetitionJudge> competitionJudgeList = qyPingShenJudgeList([appId: appId, masterCompetitionId: masterCompetitionId, judgeId: null, pingShenStepId: pingShenStepId]);
        var groupList = Linq.of(competitionJudgeList).groupBy(cj -> new Tuple2(cj.competitionJudgePK.competitionId,cj.competitionJudgePK.guiGeId));
        for (def group : groupList)
        {
//            println group.key;
            //            group is map,key is competitionId or guigeid; value is a list of CompetitionJudge. 即评委
            if (pingShenStepId == 0 as byte)
            {
                List<Work> workList = this.qyWorks([appId:appId,competitionId:group.key.item1,guiGeId:group.key.item2,statusList:[1 as byte]]).content;//查询已经提交的作品
                if (group.value?.size()>0 && workList?.size()>0)
                {
                    for(Work work in workList)
                    {
                        for(CompetitionJudge cj in group.value) {
                            JudgeWork judgeWork = new JudgeWork();
                            JudgeWorkPK judgeWorkPK = new JudgeWorkPK(appId, cj.competitionJudgePK.judgeId, work.id,masterCompetitionId, pingShenStepId);
                            judgeWork.judgeWorkPK = judgeWorkPK;
                            judgeWork.fen = 0;
                            judgeWork.fenJson = null;
                            judgeWork.shiPass = false;
                            judgeWork.competitionId = group.key.item1;
                            judgeWork.guiGeId = group.key.item2;
                            judgeWork.cancelLazyEr();
                            this.updateObject(judgeWork);
                        }
                    }
                }
            }
            else if (pingShenStepId in [1 as byte,2 as byte])
            {
                Map paramsMap = null;
                if (group.key.item2=="-1") {
                    paramsMap = [competitionId:group.key.item1,appId:appId,masterCompetitionId:masterCompetitionId,shiWorkItemList:true,judgeId:null,stepStatus:(pingShenStepId-1) as byte,qyPassCount:false,shiPass:true];
                } else {
                    paramsMap = [competitionId:group.key.item1,guiGeId:group.key.item2,appId:appId,masterCompetitionId:masterCompetitionId,shiWorkItemList:true,judgeId:null,stepStatus:(pingShenStepId-1) as byte,qyPassCount:false,shiPass:true];
                }
                List<Work> workList = null;
                if (pingShenStepId == 1 as byte)
                {
                    //从初筛的记录中获取通过的作品
                    workList = Linq.of(this.qyJudgeWorks(paramsMap).content).distinctBy (w->w.id).toList();
                } else if (pingShenStepId == 2 as byte)
                {
                    //从第一轮评分的记录中通过分数得到排名N位的记录（由于是多个评委对不同字段打分，所以需要在JudgeWork表中对相同workId的记录进行分数汇总）
                    List sumList = this.newQueryUtils(true,true).masterTable("judgework","jw",[
                        [sf:"workid",bf:"judgeWorkPK.workId"],
                        [sf:"workid",bf:"tempMap.workId"],
                        [isCop:true,cop:"sum(jw.fen)",sf:"sumfen",bf:"tempMap.sumFen"]
                    ])
                            .where("jw.appid = :appId",[appId:appId],null,{return true})
                            .where("jw.stepstatus = :stepStatus",[stepStatus:paramsMap.stepStatus as byte],"and",{return true})
                            .where("jw.competitionid = :competitionId",[competitionId:paramsMap.competitionId],"and",{return true})
                            .where("jw.shipass = :shiPass",[shiPass:paramsMap.shiPass as boolean],"and",{return true})
                            .where("jw.guigeid = :guiGeId",[guiGeId:paramsMap.guiGeId],"and",{return !(paramsMap.guiGeId in [null,""])})
                            .where("jw.mastercompetitionid = :masterCompetitionId",[masterCompetitionId:paramsMap.masterCompetitionId],"and",{return true})
                        .groupBy("jw.workid")
                        .beanSetup(JudgeWork.class,null,null)
                        .buildSql().run().content;
                    for (JudgeWork jw in sumList)
                    {
                        println jw.dump();
                    }
                }
//                println paramsMap;
//                println workList.size();
//                for(Work w in workList)
//                {
//                    println w.dump();
//                }

//                Collections.shuffle(workList);
//                Collections.shuffle(group.value);
                for(Work work in workList) {
                    for (CompetitionJudge cj in group.value) {
                        JudgeWork judgeWork = new JudgeWork();
                        JudgeWorkPK judgeWorkPK = new JudgeWorkPK(appId, cj.competitionJudgePK.judgeId, work.id, masterCompetitionId, pingShenStepId);
                        judgeWork.judgeWorkPK = judgeWorkPK;
                        judgeWork.fen = 0;
                        judgeWork.fenJson = cj.pingShenFields;
                        judgeWork.shiPass = false;
                        judgeWork.competitionId = group.key.item1;
                        judgeWork.guiGeId = group.key.item2;
                        judgeWork.cancelLazyEr();
                        this.updateObject(judgeWork);
                    }
                }
            }
        }
        CurrentMasterCompetitionSetup currentMasterCompetitionSetup = new CurrentMasterCompetitionSetup();
        CurrentMasterCompetitionSetupPK currentMasterCompetitionSetupPK = new CurrentMasterCompetitionSetupPK(appId,"masterCompetitionStatus");
        currentMasterCompetitionSetup.currentMasterCompetitionSetupPK = currentMasterCompetitionSetupPK;
        currentMasterCompetitionSetup.value = pingShenStepId;
        this.updateTheObject(currentMasterCompetitionSetup);
        throw new Exception("test");
    }
}
