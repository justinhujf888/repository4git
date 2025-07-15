package com.weavict.light.entity

import jakarta.persistence.*

@Table
@Entity
class Device extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=80)
    String deviceId;

    @Column(length=30)
    String name;

    @Column(length=30)
    String uniId;

    @Column(length=20)
    String lat;

    @Column(length=20)
    String lng;

    @Column(length=200)
    String remark;

    @ManyToOne(fetch=FetchType.EAGER)
    Buyer buyer;

    @ManyToOne(fetch=FetchType.EAGER)
    DeviceType deviceType;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {
        this.buyer?.cancelLazyEr();
        this.deviceType?.cancelLazyEr();
    }

}

@Table
@Entity
class DeviceType extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=20)
    String id;

    @Column(length=30)
    String name;

    @Column(length=30)
    String appId;

    @Column(length=350)
    String serviceId;

    @Column(length=350)
    String characteristicsReadIds;

    @Column(length=350)
    String characteristicsWriteIds;

    @OneToMany(mappedBy="deviceType",fetch = FetchType.LAZY)
    List<Device> deviceList;

    void cancelLazyEr()
    {
        this.deviceList = null;
    }

}

@Table
@Entity
class DeviceScript extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String name;

    @Column(length=500)
    String script;

    @Column(length=20)
    String deviceTypeId;

    @ManyToOne(fetch=FetchType.EAGER)
    Buyer buyer;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {
        buyer.cancelLazyEr();
    }
}

@Table
@Entity
class BuyerDeviceScript extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    BuyerDeviceScriptPK buyerDeviceScriptPK;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class BuyerDeviceScriptPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(nullable=false, insertable=false, updatable=false,length = 80)
    String deviceId;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String scriptId;

    BuyerDeviceScriptPK() {}

    BuyerDeviceScriptPK(String deviceId,String scriptId)
    {
        this.deviceId = deviceId;
        this.scriptId = scriptId;
    }
}
