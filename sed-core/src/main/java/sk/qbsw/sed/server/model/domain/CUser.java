package sk.qbsw.sed.server.model.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.codelist.CHomeOfficePermission;
import sk.qbsw.sed.server.model.codelist.CProject;
import sk.qbsw.sed.server.model.codelist.CUserType;

/**
 * Model mapped to table public.t_user
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_user", sequenceName = "s_user", allocationSize = 1)
@Table(schema = "public", name = "t_user")
public class CUser implements Serializable {
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "changedBy")
	private List<CActivity> activitiesChanged = new ArrayList<>(0);

	@Column(name = "c_city", nullable = true)
	private String city;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@Column(name = "c_contact_country", nullable = true)
	private String country;

	@Column(name = "c_contact_email", nullable = false)
	private String email;

	@Column(name = "c_emp_code", nullable = true)
	private String employeeCode;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_changedby", nullable = false)
	private CUser changedBy;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

	@Id
	@GeneratedValue(generator = "s_user", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_photo", nullable = false)
	private CUserPhoto photo;

	// tato hodnota sa uz nikdy nebude pouzivat, miesto nej je loginLong - hack pre zivu databazu z uzkym stlpcom!
	@Column(name = "c_login", nullable = false)
	private String login;

	@Column(name = "c_login_long", nullable = false)
	private String loginLong;

	@Column(name = "c_autologin_token", nullable = true)
	private String autoLoginToken;

	@Column(name = "c_flag_main", nullable = false)
	private Boolean main;

	@Column(name = "c_contact_mobile", nullable = true)
	private String mobile;

	@Column(name = "c_name", nullable = false)
	private String name;

	@Column(name = "c_note", nullable = true)
	private String note;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
	private List<COrganizationTree> organizationTreesOwned = new ArrayList<>(0);

	@Column(name = "c_password", nullable = true)
	private String password;

	@Column(name = "c_contact_phone", nullable = true)
	private String phone;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "changedBy")
	private List<CProject> projectsChanged = new ArrayList<>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy")
	private List<CRequest> requestsCreated = new ArrayList<>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "changedBy")
	private List<CRequest> requestsChanged = new ArrayList<>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
	private List<CRequest> requestsOwned = new ArrayList<>(0);

	@Column(name = "c_contact_street", nullable = true)
	private String street;

	@Column(name = "c_contact_street_number", nullable = true)
	private String streetNumber;

	@Column(name = "c_surname", nullable = false)
	private String surname;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy")
	private List<CTimeSheetRecord> timesheetsCreated = new ArrayList<>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "changedBy")
	private List<CTimeSheetRecord> timesheetsChanged = new ArrayList<>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
	private List<CTimeSheetRecord> timesheetsOwned = new ArrayList<>(0);

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_type", nullable = false)
	private CUserType type;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	@Column(name = "c_contact_zip", nullable = true)
	private String zip;

	@Column(name = "c_pin_code", nullable = true)
	private String pinCode;

	@Column(name = "c_pin_code_salt")
	private String pinCodeSalt;

	@Column(name = "c_password_salt")
	private String passwordSalt;

	@Column(name = "c_flag_system_email", nullable = false)
	private Boolean receiveSystemEmail;

	@Column(name = "c_flag_alertness_work", nullable = false)
	private Boolean allowedAlertnessWork;

	@Column(name = "c_card_code", nullable = true)
	private String cardCode;

	@Column(name = "c_card_code_salt")
	private String cardCodeSalt;

	/**
	 * Flag if process of edit time is allowed for the user
	 */
	@Column(name = "c_flag_edit_time", nullable = true)
	private Boolean editTime;

	@Column(name = "c_language", nullable = true)
	private String language;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<CProject> userProjects = new ArrayList<>(0);

	@ManyToMany(fetch = FetchType.LAZY)
	private List<CActivity> userActivities = new ArrayList<>(0);

	@Column(name = "c_jira_access_token", nullable = true)
	private String jiraAccessToken;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_zone", nullable = true)
	private CZone zone;

	@Column(name = "c_office_number", nullable = true)
	private String officeNumber;

	@Column(name = "c_table_rows")
	private Integer tableRows;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<CUser> userFavourites = new ArrayList<>(0);

	@Column(name = "c_birth_date", nullable = true)
	private Calendar birthDate;

	@Column(name = "c_work_start_date", nullable = true)
	private Calendar workStartDate;

	@Column(name = "c_vacation")
	private Double vacation;

	@Column(name = "c_vacation_next_year")
	private Double vacationNextYear;

	@Column(name = "c_identification_number", nullable = true)
	private String identificationNumber;

	@Column(name = "c_crn", nullable = true)
	private String crn;

	@Column(name = "c_vatin", nullable = true)
	private String vatin;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_type_of_employment", nullable = false)
	private CEmploymentType employmentType;

	@Column(name = "c_work_end_date", nullable = true)
	private Calendar workEndDate;

	@Column(name = "c_title", nullable = true)
	private String title;

	@Column(name = "c_resident_identity_card_number", nullable = true)
	private String residentIdentityCardNumber;

	@Column(name = "c_health_insurance_company", nullable = true)
	private String healthInsuranceCompany;

	@Column(name = "c_bank_account_number", nullable = true)
	private String bankAccountNumber;

	@Column(name = "c_bank_institution", nullable = true)
	private String bankInstitution;

	@Column(name = "c_birth_place", nullable = true)
	private String birthPlace;

	@Column(name = "c_position_name", nullable = true)
	private String positionName;

	@Column(name = "c_flag_absent_check", nullable = false)
	private Boolean absentCheck;

	@Column(name = "c_flag_list_criminal_records", nullable = false)
	private Boolean criminalRecords;

	@Column(name = "c_flag_recruit_medical_check", nullable = false)
	private Boolean recMedicalCheck;

	@Column(name = "c_flag_multisport_card", nullable = false)
	private Boolean multisportCard;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_home_office_permission", nullable = true)
	private CHomeOfficePermission homeOfficePermission;

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "t_request_notification", joinColumns = { @JoinColumn(name = "fk_user_notify") }, inverseJoinColumns = { @JoinColumn(name = "fk_user_request") })
	private List<CUser> notified = new ArrayList<>();

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "t_request_notification", joinColumns = { @JoinColumn(name = "fk_user_request") }, inverseJoinColumns = { @JoinColumn(name = "fk_user_notify") })
	private List<CUser> AddNotified = new ArrayList<>();

	@Column(name = "c_flag_jira_token_generation", nullable = false)
	private Boolean jiraTokenGeneration;

	public List<CUser> getNotified() {
		return notified;
	}

	public void setNotified(List<CUser> notified) {
		this.notified = notified;
	}

	public Boolean getReceiveSystemEmail() {
		return receiveSystemEmail;
	}

	public void setReceiveSystemEmail(Boolean receiveSystemEmail) {
		this.receiveSystemEmail = receiveSystemEmail;
	}

	public Integer getTableRows() {
		return tableRows;
	}

	public void setTableRows(Integer tableRows) {
		this.tableRows = tableRows;
	}

	public List<CActivity> getActivitiesChanged() {
		return this.activitiesChanged;
	}

	public String getCity() {
		return this.city;
	}

	public CClient getClient() {
		return this.client;
	}

	public String getCountry() {
		return this.country;
	}

	public String getEmail() {
		return this.email;
	}

	public String getEmployeeCode() {
		return this.employeeCode;
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

	public String getLogin() {
		return this.login;
	}

	public String getLoginLong() {
		return this.loginLong;
	}

	public Boolean getMain() {
		return this.main;
	}

	public String getMobile() {
		return this.mobile;
	}

	public String getName() {
		return this.name;
	}

	public String getNote() {
		return this.note;
	}

	public List<COrganizationTree> getOrganizationTreesOwned() {
		return this.organizationTreesOwned;
	}

	public String getPassword() {
		return this.password;
	}

	public String getPhone() {
		return this.phone;
	}

	public List<CProject> getProjectsChanged() {
		return this.projectsChanged;
	}

	public List<CRequest> getRequestsCreated() {
		return this.requestsCreated;
	}

	public List<CRequest> getRequestsChanged() {
		return this.requestsChanged;
	}

	public List<CRequest> getRequestsOwned() {
		return this.requestsOwned;
	}

	public String getStreet() {
		return this.street;
	}

	public String getStreetNumber() {
		return this.streetNumber;
	}

	public String getSurname() {
		return this.surname;
	}

	public List<CTimeSheetRecord> getTimesheetsCreated() {
		return this.timesheetsCreated;
	}

	public List<CTimeSheetRecord> getTimesheetsChanged() {
		return this.timesheetsChanged;
	}

	public List<CTimeSheetRecord> getTimesheetsOwned() {
		return this.timesheetsOwned;
	}

	public CUserType getType() {
		return this.type;
	}

	public Boolean getValid() {
		return this.valid;
	}

	public String getZip() {
		return this.zip;
	}

	public void setActivitiesChanged(final List<CActivity> activitiesChanged) {
		this.activitiesChanged = activitiesChanged;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public void setClient(final CClient client) {
		this.client = client;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setEmployeeCode(final String employeeCode) {
		this.employeeCode = employeeCode;
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

	public void setLogin(final String login) {
		Date date = new Date(); // iba hack
		this.login = "" + date.getTime();
	}

	public void setLoginLong(final String login) {
		this.loginLong = login;
	}

	public void setMain(final Boolean main) {
		this.main = main;
	}

	public void setMobile(final String mobile) {
		this.mobile = mobile;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setOrganizationTreesOwned(final List<COrganizationTree> organizationTreesOwned) {
		this.organizationTreesOwned = organizationTreesOwned;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public void setProjectsChanged(final List<CProject> projectsChanged) {
		this.projectsChanged = projectsChanged;
	}

	public void setRequestsCreated(final List<CRequest> requestsCreated) {
		this.requestsCreated = requestsCreated;
	}

	public void setRequestsChanged(final List<CRequest> requestsChanged) {
		this.requestsChanged = requestsChanged;
	}

	public void setRequestsOwned(final List<CRequest> requestsOwned) {
		this.requestsOwned = requestsOwned;
	}

	public void setStreet(final String street) {
		this.street = street;
	}

	public void setStreetNumber(final String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public void setSurname(final String surname) {
		this.surname = surname;
	}

	public void setTimesheetsCreated(final List<CTimeSheetRecord> timesheetsCreated) {
		this.timesheetsCreated = timesheetsCreated;
	}

	public void setTimesheetsChanged(final List<CTimeSheetRecord> timesheetsChanged) {
		this.timesheetsChanged = timesheetsChanged;
	}

	public void setTimesheetsOwned(final List<CTimeSheetRecord> timesheetsOwned) {
		this.timesheetsOwned = timesheetsOwned;
	}

	public void setType(final CUserType type) {
		this.type = type;
	}

	public void setValid(final Boolean valid) {
		this.valid = valid;
	}

	public void setZip(final String zip) {
		this.zip = zip;
	}

	public String getAutoLoginToken() {
		return this.autoLoginToken;
	}

	public void setAutoLoginToken(final String autoLoginToken) {
		this.autoLoginToken = autoLoginToken;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getPinCodeSalt() {
		return pinCodeSalt;
	}

	public void setPinCodeSalt(String pinCodeSalt) {
		this.pinCodeSalt = pinCodeSalt;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public Boolean getEditTime() {
		return editTime;
	}

	public void setEditTime(Boolean editTime) {
		this.editTime = editTime;
	}

	public Boolean getReceiverSystemEmail() {
		return receiveSystemEmail;
	}

	public void setReceiverSystemEmail(Boolean receiveSystemEmail) {
		this.receiveSystemEmail = receiveSystemEmail;
	}

	public Boolean getAllowedAlertnessWork() {
		return allowedAlertnessWork;
	}

	public void setAllowedAlertnessWork(Boolean allowedAlertnessWork) {
		this.allowedAlertnessWork = allowedAlertnessWork;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<CProject> getUserProjects() {
		return userProjects;
	}

	public void setUserProjects(List<CProject> userProjects) {
		this.userProjects = userProjects;
	}

	public List<CActivity> getUserActivities() {
		return userActivities;
	}

	public void setUserActivities(List<CActivity> userActivities) {
		this.userActivities = userActivities;
	}

	public String getJiraAccessToken() {
		return jiraAccessToken;
	}

	public void setJiraAccessToken(String jiraAccessToken) {
		this.jiraAccessToken = jiraAccessToken;
	}

	public CUserDetailRecord convert() {
		CUserDetailRecord record = new CUserDetailRecord();

		record.setId(id);
		record.setName(name);
		record.setSurname(surname);

		return record;
	}

	public CZone getZone() {
		return zone;
	}

	public void setZone(CZone zone) {
		this.zone = zone;
	}

	public String getOfficeNumber() {
		return officeNumber;
	}

	public void setOfficeNumber(String officeNumber) {
		this.officeNumber = officeNumber;
	}

	public String getCardCode() {
		return cardCode;
	}

	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}

	public String getCardCodeSalt() {
		return cardCodeSalt;
	}

	public void setCardCodeSalt(String cardCodeSalt) {
		this.cardCodeSalt = cardCodeSalt;
	}

	public CUserPhoto getPhoto() {
		return photo;
	}

	public void setPhoto(CUserPhoto photo) {
		this.photo = photo;
	}

	public CEmployeeRecord toEmployeeRecord() {
		CEmployeeRecord retval = new CEmployeeRecord();
		retval.setUserId(getId());
		retval.setName(getName() + " " + getSurname());
		return retval;
	}

	public List<CUser> getUserFavourites() {
		return userFavourites;
	}

	public void setUserFavourites(List<CUser> userFavourites) {
		this.userFavourites = userFavourites;
	}

	public Calendar getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Calendar birthDate) {
		this.birthDate = birthDate;
	}

	public Calendar getWorkStartDate() {
		return workStartDate;
	}

	public void setWorkStartDate(Calendar workStartDate) {
		this.workStartDate = workStartDate;
	}

	public Double getVacation() {
		return vacation;
	}

	public void setVacation(Double vacation, CUser changedBy) {
		this.setChangedBy(changedBy);
		this.setChangeTime(Calendar.getInstance());
		this.vacation = vacation;
	}

	public Double getVacationNextYear() {
		return vacationNextYear;
	}

	public void setVacationNextYear(Double vacationNextYear, CUser changedBy) {
		this.setChangedBy(changedBy);
		this.setChangeTime(Calendar.getInstance());
		this.vacationNextYear = vacationNextYear;
	}

	public String getIdentificationNumber() {
		return identificationNumber;
	}

	public void setIdentificationNumber(String identificationNumber) {
		this.identificationNumber = identificationNumber;
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

	public CEmploymentType getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(CEmploymentType employmentType) {
		this.employmentType = employmentType;
	}

	public Calendar getWorkEndDate() {
		return workEndDate;
	}

	public void setWorkEndDate(Calendar workEndDate) {
		this.workEndDate = workEndDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getResidentIdentityCardNumber() {
		return residentIdentityCardNumber;
	}

	public void setResidentIdentityCardNumber(String residentIdentityCardNumber) {
		this.residentIdentityCardNumber = residentIdentityCardNumber;
	}

	public String getHealthInsuranceCompany() {
		return healthInsuranceCompany;
	}

	public void setHealthInsuranceCompany(String healthInsuranceCompany) {
		this.healthInsuranceCompany = healthInsuranceCompany;
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

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
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

	public CHomeOfficePermission getHomeOfficePermission() {
		return homeOfficePermission;
	}

	public void setHomeOfficePermission(CHomeOfficePermission homeOfficePermission) {
		this.homeOfficePermission = homeOfficePermission;
	}

	public List<CUser> getAddNotified() {
		return AddNotified;
	}

	public void setAddNotified(List<CUser> addNotified) {
		AddNotified = addNotified;
	}

	public Boolean getJiraTokenGeneration() {
		return jiraTokenGeneration;
	}

	public void setJiraTokenGeneration(Boolean jiraTokenGeneration) {
		this.jiraTokenGeneration = jiraTokenGeneration;
	}

}
