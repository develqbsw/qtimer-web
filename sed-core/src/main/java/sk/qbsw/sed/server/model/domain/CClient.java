package sk.qbsw.sed.server.model.domain;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import sk.qbsw.sed.server.model.codelist.CLegalForm;

/**
 * Model mapped to table public.t_client
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_client", sequenceName = "s_client", allocationSize = 1)
@Table(schema = "public", name = "t_client")
public class CClient implements Serializable {

	@Column(name = "c_contact_country", nullable = false)
	private String country;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

	@Id
	@GeneratedValue(generator = "s_client", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_identification_number", nullable = true)
	private String identificationNumber;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_legal_form", nullable = false)
	private CLegalForm legalForm;

	@Column(name = "c_client_name", nullable = false)
	private String name;

	@Column(name = "c_client_name_short", nullable = false)
	private String nameShort;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
	private List<CRequest> requests = new ArrayList<>(0);

	@Column(name = "c_contact_street", nullable = false)
	private String street;

	@Column(name = "c_contact_street_number", nullable = true)
	private String streetNumber;

	@Column(name = "c_tax_number", nullable = true)
	private String taxNumber;

	@Column(name = "c_tax_vat_number", nullable = true)
	private String taxVatNumber;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_changedby", nullable = false)
	private CUser changedBy;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	@Column(name = "c_contact_zip", nullable = false)
	private String zip;

	@Column(name = "c_city", nullable = true)
	private String city;

	@Column(name = "c_flag_project_required", nullable = false)
	private Boolean projectRequired;

	@Column(name = "c_flag_activity_required", nullable = false)
	private Boolean activityRequired;

	@Column(name = "c_time_auto_email", nullable = true)
	private Calendar timeAutoEmail;

	@Column(name = "c_time_auto_gen_ts", nullable = true)
	private Calendar timeAutoGenerateTimestamps;

	@Column(name = "c_shift_day_auto_gen_ts", nullable = true)
	private Long dayShiftAutoGenerateTimestamps;

	@Column(name = "c_language", nullable = true)
	private String language;

	@Column(name = "c_interval_stop_work_rec", nullable = true)
	private Calendar intervalStopWorkRec;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
	private List<CZone> zones = new ArrayList<>(0);

	@Column(name = "c_flag_generate_messages", nullable = false)
	private Boolean generateMessages;

	@Column(name = "c_bonus_vacation", nullable = true)
	private String bonusVacation;

	public String getCountry() {
		return this.country;
	}

	public Calendar getChangeTime() {
		return this.changeTime;
	}

	public Long getId() {
		return this.id;
	}

	public String getIdentificationNumber() {
		return this.identificationNumber;
	}

	public CLegalForm getLegalForm() {
		return this.legalForm;
	}

	public String getName() {
		return this.name;
	}

	public String getNameShort() {
		return this.nameShort;
	}

	public List<CRequest> getRequests() {
		return this.requests;
	}

	public String getStreet() {
		return this.street;
	}

	public String getStreetNumber() {
		return this.streetNumber;
	}

	public String getTaxNumber() {
		return this.taxNumber;
	}

	public String getTaxVatNumber() {
		return this.taxVatNumber;
	}

	public Boolean getValid() {
		return this.valid;
	}

	public String getZip() {
		return this.zip;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public void setChangeTime(final Calendar changeTime) {
		this.changeTime = changeTime;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setIdentificationNumber(final String identificationNumber) {
		this.identificationNumber = identificationNumber;
	}

	public void setLegalForm(final CLegalForm legalForm) {
		this.legalForm = legalForm;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNameShort(final String nameShort) {
		this.nameShort = nameShort;
	}

	public void setRequests(final List<CRequest> requests) {
		this.requests = requests;
	}

	public void setStreet(final String street) {
		this.street = street;
	}

	public void setStreetNumber(final String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public void setTaxNumber(final String taxNumber) {
		this.taxNumber = taxNumber;
	}

	public void setTaxVatNumber(final String taxVatNumber) {
		this.taxVatNumber = taxVatNumber;
	}

	public void setValid(final Boolean valid) {
		this.valid = valid;
	}

	public void setZip(final String zip) {
		this.zip = zip;
	}

	public CUser getChangedBy() {
		return this.changedBy;
	}

	public void setChangedBy(final CUser changedBy) {
		this.changedBy = changedBy;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public Boolean getProjectRequired() {
		return this.projectRequired;
	}

	public void setProjectRequired(final Boolean projectRequired) {
		this.projectRequired = projectRequired;
	}

	public Boolean getActivityRequired() {
		return this.activityRequired;
	}

	public void setActivityRequired(final Boolean activityRequired) {
		this.activityRequired = activityRequired;
	}

	public Calendar getTimeAutoEmail() {
		return timeAutoEmail;
	}

	public void setTimeAutoEmail(Calendar timeAutoEmail) {
		this.timeAutoEmail = timeAutoEmail;
	}

	public Calendar getTimeAutoGenerateTimestamps() {
		return timeAutoGenerateTimestamps;
	}

	public void setTimeAutoGenerateTimestamps(Calendar timeAutoGenerateTimestamps) {
		this.timeAutoGenerateTimestamps = timeAutoGenerateTimestamps;
	}

	public Long getDayShiftAutoGenerateTimestamps() {
		return this.dayShiftAutoGenerateTimestamps;
	}

	public void setDayShiftAutoGenerateTimestamps(Long dayShiftAutoGenerateTimestamps) {
		this.dayShiftAutoGenerateTimestamps = dayShiftAutoGenerateTimestamps;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Calendar getIntervalStopWorkRec() {
		return intervalStopWorkRec;
	}

	public void setIntervalStopWorkRec(Calendar intervalStopWorkRec) {
		this.intervalStopWorkRec = intervalStopWorkRec;
	}

	public List<CZone> getZones() {
		return zones;
	}

	public void setZones(List<CZone> zones) {
		this.zones = zones;
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
