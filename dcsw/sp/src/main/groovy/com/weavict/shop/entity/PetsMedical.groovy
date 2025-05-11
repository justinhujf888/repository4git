package com.weavict.shop.entity

import jakarta.persistence.*

/**
 * The persistent class for the brand database table.
 * 
 */
@Entity
class PetsMedical implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;
	
	@Column(length=30)
	String medicalName;

	@Temporal(TemporalType.TIMESTAMP)
	Date medicalDate;

	@Column(length=25)
	String area;

	@Column(length=100)
	String address;

	@Column(length=20)
	String hospital;

	@Column(length=20)
	String hospitalTel;

	@Column(length=15)
	String hospitalContact;

	@Column(length=250)
	String description;

	@ManyToOne(fetch=FetchType.EAGER)
	Pets pets;

	@OneToMany(mappedBy="petsMedical",fetch = FetchType.LAZY)
	List<PetsMedicalImages> petsMedicalImagesList;

	void cancelLazyEr()
	{
		this.pets.cancelLazyEr();
		this.petsMedicalImagesList = null;
	}
}