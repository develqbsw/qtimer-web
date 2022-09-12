package sk.qbsw.sed.page.passwordchange;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.passwordchange.CPasswordChangeContentPanel;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /passwordChangeReception SubPage title: Zmena hesla - recepcia
 */
@AuthorizeInstantiation({ IUserTypeCode.EMPLOYEE, IUserTypeCode.ORG_ADMIN })
@MountPath(CPasswordChangePage.PATH_SEGMENT)
public class CPasswordChangePage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "passwordChange";

	public CPasswordChangePage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		String name = CSedSession.get().getUser().getName() + " " + CSedSession.get().getUser().getSurname();
		String login = CSedSession.get().getUser().getLogin();
		contents.add(new CPasswordChangeContentPanel(id, name, login, false));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
