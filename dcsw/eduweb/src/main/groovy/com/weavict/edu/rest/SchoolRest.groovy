package com.weavict.edu.rest

import com.aliyun.oss.OSSClient
import com.aliyun.oss.model.CannedAccessControlList
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.edu.entity.Buyer
import com.weavict.edu.entity.GroupStudentDtail
import com.weavict.edu.entity.GroupStudyDay
import com.weavict.edu.entity.GroupStudyDayPK
import com.weavict.edu.entity.HuoDongClass
import com.weavict.edu.entity.JifenGoLog
import com.weavict.edu.entity.Manager
import com.weavict.edu.entity.ProductsPrivater
import com.weavict.edu.entity.Student
import com.weavict.edu.entity.StudentKeShiInfo
import com.weavict.edu.entity.StudentPK
import com.weavict.edu.entity.StudentSals
import com.weavict.edu.entity.StudentSalsDetail
import com.weavict.edu.entity.StudentStudyReport
import com.weavict.edu.entity.StudentStudyReportPK
import com.weavict.edu.entity.TeachStudy
import com.weavict.edu.entity.Teacher
import com.weavict.edu.entity.TeacherGroup
import com.weavict.edu.entity.TeacherStudent
import com.weavict.edu.entity.TeacherStudentPK
import com.weavict.edu.module.PageUtil
import com.weavict.edu.module.SchoolService
import com.weavict.edu.module.SourcesService
import com.weavict.common.util.DateUtil
import com.weavict.common.util.MathUtil
import com.weavict.website.common.OtherUtils
import org.glassfish.jersey.server.JSONP
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.TransactionDefinition
import org.springframework.web.bind.annotation.RequestBody

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

import java.text.SimpleDateFormat

/**
 * Created by Justin on 2018/6/10.
 */
@Path("/school")
class SchoolRest extends BaseRest
{
    @Autowired
    SourcesService sourcesService;
    @Autowired
    SchoolService schoolService;

    @Context
    HttpServletRequest request;

    @POST
    @JSONP
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/teachergroup")
    String getTeacherGroupInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            if (!(query.teacherid in [""]))
            {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(
                        ["status": "OK",
                         "ds"    : ["groups" : ({
                             List ls = new ArrayList();
                             for (TeacherGroup g in sourcesService.queryGroups(query.xiaoId,query.teacherid, query.groupid, query.groupname, (query.qpf == "1" ? 0 as byte : -1 as byte), query.beginDate in [null, ""] ? null : DateUtil.parse(query.beginDate,"yyyy-MM-dd HH:mm:ss"), query.endDate in [null, ""] ? null : DateUtil.parse(query.endDate,"yyyy-MM-dd HH:mm:ss")))
                             {
                                 g.cancelLazyEr();
                                 ls << g;
                             }
                             return ls;
                         }).call()]
                        ]);
            }
            else
            {
                return """{"status":"OK","ds":{}}""";
            }
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER","ds":{}}""";
        }
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/groupinfo")
    String getGroupInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            Date jdate = DateUtil.parse(query.studyday, "yyyy-MM-dd");
