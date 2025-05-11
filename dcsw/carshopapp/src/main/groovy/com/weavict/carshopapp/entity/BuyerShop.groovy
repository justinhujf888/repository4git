package com.weavict.carshopapp.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table
@Entity
class BuyerShop implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    BuyerShopPK buyerShopPK;

    @Column(length=30)
    String name;

    @Column(length=20)
    String contectPhone;

    @Column(length=12)
    String contectName;

    @Column(length=250)
    String remark;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class BuyerShopPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(name="shop_id",length=30,nullable=false, insertable=false, updatable=false)
    String shopId;

    @Column(name="buyer_id",length=20,nullable=false, insertable=false, updatable=false)
    String buyerId;

    //    0：个人用户；1：公司用户
    @Column(name="type",length=2,nullable=false, insertable=false, updatable=false)
    byte type;

    BuyerShopPK() {}

    BuyerShopPK(String buyerId,String shopId,byte type)
    {
        this.shopId = shopId;
        this.buyerId = buyerId;
        this.type = type;
    }
}
