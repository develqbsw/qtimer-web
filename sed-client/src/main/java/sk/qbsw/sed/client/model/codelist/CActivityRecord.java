package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;
import java.util.Date;

/**
 * Client model
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
public class CActivityRecord implements Serializable {
	private Long id;
	private Integer order;
	private String name;
	private Boolean active;
	private Boolean working;
	private String note;
	private String changeName;
	private String changeSurname;
	private Date changeTime;
	private Boolean flagDefault;
	private Boolean changeable;
	private Date timeMin;
	private Date timeMax;
	private Integer hoursMax;
	private Boolean flagExport;
	private Boolean flagSum;

	public String getChangedBy() {
		StringBuilder sb = new StringBuilder();
		if (changeName != null) {
			sb.append(changeName);
			if (changeSurname != null)
				sb.append(" ");
		}
		if (changeSurname != null)
			sb.append(changeSurname);
		return sb.toString();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Integer getOrder() {
		return this.order;
	}

	public void setOrder(final Integer order) {
		this.order = order;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public Boolean getWorking() {
		return this.working;
	}

	public void setWorking(final Boolean working) {
		this.working = working;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public String getChangeName() {
		return this.changeName;
	}

	public void setChangeName(final String changeName) {
		this.changeName = changeName;
	}

	public String getChangeSurname() {
		return this.changeSurname;
	}

	public void setChangeSurname(final String changeSurname) {
		this.changeSurname = changeSurname;
	}

	public Date getChangeTime() {
		return this.changeTime;
	}

	public void setChangeTime(final Date changeTime) {
		this.changeTime = changeTime;
	}

	public Boolean getFlagDefault() {
		return flagDefault;
	}

	public void setFlagDefault(Boolean flagDefault) {
		this.flagDefault = flagDefault;
	}

	public Boolean getChangeable() {
		return changeable;
	}

	public void setChangeable(Boolean changeable) {
		this.changeable = changeable;
	}

	public Date getTimeMin() {
		return timeMin;
	}

	public void setTimeMin(Date timeMin) {
		this.timeMin = timeMin;
	}

	public Date getTimeMax() {
		return timeMax;
	}

	public void setTimeMax(Date timeMax) {
		this.timeMax = timeMax;
	}

	public Integer getHoursMax() {
		return hoursMax;
	}

	public void setHoursMax(Integer hoursMax) {
		this.hoursMax = hoursMax;
	}

	public Boolean getFlagExport() {
		return flagExport;
	}

	public void setFlagExport(Boolean flagExport) {
		this.flagExport = flagExport;
	}

	public Boolean getFlagSum() {
		return flagSum;
	}

	public void setFlagSum(Boolean flagSum) {
		this.flagSum = flagSum;
	}
}
