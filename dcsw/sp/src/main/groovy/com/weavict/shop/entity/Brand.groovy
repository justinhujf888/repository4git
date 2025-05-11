package com.weavict.shop.entity

import jakarta.persistence.*

/**
 * The persistent class for the brand database table.
 * 
 */
@Entity
@NamedQuery(name="Brand.findAll", query="SELECT b FROM Brand b")
class Brand implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=3000)
	String introduction;

	@Column(length=200)
	String logo;

	@Temporal(TemporalType.TIMESTAMP)
	Date modifyDate;

	@Column(length=30)
	String name;

	@Column(length=30)
	int orderList;

	@Column(length=100)
	String url;

	@ManyToOne(fetch=FetchType.EAGER)
	ProductsPrivater productsPrivater;

	//bi-directional many-to-one association to Product
	@OneToMany(mappedBy="brand",fetch = FetchType.LAZY)
	List<Product> productList;

	void cancelLazyEr()
	{
		this.productList = null;
		this.productsPrivater.cancelLazyEr();
	}
}