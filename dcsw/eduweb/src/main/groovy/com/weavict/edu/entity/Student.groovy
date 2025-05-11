package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class Student extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@EmbeddedId
	StudentPK id;

	@Column(length=30)
	String zxId;
	
	@Column(length=30)
	String wxid;

	@Column(length=35)
	String wxUniId;

	@Column(length=150)
	String headimgurl;

	@Column(length=20)
	String wxNickName;

	@Column(length=20)
	String wxNickEm;
	
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

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=30)
	Integer amb;

	@Column(length=3)
	Integer keLong;

	@Column(length=5)
	Integer keTimes;

	@Column(length=5)
	Integer keRuned;

	@Column(length=200)
	String familyBackground;

	Student() {

	}

	Student(String studentId,String xiaoId) {
		this.id = new StudentPK(studentId,xiaoId);
	}

	void cancelLazyEr()
	{

	}
}

@Table
@Entity
class StudentKeShiInfo extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length = 30)
	String studentId;

	@Column(length=30)
	String xiaoId;

	@Column(length = 2)
	byte type;

	@Column(length = 10)
	int fee;

	@Column(length=3)
	Integer keLong;

	@Column(length=5)
	Integer keTimes;

	@Column(length=30)
	String modifier;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length = 150)
	String remark;

	void cancelLazyEr()
	{

	}
}