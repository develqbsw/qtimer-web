package sk.qbsw.sed.common.utils;

import java.io.Serializable;
import java.util.Calendar;

public class CDateRange implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Calendar dateFrom;
	private Calendar dateTo;

	public CDateRange() {
		super();
	}

	public CDateRange(Calendar dateFrom, Calendar dateTo) {
		super();
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public Calendar getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Calendar dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Calendar getDateTo() {
		return dateTo;
	}

	public void setDateTo(Calendar dateTo) {
		this.dateTo = dateTo;
	}
}
