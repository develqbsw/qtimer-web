package sk.qbsw.sed.client.request;

import java.util.Calendar;
import java.util.Set;

public class CGenerateReportRequest extends ARequest {

	private static final long serialVersionUID = 1L;

	private Set<Long> userIds;

	private Long reportType;

	private Calendar dateFrom;

	private Calendar dateTo;

	private boolean alsoNotConfirmed;

	private String screenType;

	public CGenerateReportRequest() {
		super();
	}

	public CGenerateReportRequest(Set<Long> userIds, Long reportType, Calendar dateFrom, Calendar dateTo, boolean alsoNotConfirmed, String screenType) {
		super();
		this.userIds = userIds;
		this.reportType = reportType;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.alsoNotConfirmed = alsoNotConfirmed;
		this.screenType = screenType;
	}

	public Set<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(Set<Long> userIds) {
		this.userIds = userIds;
	}

	public Long getReportType() {
		return reportType;
	}

	public void setReportType(Long reportType) {
		this.reportType = reportType;
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

	public boolean isAlsoNotConfirmed() {
		return alsoNotConfirmed;
	}

	public void setAlsoNotConfirmed(boolean alsoNotConfirmed) {
		this.alsoNotConfirmed = alsoNotConfirmed;
	}

	public String getScreenType() {
		return screenType;
	}

	public void setScreenType(String screenType) {
		this.screenType = screenType;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
