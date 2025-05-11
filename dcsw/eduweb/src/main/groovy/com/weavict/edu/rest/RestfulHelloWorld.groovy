package com.weavict.edu.rest


import com.weavict.edu.module.SourcesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

/**
 * Created by Justin on 2018/6/10.
 */

@Path("/hw")
class RestfulHelloWorld
{
    @Autowired
    SourcesService sourcesService;

    @Context
    HttpServletRequest request;

    @GET
    //Produces表明发送出去的数据类型为text/plain
    // 与Produces对应的是@Consumes，表示接受的数据类型为text/plain
    @Produces("text/plain")
    String getMessage() {
        return "Hello world!";
    }

    @GET
    @Produces("text/plain;charset=utf-8")
    @Path("/getText/{name}")
    String getString(@PathParam("name") String name)
    {
        return "您好, ${name}, Your session id is : ${request.getSession().getId()}";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
//     @Produces("text/plain;charset=utf-8") @Consumes("application/x-www-form-urlencoded")
    @Path("/studentsid")
    Map<String,Object> getStudents(@RequestBody Map<String,Object> query)
    {
        println query;
//        ObjectMapper objectMapper = new ObjectMapper();
        return ["datas":["id":"","xiaoid":"","name":"阿斯顿发空间按时"]];
//        return objectMapper.writeValueAsString(
//            [
//                "datas":(
//                    {
//                        def map = [];
//                        for(def s in sourcesService.queryStudents(null,-1,-1,null,null,-1 as byte,null,null,null,null))
//                        {
//                            map << ["id":s[0],"xiaoid":s[1],"name":s[2]];
//                        }
//                        return map;
//                    }
//                ).call()
//            ]
//        );
    }

    @GET
    @Path("/getFclist")
    String getFclist()
    {
        return "aaa不不不";
    }

//    @POST
//    @Path("add")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//// @FormParam表示通过HTML表单传入参数
//    Viewable addStudent2(@FormParam("name") String name,
//                                @FormParam("dept") String dept,
//                                @Context HttpServletRequest request,
//                                @Context HttpServletResponse response) {
//// 保存name和dept的逻辑（略）
//        request.setAttribute("resultString", "success");
//        request.setAttribute("studentList", studentList);
//        HttpSession session = request.getSession();
//        session.setAttribute("user", "guohengj");
//// 跳转到/strudent.jsp
//        return new Viewable("/student.jsp", null);
//    }


    @GET
    @Path("/admin")
    void index (@Context HttpServletResponse response){
        try {
            response.sendRedirect("/index.html");
        } catch (IOException e) {
        }
    }

}
