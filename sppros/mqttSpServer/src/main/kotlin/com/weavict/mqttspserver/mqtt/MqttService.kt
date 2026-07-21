package com.weavict.mqttspserver.mqtt

import net.dreamlu.mica.net.core.ChannelContext
import org.dromara.mica.mqtt.codec.message.MqttPublishMessage
import org.dromara.mica.mqtt.core.annotation.MqttServerFunction
import org.dromara.mica.mqtt.spring.server.event.MqttClientOfflineEvent
import org.dromara.mica.mqtt.spring.server.event.MqttClientOnlineEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class MqttService {
    @EventListener
    fun onLine(event: MqttClientOnlineEvent)
    {
        println(event)
    }

    @EventListener
    fun offLine(event: MqttClientOfflineEvent)
    {
        println(event)
    }

    @MqttServerFunction($$"device/${deviceId}/cmd")
    fun rnDeviceCmdDatas(context:ChannelContext, topic:String, publishMessage: MqttPublishMessage, data: Any)
    {
        println("---------------server begin----------------")
        println(context)
        println(topic)
        println(publishMessage)
        println(data)
        println("---------------server end-----------------")
//        mqttServerTemplate.publish("000001",topic,"Hello World");
    }

    @MqttServerFunction($$"device/${deviceId}/reply")
    fun rnDeviceReplyDatas(context:ChannelContext, topic:String, publishMessage: MqttPublishMessage, data: Any)
    {
        println("---------------server begin----------------")
        println(context)
        println(topic)
        println(publishMessage)
        println(data)
        println("---------------server end-----------------")
//        mqttServerTemplate.publish("000001",topic,"Hello World");
    }
}