package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductSpecification implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=50)
	String name;
	
	@OneToMany(mappedBy="productSpecification",fetch = FetchType.LAZY)
	List<ProductSpecificationItems> productSpecificationItemsList;

	@ManyToOne(fetch=FetchType.EAGER)
	ProductsPrivater productsPrivater;

	void cancelLazyEr()
	{
		this.productSpecificationItemsList = null;
	}
}
