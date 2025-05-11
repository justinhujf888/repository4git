package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class StudentParent implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	StudentParentPK studentParentPK;

	@Column(length=1)
	byte cgx;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	StudentParent()
	{

	}

	StudentParent(String studentId,String parentId)
	{
		this.studentParentPK = new StudentParentPK(studentId,parentId);
	}

	void cancelLazyEr()
	{

	}
}
