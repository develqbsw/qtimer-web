package sk.qbsw.sed.server.model.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

public class CShortlyReportDataModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4504352856992293673L;

	private Long userId;
	private String projectName;
	private String etapaId;
	private String userName;
	private String activityName;
	private BigDecimal timeInPercent;
	private BigDecimal time;
	private Calendar date;

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(final String projectName) {
		this.projectName = projectName;
	}

	public String getEtapaId() {
		return this.etapaId;
	}

	public void setEtapaId(final String etapaId) {
		this.etapaId = etapaId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getActivityName() {
		return this.activityName;
	}

	public void setActivityName(final String activityName) {
		this.activityName = activityName;
	}

	public BigDecimal getTimeInPercent() {
		return this.timeInPercent;
	}

	public void setTimeInPercent(final BigDecimal timeInPercent) {
		this.timeInPercent = timeInPercent;
	}

	public BigDecimal getTime() {
		return this.time;
	}

	public void setTime(final BigDecimal time) {
		this.time = time;
	}

	public Calendar getDate() {
		return this.date;
	}

	public void setDate(final Calendar date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.activityName == null) ? 0 : this.activityName.hashCode());
		result = prime * result + ((this.etapaId == null) ? 0 : this.etapaId.hashCode());
		result = prime * result + ((this.userId == null) ? 0 : this.userId.hashCode());
		result = prime * result + ((this.projectName == null) ? 0 : this.projectName.hashCode());
		result = prime * result + ((this.time == null) ? 0 : this.time.hashCode());
		result = prime * result + ((this.timeInPercent == null) ? 0 : this.timeInPercent.hashCode());
		result = prime * result + ((this.userName == null) ? 0 : this.userName.hashCode());
		result = prime * result + ((this.date == null) ? 0 : this.date.hashCode());
		return result;
	}

	/**
	 * Tento equals je modifikovany...
	 */
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
		final CShortlyReportDataModel other = (CShortlyReportDataModel) obj;
		if (this.etapaId == null) {
			if (other.etapaId != null) {
				return false;
			}
		} else if (!this.etapaId.equals(other.etapaId)) {
			return false;
		}
		if (this.userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!this.userId.equals(other.userId)) {
			return false;
		}
		if (this.projectName == null) {
			if (other.projectName != null) {
				return false;
			}
		} else if (!this.projectName.equals(other.projectName)) {
			return false;
		}
		if (this.activityName == null) {
			if (other.activityName != null) {
				return false;
			}
		} else if (!this.activityName.equals(other.activityName)) {
			return false;
		}
		if (this.userName == null) {
			if (other.userName != null) {
				return false;
			}
		} else if (!this.userName.equals(other.userName)) {
			return false;
		}
		return true;
	}
}
