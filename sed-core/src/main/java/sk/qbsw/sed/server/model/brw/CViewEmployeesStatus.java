package sk.qbsw.sed.server.model.brw;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import sk.qbsw.sed.client.model.CEmployeesStatusNew;
import sk.qbsw.sed.client.model.IStatusConstants;

/**
 * 
 * @author rosenberg
 *
 */
@Entity
@Table(schema = "public", name = "v_employees_status")
public class CViewEmployeesStatus implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_client_id", nullable = false)
	private Long clientId;

	@Column(name = "c_emp_code", nullable = false)
	private String employeeCode;

	@Column(name = "c_surname", nullable = false)
	private String surname;

	@Column(name = "c_name", nullable = false)
	private String name;

	@Column(name = "c_start", nullable = true)
	private Calendar startTime;

	@Column(name = "c_stop", nullable = true)
	private Calendar stopTime;

	@Column(name = "tmpday", nullable = true)
	private Calendar tmpday;

	@Column(name = "c_status", nullable = false)
	private String status;

	@Column(name = "c_absence_info", nullable = true)
	private String absenceInfo;

	@Column(name = "c_outside", nullable = true)
	private String outside;

	@Column(name = "c_flag_workplace", nullable = true)
	private Boolean flagWorkplace;

	@Column(name = "c_home_office", nullable = true)
	private String homeOffice;

	@Column(name = "c_zone_id", nullable = true)
	private Long zoneId;

	@Column(name = "c_office_number", nullable = true)
	private String officeNumber;

	@Column(name = "c_contact_email", nullable = false)
	private String email;

	@Column(name = "c_contact_phone", nullable = true)
	private String phone;

	@Column(name = "fk_user_photo", nullable = true)
	private Long userPhotoId;

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

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Calendar getStopTime() {
		return stopTime;
	}

	public void setStopTime(Calendar stopTime) {
		this.stopTime = stopTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Calendar getTmpday() {
		return tmpday;
	}

	public void setTmpday(Calendar tmpday) {
		this.tmpday = tmpday;
	}

	public String getAbsenceInfo() {
		return absenceInfo;
	}

	public void setAbsenceInfo(String absenceInfo) {
		this.absenceInfo = absenceInfo;
	}

	public String getOutside() {
		return outside;
	}

	public void setOutside(String outside) {
		this.outside = outside;
	}

	public Boolean getFlagWorkplace() {
		return flagWorkplace;
	}

	public void setFlagWorkplace(Boolean flagWorkplace) {
		this.flagWorkplace = flagWorkplace;
	}

	public Long getZoneId() {
		return zoneId;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public String getOfficeNumber() {
		return officeNumber;
	}

	public void setOfficeNumber(String officeNumber) {
		this.officeNumber = officeNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getUserPhotoId() {
		return userPhotoId;
	}

	public void setUserPhotoId(Long userPhotoId) {
		this.userPhotoId = userPhotoId;
	}

	public String getHomeOffice() {
		return homeOffice;
	}

	public void setHomeOffice(String homeOffice) {
		this.homeOffice = homeOffice;
	}

	public CEmployeesStatusNew convertToEmployeesStatusNew() {
		CEmployeesStatusNew employeesStatus = new CEmployeesStatusNew();

		employeesStatus.setClientId(clientId);
		employeesStatus.setId(id);
		employeesStatus.setName(name);
		employeesStatus.setSurname(surname);
		employeesStatus.setOfficeNumber(officeNumber);
		employeesStatus.setEmail(email);
		employeesStatus.setPhone(phone);
		employeesStatus.setPhotoId(userPhotoId);
		employeesStatus.setZoneId(zoneId);

		String statusNew = IStatusConstants.NOT_IN_WORK;

		if (IStatusConstants.NOT_IN_WORK.equals(status)) {
			if (absenceInfo != null && absenceInfo.startsWith("RQSTATE_APPROVED")) {
				// ma schvalenu dovolenku
				statusNew = IStatusConstants.HOLIDAY;
			} else {
				statusNew = IStatusConstants.NOT_IN_WORK;
			}
		} else if (IStatusConstants.IN_WORK.equals(status)) {
			if (flagWorkplace) {
				statusNew = IStatusConstants.IN_WORK;
			} else if ("YES".equals(homeOffice)) {
				statusNew = IStatusConstants.HOME_OFFICE;
			} else {
				statusNew = IStatusConstants.MEETING;
			}
		} else if (IStatusConstants.OUT_OF_WORK.equals(status)) {
			if (absenceInfo == null) {
				statusNew = IStatusConstants.OUT_OF_WORK;
			} else {
				if (IStatusConstants.WORK_BREAK.equals(absenceInfo)) {
					statusNew = IStatusConstants.WORK_BREAK;
				} else if (absenceInfo.startsWith("RQSTATE_APPROVED")) {
					// ma schvalenu dovolenku, ale bol aj v praci a uz odisiel
					statusNew = IStatusConstants.OUT_OF_WORK;
				}
			}
		}

		employeesStatus.setStatus(statusNew);

		return employeesStatus;
	}
}
