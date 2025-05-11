package com.weavict.edu.module

import com.weavict.edu.entity.PayReturnOrder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("payBean")
class PayService extends ModuleBean
{
    @Transactional
    void payedReturn(PayReturnOrder payReturnOrder)
    {
        this.executeEQL("update PayReturnOrder set paymentStatus = :paymentStatus,tradeNo = :tradeNo where id = :id",["id":payReturnOrder.id,"tradeNo":payReturnOrder.tradeNo,"paymentStatus": (1 as byte)]);
    }
}
