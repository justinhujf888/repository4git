package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductImages implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=200)
	String path;

	@Column(length=200)
	String posterPath;
	
	@Column(length=2)
	int bannerOrderListNum;

	@Column(length=2)
	int productOrderListNum;
	
	boolean isMasterImg;
	
	boolean isBannerImg;
	
	boolean isProductImg;
	
	boolean isSpecImg;

	boolean isVideo;

	@Column(length=50)
	String description;

	@ManyToOne(fetch=FetchType.EAGER)
	Product product;

	void cancelLazyEr()
	{
		this.product.cancelLazyEr();
	}
}
