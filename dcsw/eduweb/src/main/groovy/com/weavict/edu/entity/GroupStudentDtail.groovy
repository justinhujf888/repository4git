package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class GroupStudentDtail extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	GroupStudentDtailPK groupStudentDtailPK;

	@Column(length=1)
	byte status;

	@Column(length=30)
	String answer;

	@Column(length=30)
	Integer amb;

	@Column(length=30)
	Integer samb;

	@Column(length=30)
	Integer totalAmb;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=2000)
	String mediaPath;

	GroupStudentDtail()
	{

	}

	GroupStudentDtail(String xiaoId,String teacherId,String studentId,String groupId,String dateStr)
	{
		this.groupStudentDtailPK = new GroupStudentDtailPK(xiaoId,teacherId,studentId,groupId,dateStr);
	}

	void cancelLazyEr()
	{

	}
}
