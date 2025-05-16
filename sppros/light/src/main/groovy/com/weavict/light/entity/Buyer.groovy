package com.weavict.light.entity

import com.weavict.light.entity.BEntity
import com.weavict.light.entity.IEntity
import jakarta.persistence.*

@Table
@Entity
class Buyer extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=20)
    String phone;

    @Column(length=50)
    String name;

    @Column(length=20)
    String loginName;

    @Column(length=50)
    String password;

    @Column(length=32)
    String wxid;

    @Column(length=32)
    String wxopenid;

    @Column(length=25)
    String area;

    @Column(length=100)
    String address;

    @Column(length=20)
    String tel;

    @Column(length=150)
    String headImgUrl;

    @Column(length=20)
    String wxNickName;

    @Column(length=20)
    String wxNickEm;

    @Column(length=30)
    Integer amb;

    @Column(length=1)
    byte sex;

    @Column(length=250)
    String description;

    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @OneToMany(mappedBy="buyer",fetch = FetchType.LAZY)
    List<Device> deviceList;

    void cancelLazyEr()
    {

    }
}