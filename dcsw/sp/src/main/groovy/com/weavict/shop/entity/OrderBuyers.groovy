package com.weavict.shop.entity

import jakarta.persistence.*
/**
 * The persistent class for the product database table.
 * 大货订单和拼团订单的用户参与详情记录在这里
 */
@Entity
class OrderBuyers extends BEntity implements Serializable,IEntity
{
	private static final long serialVersionUID = 1L;

	//对应参与团购用户支付 PayReturnOrder.id = OrderBuyers.id
	@Id
	@Column(length=30)
	String id;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	int deliveryQuantity;

	@Column(length=8)
	int price;

	@Column(length=8)
	int paymentFee;

	@Column(length=2)
	byte paymentStatus;

	@Column(length=2)
	byte orderStatus;

	@Column(length=200)
	String ids;

	@Column(length=200)
	String specImg;

	//实际传入是privaterId;
	@Column(length=30)
	String orgrationId;

	@Column(length=30)
	String deliveryId;

	@Column(length=30)
	String deliveryName;

	@Column(length=30)
	String deliveryNo;

	@Column(length=10)
	int deliveryFee;

	//0：到店取货；1：快递取货
	@Column(length=2)
	byte deliveryType;

	@Column(length=100)
	String shipAddress;

	@Column(length=50)
	String shipAreaPath;

	@Column(length=20)
	String shipMobile;

	@Column(length=20)
	String shipName;

	@Column(length=20)
	String shipPhone;

	@Column(length=15)
	String shipZipCode;

	@ManyToOne(fetch=FetchType.EAGER)
	Product product;

	@ManyToOne(fetch=FetchType.EAGER)
	Order order;

	@ManyToOne(fetch=FetchType.EAGER)
	Buyer buyer;

	void cancelLazyEr()
	{
		this.product?.cancelLazyEr();
		this.order?.cancelLazyEr();
		this.buyer?.cancelLazyEr();
	}
}