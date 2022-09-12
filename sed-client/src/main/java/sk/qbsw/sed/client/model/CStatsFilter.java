package sk.qbsw.sed.client.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.common.utils.CRange;

public class CStatsFilter implements ISubordinateBrwFilterCriteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long activityId;
	private Long projectId;
	private Calendar dateFrom;
	private Calendar dateTo;
	private Set<Long> emplyees;

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
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

	@Override
	public Set<Long> getEmplyees() {
		return emplyees;
	}

	@Override
	public void setEmplyees(Set<Long> emplyees) {
		this.emplyees = emplyees;
	}

	public String getDateRange() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return sdf.format(this.dateFrom.getTime()) + " - " + sdf.format(this.dateTo.getTime());
	}

	public String getDateInput() {
		return getDateRange();
	}

	public void setDateInput(final String dateSting) throws ParseException {
		CRange range = CDateUtils.parseRange(dateSting);
		Calendar from = Calendar.getInstance();
		from.setTime(range.getFromDate());

		Calendar to = Calendar.getInstance();
		to.setTime(range.getToDate());

		dateFrom = from;
		dateTo = to;
	}
}
