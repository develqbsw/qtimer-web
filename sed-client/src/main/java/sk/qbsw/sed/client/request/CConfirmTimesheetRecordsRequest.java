package sk.qbsw.sed.client.request;

import java.util.Date;
import java.util.Set;

public class CConfirmTimesheetRecordsRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String screenType;
	private Set<Long> users;
	private Date dateFrom;
	private Date dateTo;
	private Long userId;
	private boolean alsoEmployees;
	private boolean alsoSuperiors;

	public CConfirmTimesheetRecordsRequest() {
		super();
	}

	public CConfirmTimesheetRecordsRequest(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors) {
		super();
		this.screenType = screenType;
		this.users = users;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.userId = userId;
		this.alsoEmployees = alsoEmployees;
		this.alsoSuperiors = alsoSuperiors;
	}

	public String getScreenType() {
		return screenType;
	}

	public void setScreenType(String screenType) {
		this.screenType = screenType;
	}

	public Set<Long> getUsers() {
		return users;
	}

	public void setUsers(Set<Long> users) {
		this.users = users;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean isAlsoEmployees() {
		return alsoEmployees;
	}

	public void setAlsoEmployees(boolean alsoEmployees) {
		this.alsoEmployees = alsoEmployees;
	}

	public boolean isAlsoSuperiors() {
		return alsoSuperiors;
	}

	public void setAlsoSuperiors(boolean alsoSuperiors) {
		this.alsoSuperiors = alsoSuperiors;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
