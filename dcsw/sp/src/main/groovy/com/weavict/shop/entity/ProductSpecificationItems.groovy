package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductSpecificationItems implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=50)
	String name;

	@ManyToOne(fetch=FetchType.EAGER)
	ProductSpecification productSpecification;

	void cancelLazyEr()
	{

	}
}
