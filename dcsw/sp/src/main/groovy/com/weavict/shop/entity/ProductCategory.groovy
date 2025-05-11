package com.weavict.shop.entity

import jakarta.persistence.*

/**
 * The persistent class for the productcategory database table.
 * 
 */
@Entity
@NamedQuery(name="Productcategory.findAll", query="SELECT p FROM ProductCategory p")
class ProductCategory extends BEntity implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=30)
	String appId;

	@Column(length=30)
	String privaterId;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=500)
	String metaDescription;

	@Column(length=500)
	String metaKeywords;

	@Temporal(TemporalType.TIMESTAMP)
	Date modifyDate;

	@Column(length=20)
	String name;

	int orderListNum;

	@Column(length=250)
	String path;

	@Column(length=2)
	int layer;

	//bi-directional many-to-one association to Product
	@ManyToMany(cascade = CascadeType.PERSIST, mappedBy = "productCategoryList", fetch = FetchType.LAZY)
	List<Product> productList;

	//bi-directional many-to-one association to Productcategory
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="parent_id")
	ProductCategory productCategory;

	//bi-directional many-to-one association to Productcategory
	@OneToMany(mappedBy="productCategory",fetch = FetchType.LAZY)
	List<ProductCategory> productCategoryList;

	ProductCategory addProductCategory(ProductCategory productCategory) {
		this.productCategoryList?.add(productCategory);
		this.productCategory = productCategory;
		return productCategory;
	}

	ProductCategory removeProductCategory(ProductCategory productCategory) {
		this.productCategoryList?.remove(productCategory);
		this.productCategory = null;
		return productCategory;
	}
	void cancelLazyEr()
	{
		this.productList = null;
		this.productCategoryList = null;
	}
}