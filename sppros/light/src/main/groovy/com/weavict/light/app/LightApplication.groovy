package com.weavict.light.app

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = ["com.weavict.light"])
@EntityScan(basePackages = ["com.weavict.light.entity"])
@EnableProcessApplication
class LightApplication extends SpringBootServletInitializer {

    static void main(String[] args) {
//        SpringApplication.run(LightApplication, args);
        new LightApplication().configure(new SpringApplicationBuilder(LightApplication.class)).run(args);
    }

}
