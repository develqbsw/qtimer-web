package sk.qbsw.sed.client.request;

import java.util.Date;

public class CGetWorkTimeInIntervalRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long userId;

	private Date dateFrom;

	private Date dateTo;

	public CGetWorkTimeInIntervalRequest() {
		super();
	}

	public CGetWorkTimeInIntervalRequest(Long userId, Date dateFrom, Date dateTo) {
		super();
		this.userId = userId;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	@Override
	public Boolean validate() {
		return true;
	}
}
