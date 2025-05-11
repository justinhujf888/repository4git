package com.weavict.shop.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity

@Entity
class Cart extends BEntity implements Serializable,IEntity
{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    CartPK cartPK;

    @Column(length=10)
    int qutityNum;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class CartPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(name="appid",length=30,nullable=false, insertable=false, updatable=false)
    String appId;

    @Column(name="product_id",length=30,nullable=false, insertable=false, updatable=false)
    String productId;

    @Column(name="buyer_id",length=20,nullable=false, insertable=false, updatable=false)
    String buyerId;

    @Column(name="specid",length=30,nullable=false, insertable=false, updatable=false)
    String specId;

    CartPK()
    {

    }

    CartPK(String appId,String productId,String buyerId,String specId)
    {
        this.appId = appId;
        this.productId = productId;
        this.buyerId = buyerId;
        this.specId = specId;
    }
}
