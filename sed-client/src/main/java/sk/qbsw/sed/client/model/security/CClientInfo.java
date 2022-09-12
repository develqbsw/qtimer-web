package sk.qbsw.sed.client.model.security;

import java.io.Serializable;
import java.util.List;

public class CClientInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8584928614967488632L;

	private Long clientId;

	private String clientName;

	private Boolean projectRequired;

	private Boolean activityRequired;

	private String language;

	private List<CZoneRecord> zones;

	private Boolean generateMessages;

	public Long getClientId() {
		return this.clientId;
	}

	public void setClientId(final Long clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return this.clientName;
	}

	public void setClientName(final String clientName) {
		this.clientName = clientName;
	}

	public Boolean getProjectRequired() {
		return this.projectRequired;
	}

	public void setProjectRequired(final Boolean projectRequired) {
		this.projectRequired = projectRequired;
	}

	public Boolean getActivityRequired() {
		return this.activityRequired;
	}

	public void setActivityRequired(final Boolean activityRequired) {
		this.activityRequired = activityRequired;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isQbsw() {
		return getClientName().contains("QBSW");
	}

	public List<CZoneRecord> getZones() {
		return zones;
	}

	public void setZones(List<CZoneRecord> zones) {
		this.zones = zones;
	}

	public Boolean getGenerateMessages() {
		return generateMessages;
	}

	public void setGenerateMessages(Boolean generateMessages) {
		this.generateMessages = generateMessages;
	}
}
