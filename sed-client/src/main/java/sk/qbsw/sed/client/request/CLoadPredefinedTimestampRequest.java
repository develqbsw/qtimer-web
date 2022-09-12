package sk.qbsw.sed.client.request;

import java.util.Date;

public class CLoadPredefinedTimestampRequest extends ARequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long userId;

	private Boolean forSubordinateEmployee;

	private Date timeToPredefine;

	public CLoadPredefinedTimestampRequest() {
		super();
	}

	public CLoadPredefinedTimestampRequest(Long userId, Date timeToPredefine) {
		super();
		this.userId = userId;
		this.timeToPredefine = timeToPredefine;
	}

	public CLoadPredefinedTimestampRequest(Long userId, Boolean forSubordinateEmployee) {
		super();
		this.userId = userId;
		this.forSubordinateEmployee = forSubordinateEmployee;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getForSubordinateEmployee() {
		return forSubordinateEmployee;
	}

	public void setForSubordinateEmployee(Boolean forSubordinateEmployee) {
		this.forSubordinateEmployee = forSubordinateEmployee;
	}

	public Date getTimeToPredefine() {
		return timeToPredefine;
	}

	public void setTimeToPredefine(Date timeToPredefine) {
		this.timeToPredefine = timeToPredefine;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
