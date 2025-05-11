package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class DemoClassPersonKes implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	DemoClassPersonKesPK demoClassPersonKesPK;

	@Column(length=30)
	String dailiNo;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	DemoClassPersonKes()
	{

	}

	DemoClassPersonKes(String parentId,String demoClassId,String demoClassMasterId)
	{
		this.demoClassPersonKesPK = new DemoClassPersonKesPK(parentId,demoClassId,demoClassMasterId);
	}

	void cancelLazyEr()
	{

	}
}

@Embeddable
class DemoClassPersonKesPK implements Serializable
{
	static final long serialVersionUID = 1L;

	@Column(nullable=false, insertable=false, updatable=false,length = 30)
	String parentId;

	@Column(nullable=false, insertable=false, updatable=false,length = 30)
	String demoClassId;

	@Column(nullable=false, insertable=false, updatable=false,length = 30)
	String demoClassMasterId;

	DemoClassPersonKesPK()
	{

	}

	DemoClassPersonKesPK(String parentId,String demoClassId,String demoClassMasterId)
	{
		this.parentId = parentId;
		this.demoClassId = demoClassId;
		this.demoClassMasterId = demoClassMasterId;
	}
}