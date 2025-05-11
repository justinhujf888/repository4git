package com.weavict.shop.entity

import jakarta.persistence.*

/**
 * The persistent class for the brand database table.
 * 
 */
@Entity
class PetType implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;
	
	@Column(length=30)
	String name;

	@Column(length=1)
	String firstChar;

	@Column(length=200)
	String imgPath;

	@Column(length=2)
	byte bigType;

	@OneToMany(mappedBy="petType",fetch = FetchType.LAZY)
	List<Pets> petsList;

	void cancelLazyEr()
	{
		this.petsList = null;
	}
}