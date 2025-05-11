package com.weavict.edu.module

import com.weavict.edu.entity.Bills
import com.weavict.edu.entity.Parent
import com.weavict.common.util.MathUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("transactionBean")
class TransactionService extends ModuleBean
{
    List<Bills> genBillsList8Parent(String parentId,byte status)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select b from Bills as b where b.parent.id = :parentId";
        paramsMap["parentId"] = parentId;
        if (status!=(-1 as byte))
        {
            sbf << " and b.status = :status";
            paramsMap["status"] = status;
        }
        this.queryObject(sbf.toString(),paramsMap);
    }

    @Transactional
    void issueBillses(Bills bills,int unit)
    {
        if (this.findObjectById(Parent.class,bills.parent.id)==null)
        {
            Parent parent = new Parent();
            parent.id = bills.parent.id;
            parent.wxid = parent.id;
            parent.createDate = new Date();
            this.updateObject(parent);
        }
        for(int i in 1..unit)
        {
            Bills b = new Bills();
            b.id = MathUtil.getPNewId();
            b.name = bills.name;
            b.description = bills.description;
            b.condition = bills.condition;
            b.billsType = bills.billsType;
            b.status = bills.status;
            b.amout = bills.amout;
            b.createDate = new Date();
            b.sourceXiaoId = bills.sourceXiaoId;
            b.distXiaoId = bills.distXiaoId;
            b.endDate = bills.endDate;
            b.parent = bills.parent;
            b.creater = bills.creater;
            this.updateObject(b);
        }
    }

    @Transactional
    void editTheBillsStatus(Bills bills)
    {
        this.executeEQL("update Bills set status = :status,distXiaoId = :distXiaoId,usedDate = :usedDate,modifier = :modifier where id = :id",
                ["status": bills.status, "distXiaoId": bills.distXiaoId, "usedDate": bills.usedDate,"modifier":bills.modifier,"id":bills.id]);
    }
}
