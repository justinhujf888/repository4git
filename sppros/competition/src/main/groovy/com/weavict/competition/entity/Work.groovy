package com.weavict.competition.entity

import jakarta.persistence.*


@Table
@Entity
class MasterCompetition extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=50)
    String name;

    @Column(length=350)
    String description;

    @Temporal(TemporalType.TIMESTAMP)
    Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(length=30)
    String appId;

    @OneToMany(mappedBy="competition",fetch = FetchType.LAZY)
    List<Competition> competitionList;

    void cancelLazyEr()
    {
        this.competitionList = null;
    }

}

@Table
@Entity
class Competition extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=50)
    String name;

    @Column(length=350)
    String description;

    @Column(length=1)
    //	0:竞赛开始；1:初审；2：复审第一轮；3：复审第二轮；9：竞赛结束；
    byte status;

    @ManyToOne(fetch=FetchType.EAGER)
    MasterCompetition masterCompetition;

    @OneToMany(mappedBy="competition",fetch = FetchType.LAZY)
    List<Work> workList;

    void cancelLazyEr()
    {
        this.masterCompetition.cancelLazyEr();
        this.workList = null;
    }
}


@Table
@Entity
class Work extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String name;

    @Column(length=20)
    String lat;

    @Column(length=20)
    String lng;

    @ManyToOne(fetch=FetchType.EAGER)
    Buyer buyer;

    @ManyToOne(fetch=FetchType.EAGER)
    Competition competition;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @OneToMany(mappedBy="work",fetch = FetchType.LAZY)
    List<WorkItem> workItemList;

    void cancelLazyEr()
    {
        this.buyer?.cancelLazyEr();
        this.competition?.cancelLazyEr();
        this.workItemList = null;
    }
}

@Table
@Entity
class WorkItem extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=200)
    String path;

    @Column(length=1)
    //	0:image；1:video；
    byte mediaType;

    @ManyToOne(fetch=FetchType.EAGER)
    Work work;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {
        this.work?.cancelLazyEr();
    }

}
