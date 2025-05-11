package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductSpecification4Prodcut implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=30)
	String name;

	@Column(length=2)
	int orderNum;

	@OneToMany(mappedBy="productSpecification4Prodcut",fetch = FetchType.LAZY)
	List<ProductSpecificationItems4Prodcut> productSpecificationItems4ProdcutList;

	@ManyToOne(fetch=FetchType.EAGER)
	Product product;

	void cancelLazyEr()
	{
		this.productSpecificationItems4ProdcutList = null;
		this.product.cancelLazyEr();
	}
}
