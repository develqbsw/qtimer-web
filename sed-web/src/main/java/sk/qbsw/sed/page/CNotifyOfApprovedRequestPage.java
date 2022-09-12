package sk.qbsw.sed.page;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.notifyOfApprovedRequest.CNotifyOfApprovedRequestTableContentPanel;

@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(CNotifyOfApprovedRequestPage.PATH_SEGMENT)
public class CNotifyOfApprovedRequestPage extends AAuthenticatedPage {

	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "notifyOfApprovedRequest";

	public CNotifyOfApprovedRequestPage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CNotifyOfApprovedRequestTableContentPanel(id, getPageTitleSmall(), getRemainingVacationLabel()));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
