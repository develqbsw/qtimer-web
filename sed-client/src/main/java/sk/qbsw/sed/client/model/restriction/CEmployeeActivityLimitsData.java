package sk.qbsw.sed.client.model.restriction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

/**
 * User activity limits record ( C<->S communication)
 * 
 * @author rosenberg
 * @version 1.6.6.1
 * @since 1.6.6.1
 */
@SuppressWarnings("serial")
public class CEmployeeActivityLimitsData implements Serializable {
	Long employeeId;

	private List<CCodeListRecord> employeeAssignedLimits;

	private List<CCodeListRecord> employeeNotAssignedLimits;

	public CEmployeeActivityLimitsData() {
		employeeAssignedLimits = new ArrayList<>();
		employeeNotAssignedLimits = new ArrayList<>();
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public List<CCodeListRecord> getEmployeeAssignedLimits() {
		return employeeAssignedLimits;
	}

	public void setEmployeeAssignedLimits(List<CCodeListRecord> limits) {
		if (limits != null) {
			this.employeeAssignedLimits.addAll(limits);
		}
	}

	public List<CCodeListRecord> getEmployeeNotAssignedLimits() {
		return employeeNotAssignedLimits;
	}

	public void setEmployeeNotAssignedLimits(List<CCodeListRecord> limits) {
		if (limits != null) {
			this.employeeNotAssignedLimits.addAll(limits);
		}
	}
}
