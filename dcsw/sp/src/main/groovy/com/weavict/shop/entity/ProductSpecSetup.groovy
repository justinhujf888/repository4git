package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductSpecSetup implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=8)
	Integer price;

	@Column(length=8)
	int tuanPrice;

	@Column(length=8)
	Integer cbPrice;

	@Column(length=8)
	int store;

	@Column(length=1000)
	String ids;

	boolean used;

	@Column(length=200)
	String specImg;

	@ManyToOne(fetch=FetchType.EAGER)
	Product product;

	void cancelLazyEr()
	{
		product.cancelLazyEr();
	}
}
