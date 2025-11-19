package com.weavict.competition.entity

import jakarta.persistence.*

@Table
@Entity
class Buyer extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=20)
    String phone;

    @Column(length=50)
    String name;

    @Column(length=20)
    String loginName;

    @Column(length=30)
    String password;

    @Column(length=32)
    String wxid;

    @Column(length=32)
    String wxopenid;

    @Column(length=25)
    String area;

    @Column(length=100)
    String address;

    @Column(length=20)
    String tel;

    @Column(length=150)
    String headImgUrl;

    @Column(length=20)
    String wxNickName;

    @Column(length=20)
    String wxNickEm;

    @Column(length=30)
    Integer amb;

    @Column(length=1)
    byte sex;

    @Column(length=250)
    String description;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @OneToMany(mappedBy="buyer",fetch = FetchType.LAZY)
    List<Work> workList;

    void cancelLazyEr()
    {
        workList = null;
    }
}

@Table
@Entity
class BuyerAppInfo extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    BuyerAppInfoPK buyerAppInfoPK;

    @Column(length=20)
    String loginName;

    @Column(length=30)
    String password;

    @Column(length=32)
    String wxid;

    @Column(length=32)
    String wxopenid;

    @Column(length=150)
    String headImgUrl;

    @Column(length=20)
    String wxNickName;

    @Column(length=20)
    String wxNickEm;

    @Column(length=30)
    Integer amb;

    @Column(length=8)
    int money;

    @Column(length=500)
    String description;

    @Column(length=80)
    String workCompany;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class BuyerAppInfoPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(name="appid",length=30,nullable=false, insertable=false, updatable=false)
    String appId;

    @Column(name="buyerid",length=20,nullable=false, insertable=false, updatable=false)
    String buyerId;

    BuyerAppInfoPK()
    {

    }

    BuyerAppInfoPK(String appId,String buyerId)
    {
        this.appId = appId;
        this.buyerId = buyerId;
    }
}

@Embeddable
class ManagerPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(name="appid",length=30,nullable=false, insertable=false, updatable=false)
    String appId;

    @Column(name="managerid",length=20,nullable=false, insertable=false, updatable=false)
    String managerId;

    ManagerPK()
    {

    }

    ManagerPK(String appId,String managerId)
    {
        this.appId = appId;
        this.managerId = managerId;
    }
}

@Table
@Entity
class Manager extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    ManagerPK managerPK;

    @Column(length=20)
    String phone;

    @Column(length=50)
    String name;

    @Column(length=30)
    String password;

    @Column(length=150)
    String headImgUrl;

    @Column(length=1)
    byte sex;

    @Column(length=250)
    String description;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {

    }
}