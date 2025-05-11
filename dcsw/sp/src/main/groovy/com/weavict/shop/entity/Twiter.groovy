package com.weavict.shop.entity

import jakarta.persistence.*
/**
 * The persistent class for the product database table.
 * 
 */
@Entity
class Twiter implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=50)
	String title;

	@Column(length=50)
	String url;

	@Column(length=200)
	String description;

//	0:普通图文贴；1：公告；2：产品广告；3：活动报名；11：私有不公开；12：私有也公开
	byte twiterType;

	@Column(length=9)
	int twiterDetailCount;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date modifyDate;

	@OneToMany(mappedBy="twiter",fetch = FetchType.LAZY)
	List<TwiterImages> twiterImagesList;

	@OneToMany(mappedBy="twiter",fetch = FetchType.LAZY)
	List<TwiterDetails> twiterDetailsList;

	@OneToMany(mappedBy="twiter",fetch = FetchType.LAZY)
	List<TwiterZan> twiterZanList;

	@ManyToOne(fetch=FetchType.EAGER)
	BuyerOrgration buyerOrgration;

	void cancelLazyEr()
	{
		this.twiterImagesList = null;
		this.twiterDetailsList = null;
		this.twiterZanList = null;
		this.buyerOrgration.cancelLazyEr();
	}
}