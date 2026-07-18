package com.weavict.light.module

import com.alibaba.fastjson2.JSON
import org.dromara.mica.mqtt.codec.MqttQoS
import org.dromara.mica.mqtt.codec.message.MqttMessage
import org.dromara.mica.mqtt.codec.message.MqttPublishMessage
import org.dromara.mica.mqtt.core.annotation.MqttClientSubscribe
import org.dromara.mica.mqtt.core.client.MqttClient
import org.dromara.mica.mqtt.core.client.MqttClientCreator
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate
import org.dromara.mica.mqtt.spring.client.event.MqttConnectedEvent
import org.dromara.mica.mqtt.spring.client.event.MqttDisconnectEvent
import org.dromara.mica.mqtt.spring.server.MqttServerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
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
    private MqttClientCreator mqttClientCreator0;

    @Autowired
    @Qualifier(MqttClientTemplate.DEFAULT_CLIENT_TEMPLATE_BEAN)
    MqttClientTemplate mqttClientTemplate0;

    @EventListener
    void onConnected(MqttConnectedEvent event) {
        println "MqttConnectedEvent:${event}";
    }

    @EventListener
    void onDisconnect(MqttDisconnectEvent event) {
        // 离线时更新重连时的密码，适用于类似阿里云 mqtt clientId 连接带时间戳的方式
        println "MqttDisconnectEvent:${event}";
    }

    @MqttClientSubscribe(value="device/\${deviceId}/cmd",qos = MqttQoS.QOS0)
    void receiveDeviceDataCmd0(String topic, MqttPublishMessage message, Object data)
    {
        println "---------------client begin----------------";
        println topic;
        println message.dump();
        println data;
        println "---------------client end------------------";
    }

    @MqttClientSubscribe(value="device/\${deviceId}/telemetry",qos = MqttQoS.QOS0)
    void receiveDeviceDataTelemetry0(String topic, MqttPublishMessage message, Object data)
    {
        println "---------------client begin----------------";
        println topic;
        println message.dump();
        println data;
        mqttClientTemplate0.publish("device/mqttx_a8e885bc/cmd", JSON.toJSON([token:System.currentTimeMillis()]), MqttQoS.QOS0);
        println "---------------client end------------------";
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
