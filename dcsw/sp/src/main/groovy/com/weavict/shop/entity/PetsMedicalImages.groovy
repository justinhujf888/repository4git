package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class PetsMedicalImages implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;
	
	@Column(length=200)
	String path;

	@Column(length=200)
	String thumbPath;

	@Column(length=2)
	int listNum;

//	0:image；1:video
	byte mediaType;

	@ManyToOne(fetch=FetchType.EAGER)
	PetsMedical petsMedical;

	void cancelLazyEr()
	{
		this.petsMedical.cancelLazyEr();
	}
}
