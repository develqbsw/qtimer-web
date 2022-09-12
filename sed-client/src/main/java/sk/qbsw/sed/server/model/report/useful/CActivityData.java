package sk.qbsw.sed.server.model.report.useful;

import java.io.Serializable;

/**
 * @author rosenberg
 *
 */
public class CActivityData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4972085068754436645L;
	String activityName;

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
}
