package com.weavict.light.module

import com.weavict.light.entity.Device
import org.springframework.stereotype.Service

@Service("deviceService")
class DeviceService extends ModuleBean
{
    List<Device> qyBuyerDeviceList(String buyerId)
    {
        return this.newQueryUtils(false).masterTable("Device",null,null)
            .where("Device.buyer.phone = :buyerId",["buyerId":buyerId],null,null)
            .buildSql().run().content;
    }
}
