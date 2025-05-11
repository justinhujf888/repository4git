package com.weavict.shop.app

import org.camunda.bpm.engine.delegate.JavaDelegate
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
@ComponentScan(basePackages = ["com.weavict.shop"])
@EntityScan(basePackages = ["com.weavict.shop.entity"])
@EnableProcessApplication
class SpApplication extends SpringBootServletInitializer
{

    static void main(String[] args)
    {
//        SpringApplication.run(SpApplication, args);
        new SpApplication().configure(new SpringApplicationBuilder(SpApplication.class)).run(args);
    }

}