package com.weavict.light.rest

import cn.hutool.core.date.DateUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.util.MathUtil
import com.weavict.light.entity.BuyerDeviceScript
import com.weavict.light.entity.BuyerDeviceScriptPK
import com.weavict.light.entity.Device
import com.weavict.light.entity.DeviceScript
import com.weavict.light.module.DeviceService
import com.weavict.website.common.client.WebUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
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
//        println query.userId;println query.deviceId;println query.deviceTypeId;
        try
        {
            ObjectMapper objectMapper = buildObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "deviceScriptList":({
                         return deviceService.qyDeviceScriptList(query.userId,query.deviceId,query.deviceTypeId)?.each {
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
            Device hdevice = deviceService.findObjectById(Device.class,device.deviceId);

            deviceService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                if (hdevice==null)
                {
                    device.createDate = new Date();
                    deviceService.createNativeQuery4Params("insert into device (buyer_phone,createDate,deviceType_id,lat,lng,name,deviceId,uniid,remark) values (:userid,:createdate,:typeid,:lat,:lng,:name,:deviceid,:uniId,:remark)",["userid":device.buyer.phone,"name":device.name,"deviceid":device.deviceId,"typeid":device.deviceType.id,"lat":device.lat,"lng":device.lng,"uniId":device.uniId,"remark":device.remark,"createdate": new Date()]).executeUpdate();
                }
                else if (hdevice.buyer.phone != device.buyer.phone)
                {
                    deviceService.createNativeQuery4Params("update device set buyer_phone = :phone where deviceid = :deviceId",["phone":device.buyer.phone,"deviceId":device.deviceId]).executeUpdate();
                }

                int size = deviceService.createNativeQuery4Params("select count(id) from devicescript where buyer_phone = :userId and devicetypeid = :deviceTypeId",["userId":device.buyer.phone,"deviceTypeId":device.deviceType.id]).getSingleResult();

                if (size < 1)
                {
                    List<DeviceScript> scriptList = deviceService.qyDeviceScriptList("all",null,device.deviceType.id);
                    for (DeviceScript script in scriptList)
                    {
                        deviceService.createNativeQuery4Params("insert into devicescript (id,name,script,createdate,buyer_phone,devicetypeid) values (:id,:name,:script,:createdate,:userId,:deviceTypeId)",["id":MathUtil.getPNewId(),"name":script.name,"script":script.script,"createdate": new Date(),"userId":device.buyer.phone,"deviceTypeId":device.deviceType.id]).executeUpdate();
                    }
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
    @Path("/delBuyerDevice")
    String delBuyerDevice(@RequestBody Map<String,Object> query)
    {
        try
        {
            deviceService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                deviceService.deleteTheObject8Fields("BuyerDeviceScript","buyerDeviceScriptPK.deviceId = :deviceId",["deviceId":query.deviceId],false);
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
            deviceService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                deviceService.deleteTheObject8Fields("BuyerDeviceScript","buyerDeviceScriptPK.scriptId = :scriptId",["scriptId":query.id],false);
                deviceService.deleteTheObject8Fields("DeviceScript","id = :id",["id":query.id],false);
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
                    deviceService.deleteTheObject8Fields("BuyerDeviceScript","buyerDeviceScriptPK.deviceId = :deviceId",["deviceId":query.deviceId],false);
                    BuyerDeviceScript buyerDeviceScript = new BuyerDeviceScript();
                    buyerDeviceScript.buyerDeviceScriptPK = new BuyerDeviceScriptPK(query.deviceId,query.id);
                    deviceService.updateObject(buyerDeviceScript);
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/tempProcess")
    String tempProcess(@RequestBody Map<String,Object> query)
    {
        try
        {
            List<DeviceScript> deviceScriptList = deviceService.newQueryUtils(true,true).masterTable("devicescript","ds",[
                    [sf:"id",bf:"id"],
                    [sf:"name",bf:"name"],
                    [sf:"areuse",bf:"tempMap.areUse"],
                    [sf:"device_deviceid",bf:"tempMap.deviceId"]
            ]).where("device_deviceid <> :userId",["userId":"all"],null,null)
            .beanSetup(DeviceScript.class,null,null)
            .buildSql().run().content;
            deviceService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                for(DeviceScript script in deviceScriptList)
                {
                    println script.dump();
                    Device device = deviceService.findObjectById(Device.class,script.tempMap.deviceId);
                    deviceService.createNativeQuery4Params("update devicescript set buyer_phone = :userId,devicetypeid = :deviceTypeId where device_deviceid = :deviceId",["userId":device.buyer.phone,"deviceId":script.tempMap.deviceId,"deviceTypeId":"0"]).executeUpdate();
                    if (script.tempMap.areUse==(1 as byte))
                    {
                        BuyerDeviceScript buyerDeviceScript = new BuyerDeviceScript();
                        buyerDeviceScript.buyerDeviceScriptPK = new BuyerDeviceScriptPK(script.tempMap.deviceId,script.id);
                        deviceService.updateObject(buyerDeviceScript);
                    }
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
}
