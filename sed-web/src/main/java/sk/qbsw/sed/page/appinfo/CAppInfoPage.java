package sk.qbsw.sed.page.appinfo;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.appinfo.CAppInfoPanel;

/**
 * SubPage: /appInfo SubPage title: O aplikácii
 */
@AuthorizeInstantiation({ IUserTypeCode.EMPLOYEE, IUserTypeCode.ORG_ADMIN })
@MountPath(CAppInfoPage.PATH_SEGMENT)
public class CAppInfoPage extends AAuthenticatedPage {
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "appInfo";

	/**
	 * Constructor
	 * 
	 * @param parameters
	 */
	public CAppInfoPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CAppInfoPanel(id, getPageTitleSmall()));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
