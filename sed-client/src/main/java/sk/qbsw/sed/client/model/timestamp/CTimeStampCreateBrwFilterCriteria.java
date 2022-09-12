package sk.qbsw.sed.client.model.timestamp;

import java.util.Date;

import sk.qbsw.sed.client.model.table.IFilterCriteria;

/**
 * Timestamp filter criteria
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@SuppressWarnings("serial")
public class CTimeStampCreateBrwFilterCriteria implements IFilterCriteria {
	
	private Date date;
	private Long userId;

	public Date getDate() {
		return date;
	}

	@SuppressWarnings("deprecation")
	public void setDate(Date date) {
		this.date = date;
		this.date.setHours(0);
		this.date.setMinutes(0);
		this.date.setSeconds(0);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getDateFrom() {
		return date;
	}

	@SuppressWarnings("deprecation")
	public Date getDateTo() {
		Date retVal = new Date(date.getTime());
		retVal.setHours(23);
		retVal.setMinutes(59);
		retVal.setSeconds(59);

		return retVal;
	}
}
