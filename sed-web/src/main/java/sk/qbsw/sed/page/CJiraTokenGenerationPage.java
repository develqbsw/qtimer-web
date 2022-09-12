package sk.qbsw.sed.page;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.jira.token.generation.CJiraTokenGenerationContentPanel;

/**
 * SubPage: /jiraTokenGeneration SubPage title: Generovanie JIRA tokenu
 */
@AuthorizeInstantiation({ IUserTypeCode.EMPLOYEE })
@MountPath(CJiraTokenGenerationPage.PATH_SEGMENT)
public class CJiraTokenGenerationPage extends AAuthenticatedPage {

	private static final long serialVersionUID = -6024883453220231380L;

	public static final String PATH_SEGMENT = "jiraTokenGeneration";

	public CJiraTokenGenerationPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CJiraTokenGenerationContentPanel(id));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
	
}
