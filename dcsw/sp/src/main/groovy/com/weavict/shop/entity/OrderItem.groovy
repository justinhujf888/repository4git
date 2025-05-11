package com.weavict.shop.entity

import jakarta.persistence.*
/**
 * The persistent class for the product database table.
 * 
 */
@Entity
@NamedQuery(name="OrderItem.findAll", query="SELECT oi FROM OrderItem oi")
class OrderItem implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	int deliveryQuantity;

	@Column(length=8)
	int price;

	@Column(length=50)
	String productName;

	@Column(length=200)
	String ids;

	@Column(length=200)
	String specImg;

	@ManyToOne(fetch=FetchType.EAGER)
	Product product;

	@ManyToOne(fetch=FetchType.EAGER)
	Order order;

	void cancelLazyEr()
	{
		this.product?.cancelLazyEr();
		this.order.cancelLazyEr();
	}
}