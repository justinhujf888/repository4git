package com.weavict.edu.module


import com.weavict.common.util.DateUtil
import com.weavict.edu.entity.Manager
import com.weavict.edu.entity.Parent
import com.weavict.edu.entity.Student
import com.weavict.edu.entity.StudentParent
import com.weavict.edu.entity.StudentParentPK
import com.weavict.edu.entity.Teacher
import com.weavict.edu.entity.TeacherGroup
import com.weavict.edu.entity.TeacherStudent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("sourcesBean")
class SourcesService extends ModuleBean
{
    //insert into student (studentid,xiaoid,name) values ((select max(studentid)+1 from student where xiaoid=1),1,'ttttt');
    @Transactional
    void addTheStudent(Student student, Parent parent, byte cgx)
    {
        int studentId = this.em.createNativeQuery("""insert into student 
                                        (studentid,
                                        xiaoid,
                                        address,
                                        areapath,
                                        birthdaydate,
                                        createdate,
                                        currentclass,
                                        currentschool,
                                        engname,
                                        headimgurl,
                                        name,
                                        phone1,
                                        phone2,
                                        phone3,
                                        sex,
                                        wxnickname,
                                        wxid,
                                        status,
                                        amb,
                                        kelong,
                                        ketimes,
                                        keruned,zxid) 
                                     values 
                                     ((select COALESCE(max(studentid),0)+1 from student where xiaoid=${student.id.xiaoId}),
                                      ${student.id.xiaoId},
                                      '${student.address}',
                                      '${student.areaPath}',
                                      '${DateUtil.format(student.birthdayDate,"yyyy-MM-dd HH:mm:ss")}',
                                      '${DateUtil.format(student.createDate,"yyyy-MM-dd HH:mm:ss")}',
                                      '${student.currentClass}',
                                      '${student.currentSchool}',
                                      '${student.engName}',
                                      '${student.headimgurl}',
                                      '${student.name}',
                                      '${student.phone1}',
                                      '${student.phone2}',
                                      '${student.phone3}',
                                       ${student.sex},
                                       '${student.wxNickName}',
                                       '${student.wxid}',
                                       '${student.status}',
                                       ${student.amb},0,0,0,'${student.zxId}'
                                      ) RETURNING studentid;""").getSingleResult();
        parent.createDate = new Date();
        StudentParent sp = new StudentParent();
        sp.studentParentPK = new StudentParentPK(student.id.xiaoId,studentId,parent.id);
        sp.cgx = cgx;
        this.updateObject(parent);
        this.updateObject(sp);
    }

    @Transactional
    void bangdingTheParent(String xiaoId,int studentId,Parent parent,byte cgx)
    {
        StudentParent sp = new StudentParent();
        sp.studentParentPK = new StudentParentPK(xiaoId,studentId,parent.id);
        sp.createDate = new Date();
        sp.cgx = cgx;
        this.updateObject(parent);
        this.updateObject(sp);
    }

    List queryStudentsByTeacher(String groupId,String teacherId,String studentId,String xiaoId,String studentName,String studentEngName,String teacherName,String teacherEngName,String groupName)
    {
        return this.newQueryUtils(true).insertInsString("distinct","L").masterTable("student",null,["zxid","xiaoid","studentid","name","engname","phone1","phone2","phone3"])
            .joinTable("teacherstudent",null,"left join","student.studentid = teacherstudent.studentid and student.xiaoid = teacherstudent.xiaoid",["teacherid"])
            .joinTable("teachergroup",null,"left join","teacherstudent.groupid = teachergroup.id",["id","name"])
            .joinTable("usershop",null,"left join","usershop.user_id = teacherstudent.teacherid",["name"])
            .orderBy("student.studentid")
            .where("teachergroup.id = :groupId",["groupId":groupId],"",{return !(groupId in [null,""])})
            .where("teachergroup.name = :groupName",["groupName":groupName],"",{return !(groupName in [null,""])})
            .where("usershop.user_id = :teacherId",["teacherId":teacherId],"and",{return !(teacherId in [null,""])})
            .where("student.studentid = :studentId",["studentId":studentId],"and",{return !(studentId in [null,""])})
            .where("student.xiaoid = :xiaoId",["xiaoId":xiaoId],"and",{return !(xiaoId in [null,""])})
            .where("student.name = :studentName",["studentName":studentName,],"and",{return !(studentName in [null,""])})
            .where("usershop.name = :teacherName",["teacherName":teacherName],"and",{return !(teacherName in [null,""])})
            .where("student.engname = :studentEngName",["studentEngName":studentEngName],"and",{return !(studentEngName in [null,""])})
            .beanSetup(Student.class,["zxId","id.xiaoId","id.studentId","name","engName","phone1","phone2","phone3"],["teacherId","groupId","groupName","teacherName"])
            .buildSql().run().content;
    }

