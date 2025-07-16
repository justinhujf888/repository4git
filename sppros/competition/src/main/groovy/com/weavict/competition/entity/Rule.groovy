package com.weavict.competition.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table
@Entity
class Rule extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @Id
    @Column(length=30)
    String id;

    @Column(length=30)
    String name;

    void cancelLazyEr()
    {

    }
}

@Table
@Entity
class RulePermission extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    RulePermissionPK rulePermissionPK;

    void cancelLazyEr()
    {

    }
}

@Embeddable
class RulePermissionPK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String ruleId;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String permissionId;

    RulePermissionPK() {}

    RulePermissionPK(String ruleId,String permissionId)
    {
        this.ruleId = ruleId;
        this.permissionId = permissionId;
    }
}