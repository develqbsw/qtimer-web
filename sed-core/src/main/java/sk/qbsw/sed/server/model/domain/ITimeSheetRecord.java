package sk.qbsw.sed.server.model.domain;

public interface ITimeSheetRecord {

	public String getPhase();

	public void setPhase(String phase);

	public String getNote();

	public void setNote(String note);

	public void setJiraTimeSpentSeconds(Long jiraTimeSpentSeconds);
}