    List queryStudents(String parentId,String studentId,String xiaoId,String name,String engName,byte sex,String sphone,String pphone,String sbirthday,String sbirthdaystyle)
    {
//        Query nativeQuery = this.em.createNativeQuery("""select s.* from Student s left join StudentParent sp on sp.StudentId = s.StudentId where sp.parentId = '${parentId}'""");
//        nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean( Student));
//        return nativeQuery.getResultList();
        StringBuffer sbf = new StringBuffer();
        sbf << "select s.studentid,s.xiaoid,s.name,s.engname,s.sex,s.phone1,s.phone2,s.phone3,s.areapath,s.address,to_char(s.birthdaydate,'YYYY-MM-DD HH24:MI:SS') as birthdaydate,to_char(s.createdate,'YYYY-MM-DD HH24:MI:SS') as createdate,s.currentschool,s.currentclass,s.wxid,s.wxnickname,s.headimgurl,sp.cgx,s.amb,s.kelong,s.ketimes,s.keruned,s.zxid from Student s left join StudentParent sp on sp.StudentId = s.StudentId and sp.xiaoid = s.xiaoid where 1=1";
        if (!(parentId in [null,""]))
        {
            sbf << " and sp.parentId = '${parentId}'";
        }
        if (!(studentId in [null,""]))
        {
            sbf << " and s.studentId = '${studentId}'";
        }
        if (!(xiaoId in [null,""]))
        {
            sbf << " and s.xiaoId = '${xiaoId}'";
        }
        if (!(name in [null,""]))
        {
            sbf << " and s.name like '%${name}%'";
        }
        if (!(engName in [null,""]))
        {
            sbf << " and s.engName like '%${engName}%'";
        }
        if (sex>=0)
        {
            return " and s.sex = '${sex}'";
        }
        if (!(pphone in [null,""]))
        {
            sbf << " and (s.phone1 like '%${sphone}%' or s.phone2 like '%${sphone}%' or s.phone3 like '%${sphone}%')";
        }
        if (!(sphone in [null,""]))
        {
            sbf << " and (sp.phone1 like '%${pphone}%' or sp.phone2 like '%${pphone}%' or sp.phone3 like '%${pphone}%')";
        }
        if (!(sbirthday in [null,""]))
        {
            sbf << " and to_char(s.birthdaydate,'${sbirthdaystyle}')='${sbirthday}'";
        }
        this.em.createNativeQuery(sbf.toString().replaceAll("<script","")).getResultList();
    }

