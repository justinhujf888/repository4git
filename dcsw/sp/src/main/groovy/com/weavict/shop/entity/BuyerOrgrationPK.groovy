package com.weavict.shop.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class BuyerOrgrationPK implements Serializable
{
	static final long serialVersionUID = 1L;

	@Column(name="orgration_id",length=30,nullable=false, insertable=false, updatable=false)
	String orgrationId;

	@Column(name="buyer_id",length=20,nullable=false, insertable=false, updatable=false)
	String buyerId;

	BuyerOrgrationPK()
	{

	}

	BuyerOrgrationPK(String orgrationId,String buyerId)
	{
		this.orgrationId = orgrationId;
		this.buyerId = buyerId;
	}
}
