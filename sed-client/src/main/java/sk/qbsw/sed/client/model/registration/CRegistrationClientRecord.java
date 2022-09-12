package sk.qbsw.sed.client.model.registration;

import java.io.Serializable;

/**
 * Model for registration of organization
 * 
 * @author Dalibor Rak
 */
@SuppressWarnings("serial")
public class CRegistrationClientRecord implements Serializable {
	private String orgName;
	private Long legacyForm;
	private String idNo;
	private String taxNo;
	private String vatNo;

	private String street;
	private String streetNo;
	private String city;
	private String zip;
	private String country;

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Long getLegalForm() {
		return legacyForm;
	}

	public void setLegacyForm(Long legacyForm) {
		this.legacyForm = legacyForm;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getTaxNo() {
		return taxNo;
	}

	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}

	public String getVatNo() {
		return vatNo;
	}

	public void setVatNo(String vatNo) {
		this.vatNo = vatNo;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNo() {
		return streetNo;
	}

	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
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

	public Long getLegacyForm() {
		return legacyForm;
	}
}
