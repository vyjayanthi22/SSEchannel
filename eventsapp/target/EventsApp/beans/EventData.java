/**
 * 
 */
package com.oracle.ocm.events.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * @author napattan
 *
 */
public class EventData implements Cloneable{

	private String id = UUID.randomUUID().toString().toUpperCase().replace("-", "");
	@JsonProperty("name")
	private String name;

	@JsonProperty("eventType")
	private EventType eventType;

	@JsonProperty("eventSource")
	private EventSource eventSource;

	@JsonProperty("eventTarget")
	private EventTarget eventTarget;

	@JsonProperty("eventTime")
	private Date eventTime = new Date();

	@JsonProperty("eventOwner")
	private String eventOwner;

	@JsonProperty("objectIds")
	private List<String> objectIds;

	@JsonProperty("message")
	private Map<String,Object> message = new HashMap<>();

	@JsonProperty("details")
	private Map<String,Object> details ;

	@JsonProperty("details")
	public Map<String, Object> getDetails() {
		return details;
	}

	@JsonProperty("details")
	public void setDetails(Map<String, Object> details) {
		this.details = details;
	}
	@JsonCreator
	public EventData() {}

	public EventData(EventData eventData) {
		super();
		this.name = eventData.getName();
		this.eventType = eventData.getEventType();
		this.eventSource = eventData.getEventSource();
		this.eventTarget = eventData.getEventTarget();
		this.eventOwner = eventData.getEventOwner();
		this.message = eventData.getMessage();
		this.objectIds = eventData.getObjectIds();
	}

	public EventData(String name, EventType eventType, EventSource eventSource, EventTarget eventTarget,String eventOwner,
					 Map<String, Object> message) {
		super();
		this.name = name;
		this.eventType = eventType;
		this.eventSource = eventSource;
		this.eventTarget = eventTarget;
		this.eventOwner = eventOwner;
		this.message = message;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the eventSource
	 */
	public EventSource getEventSource() {
		return eventSource;
	}

	/**
	 * @param eventSource the eventSource to set
	 */
	public void setEventSource(EventSource eventSource) {
		this.eventSource = eventSource;
	}

	/**
	 * @return the eventTarget
	 */
	public EventTarget getEventTarget() {
		return eventTarget;
	}

	/**
	 * @param eventTarget the eventTarget to set
	 */
	public void setEventTarget(EventTarget eventTarget) {
		this.eventTarget = eventTarget;
	}

	/**
	 * @return the eventTime
	 */
	public Date getEventTime() {
		return eventTime;
	}

	/**
	 * @param eventTime the eventTime to set
	 */
	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	/**
	 * @return the message
	 */
	@JsonProperty("message")
	public Map<String, Object> getMessage() {
		return message;
	}

	/**
	 * @param data the message to set
	 */
	@JsonProperty("message")
	public void setMessage(Map<String, Object> message) {
		this.message = message;
	}

	public String getEventOwner() {
		return eventOwner;
	}

	public void setEventOwner(String eventOwner) {
		this.eventOwner = eventOwner;
	}

	public List<String> getObjectIds() {
		return objectIds;
	}

	public void setObjectIds(List<String> objectIds) {
		this.objectIds = objectIds;
	}


}
