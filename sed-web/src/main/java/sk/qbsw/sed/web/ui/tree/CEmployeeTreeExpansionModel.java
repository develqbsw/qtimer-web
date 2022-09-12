package sk.qbsw.sed.web.ui.tree;

import java.util.Set;

import org.apache.wicket.model.AbstractReadOnlyModel;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;

public class CEmployeeTreeExpansionModel extends AbstractReadOnlyModel<Set<CViewOrganizationTreeNodeRecord>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Set<CViewOrganizationTreeNodeRecord> getObject() {
		return CEmployeeTreeExpansion.get();
	}
}