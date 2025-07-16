package com.weavict.competition.module

import com.weavict.competition.entity.Work
import com.weavict.competition.entity.WorkItem
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
}
