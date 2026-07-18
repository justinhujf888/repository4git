package com.weavict.light.module

import org.dromara.mica.mqtt.codec.MqttQoS
import org.dromara.mica.mqtt.codec.message.MqttMessage
import org.dromara.mica.mqtt.codec.message.MqttPublishMessage
import org.dromara.mica.mqtt.core.annotation.MqttClientSubscribe
import org.dromara.mica.mqtt.core.client.MqttClient
import org.dromara.mica.mqtt.core.client.MqttClientCreator
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate
import org.dromara.mica.mqtt.spring.server.MqttServerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service

import java.nio.charset.StandardCharsets

@Configuration
class MqttClientConfiguration1 {

//    @Bean("mqttClientTemplate1")
//    MqttClientTemplate mqttClientTemplate1() {
//        MqttClientCreator mqttClientCreator = MqttClient.create()
//                .ip("broker.emqx.io").clientId("brokersuperclient");
////                .username("mica")
////                .password("mica");
//        return new MqttClientTemplate(mqttClientCreator);
//    }
}

@Service
class MqttClientService
{

    /*
    上传：device/{deviceId}/telemetry
    状态：device/{deviceId}/status
    事件：device/{deviceId}/event
    命令：device/{deviceId}/cmd
    回复：device/{deviceId}/reply
    OTA：device/{deviceId}/ota
     */

    @Autowired
    @Qualifier(MqttClientTemplate.DEFAULT_CLIENT_TEMPLATE_BEAN)
    MqttClientTemplate mqttClientTemplate;

    @MqttClientSubscribe(value="device/\${deviceId}/cmd",qos = MqttQoS.QOS0)
    void receiveDeviceData0(String topic, MqttPublishMessage message, Object data)
    {
        println "---------------client begin----------------";
        println topic;
        println message.dump();
        println data;
        println "---------------client end------------------";
//        mqttServerTemplate.publish("superclient",topic,data);
    }

//    @MqttClientSubscribe(value="device/\${deviceId}/cmd",qos = MqttQoS.QOS0,clientTemplateBean = "mqttClientTemplate1")
//    void receiveDeviceData1(String topic, MqttPublishMessage message, Object data)
//    {
//        println "---------------client begin----------------";
//        println topic;
//        println message.dump();
//        println data;
//        println "---------------client end----------------";
////        mqttServerTemplate.publish("superclient",topic,data);
//    }

//    @MqttClientSubscribe(value="device/\${deviceId}/reply",qos = MqttQoS.QOS1)
//    void receiveTest(String topic, byte[] payload)
//    {
//        println "---------------client begin----------------";
//        println topic;
//        println new String(payload, StandardCharsets.UTF_8);
//        println "---------------client end----------------";
//    }
}
