package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class GroupStudyDayPK implements Serializable
{
	static final long serialVersionUID = 1L;

	@Column(length=25,nullable=false, insertable=false, updatable=false)
	String dateStr;

	@Column(length=25,nullable=false, insertable=false, updatable=false)
	String groupId;

	GroupStudyDayPK()
	{

	}

	GroupStudyDayPK(String dateStr,String groupId)
	{
		this.dateStr = dateStr;
		this.groupId = groupId;
	}
}
