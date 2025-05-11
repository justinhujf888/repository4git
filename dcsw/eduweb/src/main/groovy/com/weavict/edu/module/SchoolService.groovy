package com.weavict.edu.module


import com.weavict.edu.entity.Assess
import com.weavict.edu.entity.AssessAnswerReport
import com.weavict.edu.entity.AssessItems
import com.weavict.edu.entity.Footage
import com.weavict.edu.entity.FootageType
import com.weavict.edu.entity.GroupStudentDtail
import com.weavict.edu.entity.GroupStudyDay
import com.weavict.edu.entity.HuoDongClass
import com.weavict.edu.entity.JifenGoLog
import com.weavict.edu.entity.ProductsPrivater
import com.weavict.edu.entity.Student
import com.weavict.edu.entity.StudentKeShiInfo
import com.weavict.edu.entity.StudentPK
import com.weavict.edu.entity.StudentStudyReport
import com.weavict.edu.entity.TeacherGroup
import com.weavict.edu.entity.UserShop
import com.weavict.edu.entity.UserShopPK
import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Transactional

import jakarta.inject.Inject
import org.springframework.transaction.support.DefaultTransactionDefinition

@Service("schoolBean")
class SchoolService extends ModuleBean
{
    @Inject
    SourcesService sourceService;

    Integer amb2Samb(Integer ambValue)
    {
        return ambValue / 40;
    }

    Integer samb2Rmb(Integer sambValue)
    {
        return sambValue / 100;
    }

    @Transactional
    void processTeacherPingFen(Map pm)
    {
        GroupStudyDay groupStudyDay = pm["groupStudyDay"] as GroupStudyDay;
        this.updateObject(groupStudyDay);
        for (GroupStudentDtail gsd in pm["groupStudentDtailList"])
        {
            Student student = this.findObjectById(Student.class, new StudentPK(gsd.groupStudentDtailPK.studentId, gsd.groupStudentDtailPK.xiaoId));
            gsd.samb = 0;//amb2Samb(gsd.amb);
            gsd.totalAmb = student.amb;//gsd.samb + student.amb;
            this.updateObject(gsd);
            if (gsd.status == 1 as byte)
            {
                this.executeEQL("update Student set keRuned = keRuned + 1 where id.studentId = :studentId and id.xiaoId = :xiaoId",
                        ["studentId": gsd.groupStudentDtailPK.studentId,
                         "xiaoId"   : gsd.groupStudentDtailPK.xiaoId]);//amb = amb + :amb, "amb": gsd.samb

//                2024-05-27暂时取消，目前没有涉及积分产品的部分；包含上面代码amb换算赋值的部分
//                JifenGoLog jifenGoLog = new JifenGoLog();
//                jifenGoLog.id = MathUtil.getPNewId();
//                jifenGoLog.productId = "";
//                jifenGoLog.productJifen = gsd.amb;
//                jifenGoLog.jifenAfter = gsd.totalAmb;
//                jifenGoLog.createDate = new Date();
//                jifenGoLog.userId = """{"studentId":"${student.id.studentId}","xiaoId":"${student.id.xiaoId}"}""";
//                jifenGoLog.userType = 10;//学生获取积分
//                this.updateObject(jifenGoLog);
                //发送微信模板消息：课堂报告通知
                //传模板消息给授课老师，2024-05-27暂时取消，有可能不在使用
//                WxNotifiesFun.send_ClassReport_Message([
//                        "parentId":pm["teacherId"],
//                        "studentId":student.id.studentId,
//                        "studentName":student.name,
//                        "studyType":groupStudyDay.studyType,
//                        "xiaoId":gsd.groupStudentDtailPK.xiaoId,
//                        "xiao":pm["xiao"],
//                        "dateStr":gsd.groupStudentDtailPK.dateStr,
//                        "studyTime":DateUtil.format(groupStudyDay.createDate,"yyyy-MM-dd"),
//                        "teacherId":gsd.groupStudentDtailPK.teacherId,
//                        "teacherName":pm["teacherEngName"],
//                        "groupStudyDayId":groupStudyDay.groupStudyDayPK.dateStr,
//                        "groupId":groupStudyDay.groupStudyDayPK.groupId
//                ]);
                //传模板消息给关注该学生的家长；2024-05-27暂时取消，有可能不在使用
//                for (p in this.createNativeQuery("select parentid from studentparent where studentid = ${student.id.studentId} and xiaoid = ${gsd.groupStudentDtailPK.xiaoId}").getResultList())
//                {
//                    WxNotifiesFun.send_ClassReport_Message([
//                            "parentId":p as String,
//                            "studentId":student.id.studentId,
//                            "studentName":student.name,
//                            "studyType":groupStudyDay.studyType,
//                            "xiaoId":gsd.groupStudentDtailPK.xiaoId,
//                            "xiao":pm["xiao"],
//                            "dateStr":gsd.groupStudentDtailPK.dateStr,
//                            "studyTime":DateUtil.format(groupStudyDay.createDate,"yyyy-MM-dd"),
//                            "teacherId":gsd.groupStudentDtailPK.teacherId,
//                            "teacherName":"${pm["teacherEngName"]}",
//                            "groupStudyDayId":groupStudyDay.groupStudyDayPK.dateStr,
//                            "groupId":groupStudyDay.groupStudyDayPK.groupId
//                    ]);
//                }
            }
        }
    }

