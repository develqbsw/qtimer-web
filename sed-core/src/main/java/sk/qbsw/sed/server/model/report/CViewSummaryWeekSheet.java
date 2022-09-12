package sk.qbsw.sed.server.model.report;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the v_week_sheet database table.
 * 
 */
@Entity
@Table(schema = "public", name = "v_summary_sheet")
public class CViewSummaryWeekSheet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "pk_id")
	private Long id;

	@Column(name = "c_year")
	private String cYear;

	@Column(name = "c_year_month")
	private String cYearMonth;

	@Column(name = "c_year_week")
	private String cYearWeek;

	@Column(name = "c_user")
	private String cUser;

	@Column(name = "c_project_group")
	private String cProjectGroup;

	@Column(name = "c_project_code")
	private String cProjectCode;

	@Column(name = "c_user_id")
	private Long cUserId;

	@Column(name = "c_user_code")
	private String cUserCode;

	@Column(name = "c_date")
	private Calendar cDate;

	@Column(name = "c_duration")
	private String cDuration;

	@Column(name = "c_activity_name")
	private String cActivityName;

	@Column(name = "c_note")
	private String cNote;

	@Column(name = "c_etapa_id")
	private String cEtapaId;

	@Column(name = "c_project_name")
	private String cProjectName;

	@Column(name = "c_status")
	private Long cStatusId;

	public CViewSummaryWeekSheet() {
		// do nothing
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getcYear() {
		return cYear;
	}

	public void setcYear(String cYear) {
		this.cYear = cYear;
	}

	public String getcYearMonth() {
		return cYearMonth;
	}

	public void setcYearMonth(String cYearMonth) {
		this.cYearMonth = cYearMonth;
	}

	public String getcYearWeek() {
		return cYearWeek;
	}

	public void setcYearWeek(String cYearWeek) {
		this.cYearWeek = cYearWeek;
	}

	public String getcUser() {
		return cUser;
	}

	public void setcUser(String cUser) {
		this.cUser = cUser;
	}

	public String getcProjectGroup() {
		return cProjectGroup;
	}

	public void setcProjectGroup(String cProjectGroup) {
		this.cProjectGroup = cProjectGroup;
	}

	public String getcProjectCode() {
		return cProjectCode;
	}

	public void setcProjectCode(String cProjectCode) {
		this.cProjectCode = cProjectCode;
	}

	public Long getcUserId() {
		return cUserId;
	}

	public void setcUserId(Long cUserId) {
		this.cUserId = cUserId;
	}

	public String getcUserCode() {
		return cUserCode;
	}

	public void setcUserCode(String cUserCode) {
		this.cUserCode = cUserCode;
	}

	public Calendar getcDate() {
		return cDate;
	}

	public void setcDate(Calendar cDate) {
		this.cDate = cDate;
	}

	public String getcDuration() {
		return cDuration;
	}

	public void setcDuration(String cDuration) {
		this.cDuration = cDuration;
	}

	public String getcActivityName() {
		return cActivityName;
	}

	public void setcActivityName(String cActivityName) {
		this.cActivityName = cActivityName;
	}

	public String getcNote() {
		return cNote;
	}

	public void setcNote(String cNote) {
		this.cNote = cNote;
	}

	public String getcEtapaId() {
		return cEtapaId;
	}

	public void setcEtapaId(String cEtapaId) {
		this.cEtapaId = cEtapaId;
	}

	public String getcProjectName() {
		return cProjectName;
	}

	public void setcProjectName(String cProjectName) {
		this.cProjectName = cProjectName;
	}

	public Long getcStatusId() {
		return cStatusId;
	}

	public void setcStatusId(Long cStatusId) {
		this.cStatusId = cStatusId;
	}
}
