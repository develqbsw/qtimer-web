package sk.qbsw.sed.client.model.request;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.common.utils.CRange;

/**
 * Model for client
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@SuppressWarnings("serial")
public class CRequestRecord implements Serializable {
	
	private Long id;
	private Long typeId;
	private String typeDescription;
	private Long ownerId;
	private String ownerName;
	private String ownerSurname;
	private String creatorName;
	private String creatorSurname;
	private String creatorPosition;

	private boolean fullday;
	// full days
	private Date dateFrom;
	private Date dateTo;

	// day part
	private int hours;

	private float workDays;
	private int calendarDays;
	private String place;
	private String note;

	private Long statusId;
	private String statusDescription;
	private Date changeDateTime;
	private Date createDate;
	private String changedByName;
	private String changedBySurname;

	private String responsalisName;

	private Long requestReasonId;
	private String requestReasonDescription;

	private String superior;
	private Double remainingDays;

	public CCodeListRecord getStatus() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(statusId);
		return record;
	}

	public String getChangedBy() {
		return changedByName + " " + changedBySurname;
	}

	public String getOwnerWholeName() {
		return ownerSurname + " " + ownerName;
	}

	public CCodeListRecord getRequestReason() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(requestReasonId);
		return record;
	}

	public void setRequestReason(CCodeListRecord record) {
		this.requestReasonId = record.getId();
	}

	public CCodeListRecord getRequestType() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(typeId);
		return record;
	}

	public void setRequestType(CCodeListRecord record) {
		this.typeId = record.getId();
	}

	public CCodeListRecord getOwner() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(ownerId);
		return record;
	}

	public void setOwner(CCodeListRecord record) {
		this.ownerId = record.getId();
	}

	public boolean getHalfday() {
		return !fullday;
	}

	public void setHalfday(boolean b) {
		this.fullday = !b;
	}

	public String getResponsalisName() {
		return responsalisName;
	}

	public void setResponsalisName(String responsalisName) {
		this.responsalisName = responsalisName;
	}

	public String getCreatorPosition() {
		return this.creatorPosition;
	}

	public void setCreatorPosition(final String creatorPosition) {
		this.creatorPosition = creatorPosition;
	}

	public String getChangedByName() {
		return this.changedByName;
	}

	public void setChangedByName(final String changedByName) {
		this.changedByName = changedByName;
	}

	public String getChangedBySurname() {
		return this.changedBySurname;
	}

	public void setChangedBySurname(final String changedBySurname) {
		this.changedBySurname = changedBySurname;
	}

	public Date getChangeDateTime() {
		return this.changeDateTime;
	}

	public void setChangeDateTime(final Date changeDateTime) {
		this.changeDateTime = changeDateTime;
	}

	public Long getStatusId() {
		return this.statusId;
	}

	public void setStatusId(final Long status) {
		this.statusId = status;
	}

	public Long getTypeId() {
		return this.typeId;
	}

	public void setTypeId(final Long type) {
		this.typeId = type;
	}

	public Long getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(final Long owner) {
		this.ownerId = owner;
	}

	public String getCreatorName() {
		return this.creatorName;
	}

	public void setCreatorName(final String creatorName) {
		this.creatorName = creatorName;
	}

	public String getCreatorSurname() {
		return this.creatorSurname;
	}

	public void setCreatorSurname(final String creatorSurname) {
		this.creatorSurname = creatorSurname;
	}

	public boolean isFullday() {
		return this.fullday;
	}

	public void setFullday(final boolean fullday) {
		this.fullday = fullday;
	}

	public Date getDateFrom() {
		return this.dateFrom;
	}

	public void setDateFrom(final Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return this.dateTo;
	}

	public void setDateTo(final Date dateTo) {
		this.dateTo = dateTo;
	}

	public int getHours() {
		return this.hours;
	}

	public void setHours(final int hours) {
		this.hours = hours;
	}

	public float getWorkDays() {
		return this.workDays;
	}

	public void setWorkDays(final float workDays) {
		this.workDays = workDays;
	}

	public int getCalendarDays() {
		return this.calendarDays;
	}

	public void setCalendarDays(final int calendarDays) {
		this.calendarDays = calendarDays;
	}

	public String getPlace() {
		return this.place;
	}

	public void setPlace(final String place) {
		this.place = place;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(final Date createDate) {
		this.createDate = createDate;
	}

	public Long getRequestReasonId() {
		return requestReasonId;
	}

	public void setRequestReasonId(Long requestReasonId) {
		this.requestReasonId = requestReasonId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
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

	public String getDate() {
		if (dateFrom == null || dateTo == null) {
			return "";
		}

		if (fullday) {
			return CDateUtils.createRange(dateFrom, dateTo);
		} else {
			return CDateUtils.formatDate(dateFrom);
		}
	}

	public void setDate(String dateField) {
		if (dateField.contains("-")) {
			CRange range;
			try {
				range = CDateUtils.parseRange(dateField);
				this.dateFrom = range.getFromDate();
				this.dateTo = range.getToDate();
			} catch (ParseException e) {
				// nic
			}
		} else {
			try {
				Date date = CDateUtils.parseDate(dateField);
				this.dateFrom = date;
				this.dateTo = date;
			} catch (ParseException e) {
				// nic
			}
		}
	}

	public String getRequestReasonDescription() {
		return requestReasonDescription;
	}

	public void setRequestReasonDescription(String requestReasonDescription) {
		this.requestReasonDescription = requestReasonDescription;
	}

	public Double getRemainingDays() {
		return remainingDays;
	}

	public void setRemainingDays(Double remainingDays) {
		this.remainingDays = remainingDays;
	}

	public String getEmployeeField() {
		return ownerName + " " + ownerSurname;
	}

	public void setEmployeeField(String employeeField) {
		// nothing
	}
}
