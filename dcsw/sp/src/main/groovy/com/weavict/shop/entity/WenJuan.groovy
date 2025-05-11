package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class Questionnaire implements Serializable,IEntity {
    static final long serialVersionUID = 1L;

    @Id
    @Column(length = 25)
    String id;

    @Column(length = 30)
    String privaterId;

    //0：活动；1：问卷
    @Column(length=2)
    byte quesType;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(length=150)
    String description;

    @Column(length=100)
    String title;

    @OneToMany(mappedBy="question",fetch= FetchType.LAZY)
    List<QuestionnaireItems> questionItems = new ArrayList();

    void cancelLazyEr()
    {
        this.questionItems = null;
    }
}

@Table
@Entity
class QuestionnaireItems extends BEntity implements Serializable,IEntity {
    static final long serialVersionUID = 1L;

    @Id
    @Column(length = 25)
    String id;

    @Column(length = 150)
    String title;

    @Column(length = 4000)
    String content;

    //0:input 1:check 2:radio 3:问答 4:判断 5:big text 20:通过验证码获取手机信息 99:纯观看 98:开始页 97:结束页
    @Column(length=2)
    byte theType;

    @Column(length=2)
    int sortNum;

    @Column(length=2)
    byte mediaType;

    @ManyToOne(fetch=FetchType.EAGER)
    Questionnaire question;

    @OneToMany(mappedBy="questionItem",fetch=FetchType.LAZY)
    List<QuestionnaireItemDetail> questionnaireItemDetailList;

    void cancelLazyEr()
    {
        this.question.cancelLazyEr();
        this.questionnaireItemDetailList = null;
    }
}

@Table
@Entity
class QuestionnaireItemDetail extends BEntity implements Serializable,IEntity {
    static final long serialVersionUID = 1L;

    @Id
    @Column(length = 25)
    String id;

    @Column(length=2)
    int sortNum;

    @Column(length=150)
    String imgUrl;

    @Column(length=2)
    byte mediaType;

    @Column(length=4000)
    String content;

    @Column(length=1000)
    String optionContent;

    @ManyToOne(fetch=FetchType.EAGER)
    QuestionnaireItems questionItem;

    void cancelLazyEr()
    {
        this.questionItem.cancelLazyEr();
    }
}

@Embeddable
class QuestionnaireReportPK implements Serializable {
    static final long serialVersionUID = 1L;

    @Column(length=30, nullable=false, insertable=false, updatable=false)
    String customerId;

    @Column(length=25, nullable=false, insertable=false, updatable=false)
    String questionaireId;

    QuestionnaireReportPK()
    {

    }

    QuestionnaireReportPK(String customerId,String questionaireId)
    {
        this.customerId = customerId;
        this.questionaireId = questionaireId;
    }
}

@Entity
class QuestionnaireReport implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    QuestionnaireReportPK questionnaireReportPK;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(length=3000)
    String answerOption;

    @Column(length=150)
    String description;

    void cancelLazyEr()
    {

    }
}