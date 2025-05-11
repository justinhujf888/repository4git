package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductAttribute implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;

	@Column(length=20)
	String name;

	@OneToMany(mappedBy="productAttribute",fetch = FetchType.LAZY)
	List<ProductAttributeItems> productAttributeItemsList;

	@ManyToOne(fetch=FetchType.EAGER)
	ProductsPrivater productsPrivater;

	void cancelLazyEr()
	{
		this.productAttributeItemsList = null;
	}
}
