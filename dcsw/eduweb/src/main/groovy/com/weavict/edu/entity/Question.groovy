package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

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

    @Column(length=30)
    String bookId;

    @Column(length=30)
    String bookUniId;

    @Column(length = 5)
    int fen;

    @OneToMany(mappedBy="questionnaireItems",fetch=FetchType.LAZY)
    List<QuestionnaireItemDetail> questionnaireItemDetailList;

    void cancelLazyEr()
    {
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

    @Column(length = 1500)
    String rightAnswer;

    @Column(length = 5)
    int fen;

    @ManyToOne(fetch=FetchType.EAGER)
    QuestionnaireItems questionnaireItems;

    void cancelLazyEr()
    {
        this.questionnaireItems.cancelLazyEr();
    }
}
