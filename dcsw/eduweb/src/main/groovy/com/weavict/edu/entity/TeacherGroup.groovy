package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class TeacherGroup extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=25)
	String id;
	
	@Column(length=50)
	String name;

	@Column(length=30,name="teacher_id")
	String teacherId;

	@Column(length=30)
	String xiaoId;

//	0：开班中；1：关闭
	@Column(length=1)
	byte status;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date beginDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date endDate;

	@Column(length=500)
	String weekDaysTime;

	@Column(length=100)
	String keSubject;

	@OneToMany(mappedBy="teacherGroup",fetch=FetchType.LAZY)
	List<TeachStudy> teachStudyList;

	void cancelLazyEr()
	{
		this.teachStudyList = null;
	}
}
