package sk.qbsw.sed.page.users;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.users.COrgStructurePanel;

/**
 * SubPage: /org/structure SubPage title: Organizačná štruktúra
 */
@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(COrgStructureTreePage.PATH_SEGMENT)
public class COrgStructureTreePage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "org/structure";
	public static final String KEY = "org.structure";
	private COrgStructurePanel orgStructurePanel;

	public COrgStructureTreePage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		orgStructurePanel = new COrgStructurePanel(id);
		contents.add(orgStructurePanel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		orgStructurePanel.registerFeedbackPanel(getFeedbackPanel());
	}

	@Override
	public String getPageKey() {
		return COrgStructureTreePage.KEY;
	}
}
