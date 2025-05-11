package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.NamedQuery
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Entity
@Table(name = "orders")
class Order extends BEntity implements Serializable,IEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String appId;

    @Column(length=30)
    String privaterId;

    @Column(length=30)
    String payReturnOrderId;

    @Column(length=30)
    String tuanInfoId;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(length=5000)
    String description;

    @Column(length=30)
    String dailiNo;

    //0:未支付；1：已支付；2：已经完成；3：已经取消；4：已发货；9：售后；11：预备退款；12：已退款
    @Column(length=2)
    byte orderStatus;

    //9:体验课;0:一般个人购买；10：一般个人购买，但不提前生成order;（只支持购买一个商品，，一般用于购买卡券，不能用于购物车）1：社区团购；2：拼团购;3：跟随拼团;4：到店消费
    @Column(length=2)
    byte orderType;

    @Column(length=10)
    int orderTotalAmount;

    @Column(length=10)
    int orderTotalBuyerNum;

    @Column(length=10)
    int orderTotalQutityNum;

    @Column(length=10)
    int paymentFee;

    //	0：未支付；1：已支付；2：已退款
    @Column(length=2)
    byte paymentStatus;

    @Column(length=100)
    String shipAddress;

    @Column(length=50)
    String shipAreaPath;

    @Column(length=20)
    String shipMobile;

    @Column(length=20)
    String shipName;

    @Column(length=20)
    String shipPhone;

    @Column(length=15)
    String shipZipCode;

    @Column(length=10)
    int deliveryFee;

    //0：到店取货；1：快递取货
    @Column(length=2)
    byte deliveryType;

    @Column(length=30)
    String deliveryId;

    @Column(length=30)
    String deliveryName;

    @Column(length=30)
    String deliveryNo;

    @ManyToOne(fetch=FetchType.EAGER)
    Orgration orgration;

    @ManyToOne(fetch=FetchType.EAGER)
    Buyer buyer;

    @OneToMany(mappedBy="order",fetch = FetchType.LAZY)
    List<OrderItem> orderItemList;

    @OneToMany(mappedBy="order",fetch = FetchType.LAZY)
    List<OrderBuyers> orderBuyersList;

    void cancelLazyEr()
    {
        this.buyer?.cancelLazyEr();
        this.orgration?.cancelLazyEr();
        this.orderItemList = null;
        this.orderBuyersList = null;
    }
}

@Entity
@NamedQuery(name="OrderItem.findAll", query="SELECT oi FROM OrderItem oi")
class OrderItem implements Serializable,IEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    int deliveryQuantity;

    @Column(length=8)
    int price;

    @Column(length=200)
    String ids;

    @Column(length=200)
    String specImg;

    @ManyToOne(fetch=FetchType.EAGER)
    Product product;

    @ManyToOne(fetch=FetchType.EAGER)
    Order order;

    void cancelLazyEr()
    {
        this.product?.cancelLazyEr();
        this.order?.cancelLazyEr();
    }
}


@Entity
class OrderBuyers implements Serializable,IEntity {
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

    @Column(length=2)
    byte paymentStatus;

    @Column(length=2)
    byte orderStatus;

    @Column(length=200)
    String ids;

    @Column(length=200)
    String specImg;

    @Column(length=30)
    String orgrationId;

    @Column(length=30)
    String deliveryId;

    @Column(length=30)
    String deliveryName;

    @Column(length=30)
    String deliveryNo;

    @ManyToOne(fetch=FetchType.EAGER)
    Product product;

    @ManyToOne(fetch=FetchType.EAGER)
    Order order;

    @ManyToOne(fetch=FetchType.EAGER)
    Buyer buyer;

    void cancelLazyEr()
    {
        this.product?.cancelLazyEr();
        this.order?.cancelLazyEr();
        this.buyer?.cancelLazyEr();
    }
}

@Entity
class TuanInfo implements Serializable,IEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String productId;

    boolean ended;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate

    @Temporal(TemporalType.TIMESTAMP)
    Date endDate;

    @Column(length=10)
    int orderTotalAmount;

    @Column(length=10)
    int orderTotalBuyerNum;

    @Column(length=10)
    int orderTotalOrgNum;

    @Column(length=10)
    int orderTotalQutityNum;

    void cancelLazyEr()
    {

    }
}