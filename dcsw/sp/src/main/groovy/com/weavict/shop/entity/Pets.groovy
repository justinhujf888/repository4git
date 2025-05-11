package com.weavict.shop.entity

import jakarta.persistence.*

/**
 * The persistent class for the brand database table.
 * 
 */
@Entity
class Pets implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;
	
	@Column(length=30)
	String name;

	@Column(length=1)
	byte sex;

	@Column(length=200)
	String headImgPath;

	@Temporal(TemporalType.TIMESTAMP)
	Date birthday;

	@Temporal(TemporalType.TIMESTAMP)
	Date changeMoneyDate;

	//疫苗日期
	@Temporal(TemporalType.TIMESTAMP)
	Date vaccineDate;

	//驱虫提醒日期
	@Temporal(TemporalType.TIMESTAMP)
	Date clearBugDate;

	@Column(length=2)
	int geMoth;

//	0：未收费；1：已收费；
	@Column(length=2)
	byte status;

//	0：正常；1：不接受服务状态
	@Column(length=2)
	byte petsStatus;

//	性格
	@Column(length=2)
	byte disposition;

//	是否受过训练
	boolean isTraining;

//	是否绝育
	boolean isTerilization;

//	体重
	@Column(length=6)
	int weight;

	@Column(length=10)
	String color;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@ManyToOne(fetch=FetchType.EAGER)
	PetType petType;

	@ManyToOne(fetch=FetchType.EAGER)
	Buyer buyer;

	@OneToMany(mappedBy="pets",fetch = FetchType.LAZY)
	List<PetsMedical> petsMedicalList;

	void cancelLazyEr()
	{
		this.petsMedicalList = null;
		this.petType.cancelLazyEr();
		this.buyer.cancelLazyEr();
	}
}