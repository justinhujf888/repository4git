package com.weavict.light.module

import org.dromara.mica.mqtt.codec.MqttQoS
import org.dromara.mica.mqtt.codec.message.MqttMessage
import org.dromara.mica.mqtt.core.annotation.MqttClientSubscribe
import org.springframework.stereotype.Service

@Service
class MqttClientService
{
    @MqttClientSubscribe(value="/device/up/{deviceId}",qos = MqttQoS.QOS1)
    void receiveDeviceData(String deviceId,MqttMessage message)
    {
        println message.dump();
    }
}
