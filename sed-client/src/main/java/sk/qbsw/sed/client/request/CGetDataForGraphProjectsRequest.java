package sk.qbsw.sed.client.request;

import java.util.Calendar;

public class CGetDataForGraphProjectsRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Calendar calendarFrom;
	private Calendar calendarTo;

	public CGetDataForGraphProjectsRequest(Calendar calendarFrom, Calendar calendarTo) {
		super();
		this.calendarFrom = calendarFrom;
		this.calendarTo = calendarTo;
	}

	public Calendar getCalendarFrom() {
		return calendarFrom;
	}

	public void setCalendarFrom(Calendar calendarFrom) {
		this.calendarFrom = calendarFrom;
	}

	public Calendar getCalendarTo() {
		return calendarTo;
	}

	public void setCalendarTo(Calendar calendarTo) {
		this.calendarTo = calendarTo;
	}

	@Override
	public Boolean validate() {
		return null;
	}
}
