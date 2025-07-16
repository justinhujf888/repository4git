package com.weavict.competition.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.competition.entity.Work
import com.weavict.competition.module.WorkService
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

@Path("/work")
class WorkRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    WorkService workService;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/qyBuyerCompetitionWorkList")
    String qyBuyerCompetitionWorkList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "workList":({
                        return workService.qyBuyerCompetitionWorkInfo(query.userId,query.competitionId)?.each {
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

}
