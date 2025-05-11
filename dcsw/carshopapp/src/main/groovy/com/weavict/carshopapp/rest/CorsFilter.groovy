package com.weavict.carshopapp.rest

import com.weavict.website.common.OtherUtils
import com.yicker.utility.RSAEncrypt

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.Response

/**
 * Created by Justin on 2018/6/10.
 */


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
