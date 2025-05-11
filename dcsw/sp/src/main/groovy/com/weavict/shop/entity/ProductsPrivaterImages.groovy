package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductsPrivaterImages implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=200)
	String path;
	
	boolean isYingYeImg;
	
	boolean isZiZhiImg;
	
	boolean isJiangLiImg;

	@ManyToOne(fetch=FetchType.EAGER)
	ProductsPrivater productsPrivater;

	void cancelLazyEr()
	{
		this.productsPrivater.cancelLazyEr();
	}
}
