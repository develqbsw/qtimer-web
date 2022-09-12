package sk.qbsw.sed.page;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.requests.CRequestTableContentPanel;

/**
 * SubPage: /requests SubPage title: Žiadosti
 */
@AuthorizeInstantiation({ IUserTypeCode.EMPLOYEE, IUserTypeCode.ORG_ADMIN })
@MountPath(CRequestPage.PATH_SEGMENT)
public class CRequestPage extends AAuthenticatedPage {

	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "requests";

	public CRequestPage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CRequestTableContentPanel(id, getPageTitleSmall(), getRemainingVacationLabel()));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
