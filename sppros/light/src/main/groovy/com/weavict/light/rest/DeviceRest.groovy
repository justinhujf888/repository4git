package com.weavict.light.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.light.module.DeviceService
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

@Path("/device")
class DeviceRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    DeviceService deviceService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/qyBuyerDeviceList")
    String qyBuyerDeviceList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "deviceList":({
                        return deviceService.qyBuyerDeviceList("13268990066");
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
