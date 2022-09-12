package sk.qbsw.sed.client.model.detail;

import java.util.Date;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;
import sk.qbsw.sed.client.model.security.CZoneRecord;

/**
 * Transfer object for user detail and modify
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
public class CUserDetailRecord extends CRegistrationUserRecord {
	private String phoneMobile;
	private String position;
	private String userTypeString;
	private String employeeCode;
	private String note;
	private Byte[] photo;
	private Long photoId;
	private Long superiorId;
	private String street;
	private String streetNumber;
	private String zip;
	private String country;
	private String city;
	private String superiorName;
	private String superiorSurname;
	private String superiorPosition;
	private Long language;
	private Long zone;
	private String officeNumber;
	private Integer tableRows;

	/**
	 * Flag if process of edit time is allowed for the user
	 */
	private Boolean editTime;

	/**
	 * Flag if the user has the right to use alertness/interactive work buttons
	 */
	private Boolean allowedAlertnessWork;
	private Date birthDate;
	private Date workStartDate;
	private Double vacation;
	private Double vacationNextYear;
	private String personalIdNumber;
	private String crn;
	private String vatin;
	private Long typeOfEmployment;
	private Date workEndDate;
	private String degTitle;
	private String residentIdCardNum;
	private String healthInsurComp;
	private String bankAccountNumber;
	private String bankInstitution;
	private String birthPlace;
	private Boolean absentCheck;
	private Boolean criminalRecords;
	private Boolean recMedicalCheck;
	private Boolean multisportCard;
	private Boolean jiraTokenGeneration;
	private Long homeOfficePermission;

	public CCodeListRecord getTableRowsField() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(tableRows.longValue());
		return record;
	}

	public void setTableRowsField(CCodeListRecord record) {
		this.tableRows = record.getId().intValue();
	}

	public String getSuperior() {
		StringBuilder sb = new StringBuilder();
		if (superiorName != null) {
			sb.append(superiorName);
			if (superiorSurname != null)
				sb.append(" ");
		}
		if (superiorSurname != null)
			sb.append(superiorSurname);
		return sb.toString();
	}

	public CCodeListRecord getLanguageField() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(language);
		return record;
	}

	public void setLanguageField(CCodeListRecord record) {
		this.language = record.getId();
	}

	public CCodeListRecord getTypeOfEmploymentField() {
		CCodeListRecord record = new CCodeListRecord();
		if (typeOfEmployment != null) {
			record.setId(typeOfEmployment);
		} else {
			// ak je type of employment null, nastavím 1 = Zamestnanec
			record.setId(Long.valueOf(1));
		}

		return record;
	}

	public void setTypeOfEmploymentField(CCodeListRecord record) {
		if (record != null) {
			this.typeOfEmployment = record.getId();
		}
	}

	public CZoneRecord getZoneField() {
		CZoneRecord record = new CZoneRecord();
		record.setId(zone);
		return record;
	}

	public void setZoneField(CZoneRecord record) {
		if (record != null)
			this.zone = record.getId();
	}

	public CCodeListRecord getSuperiorIdField() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(superiorId);
		return record;
	}

	public void setSuperiorIdField(CCodeListRecord record) {
		if (record != null)
			this.superiorId = record.getId();
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getUserTypeString() {
		return userTypeString;
	}

	public void setUserTypeString(String typeString) {
		this.userTypeString = typeString;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(Byte[] photo) {
		this.photo = photo;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSuperiorName() {
		return superiorName;
	}

	public void setSuperiorName(String superiorName) {
		this.superiorName = superiorName;
	}

	public String getSuperiorSurname() {
		return superiorSurname;
	}

	public void setSuperiorSurname(String superiorSurname) {
		this.superiorSurname = superiorSurname;
	}

	public String getSuperiorPosition() {
		return superiorPosition;
	}

	public void setSuperiorPosition(String superiorPosition) {
		this.superiorPosition = superiorPosition;
	}

	public Boolean getEditTime() {
		return editTime;
	}

	public void setEditTime(Boolean editTime) {
		this.editTime = editTime;
	}

	public Boolean getAllowedAlertnessWork() {
		return allowedAlertnessWork;
	}

	public void setAllowedAlertnessWork(Boolean allowedAlertnessWork) {
		this.allowedAlertnessWork = allowedAlertnessWork;
	}

	public Long getLanguage() {
		return language;
	}

	public void setLanguage(Long language) {
		this.language = language;
	}

	public Long getZone() {
		return zone;
	}

	public void setZone(Long zone) {
		this.zone = zone;
	}

	public String getOfficeNumber() {
		return officeNumber;
	}

	public void setOfficeNumber(String officeNumber) {
		this.officeNumber = officeNumber;
	}

	public Integer getTableRows() {
		return tableRows;
	}

	public void setTableRows(Integer tableRows) {
		this.tableRows = tableRows;
	}

	public Long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(Long photoId) {
		this.photoId = photoId;
	}

	public Long getSuperiorId() {
		return superiorId;
	}

	public void setSuperiorId(Long superiorId) {
		this.superiorId = superiorId;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getWorkStartDate() {
		return workStartDate;
	}

	public void setWorkStartDate(Date workStartDate) {
		this.workStartDate = workStartDate;
	}

	public Double getVacation() {
		return vacation;
	}

	public void setVacation(Double vacation) {
		this.vacation = vacation;
	}

	public Double getVacationNextYear() {
		return vacationNextYear;
	}

	public void setVacationNextYear(Double vacationNextYear) {
		this.vacationNextYear = vacationNextYear;
	}

	public String getPersonalIdNumber() {
		return personalIdNumber;
	}

	public void setPersonalIdNumber(String personalIdNumber) {
		this.personalIdNumber = personalIdNumber;
	}

	public String getCrn() {
		return crn;
	}

	public void setCrn(String crn) {
		this.crn = crn;
	}

	public String getVatin() {
		return vatin;
	}

	public void setVatin(String vatin) {
		this.vatin = vatin;
	}

	public Long getTypeOfEmployment() {
		return typeOfEmployment;
	}

	public void setTypeOfEmployment(Long typeOfEmployment) {
		this.typeOfEmployment = typeOfEmployment;
	}

	public Date getWorkEndDate() {
		return workEndDate;
	}

	public void setWorkEndDate(Date workEndDate) {
		this.workEndDate = workEndDate;
	}

	public String getDegTitle() {
		return degTitle;
	}

	public void setDegTitle(String degTitle) {
		this.degTitle = degTitle;
	}

	public String getResidentIdCardNum() {
		return residentIdCardNum;
	}

	public void setResidentIdCardNum(String residentIdCardNum) {
		this.residentIdCardNum = residentIdCardNum;
	}

	public String getHealthInsurComp() {
		return healthInsurComp;
	}

	public void setHealthInsurComp(String healthInsurComp) {
		this.healthInsurComp = healthInsurComp;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getBankInstitution() {
		return bankInstitution;
	}

	public void setBankInstitution(String bankInstitution) {
		this.bankInstitution = bankInstitution;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public Boolean getAbsentCheck() {
		return absentCheck;
	}

	public void setAbsentCheck(Boolean absentCheck) {
		this.absentCheck = absentCheck;
	}

	public Boolean getCriminalRecords() {
		return criminalRecords;
	}

	public void setCriminalRecords(Boolean criminalRecords) {
		this.criminalRecords = criminalRecords;
	}

	public Boolean getRecMedicalCheck() {
		return recMedicalCheck;
	}

	public void setRecMedicalCheck(Boolean recMedicalCheck) {
		this.recMedicalCheck = recMedicalCheck;
	}

	public Boolean getMultisportCard() {
		return multisportCard;
	}

	public void setMultisportCard(Boolean multisportCard) {
		this.multisportCard = multisportCard;
	}

	public Long getHomeOfficePermission() {
		return homeOfficePermission;
	}

	public void setHomeOfficePermission(Long homeOfficePermission) {
		this.homeOfficePermission = homeOfficePermission;
	}

	public void setHomeOfficePermissionField(CCodeListRecord record) {
		if (record != null) {
			this.homeOfficePermission = record.getId();
		}
	}

	public Boolean getJiraTokenGeneration() {
		return jiraTokenGeneration;
	}

	public void setJiraTokenGeneration(Boolean jiraTokenGeneration) {
		this.jiraTokenGeneration = jiraTokenGeneration;
	}

	public CCodeListRecord getHomeOfficePermissionField() {
		CCodeListRecord record = new CCodeListRecord();
		if (homeOfficePermission != null) {
			record.setId(homeOfficePermission);
		} else {
			// ak je home office permission null, nastavím 2 = Iba so žiadosťou
			record.setId(Long.valueOf(2));
		}

		return record;
	}
}
