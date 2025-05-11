package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class DemoClassMaster implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=50)
	String description;

	@Column(length=2)
	byte status;

	@Temporal(TemporalType.TIMESTAMP)
	Date beginDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date endDate;

	void cancelLazyEr()
	{

	}
}
