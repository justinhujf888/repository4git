package com.weavict.light.module

import org.dromara.mica.mqtt.codec.MqttQoS
import org.dromara.mica.mqtt.codec.message.MqttMessage
import org.dromara.mica.mqtt.codec.message.MqttPublishMessage
import org.dromara.mica.mqtt.core.annotation.MqttClientSubscribe
import org.dromara.mica.mqtt.spring.server.MqttServerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.nio.charset.StandardCharsets

@Service
class MqttClientService
{

    @Autowired
    MqttServerTemplate mqttServerTemplate;

    @MqttClientSubscribe(value="/device/up/\${deviceId}",qos = MqttQoS.QOS1)
    void receiveDeviceData(String topic, MqttPublishMessage message, Object data)
    {
        println "---------------client begin----------------";
        println topic;
        println message.dump();
        println data;
        println "---------------client begin----------------";
//        mqttServerTemplate.publish("superclient",topic,data);
    }

    @MqttClientSubscribe(value="/device/test",qos = MqttQoS.QOS1)
    void receiveTest(String topic, byte[] payload)
    {
        println "---------------client begin----------------";
        println topic;
        println new String(payload, StandardCharsets.UTF_8);
        println "---------------client begin----------------";
    }
}
