package com.weavict.light.rest

import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.URLUtil
import cn.hutool.crypto.SecureUtil
import cn.hutool.crypto.asymmetric.RSA
import com.weavict.website.common.OtherUtils
import com.yicker.utility.RSAEncrypt
import jakarta.servlet.http.HttpServletResponse
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.*

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
            String ss = IoUtil.read(context.getInputStream());
            if (!(ss in [null,"","{}"]))
            {
                context.setInputStream(IoUtil.toUtf8Stream(URLUtil.decode(SecureUtil.des(OtherUtils.givePropsValue("publickey").getBytes()).decryptStr(ss))));
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
        context.proceed();
    }
}


class CorsFilter implements ContainerResponseFilter {
    void filter(ContainerRequestContext request, ContainerResponseContext c) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            //浏览器会先通过options请求来确认服务器是否可以正常访问，此时应放行
            c.setStatus(HttpServletResponse.SC_OK);
        }
        if (request.getHeaderString("origin") in ["http://localhost:5173","https://localhost:5173","https://192.168.1.46:5173"])
        {
            c.getHeaders().add("Access-Control-Allow-Origin", request.getHeaderString("origin"));
        }
//        c.getHeaders().add("Access-Control-Allow-Origin", "*");

        c.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorizationk, self");
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
