package com.weavict.shop.entity

import jakarta.persistence.*
/**
 * The persistent class for the product database table.
 * 记录了某个产品一次开团的汇总，开始时间和结束时间
 */
@Entity
class TuanInfo extends BEntity implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=30)
	String productId;

	boolean ended;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate

	@Temporal(TemporalType.TIMESTAMP)
	Date endDate;

	@Column(length=10)
	int orderTotalAmount;

	@Column(length=10)
	int orderTotalBuyerNum;

	@Column(length=10)
	int orderTotalOrgNum;

	@Column(length=10)
	int orderTotalQutityNum;

	void cancelLazyEr()
	{

	}
}