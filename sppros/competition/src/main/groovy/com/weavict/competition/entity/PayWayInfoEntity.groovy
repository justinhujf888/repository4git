package com.weavict.competition.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Embeddable
class PayWayInfoEntityPK implements Serializable
{
    static final long serialVersionUID = 1L;

    /**
     * 微信或者其他小程序应用编号
     */
    @Column(name="appid",length=30,nullable=false, insertable=false, updatable=false)
    String appId;

    /**
     * 支付方式类型 0公众号；1微信;2支付宝
     */
    @Column(name="type",length=20,nullable=false, insertable=false, updatable=false)
    byte type;

    PayWayInfoEntityPK()
    {}

    PayWayInfoEntityPK(String appId,byte type)
    {
        this.appId = appId;
        this.type = type;
    }
}

@Table
@Entity
class PayWayInfoEntity extends BEntity implements Serializable,IEntity
{
    static final long serialVersionUID = 1L;

    @EmbeddedId
    PayWayInfoEntityPK payWayInfoEntityPK;

    @Column(length=30)
    String appName;

    /**
     * appSecret 是 appId 对应的接口密码,(服务商模式)就是服务商的 appSecret
     * 如果是支付宝支付就是支付宝的公钥
     */
    @Column(length=64)
    String appSecret;

    /**
     * API 密钥，微信商户后台配置
     * 如果是支付宝支付就是支付宝的私钥
     */
    @Column(length=500)
    String partnerKey;

    /**
     * apiclient_cert.p12 证书绝对路径，在服务商微信商户后台下载
     */
    @Column(length=260)
    String certPath;

    /**
     * 外网访问项目的域名，支付通知、回调中会使用
     */
    @Column(length=350)
    String doMain;

    /**
     * 外网访问项目的域名，退款通知、回调中会使用
     */
    @Column(length=350)
    String returnDoMain;

    /**
     * 用于模板消息
     */
    @Column(length=350)
    String notiUrl;

    /**
     * 微信支付商户号
     */
    @Column(length=30)
    String mchId;

    /**
     * 是否是服务商模式
     */
    Boolean isServer;

    /**
     * 子商户号的应用编号(服务商模式)
     */
    @Column(length=30)
    String subAppId;

    /**
     * 子商户号(服务商模式)
     */
    @Column(length=30)
    String subMchId;

    /**
     * 支付描述信息
     */
    @Column(length=50)
    String body;

    void cancelLazyEr()
    {

    }
}
