package sk.qbsw.sed.server.model.report;

import java.io.Serializable;

public class CWorkplaceDataModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Long userId;
	String name;
	String contactPhone;
	String officeNumber;
	String zone;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getOfficeNumber() {
		return officeNumber;
	}

	public void setOfficeNumber(String officeNumber) {
		this.officeNumber = officeNumber;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contactPhone == null) ? 0 : contactPhone.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((officeNumber == null) ? 0 : officeNumber.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((zone == null) ? 0 : zone.hashCode());
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
		CWorkplaceDataModel other = (CWorkplaceDataModel) obj;
		if (contactPhone == null) {
			if (other.contactPhone != null) {
				return false;
			}
		} else if (!contactPhone.equals(other.contactPhone)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (officeNumber == null) {
			if (other.officeNumber != null) {
				return false;
			}
		} else if (!officeNumber.equals(other.officeNumber)) {
			return false;
		}
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		if (zone == null) {
			if (other.zone != null) {
				return false;
			}
		} else if (!zone.equals(other.zone)) {
			return false;
		}
		return true;
	}
}
