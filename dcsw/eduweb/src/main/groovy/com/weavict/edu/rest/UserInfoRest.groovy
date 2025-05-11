package com.weavict.edu.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.weavict.edu.entity.Buyer
import com.weavict.common.util.DateUtil
import com.weavict.edu.entity.Manager
import com.weavict.edu.entity.Parent
import com.weavict.edu.entity.Student
import com.weavict.edu.entity.StudentPK
import com.weavict.edu.entity.Teacher
import com.weavict.edu.module.SourcesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

/**
 * Created by Justin on 2018/6/10.
 */

@Path("/schooluser")
class UserInfoRest extends BaseRest
{
    @Autowired
    SourcesService sourcesService;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/parentinfo")
    Map<String,Object> getParentInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            if (query.p_id in [null,""])
            {
                return ["status":"OK","datas":["id":""]];
            }
            List l = sourcesService.queryParents(query.p_id,-1 as int,-1 as int,null,null,-1 as byte,null,null,null);
            if (l!=null && l.size()>0)
            {
                def p = l.get(0);
                return ["status":"OK","datas":["id":p[0],"name":p[3],"engname":p[4],"sex":p[5],
                        "phone1":p[6],"phone2":p[7],"phone3":p[8],
                        "areapath":p[9],"address":p[10],"birthdaydate":p[11],
                        "createdate":p[12],"wxid":p[13],"wxnickname":p[14],"headimgurl":p[15]]];
            }
            return ["status":"OK","datas":["id":""]];
        }
        catch (Exception e)
        {
            processExcetion(e);
            return ["status":"FA_ER"];
        }
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/studentInfo")
    String getStudentInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            StudentPK spk = new StudentPK(query.studentId as String,query.xiaoId as String);
            Student student = sourcesService.findObjectById(Student.class,spk);
            student.cancelLazyEr();
            if (student==null)
            {
                return """{"status":"FA_NOHAS"}""";
            }
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "student":({
                         return student;
                     }).call()]);
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
    String getTeacherInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            Teacher teacher = sourcesService.findObjectById(Teacher.class,query.id);
            if (teacher!=null)
            {
                teacher.cancelLazyEr();
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(
                        ["status": "OK",
                         "teacher": ({
                             return teacher;
                         }).call(),
                         "ext":({
                             ["createDate":DateUtil.format(teacher.createDate,"yyyy-MM-dd HH:mm:ss"),
                                  "birthdayDate":DateUtil.format(teacher.birthdayDate,"yyyy-MM-dd")]
                         }).call()
                        ]);
//                Gson gson = new Gson();
//                return ["status":"OK","teacher":gson.fromJson(gson.toJson(teacher),Map),
//                        "ext":["createDate":DateUtil.format(teacher.createDate,"yyyy-MM-dd HH:mm:ss"),
//                        "birthdayDate":DateUtil.format(teacher.birthdayDate,"yyyy-MM-dd")]];
            }
            return """{"status":"OK","teacher":{"id":""}}""";
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
    @Path("/managerinfo")
    String getManagerInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            Manager manager = sourcesService.findObjectById(Manager.class,query.id);
            if (manager!=null)
            {
                manager.cancelLazyEr();
                Gson gson = new Gson();
                return """{"status":"OK","manager":${gson.toJson(manager)},
"ext":{"createDate":"${DateUtil.format(manager.createDate,"yyyy-MM-dd HH:mm:ss")}",
"birthdayDate":"${DateUtil.format(manager.birthdayDate,"yyyy-MM-dd")}"}}
""";
            }
            else
            {
                return """{"status":"OK","manager":{"id":""}}""";
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
    @Path("/addparent")
    Map<String,Object> addParent(@RequestBody Map<String,Object> query)
    {
        try
        {
            Parent p = new Parent();
            if (query.p_createdate in [null,""])
            {
                p.createDate = new Date();
            }
            else
            {
                p.createDate = DateUtil.parse(query.p_createdate,"yyyy-MM-dd HH:mm:ss");
            }
            p.id = query.p_id;
            p.address = query.s_address;
            p.areaPath = query.s_area;
            p.engName = query.p_engname;
            p.name = query.p_name;
            p.phone1 = query.p_phone1;
            p.phone2 = query.p_phone2;
            p.phone3 = query.p_phone3;
            p.sex = query.p_sex as byte;
            p.wxid = query.p_wxid;
            p.wxNickName = query.p_wxnickname;
            p.headimgurl = query.p_wxheadimg;
            p.wxNickEm = query.p_wxnickem;

            if (query.s_name in [null,""] && query.s_engname in [null,""])
            {
                sourcesService.updateTheObject(p);
            }
            else
            {
                Student s = new Student();
                s.id = new StudentPK(-1,query.s_xiaoid as int);
                s.zxId = query.s_zxid;
                s.name = query.s_name;
                s.engName = query.s_engname;
                s.sex = query.s_sex as byte;
                s.createDate = new Date();
                s.birthdayDate = DateUtil.parse(query.s_birthday,"yyyy-MM-dd");
                s.areaPath = query.s_area;
                s.address = query.s_address;
                s.currentClass = query.s_currentclass;
                s.currentSchool = query.s_currentschool;
                s.status = query.s_status as byte;
                if (query.s_phone1 in [null,""])
                {
                    s.phone1 = query.p_phone1;
                }
                else {
                    s.phone1 = query.s_phone1;
                }
                s.phone2 = "";
                s.phone3 = "";
                s.wxid = "";
                s.wxNickName = "";
                s.wxNickEm = "";
                s.headimgurl = "";
                s.amb = 0;
                sourcesService.addTheStudent(s,p,query.s_cgx as byte);
            }
            return ["status":"OK"];
        }
        catch (Exception e)
        {
            processExcetion(e);
            return ["status":"FA_ER"];
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/bandingParent")
    Map<String,Object> bangdingTheParent(@RequestBody Map<String,Object> query)
    {
        try
        {
            Parent p = sourcesService.findObjectById(Parent.class,query.parentId);
            if (p==null)
            {
                p = new Parent();
                p.id = query.parentId;
                p.sex = 0 as byte;
                p.createDate = new Date();
            }
            sourcesService.bangdingTheParent(query.xiaoId as int,query.studentId as int,p,query.cgx as byte);
            return ["status":"OK"];
        }
        catch (Exception e)
        {
            processExcetion(e);
            return ["status": "FA_ER"];
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addteacher")
    Map<String,Object> addTeacher(@RequestBody Map<String,Object> query)
    {
        try
        {
//            Gson gson = new Gson();
//            Teacher teacher = gson.fromJson(gson.toJson(query.teacher),Teacher);
            Teacher teacher = this.objToBean(query.teacher,Teacher.class,null);
            teacher.birthdayDate = DateUtil.parse(query.ext.birthdayDate,"yyyy-MM-dd");
            if (query.ext.createDate in [null,""])
            {
                teacher.createDate = new Date();
            }
            else
            {
                teacher.createDate = DateUtil.parse(query.ext.createDate,"yyyy-MM-dd HH:mm:ss");
            }

            sourcesService.updateTheObject(teacher);
            return ["status":"OK"];
        }
        catch (Exception e)
        {
            processExcetion(e);
            return ["status":"FA_ER"];
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addmanager")
    Map<String,Object> addManager(@RequestBody Map<String,Object> query)
    {
        try
        {
            Gson gson = new Gson();
            Manager manager = gson.fromJson(gson.toJson(query.manager),Manager);
            manager.birthdayDate = DateUtil.parse(query.ext.birthdayDate,"yyyy-MM-dd");
            if (query.ext.createDate in [null,""])
            {
                manager.createDate = new Date();
            }
            else
            {
                manager.createDate = DateUtil.parse(query.ext.createDate,"yyyy-MM-dd HH:mm:ss");
            }

            sourcesService.updateTheObject(manager);
            return ["status":"OK"];
        }
        catch (Exception e)
        {
            processExcetion(e);
            return ["status":"FA_ER"];
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryParentStudent")
    String queryTheParentStudent(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "students": ({
                         return sourcesService.queryTheParentStudents(query.parentId);
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
    @Path("/updateUserInfo")
    Map<String,Object> updateUserInfo(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            if (query.userType.equals("parent"))
            {
                Parent parent = objToBean(query.obj,Parent.class,objectMapper);
                Parent p = sourcesService.findObjectById(Parent.class,parent.id);
                if (p==null && !(parent.id in [null,""]))
                {
                    parent.createDate = new Date();
                    sourcesService.updateTheObject(parent);
                }
                else
                {
                    p.headimgurl = parent.headimgurl;
                    p.wxNickEm = parent.wxNickEm;
                    p.wxNickName = parent.wxNickName;
                    p.wxUniId = parent.wxUniId;
                    p.wxid = parent.wxid;
                    sourcesService.updateTheObject(p);
                }
            }
            return ["status":"OK"];
        }
        catch (Exception e)
        {
            processExcetion(e);
            return ["status":"FA_ER"];
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/querySchoolHumans")
    String querySchoolHumans(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "humanList": ({
                         List l = new ArrayList();
                         sourcesService.querySchoolHumansList(query.zxId,query.xiaoId as int,query.status as byte)?.each {o->
                            l << ["id":o[0],"name":o[1],"engName":o[2]];
                         };
                         return l;
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
    @Path("/queryTeacher4LoginRule")
    String queryTeacher4LoginRule(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "teacher": ({
                         return sourcesService.queryTeachers(["status":0 as byte,"phone":query.phone])?.each {
                             it.cancelLazyEr();
                         };
                     }).call(),
                     "manager": ({
                         return sourcesService.queryManagers(["status":0 as byte,"phone":query.phone])?.each {
                             it.cancelLazyEr();
                         };
                     }).call(),
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
    @Path("/test")
    String test(@RequestBody Map<String,Object> query)
    {
        try
        {
            Teacher teacher = sourcesService.findObjectById(Teacher.class,query.id);
            Gson gson = new Gson();
            println """{"status":"ok","ds":${gson.toJson(teacher)}}""";
            return """{"status":"ok","ds":${gson.toJson(teacher)}}""";
        }
        catch(Exception e)
        {
            processExcetion(e);
        }

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryTheBuyer")
    String queryTheBuyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "buyer": ({
                         Buyer buyer = sourcesService.findObjectById(Buyer.class,query.phone);
                         buyer.cancelLazyEr();
                         return buyer;
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
    @Path("/editBuyer")
    String editBuyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper()
            Buyer buyer = objToBean(query.buyer, Buyer.class, objectMapper);
            Buyer by = sourcesService.findObjectById(Buyer.class,buyer.phone);
            by.area = buyer.area;
            by.address = buyer.address;
            by.sex = buyer.sex;
            by.wxNickName = buyer.wxNickName;
            by.wxNickEm = buyer.wxNickEm;
            by.headImgUrl = buyer.headImgUrl;
            by.loginName = buyer.loginName;
            by.password = buyer.password;
            by.wxid = buyer.wxid;
            by.name = buyer.name;
            by.tel = buyer.tel;
            by.description = buyer.description;
            by.wxopenid = buyer.wxopenid;
            by.amb = buyer.amb;
            sourcesService.updateTheObject(by);
//            不直接update buyer的原因是orgrationList join关系映射造成的错误
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
