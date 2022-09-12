package sk.qbsw.sed.page.holidays;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.holidays.CHolidayTableContentPanel;

/**
 * SubPage: /holidays SubPage title: Sviatky
 */
@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(CHolidayPage.PATH_SEGMENT)
public class CHolidayPage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "holidays";

	public CHolidayPage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CHolidayTableContentPanel(id));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
