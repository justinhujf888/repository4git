package com.weavict.light.rest

import cn.hutool.core.date.DateUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.MathUtil
import com.weavict.light.entity.Device
import com.weavict.light.entity.DeviceScript
import com.weavict.light.module.DeviceService
import com.weavict.website.common.client.WebUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.TransactionDefinition
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
                        return deviceService.qyBuyerDeviceList(query.userId,query.appId)?.each {
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
    @Path("/qyDeviceScriptList")
    String qyDeviceScriptList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "deviceScriptList":({
                         return deviceService.qyDeviceScriptList(query.deviceId)?.each {
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
            if (deviceService.findObjectById(Device.class,device.deviceId)==null)
            {
                device.createDate = new Date();
                List<DeviceScript> scriptList = deviceService.qyDeviceScriptList("all");
                deviceService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                    deviceService.createNativeQuery4Params("insert into device (buyer_phone,createDate,deviceType_id,lat,lng,name,deviceId) values (:userid,:createdate,:typeid,:lat,:lng,:name,:deviceid)",["userid":device.buyer.phone,"name":device.name,"deviceid":device.deviceId,"typeid":device.deviceType.id,"lat":device.lat,"lng":device.lng,"createdate": new Date()]).executeUpdate();
                    for (DeviceScript script in scriptList)
                    {
                        deviceService.createNativeQuery4Params("insert into devicescript (id,name,script,areuse,device_deviceid,createdate) values (:id,:name,:script,:areuse,:deviceid,:createdate)",["id":MathUtil.getPNewId(),"name":script.name,"script":script.script,"areuse":script.areUse,"deviceid":device.deviceId,"createdate": new Date()]).executeUpdate();
                    }
                });
            }

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
            deviceService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                deviceService.deleteTheObject8Fields("DeviceScript","device.deviceId = :deviceId",["deviceId":query.deviceId],false);
                deviceService.deleteTheObject8Fields("Device","buyer.phone = :userId and deviceId = :deviceId",["userId":query.userId,"deviceId":query.deviceId],false);
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
    @Path("/renameBuyerDevice")
    String renameBuyerDevice(@RequestBody Map<String,Object> query)
    {
        try
        {
//            println query.userId;println query.deviceId;println query.name;
            deviceService.updateTheObjectFilds("Device","buyer.phone = :userId and deviceId = :deviceId",["name":query.name],["userId":query.userId,"deviceId":query.deviceId],false);
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
    @Path("/delTheDeviceScript")
    String delTheDeviceScript(@RequestBody Map<String,Object> query)
    {
        try
        {
            deviceService.deleteTheObject8Fields("DeviceScript","id = :id",["id":query.id],false);
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
    @Path("/updateTheDeviceScript")
    String updateTheDeviceScript(@RequestBody Map<String,Object> query)
    {
        try
        {
            DeviceScript deviceScript = objToBean(query.deviceScript,DeviceScript.class,buildObjectMapper());
            deviceService.updateTheObject(deviceScript);
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
    @Path("/renameDeviceScript")
    String renameDeviceScript(@RequestBody Map<String,Object> query)
    {
        try
        {
            if (!(query.name in [null,""]))
            {
                deviceService.updateTheObjectFilds("DeviceScript","id = :id",["name":query.name],["id":query.id],false);
            }
            if ((query.areUse as byte)>-1)
            {
                deviceService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                    deviceService.executeEQL("update DeviceScript set areUse = :areUse where device.deviceType.appId = :appId and device.deviceId = :deviceId",["areUse":0 as byte,"appId":query.appId,"deviceId":query.deviceId]);
                    deviceService.updateTheObjectFilds("DeviceScript","id = :id",["areUse":query.areUse as byte],["id":query.id],false);
                });
            }
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
    @Path("/reScriptDeviceScript")
    String reScriptDeviceScript(@RequestBody Map<String,Object> query)
    {
        try
        {
            deviceService.reScriptDeviceScript(query.appId,query.deviceId,query.scriptId,query.script);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
