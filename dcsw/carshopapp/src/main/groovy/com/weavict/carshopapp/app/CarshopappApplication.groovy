package com.weavict.carshopapp.app

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

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

@Configuration
class SpringBootConfig
{
    @Bean
    ServletWebServerFactory servletContainer()
    {
//		TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
//		tomcatServletWebServerFactory.setPort(8091);
//		return tomcatServletWebServerFactory;

//		JettyServletWebServerFactory jettyServletWebServerFactory = new JettyServletWebServerFactory();
//		jettyServletWebServerFactory.setPort(8091);
//		return jettyServletWebServerFactory;

        UndertowServletWebServerFactory undertowServletWebServerFactory = new UndertowServletWebServerFactory();
        undertowServletWebServerFactory.setPort(8091);
        return undertowServletWebServerFactory;
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
        config.addAllowedOrigin("http://localhost:5173");
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