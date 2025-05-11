package com.weavict.carshopapp.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table(name = "orders")
@Entity
class Order extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String buyerName;

    @Column(length=12)
    String carNumber;

    @Column(length=30)
    String companyId;

    @Column(length=30)
    String companyName;

    //0:未支付；1：已支付；
    @Column(length=2)
    byte status;

    //0:维修；1：保养；
    @Column(length=2)
    byte type;

    @Column(length=10)
    int orderTotalCbAmount;

    @Column(length=10)
    int orderTotalAmount;

    @Column(length=10)
    int orderPayAmount;

    @Column(length=20)
    String brand;

    @Column(length=20)
    String xingHao;

    @Column(length=8)
    int runedKl;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date inDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date outDate;

    @Column(length=200)
    String description;

    @Column(length=20)
    String wxPhone;

    @Column(length=12)
    String wxName;

    @Column(length=20)
    String wxZhiWei;

    @ManyToOne(fetch= FetchType.EAGER)
    Shop shop;

    @ManyToOne(fetch= FetchType.EAGER)
    User user;

    @OneToMany(mappedBy="order",fetch = FetchType.LAZY)
    List<OrderItem> orderItemList;

    @Override
    void cancelLazyEr()
    {
        this.orderItemList = null;
        this.shop.cancelLazyEr();
        this.user.cancelLazyEr();
    }
}

@Table
@Entity
class OrderItem extends BEntity implements Serializable,IEntity
{

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String name;

    @Column(length=30)
    String xingHao;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    int deliveryQuantity;

    @Column(length=8)
    int price;

    @Column(length=8)
    int cbPrice;

    @ManyToOne(fetch= FetchType.EAGER)
    Order order;

    @Override
    void cancelLazyEr()
    {
        this.order.cancelLazyEr();
    }
}