    List queryParents(String parentId,int studentId,String xiaoId,String name,String engName,byte sex,String pphone,String birthday,String birthdaystyle)
    {
        StringBuffer sbf = new StringBuffer();
        sbf << "select p.id,sp.studentid,sp.xiaoid,p.name,p.engname,p.sex,p.phone1,p.phone2,p.phone3,p.areapath,p.address,to_char(p.birthdaydate,'YYYY-MM-DD HH24:MI:SS') as birthdaydate,to_char(p.createdate,'YYYY-MM-DD HH24:MI:SS') as createdate,p.wxid,p.wxnickname,p.headimgurl,sp.cgx from parent as p left join studentparent as sp on p.id = sp.parentid where 1=1";
        if (studentId>=0)
        {
            return " and sp.studentId = ${studentId}";
        }
        if (xiaoId>=0)
        {
            return " and sp.xiaoId = ${xiaoId}";
        }
        if (!(parentId in [null,""]))
        {
            sbf << " and p.id = '${parentId}'";
        }
        if (!(name in [null,""]))
        {
            sbf << " and p.name like '%${name}%'";
        }
        if (!(engName in [null,""]))
        {
            sbf << " and p.engName like '%${engName}%'";
        }
        if (sex>=0)
        {
            return " and p.sex = ${sex}";
        }
        if (!(pphone in [null,""]))
        {
            sbf << " and (p.phone1 like '%${pphone}%' or p.phone2 like '%${pphone}%' or p.phone3 like '%${pphone}%')";
        }
        if (!(birthday in [null,""]))
        {
            sbf << " and to_char(p.birthdaydate,'${birthdaystyle}')='${birthday}'";
        }
        this.em.createNativeQuery(sbf.toString().replaceAll("<script","")).getResultList();
    }

    List queryTheParentStudents(String parentId)
    {
        //select s.xiaoid,s.studentid,s.name,p.id,p.name from studentparent as sp left join parent as p on p.id = sp.parentid left join student as s on s.studentid = sp.studentid and s.xiaoid = sp.xiaoid where sp.parentid = 'oL7Bq1GLmguB9KtjEC5JlFoW4zFc'
        return this.em.createNativeQuery("""select s.xiaoid,s.studentid,s.name as studentName,p.id,p.name as parentName,p.phone1 as phone from studentparent as sp left join parent as p on p.id = sp.parentid left join student as s on s.studentid = sp.studentid and s.xiaoid = sp.xiaoid where sp.parentid = '${parentId}'""".replaceAll("<script","")).getResultList();
    }

