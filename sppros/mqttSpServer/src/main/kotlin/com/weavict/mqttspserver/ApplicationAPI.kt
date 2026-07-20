package com.weavict.mqttspserver

import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.URLUtil
import cn.hutool.crypto.SecureUtil
import com.weavict.mqttspserver.rest.DeviceRest
import com.weavict.website.common.OtherUtils
import jakarta.servlet.http.HttpServletResponse
import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.ext.*
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.spring.scope.RequestContextFilter
import org.glassfish.jersey.servlet.ServletProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.io.IOException


@Component
@Configuration
@ApplicationPath("/r")
@Primary
class ApplicationAPI : ResourceConfig()
{
    init {
        register(LoggingFeature::class.java)
        register(RequestContextFilter::class.java)
        register(JacksonJsonProvider::class.java)
        register(CorsFilter::class.java)

        register(RequestClientWriterInterceptor::class.java)
        register(RequestServerReaderInterceptor::class.java)

        register(DeviceRest::class.java)

        property("jersey.config.server.response.setStatusOverSendError",true);
        property(ServletProperties.FILTER_FORWARD_ON_404, true);
    }
}

@Provider
internal class RequestClientWriterInterceptor : WriterInterceptor {
    @Throws(IOException::class, WebApplicationException::class)
    override fun aroundWriteTo(context: WriterInterceptorContext) {
        context.proceed()
    }
}

@Provider
class RequestServerReaderInterceptor : ReaderInterceptor {

    @Throws(IOException::class, WebApplicationException::class)
    override fun aroundReadFrom(context: ReaderInterceptorContext): Any {
        return try {
            val ss: String? = IoUtil.read(context.inputStream, Charsets.UTF_8) as String?

            if (ss != null && ss.isNotBlank() && ss != "{}") {
                val publicKey = OtherUtils.givePropsValue("publickey")
                    ?: throw IllegalArgumentException("publickey is missing")
println(publicKey)
                val decrypted = SecureUtil.des(publicKey.toByteArray())
                    .decryptStr(ss)
                    ?: throw IllegalArgumentException("Decryption failed")

                val decoded = URLUtil.decode(decrypted)
                context.inputStream = IoUtil.toUtf8Stream(decoded)
            }

            context.proceed()
        } catch (e: Exception) {
            e.printStackTrace()
            """{"status":"FA_SIGN"}"""
        }
    }
}

class CorsFilter : ContainerResponseFilter {

    @Throws(IOException::class)
    override fun filter(request: ContainerRequestContext, response: ContainerResponseContext) {
        val method = request.method
        if (method.equals("OPTIONS", ignoreCase = true)) {
            response.status = HttpServletResponse.SC_OK
        }

        val origin = request.getHeaderString("origin")
        if (!origin.isNullOrBlank() && origin in allowedOrigins) {
            response.headers.add("Access-Control-Allow-Origin", origin)
        }

        response.headers.apply {
            add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, self")
            add("Access-Control-Allow-Credentials", "true")
            add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            add("Access-Control-Max-Age", "1209600")
        }

        logRequestDetails(request)
    }

    private fun logRequestDetails(request: ContainerRequestContext) {
        println("=====================begin==========================")
        println(request.uriInfo.requestUri)
        println(request.method)
        request.headers.entries.forEach { (key, value) ->
            println("$key: $value")
        }
        println("=====================end==========================")
    }

    companion object {
        private val allowedOrigins = setOf(
            "http://localhost:5173",
            "https://localhost:5173",
            "https://192.168.1.49:5173"
        )
    }
}