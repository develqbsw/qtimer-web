package sk.qbsw.sed.server.model.report;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the v_employees database table.
 * 
 */

@Entity
@Table(schema = "public", name = "v_employees")
public class CViewEmployees implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "id", nullable = false)
	private String empCode;

	@Column(name = "meno_priezvisko", nullable = true)
	private String name;

	@Column(name = "rodne_cislo", nullable = true)
	private String identificationNumber;

	@Column(name = "ico", nullable = true)
	private String crn;

	@Column(name = "dic", nullable = true)
	private String vatin;

	@Column(name = "datum_narodenia", nullable = true)
	private String birthDate;

	@Column(name = "pp_typ", nullable = true)
	private String description;

	@Column(name = "prac_pomer_od", nullable = true)
	private String workStartDate;

	@Column(name = "prac_pomer_do", nullable = true)
	private String workEndDate;

	@Column(name = "zaradenie", nullable = true)
	private String positionName;

	@Column(name = "titul", nullable = true)
	private String title;

	@Column(name = "miesto_narodenia", nullable = true)
	private String birthPlace;

	@Column(name = "cislo_op", nullable = true)
	private String residentIdentityCardNumber;

	@Column(name = "tp_ulica", nullable = true)
	private String contactStreet;

	@Column(name = "tp_cislo", nullable = true)
	private String contactStreetNumber;

	@Column(name = "tp_psc", nullable = true)
	private String contactZip;

	@Column(name = "tp_mesto", nullable = true)
	private String city;

	@Column(name = "zdravotna_poistovna", nullable = true)
	private String healthInsuranceCompany;

	@Column(name = "cislo_uctu", nullable = true)
	private String bankAccountNumber;

	@Column(name = "banka", nullable = true)
	private String bankInstitution;

	@Column(name = "fk_client", nullable = true)
	private Long client;

	@Column(name = "c_flag_valid", nullable = true)
	private Boolean valid;

	@Column(name = "c_flag_list_criminal_records", nullable = true)
	private Boolean criminalRecords;

	@Column(name = "c_flag_recruit_medical_check", nullable = true)
	private Boolean recMedicalCheck;

	@Column(name = "c_flag_multisport_card", nullable = true)
	private Boolean multisportCard;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWorkStartDate() {
		return workStartDate;
	}

	public void setWorkStartDate(String workStartDate) {
		this.workStartDate = workStartDate;
	}

	public String getWorkEndDate() {
		return workEndDate;
	}

	public void setWorkEndDate(String workEndDate) {
		this.workEndDate = workEndDate;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public String getResidentIdentityCardNumber() {
		return residentIdentityCardNumber;
	}

	public void setResidentIdentityCardNumber(String residentIdentityCardNumber) {
		this.residentIdentityCardNumber = residentIdentityCardNumber;
	}

	public String getContactStreet() {
		return contactStreet;
	}

	public void setContactStreet(String contactStreet) {
		this.contactStreet = contactStreet;
	}

	public String getContactStreetNumber() {
		return contactStreetNumber;
	}

	public void setContactStreetNumber(String contactStreetNumber) {
		this.contactStreetNumber = contactStreetNumber;
	}

	public String getContactZip() {
		return contactZip;
	}

	public void setContactZip(String contactZip) {
		this.contactZip = contactZip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public Long getClient() {
		return client;
	}

	public void setClient(Long client) {
		this.client = client;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
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
}
