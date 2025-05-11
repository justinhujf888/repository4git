package com.weavict.edu.module

import com.weavict.edu.entity.DemoClass
import com.weavict.edu.entity.DemoClassMaster
import com.weavict.edu.entity.DemoClassPersonKes
import com.weavict.edu.entity.DemoClassSchool
import com.weavict.edu.entity.PayReturnOrder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("demoClassBean")
class DemoClassService extends ModuleBean
{
    List<DemoClassSchool> queryDemoClassSchools()
    {
        return this.queryObject("select s from DemoClassSchool as s where s.schoolStatus = 1");
    }

    List<DemoClass> queryDemoClass8TheSchool(String schoolId)
    {
        return this.queryObject("select dc from DemoClass as dc where status = 1 and dc.demoClassSchool.id = :schoolId",["schoolId":schoolId]);
    }

    DemoClassMaster queryTheRunningDemoClassMaster()
    {
        return this.queryObject("select dm from DemoClassMaster as dm where dm.status = 1")?.get(0);
    }

    List queryRunningDemoClasses8Buyer(String buyerId)
    {
        DemoClassMaster demoClassMaster = this.queryTheRunningDemoClassMaster();
        demoClassMaster.cancelLazyEr();
        return this.createNativeQuery("select kes.parentid,parent.name as parentname,parent.wxnickname,dcs.id as dcsid,dcs.name as dcsname,kes.democlassid as dcid,dc.name as dcname,kes.createdate,pay.tradeno,pay.dailino as dailino,pay.paymentstatus,pay.price,dcs.area,dcs.address,pay.id from democlasspersonkes as kes left join payreturnorder as pay on pay.productid = kes.democlassmasterid and pay.buyerid = kes.parentid left join democlass as dc on dc.id = kes.democlassid left join democlassschool as dcs on dcs.id = dc.democlassschool_id left join parent as parent on kes.parentid = parent.id where pay.paymentstatus = 1 and kes.parentid = '${buyerId}' and kes.democlassmasterid = '${demoClassMaster.id}'").getResultList();
    }

    @Transactional
    void addDemoClasses4TheParent(String buyerId,String dailiNo,int fee,keList,String orderNo,DemoClassMaster demoClassMaster)
    {
        this.executeEQL("delete PayReturnOrder where buyerId = :buyerId and productId = :productId and paymentStatus = :paymentStatus",["buyerId":buyerId,"productId":demoClassMaster.id,"paymentStatus":0 as byte]);
        PayReturnOrder payReturnOrder = new PayReturnOrder();
        payReturnOrder.id = orderNo;
        payReturnOrder.buyerId = buyerId;
        payReturnOrder.dailiNo = dailiNo;
        payReturnOrder.createDate = new Date();
        payReturnOrder.deliveryQuantity = 1;
        payReturnOrder.orderType = 9 as byte;
        payReturnOrder.ids = "";
        payReturnOrder.orgrationId = "";
        payReturnOrder.paymentFee = fee;
        payReturnOrder.paymentStatus = 0 as byte;
        payReturnOrder.price = fee;
        payReturnOrder.productId = demoClassMaster.id;
        payReturnOrder.specId = "";
        payReturnOrder.specImg = "";
        payReturnOrder.tradeNo = "";
        this.updateObject(payReturnOrder);
        this.executeEQL("delete DemoClassPersonKes where demoClassPersonKesPK.parentId = :buyerId and demoClassPersonKesPK.demoClassMasterId = :demoClassMasterId",["buyerId":buyerId,"demoClassMasterId":demoClassMaster.id]);
        keList?.each {ke->
            DemoClassPersonKes demoClassPersonKes = new DemoClassPersonKes(buyerId,ke,demoClassMaster.id);
            demoClassPersonKes.createDate = payReturnOrder.createDate;
            demoClassPersonKes.dailiNo = dailiNo;
            this.updateObject(demoClassPersonKes);
        }
    }
}
