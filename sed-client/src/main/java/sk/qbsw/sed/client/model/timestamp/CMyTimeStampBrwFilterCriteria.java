package sk.qbsw.sed.client.model.timestamp;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.common.utils.CRange;

/**
 * Timestamp criteria
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@SuppressWarnings("serial")
public class CMyTimeStampBrwFilterCriteria implements ITimeStampBrwFilterCriteria, Serializable {

	private Long activityId;

	private Long projectId;

	private Date dateFrom;

	private Date dateTo;

	private String searchText;

	@Override
	public Long getActivityId() {
		return this.activityId;
	}

	public void setActivityId(final Long activityId) {
		this.activityId = activityId;
	}

	@Override
	public Long getProjectId() {
		return this.projectId;
	}

	public void setProjectId(final Long projectId) {
		this.projectId = projectId;
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

	@Override
	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
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
