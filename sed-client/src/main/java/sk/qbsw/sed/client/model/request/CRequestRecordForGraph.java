package sk.qbsw.sed.client.model.request;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Model for client request record for graph
 * 
 */
@SuppressWarnings("serial")
public class CRequestRecordForGraph implements Serializable {
	
	private Long id;
	private String typeDescription;
	private Long ownerId;
	private String ownerName;
	private String ownerSurname;
	private boolean fullday;
	private Calendar dateFrom;
	private Calendar dateTo;
	private Long statusId;
	private String statusDescription;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerSurname() {
		return ownerSurname;
	}

	public void setOwnerSurname(String ownerSurname) {
		this.ownerSurname = ownerSurname;
	}

	public boolean isFullday() {
		return fullday;
	}

	public void setFullday(boolean fullday) {
		this.fullday = fullday;
	}

	public Calendar getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Calendar dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Calendar getDateTo() {
		return dateTo;
	}

	public void setDateTo(Calendar dateTo) {
		this.dateTo = dateTo;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public boolean getHalfday() {
		return !fullday;
	}
}
