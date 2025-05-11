package com.weavict.shop.entity

import jakarta.persistence.*

@Table(name="orgration_buyer")
@Entity
class BuyerOrgration extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	BuyerOrgrationPK buyerOrgrationPK;

	@Column(length=30)
	String niName;

	@Column(length=200)
	String headImg;

	@Column(length=25)
	String area;

	@Column(length=100)
	String address;

	@Column(length=25)
	String latitude;

	@Column(length=25)
	String longitude;

//	0:申请;1:通过; 2:禁言;3:被移除
	@Column(length=2)
	byte status;

	boolean isManager;

	boolean isCreater;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=500)
	String description;

	@OneToMany(mappedBy="buyerOrgration",fetch = FetchType.LAZY)
	List<Twiter> twiterList;

	void cancelLazyEr()
	{
		this.twiterList = null;
	}
}
