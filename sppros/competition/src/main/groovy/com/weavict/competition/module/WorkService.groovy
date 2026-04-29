package com.weavict.competition.module

import com.bestvike.linq.Linq
import com.bestvike.tuple.Tuple
import com.bestvike.tuple.Tuple2
import com.weavict.competition.entity.Competition
import com.weavict.competition.entity.CompetitionJudge
import com.weavict.competition.entity.CompetitionJudgePK
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
                [sort: 0, id: 0, name: "作品初筛", type: 0],
                [sort: 1, id: 1, name: "作品第一轮评分", type: 1],
                [sort: 2, id: 2, name: "作品第二轮评分", type: 1]
        ]];
    }

    @Transactional
    void pingShenJudgesInit(String appId, String masterCompetitionId, byte pingShenStepId)
    {
        List<String> comList = [];
        List<String> ggList = [];
        MasterCompetition masterCompetition = this.findObjectById(MasterCompetition.class, masterCompetitionId);
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
                                competitionJudge.appId = appId;
                                this.updateObject(competitionJudge);
                            }
                        }
                    }
                }
            }
            //                json没有分组，查询数据库进一步判断
            else {
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
                            competitionJudge.appId = appId;
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
                        competitionJudge.appId = appId;
                        this.updateObject(competitionJudge);
                    }
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
                [sf: "pingshenfields", bf: "tempMap.pingShenFields"]
        ])
                .where("cj.appid = :appId", [appId: query.appId], null, { return true })
                .where("cj.mastercompetitionid = :masterCompetitionId", [masterCompetitionId: query.masterCompetitionId], "and", { return !(query.masterCompetitionId in [null, ""]) })
                .where("cj.competitionstatus = :competitionStatus", [competitionStatus: query.pingShenStepId], "and", { return true })
                .where("cj.judgeid = :judgeId", [judgeId: query.judgeId], "and", { return !(query.judgeId in [null, ""]) })
                .beanSetup(CompetitionJudge.class, null, null)
                .buildSql().run().content;
    }

    void pingShenWorksInit(String appId, String masterCompetitionId, byte pingShenStepId)
    {
        List<CompetitionJudge> competitionJudgeList = qyPingShenJudgeList([appId: appId, masterCompetitionId: masterCompetitionId, judgeId: null, pingShenStepId: pingShenStepId]);
        var groupList = Linq.of(competitionJudgeList).groupBy(cj -> new Tuple2(cj.competitionJudgePK.competitionId,cj.competitionJudgePK.guiGeId)
//        {
//            if (cj.competitionJudgePK.guiGeId != "-1") {
//                return cj.competitionJudgePK.guiGeId;
//            } else {
//                return cj.competitionJudgePK.competitionId;
//            }
//        }
        );
        for (var group : groupList)
        {
            println group.key;
            //            group is map,key is competitionId or guigeid; value is a list of CompetitionJudge. 即评委
            for (CompetitionJudge cj in group.value) {
//                println cj.competitionJudgePK.judgeId;
                if (pingShenStepId == 0 as byte) {
                    JudgeWork judgeWork = new JudgeWork();
                    JudgeWorkPK judgeWorkPK = new JudgeWorkPK(appId, cj.competitionJudgePK.judgeId, "", pingShenStepId);
                }
            }
        }
    }
}
