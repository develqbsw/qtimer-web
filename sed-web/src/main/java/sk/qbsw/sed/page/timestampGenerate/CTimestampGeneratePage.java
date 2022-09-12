package sk.qbsw.sed.page.timestampGenerate;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.timestampGenerate.CTimestampGenerateContentPanel;

/**
 * SubPage: /timestampGenerate SubPage title: Generovanie výkazu práce
 */
@AuthorizeInstantiation({ IUserTypeCode.EMPLOYEE })
@MountPath(CTimestampGeneratePage.PATH_SEGMENT)
public class CTimestampGeneratePage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "timestampGenerate";

	public CTimestampGeneratePage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CTimestampGenerateContentPanel(id, getPageTitleSmall()));

	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}