package sk.qbsw.sed.server.model.report;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "public", name = "v_month_sheet")
public class CViewMonthSheet implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "pk_id")
	private Long id;

	@Column(name = "c_user_id")
	private Long cUserId;

	@Column(name = "c_activity_name")
	private String cActivityName;

	@Column(name = "c_date")
	private Calendar cDate;

	@Column(name = "c_duration")
	private String cDuration;

	@Column(name = "c_etapa_id")
	private String cEtapaId;

	@Column(name = "c_note")
	private String cNote;

	@Column(name = "c_project_name")
	private String cProjectName;

	@Column(name = "c_time_from")
	private Calendar cTimeFrom;

	@Column(name = "c_time_to")
	private Calendar cTimeTo;

	@Column(name = "c_user")
	private String cUser;

	@Column(name = "c_status")
	private Long cStatusId;

	public CViewMonthSheet() {
		// do nothing
	}

	public Long getcUserId() {
		return this.cUserId;
	}

	public void setcUserId(final Long cUserId) {
		this.cUserId = cUserId;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getCActivityName() {
		return this.cActivityName;
	}

	public void setCActivityName(final String cActivityName) {
		this.cActivityName = cActivityName;
	}

	public Calendar getCDate() {
		return this.cDate;
	}

	public void setCDate(final Calendar cDate) {
		this.cDate = cDate;
	}

	public String getCDuration() {
		return this.cDuration;
	}

	public void setCDuration(final String cDuration) {
		this.cDuration = cDuration;
	}

	public String getCEtapaId() {
		return this.cEtapaId;
	}

	public void setCEtapaId(final String cEtapaId) {
		this.cEtapaId = cEtapaId;
	}

	public String getCNote() {
		return this.cNote;
	}

	public void setCNote(final String cNote) {
		this.cNote = cNote;
	}

	public String getCProjectName() {
		return this.cProjectName;
	}

	public void setCProjectName(final String cProjectName) {
		this.cProjectName = cProjectName;
	}

	public Calendar getCTimeFrom() {
		return this.cTimeFrom;
	}

	public void setCTimeFrom(final Calendar cTimeFrom) {
		this.cTimeFrom = cTimeFrom;
	}

	public Calendar getCTimeTo() {
		return this.cTimeTo;
	}

	public void setCTimeTo(final Calendar cTimeTo) {
		this.cTimeTo = cTimeTo;
	}

	public String getCUser() {
		return this.cUser;
	}

	public void setCUser(final String cUser) {
		this.cUser = cUser;
	}

	public Long getCStatusId() {
		return cStatusId;
	}

	public void setCStatusId(Long cStatusId) {
		this.cStatusId = cStatusId;
	}
}
