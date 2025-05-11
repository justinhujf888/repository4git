package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class StudentPK implements Serializable
{
	static final long serialVersionUID = 1L;

	@Column(nullable=false, insertable=false, updatable=false,length = 30)
	String studentId;

	@Column(length=30, nullable=false, insertable=false, updatable=false)
	String xiaoId;

	StudentPK()
	{

	}

	StudentPK(String studentId,String xiaoId)
	{
		this.studentId = studentId;
		this.xiaoId = xiaoId;
	}
}
