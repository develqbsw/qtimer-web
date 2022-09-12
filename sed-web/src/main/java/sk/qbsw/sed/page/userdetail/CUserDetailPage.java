package sk.qbsw.sed.page.userdetail;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.userdetail.CUserDetailContentPanel;

/**
 * SubPage: /userDetail SubPage title: Moje detaily
 */
@AuthorizeInstantiation({ IUserTypeCode.EMPLOYEE, IUserTypeCode.ORG_ADMIN })
@MountPath(CUserDetailPage.PATH_SEGMENT)
public class CUserDetailPage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "userDetail";

	public CUserDetailPage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CUserDetailContentPanel(id));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
