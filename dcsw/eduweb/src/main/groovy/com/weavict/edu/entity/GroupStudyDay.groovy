package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.persistence.Transient

@Table
@Entity
class GroupStudyDay extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	GroupStudyDayPK groupStudyDayPK;

	@Column(length=30)
	String teacherId;

	@Column(length=200)
	String studyType;

	@Column(length=500)
	String studyInfo;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;



	GroupStudyDay()
	{

	}

	GroupStudyDay(String dateStr,String groupId)
	{
		this.groupStudyDayPK = new GroupStudyDayPK(dateStr,groupId);
	}

	void cancelLazyEr()
	{

	}
}

@Table
@Entity
class StudentStudyReport extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	StudentStudyReportPK studentStudyReportPK;

	@Column(length=150)
	String dataUrl;

	@Temporal(TemporalType.TIMESTAMP)
	Date beginDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date endDate;

	@Transient
	List<StudentStudyReportDetail> studentStudyReportDetailList;

	StudentStudyReport()
	{

	}

	void cancelLazyEr()
	{

	}
}

@Embeddable
class StudentStudyReportPK implements Serializable
{
	static final long serialVersionUID = 1L;

	@Column(length=25,nullable=false, insertable=false, updatable=false)
	String beginDateStr;

	@Column(length=25,nullable=false, insertable=false, updatable=false)
	String endDateStr;

	@Column(length=30,nullable=false, insertable=false, updatable=false)
	String groupId;

	StudentStudyReportPK()
	{

	}

	StudentStudyReportPK(String beginDateStr,String endDateStr,String groupId)
	{
		this.beginDateStr = beginDateStr;
		this.endDateStr = endDateStr;
		this.groupId = groupId;
	}
}

@Table
@Entity
class StudentStudyReportDetail extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	StudentStudyReportDetailPK studentStudyReportDetailPK;

	@Column(length=500)
	String teacherHope;

	@Column(length=500)
	String parentHope;

	@Transient
	StudentStudyReport studentStudyReport;

	void cancelLazyEr()
	{

	}
}

@Embeddable
class StudentStudyReportDetailPK implements Serializable
{
	static final long serialVersionUID = 1L;

	@Column(length=25,nullable=false, insertable=false, updatable=false)
	String beginDateStr;

	@Column(length=25,nullable=false, insertable=false, updatable=false)
	String endDateStr;

	@Column(length=30,nullable=false, insertable=false, updatable=false)
	String groupId;

	@Column(nullable=false, insertable=false, updatable=false,length = 30)
	String studentId;

	StudentStudyReportDetailPK()
	{

	}

	StudentStudyReportDetailPK(String studentId,String beginDateStr,String endDateStr,String groupId)
	{
		this.beginDateStr = beginDateStr;
		this.endDateStr = endDateStr;
		this.groupId = groupId;
		this.studentId = studentId;
	}
}