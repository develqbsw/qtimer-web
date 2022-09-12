package sk.qbsw.sed.server.model.report.useful;

import java.io.Serializable;

/**
 * Used for output XLS reports
 * 
 * @author rosenberg
 *
 */
public class CUserData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1310025395201285556L;

	String employeeName;

	/**
	 * the code of employee (for selected client)
	 */
	String employeeId;

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
}
