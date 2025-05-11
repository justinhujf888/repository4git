package com.weavict.shop.entity

import jakarta.persistence.*

@Table
@Entity
class TwiterZan implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

//	这里没有用ORM映射，因为只是点赞，不重要，加快速度
	@Column(length=30)
	String buyerId;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate

	@ManyToOne(fetch=FetchType.EAGER)
	Twiter twiter;

	void cancelLazyEr()
	{
		this.twiter.cancelLazyEr();
	}
}
