package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductAttributeItems4Product implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;

	@Column(length=20)
	String name;

	@ManyToOne(fetch=FetchType.EAGER)
	ProductAttribute4Product productAttribute4Product;

	void cancelLazyEr()
	{

	}
}
