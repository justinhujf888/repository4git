package com.weavict.shop.entity

import jakarta.persistence.*

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
