package com.weavict.competition.module

import com.bestvike.linq.Linq
import com.weavict.competition.entity.Competition
import com.weavict.competition.entity.GuiGe
import com.weavict.competition.entity.MCPageSetup
import com.weavict.competition.entity.MasterCompetition
import com.weavict.competition.entity.OrgHuman
import com.weavict.competition.entity.SiteCompetition
import com.weavict.competition.entity.SiteWorkItem
import com.weavict.competition.entity.Work
import com.weavict.competition.entity.WorkItem
//import static org.jooq.impl.DSL.*;
//import org.jooq.*;
//import org.jooq.impl.*;
import org.springframework.stereotype.Service

@Service("workService")
class WorkService extends ModuleBean
{
    List<Work> qyBuyerCompetitionWorkInfo(String userId,String competitionId)
    {
        List<Work> workList = this.newQueryUtils(false).masterTable("Work",null,null)
            .where("competition.id = :competitionId",["competitionId":competitionId],null,null)
            .where("buyer.phone = :userId",["userId":userId],"and",null)
            .buildSql().run().content;
        for(Work work in workList)
        {
            work.cancelLazyEr();
            List<WorkItem> workItemList = this.newQueryUtils(false).masterTable("WorkItem",null,null)
                .where("work.id = :workId",["workId":work.id],null,null)
                .buildSql().run().content;
            for(WorkItem workItem in workItemList)
            {
                workItem.cancelLazyEr();
            }
            work.workItemList = workItemList;
        }
        return workList;
    }

    SiteCompetition gainSiteCompetition(Map query)
    {
        SiteCompetition siteCompetition = this.findObjectById(SiteCompetition.class,query.id);
        siteCompetition?.cancelLazyEr();
        return siteCompetition;
    }

    List<SiteWorkItem> qySiteWorkItemList(Map query)
    {
        this.newQueryUtils(false).masterTable("SiteWorkItem",null,null)
                .where("appId=:appId",["appId":query.appId],null,{return true})
                .where("sourceId=:sourceId",["sourceId":query.sourceId],"and",{return true})
                .where("sourceType=:sourceType",["sourceType":query.sourceType as byte],"and",{return true})
                .where("type=:type",["type":query.type as byte],"and",{return true})
                .orderBy("createDate")
                .buildSql().run().content;
    }

    List<OrgHuman> qyOrgHumanList(Map query)
    {
        return this.newQueryUtils(false,false).masterTable(OrgHuman.class.name,"j",null)
                .where("j.appId = :appId",["appId":query.appId],null,{return true})
                .where("j.sourceType = :sourceType",["sourceType":query.sourceType],"and",{return true})
                .where("j.sourceId = :sourceId",["sourceId":query.sourceId],"and",{return true})
                .where("j.name like :name",["name":"%${query.name}%"],"and",{return !(query.name in [null,""])})
                .where("j.engName like :engName",["engName":"%${query.engName}%"],"and",{return !(query.engName in [null,""])})
                .orderBy("createDate")
                .buildSql().run().content;
    }

    List<MasterCompetition> qyMasterSiteCompetitionList(Map query)
    {
        List<MasterCompetition> masterCompetitionList = this.newQueryUtils(false).masterTable("MasterCompetition",null,null)
                .where("appId = :appId",["appId":query.appId],null,{return true})
                .where("id = :id",[id:query.id],"and",{!(query.id in [null,""])})
                .where("siteCompetition.id = :siteCompetitionId",["siteCompetitionId":query.siteCompetitionId],"and",{return true})
                .where("cptDate = :cptDate",["cptDate":query.cptDate],"and",{return query.cptDate!=null})
                .orderBy("cptDate,beginDate")
                .buildSql().run().content;
        for(MasterCompetition masterCompetition in masterCompetitionList)
        {
            masterCompetition.cancelLazyEr();
        }
        return masterCompetitionList;
    }

    List<Competition> qyCompetitionList(Map query)
    {
        List<Competition> competitionList = this.newQueryUtils(false).masterTable(Competition.class.simpleName,null,null)
                .where("appId = :appId",["appId":query.appId],null,{return true})
                .where("masterCompetition.id = :masterCompetitionId",["masterCompetitionId":query.masterCompetitionId],"and",{return true})
                .where("name = :name",["name":query.name],"and",{return query.name!=null})
//                                 .orderBy("id")
                .buildSql().run().content;
        if (query.shiQyGuiGeList!=null && query.shiQyGuiGeList as boolean)
        {
            for(Competition competition in competitionList)
            {
                competition.cancelLazyEr();
                competition.guiGeList = this.newQueryUtils(false).masterTable(GuiGe.class.simpleName,null,null)
                        .where("competition.id = :competitionId",["competitionId":competition.id],null,{return true})
                        .buildSql().run().content;
                for(GuiGe guiGe in competition.guiGeList)
                {
                    guiGe.cancelLazyEr();
                }
            }
        }
        return competitionList;
    }

    List<MCPageSetup> qyPageSetup(String competitionId,String key,String appId)
    {
        List<MCPageSetup> mcPageSetupList = this.newQueryUtils(false).masterTable(MCPageSetup.class.simpleName,null,null)
                .where("mcPageSetupPK.appId = :appId",["appId":appId],null,{return true})
                .where("mcPageSetupPK.competitionId = :competitionId",["competitionId":competitionId],"and",{return true})
                .where("mcPageSetupPK.key = :key",["key":key],"and",{return !(key in [null,""])})
                .buildSql().run().content;
        for(MCPageSetup mcPageSetup in mcPageSetupList)
        {
            mcPageSetup.cancelLazyEr();
        }
        return mcPageSetupList;
    }

    Map qyPingShenFlow(Map query) {
        return [flow:[
                [sort:0,id:0,name:"作品初筛",type:0],
                [sort:1,id:1,name:"作品第一轮评分",type:1],
                [sort:2,id:2,name:"作品第二轮评分",type:1]
        ]];
    }

    void pingShenInit(String masterCompetitionId,int pingShenStepId)
    {
        List<String> comList = [];
        List<String> ggList = [];
        MasterCompetition masterCompetition = this.findObjectById(MasterCompetition.class,masterCompetitionId);
        for(def it in masterCompetition.judgeSetup.datas)
        {//it 为一个类别competition；it.data是评审的n个流程环节
            comList = [];
            for(def cit in it.data)
            {
                if (cit.id==pingShenStepId)
                {
                    for(def j in cit.setupData)
                    {
//                        println "cid:${it.id}  jid:${j.id}";
                        comList << j.id;
                    }
                }
            }

            //如果有分组GuiGe
            if (it.guiGeList.size()>0)
            {
                for(def gl in it.guiGeList)
                {//gl 为一个类别下的某一个guiGe规格；gl.data是评审的n个流程环节
                    ggList = [];
                    for(def glit in gl.data)
                    {
                        if (glit.id==pingShenStepId)
                        {
                            for(def j in glit.setupData)
                            {
//                                println "cid:${it.id} gid:${gl.id}  jid:${j.id}";
                                ggList << j.id;
                            }
                            //将类别的评委信息与规格进行交集合并
//                            println "======================begin=======================";
//                            println comList;
//                            println ggList;
//                            println Linq.of(comList).where(o -> !(o in ggList) ).toList();
//                            println "======================end==========================";
                            for(def x in Linq.of(comList).where(o -> !(o in ggList) ).toList())
                            {
                                ggList << x;
                            }
                            //ggList就是最后和类评委合并的评委集合，就可以开始进行分配作品的工作。

                        }
                    }
                }
            }
//                没有分组，comList里是最后的评委信息
            else
            {

            }
        }
    }
}
