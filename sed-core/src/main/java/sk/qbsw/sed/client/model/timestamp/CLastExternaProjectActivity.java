package sk.qbsw.sed.client.model.timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author rosenberg
 * @since 1.6.5
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class CLastExternaProjectActivity implements Serializable {
	
	private List<Long> projects;
	private List<Long> activities;

	public CLastExternaProjectActivity() {
		this.projects = new ArrayList<>();
		this.activities = new ArrayList<>();
	}

	public List<Long> getProjects() {
		return projects;
	}

	public List<Long> getActivities() {
		return activities;
	}

	public void setProjects(List<Long> projects) {
		this.projects = projects;
	}

	public void setActivities(List<Long> activities) {
		this.activities = activities;
	}
}
