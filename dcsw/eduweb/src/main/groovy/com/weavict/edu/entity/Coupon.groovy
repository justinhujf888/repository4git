package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Entity
class Coupon extends BEntity implements Serializable,IEntity
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String appId;

    @Column(length=50)
    String name;

    @Column(length=8)
    int price;

    @Column(length=8)
    int moneyValue;

    @Column(length=8)
    int salsMinValue;

    @Column(length=8)
    int youXiaoDays;

    @Column(length=5)
    int canGetCount;

    boolean shiLoopUse;

    boolean shiYouXiao;

    @Column(length=30)
    String categoryId;

    @Column(length=30)
    String productId;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(length=80)
    String remark;

    @ManyToOne(fetch= FetchType.EAGER)
    ProductsPrivater productsPrivater;

    void cancelLazyEr()
    {
        this.productsPrivater.cancelLazyEr();
    }
}

@Entity
class BuyerCoupon extends BEntity implements Serializable,IEntity
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String appId;

    @Column(length=30)
    String couponId;

    @Column(length=50)
    String name;

    @Column(length=20)
    String buyerId;

    @Column(length=30)
    String privaterId;

    @Column(length=8)
    int price;

    @Column(length=8)
    int moneyValue;

    @Column(length=8)
    int salsMinValue;

    @Column(length=8)
    int youXiaoDays;

    @Temporal(TemporalType.TIMESTAMP)
    Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date useDate;

    @Column(length=30)
    String categoryId;

    @Column(length=30)
    String productId;

    @Column(length=80)
    String remark;

    boolean shiUsed;

    boolean shiGuoQi;

    void cancelLazyEr()
    {

    }
}
