package sk.qbsw.sed.client.model.detail;

import java.io.Serializable;
import java.util.Date;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

@SuppressWarnings("serial")
public class CClientDetailRecord implements Serializable {
	private Long clienId;
	private String name;
	private String nameShort;
	private Long legalFormId;

	private String street;
	private String streetNumber;
	private String city;
	private String zipCode;

	private Boolean projectRequiredFlag;
	private Boolean activityRequiredFlag;
	private Date timeAutoEmail;
	private Date timeAutoGenerateTimestamps;
	private Long shiftDay;
	private Long language;
	private Date intervalStopWorkRec;
	private Boolean generateMessages;
	private String bonusVacation;

	public CCodeListRecord getLanguageField() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(language);
		return record;
	}

	public void setLanguageField(CCodeListRecord record) {
		this.language = record.getId();
	}

	public CCodeListRecord getLegalForm() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(legalFormId);
		return record;
	}

	public void setLegalForm(CCodeListRecord record) {
		this.legalFormId = record.getId();
	}

	public CCodeListRecord getShiftDayField() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(shiftDay);
		return record;
	}

	public void setShiftDayField(CCodeListRecord record) {
		this.shiftDay = record.getId();
	}

	public Long getClientId() {
		return clienId;
	}

	public void setClientId(Long clienId) {
		this.clienId = clienId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameShort() {
		return nameShort;
	}

	public void setNameShort(String nameShort) {
		this.nameShort = nameShort;
	}

	public Long getLegalFormId() {
		return legalFormId;
	}

	public void setLegalFormId(Long legalFormId) {
		this.legalFormId = legalFormId;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Boolean getProjectRequiredFlag() {
		return projectRequiredFlag;
	}

	public void setProjectRequiredFlag(Boolean projectRequiredFlag) {
		this.projectRequiredFlag = projectRequiredFlag;
	}

	public Boolean getActivityRequiredFlag() {
		return activityRequiredFlag;
	}

	public void setActivityRequiredFlag(Boolean activityRequiredFlag) {
		this.activityRequiredFlag = activityRequiredFlag;
	}

	public Date getTimeAutoEmail() {
		return timeAutoEmail;
	}

	public void setTimeAutoEmail(Date timeAutoEmail) {
		this.timeAutoEmail = timeAutoEmail;
	}

	public Date getTimeAutoGenerateTimestamps() {
		return timeAutoGenerateTimestamps;
	}

	public void setTimeAutoGenerateTimestamps(Date timeAutoGenerateTimestamps) {
		this.timeAutoGenerateTimestamps = timeAutoGenerateTimestamps;
	}

	public Long getShiftDay() {
		return shiftDay;
	}

	public void setShiftDay(Long shiftDay) {
		this.shiftDay = shiftDay;
	}

	public Long getLanguage() {
		return language;
	}

	public void setLanguage(Long language) {
		this.language = language;
	}

	public Date getIntervalStopWorkRec() {
		return intervalStopWorkRec;
	}

	public void setIntervalStopWorkRec(Date intervalStopWorkRec) {
		this.intervalStopWorkRec = intervalStopWorkRec;
	}

	public Boolean getGenerateMessages() {
		return generateMessages;
	}

	public void setGenerateMessages(Boolean generateMessages) {
		this.generateMessages = generateMessages;
	}

	public String getBonusVacation() {
		return bonusVacation;
	}

	public void setBonusVacation(String bonusVacation) {
		this.bonusVacation = bonusVacation;
	}
}
