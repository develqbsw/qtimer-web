package sk.qbsw.sed.client.request;

import java.util.List;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;

/**
 * 
 * @author lobb
 *
 */
public class CSendWarningToPresentedEmployeesRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CLoggedUserRecord user;

	private Long zoneId;

	/*
	 * list of employees which we want to send warning
	 */
	private List<Long> employees;

	public CSendWarningToPresentedEmployeesRequest(CLoggedUserRecord user, Long zoneId, List<Long> employees) {
		super();
		this.user = user;
		this.zoneId = zoneId;
		this.employees = employees;
	}

	public CSendWarningToPresentedEmployeesRequest() {
		super();
	}

	public CSendWarningToPresentedEmployeesRequest(CLoggedUserRecord user, Long zoneId) {
		super();
		this.user = user;
		this.zoneId = zoneId;
	}

	public CLoggedUserRecord getUser() {
		return user;
	}

	public void setUser(CLoggedUserRecord user) {
		this.user = user;
	}

	public Long getZoneId() {
		return zoneId;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public List<Long> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Long> employees) {
		this.employees = employees;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}