package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table
@Entity
class VideoContent extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String bookId;

    @Column(length=30)
    String bookUniId;

    @Column(length=150)
    String imgUrl;

    @Column(length=150)
    String poster;

    @Column(length=50)
    String name;

    @Column(length=200)
    String description;

    @Column(length=4)
    int sortNum;

    @Column(length=20)
    String duration;

    void cancelLazyEr()
    {

    }
}
