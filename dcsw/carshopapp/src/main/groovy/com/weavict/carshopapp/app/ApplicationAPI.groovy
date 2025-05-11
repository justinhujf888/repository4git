package com.weavict.carshopapp.app

import jakarta.servlet.http.HttpServletResponse
import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider
import com.weavict.carshopapp.rest.JerseyFeature
import com.weavict.carshopapp.rest.OrderRest
import com.weavict.carshopapp.rest.OtherRest
import com.weavict.carshopapp.rest.PayRest
import com.weavict.carshopapp.rest.ShopRest
import com.weavict.carshopapp.rest.UserRest
import com.weavict.carshopapp.rest.WxRest
import org.glassfish.jersey.logging.LoggingFeature

//import org.glassfish.jersey.filter.LoggingFilter
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.web.filter.RequestContextFilter

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
// 加载resources
        register(LoggingFeature.class);
        register(RequestContextFilter.class);
        register(JacksonJsonProvider.class);
        register(CorsFilter.class);
//        register(CorsFilter.class);
        register(ShopRest.class);
        register(UserRest.class);
        register(OrderRest.class);
        register(WxRest.class);
        register(OtherRest.class);
        register(PayRest.class);
        register(JerseyFeature.class);
// 注册数据转换器
        register(JacksonJsonProvider.class);

// 注册日志
//        register(LoggingFilter.class);

        property(ServletProperties.FILTER_FORWARD_ON_404, true);
        setProperties(["jersey.config.server.response.setStatusOverSendError":true]);
    }

}

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
        println "\u001B[31m=====================begin==========================\u001B[37m";
        println "\u001B[31m${request.requestUri}\u001B[37m";
        println "\u001B[31m${request.httpMethod}\u001B[37m";
        for(def h in request.headers)
        {
            println "\u001B[31m${h.key}: ${h.value}\u001B[37m";
        }
        println "\u001B[31m=====================end==========================\u001B[37m";
        println "\u001B[33m";
    }

}