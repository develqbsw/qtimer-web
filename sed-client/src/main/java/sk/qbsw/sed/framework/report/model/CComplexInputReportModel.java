package sk.qbsw.sed.framework.report.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sk.qbsw.sed.server.model.report.useful.CActivityData;
import sk.qbsw.sed.server.model.report.useful.CProjectData;
import sk.qbsw.sed.server.model.report.useful.CUserData;

public class CComplexInputReportModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8782640377460891557L;

	private Long clientId;

	// ciselnik projektov
	private List<CProjectData> projectsData = new ArrayList<>();

	// ciselnik projektov
	private List<CActivityData> activitiesData = new ArrayList<>();

	// ciselnik zamestnancov
	private List<CUserData> usersData = new ArrayList<>();

	// for all reports
	private List<CReportModel> reportRows = new ArrayList<>();

	// specific for month report
	/**
	 * name of employee
	 */
	private String reportName;

	/**
	 * report for the month
	 */
	private String month;

	/**
	 * report for the year
	 */
	private String year;

	/**
	 * start date of the report interval
	 */
	private String dateFrom;

	/**
	 * end date of the report interval
	 */
	private String dateTo;

	private Map<Long, CSummaryValues> userSummaryParameters = new LinkedHashMap<>();

	private int workingDays;

	public List<CReportModel> getReportRows() {
		return reportRows;
	}

	public void setReportRows(List<CReportModel> reportRows) {
		this.reportRows = reportRows;
	}

	public List<CProjectData> getProjectsData() {
		return projectsData;
	}

	public void setProjectsData(List<CProjectData> projectsData) {
		this.projectsData = projectsData;
	}

	public List<CUserData> getUsersData() {
		return usersData;
	}

	public void setUsersData(List<CUserData> usersData) {
		this.usersData = usersData;
	}

	public List<CActivityData> getActivitiesData() {
		return activitiesData;
	}

	public void setActivitiesData(List<CActivityData> activitiesData) {
		this.activitiesData = activitiesData;
	}

	/**
	 * name of employee
	 */
	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * report for the month
	 */
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * report for the year
	 */
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Map<Long, CSummaryValues> getUserSummaryParameters() {
		return userSummaryParameters;
	}

	public void setUserSummaryParameters(Map<Long, CSummaryValues> userSummaryParameters) {
		this.userSummaryParameters = userSummaryParameters;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public int getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(int workingDays) {
		this.workingDays = workingDays;
	}
}
