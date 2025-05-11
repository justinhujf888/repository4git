package com.weavict.edu.entity
import jakarta.persistence.*

@Table
@Entity
class StudentSals extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=30)
	String zxId;

	@Column(length=30)
	String xiaoId;

	@Column(length=2)
	int age;

	@Column(length=10)
	String name;

	@Column(length=18)
	String engName;

	@Column(length=15)
	String phone1;

	@Column(length=15)
	String phone2;

	@Column(length=15)
	String phone3;

	@Column(length=1)
	byte sex;

	@Column(length=1)
	byte status;

	@Column(length=150)
	String address;

	@Column(length=30)
	String areaPath;

	@Column(length=40)
	String currentSchool;

	@Column(length=20)
	String currentClass;

	@Temporal(TemporalType.TIMESTAMP)
	Date birthdayDate;

	@Column(length=30)
	String createrId;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	//0:线上活动；1：线下活动；
	@Column(length=2)
	byte sourceType;

	@Column(length=30)
	String sourceID;

	@Column(length=200)
	String description;

	@OneToMany(mappedBy="studentSals",fetch=FetchType.LAZY)
	List<StudentSalsDetail> studentSalsDetailList;

	void cancelLazyEr()
	{
		this.studentSalsDetailList = null;
	}
}
