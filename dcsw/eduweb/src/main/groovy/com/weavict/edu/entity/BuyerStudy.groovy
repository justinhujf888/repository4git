package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class BuyerStudy extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    BuyerStudyPK buyerStudyPK;

    @Column(length=150)
    String dataUrl;

    @Column(length=4)
    int rightCount;

    @Column(length=4)
    int unfamiliarCount;

    @Column(length=4)
    int errorCount;

    @Column(length=4)
    int totalCount;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class BuyerStudyPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String bookId;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String bookUniId;

    @Column(nullable=false, insertable=false, updatable=false,length = 20)
    String buyerId;

//   单词【 0：单词听写练习；1：单词书写练习；2：单词卡片 】
    @Column(nullable=false, insertable=false, updatable=false,length = 2)
    byte type;

    BuyerStudyPK()
    {

    }

    BuyerStudyPK(String bookId,String bookUniId,String buyerId,byte type)
    {
        this.bookId = bookId;
        this.bookUniId = bookUniId;
        this.buyerId = buyerId;
        this.type = type;
    }
}

@Table
@Entity
class BuyerStudyPlan extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    BuyerStudyPlanPK buyerStudyPlanPK;

    @Temporal(TemporalType.TIMESTAMP)
    Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class BuyerStudyPlanPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String bookId;

    @Column(nullable=false, insertable=false, updatable=false,length = 20)
    String buyerId;

    BuyerStudyPlanPK()
    {

    }

    BuyerStudyPlanPK(String bookId,String buyerId)
    {
        this.bookId = bookId;
        this.buyerId = buyerId;
    }
}

@Table
@Entity
class BuyerBook extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    BuyerBookPK buyerBookPK;

    @Temporal(TemporalType.TIMESTAMP)
    Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date endDate;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class BuyerBookPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String bookId;

    @Column(nullable=false, insertable=false, updatable=false,length = 20)
    String buyerId;

    BuyerBookPK()
    {

    }

    BuyerBookPK(String bookId,String buyerId)
    {
        this.bookId = bookId;
        this.buyerId = buyerId;
    }
}