package com.weavict.edu.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

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

	void cancelLazyEr()
	{
		this.orgrationList = null;
		this.orderList = null;
		this.orderBuyersList = null;
	}
}

@Table
@Entity
class Rules extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=30)
	String ruleName;

	@Column(length=200)
	String rules;

	@ManyToOne(fetch=FetchType.EAGER)
	ProductsPrivater productsPrivater;

	void cancelLazyEr()
	{

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
class Orgration implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=50)
	String name;

	@Column(length=25)
	String area;

	@Column(length=100)
	String address;

	@Column(length=20)
	String tel;

	@Column(length=250)
	String description;

	@Column(length=250)
	String manageDesc;

	@Column(length=250)
	String buyDesc;

	@Column(length=100)
	String mImage;

	@Column(length=20)
	String lat;

	@Column(length=20)
	String lng;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@OneToMany(mappedBy="orgration",fetch = FetchType.LAZY)
	List<Order> orderList;

	@ManyToMany(cascade = CascadeType.PERSIST, mappedBy = "orgrationList", fetch = FetchType.LAZY)
	List<Buyer> buyerList;

	void addBuyer(Buyer buyer)
	{
		if (buyerList==null)
		{
			this.buyerList = new ArrayList<Buyer>();
		}
		this.buyerList.add(buyer);
	}

	void removeBuyer(Buyer buyer)
	{
		if (this.buyerList?.contains(buyer))
		{
			this.buyerList.remove(buyer);
		}
	}

	void cancelLazyEr()
	{
		this.buyerList = null;
		this.orderList = null;
	}
}

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