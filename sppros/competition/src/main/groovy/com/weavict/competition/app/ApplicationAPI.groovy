package com.weavict.competition.app

import com.weavict.competition.rest.CorsFilter
import com.weavict.competition.rest.NotificationResource
import com.weavict.competition.rest.OtherRest
import com.weavict.competition.rest.RequestClientWriterInterceptor
import com.weavict.competition.rest.RequestServerReaderInterceptor
import com.weavict.competition.rest.SseSource
import com.weavict.competition.rest.WorkRest
import com.weavict.competition.rest.WxRest
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

        register(RequestClientWriterInterceptor.class);
        register(RequestServerReaderInterceptor.class);

        register(WorkRest.class);
//        register(WxRest.class);
        register(OtherRest.class);
        register(SseSource.class);
        register(NotificationResource.class);

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