    List<GroupStudyDay> queryGroupStudyDay(String dateStr, String groupId, String studyType, String studySubType, String studyInfo, Date createBeginDate, Date createEndDate)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select g from GroupStudyDay as g where 1=1";
        if (!(dateStr in [null, ""]))
        {
            sbf << " and g.groupStudyDayPK.dateStr = :dateStr";
            paramsMap["dateStr"] = dateStr;
        }
        if (!(groupId in [null, ""]))
        {
            sbf << " and g.groupStudyDayPK.groupId = :groupId";
            paramsMap["groupId"] = groupId;
        }
        if (!(studyType in [null, ""]))
        {
            sbf << " and g.studyType like :studyType";
            paramsMap["studyType"] = "%${studyType}%";
        }
        if (!(studySubType in [null, ""]))
        {
            sbf << " and g.studySubType like :studySubType";
            paramsMap["studySubType"] = "%${studySubType}%";
        }
        if (!(studyInfo in [null, ""]))
        {
            sbf << " and g.studyInfo like :studyInfo";
            paramsMap["studyInfo"] = "%${studyInfo}%";
        }
        if (createBeginDate != null && createEndDate != null)
        {
            sbf << " and g.createDate between :beginDate and :endDate";
            paramsMap["beginDate"] = createBeginDate;
            paramsMap["endDate"] = createEndDate;
        }
        return this.queryObject(sbf.toString(), paramsMap);
    }

    List<GroupStudentDtail> queryGroupStudentDetail(String groupId, String teacherId, String xiaoId, String studentId, String dateStr, Date createBeginDate, Date createEndDate)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select g from GroupStudentDtail as g where 1=1";
        if (!(groupId in [null, ""]))
        {
            sbf << " and g.groupStudentDtailPK.groupId = :groupId";
            paramsMap["groupId"] = groupId;
        }
        if (!(teacherId in [null, ""]))
        {
            sbf << " and g.groupStudentDtailPK.teacherId = :teacherId";
            paramsMap["teacherId"] = teacherId;
        }
        if (!(xiaoId in [null, ""]))
        {
            sbf << " and g.groupStudentDtailPK.xiaoId = :xiaoId";
            paramsMap["xiaoId"] = xiaoId;
        }
        if (!(studentId in [null, ""]))
        {
            sbf << " and g.groupStudentDtailPK.studentId = :studentId";
            paramsMap["studentId"] = studentId;
        }
        if (!(dateStr in [null, ""]))
        {
            sbf << " and g.groupStudentDtailPK.dateStr = :dateStr";
            paramsMap["dateStr"] = dateStr;
        }
        if (createBeginDate != null && createEndDate != null)
        {
            sbf << " and g.createDate between :beginDate and :endDate";
            paramsMap["beginDate"] = createBeginDate;
            paramsMap["endDate"] = createEndDate;
        }
        return this.queryObject(sbf.toString(), paramsMap);
    }

    int queryTheStudentKe(Map pm)
    {
        StringBuffer sbf = new StringBuffer();
        sbf << "select count(1) from groupstudentdtail where 1=1";
        if (!(pm["groupid"] in [null, ""]))
        {
            sbf << " and groupid = '${pm["groupid"]}'";
        }
        if (!(pm["studentid"] in [null, ""]))
        {
            sbf << " and studentid = '${pm["studentid"]}'";
        }
        if (!(pm["teacherid"] in [null, ""]))
        {
            sbf << " and teacherid = '${pm["teacherid"]}'";
        }
        if ((pm["status"] as byte) > -1)
        {
            sbf << " and status = '${pm["status"]}'";
        }
        return this.em.createNativeQuery(sbf.toString().replaceAll("<script", "")).getSingleResult() as int;
    }

    List queryPfReprot(Map pm, int currentPageNu, int pageSize)
    {
        String xiaoQueryStr = pm["xiaoId"] in [null, ""] ? "" : "and gd.xiaoid = ${pm["xiaoId"]}"
        String groupQueryStr = pm["groupId"] in [null, ""] ? "" : "and gd.groupid = '${pm["groupId"]}'";
        String teacherQueryStr = pm["teacherId"] in [null, ""] ? "" : " and gd.teacherid = '${pm["teacherId"]}'";
        StringBuffer sbf = new StringBuffer();
        sbf << """
select gd.datestr,gd.groupid,tg.name as groupname,gd.studentid,s.name as studentname,s.engname as studentengname,gd.teacherid,t.name as teachername,t.engname as teacherengname,gd.xiaoid,gd.amb,gd.createdate,gd.status,gs.studytype,gs.studyinfo from groupstudentdtail as gd
 left join groupstudyday as gs on gd.datestr = gs.datestr and gd.groupid = gs.groupid
  left join teacher as t on t.id = gd.teacherid
   left join student as s on s.studentid = gd.studentid and s.xiaoid = gd.xiaoid
    left join teachergroup as tg on tg.id = gd.groupid
    where 1=1 ${xiaoQueryStr} ${groupQueryStr} ${teacherQueryStr} and gd.studentid = ${pm["studentId"]} and (gd.createdate between '${pm["beginDate"]}' and '${pm["endDate"]}')
""";
        return this.em.createNativeQuery(sbf.toString().replaceAll("<script", "")).getResultList();
    }

    Map queryRunedTimes8Teacher(String teacherId, String groupId, String beginDate, String endDate)
    {
        String groupQueryStr = groupId in ["", null] ? "" : "and gsd.groupid = '${groupId}'";
        Map retuanMap = [:];
        retuanMap["totalRunedCount"] = this.createNativeQuery("""select count(gsd.studentid) from groupstudentdtail as gsd where gsd.teacherid = '${teacherId}' ${groupQueryStr} and (gsd.createdate between '${beginDate}' and '${endDate}') and gsd.status = '1'""").getSingleResult() as int;
        retuanMap["totalNoRunCount"] = this.createNativeQuery("""select count(gsd.studentid) from groupstudentdtail as gsd where gsd.teacherid = '${teacherId}' ${groupQueryStr} and (gsd.createdate between '${beginDate}' and '${endDate}') and gsd.status = '0'""").getSingleResult() as int;
        retuanMap["runedList"] = this.createNativeQuery("""select gsd.xiaoid,gsd.studentid,s.name,s.engname,count(gsd.studentid) from groupstudentdtail as gsd left join student as s on s.studentid = gsd.studentid and s.xiaoid = gsd.xiaoid where gsd.teacherid = '${teacherId}' ${groupQueryStr} and gsd.status = 1 and (gsd.createdate between '${beginDate}' and '${endDate}') group by gsd.studentid,s.name,s.engname,gsd.xiaoid""").getResultList();
        retuanMap["noRunList"] = this.createNativeQuery("""select gsd.xiaoid,gsd.studentid,s.name,s.engname,count(gsd.studentid) from groupstudentdtail as gsd left join student as s on s.studentid = gsd.studentid and s.xiaoid = gsd.xiaoid where gsd.teacherid = '${teacherId}' ${groupQueryStr} and gsd.status = 0 and (gsd.createdate between '${beginDate}' and '${endDate}') group by gsd.studentid,s.name,s.engname,gsd.xiaoid""").getResultList();
        return retuanMap;
    }

    @Transactional
    jifenGo(JifenGoLog log)
    {
        log.id = MathUtil.getPNewId();
        log.createDate = new Date();
        this.updateObject(log);
        this.executeEQL("update Student set amb = amb - :jifen where id = :id", ["jifen": log.productJifen, "id": log.student.id]);
    }

    String comFen2Word(int fen)
    {
        int f = 0;
        if (fen <= 4)
        {
            f = 4;
        }
        else if (fen >=4 && fen < 6)
        {
            f = 4;
        }
        else if (fen >= 6 && fen < 8)
        {
            f = 6;
        }
        else if (fen >= 8 && fen < 10)
        {
            f = 8;
        }
        else if (fen >=10)
        {
            f = 10;
        }
        return [4:"D",6:"C",8:"B",10:"A"][f];
    }

    TeacherGroup buildStudentStudyReport8Days(String assessId, StudentStudyReport studentStudyReport)
    {
        TeacherGroup group = this.findObjectById(TeacherGroup.class, studentStudyReport.studentStudyReportPK.groupId);
        List<Student> sts = sourceService.queryStudentsByTeacher(studentStudyReport.studentStudyReportPK.groupId, null, null, null, null, null, null, null, null);
        group.cancelLazyEr();
        group.tempMap = [:];
        List<GroupStudyDay> groupStudyDayList = this.newQueryUtils(false).masterTable("GroupStudyDay", "gsd", null)
                .where("gsd.groupStudyDayPK.groupId=:groupId", ["groupId": studentStudyReport.studentStudyReportPK.groupId], null, null)
                .where("gsd.createDate between :beginDate and :endDate", ["beginDate": studentStudyReport.beginDate, "endDate": studentStudyReport.endDate], "and", null)
                .orderBy("gsd.createDate desc")
                .buildSql().run().content;
        for (gsd in groupStudyDayList)
        {
            gsd.cancelLazyEr();
            gsd.tempMap = [:];
            gsd.tempMap.teacher = this.findObjectById(UserShop.class,new UserShopPK(gsd.teacherId,group.xiaoId));
            gsd.tempMap.teacher?.cancelLazyEr();
            gsd.tempMap.groupStudentDetailList = this.queryGroupStudentDetail(studentStudyReport.studentStudyReportPK.groupId, null, null, null, gsd.groupStudyDayPK.dateStr, null, null);
            for (gsdd in gsd.tempMap.groupStudentDetailList)
            {
                gsdd.cancelLazyEr();
            }
        }
        group.tempMap.groupStudyDayList = groupStudyDayList;
        List assessReportList = this.groupReport8Date(assessId, "pf_${group.id}_${group.xiaoId}", studentStudyReport.beginDate, studentStudyReport.endDate);
        for (student in sts)
        {
            student.tempMap = [:];
            student.tempMap.assessReportList = assessReportList.findAll {
                it.objId.indexOf(student.id.studentId) > -1;
            }
            student.tempMap.assessGroup = [:];
            student.tempMap.assessGroup.sumMap = [:];
            student.tempMap.assessGroup.groupList = student.tempMap.assessReportList.groupBy { it.assessItemDetailId }
            for (ag in student.tempMap.assessGroup.groupList)
            {
//                ag.sumValue = ag.value.transpose()*.sum();//sum{it.optionConent};
//                println ag.value.sum {
//                    it.optionContent;
//                }
//                println "${student.id.studentId}: ${ag.key} ${ag.value.size()}";
                student.tempMap.assessGroup.sumMap[ag.key] = ag.value.sum{
                    switch (it.optionContent)
                    {
                        case "A":
                            10;
                            break;
                        case "B":
                            8;
                            break;
                        case "C":
                            6;
                            break;
                        case "D":
                            4;
                            break;
                    }
                };
                student.tempMap.assessGroup.sumMap[ag.key] = comFen2Word((student.tempMap.assessGroup.sumMap[ag.key] / ag.value.size()) as int);
            }
            group.tempMap.studentList = sts;
//        this.updateObject(studentStudyReport);
        }

        Assess assess = this.findObjectById(Assess.class,assessId);
        assess.cancelLazyEr();
        assess.assessItemsList = this.newQueryUtils(false).masterTable("AssessItems","ai",null)
                .where("ai.assess.id = :assessId",["assessId":assessId],null,null)
                .orderBy("ai.sortNum")
                .buildSql().run().content;
        for(ai in assess.assessItemsList)
        {
            ai = ai as AssessItems;
            ai.cancelLazyEr();
            ai.assess = null;
            ai.assessItemDetailList = this.newQueryUtils(false).masterTable("AssessItemDetail","ad",null)
                    .where("ad.assessItems.id = :assessitemId",["assessitemId":ai.id],null,null)
                    .orderBy("ad.sortNum")
            .buildSql().run().content;
            for(ad in ai.assessItemDetailList)
            {
                ad.cancelLazyEr();
                ad.assessItems = null;
            }
        }

        group.tempMap.assess = assess;
        group.tempMap.studentStudyReport = studentStudyReport;
        group.tempMap.shop = this.findObjectById(ProductsPrivater.class,group.xiaoId);
        group.tempMap.shop.cancelLazyEr();

        return group;
    }

    List<HuoDongClass> queryHuoDongList8School(String zxId, String xiaoId)
    {
        if (!(zxId in [null, ""]))
        {
            return this.queryObject("select h from HuoDongClass h where h.zxId = :zxId", ["zxId": zxId]);
        } else if (!(xiaoId in [null, ""]))
        {
            return this.queryObject("select h from HuoDongClass h where h.xiaoId = :xiaoId", ["xiaoId": xiaoId]);
        }
    }

    List<FootageType> queryFootageTypeList(String appId, String zxId, String xiaoId)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select f.id,f.xiaoid,f.name,f.appid,pp.zxid from footagetype f left join productsprivater pp on f.xiaoid = pp.id where f.appid = :appId";
        paramsMap["appId"] = appId;
        if (!(xiaoId in [null, ""]))
        {
            sbf << " and (f.xiaoid = :xiaoId or pp.zxid = :zxId)";
            paramsMap["xiaoId"] = xiaoId;
        } else
        {
            sbf << " and pp.zxid = :zxId"
        }
        paramsMap["zxId"] = zxId;
        sbf << " order by f.id";
        List<FootageType> footageTypeList = new ArrayList();
        this.createNativeQuery4Params(sbf.toString(), paramsMap).getResultList()?.each {
            FootageType footageType = new FootageType();
            footageType.id = it[0];
            footageType.xiaoId = it[1];
            footageType.name = it[2];
            footageType.appId = it[3];
            footageType.tempMap = [:];
            footageType.tempMap["zxId"] = it[4];
            footageTypeList << footageType;
        };
        return footageTypeList;
    }

    List groupReport8Date(String assessId, String objIdSubStr, Date beginDate, Date endDate)
    {println beginDate;println endDate;
        return this.newQueryUtils(true).masterTable("assessanswerreport", "ar", ["id", "assessid", "assessitemid", "assessitemdetailid", "optionid", "optioncontent", "rightanswer", "objid", "createdate"])
                .where("ar.assessid = :assessId", ["assessId": assessId], null, null)
                .where("position(:objIdSubStr in ar.objid)=1", ["objIdSubStr": objIdSubStr], "and", null)
                .where("ar.createdate between :beginDate and :endDate", [beginDate: beginDate, endDate: endDate], "and", null)
                .beanSetup(AssessAnswerReport.class, ["id", "assessId", "assessItemId", "assessItemDetailId", "optionId", "optionContent", "rightAnswer", "objId", "createDate"], null)
                .buildSql().run().content;
    }

    List<Footage> queryFootageList(String appId, String zxId, String xiaoId, String name, Date beginDate, Date endDate, String footageTypeId)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select f.id,f.xiaoid,f.name,to_char(f.createdate,'yyyy-mm-dd') as createdate,f.appid,f.mediatype,f.poster,f.sourcepath,pp.zxid,f.footagetype_id from footage f left join productsprivater pp on pp.id = f.xiaoid where f.appid = :appId";
        paramsMap["appId"] = appId;
        if (!(xiaoId in [null, ""]))
        {
            sbf << " and (f.xiaoid = :xiaoId or pp.zxid = :zxId)";
            paramsMap["xiaoId"] = xiaoId;
            paramsMap["zxId"] = zxId;
        }
        if (!(zxId in [null, ""]))
        {
            sbf << " and pp.zxid = :zxId";
            paramsMap["zxId"] = zxId;
        }
        if (!(footageTypeId in [null, ""]))
        {
            sbf << " and f.footagetype_id = :footageTypeId";
            paramsMap["footageTypeId"] = footageTypeId;
        }
        if (!(name in [null, ""]))
        {
            sbf << " and f.name like :name";
            paramsMap["name"] = "%${name}%";
        }
        if (beginDate != null && endDate != null)
        {
            sbf << " and (f.createdate between :beginDate and :endDate)";
            paramsMap["beginDate"] = beginDate;
            paramsMap["endDate"] = endDate;
        }
        sbf << " order by f.createdate desc";
        List<Footage> footageList = new ArrayList();
        this.createNativeQuery4Params(sbf.toString(), paramsMap).getResultList()?.each {
            Footage footage = new Footage();
            footage.id = it[0];
            footage.xiaoId = it[1];
            footage.name = it[2];
            footage.createDate = DateUtil.parse(it[3], "yyyy-MM-dd");
            footage.appId = it[4];
            footage.mediaType = it[5] as byte;
            footage.poster = it[6];
            footage.sourcePath = it[7];
            footage.tempMap = [:];
            footage.tempMap["zxId"] = it[8];
            footage.footageType = new FootageType();
            footage.footageType.id = it[9];
            footageList << footage;
        };
        return footageList;
    }

    int queryFootageCount8Type(String typeId)
    {
        this.createNativeQuery4Params("select count(id) from footage where footagetype_id = :typeId", ["typeId": typeId]).getSingleResult() as int;
    }

    @Transactional
    void delFootageType(String id)
    {
        this.executeEQL("delete FootageType where id = :id", ["id": id]);
    }

    @Transactional
    HuoDongClass updateTheHuoDong(HuoDongClass huoDongClass)
    {
        if (huoDongClass.id in [null, ""])
        {
            huoDongClass.id = MathUtil.getPNewId();
        }
        if (huoDongClass.createDate == null)
        {
            huoDongClass.createDate = new Date();
            huoDongClass.modifyDate = huoDongClass.createDate;
        } else
        {
            huoDongClass.modifyDate = new Date();
        }
        return this.updateObject(huoDongClass);
    }

    @Transactional
    void updateTheStudentKeShiInfo(StudentKeShiInfo studentKeShiInfo, boolean isDel)
    {
        if (isDel)
        {
            this.executeEQL("delete StudentKeShiInfo where id = :id", ["id": studentKeShiInfo.id]);
            if (studentKeShiInfo.keTimes > 0)
            {
                studentKeShiInfo.keTimes = studentKeShiInfo.keTimes * -1;
            }
        } else
        {
            studentKeShiInfo.createDate = new Date();
            this.updateObject(studentKeShiInfo);
        }
        this.executeEQL("update Student set keTimes = keTimes + :keTimes,keLong = :keLong where id.studentId = :studentId and id.xiaoId = :xiaoId", ["studentId": studentKeShiInfo.studentId, "xiaoId": studentKeShiInfo.xiaoId, "keTimes": studentKeShiInfo.keTimes, "keLong": studentKeShiInfo.keLong]);
    }
}