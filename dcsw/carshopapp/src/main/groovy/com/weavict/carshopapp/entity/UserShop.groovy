package com.weavict.carshopapp.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table
@Entity
class UserShop implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    UserShopPK userShopPK;

    //    0：在职；1：离职；2：注册管理员
    @Column(length=1)
    byte status;

    //    0：普通员工；1：管理层；2：老板级别
    @Column(length=1)
    byte ruleStatus;

    @Column(length=20)
    String zhiWei;

    @Column(length=20)
    String loginName;

    @Column(length=50)
    String password;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class UserShopPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(name="user_id",length=20,nullable=false, insertable=false, updatable=false)
    String userId;

    @Column(name="shop_id",length=30,nullable=false, insertable=false, updatable=false)
    String shopId;

    UserShopPK() {}

    UserShopPK(String userId,String shopId)
    {
        this.userId = userId;
        this.shopId = shopId;
    }
}
