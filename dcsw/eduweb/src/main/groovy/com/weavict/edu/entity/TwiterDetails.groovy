package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType;
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class TwiterDetails implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;

	@Column(length=200)
	String description;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date modifyDate;
	
	@ManyToOne(fetch=FetchType.EAGER)
	Twiter twiter;

	@ManyToOne(fetch=FetchType.EAGER)
	BuyerOrgration buyerOrgration;

	@ManyToOne(fetch=FetchType.EAGER)
	BuyerOrgration distBuyerOrgration;

	@OneToMany(mappedBy="twiterDetails",fetch = FetchType.LAZY)
	List<TwiterDetailsImages> twiterDetailsImagesList;

	void cancelLazyEr()
	{
		this.buyerOrgration.cancelLazyEr();
		this.distBuyerOrgration.cancelLazyEr();
		this.twiter.cancelLazyEr();
		this.twiterDetailsImagesList = null;
	}
}
