package sk.qbsw.sed.server.model.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.server.model.codelist.CRequestReason;
import sk.qbsw.sed.server.model.codelist.CRequestStatus;
import sk.qbsw.sed.server.model.codelist.CRequestType;

/**
 * Model mapped to table public.t_request
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_request", sequenceName = "s_request", allocationSize = 1)
@Table(schema = "public", name = "t_request")
public class CRequest implements Serializable {
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_createdby", nullable = false)
	private CUser createdBy;

	@Column(name = "c_date_from", nullable = false)
	private Calendar dateFrom;

	@Column(name = "c_date_to", nullable = false)
	private Calendar dateTo;

	@Column(name = "c_hours_for_datefrom", nullable = true)
	private Float hoursDateFrom;

	@Column(name = "c_hours_for_dateto", nullable = true)
	private Float hoursDateTo;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_changedby", nullable = false)
	private CUser changedBy;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

	@Id
	@GeneratedValue(generator = "s_request", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_note", nullable = true)
	private String note;

	@Column(name = "c_number_of_working_days", nullable = false)
	private Float numberWorkDays;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_owner", nullable = false)
	private CUser owner;

	@Column(name = "c_place_description", nullable = true)
	private String place;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_status", nullable = false)
	private CRequestStatus status;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_request_type", nullable = false)
	private CRequestType type;

	@Column(name = "c_create_date", nullable = false)
	private Calendar createDate;

	@Column(name = "c_responsalis_name", nullable = true, length = 150)
	private String responsalisName;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_reason", nullable = true)
	private CRequestReason reason;

	@Column(name = "c_code", nullable = true)
	private String code;

	@Column(name = "c_date_last_gen_holiday", nullable = true)
	private Calendar dateLastGenHoliday;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public CClient getClient() {
		return this.client;
	}

	public CUser getCreatedBy() {
		return this.createdBy;
	}

	public Calendar getDateFrom() {
		return this.dateFrom;
	}

	public Calendar getDateTo() {
		return this.dateTo;
	}

	public Float getHoursDateFrom() {
		return this.hoursDateFrom;
	}

	public Float getHoursDateTo() {
		return this.hoursDateTo;
	}

	public CUser getChangedBy() {
		return this.changedBy;
	}

	public Calendar getChangeTime() {
		return this.changeTime;
	}

	public Long getId() {
		return this.id;
	}

	public String getNote() {
		return this.note;
	}

	public Float getNumberWorkDays() {
		return this.numberWorkDays;
	}

	public CUser getOwner() {
		return this.owner;
	}

	public String getPlace() {
		return this.place;
	}

	public CRequestStatus getStatus() {
		return this.status;
	}

	public CRequestType getType() {
		return this.type;
	}

	public void setClient(final CClient client) {
		this.client = client;
	}

	public void setCreatedBy(final CUser createdBy) {
		this.createdBy = createdBy;
	}

	public void setDateFrom(final Calendar dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(final Calendar dateTo) {
		this.dateTo = dateTo;
	}

	public void setHoursDateFrom(final Float hoursDateFrom) {
		this.hoursDateFrom = hoursDateFrom;
	}

	public void setHoursDateTo(final Float hoursDateTo) {
		this.hoursDateTo = hoursDateTo;
	}

	public void setChangedBy(final CUser changedBy) {
		this.changedBy = changedBy;
	}

	public void setChangeTime(final Calendar changeTime) {
		this.changeTime = changeTime;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setNumberWorkDays(final Float numberWorkDays) {
		this.numberWorkDays = numberWorkDays;
	}

	public void setOwner(final CUser owner) {
		this.owner = owner;
	}

	public void setPlace(final String place) {
		this.place = place;
	}

	public void setStatus(final CRequestStatus status) {
		this.status = status;
	}

	public void setType(final CRequestType type) {
		this.type = type;
	}

	public Calendar getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(final Calendar createDate) {
		this.createDate = createDate;
	}

	public String getResponsalisName() {
		return responsalisName;
	}

	public void setResponsalisName(String responsalisName) {
		this.responsalisName = responsalisName;
	}

	public CRequestReason getReason() {
		return reason;
	}

	public void setReason(CRequestReason reason) {
		this.reason = reason;
	}

	public Calendar getDateLastGenHoliday() {
		return dateLastGenHoliday;
	}

	public void setDateLastGenHoliday(Calendar dateLastGenHoliday) {
		this.dateLastGenHoliday = dateLastGenHoliday;
	}

	public CRequestRecord convert() {
		final CRequestRecord detail = new CRequestRecord();

		CRequest request = this;

		// creator
		detail.setId(request.getId());
		detail.setCreatorName(request.getCreatedBy().getName());
		detail.setCreatorSurname(request.getCreatedBy().getSurname());

		final List<COrganizationTree> positions = request.getCreatedBy().getOrganizationTreesOwned();
		if ((positions != null) && (positions.size() > 0)) {
			detail.setCreatorPosition(positions.get(0).getPossition());
		}

		// dates
		detail.setDateFrom(request.getDateFrom().getTime());
		detail.setDateTo(request.getDateTo().getTime());
		// day part
		if (request.getHoursDateFrom() != null) {
			detail.setFullday(false);
			detail.setHours(request.getHoursDateFrom().intValue());
		} else {
			detail.setFullday(true);
		}

		// other data
		detail.setNote(request.getNote());
		detail.setPlace(request.getPlace());
		detail.setTypeId(request.getType().getId());
		detail.setWorkDays(request.getNumberWorkDays().floatValue());
		detail.setOwnerId(request.getOwner().getId());
		detail.setOwnerName(request.getOwner().getName());
		detail.setOwnerSurname(request.getOwner().getSurname());
		detail.setCreateDate(request.getCreateDate().getTime());

		// status
		detail.setStatusId(request.getStatus().getId());
		detail.setChangeDateTime(request.getChangeTime().getTime());
		detail.setChangedByName(request.getChangedBy().getName());
		detail.setChangedBySurname(request.getChangedBy().getSurname());

		detail.setResponsalisName(request.getResponsalisName());

		if (request.getReason() != null) {
			detail.setRequestReasonId(request.getReason().getId());
			detail.setRequestReasonDescription(request.getReason().getReasonName());
		}

		detail.setStatusDescription(request.getStatus().getDescription());
		detail.setTypeDescription(request.getType().getDescription());
		if (request.getOwner().getOrganizationTreesOwned().get(0).getSuperior() != null) {
			CUser superior = request.getOwner().getOrganizationTreesOwned().get(0).getSuperior().getOwner();
			detail.setSuperior(superior.getName() + " " + superior.getSurname());
		}

		return detail;
	}
}
