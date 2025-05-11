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
class DemoClass implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=10)
	String name;

	@Column(length=2)
	byte status;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@ManyToOne(fetch=FetchType.EAGER)
	DemoClassSchool demoClassSchool;

	void cancelLazyEr()
	{
		this.demoClassSchool.cancelLazyEr();
	}
}
