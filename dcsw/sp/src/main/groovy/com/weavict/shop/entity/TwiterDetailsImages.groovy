package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class TwiterDetailsImages implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=200)
	String path;
	
	@Column(length=2)
	int listNum;

//	0:image；1:video
	byte mediaType;

	@ManyToOne(fetch=FetchType.EAGER)
	TwiterDetails twiterDetails;

	void cancelLazyEr()
	{
		this.twiterDetails.cancelLazyEr();
	}
}
