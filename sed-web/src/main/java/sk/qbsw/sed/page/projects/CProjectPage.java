package sk.qbsw.sed.page.projects;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.projects.CProjectTableContentPanel;

/**
 * SubPage: /projects SubPage title: Projekty
 */
@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(CProjectPage.PATH_SEGMENT)
public class CProjectPage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "projects";

	public CProjectPage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CProjectTableContentPanel(id));

	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}