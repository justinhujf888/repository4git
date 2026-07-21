package com.weavict.mqttspserver.mqtt

import cn.hutool.crypto.SecureUtil
import com.alibaba.fastjson2.JSON
import com.weavict.website.common.OtherUtils
import net.dreamlu.mica.net.core.ChannelContext
import org.dromara.mica.mqtt.codec.MqttQoS
import org.dromara.mica.mqtt.core.server.MqttServerCustomizer
import org.dromara.mica.mqtt.core.server.auth.IMqttServerAuthHandler
import org.dromara.mica.mqtt.core.server.auth.IMqttServerPublishPermission
import org.dromara.mica.mqtt.core.server.auth.IMqttServerSubscribeValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Configuration(proxyBeanMethods = false)
class MqttServerCustomizerConfiguration {

    private val MQTT_LISTEN_PORT = 1883
    private val MQTT_SUPERCLIENT_ID = OtherUtils.givePropsValue("mqtt_super_client");
    private val WEB_SERVER_URL = OtherUtils.givePropsValue("web_server_url");
    private val HTTP_PUBLIC_KEY = OtherUtils.givePropsValue("publickey");

    @Bean
    fun mqttServerCustomizer() = MqttServerCustomizer { creator ->
        println("----------------MqttServerCustomizer-----------------")
        // 自定义 MQTT 服务器配置，覆盖 yml 中的设置
//        creator.enableMqtt(MQTT_LISTEN_PORT)
        creator.readBufferSize(8192)
            //  最大包体长度
//  .maxBytesInMessage(1024 * 100)
//  mqtt 3.1 协议会校验 clientId 长度。
//  .maxClientIdLength(64)
//            .messageListener ({ context, clientId, topic, qos, message ->
//                println("clientId:$clientId payload:${message.payload()} topic:$topic qos:$qos message:$message")
//            })
            .statEnable(true)
            .authHandler(authHandler)
            .subscribeValidator(subscribeValidator)
            .publishPermission(publishPermission)
    }

    val authHandler = object : IMqttServerAuthHandler {
        override fun authenticate(
            context: ChannelContext,
            uniqueId: String,
            clientId: String,
            username: String,
            password: String
        ): Boolean {
            return doAuthCheck(clientId, username, password, context)
        }
    }

    // 订阅主题权限校验器
    val subscribeValidator = object : IMqttServerSubscribeValidator {
        override fun isValid(
            context: ChannelContext,
            clientId: String,
            topicFilter: String,
            qoS: MqttQoS,
        ): Boolean {
            return doSubscribeCheck(clientId, topicFilter)
        }
    }

    // 发布消息权限校验器
    val publishPermission = object : IMqttServerPublishPermission {
        override fun hasPermission(
            context: ChannelContext,
            clientId: String,
            topic: String,
            qoS: MqttQoS,
            isRetain: Boolean,
        ): Boolean {
            return doPublishCheck(clientId, topic)
        }
    }

    /**
     * 连接鉴权：黑名单、IP网段、账号密码三层校验
     */
    private fun doAuthCheck(
        clientId: String,
        username: String,
        password: String,
        ctx: ChannelContext
    ): Boolean {
        // 1. 黑名单拦截
//        if (BLOCK_CLIENT_ID_SET.contains(clientId)) {
//            println("拦截黑名单设备，ClientId：$clientId")
//            return false
//        }

//         2. IP白名单校验
//        val socketAddress = ctx.channel().remoteAddress() as InetSocketAddress
        val clientIp = ctx.clientNode.ip
//        var ipPass = false
//        val ipPrefixArray = arrayOfNulls<String>(ALLOW_IP_PREFIX_SET.size)
//        ALLOW_IP_PREFIX_SET.toArray(ipPrefixArray)
//        var index = 0
//        while (index < ipPrefixArray.size) {
//            val prefix = ipPrefixArray[index] ?: ""
//            if (clientIp.startsWith(prefix)) {
//                ipPass = true
//                break
//            }
//            index++
//        }
//        if (!ipPass) {
//            println("非法IP拦截，ClientId：$clientId，IP：$clientIp")
//            return false
//        }

        // 3. 账号密码校验
        if (!checkUserPassword(clientId,username,password)) {
            println("账号密码错误，ClientId：$clientId，输入用户名：$username")
            return false
        }

        println("设备认证通过，ClientId：$clientId，IP：$clientIp")
        return true
    }

    /**
     * 订阅ACL：仅允许 device/{clientId}/#
     */
    private fun doSubscribeCheck(clientId: String, topicFilter: String): Boolean {
        if (clientId== MQTT_SUPERCLIENT_ID) {
            return true;
        }
        val allowPrefix = "device/$clientId/"
        val result = topicFilter.startsWith(allowPrefix)
        if (!result) {
            println("禁止订阅非法主题，ClientId：$clientId，订阅主题：$topicFilter")
        }
        return result
    }

    /**
     * 发布ACL
     */
    private fun doPublishCheck(clientId: String, topic: String): Boolean {
        if (clientId== MQTT_SUPERCLIENT_ID) {
            return true;
        }
        val result = topic in arrayOf("device/$clientId/telemetry","device/$clientId/reply","device/$clientId/status")
        if (!result) {
            println("禁止发布非法主题，ClientId：$clientId，发布主题：$topic")
        }
        return result
    }

    private fun checkUserPassword(clientId: String,username: String,password: String): Boolean {
        val client = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(5)) // 关键：设置超时防止阻塞
            .build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${WEB_SERVER_URL}/other/test")) // 替换为你的实际地址
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                SecureUtil.des(HTTP_PUBLIC_KEY?.toByteArray()).encryptHex(JSON.toJSONString(
                    mapOf(
                        "deviceId" to clientId,
                        "userName" to username,
                        "password" to password
                    )
                )))
            )
            .build()
        return try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            // 假设 200 表示鉴权成功
//            response.statusCode() == 200
            println("-----------------${response.body()}----------------------------");
            JSON.parseObject(response.body())["status"] == "OK"
        } catch (e: Exception) {
            // 记录日志
            false
        }
    }
}