package sk.qbsw.sed.client.request;

import java.util.Calendar;

public class CGetSumAndAverageTimeRequest extends ARequest {

	private static final long serialVersionUID = 1L;

	private Calendar dateFrom;

	private Calendar dateTo;

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

	@Override
	public Boolean validate() {
		return null;
	}
}
