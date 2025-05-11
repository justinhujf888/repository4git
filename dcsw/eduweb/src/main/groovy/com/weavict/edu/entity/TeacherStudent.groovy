package com.weavict.edu.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table
@Entity
class TeacherStudent implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	TeacherStudentPK teacherStudentPK;

	TeacherStudent()
	{

	}

	TeacherStudent(String xiaoId,String teacherId,String studentId,String groupId)
	{
		this.teacherStudentPK = new TeacherStudentPK(xiaoId,teacherId,studentId,groupId);
	}

	void cancelLazyEr()
	{

	}
}
