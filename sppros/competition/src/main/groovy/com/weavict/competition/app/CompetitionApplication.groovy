package com.weavict.competition.app

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

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
//		docker build -t cpt:v1.0.0 .
		new CompetitionApplication().configure(new SpringApplicationBuilder(CompetitionApplication.class)).run(args);
	}
}

@Configuration
class SpringBootConfig
{
	@Bean
	ServletWebServerFactory servletContainer()
	{
		TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
//		tomcatServletWebServerFactory.setPort(8091);
		TomcatProtocolHandlerCustomizer customizer = protocolHandler -> {
			ThreadFactory virtualThreadFactory = Thread.ofVirtual()
					.name("jersey-vt-", 0)
					.factory();
			protocolHandler.setExecutor(Executors.newThreadPerTaskExecutor(virtualThreadFactory));
		};
		tomcatServletWebServerFactory.setTomcatProtocolHandlerCustomizers(List.of(customizer));
		return tomcatServletWebServerFactory;

//		JettyServletWebServerFactory jettyServletWebServerFactory = new JettyServletWebServerFactory();
//		jettyServletWebServerFactory.setPort(8091);
//		return jettyServletWebServerFactory;

//		UndertowServletWebServerFactory undertowServletWebServerFactory = new UndertowServletWebServerFactory();
//		undertowServletWebServerFactory.setPort(8091);
//		return undertowServletWebServerFactory;
	}
}

//@Configuration
//class WebConfig implements WebMvcConfigurer {
//
//	@Override
//	void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**") // 对所有路径应用CORS配置
//				.allowedOrigins("http://localhost:5173","https://localhost:5173","https://192.168.64.1:5173","https://image.arkydesign.cn") // 允许的源
//				.allowedOriginPatterns("*")
//				.allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS") // 允许的方法
//				.allowedHeaders("*") // 允许的头部
//				.allowCredentials(true) // 是否发送cookies
//				.maxAge(3600) // 预检请求的有效期（秒）
//				.exposedHeaders("*");
//	}
//}

@Configuration
class GlobalCorsFilter {

	@Bean
	CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		//支持域
//		config.addAllowedOriginPattern("*");
        config.addAllowedOrigin("http://localhost");
		config.addAllowedOrigin("http://localhost:5173");
		config.addAllowedOrigin("https://www.ivac-hub.com");
		config.addAllowedOrigin("https://ivac-hub.com");
		config.addAllowedOrigin("https://test.arkydesign.cn");
		config.addAllowedOrigin("http://test.arkydesign.cn");
		//是否发送Cookie
		config.setAllowCredentials(true);
		//支持请求方式
		config.addAllowedMethod("*");
		//允许的原始请求头部信息
		config.addAllowedHeader("*");
		//暴露的头部信息
		config.addExposedHeader("*");

		//添加地址映射
		UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
		corsConfigurationSource.registerCorsConfiguration("/**", config);

		return new CorsFilter(corsConfigurationSource);
	}
}


/*
docker network connect dockercompose_default 容器名

docker build -t cptapp .

docker run -d --name cptapp --network dockercompose_default -p 8091:8091 -e SPRING_DATASOURCE_PRIMARY_URL=jdbc:postgresql://pgsql:5432/cptdb -e SPRING_DATASOURCE_CAMUNDA_URL=jdbc:postgresql://pgsql:5432/camunda -e SPRING_DATASOURCE_PRIMARY_USERNAME=juser -e SPRING_DATASOURCE_PRIMARY_PASSWORD=weav2880com -e SPRING_DATASOURCE_CAMUNDA_USERNAME=juser -e SPRING_DATASOURCE_CAMUNDA_PASSWORD=weav2880com -e SPRING_DATA_REDIS_HOST=redis -e SPRING_DATA_REDIS_PORT=6379 -e SPRING_DATA_REDIS_PASSWORD=Weav2880com -e SPRING_DATA_REDIS_DATABASE=0 --restart=always cptapp


==============================================================
docker save -o cptapp.tar cptapp

docker load -i /root/cptapp.tar





============================================================================================
# ===== 第一阶段：构建 =====
FROM maven:3.9-eclipse-temurin-22 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# ===== 第二阶段：提取分层 =====
FROM eclipse-temurin:22-jdk-jammy AS extractor
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ===== 第三阶段：运行 =====
FROM eclipse-temurin:22-jre-jammy
WORKDIR /app
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "com.example.CptApplication"]




<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <layers>
            <enabled>true</enabled>
        </layers>
    </configuration>
</plugin>
 */
