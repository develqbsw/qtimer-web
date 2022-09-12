package sk.qbsw.sed.client.request;

import java.util.Date;

public class CGetListOfUsersWithCorruptedSummaryReportRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long userId;

	private Date from;

	private Date to;

	public CGetListOfUsersWithCorruptedSummaryReportRequest() {
		super();
	}

	public CGetListOfUsersWithCorruptedSummaryReportRequest(Long userId, Date from, Date to) {
		super();
		this.userId = userId;
		this.from = from;
		this.to = to;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
