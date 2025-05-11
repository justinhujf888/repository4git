package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table
@Entity
class EngEveryDay extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length = 30)
    String id;

    @Column(length = 5000)
    String content;

    @Column(length = 5000)
    String note;

    @Column(length = 100)
    String source;

    @Column(length = 3000)
    String images;

    @Column(length = 300)
    String ttsPath;

    @Column(length = 2)
    byte type;

    @Column(length = 30)
    String kind;

    void cancelLazyEr()
    {

    }
}

@Table
@Entity
class Dict extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length = 100)
    String id;

    @Column(length = 100)
    String ds;

    @Column(length = 5000)
    String content;

    @Column(length = 5000)
    String note;

    @Column(length = 100)
    String source;

    @Column(length = 3000)
    String images;

    @Column(length = 300)
    String ttsPath;

    @Column(length = 3)
    int wlong;

    @Column(length = 2)
    byte type;

    @Column(length = 30)
    String kind;

    void cancelLazyEr()
    {

    }
}
