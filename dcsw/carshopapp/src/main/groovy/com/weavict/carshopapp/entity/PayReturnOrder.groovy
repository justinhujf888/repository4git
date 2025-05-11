package com.weavict.carshopapp.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class PayReturnOrder implements Serializable,IEntity
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    int deliveryQuantity;

    @Column(length=8)
    int price;

    @Column(length=8)
    int paymentFee;

//	0：未支付；1：已支付；2：已退款
    @Column(length=2)
    byte paymentStatus;

    @Column(length=2)
    byte orderType;

    @Column(length=200)
    String ids;

    @Column(length=200)
    String specImg;

    @Column(length=30)
    String productId;

    @Column(length=30)
    String specId;

    @Column(length=30)
    String orgrationId;

    @Column(length=20)
    String buyerId;

    @Column(length=30)
    String dailiNo;

//	微信支付单号（微信系统传回）
    @Column(length=64)
    String tradeNo;

    @Column(length=30)
    String refundId;

//	微信退款单号（微信系统传回）
    @Column(length=64)
    String refundNo;

    @Column(length=1800)
    String payReturnDatas;

    void cancelLazyEr()
    {

    }
}