    List querySchoolHumansList(String zxId,String xiaoId,byte status)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        if (!(zxId in [null,""]))
        {
            sbf << " and zxId = :zxId";
            paramsMap["zxId"] = zxId;
        }
        if (status > -1)
        {
            sbf << " and status = :status";
            paramsMap["status"] = status;
        }
        return this.createNativeQuery4Params("""select distinct human.id,human.name,human.engname from (
select id,rtrim(ltrim(name)) as name,rtrim(ltrim(engname)) as engname from teacher where ${xiaoId>-1 ? "and status=${status}" : "1=1"} ${sbf.toString()}
union all
select id,rtrim(ltrim(name)),rtrim(ltrim(engname)) as engname from manager where 1=1 ${sbf.toString()} ) as human
""",paramsMap).getResultList();
    }

    List<TeacherGroup> queryGroups(String xiaoId, String teacherId, String groupId, String groupName, byte status, Date createBeginDate, Date createEndDate)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select g from TeacherGroup as g where 1=1";
        if (!(xiaoId in [null,""]))
        {
            sbf << " and g.xiaoId = :xiaoId";
            paramsMap["xiaoId"] = xiaoId;
        }
        if (!(teacherId in [null,""]))
        {
            sbf << " and g.teacherId = :teacherId";
            paramsMap["teacherId"] = teacherId;
        }
        if (!(groupId in [null,""]))
        {
            sbf << " and g.id = :id";
            paramsMap["id"] = groupId;
        }
        if (!(groupName in [null,""]))
        {
            sbf << " and g.name like :name";
            paramsMap["name"] = "%${groupName}%";
        }
        if (status > (-1 as byte))
        {
            sbf << " and g.status = :status";
            paramsMap["status"] = status;
        }
        if (createBeginDate!=null && createEndDate!=null)
        {
            sbf << " and g.createDate between :beginDate and :endDate";
            paramsMap["beginDate"] = createBeginDate;
            paramsMap["endDate"] = createEndDate;
        }
        sbf << " order by g.createDate desc";
        return this.queryObject(sbf.toString(),paramsMap);
    }

    List<Student> queryCjStudents(Map pm)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select t from Student as t where 1=1";
        if (!(pm["zxId"] in [null,""]))
        {
            sbf << " and t.zxId = :zxId";
            paramsMap["zxId"] = pm["zxId"];
        }
        if (!(pm["xiaoId"] in [null,""]))
        {
            sbf << " and t.id.xiaoId = :xiaoId";
            paramsMap["xiaoId"] = pm["xiaoId"];
        }
        if (!(pm["studentId"] in [null,""]))
        {
            sbf << " and t.id.studentId = :studentId";
            paramsMap["studentId"] = pm["studentId"];
        }
        if (!(pm["name"] in [null,""]))
        {
            sbf << " and t.name like :name";
            paramsMap["name"] = "%${pm["name"]}%" as String;
        }
        if (!(pm["bname"] in [null,""]))
        {
            sbf << " and t.name = :bname";
            paramsMap["bname"] = "${pm["bname"]}" as String;
        }
        if (!(pm["engName"] in [null,""]))
        {
            sbf << " and t.engName like :engName";
            paramsMap["engName"] = "%${pm["engName"]}%" as String;
        }
        if (!(pm["loginPhone"] in [null,""]))
        {
            sbf << " and t.phone1 = :loginPhone";
            paramsMap["loginPhone"] = pm["loginPhone"];
        }
        if (!(pm["phone"] in [null,""]))
        {
            sbf << " and (t.phone2 = :phone or t.phone3 = :phone)";
            paramsMap["phone"] = pm["phone"];
        }
        if (pm["status"] > -1)
        {
            sbf << " and t.status = :status";
            paramsMap["status"] = pm["status"];
        }
        if (pm["yue"] > -1)
        {
            sbf << " and month(t.birthdayDate) = :month";
            paramsMap["month"] = pm["yue"];
        }
        if (pm["syEeTimes"] > 0)
        {
            sbf << " and t.keTimes - t.keRuned <= :syEeTimes";
            paramsMap["syEeTimes"] = pm["syEeTimes"];
        }
        if (pm["beginBirthDateDay"]!=null && pm["endBirthDateDay"]!=null)
        {
            sbf << " and t.birthdayDate between :beginBirthDateDay and :endBirthDateDay";
            paramsMap["beginBirthDateDay"] = pm["beginBirthDateDay"];
            paramsMap["endBirthDateDay"] = pm["endBirthDateDay"];;
        }
        sbf << " order by t.id.studentId";
        return this.queryObject(sbf.toString(),paramsMap);
    }

    @Transactional
    void addTeacherGroup(TeacherGroup group, List<TeacherStudent> tstudents)
    {
        this.updateObject(group);
        this.executeEQL("delete TeacherStudent where teacherStudentPK.groupId = :groupId",["groupId":group.id]);
        for(TeacherStudent ts in tstudents)
        {
            this.updateObject(ts);
        }
    }

    List<Teacher> queryTeachers(Map p)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select t from Teacher as t where 1=1";
        if (p["status"]!=null)
        {
            sbf << " and t.status = :status";
            paramsMap["status"] = p["status"] as byte;
        }
        if (p["companyId"]!=null)
        {
            sbf << " and t.zxId = :zxId";
            paramsMap["zxId"] = p["companyId"];
        }
        if (p["fxId"]!=null)
        {
            sbf << " and t.xiaoId = :fxId";
            paramsMap["fxId"] = p["fxId"];
        }
        if (p["phone"]!=null)
        {
            sbf << " and (t.phone1 = :phone or t.phone2 = :phone or t.phone3 = :phone)";
            paramsMap["phone"] = p["phone"];
        }
        return this.queryObject(sbf.toString(),paramsMap);
    }

    List<Manager> queryManagers(Map p)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select m from Manager as m where 1=1";
        if (p["status"]!=null)
        {
            sbf << " and m.status = :status";
            paramsMap["status"] = p["status"] as byte;
        }
        if (p["companyId"]!=null)
        {
            sbf << " and m.zxId = :zxId";
            paramsMap["zxId"] = p["companyId"];
        }
        if (p["phone"]!=null)
        {
            sbf << " and (m.phone1 = :phone or m.phone2 = :phone or m.phone3 = :phone)";
            paramsMap["phone"] = p["phone"];
        }
        return this.queryObject(sbf.toString(),paramsMap);
    }

    PageUtil queryStudentSalsList(String zxId,String xiaoId,String createrId,String name,byte status,Date beginDate,Date endDate,String currentSchool,String currentClass,String phone,String address,String sourceId,int currentPage,int pageSize)
    {
        StringBuffer sbf = new StringBuffer();
        if (!(zxId in [null,""]))
        {
            sbf << " and s.zxid = '${zxId}'";
        }
        if (!(xiaoId in [null,""]))
        {
            sbf << " and s.xiaoid = '${xiaoId}'";
        }
        if (!(name in [null,""]))
        {
            sbf << " and s.name like '%${name}%'";
        }
        if (!(createrId in [null,""]))
        {
            sbf << " and s.createrid = '${createrId}'";
        }
        if (!(sourceId in [null,""]))
        {
            sbf << " and s.sourceid = '${sourceId}'";
        }
        if (status > -1)
        {
            sbf << " and s.status = ${status}";
        }
        if (beginDate!=null && endDate!=null)
        {
            sbf << " and s.createdate between '${DateUtil.format(beginDate,"yyyy-MM-dd")}' and '${DateUtil.format(endDate,"yyyy-MM-dd")}'";
        }
        if (!(currentSchool in [null,""]))
        {
            sbf << " and s.currentschool like '%${currentSchool}%'";
        }
        if (!(currentClass in [null,""]))
        {
            sbf << " and s.currentclass like '%${currentClass}%'";
        }
        if (!(phone in [null,""]))
        {
            sbf << " and (s.phone1 like '%${phone}%' or s.phone2 like '%${phone}%' or s.phone3 like '%${phone}%')";
        }
        if (!(address in [null,""]))
        {
            sbf << " and (s.areapath like '%${address}%' or s.address like '%${address}%')";
        }
        return this.createNativeQueryLimit(
                "select DISTINCT t.name as sname,s.id,s.name,s.engname,s.birthdaydate,s.sex,s.phone1,s.phone2,s.phone3,s.areapath,s.address,s.currentschool,s.currentclass,s.zxid,s.xiaoid,s.createrid,s.createdate,'' as tengname,t.name as mname,'' as mengname,s.age,s.status from studentsals as s left join usershop as t on s.createrid = t.user_id where 1=1 ${sbf.toString()} order by s.createdate desc",
                "select count(id) from studentsals as s where 1=1 ${sbf.toString()}",
                currentPage,pageSize
        );
    }

    List queryStudentSalsDetailList(String studentSalsId)
    {
        return this.createNativeQuery4Params("select DISTINCT sd.intention,sd.description,(select name from usershop where user_id = sd.humanid and shop_id = ss.xiaoid) as tName,'' as tEngName,sd.modifydate,'' as tHeadImg,sd.id,sd.nextdate,sd.nextdescription,(select name from usershop where user_id = sd.nexthumanid and shop_id = ss.xiaoid) as tNextName from studentsalsdetail as sd left join studentsals as ss on ss.id = sd.studentsals_id where sd.studentsals_id = :studentSalsId order by modifydate",["studentSalsId":studentSalsId]).getResultList();
    }

    void delTheStudentSalsDetail(String id)
    {
        this.executeEQL("delete StudentSalsDetail where id = :id",["id":id]);
    }
}
