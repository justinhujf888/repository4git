package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType;
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table

@Table
@Entity
class TwiterImages implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=200)
	String path;

	@Column(length=200)
	String thumbPath;

	@Column(length=5)
	int size;

	@Column(length=5)
	int duration;
	
	@Column(length=2)
	int listNum;

//	0:image；1:video；
	byte mediaType;

	@ManyToOne(fetch=FetchType.EAGER)
	Twiter twiter;

	void cancelLazyEr()
	{
		this.twiter.cancelLazyEr();
	}
}
