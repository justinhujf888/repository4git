package com.weavict.edu.entity
import jakarta.persistence.*

class WordLabelPK implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name="wordId",length=50,nullable=false, insertable=false, updatable=false)
	String wordId;

	@Column(name="wordType",length=2,nullable=false, insertable=false, updatable=false)
	int wordType;

	@Column(name="label",length=30,nullable=false, insertable=false, updatable=false)
	String label;
}

@Table
@Entity
class WordLabel implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@EmbeddedId
	WordLabelPK wordLabelPK;

	@Column(length=4)
	int sortNum;

	void cancelLazyEr()
	{

	}
}


@Table
@Entity
class Word implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=50)
	String name;

	@Column(length=50)
	String updateName;

	@Column(length=150)
	String imgUrl;

	@Column(length=150)
	String imgUrl2;

	@Column(length=200)
	String description;

//	音标
	@Column(length=100)
	String phonetics;

	@Column(length=4)
	int sortNum;

	@Column(length=30)
	String zxId;

	@OneToMany(mappedBy="word",fetch = FetchType.LAZY)
	List<WordXing> wordXingList;

//	例句
	@OneToMany(mappedBy="word",fetch = FetchType.LAZY)
	List<WordEg> wordEgList;

//	联想
	@OneToMany(mappedBy="word",fetch = FetchType.LAZY)
	List<WordAssociate> wordAssociateList;

	void cancelLazyEr()
	{
		this.wordXingList = null;
		this.wordEgList = null;
		this.wordAssociateList = null;
	}
}

@Table
@Entity
class WordXing implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=180)
	String chnXing;

	@ManyToOne(fetch=FetchType.EAGER)
	Word word;

	void cancelLazyEr()
	{
		this.word.cancelLazyEr();
	}
}

@Table
@Entity
class WordEg implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=180)
	String engEg;

	@Column(length=180)
	String chnEg;

	@ManyToOne(fetch=FetchType.EAGER)
	Word word;

	void cancelLazyEr()
	{
		this.word.cancelLazyEr();
	}
}

@Table
@Entity
//联想
class WordAssociate implements Serializable,IEntity
{
	static final long serialVersionUID = 1L;

	@Id
	@Column(length=30)
	String id;

	@Column(length=180)
	String engAss;

	@Column(length=180)
	String chnAss;

	@ManyToOne(fetch=FetchType.EAGER)
	Word word;

	void cancelLazyEr()
	{
		this.word.cancelLazyEr();
	}
}