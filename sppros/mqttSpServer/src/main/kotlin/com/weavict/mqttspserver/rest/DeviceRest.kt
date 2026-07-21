package com.weavict.mqttspserver.rest

import com.weavict.mqttspserver.redis.RedisUtil
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.springframework.web.bind.annotation.RequestBody


@Path("/device")
class DeviceRest: BaseRest() {

//    @Autowired
//    private lateinit var deviceService: DeviceService
    @Inject
    var redisUtil: RedisUtil? = null

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/qyDeviceTypeList")
    fun qyDeviceTypeList(@RequestBody query: Map<String, Any?>?): String {
        return try {
            println($$"device/${deviceId}/reply")
            println(query)
            println(redisUtil?.hGet("buyer_13268990066","bean"))
            val objectMapper = buildObjectMapper()
//            val appId = query["appId"] as? String ?: ""
//            val typeId = query["typeId"] as? String ?: ""
//            val serviceId = query["serviceId"] as? String ?: ""
//            val name = query["name"] as? String ?: ""

            objectMapper.writeValueAsString(
                mapOf(
                    "status" to "OK",
                    "deviceTypeList" to fun():List<String> {return listOf("a","b")}()
                )
            )
        } catch (e: Exception) {
            processException(e)
            """{"status":"FA_ER"}"""
        }
    }
}