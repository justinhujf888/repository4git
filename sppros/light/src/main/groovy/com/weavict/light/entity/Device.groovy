package com.weavict.light.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.*

@Table
@Entity
class Device extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=20)
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

    void cancelLazyEr()
    {

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

    @Column(length=150)
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
