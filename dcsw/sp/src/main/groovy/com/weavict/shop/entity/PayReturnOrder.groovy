package com.weavict.shop.entity

import jakarta.persistence.*
/**
 * The persistent class for the product database table.
 * 
 */
@Entity
class PayReturnOrder extends BEntity implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=2)
	byte payType;

	@Column(length=30)
	String appId;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	int deliveryQuantity;

	@Column(length=8)
	int price;

	@Column(length=8)
	int paymentFee;

//	0：未支付；1：已支付；2：已退款
//	0:未支付；1：已支付；2：已经完成；3：已经取消；4：已发货；9：售后；11：预备退款；12：已退款
	@Column(length=2)
	byte paymentStatus;

	//9:体验课;0:一般个人购买；10：一般个人购买，但不提前生成order;1：社区团购；2：拼团购;3：跟随拼团;4：到店消费
	@Column(length=2)
	byte orderType;

	@Column(length=200)
	String ids;

	@Column(length=200)
	String specImg;

	@Column(length=30)
	String productId;

	@Column(length=30)
	String specId;

	@Column(length=30)
	String orgrationId;

	@Column(length=30)
	String buyerId;

	@Column(length=30)
	String dailiNo;

//	微信支付单号（微信系统传回）
	@Column(length=64)
	String tradeNo;

	@Column(length=30)
	String refundId;

//	微信退款单号（微信系统传回）
	@Column(length=64)
	String refundNo;

	@Column(length=1800)
	String payReturnDatas;

	//	用于拼团购已经发起拼团的orderId
	@Column(length=30)
	String orderId;

	@Column(length=1800)
	String description;

	void cancelLazyEr()
	{

	}
}