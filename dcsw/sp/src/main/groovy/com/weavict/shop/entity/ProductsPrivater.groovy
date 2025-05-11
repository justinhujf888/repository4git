package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class ProductsPrivater extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;

	@Column(length=30)
	String appId;

	@Column(length=30)
	String privaterId;
	
	@Column(length=50)
	String name;

//	0:一般供应商；1:社区商户
	@Column(length=2)
	byte privaterType;

//	[{id:0,name:"两纵列"},{id:1,name:"3纵列（产品图小）"},{id:2,name:"1纵列（产品图大）"},{id:3,name:"餐饮菜单展示"}]
	@Column(length=2)
	byte productListType;

	@Column(length=30)
	String orgrationId;

	@Column(length=20)
	String loginName;

	@Column(length=50)
	String password;
	
	@Column(length=32)
	String wxid;
	
	@Column(length=32)
	String wxopenid;

	@Column(length=25)
	String area;
	
	@Column(length=100)
	String address;
	
	@Column(length=20)
	String phone;
	
	@Column(length=20)
	String tel;
	
	@Column(length=3000)
	String description;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=20)
	String lat;

	@Column(length=20)
	String lng;

	@OneToMany(mappedBy="productPrivater",fetch = FetchType.LAZY)
	List<Product> productList;

	@OneToMany(mappedBy="productsPrivater",fetch = FetchType.LAZY)
	List<Brand> brandList;

	@OneToMany(mappedBy="productsPrivater",fetch = FetchType.LAZY)
	List<ProductSpecification> productSpecificationList;

	@OneToMany(mappedBy="productsPrivater",fetch = FetchType.LAZY)
	List<ProductAttribute> productAttributeList;

	@OneToMany(mappedBy="productsPrivater",fetch = FetchType.LAZY)
	List<ProductsPrivaterImages> productsPrivaterImagesList;

	@OneToMany(mappedBy="productsPrivater",fetch = FetchType.LAZY)
	List<Coupon> couponList;

	void cancelLazyEr()
	{
		this.productList = null;
		this.brandList = null;
		this.productsPrivaterImagesList = null;
		this.productSpecificationList = null;
		this.productAttributeList = null;
		this.couponList = null;
	}
}

@Embeddable
class PayWayInfoEntityPK implements Serializable
{
	static final long serialVersionUID = 1L;

	/**
	 * 微信或者其他小程序应用编号
	 */
	@Column(name="appid",length=30,nullable=false, insertable=false, updatable=false)
	String appId;

	/**
	 * 支付方式类型 0公众号；1微信;2支付宝
	 */
	@Column(name="type",length=20,nullable=false, insertable=false, updatable=false)
	byte type;

	PayWayInfoEntityPK()
	{}

	PayWayInfoEntityPK(String appId,byte type)
	{
		this.appId = appId;
		this.type = type;
	}
}

@Table
@Entity
class PayWayInfoEntity extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	PayWayInfoEntityPK payWayInfoEntityPK;

	@Column(length=30)
	String appName;

	/**
	 * appSecret 是 appId 对应的接口密码,(服务商模式)就是服务商的 appSecret
	 * 如果是支付宝支付就是支付宝的公钥
	 */
	@Column(length=64)
	String appSecret;

	/**
	 * API 密钥，微信商户后台配置
	 * 如果是支付宝支付就是支付宝的私钥
	 */
	@Column(length=500)
	String partnerKey;

	/**
	 * apiclient_cert.p12 证书绝对路径，在服务商微信商户后台下载
	 */
	@Column(length=260)
	String certPath;

	/**
	 * 外网访问项目的域名，支付通知、回调中会使用
	 */
	@Column(length=350)
	String doMain;

	/**
	 * 外网访问项目的域名，退款通知、回调中会使用
	 */
	@Column(length=350)
	String returnDoMain;

	/**
	 * 用于模板消息
	 */
	@Column(length=350)
	String notiUrl;

	/**
	 * 微信支付商户号
	 */
	@Column(length=30)
	String mchId;

	/**
	 * 是否是服务商模式
	 */
	Boolean isServer;

	/**
	 * 子商户号的应用编号(服务商模式)
	 */
	@Column(length=30)
	String subAppId;

	/**
	 * 子商户号(服务商模式)
	 */
	@Column(length=30)
	String subMchId;

	/**
	 * 支付描述信息
	 */
	@Column(length=50)
	String body;

	void cancelLazyEr()
	{

	}
}

@Table
@Entity
class UserShop implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	UserShopPK userShopPK;

	@Column(length=20)
	String name;

	//    0：在职；1：离职；2：注册管理员
	@Column(length=1)
	byte status;

	//    0：普通员工；1：管理层；2：老板级别；3：代理销售
	@Column(length=1)
	byte ruleStatus;
	//是否为代理
	@Column(length=1)
	boolean shiDaiLi;

	@Column(length=20)
	String zhiWei;

	@Column(length=20)
	String loginName;

	@Column(length=50)
	String password;

	void cancelLazyEr()
	{

	}
}

@Embeddable
class UserShopPK implements Serializable
{
	static final long serialVersionUID = 1L;

	@Column(name="user_id",length=20,nullable=false, insertable=false, updatable=false)
	String userId;

	@Column(name="shop_id",length=30,nullable=false, insertable=false, updatable=false)
	String shopId;

	UserShopPK() {}

	UserShopPK(String userId,String shopId)
	{
		this.userId = userId;
		this.shopId = shopId;
	}
}
