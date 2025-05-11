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
class TeachStudy implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=50)
	String keCheng;

	@Column(length=50)
	String teachContext;

	@Column(length=50)
	String point;

	@Column(length=250)
	String greeting;

	@Column(length=250)
	String games;

	@Column(length=250)
	String homeWork;

	@Temporal(TemporalType.TIMESTAMP)
	Date teachDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@ManyToOne(fetch=FetchType.EAGER)
	Teacher teacher;

	@ManyToOne(fetch=FetchType.EAGER)
	TeacherGroup teacherGroup;

	void cancelLazyEr()
	{
		this.teacher.cancelLazyEr();
		this.teacherGroup.cancelLazyEr();
	}
}
