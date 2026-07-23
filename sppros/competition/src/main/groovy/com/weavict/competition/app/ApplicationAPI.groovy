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
import org.apache.juli.FileHandler
import org.glassfish.jersey.CommonProperties
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.servlet.ServletContainer
import org.glassfish.jersey.servlet.ServletProperties
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component;
import org.springframework.web.filter.RequestContextFilter
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory;


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
//        register(SseSource.class);
//        register(NotificationResource.class);
ß
//        register(JerseyFeature.class);

        property("jersey.config.server.response.setStatusOverSendError",true);
        property(ServletProperties.FILTER_FORWARD_ON_404, true);

//        property(CommonProperties.USE_VIRTUAL_THREADS,true);
//        property(
//                CommonProperties.THREAD_FACTORY,
//                Thread.ofVirtual()
//                        .name("jersey-", 1)
//                        .factory()
//        );

//        setProperties会把property()方式设置的清空
//        setProperties(["jersey.config.server.response.setStatusOverSendError":true]);
    }

//    @Bean
//    ServletRegistrationBean<ServletContainer> jerseyServletRegistration() {
//        ServletRegistrationBean<ServletContainer> registration =
//                new ServletRegistrationBean(new ServletContainer(new ApplicationAPI()), "/r/*");
//
//        // 开启 Servlet 异步支持（替代不存在的 yml 配置）
//        registration.setAsyncSupported(true);
//        // 关联上面的 JerseyConfig
//        registration.addInitParameter(
//                "jakarta.ws.rs.Application",
//                ApplicationAPI.class.getName()
//        );
//
//        return registration;
//    }

}