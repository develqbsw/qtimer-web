package sk.qbsw.sed.server.model.brw;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(schema = "public", name = "v_requests")
public class CViewRequest implements Serializable {

	@Id
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_date_from", nullable = true)
	private Calendar dateFrom;

	@Column(name = "c_date_to", nullable = true)
	private Calendar dateTo;

	@Column(name = "c_employee_name", nullable = true)
	private String employeeName;

	@Column(name = "c_employee_surname", nullable = true)
	private String employeeSurname;

	@Column(name = "c_changed_name", nullable = false)
	private String changedByName;

	@Column(name = "c_changed_surname", nullable = false)
	private String changedBySurname;

	@Column(name = "fk_owner", nullable = false)
	private Long ownerId;

	@Column(name = "c_status_description", nullable = false)
	private String status;

	@Column(name = "fk_status", nullable = false)
	private Long statusId;

	@Column(name = "c_type_description", nullable = false)
	private String type;

	@Column(name = "fk_request_type", nullable = false)
	private Long typeId;

	@Column(name = "c_date_day")
	private Long day;

	@Column(name = "c_date_month")
	private Long month;

	@Column(name = "c_date_year")
	private Long year;

	public Calendar getDateFrom() {
		return this.dateFrom;
	}

	public Calendar getDateTo() {
		return this.dateTo;
	}

	public String getEmployeeName() {
		return this.employeeName;
	}

	public String getEmployeeSurname() {
		return this.employeeSurname;
	}

	public String getChangedByName() {
		return this.changedByName;
	}

	public String getChangedBySurname() {
		return this.changedBySurname;
	}

	public Long getId() {
		return this.id;
	}

	public Long getOwnerId() {
		return this.ownerId;
	}

	public String getStatus() {
		return this.status;
	}

	public Long getStatusId() {
		return this.statusId;
	}

	public String getType() {
		return this.type;
	}

	public Long getTypeId() {
		return this.typeId;
	}

	public void setDateFrom(final Calendar dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(final Calendar dateTo) {
		this.dateTo = dateTo;
	}

	public void setEmployeeName(final String employeeName) {
		this.employeeName = employeeName;
	}

	public void setEmployeeSurname(final String employeeSurname) {
		this.employeeSurname = employeeSurname;
	}

	public void setChangedByName(final String changedByName) {
		this.changedByName = changedByName;
	}

	public void setChangedBySurname(final String changedBySurname) {
		this.changedBySurname = changedBySurname;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setOwnerId(final Long ownerId) {
		this.ownerId = ownerId;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setStatusId(final Long statusId) {
		this.statusId = statusId;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public void setTypeId(final Long typeId) {
		this.typeId = typeId;
	}

	public Long getDay() {
		return this.day;
	}

	public void setDay(final Long day) {
		this.day = day;
	}

	public Long getMonth() {
		return this.month;
	}

	public void setMonth(final Long month) {
		this.month = month;
	}

	public Long getYear() {
		return this.year;
	}

	public void setYear(final Long year) {
		this.year = year;
	}
}
