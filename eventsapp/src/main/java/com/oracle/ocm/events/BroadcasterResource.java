package com.oracle.ocm.events;

import com.oracle.ocm.events.beans.EventData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.sse.SseBroadcaster;

import java.util.UUID;

@Singleton
@Path("/broadcast/events")
public class BroadcasterResource {
    private static ObjectMapper mapper = new ObjectMapper();
    private SseBroadcaster broadcaster = new SseBroadcaster();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public EventData broadcastMessage(EventData eventData) {

        String txId = UUID.randomUUID().toString();
        try{
            OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
            OutboundEvent event = eventBuilder
                    .id(txId)
                    .data(String.class, mapper.writeValueAsString(eventData) + "\n\n")
                    .mediaType(MediaType.TEXT_PLAIN_TYPE)
                    .build();

            broadcaster.broadcast(event);
            return eventData;

        }
        catch (Exception e) {
            throw new RuntimeException("Error when writing the event.", e);
        }

    }
    @GET
    @Path("/subscribe")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput listenToBroadcast() {
        final EventOutput eventOutput = new EventOutput();
        this.broadcaster.add(eventOutput);
        return eventOutput;
    }

}
