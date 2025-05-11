package com.weavict.edu.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.edu.entity.Bills
import com.weavict.edu.module.TransactionService
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

@Path("/tran")
class TransactionRest extends BaseRest
{
    @Autowired
    TransactionService transactionService;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/issueBillses")
    Map<String,Object> issueBillses(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Bills bills = objToBean(query.bills,Bills.class,objectMapper);
            transactionService.issueBillses(bills,query.unit as int);
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
    @Path("/queryBillses8Parent")
    String queryBillses8Parent(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(["status": "OK",
                "billsList": ({
                    return transactionService.genBillsList8Parent(query.parentId,query.billsStatus as byte)?.each {
                        it?.cancelLazyEr();
                    };
                }).call()
            ]);
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
    @Path("/editTheBillsStatus")
    String editTheBillsStatus(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Bills bills = objToBean(query.bills,Bills.class,objectMapper);
            bills.usedDate = new Date();
            transactionService.editTheBillsStatus(bills);
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
    @Path("/queryTheBills")
    String queryTheBills(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Bills bills = transactionService.findObjectById(Bills.class,query.billsId);
            bills.cancelLazyEr();
            return objectMapper.writeValueAsString(bills);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
