package com.weavict.light.app

import com.weavict.light.rest.DeviceRest
import com.weavict.light.rest.OtherRest
import com.weavict.light.rest.RequestClientWriterInterceptor
import com.weavict.light.rest.RequestServerReaderInterceptor

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

        register(DeviceRest.class);
        register(OtherRest.class);

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

class CorsFilter implements ContainerResponseFilter {
    void filter(ContainerRequestContext request, ContainerResponseContext c) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            //浏览器会先通过options请求来确认服务器是否可以正常访问，此时应放行
            c.setStatus(HttpServletResponse.SC_OK);
        }
        c.getHeaders().add("Access-Control-Allow-Origin", "*");

        c.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        c.getHeaders().add("Access-Control-Allow-Credentials", "true");

        c.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // CORS策略的缓存时间
        c.getHeaders().add("Access-Control-Max-Age", "1209600");
        println "=====================begin==========================";
        println request.requestUri;
        println request.httpMethod;
        for(def h in request.headers)
        {
            println "${h.key}: ${h.value}";
        }
        println "=====================end==========================";
    }

}


