package sk.qbsw.sed.server.model.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.codelist.CProject;

/**
 * Model mapped to table public.t_tmp_time_sheet_record
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.4.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_tmp_time_sheet_record", sequenceName = "s_tmp_time_sheet_record", allocationSize = 1)
@Table(schema = "public", name = "t_tmp_time_sheet_record")
public class CTmpTimeSheetRecord implements Serializable, ITimeSheetRecord {

	@Id
	@GeneratedValue(generator = "s_tmp_time_sheet_record", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "fk_client", nullable = false)
	private Long clientId;

	@Column(name = "fk_user_owner", nullable = false)
	private Long ownerId;

	@Column(name = "fk_user_createdby", nullable = false)
	private Long createdById;

	@Column(name = "fk_user_changedby", nullable = false)
	private Long changedById;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

	@Column(name = "c_date_from", nullable = false)
	private Calendar dateFrom;

	@Column(name = "c_date_to", nullable = false)
	private Calendar dateTo;

	@Column(name = "c_date_generate_action", nullable = true)
	private Calendar dateGenerateAction;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_project", nullable = true)
	private CProject project;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_activity", nullable = true)
	private CActivity activity;

	@Column(name = "c_phase", nullable = true)
	private String phase;

	@Column(name = "c_note", nullable = true)
	private String note;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	@Column(name = "c_flag_outside", nullable = false)
	private Boolean outside;

	@Column(name = "c_flag_generated", nullable = false)
	private Boolean generated;

	@Column(name = "c_minutes_value", nullable = true)
	private Long durationInMinutes;

	@Column(name = "c_percent_value", nullable = true)
	private Long durationInPercent;

	@Column(name = "c_summary_minutes_value", nullable = true)
	private Long summaryDurationInMinutes;

	@Column(name = "c_flag_home_office", nullable = false)
	private Boolean homeOffice;

	@Transient
	private Long jiraTimeSpentSeconds;

	public CTmpTimeSheetRecord() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}

	public Long getChangedById() {
		return changedById;
	}

	public void setChangedById(Long changedById) {
		this.changedById = changedById;
	}

	public Calendar getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Calendar changeTime) {
		this.changeTime = changeTime;
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

	public Calendar getDateGenerateAction() {
		return dateGenerateAction;
	}

	public void setDateGenerateAction(Calendar dateGenerateAction) {
		this.dateGenerateAction = dateGenerateAction;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Boolean getOutside() {
		return outside;
	}

	public void setOutside(Boolean outside) {
		this.outside = outside;
	}

	public Long getDurationInMinutes() {
		return durationInMinutes;
	}

	public void setDurationInMinutes(Long durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}

	public Long getSummaryDurationInMinutes() {
		return summaryDurationInMinutes;
	}

	public void setSummaryDurationInMinutes(Long summaryDurationInMinutes) {
		this.summaryDurationInMinutes = summaryDurationInMinutes;
	}

	public Boolean getGenerated() {
		return generated;
	}

	public void setGenerated(Boolean generated) {
		this.generated = generated;
	}

	public Long getJiraTimeSpentSeconds() {
		return jiraTimeSpentSeconds;
	}

	public void setJiraTimeSpentSeconds(Long jiraTimeSpentSeconds) {
		this.jiraTimeSpentSeconds = jiraTimeSpentSeconds;
	}

	public Long getDurationInPercent() {
		return durationInPercent;
	}

	public void setDurationInPercent(Long durationInPercent) {
		this.durationInPercent = durationInPercent;
	}

	public CProject getProject() {
		return project;
	}

	public void setProject(CProject project) {
		this.project = project;
	}

	public CActivity getActivity() {
		return activity;
	}

	public void setActivity(CActivity activity) {
		this.activity = activity;
	}

	public Boolean getHomeOffice() {
		return homeOffice;
	}

	public void setHomeOffice(Boolean homeOffice) {
		this.homeOffice = homeOffice;
	}

	public CTmpTimeSheet convert() {
		CTmpTimeSheet record = new CTmpTimeSheet();
		record.setActivityId(activity.getId());
		record.setActivityName(activity.getName());
		record.setChangedById(changedById);
		record.setChangeTime(changeTime.getTime());
		record.setClientId(clientId);
		record.setCreatedById(createdById);
		record.setDateFrom(dateFrom.getTime());
		record.setDateGenerateAction(dateGenerateAction == null ? null : dateGenerateAction.getTime());
		record.setDateTo(dateTo.getTime());
		record.setDurationInMinutes(durationInMinutes);
		record.setGenerated(generated);
		record.setId(id);
		record.setNote(note);
		record.setOutside(outside);
		record.setHomeOffice(homeOffice);
		record.setOwnerId(ownerId);
		record.setPhase(phase);
		record.setProjectId(project.getId());
		record.setProjectName(project.getName());
		record.setSummaryDurationInMinutes(summaryDurationInMinutes);
		record.setValid(valid);
		record.setJiraTimeSpentSeconds(jiraTimeSpentSeconds);
		record.setDurationInPercent(durationInPercent);
		return record;
	}
}
