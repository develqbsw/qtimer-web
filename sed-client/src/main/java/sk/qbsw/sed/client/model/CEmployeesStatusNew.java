package sk.qbsw.sed.client.model;

import java.io.Serializable;

import sk.qbsw.sed.client.model.brw.CEmployeesStatus;

public class CEmployeesStatusNew implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private Long clientId;

	private String surname;

	private String name;

	private String status;

	private String officeNumber;

	private Long photoId;

	private String email;

	private String phone;

	private Long zoneId;

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

	public Long getZoneId() {
		return zoneId;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public CEmployeesStatus convert() {
		CEmployeesStatus employeesStatus = new CEmployeesStatus();

		employeesStatus.setClientId(clientId);
		employeesStatus.setId(id);
		employeesStatus.setName(name);
		employeesStatus.setSurname(surname);
		employeesStatus.setOfficeNumber(officeNumber);
		employeesStatus.setEmail(email);
		employeesStatus.setPhone(phone);
		employeesStatus.setPhotoId(photoId);

		String statusNew = status;
		String absenceInfo = null;

		if (IStatusConstants.HOLIDAY.equals(status)) {
			statusNew = IStatusConstants.NOT_IN_WORK;
			absenceInfo = "RQSTATE_APPROVED";
		} else if (IStatusConstants.MEETING.equals(status)) {
			statusNew = IStatusConstants.IN_WORK;
		} else if (IStatusConstants.WORK_BREAK.equals(status)) {
			statusNew = IStatusConstants.OUT_OF_WORK;
			absenceInfo = IStatusConstants.WORK_BREAK;
		}

		employeesStatus.setStatus(statusNew);
		employeesStatus.setAbsenceInfo(absenceInfo);

		return employeesStatus;
	}
}
