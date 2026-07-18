package com.weavict.com.weavict.micamqtt.server

import com.alibaba.fastjson2.JSON
import io.netty.channel.ChannelHandlerContext
import net.dreamlu.mica.net.core.ChannelContext
import org.dromara.mica.mqtt.codec.MqttQoS
import org.dromara.mica.mqtt.core.server.MqttServer
import org.dromara.mica.mqtt.core.server.auth.IMqttServerAuthHandler
import org.dromara.mica.mqtt.core.server.auth.IMqttServerPublishPermission
import org.dromara.mica.mqtt.core.server.auth.IMqttServerSubscribeValidator
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import kotlin.collections.mapOf
import kotlin.to

object MqttStandaloneServer
{
    private const val MQTT_LISTEN_PORT = 1883
    private const val MQTT_SUPERCLIENT_ID = "superclient-weavict-justin";
    private const val WEB_SERVER_URL = "http://localhost/r";

    // 黑名单ClientId集合
    private val BLOCK_CLIENT_ID_SET = HashSet<String>()
    // 允许访问的IP前缀白名单
    private val ALLOW_IP_PREFIX_SET = HashSet<String>()

    init
    {
        BLOCK_CLIENT_ID_SET.add("hack_device_001")
        BLOCK_CLIENT_ID_SET.add("test_black_client")

        ALLOW_IP_PREFIX_SET.add("127.0.0.")
        ALLOW_IP_PREFIX_SET.add("192.168.1.")
        ALLOW_IP_PREFIX_SET.add("10.0.0.")
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

    @JvmStatic
    fun main(args: Array<String>) {
        var mqttServer = MqttServer.create()
            .enableMqtt(MQTT_LISTEN_PORT)
            .readBufferSize(8192)
            //  最大包体长度
//  .maxBytesInMessage(1024 * 100)
//  mqtt 3.1 协议会校验 clientId 长度。
//  .maxClientIdLength(64)
            .messageListener ({ context, clientId, topic, qos, message ->
                println("clientId:$clientId payload:${message.payload()} topic:$topic qos:$qos message:$message")
            })
            .statEnable(true)
            .authHandler(authHandler)
            .subscribeValidator(subscribeValidator)
            .publishPermission(publishPermission)
            .start()
//        mqttServer.schedule({
//            val message = """{"msg":"mica最牛皮 ${System.currentTimeMillis()}"}"""
//            mqttServer.publishAll("device/mqttx_a8e885bc/cmd", message.toByteArray(StandardCharsets.UTF_8))
//        },2000);
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
        if (BLOCK_CLIENT_ID_SET.contains(clientId)) {
            println("拦截黑名单设备，ClientId：$clientId")
            return false
        }

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

        if (checkUserPassword(clientId,username,password)) {
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
        if (clientId==MQTT_SUPERCLIENT_ID) {
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
        if (clientId==MQTT_SUPERCLIENT_ID) {
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
            .connectTimeout(java.time.Duration.ofSeconds(2)) // 关键：设置超时防止阻塞
            .build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$WEB_SERVER_URL/test")) // 替换为你的实际地址
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                JSON.toJSONString(
                    mapOf(
                        "deviceId" to clientId,
                        "userName" to username,
                        "password" to password
                    )
                ))
            )
            .build()
        return try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            // 假设 200 表示鉴权成功
//            response.statusCode() == 200
            JSON.parseObject(response.body())["status"] == "OK"
        } catch (e: Exception) {
            // 记录日志
            false
        }
    }
}