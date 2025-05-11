package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductAttribute4Product implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;

	@Column(length=20)
	String name;

	@OneToMany(mappedBy="productAttribute4Product",fetch = FetchType.LAZY)
	List<ProductAttributeItems4Product> productAttributeItems4ProductList;

	@ManyToOne(fetch=FetchType.LAZY)
	Product product;

	void cancelLazyEr()
	{
		this.productAttributeItems4ProductList = null;
	}
}
