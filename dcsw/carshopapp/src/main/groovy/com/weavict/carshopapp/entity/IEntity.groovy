package com.weavict.carshopapp.entity

import jakarta.persistence.Transient

/**
 * The persistent class for the brand database table.
 * 
 */
class BEntity
{
	@Transient
	String temp;

	@Transient
	Map tempMap;
}

interface IEntity
{
	void cancelLazyEr();
}