package sk.qbsw.sed.client.model.registration;

import java.io.Serializable;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.security.CClientInfo;

@SuppressWarnings("serial")
public class CRegistrationFormRecord implements Serializable {

	private CRegistrationUserRecord userRecord;
	private CRegistrationClientRecord clientRecord;

	private String repeatedPass;
	private Boolean agreeLicence;
	private Boolean agreeQBSW;

	public CRegistrationUserRecord getUserRecord() {
		return userRecord;
	}

	public CRegistrationClientRecord getClientRecord() {
		return clientRecord;
	}

	public CRegistrationFormRecord() {
		userRecord = new CRegistrationUserRecord();
		clientRecord = new CRegistrationClientRecord();
	}

	public CClientInfo getClientInfo() {
		return userRecord.getClientInfo();
	}

	public void setClientInfo(final CClientInfo clientInfo) {
		userRecord.setClientInfo(clientInfo);
	}

	public Boolean getIsMain() {
		return userRecord.getIsMain();
	}

	public void setIsMain(final Boolean isMain) {
		userRecord.setIsMain(isMain);
	}

	public Boolean getIsValid() {
		return userRecord.getIsValid();
	}

	public void setIsValid(final Boolean isValid) {
		userRecord.setIsValid(isValid);
	}

	public Long getUserType() {
		return userRecord.getUserType();
	}

	public void setUserType(final Long userType) {
		userRecord.setUserType(userType);
	}

	public String getEmail() {
		return userRecord.getEmail();
	}

	public String getLogin() {
		return userRecord.getLogin();
	}

	public String getName() {
		return userRecord.getName();
	}

	public String getPassword() {
		return userRecord.getPassword();
	}

	public String getPhoneFix() {
		return userRecord.getPhoneFix();
	}

	public String getSurname() {
		return userRecord.getSurname();
	}

	public void setEmail(final String email) {
		userRecord.setEmail(email);
	}

	public void setLogin(final String login) {
		userRecord.setLogin(login);
	}

	public void setName(final String name) {
		userRecord.setName(name);
	}

	public void setPassword(final String password) {
		userRecord.setPassword(password);
	}

	public void setPhoneFix(final String phoneFix) {
		userRecord.setPhoneFix(phoneFix);
	}

	public void setSurname(final String surname) {
		userRecord.setSurname(surname);
	}

	public Long getId() {
		return userRecord.getId();
	}

	public void setId(Long id) {
		userRecord.setId(id);
	}

	public String getOrgName() {
		return clientRecord.getOrgName();
	}

	public void setOrgName(String orgName) {
		clientRecord.setOrgName(orgName);
	}

	public Long getLegalForm() {
		return clientRecord.getLegacyForm();
	}

	public void setLegacyForm(CCodeListRecord legacyForm) {
		clientRecord.setLegacyForm(legacyForm.getId());
	}

	public String getIdNo() {
		return clientRecord.getIdNo();
	}

	public void setIdNo(String idNo) {
		clientRecord.setIdNo(idNo);
	}

	public String getTaxNo() {
		return clientRecord.getTaxNo();
	}

	public void setTaxNo(String taxNo) {
		clientRecord.setTaxNo(taxNo);
	}

	public String getVatNo() {
		return clientRecord.getVatNo();
	}

	public void setVatNo(String vatNo) {
		clientRecord.setVatNo(vatNo);
	}

	public String getStreet() {
		return clientRecord.getStreet();
	}

	public void setStreet(String street) {
		clientRecord.setStreet(street);
	}

	public String getStreetNo() {
		return clientRecord.getStreetNo();
	}

	public void setStreetNo(String streetNo) {
		clientRecord.setStreetNo(streetNo);
	}

	public String getZip() {
		return clientRecord.getZip();
	}

	public void setZip(String zip) {
		clientRecord.setZip(zip);
	}

	public String getCountry() {
		return clientRecord.getCountry();
	}

	public void setCountry(String country) {
		clientRecord.setCountry(country);
	}

	public String getCity() {
		return clientRecord.getCity();
	}

	public void setCity(String city) {
		clientRecord.setCity(city);
	}

	public CCodeListRecord getLegacyForm() {
		CCodeListRecord ret = new CCodeListRecord();
		ret.setId(clientRecord.getLegacyForm());
		return ret;
	}

	public String getRepeatedPass() {
		return repeatedPass;
	}

	public void setRepeatedPass(String repeatedPass) {
		this.repeatedPass = repeatedPass;
	}

	public Boolean getAgreeLicence() {
		return agreeLicence;
	}

	public void setAgreeLicence(Boolean agreeLicence) {
		this.agreeLicence = agreeLicence;
	}

	public Boolean getAgreeQBSW() {
		return agreeQBSW;
	}

	public void setAgreeQBSW(Boolean agreeQBSW) {
		this.agreeQBSW = agreeQBSW;
	}
}
