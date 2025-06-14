package com.weavict.light.module

import com.weavict.light.entity.Device
import com.weavict.light.entity.DeviceType
import org.springframework.stereotype.Service

@Service("deviceService")
class DeviceService extends ModuleBean
{
    List<DeviceType> qyDeviceTypeList(String appId,String typeId,String serviceId,String name)
    {
        return this.newQueryUtils(false).masterTable("DeviceType",null,null)
                .where("1=1",null,null,null)
                .where("DeviceType.id = :typeId",["typeId":typeId],"and",{return !(typeId in [null,""])})
                .where("DeviceType.serviceId = :serviceId",["serviceId":serviceId],"and",{return !(serviceId in [null,""])})
                .where("DeviceType.name like :name",["name":"%${name}%"],"and",{return !(name in [null,""])})
                .where("DeviceType.appId = :appId",["appId":appId],"and",null)
                .buildSql().run().content;
    }

    List<Device> qyBuyerDeviceList(String buyerId,String appId)
    {
        return this.newQueryUtils(false).masterTable("Device",null,null)
            .where("Device.buyer.phone = :buyerId",["buyerId":buyerId],null,null)
            .where("Device.deviceType.appId = :appId",["appId":appId],"and",null)
            .buildSql().run().content;
    }

    List<Device> qyDeviceScriptList(String deviceId)
    {
        return this.newQueryUtils(false).masterTable("DeviceScript",null,null)
                .where("device.deviceId = :deviceId",["deviceId":deviceId],null,null)
                .buildSql().run().content;
    }
}
