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

@Table
@Entity
class Shop extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=50)
    String name;

    @Column(length=30)
    String zxId;

//    0：未支付；1：已支付；2：试用期
    @Column(length=1)
    byte status;

    @Column(length=25)
    String area;

    @Column(length=100)
    String address;

    @Column(length=20)
    String lat;

    @Column(length=20)
    String lng;

    @Column(length=20)
    String phone;

    @Column(length=20)
    String tel;

    @Column(length=200)
    String imgPath;

    @Column(length=250)
    String description;

    @Column(length=30)
    String dailiNo;

    @Temporal(TemporalType.TIMESTAMP)
    Date preDate;//上一次支付的时间或者注册的时间

    @Temporal(TemporalType.TIMESTAMP)
    Date nextDate;//到期需要支付的时间

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @OneToMany(mappedBy="shop",fetch = FetchType.LAZY)
    List<Order> orderList;

    @OneToMany(mappedBy="shop",fetch = FetchType.LAZY)
    List<ShopImages> shopImagesList;

    @Override
    void cancelLazyEr()
    {
        this.orderList = null;
        this.shopImagesList = null;
    }
}

@Table
@Entity
class ShopImages extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=200)
    String path;

    @Column(length=200)
    String thumbPath;

    @Column(length=2)
    int listNum;

//	0:image；1:video
    byte mediaType;

    @ManyToOne(fetch= FetchType.EAGER)
    Shop shop;

    @Override
    void cancelLazyEr()
    {
        this.shop.cancelLazyEr();
    }
}
