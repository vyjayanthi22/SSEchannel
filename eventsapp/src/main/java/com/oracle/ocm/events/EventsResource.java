package com.oracle.ocm.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.ocm.events.beans.*;

import javax.ws.rs.*;

import com.oracle.ocm.events.beans.EventData;
import com.oracle.ocm.events.beans.EventSource;
import com.oracle.ocm.events.beans.EventTarget;
import com.oracle.ocm.events.beans.EventType;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Path("/events-server/events")
public class EventsResource {
    private static ObjectMapper mapper = new ObjectMapper();

    public static int eventCount = 0;

    public static Set<EventTarget> registeredSubscribers = new HashSet<>();

    public static Map<EventTarget, ConcurrentLinkedQueue<EventData>> eventQueues = new HashMap<EventTarget, ConcurrentLinkedQueue<EventData>>();

    static {
        eventQueues.put(EventTarget.ALL, new ConcurrentLinkedQueue<EventData>());
        eventQueues.put(EventTarget.CAAS, new ConcurrentLinkedQueue<EventData>());
        eventQueues.put(EventTarget.WEBCLIENT, new ConcurrentLinkedQueue<EventData>());
        eventQueues.put(EventTarget.DOCS, new ConcurrentLinkedQueue<EventData>());
        eventQueues.put(EventTarget.CECAI, new ConcurrentLinkedQueue<EventData>());
    }

    @POST
    @Path("/publish")
    @Consumes("application/json")
    public void publishEvents(EventData eventData) {
        if (EventTarget.ALL.equals(eventData.getEventTarget())) {
            for (EventTarget registeredEventTarget : registeredSubscribers) {
                EventData newEventData = new EventData(eventData);
                eventQueues.get(registeredEventTarget).add(newEventData);
            }
        } else {
            eventQueues.get(eventData.getEventTarget()).add(eventData);
        }
        eventCount++;
    }

    @GET
    @Path("/{source}/subscribe")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput getServerSentEvents(@PathParam("source") String sourceName, @QueryParam("user") String userId) {
        eventCount = 0;
        EventTarget eventTarget = EventTarget.valueOf(sourceName);
        System.out.println("Subscriber Id:" + sourceName + ", Owner:" + userId);
        if (!registeredSubscribers.contains(eventTarget)) {
            registeredSubscribers.add(eventTarget);
        }

        final EventOutput eventOutput = new EventOutput();
        new Thread() {
            public void run() {
                EventData eventData = null;
                String txId = UUID.randomUUID().toString();
                if (!EventTarget.EVENTSAPP.equals(eventTarget) && eventQueues.get(eventTarget).isEmpty()) {
                    Map<String, Object> message = new HashMap<>();
                    message.put("status", "NA");
                    eventData = new EventData("Keep Alive", EventType.KEEP_ALIVE, EventSource.EVENTSAPP, eventTarget,
                            userId, message);
                    eventQueues.get(eventTarget).add(eventData);
                }
                try {
                    if (EventTarget.EVENTSAPP.equals(eventTarget)) {
                        for (EventTarget eventQueue : eventQueues.keySet()) {
                            if (!eventQueues.get(eventQueue).isEmpty()) {
                                for (int i = 0; i < eventQueues.get(eventQueue).size(); i++) {
                                    eventData = eventQueues.get(eventQueue).peek();
                                    final OutboundEvent event = new OutboundEvent.Builder()
                                            .id(txId)
                                            .data(String.class, mapper.writeValueAsString(eventData))
                                            .reconnectDelay(20000)
                                            .build();
                                    eventOutput.write(event);
                                }
                            }
                        }

                    } else {
                        for (int e = 0; e < eventQueues.get(eventTarget).size(); e++) {
                            eventData = eventQueues.get(eventTarget).poll();
                            final OutboundEvent event = new OutboundEvent.Builder()
                                    .id(txId)
                                    .data(String.class, mapper.writeValueAsString(eventData))
                                    .reconnectDelay(10000)
                                    .build();
                            eventOutput.write(event);
                        }
                    }

                }
                catch (IOException e) {
                    throw new RuntimeException("Error when writing the event.", e);
                }
                finally {
                    try {
                        eventOutput.close();
                    } catch (IOException ioClose) {
                        throw new RuntimeException("Error when closing the event output.", ioClose);
                    }
                }
            }
        }.start();

        return eventOutput;

    }

}