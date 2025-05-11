package com.weavict.edu.entity

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
class HuoDongClass extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=100)
    String name;

    @Column(length=30)
    String zxId;

    @Column(length=30)
    String xiaoId;

    @Column(length=150)
    String imgPath;

    @Column(length=5000)
    String content;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date modifyDate;

    @Column(length=100)
    String description;

    //0：进行；1：暂停；2：结束；
    @Column(length=2)
    byte status;

    void cancelLazyEr()
    {

    }
}


@Table
@Entity
class FootageType extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=50)
    String name;

    @Column(length=30)
    String appId;

    @Column(length=30)
    String xiaoId;

    @OneToMany(mappedBy="footageType",fetch = FetchType.LAZY)
    List<Footage> footageList;

    void cancelLazyEr()
    {

    }
}

@Table
@Entity
class Footage extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=50)
    String name;

    @Column(length=150)
    String sourcePath;

    @Column(length=150)
    String poster;

    @Column(length=150)
    String sourceInfo;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(length=30)
    String appId;

    @Column(length=30)
    String xiaoId;

    //0：图片；1：视频
    @Column(length=2)
    byte mediaType;

    @ManyToOne(fetch=FetchType.EAGER)
    FootageType footageType;

    void cancelLazyEr()
    {

    }
}