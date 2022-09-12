package sk.qbsw.sed.server.model.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * den s kumulativnymi udajmi moze obsahovat niekolko odlisnych aktivit (napr.
 * pvp) treba sem doplnit moznost uchovavat na dany den aj niekolko rozlicnych
 * typov
 * 
 * @author rosenberg
 *
 */
public class CMonthReportDailyRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -83324683337999555L;
	private Long userId;
	private Calendar date;
	private String note;
	private String activityCode;

	/**
	 * work duration in milliseconds
	 */
	private BigDecimal duration;

	/**
	 * Alertness work duration in in milliseconds
	 */
	private BigDecimal workAlertnessDuration;

	/**
	 * Interactive work duration in in milliseconds
	 */
	private BigDecimal workInteractiveDuration;

	private List<CPvPData> pvpData;

	public CMonthReportDailyRecord() {
		this.pvpData = new ArrayList<>();
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * Work duration in milliseconds
	 */
	public BigDecimal getDuration() {
		return this.duration;
	}

	public void setDuration(BigDecimal duration) {
		this.duration = duration;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigDecimal getWorkAlertnessDuration() {
		return workAlertnessDuration;
	}

	public void setWorkAlertnessDuration(BigDecimal workAlertnessDuration) {
		this.workAlertnessDuration = workAlertnessDuration;
	}

	public BigDecimal getWorkInteractiveDuration() {
		return workInteractiveDuration;
	}

	public void setWorkInteractiveDuration(BigDecimal workInteractiveDuration) {
		this.workInteractiveDuration = workInteractiveDuration;
	}

	public List<CPvPData> getPvpData() {
		return pvpData;
	}

	public void setPvpData(List<CPvPData> pvpData) {
		this.pvpData = pvpData;
	}

	@Override
	public int hashCode() {
		return hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final CMonthReportDailyRecord other = (CMonthReportDailyRecord) obj;
		if (this.userId != null && other.userId != null && this.date != null && other.date != null && this.userId.equals(other.userId) && this.date.equals(other.date)) {
			// the same user and day
			return true;
		} else {
			// other user or day
			return false;
		}

	}

	public Boolean containsPvPActivities() {
		return !this.pvpData.isEmpty();
	}
}
