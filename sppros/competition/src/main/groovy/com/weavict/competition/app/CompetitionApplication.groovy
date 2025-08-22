package com.weavict.competition

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = ["com.weavict"])
@EntityScan(basePackages = ["com.weavict.competition.entity"])
@EnableProcessApplication
@EnableCaching
class CompetitionApplication extends SpringBootServletInitializer {

	static void main(String[] args) {
//		SpringApplication.run(CompetitionApplication, args);
		new CompetitionApplication().configure(new SpringApplicationBuilder(CompetitionApplication.class)).run(args);
	}

}
