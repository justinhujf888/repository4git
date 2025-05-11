package com.weavict.carshopapp.rest


import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.carshopapp.entity.BuyerShop
import com.weavict.carshopapp.entity.BuyerShopPK
import com.weavict.carshopapp.entity.User
import com.weavict.carshopapp.entity.UserShop
import com.weavict.carshopapp.module.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

@Path("/user")
class UserRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    UserService userService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addUser2TheShop")
    String addUser2TheShop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            User user = this.objToBean(query.user,User.class,objectMapper);
            UserShop userShop = this.objToBean(query.userShop,UserShop.class,objectMapper);
            userService.addUser2Shop(user,userShop);
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
    @Path("/addBuyer2TheShop")
    String addBuyer2TheShop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            User user = this.objToBean(query.buyer,User.class,objectMapper);
            BuyerShop buyerShop = this.objToBean(query.buyerShop,BuyerShop.class,objectMapper);
            userService.addBuyer2Shop(user,buyerShop);
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
    @Path("/delBuyerShop")
    String delBuyerShop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            BuyerShopPK buyerShopPK = this.objToBean(query.buyerShopPK, BuyerShopPK.class,objectMapper);
            userService.delBuyerShop(buyerShopPK);
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
    @Path("/users8TheShop")
    String users8TheShop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "usersInfo":({
                         List<User> userList = new ArrayList();
                         userService.queryUsers8TheShop(query.shopId,query.userId,query.status as byte)?.each {u->
                             User user = new User();
                             user.phone = u[0];
                             user.name = u[1];
                             user.description = u[3];
                             user.imgPath = u[4];
                             user.temp = """{"zhiWei":"${u[2]}","usStatus":${u[5]},"usRuleStatus":${u[6]}}""";
                             userList << user;
                         }
                         return userList;
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
    @Path("/buyers8TheShop")
    String buyers8TheShop(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "buyersInfo":({
                         List<User> buyerList = new ArrayList();
                         userService.queryBuyers8TheShop(query.shopId)?.each {b->
                             User user = new User();
                             user.phone = b[0];
                             user.createDate = b[1];
                             user.description = b[2];
                             user.name = b[3];
                             user.wxid = b[4];
                             user.wxopenid = b[5];
                             user.temp = """{"remark":"${b[6]}","name":"${b[7]}","type":${b[8]},"buyerId":"${b[9]}","contectPhone":"${b[10]}","contectName":"${b[11]}"}""";
                             buyerList << user;
                         }
                         return buyerList;
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
    @Path("/cars8TheBuyer")
    String cars8TheBuyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "buyersInfo":({
                         return userService.queryCars8TheBuyer(query.buyerId)?.each {u->
                             u.cancelLazyEr();
                         }

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
    @Path("/queryTheUser")
    String queryTheUser(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "user":({
                         User user = userService.findObjectById(User.class,query.userId);
                         user?.cancelLazyEr();
                         return user;
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
    @Path("/saveTheUser")
    String saveTheUser(@RequestBody Map<String,Object> query)
    {
        try
        {
            User user = this.objToBean(query.user,User.class,null);
            if (user.createDate==null)
            {
                user.createDate = new Date();
            }
            userService.updateTheObject(user);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
