package sk.qbsw.sed.page.activitylimits;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.activitylimits.CActivityRIntervalTableContentPanel;

/**
 * SubPage: /activityRInterval SubPage title: Limity aktivít - Obmedzenia
 */
@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(CActivityRestrictionPage.PATH_SEGMENT)
public class CActivityRestrictionPage extends AAuthenticatedPage {
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "activityRInterval";

	/**
	 * Constructor
	 * 
	 * @param parameters
	 */
	public CActivityRestrictionPage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CActivityRIntervalTableContentPanel(id));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
