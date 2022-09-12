package sk.qbsw.sed.page.home;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.stats.CMultiStatsPanel;

/**
 * SubPage: /stats SubPage title: Štatistiky
 */
@AuthorizeInstantiation({ IUserTypeCode.EMPLOYEE, IUserTypeCode.ORG_ADMIN })
@MountPath(CStatsPage.PATH_SEGMENT)
public class CStatsPage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "stats";

	public CStatsPage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CMultiStatsPanel(id, getPageTitleSmall()));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
