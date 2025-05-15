package com.weavict.light.rest

import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.URLUtil
import cn.hutool.crypto.SecureUtil
import cn.hutool.crypto.asymmetric.RSA
import com.weavict.website.common.OtherUtils
import com.yicker.utility.RSAEncrypt

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
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
        context.proceed();
    }
}


class CorsFilter implements ContainerRequestFilter
{
    static rsaPrivateKey = "";

    private String genRSAPrivateKey()
    {
        if (rsaPrivateKey in [null,""])
        {
            rsaPrivateKey = OtherUtils.givePropsValue("rsa_privateKey");
        }
        return rsaPrivateKey;
    }

    @Override
    void filter(ContainerRequestContext request)
    {
        try
        {
            if (request.getHeaderString("timestamp") in [null,""] || request.getHeaderString("createTime") in [null,""])
            {
                throw new WebApplicationException(
                        Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
            }
            else
            {
                if (RSAEncrypt.decrypt(request.getHeaderString("timestamp"), genRSAPrivateKey()).equals(request.getHeaderString("createTime")))
                {
                    return;
                }
                else
                {
                    throw new WebApplicationException(
                            Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
                }
            }
        }
        catch (Exception e)
        {
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
        }
    }
}
