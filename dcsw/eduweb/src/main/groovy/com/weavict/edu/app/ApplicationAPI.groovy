package com.weavict.edu.app

import com.weavict.edu.rest.CorsFilter
import com.weavict.edu.rest.DemoClassRest
import com.weavict.edu.rest.JiaoHuRest
import com.weavict.edu.rest.NotificationResource
import com.weavict.edu.rest.OrderRest
import com.weavict.edu.rest.OtherRest
import com.weavict.edu.rest.PayRest
import com.weavict.edu.rest.RequestServerReaderInterceptor
import com.weavict.edu.rest.SchoolRest
import com.weavict.edu.rest.SseSource
import com.weavict.edu.rest.StudyRest
import com.weavict.edu.rest.TeachRest
import com.weavict.edu.rest.TransactionRest
import com.weavict.edu.rest.UserInfoRest
import com.weavict.edu.rest.UserRest
import com.weavict.edu.rest.WxRest
import jakarta.ws.rs.ApplicationPath
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletProperties
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

//        register(AuthorizationFilter.class);
        register(RequestServerReaderInterceptor.class);
//        register(RequestClientWriterInterceptor.class);

        register(UserInfoRest.class);
        register(SchoolRest.class);
        register(TeachRest.class);
        register(StudyRest.class);
//        register(RestfulHelloWorld.class);
        register(WxRest.class);
        register(PayRest.class);
        register(OtherRest.class);
        register(OrderRest.class);
        register(JiaoHuRest.class);
        register(TransactionRest.class);
        register(DemoClassRest.class);
        register(UserRest.class);

        register(SseSource.class);
        register(NotificationResource.class);

// 注册数据转换器
//        register(JacksonJsonProvider.class);

//        register(JerseyFeature.class);

        property(ServletProperties.FILTER_FORWARD_ON_404, true);
        setProperties(["jersey.config.server.response.setStatusOverSendError":true]);
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


