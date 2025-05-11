package com.weavict.edu.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.persistence.Transient

@Table
@Entity
class Book extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;
	
	@Id
	@Column(length=30)
	String id;

	@Column(length=150)
	String name;

	@Column(length=150)
	String imgUrl;

	@Column(length=4)
	int sortNum;

	//[{id:0,type:"单词/文字"},{id:1,type:"题库"},{id:2,type:"绘本"},{id:3,type:"视频"},{id:4,type:"文章"},{id:5,type:"综合单元"}];
	@Column(length=2)
	byte bookType;

	//[{id:0,cate:"英语"},{id:1,cate:"语文"},{id:2,cate:"数学"},{id:3,cate:"物理"}];
	@Column(length=2)
	byte bookCate;

	//[{id:0,cate:"早教"},{id:1,cate:"幼儿"},{id:2,cate:"小学"},{id:3,cate:"初中"},{id:4,cate:"高中"},{id:5,cate:"大学"},{id:6,cate:"成人"}]
	@Column(length=2)
	byte bookSubCate;

	boolean isHot;

	boolean isMarket;

	@Column(length=800)
	String description;

	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Column(length=30)
	String zxId;

	@Column(length=8)
	int price;

	@Column(length=4)
	int yxDays;

	@Column(length=500)
	String tuanPrice;

	@OneToMany(mappedBy="book",fetch = FetchType.LAZY)
	List<BookUni> bookUniList;

	void cancelLazyEr()
	{
		this.bookUniList = null;
	}
}

@Table
@Entity
class BookUni implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=150)
	String name;

	@Column(length=150)
	String imgUrl;

	@Column(length=800)
	String description;

	@Column(length=4)
	int sortNum;

	@Column
	boolean isBuy;

	@ManyToOne(fetch=FetchType.EAGER)
	Book book;

	void cancelLazyEr()
	{
		this.book.cancelLazyEr();
	}
}

@Table
@Entity
class BookCate implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=15)
	String name;

	@Column(length=2)
	byte layer;

	@Column(length=30)
	String parentId;

	@Column(length=4)
	int sortNum;

	@OneToMany(mappedBy="bookCate",fetch = FetchType.LAZY)
	List<BookCateDetail> bookCateDetailList;

	void cancelLazyEr()
	{
		this.bookCateDetailList = null;
	}
}

@Table
@Entity
class BookCateDetail extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=15)
	String name;

	@Column(length=2)
	byte layer;

	@Column(length=30)
	String parentId;

	@Column(length=4)
	int sortNum;

	@ManyToOne(fetch=FetchType.EAGER)
	BookCate bookCate;

	@Transient
	List subList;

	void cancelLazyEr()
	{
		this.bookCate.cancelLazyEr();
	}
}

class BookCateBookDetailPK implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name="bookId",length=30,nullable=false, insertable=false, updatable=false)
	String bookId;

	@Column(name="bookCateDetailId",length=30,nullable=false, insertable=false, updatable=false)
	String bookCateDetailId;
}

@Table
@Entity
class BookCateBookDetail implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	BookCateBookDetailPK bookCateBookDetailPK;

	void cancelLazyEr()
	{

	}
}

@Table
@Entity
class BookSubBook extends BEntity implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	BookSubBookPK bookSubBookPK;

	@Column(length=2)
	byte bookType;

	void cancelLazyEr()
	{

	}
}

class BookSubBookPK implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name="bookId",length=30,nullable=false, insertable=false, updatable=false)
	String bookId;

	@Column(name="subBookId",length=30,nullable=false, insertable=false, updatable=false)
	String subBookId;

	BookSubBookPK()
	{

	}

	BookSubBookPK(String bookId,String subBookId)
	{
		this.bookId = bookId;
		this.subBookId = subBookId;
	}
}