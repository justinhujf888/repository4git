package com.weavict.light.module

import com.weavict.light.entity.BuyerDeviceScript
import com.weavict.light.entity.BuyerDeviceScriptPK
import com.weavict.light.entity.Device
import com.weavict.light.entity.DeviceScript
import com.weavict.light.entity.DeviceType
import org.springframework.stereotype.Service
import org.springframework.transaction.TransactionDefinition

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

    List<DeviceScript> qyDeviceScriptList(String userId,String deviceId,String deviceTypeId)
    {
        List<DeviceScript> deviceScriptList = this.newQueryUtils(false).masterTable("DeviceScript",null,null)
                .where("buyer.phone = :phone and deviceTypeId = :deviceTypeId",["phone":userId,"deviceTypeId":deviceTypeId],null,null)
                .buildSql().run().content;
        if (deviceId!=null)
        {
            BuyerDeviceScript buyerDeviceScript = this.queryObject("select bds from BuyerDeviceScript as bds where bds.buyerDeviceScriptPK.deviceId = :deviceId",["deviceId":deviceId])?.get(0);
            for(DeviceScript script in deviceScriptList)
            {
                script.tempMap = [:];
                if (buyerDeviceScript!=null)
                {
                    if (script.id == buyerDeviceScript.buyerDeviceScriptPK.scriptId)
                    {
                        script.tempMap.areUse = 1 as byte;
                    }
                    else
                    {
                        script.tempMap.areUse = 0 as byte;
                    }
                }
                else
                {
                    script.tempMap.areUse = 0 as byte;
                }
            }
        }
        return deviceScriptList;
    }

    void reScriptDeviceScript(String appId,String deviceId,String scriptId,String scriptStr)
    {
        this.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
            this.updateTheObjectFilds("DeviceScript","id = :id",["script":scriptStr],["id":scriptId],false);
            this.deleteTheObject8Fields("BuyerDeviceScript","buyerDeviceScriptPK.deviceId = :deviceId",["deviceId":deviceId],false);
            BuyerDeviceScript buyerDeviceScript = new BuyerDeviceScript();
            buyerDeviceScript.buyerDeviceScriptPK = new BuyerDeviceScriptPK(deviceId,scriptId);
            this.updateObject(buyerDeviceScript);
        });
    }
}
