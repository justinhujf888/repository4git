package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class Buyer extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=20)
	String phone;
	
	@Column(length=50)
	String name;

	@Column(length=20)
	String loginName;

	@Column(length=50)
	String password;
	
	@Column(length=32)
	String wxid;
	
	@Column(length=32)
	String wxopenid;

	@Column(length=25)
	String area;
	
	@Column(length=100)
	String address;
	
	@Column(length=20)
	String tel;

	@Column(length=150)
	String headImgUrl;

	@Column(length=20)
	String wxNickName;

	@Column(length=20)
	String wxNickEm;

	@Column(length=30)
	Integer amb;

	@Column(length=1)
	byte sex;

	@Column(length=250)
	String description;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "orgration_buyer", inverseJoinColumns = @JoinColumn(name = "orgration_id"), joinColumns = @JoinColumn(name = "buyer_id"))
	List<Orgration> orgrationList;

	@OneToMany(mappedBy="buyer",fetch = FetchType.LAZY)
	List<Order> orderList;

	@OneToMany(mappedBy="buyer",fetch = FetchType.LAZY)
	List<OrderBuyers> orderBuyersList;

	@OneToMany(mappedBy="buyer",fetch = FetchType.LAZY)
	List<Pets> petsList;

	void cancelLazyEr()
	{
		this.orgrationList = null;
		this.orderList = null;
		this.orderBuyersList = null;
		this.petsList = null;
	}
}

@Table
@Entity
class BuyerAppInfo extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	BuyerAppInfoPK buyerAppInfoPK;

	@Column(length=20)
	String loginName;

	@Column(length=50)
	String password;

	@Column(length=32)
	String wxid;

	@Column(length=32)
	String wxopenid;

	@Column(length=150)
	String headImgUrl;

	@Column(length=20)
	String wxNickName;

	@Column(length=20)
	String wxNickEm;

	@Column(length=30)
	Integer amb;

	@Column(length=8)
	int money;

	@Column(length=500)
	String description;

	@Column(length=80)
	String workCompany;

	void cancelLazyEr()
	{

	}
}

@Embeddable
class BuyerAppInfoPK implements Serializable
{
	static final long serialVersionUID = 1L;

	@Column(name="appid",length=30,nullable=false, insertable=false, updatable=false)
	String appId;

	@Column(name="buyerid",length=20,nullable=false, insertable=false, updatable=false)
	String buyerId;

	BuyerAppInfoPK()
	{

	}

	BuyerAppInfoPK(String appId,String buyerId)
	{
		this.appId = appId;
		this.buyerId = buyerId;
	}
}

@Table
@Entity
class JifenGoLog implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=30)
	String productId;

	@Column(length=30)
	Integer productJifen;

	@Column(length=30)
	Integer jifenAfter;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=60)
	String userId;

//	0：学生积分换购；1：商城积分购物；10：学生获取积分；11：会员获取积分
	@Column(length=2)
	byte userType;

	void cancelLazyEr()
	{

	}
}
