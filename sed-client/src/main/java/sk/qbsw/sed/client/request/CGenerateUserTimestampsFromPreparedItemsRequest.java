package sk.qbsw.sed.client.request;

import java.util.Date;

public class CGenerateUserTimestampsFromPreparedItemsRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long userId;

	private Date from;

	private Date to;

	private Long summaryWorkDurationInMinutes;

	public CGenerateUserTimestampsFromPreparedItemsRequest() {
		super();
	}

	public CGenerateUserTimestampsFromPreparedItemsRequest(Long userId, Date from, Date to, Long summaryWorkDurationInMinutes) {
		super();
		this.userId = userId;
		this.from = from;
		this.to = to;
		this.summaryWorkDurationInMinutes = summaryWorkDurationInMinutes;
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

	public Long getSummaryWorkDurationInMinutes() {
		return summaryWorkDurationInMinutes;
	}

	public void setSummaryWorkDurationInMinutes(Long summaryWorkDurationInMinutes) {
		this.summaryWorkDurationInMinutes = summaryWorkDurationInMinutes;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
