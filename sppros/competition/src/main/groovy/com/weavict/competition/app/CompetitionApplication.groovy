package com.weavict.competition.app

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = ["com.weavict"])
@EntityScan(basePackages = ["com.weavict.competition.entity"])
@EnableProcessApplication
@EnableCaching
class CompetitionApplication extends SpringBootServletInitializer
{
	static void main(String[] args)
	{
//		SpringApplication.run(CompetitionApplication, args);
		new CompetitionApplication().configure(new SpringApplicationBuilder(CompetitionApplication.class)).run(args);
	}
}

@Configuration
class SpringBootConfig
{
	@Bean
	ServletWebServerFactory servletContainer()
	{
//		TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
//		tomcatServletWebServerFactory.setPort(8091);
//		return tomcatServletWebServerFactory;
//
//		JettyServletWebServerFactory jettyServletWebServerFactory = new JettyServletWebServerFactory();
//		jettyServletWebServerFactory.setPort(8091);
//		return jettyServletWebServerFactory;

		UndertowServletWebServerFactory undertowServletWebServerFactory = new UndertowServletWebServerFactory();
		undertowServletWebServerFactory.setPort(8091);
		return undertowServletWebServerFactory;
	}
}