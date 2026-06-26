package com.weavict.competition.app

import com.weavict.competition.rest.CorsFilter
import com.weavict.competition.rest.NotificationResource
import com.weavict.competition.rest.OtherRest
import com.weavict.competition.rest.RequestClientWriterInterceptor
import com.weavict.competition.rest.RequestServerReaderInterceptor
import com.weavict.competition.rest.SseSource
import com.weavict.competition.rest.SystemRest
import com.weavict.competition.rest.UserRest
import com.weavict.competition.rest.WorkRest
import com.weavict.competition.rest.WxRest
import jakarta.annotation.PostConstruct
import jakarta.ws.rs.ApplicationPath
import org.glassfish.jersey.CommonProperties
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.servlet.ServletContainer
import org.glassfish.jersey.servlet.ServletProperties
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component;
import org.springframework.web.filter.RequestContextFilter
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;


/**
 * Created by Justin on 2018/6/10.
 */
@Component
@Configuration
@ApplicationPath("/r")
@Primary
class ApplicationAPI extends ResourceConfig
{
    ApplicationAPI() {
        register(LoggingFeature.class);
        register(RequestContextFilter.class);
        register(JacksonJsonProvider.class);
        register(CorsFilter.class);

        register(RequestClientWriterInterceptor.class);
        register(RequestServerReaderInterceptor.class);

        register(SystemRest.class);
        register(UserRest.class);
        register(WorkRest.class);
//        register(WxRest.class);
        register(OtherRest.class);
        register(SseSource.class);
        register(NotificationResource.class);

//        register(JerseyFeature.class);

        property(ServletProperties.FILTER_FORWARD_ON_404, true);
        // 1. 开启 Jersey 内部异步执行（核心，缺少这条虚拟线程完全不生效）
        property(CommonProperties.USE_VIRTUAL_THREADS,true);
        property(
                CommonProperties.THREAD_FACTORY,
                Thread.ofVirtual()
                        .name("jersey-vt-", 1)
                        .factory()
        );
        setProperties(["jersey.config.server.response.setStatusOverSendError":true])

    }

    @Bean
    ServletRegistrationBean<ServletContainer> jerseyServletRegistration() {
        ServletRegistrationBean<ServletContainer> registration =
                new ServletRegistrationBean(new ServletContainer(new ApplicationAPI()), "/r/*");

        // 开启 Servlet 异步支持（替代不存在的 yml 配置）
        registration.setAsyncSupported(true);
        // 关联上面的 JerseyConfig
        registration.addInitParameter(
                "jakarta.ws.rs.Application",
                ApplicationAPI.class.getName()
        );

        return registration;
    }
}

//@Configuration
//class ConfigBean {
//    @Bean
//    ServletRegistrationBean jerseyServlet() {
//        //手动注册servlet
//        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new ServletContainer(), "/r/*");
//        registrationBean.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS,ApplicationAPI.class.getName());
//        return registrationBean;
//    }
//}


//@Component
class JerseyVtConfig extends ResourceConfig {

    JerseyVtConfig() {
        // 扫描JAX-RS接口包
        packages("com.weavict.competition.rest");

        // 【核心】开启Jersey内部虚拟线程执行器
        property(CommonProperties.USE_VIRTUAL_THREADS, true);

        // 自定义虚拟线程命名工厂，方便日志排查
        property(
                CommonProperties.THREAD_FACTORY,
                Thread.ofVirtual()
                        .name("jersey-vt-", 1)
                        .factory()
        );
    }

    @PostConstruct
    void printConfig() {
        println "Jersey 虚拟线程已启用";
    }
}

/*
SpringBoot3 + Jersey3.1.7 + JDK21 虚拟线程完整最小工程
1 pom.xml 完整依赖
xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.4</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>${java.version}</maven.compiler.source>
        <maven.compiler.target>${java.version}</maven.compiler.target>
        <jersey.version>3.1.7</jersey.version>
    </properties>

    <dependencies>
        <!-- SpringBoot Jersey 起步 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jersey</artifactId>
        </dependency>
        <!-- 强制升级 Jersey 至支持虚拟线程版本 -->
        <dependency>
            <groupId>org.glassfish.jersey.core</groupId>
            <artifactId>jersey-server</artifactId>
            <version>${jersey.version}</version>
        </dependency>
        <!-- Web容器内置Tomcat，无需额外引入 -->
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
2 application.yml
yaml
spring:
  # 开启容器层虚拟线程
  threads:
    virtual:
      enabled: true
server:
  port: 8080
3 Jersey 核心配置 JerseyVtConfig.java
java
运行
package com.example.demo.config;

import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyVtConfig extends ResourceConfig {

    public JerseyVtConfig() {
        // 扫描JAX-RS接口所在包，改成你自己的包
        packages("com.example.demo.resource");

        // 开启Jersey虚拟线程调度
        property(CommonProperties.USE_VIRTUAL_THREADS, true);

        // 自定义虚拟线程名称，方便日志区分
        property(
                CommonProperties.THREAD_FACTORY,
                Thread.ofVirtual()
                        .name("jersey-vt-", 1)
                        .factory()
        );
    }
}
4 Servlet 注册配置 JerseyServletRegister.java
java
运行
package com.example.demo.config;

import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyServletRegister {

    @Bean
    public ServletRegistrationBean<ServletContainer> jerseyServlet() {
        ServletRegistrationBean<ServletContainer> registration =
                new ServletRegistrationBean<>(new ServletContainer(), "/*");

        // 关键：开启Servlet异步，否则Jersey虚拟线程不生效
        registration.setAsyncSupported(true);

        // 绑定上面的Jersey配置类
        registration.addInitParameter(
                "jakarta.ws.rs.Application",
                JerseyVtConfig.class.getName()
        );
        return registration;
    }
}
5 测试接口 VtTestResource.java
java
运行
package com.example.demo.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;
import java.util.Map;

@Path("/vt/test")
@Component
public class VtTestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> checkThread() {
        Thread thread = Thread.currentThread();
        return Map.of(
                "threadName", thread.getName(),
                "isVirtual", thread.isVirtual(),
                "isTomcatWorker", thread.getName().startsWith("http-nio")
        );
    }
}
6 启动类 DemoApplication.java
java
运行
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
访问验证
地址：http://127.0.0.1:8080/vt/test
成功返回示例：
json
{
    "threadName": "jersey-vt-1",
    "isVirtual": true,
    "isTomcatWorker": false
}
关键说明 & 避坑
缺一不可三要素
JDK21 + Jersey≥3.1.7
registration.setAsyncSupported(true) 开启 Servlet 异步
CommonProperties.USE_VIRTUAL_THREADS=true
若返回 http-nio 开头线程且 isVirtual=false
检查是否手动注册了 Servlet 并开启asyncSupported
确认 Jersey 版本没有被 SpringBoot 依赖管理降级
若返回 http-nio 且 isVirtual=true
代表只启用了 Tomcat 容器虚拟线程，Jersey 未开启异步调度，缺少setAsyncSupported(true)。
ThreadLocal 风险
虚拟线程会切换载体线程，接口、过滤器不要自定义 ThreadLocal 存上下文，优先ScopedValue。







如何在Spring Boot中配置Servlet异步？
除了上述方法，还有其他方式开启虚拟线程吗？
虚拟线程和普通线程有什么区别？
 */


