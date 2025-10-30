package com.weavict.competition.module

import com.weavict.competition.entity.Competition
import com.weavict.competition.entity.GuiGe
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
                .where("sourceId=:sourceId",["sourceId":query.sourceId],null,{return true})
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
}
