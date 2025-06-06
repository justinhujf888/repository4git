package com.weavict.competition.entity

import jakarta.persistence.*

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

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    void cancelLazyEr()
    {
        this.buyer?.cancelLazyEr();
    }

}
