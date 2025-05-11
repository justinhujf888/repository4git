package com.weavict.edu.entity
/**
 * The persistent class for the product database table.
 * 
 */
import jakarta.persistence.*

@Entity
class DemoClassSchool implements Serializable,IEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=2)
	byte schoolStatus;

	@Column(length=50)
	String name;

	@Column(length=200)
	String schoolImg;

	@Column(length=200)
	String logoImg;

	@Column(length=25)
	String area;

	@Column(length=100)
	String address;

	@Column(length=500)
	String description;

	@OneToMany(mappedBy="demoClassSchool",fetch=FetchType.LAZY)
	List<DemoClass> demoClassList;

	void cancelLazyEr()
	{
		this.demoClassList = null;
	}
}