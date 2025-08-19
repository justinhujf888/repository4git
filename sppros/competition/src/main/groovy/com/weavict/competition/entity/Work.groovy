package com.weavict.competition.entity

import jakarta.persistence.*


@Table
@Entity
class SiteCompetition extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=50)
    String name;

    @Column(length=350)
    String description;

    @Column(length=30)
    String appId;

    @OneToMany(mappedBy="siteCompetition",fetch = FetchType.LAZY)
    List<MasterCompetition> masterCompetitionList;

    void cancelLazyEr()
    {

    }
}

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

    @Column(length=30)
    String appId;

    @Column(length=150)
    String fields;

    @Temporal(TemporalType.TIMESTAMP)
    Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date pingShenDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @ManyToOne(fetch=FetchType.EAGER)
    SiteCompetition siteCompetition;

    @OneToMany(mappedBy="masterCompetition",fetch = FetchType.LAZY)
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

    @Column(length=150)
    String fields;

    @Column(length=350)
    String description;

    @Column(length=2)
    //	0:竞赛开始；1:初审；2：复审第一轮；3：复审第二轮；...9：竞赛结束；
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

    @Column(length=100)
    String guige;

    @Column(length=300)
    String gousiDescription;

    @Column(length=300)
    String myMeanDescription;

    @Column(length=2000)
    String otherFields;

    @ManyToOne(fetch=FetchType.EAGER)
    Buyer buyer;

    @Column(length=30)
    String appId;

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

@Table
@Entity
class Judge extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String name;

    @Column(length=30)
    String engName;

    @Column(length=30)
    String password;

    @Column(length=350)
    String description;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(length=150)
    String headImgUrl;

    @Column(length=30)
    String appId;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class CompetitionJudgePK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String competitionId;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String judgeId;

    @Column(nullable=false, insertable=false, updatable=false,length = 2)
    byte competitionStatus;

    CompetitionJudgePK() {}

    CompetitionJudgePK(String competitionId,String judgeId,byte competitionStatus)
    {
        this.competitionId = competitionId;
        this.judgeId = judgeId;
        this.competitionStatus = competitionStatus;
    }
}

@Table
@Entity
class CompetitionJudge extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    CompetitionJudgePK competitionJudgePK;

    void cancelLazyEr()
    {

    }
}
