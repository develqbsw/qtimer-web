/**
 * 
 */
package sk.qbsw.sed.web.ui.tree;

import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.ProviderSubset;

import sk.qbsw.sed.client.model.ISubordinateBrwFilterCriteria;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;

/**
 * @author podmajersky
 *
 */
public class CCheckedEmployeeTreeProviderSubset extends ProviderSubset<CViewOrganizationTreeNodeRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ITreeProvider<CViewOrganizationTreeNodeRecord> provider;

	public CCheckedEmployeeTreeProviderSubset(ITreeProvider<CViewOrganizationTreeNodeRecord> provider, ISubordinateBrwFilterCriteria filter) {
		super(provider, false);
		this.provider = provider;

		update(filter);
	}

	private void checkChildren(Set<Long> emplyees, CViewOrganizationTreeNodeRecord record) {
		Iterator<? extends CViewOrganizationTreeNodeRecord> children = provider.getChildren(record);
		while (children.hasNext()) {
			CViewOrganizationTreeNodeRecord record2 = children.next();
			if (emplyees.contains(record2.getUserId())) {
				add(record2);
			} else {
				remove(record2);
			}
			checkChildren(emplyees, record2);
		}
	}

	public void update(ISubordinateBrwFilterCriteria filter) {
		Set<Long> emplyees = filter.getEmplyees();
		Iterator<? extends CViewOrganizationTreeNodeRecord> roots = provider.getRoots();
		while (roots.hasNext()) {
			CViewOrganizationTreeNodeRecord record = roots.next();
			if (emplyees.contains(record.getUserId())) {
				add(record);
			} else {
				remove(record);
			}
			checkChildren(emplyees, record);
		}
	}
}
