package sk.qbsw.sed.client.model.message;

import java.io.Serializable;
import java.util.Calendar;

public class CMessageRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	private String messageType;

	private Calendar from;

	private Calendar to;

	private String description;

	public CMessageRecord(String messageType, Calendar from, Calendar to) {
		super();
		this.messageType = messageType;
		this.from = from;
		this.to = to;
	}

	public CMessageRecord(String messageType, String description) {
		super();
		this.messageType = messageType;
		this.description = description;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public Calendar getFrom() {
		return from;
	}

	public void setFrom(Calendar from) {
		this.from = from;
	}

	public Calendar getTo() {
		return to;
	}

	public void setTo(Calendar to) {
		this.to = to;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
