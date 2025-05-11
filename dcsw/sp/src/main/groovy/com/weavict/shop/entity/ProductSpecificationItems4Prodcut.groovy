package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductSpecificationItems4Prodcut implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=30)
	String name;

	@Column(length=8)
	Integer price;

	@Column(length=8)
	int tuanPrice;

	@Column(length=8)
	int store;

	@Column(length=2)
	int orderNum;

	@ManyToOne(fetch=FetchType.EAGER)
	ProductSpecification4Prodcut productSpecification4Prodcut;

	void cancelLazyEr()
	{
		this.productSpecification4Prodcut.cancelLazyEr();
	}
}