//            Date beginDate  = DateUtil.parse("${jdate.getYear()}-${jdate.getMonth()}-${jdate.getDay()} 00:00:00","yyyy-MM-dd HH:mm:ss");
//            Date endDate  = DateUtil.parse("${jdate.getYear()}-${jdate.getMonth()}-${jdate.getDay()} 23:59:59","yyyy-MM-dd HH:mm:ss");
            List sts = sourcesService.queryStudentsByTeacher(query.groupid, null, null, null, null, null, null, null, null);
            List<GroupStudentDtail> gl = schoolService.queryGroupStudentDetail(query.groupid, null, null, null, DateUtil.format(jdate, "yyyyMMdd"), null, null);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "ds"    : ["thegroupinfo"    : ({
                         TeacherGroup tg = sourcesService.findObjectById(TeacherGroup.class, query.groupid);
                         tg?.cancelLazyEr();
                         return tg;
                     }).call(),
                                "thegroupstudents": ({
                                    return sts?.each {
                                        it.cancelLazyEr();
                                    }
                                }).call(),
                                "studentpfs"      : ({
                                    if (query.qpf?.equals("1"))
                                    {
                                        def map = [:];
                                        if (gl == null || gl.size() < 1)
                                        {
                                            map["pfs"] = gl;
                                            GroupStudyDay groupStudyDay = new GroupStudyDay();
                                            groupStudyDay.groupStudyDayPK = new GroupStudyDayPK();
                                            groupStudyDay.groupStudyDayPK.groupId = query.groupid;
                                            map["groupstudyday"] = groupStudyDay;
                                            return map;
                                        } else
                                        {
                                            map["pfs"] = gl;
                                            map["groupstudyday"]  = schoolService.queryGroupStudyDay(DateUtil.format(jdate, "yyyyMMdd"), query.groupid, null, null, null, null, null).get(0);
                                            return map;
                                        }
                                    } else
                                    {
                                        return [];
                                    }
                                }).call(),
                                "pfed"            : ({
                                    if (gl == null || gl.size() < 1)
                                    {
                                        return false;
                                    } else
                                    {
                                        return true;
                                    }
                                }).call()]
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
    @Path("/studentsinfo")
    String getStudents(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "ds"    : ["students": ({
                         List ls = new ArrayList();
//                         for(def s in sourcesService.queryStudentsByTeacher(query.groupid,query.teacherid,(query.studentid ? query.studentid as int : -1 as int),(query.xiaoid>-1 ? query.xiaoid as int : -1 as int),query.studentname,query.studentengname,query.teachername,query.teacherengname,query.groupname))
                         for (Student s in sourcesService.queryCjStudents(
                                 [
                                         "zxId": query.zxid,
                                         "xiaoId"           : (query.xiaoid == null) ? "" : query.xiaoid as String,
                                  "studentId"        : (query.studentid == null) ? "" : query.studentid as String,
                                  "name"             : query.name as String,
                                  "bname"             : query.bname as String,
                                  "engName"          : query.engName as String,
                                  "status"           : (query.status == null) ? -1 as byte : query.status as byte,
                                  "yue"              : (query.yue == null ? -1 : query.yue) as int,
                                  "syEeTimes"        : (query.syEeTimes == null ? -1 : query.syEeTimes) as int,
                                  "beginBirthDateDay": (query.beginbirthday in [null, ""]) ? null : DateUtil.parse("${query.beginbirthday} 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                                  "endBirthDateDay"  : (query.endbirthday in [null, ""]) ? null : DateUtil.parse("${query.endbirthday} 23:59:59", "yyyy-MM-dd HH:mm:ss")]
                         ))
                         {
                             ls << s;
                         }
                         return ls;
                     }).call()]
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
    @Path("/studentsInfo")
//    getStudents的新接口，运用了前后的数据库库对象对应
    String studentsInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "students": ({
                         List ls = sourcesService.queryCjStudents(
                                 [
                                         "zxId"             : query.zxid as String,
                                         "xiaoId"           : query.xiaoid,
                                  "studentId"        : query.studentid,
                                  "name"             : query.name as String,
                                  "bname"             : query.bname as String,
                                  "engName"          : query.engName as String,
                                         "loginPhone": query.loginPhone as String,
                                         "phone": query.phone as String,
                                  "status"           : (query.status == null) ? -1 as byte : query.status as byte,
                                  "yue"              : (query.yue == null ? -1 : query.yue) as int,
                                  "syEeTimes"        : (query.syEeTimes == null ? -1 : query.syEeTimes) as int,
                                  "beginBirthDateDay": (query.beginbirthday in [null, ""]) ? null : DateUtil.parse("${query.beginbirthday} 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                                  "endBirthDateDay"  : (query.endbirthday in [null, ""]) ? null : DateUtil.parse("${query.endbirthday} 23:59:59", "yyyy-MM-dd HH:mm:ss")]
                         )?.each {s->
                             s.cancelLazyEr();
                         }
                         if (ls==null || ls.size()<1)
                         {
                             return null;
                         }
                         return ls;
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
    @Path("/addteachergroup")
    String addTeacherGroupInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            TeacherGroup t = objToBean(query.teachergroup,TeacherGroup.class,objectMapper);
//            t.name = query.teachergroup.name;
//            t.status = query.teachergroup.status as byte;
//            t.teacherId = query.teacherId;
//            t.xiaoId = query.xiaoId as String;
//            t.beginDate = DateUtil.parse(query.teachergroup.beginDate,"yyyy-MM-dd");
//            t.endDate = DateUtil.parse(query.teachergroup.endDate,"yyyy-MM-dd");
//            t.weekDaysTime = query.teachergroup.weekDaysTime;
//            t.keSubject = query.teachergroup.keSubject;
            List<TeacherStudent> tstudents = new ArrayList();
            for (def s in t.tempMap.students)
            {
                TeacherStudent ts = new TeacherStudent();
                ts.teacherStudentPK = new TeacherStudentPK(t.xiaoId,t.teacherId, s.id.studentId, t.id);
                tstudents << ts;
            }
            sourcesService.addTeacherGroup(t, tstudents);
            return objectMapper.writeValueAsString(
                [
                    "status":"OK",
                    "groupInfo":["id":t.id,"name":t.name,"status":t.status,"createDate":t.createDate]
                ]
            );
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
    @Path("/addgrouppingfen")
    String addGroupJifen(@RequestBody Map<String, Object> query)
    {
        try
        {
            GroupStudyDay groupStudyDay = objToBean(query.groupStudyDay, GroupStudyDay.class,null );
            List groupStudentDtailList = objToBean(query.groupStudentDtailList, List.class,null );
            for(def gsd in groupStudentDtailList)
            {
                gsd.createDate = cn.hutool.core.date.DateUtil.date(gsd.createDate);
                gsd = gsd as GroupStudentDtail;
            }
            schoolService.processTeacherPingFen([
                    "groupStudyDay" : groupStudyDay,
                    "groupStudentDtailList"       : groupStudentDtailList
            ]);
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
    @Path("/groupReport8Date")
    String groupReport8Date(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    [
                            "status":"OK",
                            "datas":({
                                return schoolService.groupReport8Date(query.objIdSubStr,query.beginDateStr,query.endDateStr);
                            }).call()
                    ]
            );
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
    @Path("/secinfo")
    String secInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            Manager manager = schoolService.findObjectById(Manager.class, query.id);
            Teacher teacher = schoolService.findObjectById(Teacher.class, query.id);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "sec"   : ["is_manager": ({
                         return (manager == null || manager.status == 1) ? "0" : "1";
                     }).call(),
                                "manager"   : ({
                                    manager?.cancelLazyEr();
                                    return manager;
                                }).call(),
                                "is_teacher": ({
                                    return (teacher == null || teacher.status == 1) ? "0" : "1";
                                }).call(),
                                "teacher"   : ({
                                    teacher?.cancelLazyEr();
                                    return teacher;
                                }).call()]
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
    @Path("/teacherinfo")
    String teacherInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status"     : "OK",
                     "teacherinfo": ({
                         return sourcesService.queryTeachers(["companyId": query.conpanyId, "fxId": query.fxId, "status": query.status]).each {
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
    @Path("/updatestudentinfo")
    String updateStudentInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            Student student = null;
            if (query.studentId in [null,""] && query.xiaoId in [null,""] && query.student!=null)
            {
                student = objToBean(query.student,Student.class,null);
                schoolService.updateTheObject(student);
            }
            else
            {
                student = schoolService.findObjectById(Student.class, new StudentPK(query.studentId as int, query.xiaoId as int));
                student.status = query.status as byte;
                student.currentClass = query.currentClass;
                student.currentSchool = query.currentSchool;

                student.birthdayDate = DateUtil.parse(query.birthdayDate as String, "yyyy-MM-dd");
                student.engName = query.engName;
                student.name = query.name;
                student.keLong = query.keLong == null ? null : query.keLong as int;
                student.keTimes = query.keTimes == null ? null : query.keTimes as int;
                student.keRuned = query.keRuned == null ? null : query.keRuned as int;
                student.phone1 = query.phone1;
                student.phone2 = query.phone2;
                student.phone3 = query.phone3;
                student.sex = query.sex as byte;
                student.amb = query.amb == null ? null : query.amb as int;
                schoolService.updateTheObject(student);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status"  : "OK",
                     "student" : ({
                         student.cancelLazyEr();
                         return student;
                     }).call(),
                     "birthday": ({
                         return student.birthdayDate == null ? "" : DateUtil.format(student.birthdayDate, "yyyy-MM-dd");
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
    @Path("/tjKeQing")
    String tongjiKeQing(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status"        : "OK",
                     "keTimesedCount": ({
                         return schoolService.queryTheStudentKe([
                                 "status"   : (query.status in [null, ""]) ? query.status as byte : -1 as byte,
                                 "studentid": query.studentId,
                                 "groupid"  : query.groupId,
                                 "teacherid": query.teacherId
                         ]);
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
    @Path("/queryStudentStudyReport")
    String queryStudentStudyReport(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = objectMappingIntance("yyyy-MM-dd",null);
            return objectMapper.writeValueAsString(
                    ["status"        : "OK",
                     "datas": ({
                         return schoolService.newQueryUtils(false).masterTable(StudentStudyReport.simpleName,"sr",null)
                            .where("sr.studentStudyReportPK.groupId = :groupId",["groupId":query.groupId],null,null)
//                            .where("sr.studentStudyReportPK.beginDateStr = :beginDateStr",["beginDateStr":query.beginDate],"and",null)
//                            .where("sr.studentStudyReportPK.endDateStr = :endDateStr",["endDateStr":query.endDate],"and",null)
                            .orderBy("sr.endDate desc")
                            .buildSql().run();
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
    @Path("/buildStudentStudyReport8Days")
    String buildStudentStudyReport8Days(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            StudentStudyReport studentStudyReport = this.objToBean(query.studentStudyReport,StudentStudyReport.class,objectMapper);
            studentStudyReport.dataUrl = "report/${query.xiaoId}/${studentStudyReport.studentStudyReportPK.groupId}_${MathUtil.getPNewId()}.json";
            return objectMapper.writeValueAsString(
                    ["status"        : "OK",
                     "studentStudyReport":studentStudyReport,
                     "datas": ({
                         TeacherGroup group = schoolService.buildStudentStudyReport8Days(query.assessId,studentStudyReport);
                         schoolService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                             OSSClient ossClient = OtherUtils.genOSSClient();
                             ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), studentStudyReport.dataUrl, new ByteArrayInputStream(objectMapper.writeValueAsString(
                                     group
                             ).getBytes("UTF-8")));
                             ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), studentStudyReport.dataUrl, CannedAccessControlList.PublicRead);
                             ossClient.shutdown();
                             schoolService.updateObject(studentStudyReport);
                         });
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
    @Path("/delStudentStudyReport")
    String delStudentStudyReport(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            StudentStudyReport studentStudyReport = this.objToBean(query.studentStudyReport, StudentStudyReport.class,objectMapper);
            schoolService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                if (!(studentStudyReport.dataUrl in [null,""]))
                {
                    //oss
                    OSSClient ossClient = OtherUtils.genOSSClient();
                    ossClient.deleteObject(OtherUtils.givePropsValue("ali_oss_bucketName"), studentStudyReport.dataUrl);
                    ossClient.shutdown();
                    //oss end
                    schoolService.deleteTheObject8Fields(StudentStudyReport.simpleName,"studentStudyReportPK = :studentStudyReportPK",["studentStudyReportPK":studentStudyReport.studentStudyReportPK],false);
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
    @Path("/querypfreport")
    String queryPingfenReport(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "report": ({
                         return schoolService.queryPfReprot(
                                 ["xiaoId"   : query.xiaoId, "groupId":query.groupId,"teacherId":query.teacherId,"studentId": query.studentId,
                                  "beginDate": query.beginDate, "endDate": query.endDate],
                                 0, 0);
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
    @Path("/queryRunedTimes8TeacherReport")
    String queryRunedTimes8TeacherReport(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "report": ({
                         return schoolService.queryRunedTimes8Teacher(query.teacherId,query.groupId,query.beginDate,query.endDate);
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
    @Path("/queryParentstudents")
    String queryStudentByParent(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status"  : "OK",
                     "students": ({
                         return sourcesService.queryStudents(query.parentId, query.studentId, query.xiaoId, query.name, query.engName, query.sex as byte, query.sphone, query.pphone, query.sbirthday, query.sbirthdayStyle);
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
    @Path("/addTeachStudy")
    String addTeachStudy(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            TeachStudy teachStudy = objToBean(query.teachStudy, TeachStudy.class, objectMapper);
            teachStudy.id = MathUtil.getPNewId();
            teachStudy.createDate = new Date();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "teachStudy": ({
                         schoolService.updateTheObject(teachStudy);
                         teachStudy.cancelLazyEr();
                         return teachStudy;
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
    @Path("/jifenGo")
    String jifenGo(@RequestBody Map<String, Object> query)
    {
        try
        {
            JifenGoLog log = objToBean(query.jifenGoLog, JifenGoLog.class, null);
            Student student = schoolService.findObjectById(Student.class,log.student.id);
            if (student.amb < log.productJifen)
            {
                return """{"status":"NOFULL"}""";
            }
            else
            {
                schoolService.jifenGo(log);
                return """{"status":"OK"}""";
            }
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
    @Path("/studentSalsInfo")
    String studentSalsInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "studentSals": ({
                         StudentSals studentSals = sourcesService.findObjectById(StudentSals.class,query.studentSalsId);
                         studentSals.cancelLazyEr();
                         return studentSals;
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
    @Path("/studentSalsDetailInfo")
    String studentSalsDetailInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "studentSalsDetail": ({
                         StudentSalsDetail studentSalsDetail =  sourcesService.findObjectById(StudentSalsDetail.class,query.studentSalsDetailId);
                         studentSalsDetail.cancelLazyEr();
                         return studentSalsDetail;
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
    @Path("/queryStudentSalsPageUtil")
    String queryStudentSalsPageUtil(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "studentsalsPageUtil": ({
                         List l = new ArrayList();
                         PageUtil pageUtil = sourcesService.queryStudentSalsList(query.zxId,query.fxId,query.createrId,query.name,query.status as byte,DateUtil.parse(query.beginDate,"yyyy-MM-dd"),DateUtil.parse(query.endDate,"yyyy-MM-dd"),query.currentSchool,query.currentClass,query.phone,query.address,query.sourceId,query.currentPage as int,query.pageSize as int);
                         pageUtil.content?.each {o->
                            l << ["tName":o[0],"id":o[1],"name":o[2],"engName":o[3],
                            "birthdayDate":o[4],"sex":o[5],
                            "phone1":o[6],"phone2":o[7],"phone3":o[8],
                            "areaPath":o[9],"address":o[10],"currentSchool":o[11],"currentClass":o[12],
                            "zxId":o[13],"xiaoId":o[14],"createrId":o[15],"createDate":o[16],
                            "tEngName":o[17],"mName":o[18],"mEngName":o[19],"age":o[20],"status":o[21]];
                         }
                         pageUtil.content = l;
                         return pageUtil;
                     }).call()
                    ]);
        }
        catch(Exception e)
        {
            processExcetion(e);
            return ["status":"FA_ER"];
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryStudentSalsDetailList")
    String queryStudentSalsDetailList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "studentSalsDetailList": ({
                         List l = new ArrayList();
                         sourcesService.queryStudentSalsDetailList(query.studentSalsId)?.each {o->
                            l << ["intention":o[0],"description":o[1],"tName":o[2],"tEngName":o[3],
                            "modifyDate":o[4],"tHeadImg":o[5],
                            "id":o[6],"nextDate":o[7],"nextDescription":o[8],"tNextName":o[9]
                            ];
                         };
                         return l;
                     }).call()
                    ]);
        }
        catch(Exception e)
        {
            processExcetion(e);
            return ["status":"FA_ER"];
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryStudentSalsDetail4Days")
    String queryStudentSalsDetail4Days(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "studentSalsDetailList": ({
                         return schoolService.newQueryUtils(true,true)
                                 .masterTable("studentsalsdetail","sd",[
                                         ["sf":"id","bf":"id"],
                                         ["sf":"intention","bf":"intention"],
                                         ["sf":"modifydate","bf":"modifyDate"],
                                         ["sf":"modifyId","bf":"modifyId"],
                                         ["sf":"description","bf":"description"],
                                         ["sf":"nextdate","bf":"nextDate"],
                                         ["sf":"nextdescription","bf":"nextDescription"],
                                         ["sf":"humanId","bf":"humanId"],
                                         ["sf":"nextHumanId","bf":"nextHumanId"]
                                 ])
                                 .joinTable("studentsals","ss","left join","ss.id = sd.studentsals_id",[
                                         ["sf":"id","bf":"studentSals.id"],
                                         ["sf":"zxid","bf":"studentSals.xiaoId"],
                                         ["sf":"xiaoid","bf":"studentSals.xiaoId"],
                                         ["sf":"name","bf":"studentSals.name"],
                                         ["sf":"engname","bf":"studentSals.engName"],
                                         ["sf":"phone1","bf":"studentSals.phone1"],
                                         ["sf":"phone2","bf":"studentSals.phone2"],
                                         ["sf":"phone3","bf":"studentSals.phone3"],
                                         ["sf":"sex","bf":"studentSals.sex"],
                                         ["sf":"address","bf":"studentSals.address"],
                                         ["sf":"areaPath","bf":"studentSals.areaPath"],
                                         ["sf":"currentSchool","bf":"studentSals.currentSchool"],
                                         ["sf":"currentClass","bf":"studentSals.currentClass"],
                                         ["sf":"birthdayDate","bf":"studentSals.birthdayDate"],
                                         ["sf":"sourceType","bf":"studentSals.sourceType"],
                                         ["isCop":true,"cop":"(select name from usershop where user_id = sd.humanid and shop_id = ss.xiaoid)","sf":"tName","bf":"tempMap.tName"],
                                         ["isCop":true,"cop":"(select name from usershop where user_id = sd.nexthumanid and shop_id = ss.xiaoid)","sf":"tNextName","bf":"tempMap.tNextName"]
                                 ])
                                 .where("sd.modifydate between :beginDate and :endDate",["beginDate":DateUtil.parse(query.beginDate,"yyyy-MM-dd HH:mm:ss"),"endDate":DateUtil.parse(query.endDate,"yyyy-MM-dd HH:mm:ss")],null,null)
                                 .where("ss.xiaoid = :xiaoId",["xiaoId":query.xiaoId],"and",{return !(query.xiaoId in [null,""])})
                                 .where("ss.zxid = :zxId",["zxId":query.zxId],"and",{return !(query.zxId in [null,""])})
                                 .orderBy("ss.xiaoid,sd.modifydate desc")
                                 .beanSetup(StudentSalsDetail.class,null,null)
                                 .buildSql().run();
                     }).call()
                    ]);
        }
        catch(Exception e)
        {
            processExcetion(e);
            return ["status":"FA_ER"];
        }
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/updateStudentSals")
    String updateStudentSals(@RequestBody Map<String, Object> query)
    {
        try
        {
            StudentSals studentSals = objToBean(query.studentSals, StudentSals.class, null);
            if (studentSals.id in [null,""])
            {
                studentSals.id = MathUtil.getPNewId();
                studentSals.createDate = new Date();
            }
            this.sourcesService.updateTheObject(studentSals);
            return """{"status":"OK","id":"${studentSals.id}"}""";
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
    @Path("/updateStudentSalsDetail")
    String updateStudentSalsDetail(@RequestBody Map<String, Object> query)
    {
        try
        {
            StudentSalsDetail studentSalsDetail = objToBean(query.studentSalsDetail, StudentSalsDetail.class, null);
            println studentSalsDetail.dump();
            if (studentSalsDetail.id in [null,""])
            {
                studentSalsDetail.id = MathUtil.getPNewId();
                studentSalsDetail.modifyDate = new Date();
            }
            this.sourcesService.updateTheObject(studentSalsDetail);
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
    @Path("/delTheStudentSalsDetail")
    String delTheStudentSalsDetail(@RequestBody Map<String, Object> query)
    {
        try
        {
            this.sourcesService.delTheStudentSalsDetail(query.studentSalsDetailId);
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
    @Path("/studentAmb2BuyerAmb")
    String studentAmb2BuyerAmb(@RequestBody Map<String, Object> query)
    {
        try
        {
            Student student = this.sourcesService.findObjectById(Student.class,new StudentPK(query.studentId as int,query.xiaoId as int));
            if (student.amb <= 0)
            {
                return """{"status":"FA_ZERO"}""";
            }
            Buyer buyer = this.sourcesService.findObjectById(Buyer.class,query.buyerPhone as String);
            buyer.amb = (student.amb==null ? 0 as int : student.amb as int) + (buyer.amb==null ? 0 as int : buyer.amb as int);
            student.amb = 0;
            this.sourcesService.updateTheObject(buyer);
            this.sourcesService.updateTheObject(student);
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
    @Path("/queryHuoDongList8School")
    String queryHuoDongList8School(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "huoDongList": ({
                         return schoolService.queryHuoDongList8School(query.zxId,query.xiaoId);
                     }).call()
                    ]);
        }
        catch(Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryFootageTypeList")
    String queryFootageTypeList(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "footageTypeList": ({
                         return schoolService.queryFootageTypeList(query.appId,query.zxId,query.xiaoId);
                     }).call()
                    ]);
        }
        catch(Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delFootageType")
    String delFootageType(@RequestBody Map<String, Object> query)
    {
        try
        {
            if (schoolService.queryFootageCount8Type(query.typeId) > 0)
            {
                return """{"status":"FA_NOTNULL"}""";
            }
            else
            {
                schoolService.delFootageType(query.typeId);
                return """{"status":"OK"}""";
            }
        }
        catch(Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryFootageList")
    String queryFootageList(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "footageList": ({
                         return schoolService.queryFootageList(query.appId,query.zxId,query.xiaoId,query.name,
                                 query.beginDate==null ? null : cn.hutool.core.date.DateUtil.parse(query.beginDate),
                                    query.endDate==null ? null : cn.hutool.core.date.DateUtil.parse(query.endDate),query.footageTypeId);
                     }).call()
                    ]);
        }
        catch(Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/updateTheHuoDong")
    String updateTheHuoDong(@RequestBody Map<String, Object> query)
    {
        OSSClient ossClient = null;
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "huoDong": ({
                         HuoDongClass huoDongClass = schoolService.updateTheHuoDong(this.objToBean(query.huoDong,HuoDongClass.class,objectMapper));
                         ProductsPrivater productsPrivater = schoolService.findObjectById(ProductsPrivater.class,huoDongClass.xiaoId);
                         productsPrivater.cancelLazyEr();
                         huoDongClass.tempMap = [:];
                         huoDongClass.tempMap["productsPrivater"] = productsPrivater;

                         // oss
                         ossClient = OtherUtils.genOSSClient();
                         ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "privater/${huoDongClass.xiaoId}/hd/${huoDongClass.id}.json", new ByteArrayInputStream(objectMapper.writeValueAsString(huoDongClass).getBytes("UTF-8")));
                         ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "privater/${huoDongClass.xiaoId}/hd/${huoDongClass.id}.json", CannedAccessControlList.PublicRead);

                         //临时用一下，向oss生成购买人员数据
                         List studentSalsList = new ArrayList();
                         PageUtil pageUtil = sourcesService.queryStudentSalsList(huoDongClass.zxId,huoDongClass.xiaoId,null,null,-1 as byte,DateUtil.parse("2023-01-01","yyyy-MM-dd"),DateUtil.parse("2099-01-01","yyyy-MM-dd"),null,null,null,null,null,0 as int,100 as int);
                         pageUtil.content?.each {o->
                             studentSalsList << ["tName":o[0],"id":o[1],"name":o[2],"engName":o[3],
                                   "birthdayDate":o[4],"sex":o[5],
                                   "phone1":o[6],"phone2":o[7],"phone3":o[8],
                                   "areaPath":o[9],"address":o[10],"currentSchool":o[11],"currentClass":o[12],
                                   "zxId":o[13],"xiaoId":o[14],"createrId":o[15],"createDate":o[16],
                                   "tEngName":o[17],"mName":o[18],"mEngName":o[19],"age":o[20]];
                         }
                         ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "privater/${huoDongClass.xiaoId}/hd/${huoDongClass.id}_sales.json", new ByteArrayInputStream(objectMapper.writeValueAsString(studentSalsList).getBytes("UTF-8")));
                         ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "privater/${huoDongClass.xiaoId}/hd/${huoDongClass.id}_sales.json", CannedAccessControlList.PublicRead);
                         // oss end
                         return huoDongClass;
                     }).call()
                    ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
        finally
        {
            // 关闭OSSClient。
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryStudentKeShiInfo")
    String queryStudentKeShiInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "datas": ({
                         return schoolService.newQueryUtils(true).masterTable("studentkeshiinfo",null,["id","studentId","xiaoId","type","fee","keLong","keTimes","createDate","modifier","remark"])
                                                                        .joinTable("student",null,"left join","student.studentid=studentkeshiinfo.studentid and student.xiaoid=studentkeshiinfo.xiaoid",["name","sex","status","kelong","ketimes","keruned","zxid"])
                                                                        .where("studentkeshiinfo.studentid=:studentId and studentkeshiinfo.xiaoid=:xiaoId",["studentId":query.studentId as String,"xiaoId":query.xiaoId as String],null,null)
                                                                        .orderBy("studentkeshiinfo.createdate")
                                                                        .beanSetup(StudentKeShiInfo.class,["id", "studentId", "xiaoId", "type", "fee", "keLong", "keTimes", "createDate", "modifier", "remark"],["name", "sex", "status", "kelong", "ketimes", "keruned", "zxid"])
//                                                                        .pageLimit(999999,1,"studentkeshiinfo.id")
                                                                        .buildSql()
                                                                        .run();
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
    @Path("/updateTheStudentKeShiInfo")
    String updateTheStudentKeShiInfo(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            schoolService.updateTheStudentKeShiInfo(this.objToBean(query.studentKeShiInfo,StudentKeShiInfo.class,objectMapper),query.isDel as boolean)
            return """{"status":"OK"}""";
        }
        catch(Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

}
