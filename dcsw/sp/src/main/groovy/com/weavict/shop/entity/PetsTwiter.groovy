package com.weavict.shop.entity

import jakarta.persistence.*
/**
 * The persistent class for the product database table.
 * 
 */
@Entity
class PetsTwiter implements Serializable,IEntity
{
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	PetsTwiterPK petsTwiterPK;

	void cancelLazyEr()
	{

	}
}

class PetsTwiterPK implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name="petid",length=30,nullable=false, insertable=false, updatable=false)
	String petsId;

	@Column(name="twiterid",length=30,nullable=false, insertable=false, updatable=false)
	String twiterId;
}