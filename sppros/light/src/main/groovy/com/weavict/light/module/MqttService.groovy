package com.weavict.light.module

import net.dreamlu.mica.net.core.ChannelContext
import org.dromara.mica.mqtt.codec.message.MqttPublishMessage
import org.dromara.mica.mqtt.core.annotation.MqttServerFunction
import org.dromara.mica.mqtt.spring.server.MqttServerTemplate
import org.dromara.mica.mqtt.spring.server.event.MqttClientOfflineEvent
import org.dromara.mica.mqtt.spring.server.event.MqttClientOnlineEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class MqttService
{
//    @Autowired
    MqttServerTemplate mqttServerTemplate;

    @EventListener
    void onLine(MqttClientOnlineEvent event)
    {
        println event.dump();
    }

    @EventListener
    void offLine(MqttClientOfflineEvent event)
    {
        println event.dump();
    }

    @MqttServerFunction("/device/up/\${deviceId}")
    void rnDeviceStatusDatas(ChannelContext context, String topic, MqttPublishMessage publishMessage, Object data)
    {
        println "---------------server begin----------------";
        println context.bsId;
        println topic;
        println publishMessage;
        println data;
        println "---------------server end-----------------";
//        mqttServerTemplate.publish("000001",topic,"Hello World");
    }

    @MqttServerFunction("/device/test")
    void rnDeviceTest(String topic, byte[] message)
    {
        println "---------------server begin----------------";
        println topic;
        println message;
        println "---------------server end-----------------";
//        mqttServerTemplate.publish("000001",topic,"Hello World");
    }
}
