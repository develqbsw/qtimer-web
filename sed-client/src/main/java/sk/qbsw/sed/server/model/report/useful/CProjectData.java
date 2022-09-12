package sk.qbsw.sed.server.model.report.useful;

import java.io.Serializable;

/**
 * Used for output XLS reports
 * 
 * @author rosenberg
 *
 */
public class CProjectData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1603572930635463256L;

	String prjFinder;

	String prjName;

	String prjGroup;

	String prjId;

	public String getPrjFinder() {
		return prjFinder;
	}

	public void setPrjFinder(String prjFinder) {
		this.prjFinder = prjFinder;
	}

	public String getPrjName() {
		return prjName;
	}

	public void setPrjName(String prjName) {
		this.prjName = prjName;
	}

	public String getPrjGroup() {
		return prjGroup;
	}

	public void setPrjGroup(String prjGroup) {
		this.prjGroup = prjGroup;
	}

	public String getPrjId() {
		return prjId;
	}

	public void setPrjId(String prjId) {
		this.prjId = prjId;
	}
}
