package sk.qbsw.sed.client.model.request;

import java.util.Set;

import sk.qbsw.sed.client.model.ISubordinateBrwFilterCriteria;

/**
 * Criteria for requests of subordinate users
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
public class CSubordinateRequestsBrwFilterCriteria extends CMyRequestsBrwFilterCriteria implements ISubordinateBrwFilterCriteria {

	private Set<Long> emplyees;

	public Set<Long> getEmplyees() {
		return this.emplyees;
	}

	public void setEmplyees(final Set<Long> emplyees) {
		this.emplyees = emplyees;
	}
}
