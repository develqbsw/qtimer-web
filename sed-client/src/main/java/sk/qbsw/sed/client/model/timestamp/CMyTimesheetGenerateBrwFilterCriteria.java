package sk.qbsw.sed.client.model.timestamp;

import java.util.Date;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

/**
 * 
 * @author rosenberg
 *
 */
@SuppressWarnings("serial")
public class CMyTimesheetGenerateBrwFilterCriteria implements IMyTimesheetGenerateBrwFilterCriteria {

	protected Date dateFrom;

	protected Date dateTo;

	private String dataInputType;

	private Boolean validData;

	private Long summaryDurationInMinutes;

	private boolean generateFromJira;

	private CCodeListRecord defaultActivity;

	private Boolean defaultOutside;

	private Boolean defaultHomeOffice;

	private boolean jiraKeyToPhase;

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public void setDataInputType(String dataInputType) {
		this.dataInputType = dataInputType;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public String getDataInputType() {
		return dataInputType;
	}

	public Boolean getValidData() {
		return validData;
	}

	public void setValidData(Boolean validData) {
		this.validData = validData;
	}

	public Long getSummaryDurationInMinutes() {
		return summaryDurationInMinutes;
	}

	public void setSummaryDurationInMinutes(Long summaryDurationInMinutes) {
		this.summaryDurationInMinutes = summaryDurationInMinutes;
	}

	public boolean getGenerateFromJira() {
		return generateFromJira;
	}

	public void setGenerateFromJira(boolean generateFromJira) {
		this.generateFromJira = generateFromJira;
	}

	public CCodeListRecord getDefaultActivity() {
		return defaultActivity;
	}

	public void setDefaultActivity(CCodeListRecord defaultActivity) {
		this.defaultActivity = defaultActivity;
	}

	public Boolean getDefaultOutside() {
		return defaultOutside;
	}

	public void setDefaultOutside(Boolean defaultOutside) {
		this.defaultOutside = defaultOutside;
	}

	public boolean isJiraKeyToPhase() {
		return jiraKeyToPhase;
	}

	public void setJiraKeyToPhase(boolean jiraKeyToPhase) {
		this.jiraKeyToPhase = jiraKeyToPhase;
	}

	public Boolean getDefaultHomeOffice() {
		return defaultHomeOffice;
	}

	public void setDefaultHomeOffice(Boolean defaultHomeOffice) {
		this.defaultHomeOffice = defaultHomeOffice;
	}
}
