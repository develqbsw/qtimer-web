package sk.qbsw.sed.client.model.timestamp;

import java.util.Date;
import java.util.List;

import sk.qbsw.sed.client.model.table.IFilterCriteria;

/**
 * Employees status filter criteria
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.0
 * 
 */
@SuppressWarnings("serial")
public class CEmployeesStatusBrwFilterCriteria implements IFilterCriteria {
	private Date date;
	private Boolean atWorkplace;
	private Long zoneId;
	private List<Long> userIds;

	public Boolean getAtWorkplace() {
		return atWorkplace;
	}

	public void setAtWorkplace(Boolean atWorkplace) {
		this.atWorkplace = atWorkplace;
	}

	public Date getDate() {
		return date;
	}

	@SuppressWarnings("deprecation")
	public void setDate(Date date) {
		this.date = date;
		this.date.setHours(0);
		this.date.setMinutes(0);
		this.date.setSeconds(0);
	}

	public Long getZoneId() {
		return zoneId;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}
}
