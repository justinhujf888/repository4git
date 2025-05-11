package com.weavict.edu.entity
import jakarta.persistence.*

@Table
@Entity
class StudentSalsDetail extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	//0:咨询；1：参加体验课；
	@Column(length=2)
	byte comeType;

	@Column(length=30)
	String humanId;

	@Column(length=30)
	String nextHumanId;
	
	@Column(length=300)
	String description;

	@Column(length=300)
	String nextDescription;

	@Column(length=30)
	String modifyId;

	@Column(length=1)
	int intention;

	@Temporal(TemporalType.TIMESTAMP)
	Date nextDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date modifyDate;

	@ManyToOne(fetch=FetchType.EAGER)
	StudentSals studentSals;

	void cancelLazyEr()
	{
		this.studentSals.cancelLazyEr();
	}
}
