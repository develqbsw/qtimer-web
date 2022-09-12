package sk.qbsw.sed.web.ui.components.panel;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import sk.qbsw.sed.client.model.ISubordinateBrwFilterCriteria;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.web.ui.tree.CEmployeeTreeExpansion;
import sk.qbsw.sed.web.ui.tree.CEmployeeTreeExpansionModel;
import sk.qbsw.sed.web.ui.tree.CEmployeeTreeProvider;
import sk.qbsw.sed.web.ui.tree.content.CEmployeeTreeCheckedContent;

public class CEmployeesTreeModalPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AbstractTree<CViewOrganizationTreeNodeRecord> tree;

	private CEmployeeTreeProvider provider;

	private final CEmployeeTreeCheckedContent content;

	public CEmployeesTreeModalPanel(String id, final CModalBorder parentWindow, ISubordinateBrwFilterCriteria filter, Panel panelToRefresh, Label pageTitleSmall, String pageTitleSmallString) {
		super(id);
		this.setOutputMarkupId(true);
		provider = new CEmployeeTreeProvider();

		content = new CEmployeeTreeCheckedContent(provider, filter, panelToRefresh, pageTitleSmall, pageTitleSmallString);

		tree = new NestedTree<CViewOrganizationTreeNodeRecord>("tree", provider, new CEmployeeTreeExpansionModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected Component newContentComponent(String id, IModel<CViewOrganizationTreeNodeRecord> model) {
				return content.newContentComponent(id, this, model);
			}

		};

		tree.add(new HumanTheme());
		CEmployeeTreeExpansion.get().expandAll();

		add(tree);
	}

	public void updateProviderSubset() {
		this.content.updateProviderSubset(provider);
	}

	public void initProvider(Boolean onlyValid, Boolean all) {
		this.provider.getAllEmployees(onlyValid, all);
	}

	public void getNotifiedUsers(List<CViewOrganizationTreeNodeRecord> listNotifiedEmployees) {
		this.provider.getNotifiedUsers(listNotifiedEmployees);
	}
}
