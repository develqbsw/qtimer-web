package sk.qbsw.sed.client.model.brw;

import java.io.Serializable;
import java.util.Date;

public class CEmployeesStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private Long clientId;

	private String employeeCode;

	private String surname;

	private String name;

	private Date startTime;

	private Date stopTime;

	private Date tmpday;

	private String status;

	private String absenceInfo;

	private String outside;

	private Boolean flagWorkplace;

	private String officeNumber;

	private Long photoId;

	private String email;

	private String phone;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStopTime() {
		return stopTime;
	}

	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}

	public Date getTmpday() {
		return tmpday;
	}

	public void setTmpday(Date tmpday) {
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

	public String getOfficeNumber() {
		return officeNumber;
	}

	public void setOfficeNumber(String officeNumber) {
		this.officeNumber = officeNumber;
	}

	public Long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(Long photoId) {
		this.photoId = photoId;
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
}
