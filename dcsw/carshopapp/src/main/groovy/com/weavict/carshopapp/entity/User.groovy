package com.weavict.carshopapp.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table(name = "users")
@Entity
class User extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=20)
    String phone;

    @Column(length=12)
    String name;

    @Column(length=32)
    String wxid;

    @Column(length=32)
    String wxopenid;

    @Column(length=250)
    String description;

    @Column(length=200)
    String imgPath;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @OneToMany(mappedBy="user",fetch = FetchType.LAZY)
    List<Order> orderList;

    @OneToMany(mappedBy="user",fetch = FetchType.LAZY)
    List<Car> carList;

    @Override
    void cancelLazyEr()
    {
        this.orderList = null;
        this.carList = null;
    }
}
