package com.weavict.edu.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.NamedQuery
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

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

@Table
@Entity
class ProductAttributeItems implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=20)
    String name;

    @ManyToOne(fetch=FetchType.EAGER)
    ProductAttribute productAttribute;

    void cancelLazyEr()
    {

    }
}

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

@Table
@Entity
class ProductAttributeItems4Product implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=20)
    String name;

    @ManyToOne(fetch=FetchType.EAGER)
    ProductAttribute4Product productAttribute4Product;

    void cancelLazyEr()
    {

    }
}

@Table
@Entity
class ProductSpecSetup implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=8)
    Integer price;

    @Column(length=8)
    int tuanPrice;

    @Column(length=8)
    Integer cbPrice;

    @Column(length=8)
    int store;

    @Column(length=1000)
    String ids;

    boolean used;

    @Column(length=200)
    String specImg;

    @ManyToOne(fetch=FetchType.EAGER)
    Product product;

    void cancelLazyEr()
    {
        product.cancelLazyEr();
    }
}

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

@Table
@Entity
class ProductSpecificationItems4Prodcut implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String name;

    @Column(length=8)
    Integer price;

    @Column(length=8)
    int tuanPrice;

    @Column(length=8)
    int store;

    @Column(length=2)
    int orderNum;

    @ManyToOne(fetch=FetchType.EAGER)
    ProductSpecification4Prodcut productSpecification4Prodcut;

    void cancelLazyEr()
    {
        this.productSpecification4Prodcut.cancelLazyEr();
    }
}