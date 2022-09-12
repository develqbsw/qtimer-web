package sk.qbsw.sed.client.request;

import java.util.Date;

public class CHomeOfficePermissionRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long userId;
	private Date date;

	/**
	 * constructor
	 * 
	 * @param userId
	 * @param date
	 */
	public CHomeOfficePermissionRequest(Long userId, Date date) {
		super();
		this.userId = userId;
		this.date = date;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
