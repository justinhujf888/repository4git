package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

@Table
@Entity
class Manager implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
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
	String bySchool;

	@Temporal(TemporalType.TIMESTAMP)
	Date birthdayDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=30)
	String zxId;

	void cancelLazyEr()
	{

	}
}
