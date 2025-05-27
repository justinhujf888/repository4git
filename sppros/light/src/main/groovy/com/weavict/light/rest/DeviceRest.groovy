package com.weavict.light.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.light.entity.Device
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
    @Path("/qyDeviceTypeList")
    String qyDeviceTypeList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "deviceTypeList":({
                         return deviceService.qyDeviceTypeList(query.appId,query.typeId,query.serviceId,query.name)?.each {
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
    @Path("/qyBuyerDeviceList")
    String qyBuyerDeviceList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "deviceList":({
                        return deviceService.qyBuyerDeviceList(query.userId)?.each {
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
    @Path("/addBuyerDevice")
    String addBuyerDevice(@RequestBody Map<String,Object> query)
    {
        try
        {
            Device device = objToBean(query.device,Device.class,buildObjectMapper());
            device.createDate = new Date();
            deviceService.updateTheObject(device);
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
    @Path("/delBuyerDevice")
    String delBuyerDevice(@RequestBody Map<String,Object> query)
    {
        try
        {
            deviceService.deleteTheObject8Fields("Device","buyer.phone = :userId and deviceId = :deviceId",["userId":query.userId,"deviceId":query.deviceId],false);
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
    @Path("/renameBuyerDevice")
    String renameBuyerDevice(@RequestBody Map<String,Object> query)
    {
        try
        {
            deviceService.updateTheObjectFilds("Device","buyer.phone = :userId and deviceId = :deviceId",["name":query.name],["userId":query.userId,"deviceId":query.deviceId],false);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
