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
class Assess extends BEntity implements Serializable,IEntity {
    static final long serialVersionUID = 1L;

    @Id
    @Column(length = 30)
    String id;

    @Column(length = 150)
    String title;

    @Column(length = 200)
    String content;

    //0:问卷 {id:20,name:"月反馈学生点评"},{id:21,name:"月反馈课程主题"}
    @Column(length=2)
    byte theType;

    @Column(length=200)
    String objId;

    @Column(length=30)
    String appId;

    @OneToMany(mappedBy="assess",fetch=FetchType.LAZY)
    List<AssessItems> assessItemsList;

    void cancelLazyEr()
    {
        this.assessItemsList = null;
    }
}

@Table
@Entity
class AssessItems extends BEntity implements Serializable,IEntity {
    static final long serialVersionUID = 1L;

    @Id
    @Column(length = 30)
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
    Assess assess;

    @OneToMany(mappedBy="assessItems",fetch=FetchType.LAZY)
    List<AssessItemDetail> assessItemDetailList;

    void cancelLazyEr()
    {
        this.assess?.cancelLazyEr();
        this.assessItemDetailList = null;
    }
}

@Table
@Entity
class AssessItemDetail extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length = 30)
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

    @Column(length = 1500)
    String rightAnswer;

    @ManyToOne(fetch=FetchType.EAGER)
    AssessItems assessItems;

    void cancelLazyEr()
    {
        this.assessItems?.cancelLazyEr();
    }
}

@Table
@Entity
class AssessAnswerReport extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length = 30)
    String id;

    @Column(length = 30)
    String assessId;

    @Column(length = 30)
    String assessItemId;

    @Column(length = 30)
    String assessItemDetailId;

    @Column(length = 200)
    String objId;

    @Column(length=150)
    String rightAnswer;

    @Column(length=4000)
    String optionContent;

    @Column(length=30)
    String optionId;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {

    }
}



