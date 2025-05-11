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
class Bills implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;

	@Column(length=30)
	String creater;

	@Column(length=30)
	String modifier;
	
	@Column(length=10)
	String name;

	@Column(length=50)
	String description;

	@Column(length=30)
	String condition;

//	0：未领取；1：已经领取，未使用；2：已经使用；3：已经过期
	@Column(length=1)
	byte status;

	@Column(length=2)
	byte billsType;

	@Column(length=3)
	int sourceXiaoId;

	@Column(length=3)
	int distXiaoId;

	@Column(length=9)
	int amout;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date usedDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date endDate;

	@ManyToOne(fetch=FetchType.EAGER)
	Parent parent;

	void cancelLazyEr()
	{
		this.parent.cancelLazyEr();
	}
}
