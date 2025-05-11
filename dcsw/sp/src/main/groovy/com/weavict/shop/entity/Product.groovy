package com.weavict.shop.entity

import jakarta.persistence.*

/**
 * The persistent class for the product database table.
 * 
 */
@Entity
@NamedQuery(name="Product.findAll", query="SELECT p FROM Product p")
class Product extends BEntity implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

//	0：支持到店取货和快递；1：只支持到店；2：只支持快递；
	@Column(length=2)
	byte takeType;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=5000)
	String description;

	@Column(length=50)
	String htmlFilePath;

	//最好
	@Column(name="isbest")
	boolean isBest;

	//最热
	@Column(name="ishot")
	boolean isHot;

	//上架
	@Column(name="ismarketable")
	boolean isMarketable;

	//新品
	@Column(name="isnew")
	boolean isNew;

	//新品
	@Column(name="isdel")
	boolean isDel;
	
	//售罄
	@Column(name="issalenone")
	boolean saleNone;

	@Column(length=8)
	int marketPrice;

	@Column(length=8)
	int cbPrice;

//	设置最大的可优惠金额，目前来自用户红包
	@Column(length=8)
	int yhAmount;

	@Column(length=500)
	String metaDescription;

	@Column(length=500)
	String metaKeywords;

	@Temporal(TemporalType.TIMESTAMP)
	Date modifyDate;

	@Column(length=50)
	String name;

	@Column(length=8)
	int price;

	@Column(length=8)
	int tuanPrice;

	@Column(length=50)
	String productSn;

	@Column(length=8)
	int store;

	@Column(length=8)
	int tgPersonsCount;

	@Column(length=4)
	int tgEndDays;

	//品牌
	//bi-directional many-to-one association to Brand
	@ManyToOne(fetch=FetchType.EAGER)
	Brand brand;

	@ManyToOne(fetch=FetchType.EAGER)
	ProductsPrivater productPrivater;

	@ManyToMany(cascade = CascadeType.PERSIST,fetch=FetchType.LAZY)
	@JoinTable(name = "product_category", inverseJoinColumns = @JoinColumn(name = "category_id"), joinColumns = @JoinColumn(name = "product_id"))
	List<ProductCategory> productCategoryList;

	@Column(length=200)
	String masterImg;

	@OneToMany(mappedBy="product",fetch = FetchType.LAZY)
	List<ProductSpecSetup> productSpecSetupList;

	@OneToMany(mappedBy="product",fetch = FetchType.LAZY)
	List<ProductSpecification4Prodcut> productSpecification4ProdcutList;

	@OneToMany(mappedBy="product",fetch = FetchType.LAZY)
	List<ProductImages> productImagesList;

	@OneToMany(mappedBy="product",fetch = FetchType.LAZY)
	List<ProductAttribute4Product> productAttribute4ProductList;

	@OneToMany(mappedBy="product",fetch = FetchType.LAZY)
	List<OrderItem> orderItemList;

	@OneToMany(mappedBy="product",fetch = FetchType.LAZY)
	List<OrderBuyers> orderBuyersList;


	void cancelLazyEr()
	{
		this.productSpecification4ProdcutList = null;
		this.productImagesList = null;
		this.productAttribute4ProductList = null;
		this.brand?.cancelLazyEr();
		this.productPrivater?.cancelLazyEr();
		this.productCategoryList = null;
		this.productSpecSetupList = null;
		this.orderItemList = null;
		this.orderBuyersList = null;
	}
}