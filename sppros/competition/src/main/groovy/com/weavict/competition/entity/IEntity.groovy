package com.weavict.competition.entity

import jakarta.persistence.Transient

/**
 * The persistent class for the brand database table.
 * 
 */

class BEntity
{
	@Transient
	Object temp;

	@Transient
	Map tempMap;
}

interface IEntity
{
	void cancelLazyEr();
}