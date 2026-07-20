package com.weavict.mqttspserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = ["com.weavict"])
class MqttSpServerApplication

fun main(args: Array<String>) {
    runApplication<MqttSpServerApplication>(*args)
}
