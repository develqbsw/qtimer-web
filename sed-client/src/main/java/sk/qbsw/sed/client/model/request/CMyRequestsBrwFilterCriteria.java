package sk.qbsw.sed.client.model.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.common.utils.CRange;

/**
 * Request criteria
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@SuppressWarnings("serial")
public class CMyRequestsBrwFilterCriteria implements IRequestsBrwFilterCriteria {

	/**
	 * State of the request
	 */
	private Long stateId;

	/**
	 * Type of the request
	 */
	private Long typeId;

	private Date dateFrom;

	private Date dateTo;

	@Override
	public Long getStateId() {
		return this.stateId;
	}

	public void setStateId(final Long stateId) {
		this.stateId = stateId;
	}

	@Override
	public Long getTypeId() {
		return this.typeId;
	}

	public void setTypeId(final Long typeId) {
		this.typeId = typeId;
	}

	@Override
	public Date getDateFrom() {
		return this.dateFrom;
	}

	@SuppressWarnings("deprecation")
	public void setDateFrom(final Date dateFrom) {
		this.dateFrom = dateFrom;
		if (null != dateFrom) {
			this.dateFrom.setHours(0);
			this.dateFrom.setMinutes(0);
			this.dateFrom.setSeconds(0);
		}
	}

	@Override
	public Date getDateTo() {
		return this.dateTo;
	}

	@SuppressWarnings("deprecation")
	public void setDateTo(final Date dateTo) {
		this.dateTo = dateTo;
		if (null != dateTo) {
			this.dateTo.setHours(23);
			this.dateTo.setMinutes(59);
			this.dateTo.setSeconds(59);
		}
	}

	public String getDateRange() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return sdf.format(this.dateFrom) + " - " + sdf.format(this.dateTo);
	}

	public String getDateInput() {
		return getDateRange();
	}

	public void setDateInput(final String dateSting) throws ParseException {
		CRange range = CDateUtils.parseRange(dateSting);
		dateFrom = range.getFromDate();
		dateTo = range.getToDate();
	}
}
