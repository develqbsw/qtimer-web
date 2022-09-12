package sk.qbsw.sed.page.activitylimits;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.activitylimits.CActivityRGroupTableContentPanel;

/**
 * SubPage: /activityRGroups SubPage title: Limity aktivít - Skupiny obmedzení
 */
@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(CActivityRGroupPage.PATH_SEGMENT)
public class CActivityRGroupPage extends AAuthenticatedPage {
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "activityRGroups";

	/**
	 * Constructor
	 * 
	 * @param parameters
	 */
	public CActivityRGroupPage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CActivityRGroupTableContentPanel(id));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
