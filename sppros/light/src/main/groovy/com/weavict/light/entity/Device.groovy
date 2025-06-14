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

    @Column(length=20)
    String lat;

    @Column(length=20)
    String lng;

    @ManyToOne(fetch=FetchType.EAGER)
    Buyer buyer;

    @ManyToOne(fetch=FetchType.EAGER)
    DeviceType deviceType;

    @OneToMany(mappedBy="device",fetch = FetchType.LAZY)
    List<DeviceScript> deviceScriptList;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {
        this.buyer?.cancelLazyEr();
        this.deviceType?.cancelLazyEr();
        this.deviceScriptList = null;
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

    @ManyToOne(fetch=FetchType.EAGER)
    Device device;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {
        device = null;
    }
}
