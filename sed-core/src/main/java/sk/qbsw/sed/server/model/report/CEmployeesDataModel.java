package sk.qbsw.sed.server.model.report;

import java.io.Serializable;

public class CEmployeesDataModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Long userId;
	String empCode;
	String name;
	String identificationNumber;
	String crn;
	String vatin;
	String birthDate;
	String description;
	String workStartDate;
	String workEndDate;
	String positionName;
	String title;
	String birthPlace;
	String residentIdentityCardNumber;
	String contactStreet;
	String contactStreetNumber;
	String contactZip;
	String city;
	String healthInsuranceCompany;
	String bankAccountNumber;
	String bankInstitution;
	Integer criminalRecords;
	Integer recMedicalCheck;
	Integer multisportCard;

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

	public Integer getCriminalRecords() {
		return criminalRecords;
	}

	public void setCriminalRecords(Integer criminalRecords) {
		this.criminalRecords = criminalRecords;
	}

	public Integer getRecMedicalCheck() {
		return recMedicalCheck;
	}

	public void setRecMedicalCheck(Integer recMedicalCheck) {
		this.recMedicalCheck = recMedicalCheck;
	}

	public Integer getMultisportCard() {
		return multisportCard;
	}

	public void setMultisportCard(Integer multisportCard) {
		this.multisportCard = multisportCard;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bankAccountNumber == null) ? 0 : bankAccountNumber.hashCode());
		result = prime * result + ((bankInstitution == null) ? 0 : bankInstitution.hashCode());
		result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((birthPlace == null) ? 0 : birthPlace.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((contactStreet == null) ? 0 : contactStreet.hashCode());
		result = prime * result + ((contactStreetNumber == null) ? 0 : contactStreetNumber.hashCode());
		result = prime * result + ((contactZip == null) ? 0 : contactZip.hashCode());
		result = prime * result + ((criminalRecords == null) ? 0 : criminalRecords.hashCode());
		result = prime * result + ((crn == null) ? 0 : crn.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((empCode == null) ? 0 : empCode.hashCode());
		result = prime * result + ((healthInsuranceCompany == null) ? 0 : healthInsuranceCompany.hashCode());
		result = prime * result + ((identificationNumber == null) ? 0 : identificationNumber.hashCode());
		result = prime * result + ((multisportCard == null) ? 0 : multisportCard.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((positionName == null) ? 0 : positionName.hashCode());
		result = prime * result + ((recMedicalCheck == null) ? 0 : recMedicalCheck.hashCode());
		result = prime * result + ((residentIdentityCardNumber == null) ? 0 : residentIdentityCardNumber.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((vatin == null) ? 0 : vatin.hashCode());
		result = prime * result + ((workEndDate == null) ? 0 : workEndDate.hashCode());
		result = prime * result + ((workStartDate == null) ? 0 : workStartDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CEmployeesDataModel other = (CEmployeesDataModel) obj;
		if (bankAccountNumber == null) {
			if (other.bankAccountNumber != null) {
				return false;
			}
		} else if (!bankAccountNumber.equals(other.bankAccountNumber)) {
			return false;
		}
		if (bankInstitution == null) {
			if (other.bankInstitution != null) {
				return false;
			}
		} else if (!bankInstitution.equals(other.bankInstitution)) {
			return false;
		}
		if (birthDate == null) {
			if (other.birthDate != null) {
				return false;
			}
		} else if (!birthDate.equals(other.birthDate)) {
			return false;
		}
		if (birthPlace == null) {
			if (other.birthPlace != null) {
				return false;
			}
		} else if (!birthPlace.equals(other.birthPlace)) {
			return false;
		}
		if (city == null) {
			if (other.city != null) {
				return false;
			}
		} else if (!city.equals(other.city)) {
			return false;
		}
		if (contactStreet == null) {
			if (other.contactStreet != null) {
				return false;
			}
		} else if (!contactStreet.equals(other.contactStreet)) {
			return false;
		}
		if (contactStreetNumber == null) {
			if (other.contactStreetNumber != null) {
				return false;
			}
		} else if (!contactStreetNumber.equals(other.contactStreetNumber)) {
			return false;
		}
		if (contactZip == null) {
			if (other.contactZip != null) {
				return false;
			}
		} else if (!contactZip.equals(other.contactZip)) {
			return false;
		}
		if (criminalRecords == null) {
			if (other.criminalRecords != null) {
				return false;
			}
		} else if (!criminalRecords.equals(other.criminalRecords)) {
			return false;
		}
		if (crn == null) {
			if (other.crn != null) {
				return false;
			}
		} else if (!crn.equals(other.crn)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (empCode == null) {
			if (other.empCode != null) {
				return false;
			}
		} else if (!empCode.equals(other.empCode)) {
			return false;
		}
		if (healthInsuranceCompany == null) {
			if (other.healthInsuranceCompany != null) {
				return false;
			}
		} else if (!healthInsuranceCompany.equals(other.healthInsuranceCompany)) {
			return false;
		}
		if (identificationNumber == null) {
			if (other.identificationNumber != null) {
				return false;
			}
		} else if (!identificationNumber.equals(other.identificationNumber)) {
			return false;
		}
		if (multisportCard == null) {
			if (other.multisportCard != null) {
				return false;
			}
		} else if (!multisportCard.equals(other.multisportCard)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (positionName == null) {
			if (other.positionName != null) {
				return false;
			}
		} else if (!positionName.equals(other.positionName)) {
			return false;
		}
		if (recMedicalCheck == null) {
			if (other.recMedicalCheck != null) {
				return false;
			}
		} else if (!recMedicalCheck.equals(other.recMedicalCheck)) {
			return false;
		}
		if (residentIdentityCardNumber == null) {
			if (other.residentIdentityCardNumber != null) {
				return false;
			}
		} else if (!residentIdentityCardNumber.equals(other.residentIdentityCardNumber)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		if (vatin == null) {
			if (other.vatin != null) {
				return false;
			}
		} else if (!vatin.equals(other.vatin)) {
			return false;
		}
		if (workEndDate == null) {
			if (other.workEndDate != null) {
				return false;
			}
		} else if (!workEndDate.equals(other.workEndDate)) {
			return false;
		}
		if (workStartDate == null) {
			if (other.workStartDate != null) {
				return false;
			}
		} else if (!workStartDate.equals(other.workStartDate)) {
			return false;
		}
		return true;
	}
}
