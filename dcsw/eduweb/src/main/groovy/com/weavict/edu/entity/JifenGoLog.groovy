package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class JifenGoLog implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=30)
	String productId;

	@Column(length=30)
	Integer productJifen;

	@Column(length=30)
	Integer jifenAfter;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@ManyToOne(fetch=FetchType.EAGER)
	Student student;

	@Column(length=60)
	String userId;

//	0：学生积分换购；1：商城积分购物；10：学生获取积分；11：会员获取积分
	@Column(length=2)
	byte userType;

	void cancelLazyEr()
	{

	}
}
