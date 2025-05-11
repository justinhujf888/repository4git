package com.weavict.carshopapp.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class Car implements Serializable,IEntity
{
    @Id
    @Column(length=30)
    String id;

    @Column(length=12)
    String carNumber;

    @Temporal(TemporalType.TIMESTAMP)
    Date buyCarDate;

    @Column(length=20)
    String brand;

    @Column(length=20)
    String xingHao;

    @Column(length=8)
    int runedKl;

    @Column(length=8)
    int preRunedKl;

    @Temporal(TemporalType.TIMESTAMP)
    Date preByDate;

    @ManyToOne(fetch= FetchType.EAGER)
    User user;

    @Override
    void cancelLazyEr()
    {
        this.user.cancelLazyEr();
    }
}
