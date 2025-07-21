package com.weavict.competition.rest

import com.weavict.common.util.MathUtil
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.glassfish.jersey.media.sse.EventOutput
import org.glassfish.jersey.media.sse.OutboundEvent
import org.glassfish.jersey.media.sse.SseBroadcaster
import org.glassfish.jersey.media.sse.SseFeature

@Path("/see-events")
class SseSource
{
    EventOutput eventOutput = new EventOutput();
    OutboundEvent.Builder eventBuilder;
    OutboundEvent event;

    /**
     * 提供 SSE 事件输出通道的资源方法
     * @return eventOutput
     */
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    EventOutput getServerSentEvents()
    {
        eventBuilder = new OutboundEvent.Builder();
        eventBuilder.id(MathUtil.getPNewId());
        eventBuilder.name("message");
        eventBuilder.data(String.class,"""current time:${MathUtil.getPNewId()}""".toString());
        event = eventBuilder.build();
        try
        {
            eventOutput.write(event);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            eventOutput.close();
            return eventOutput;
        }

//        new Thread(() -> {
//            for (int i = 0; i < 10; i++) {
//                // ... code that waits 1 second
//                final OutboundSseEvent event = sse.newEventBuilder()
//                        .name("message")
//                        .data(String.class, "Hello world " + i + "!")
//                        .build();
//                eventSink.send(event);
//            }
//        }).start();
    }
}

@Singleton
@Path("/notifications")
class NotificationResource
{
    private static SseBroadcaster broadcaster = new SseBroadcaster();

    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    static EventOutput subscribe()
    {
        final EventOutput eventOutput = new EventOutput();
        broadcaster.add(eventOutput);
        return eventOutput;
    }

    static void broadcast(String json)
    {
        OutboundEvent.Builder builder = new OutboundEvent.Builder();
        OutboundEvent event = builder.name("notifications")
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(String.class, json)
                .build();
        broadcaster.broadcast(event);
    }
}
