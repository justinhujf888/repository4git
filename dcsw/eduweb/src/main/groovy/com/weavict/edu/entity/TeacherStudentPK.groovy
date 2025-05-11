package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class TeacherStudentPK implements Serializable
{
	static final long serialVersionUID = 1L;

	@Column(nullable=false, insertable=false, updatable=false,length = 30)
	String xiaoId;

	@Column(nullable=false, insertable=false, updatable=false,length = 30)
	String studentId;

	@Column(length=30, nullable=false, insertable=false, updatable=false)
	String teacherId;

	@Column(length=25, nullable=false, insertable=false, updatable=false)
	String groupId;

	TeacherStudentPK()
	{

	}

	TeacherStudentPK(String xiaoId,String teacherId,String studentId,String groupId)
	{
		this.xiaoId = xiaoId;
		this.studentId = studentId;
		this.teacherId = teacherId;
		this.groupId = groupId;
	}
}
