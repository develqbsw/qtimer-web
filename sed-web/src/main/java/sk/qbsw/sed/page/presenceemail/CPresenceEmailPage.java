package sk.qbsw.sed.page.presenceemail;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.presenceemail.CPresenceEmailPanel;

/**
 * SubPage: /presenceEmail SubPage title: Email o dochádzke
 */
@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(CPresenceEmailPage.PATH_SEGMENT)
public class CPresenceEmailPage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "presenceEmail";
	
	private CPresenceEmailPanel presenceEmailPanel;

	public CPresenceEmailPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		presenceEmailPanel = new CPresenceEmailPanel(id, getPageTitleSmall());
		contents.add(presenceEmailPanel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		presenceEmailPanel.registerFeedbackPanel(this.getFeedbackPanel());
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
