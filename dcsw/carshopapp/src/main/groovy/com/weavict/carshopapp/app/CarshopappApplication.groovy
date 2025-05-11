package com.weavict.carshopapp.app

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
@ComponentScan(basePackages = ["com.weavict.carshopapp"])
@EntityScan(basePackages = ["com.weavict.carshopapp.entity"])
@EnableProcessApplication
class CarshopappApplication extends SpringBootServletInitializer
{

    static void main(String[] args)
    {
//        SpringApplication.run(CarshopappApplication, args);
        new CarshopappApplication().configure(new SpringApplicationBuilder(CarshopappApplication.class)).run(args);
    }

}
