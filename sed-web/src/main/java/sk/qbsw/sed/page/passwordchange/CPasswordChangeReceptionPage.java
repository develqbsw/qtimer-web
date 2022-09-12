package sk.qbsw.sed.page.passwordchange;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.passwordchange.CPasswordChangeContentPanel;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /passwordChangeReception
 */
@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(CPasswordChangeReceptionPage.PATH_SEGMENT)
public class CPasswordChangeReceptionPage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "passwordChangeReception";

	public CPasswordChangeReceptionPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		String name = CStringResourceReader.read("string.reception");
		String login = CSedSession.get().getUser().getLogin() + "_r";
		contents.add(new CPasswordChangeContentPanel(id, name, login, true));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
