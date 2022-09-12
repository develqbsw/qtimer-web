package sk.qbsw.sed.client.model.timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.common.utils.CRange;

public class CTimeStampGenerateFilterCriteria extends CMyTimesheetGenerateBrwFilterCriteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String duration;
	private String remaining;

	public String getDateRange() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return sdf.format(dateFrom) + " - " + sdf.format(dateTo);
	}

	public String getDateInput() {
		return getDateRange();
	}

	public void setDateInput(final String dateSting) throws ParseException {
		CRange range = CDateUtils.parseRange(dateSting);
		dateFrom = range.getFromDate();
		dateTo = range.getToDate();
	}

	public String getDuration() {
		return duration;
	}

	public String getRemaining() {
		return remaining;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setRemaining(String remaining) {
		this.remaining = remaining;
	}
}
