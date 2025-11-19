package com.weavict.competition.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Embeddable
class RulePK implements Serializable
{
    static final long serialVersionUID = 1L;

    @Column(name="appid",length=30,nullable=false, insertable=false, updatable=false)
    String appId;

    @Column(name="ruleid",length=20,nullable=false, insertable=false, updatable=false)
    String ruleId;

    RulePK()
    {

    }

    RulePK(String appId,String ruleId)
    {
        this.appId = appId;
        this.ruleId = ruleId;
    }
}

@Table
@Entity
class Rule extends BEntity implements Serializable, IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    RulePK rulePK;

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
    String appId;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String ruleId;

    @Column(nullable=false, insertable=false, updatable=false,length = 30)
    String permissionId;

    RulePermissionPK() {}

    RulePermissionPK(String appId,String ruleId,String permissionId)
    {
        this.appId = appId;
        this.ruleId = ruleId;
        this.permissionId = permissionId;
    }
}