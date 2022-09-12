package sk.qbsw.sed.page.myprojects;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.userprojects.CUserProjectTableContentPanel;

/**
 * SubPage: /userprojects SubPage title: Moje projekty
 */
@AuthorizeInstantiation({ IUserTypeCode.EMPLOYEE })
@MountPath(CUserProjectPage.PATH_SEGMENT)
public class CUserProjectPage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "userprojects";

	public CUserProjectPage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CUserProjectTableContentPanel(id));

	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}