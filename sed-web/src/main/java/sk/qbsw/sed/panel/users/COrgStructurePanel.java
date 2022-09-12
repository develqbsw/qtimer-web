package sk.qbsw.sed.panel.users;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.component.jstree.ITreeNodeSelected;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /org/structure SubPage title: Organizačná štruktúra
 * 
 * Panel OrgStructurePanel obsahuje panely:
 * 
 * - OrgStructureTreePanel - Prehľady - CUserContentPanel - Pridať / Detail /
 * Editovať
 */
public class COrgStructurePanel extends CPanel {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	public COrgStructurePanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		LoadableDetachableModel<String> model = new LoadableDetachableModel<String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				return CStringResourceReader.read("tabTitle.new");
			}
		};

		WebMarkupContainer tab1 = new WebMarkupContainer("tab1");
		tab1.add(new AjaxEventBehavior("onmouseup") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(COrgStructurePanel.this.getFeedbackPanel());
			}

		});
		add(tab1);

		final Label tabTitle = new Label("tabTitle", model);
		tabTitle.setOutputMarkupId(true);
		add(tabTitle);

		final CUserContentPanel userPanel = new CUserContentPanel("userPanel", tabTitle);
		userPanel.registerFeedbackPanel(this.getFeedbackPanel());
		add(userPanel);

		COrgStructureTreePanel treePanel = new COrgStructureTreePanel("treePanel", new ITreeNodeSelected<CViewOrganizationTreeNodeRecord>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void selected(AjaxRequestTarget target, CViewOrganizationTreeNodeRecord object) {
				tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.detail"));
				target.add(tabTitle);
				userPanel.changeUser(target, object);

				userPanel.clearFeedbackMessages();
				target.add(userPanel.getFeedbackPanel());

				target.appendJavaScript("$('#tab-2').click()");
			}
		}, this.getFeedbackPanel());
		treePanel.setOutputMarkupId(true);
		add(treePanel);
		userPanel.setPanelToRefresh(treePanel);
	}
}
