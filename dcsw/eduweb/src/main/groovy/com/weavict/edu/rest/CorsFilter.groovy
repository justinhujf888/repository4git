package com.weavict.edu.rest

import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.URLUtil
import cn.hutool.crypto.SecureUtil
import com.weavict.website.common.OtherUtils
import org.glassfish.jersey.server.ContainerRequest
import org.glassfish.jersey.server.ContainerResponse

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.ext.Provider
import jakarta.ws.rs.ext.ReaderInterceptor
import jakarta.ws.rs.ext.ReaderInterceptorContext
import jakarta.ws.rs.ext.WriterInterceptor
import jakarta.ws.rs.ext.WriterInterceptorContext


/**
 * Created by Justin on 2018/6/10.
 */

class AuthorizationFilter implements ContainerRequestFilter
{

    @Override
    void filter(ContainerRequestContext requestContext) throws IOException
    {
        try
        {

        }
        catch (Exception e)
        {

        }
    }

}


@Provider
class RequestServerReaderInterceptor implements ReaderInterceptor
{

    @Override
    Object aroundReadFrom(ReaderInterceptorContext context)
            throws IOException, WebApplicationException
    {
        try
        {
            if ("true" in context.headers.self)
            {
                String ss = IoUtil.read(context.getInputStream());
                context.setInputStream(IoUtil.toUtf8Stream(URLUtil.decode(SecureUtil.des(OtherUtils.givePropsValue("publickey").getBytes()).decryptStr(ss))));
                return context.proceed();
            }
            return context.proceed();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return """{"status":"FA_SIGN"}""";
        }
    }
}

@Provider
class RequestClientWriterInterceptor implements WriterInterceptor
{

    @Override
    void aroundWriteTo(WriterInterceptorContext context)
            throws IOException, WebApplicationException
    {
        println "WriterInterceptorContext";
    }
}




class CorsFilter implements ContainerResponseFilter
{
    ContainerResponse filter(ContainerRequest creq, ContainerResponse cres) {
        cres.getHeaders().add("Access-Control-Allow-Origin", "*");
        /**
         * 允许的Header值，不支持通配符
         */
        cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        cres.getHeaders().add("Access-Control-Allow-Credentials", "true");

        /**
         * 即使只用其中几种，header和options是不能删除的，因为浏览器通过options请求来获取服务的跨域策略
         */
        cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        /**
         * CORS策略的缓存时间
         */
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");

        //可以通过 throw new WebApplicationException(Status.UNAUTHORIZED); 来中断请求

        return cres;
    }

    void filter(ContainerRequestContext request, ContainerResponseContext cres) throws IOException {
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {//浏览器会先通过options请求来确认服务器是否可以正常访问，此时应放行
            cres.setStatus(HttpServletResponse.SC_OK);
        }
        cres.getHeaders().add("Access-Control-Allow-Origin", "*");

        cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        cres.getHeaders().add("Access-Control-Allow-Credentials", "true");

        cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        /**
         * CORS策略的缓存时间
         */
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");

    }
}
