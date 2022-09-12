package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;
import java.util.Date;

/**
 * Client model of the CHoliday entity
 * 
 * @author rosenberg
 * @version 0.1
 * @since 1.6.2
 */
@SuppressWarnings("serial")
public class CHolidayRecord implements Serializable {
	private Long id;
	private Long clientId;
	private Date day;
	private String description;
	private Boolean active;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
