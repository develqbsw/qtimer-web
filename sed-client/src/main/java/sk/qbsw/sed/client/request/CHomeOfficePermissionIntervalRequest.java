package sk.qbsw.sed.client.request;

import java.util.Date;

public class CHomeOfficePermissionIntervalRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long userId;
	private Long clientId;
	private Date dateFrom;
	private Date dateTo;

	/**
	 * constructor
	 * 
	 * @param userId
	 * @param dateFrom
	 * @param dateTo
	 */
	public CHomeOfficePermissionIntervalRequest(Long userId, Long clientId, Date dateFrom, Date dateTo) {
		super();
		this.userId = userId;
		this.clientId = clientId;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
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
