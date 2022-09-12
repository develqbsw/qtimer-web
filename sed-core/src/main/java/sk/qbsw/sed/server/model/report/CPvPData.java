package sk.qbsw.sed.server.model.report;

import java.io.Serializable;
import java.math.BigDecimal;

public class CPvPData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2392429218729754110L;

	String activityCode;

	BigDecimal pvp_PhysicianVisit_MinutesDuration;

	BigDecimal pvp_Other_MinutesDuration;

	String note;

	public BigDecimal getPvp_PhysicianVisit_MinutesDuration() {
		return pvp_PhysicianVisit_MinutesDuration;
	}

	public void setPvp_PhysicianVisit_MinutesDuration(BigDecimal pvp_PhysicianVisit_MinutesDuration) {
		this.pvp_PhysicianVisit_MinutesDuration = pvp_PhysicianVisit_MinutesDuration;
	}

	public BigDecimal getPvp_Other_MinutesDuration() {
		return pvp_Other_MinutesDuration;
	}

	public void setPvp_Other_MinutesDuration(BigDecimal pvp_Other_MinutesDuration) {
		this.pvp_Other_MinutesDuration = pvp_Other_MinutesDuration;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
