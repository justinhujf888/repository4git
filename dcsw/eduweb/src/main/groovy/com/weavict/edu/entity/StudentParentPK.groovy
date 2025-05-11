package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class StudentParentPK implements Serializable
{
	static final long serialVersionUID = 1L;

	@Column(nullable=false, insertable=false, updatable=false,length = 30)
	String xiaoId;

	@Column(nullable=false, insertable=false, updatable=false,length = 30)
	String StudentId;

	@Column(length=30, nullable=false, insertable=false, updatable=false)
	String parentId;

	StudentParentPK()
	{

	}

	StudentParentPK(String xiaoId,String StudentId,String parentId)
	{
		this.xiaoId = xiaoId;
		this.StudentId = StudentId;
		this.parentId = parentId;
	}
}
