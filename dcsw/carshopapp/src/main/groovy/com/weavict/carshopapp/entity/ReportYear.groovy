package com.weavict.carshopapp.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Embeddable
class ReportYearPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(name="shop_id",length=30,nullable=false, insertable=false, updatable=false)
    String shopId;

    @Column(name="year",length=4,nullable=false, insertable=false, updatable=false)
    String year;

    @Column(name="month",length=2,nullable=false, insertable=false, updatable=false)
    String month;

    ReportYearPK()
    {

    }

    ReportYearPK(String shopId,String year,String month)
    {
        this.shopId = shopId;
        this.year = year;
        this.month = month;
    }
}

@Table(name = "reportyear")
@Entity
class ReportYear extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    ReportYearPK reportYearPK;

    @Column(length=13)
    int orderTotalCbAmount;

    @Column(length=13)
    int orderTotalAmount;

    @Column(length=3)
    int unOrderPayCount;

    @Override
    void cancelLazyEr()
    {

    }
}
