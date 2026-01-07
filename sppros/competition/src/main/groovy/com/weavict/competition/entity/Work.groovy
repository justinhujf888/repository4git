package com.weavict.competition.entity


import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

//@SqlResultSetMappings(
//        [@SqlResultSetMapping(
//                name = "SiteCompetitionMapping",
//                entities = @EntityResult(
//                        entityClass = SiteCompetition.class,
//                        fields = [
//                                @FieldResult(name = "id",column = "id"),
//                                @FieldResult(name = "name",column = "name"),
//                                @FieldResult(name = "domain",column = "domain"),
//                                @FieldResult(name = "description",column = "description"),
//                                @FieldResult(name = "appId",column = "appid"),
//                                @FieldResult(name = "setupFields",column = "setupfields")
//                        ]
//                )
//        )]
//)

@Table
@Entity
//域名赛事，赛事定义
class SiteCompetition extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=50)
    String name;

    @Column(length=50)
    String domain;

    @Column(length=350)
    String description;

    @Column(length=30)
    String appId;

    @Column(length=1000,columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> setupFields;

    @OneToMany(mappedBy="siteCompetition",fetch = FetchType.LAZY)
    List<MasterCompetition> masterCompetitionList;

    void cancelLazyEr()
    {
        this.masterCompetitionList = null;
    }
}

@Table
@Entity
class SiteWorkItem extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=200)
    String path;

    @Column(length=1)
    //	0:site；1:年份赛事；8:页面图库;9:新闻图库
    byte sourceType;

    @Column(length=30)
    String sourceId;

    @Column(length=1)
    //	0:主题；1:历届作品；9:素材库
    byte type;

    @Column(length=1)
    //	0:image；1:video；
    byte mediaType;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(length=30)
    String appId;

    @Column(length=1000,columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> fileFields;

    void cancelLazyEr()
    {

    }
}

@Table
@Entity
class OrgHuman extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String name;

    @Column(length=30)
    String engName;

    //	0:site；1:年份赛事；
    byte sourceType;

    @Column(length=30)
    String sourceId;

    @Column(length=350)
    String description;

    @Column(length=50)
    String subDescription;

    @Column(length=50)
    String zhiWei;

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



@Table
@Entity
//年度赛事
class MasterCompetition extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=50)
    String name;

    @Column(length=1000,columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> description;

    @Column(length=30)
    String appId;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> setupFields;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> judgeSetup;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> workSetup;

    @Temporal(TemporalType.TIMESTAMP)
    Date cptDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date bmEndDate;

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
        this.siteCompetition?.cancelLazyEr();
    }
}

@Table
@Entity
//类别赛事
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

    @Column(length=2)
    //	0:竞赛开始；1:初审；2：复审第一轮；3：复审第二轮；...9：竞赛结束；
    byte status;

    @Column(length=30)
    String appId;

    @ManyToOne(fetch=FetchType.EAGER)
    MasterCompetition masterCompetition;

    @OneToMany(mappedBy="competition",fetch = FetchType.LAZY)
    List<GuiGe> guiGeList;

    void cancelLazyEr()
    {
        this.masterCompetition?.cancelLazyEr();
        this.guiGeList = null;
    }
}

@Table
@Entity
//分组
class GuiGe extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=50)
    String name;

    @Column(length=1000,columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> guigeFields;

    @Column(length=30)
    String appId;

    @ManyToOne(fetch=FetchType.EAGER)
    Competition competition;

    @OneToMany(mappedBy="guiGe",fetch = FetchType.LAZY)
    List<Work> workList;

    void cancelLazyEr()
    {
        this.competition?.cancelLazyEr();
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

    @Column(length=300)
    String gousiDescription;

    @Column(length=300)
    String myMeanDescription;

    @Column(length=1000,columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> hangyeFields;

    @Column(length=1000,columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> otherFields;

    @ManyToOne(fetch=FetchType.EAGER)
    Buyer buyer;

    @Column(length=30)
    String appId;

    @Column(length=1)
    byte status;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @ManyToOne(fetch=FetchType.EAGER)
    GuiGe guiGe;

//    如果规格是多层级，那么这个就代表具体的那个层级ID，而guiGe对象表示最上层的。
    @Column(length=30)
    String guiGeId;

    @OneToMany(mappedBy="work",fetch = FetchType.LAZY)
    List<WorkItem> workItemList;

    void cancelLazyEr()
    {
        this.buyer?.cancelLazyEr();
        this.guiGe?.cancelLazyEr();
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

    //	由用户定义
    byte type;

    @ManyToOne(fetch=FetchType.EAGER)
    Work work;

    @Column(length=200)
    String exifInfo;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(length=1000,columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> mediaFields;

    void cancelLazyEr()
    {
        this.work?.cancelLazyEr();
    }
}

@Table
@Entity
class WorkLog extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String workId;

    @Column(length=200)
    String log;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class MCPageSetupPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(nullable = false, insertable = false, updatable = false, length = 30)
    String competitionId;

    @Column(nullable = false, insertable = false, updatable = false, length = 30)
    String key;

    @Column(nullable = false, insertable = false, updatable = false, length = 30)
    String appId;

    MCPageSetupPK() {}

    MCPageSetupPK(String competitionId,String key,String appId)
    {
        this.competitionId = competitionId;
        this.key = key;
        this.appId = appId;
    }
}

@Table
@Entity
//类别赛事
class MCPageSetup extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    MCPageSetupPK mcPageSetupPK;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    LinkedHashMap<String,Object> setupJson;

    void cancelLazyEr()
    {

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

    @Column(length=20)
    String phone;

    @Column(length=30)
    String name;

    @Column(length=30)
    String engName;

    @Column(length=30)
    String password;

    @Column(length=350)
    String description;

    @Column(length=50)
    String subDescription;

    @Column(length=50)
    String zhiWei;

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
    String guiGeId;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String judgeId;

    @Column(nullable=false, insertable=false, updatable=false,length = 2)
    byte competitionStatus;

    CompetitionJudgePK() {}

    CompetitionJudgePK(String competitionId,String guiGeId,String judgeId,byte competitionStatus)
    {
        this.competitionId = competitionId;
        this.guiGeId = guiGeId;
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

    @Column(length=1000,columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String,Object> pingShenFields;

    void cancelLazyEr()
    {

    }
}
