package sk.qbsw.sed.page.activitylimits;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.activitylimits.CActivityREmployeeTableContentPanel;

/**
 * SubPage: /activityREmployee SubPage title: Limity aktivít - Priradenie
 */
@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(CActivityREmployeePage.PATH_SEGMENT)
public class CActivityREmployeePage extends AAuthenticatedPage {
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "activityREmployee";

	/**
	 * Constructor
	 * 
	 * @param parameters
	 */
	public CActivityREmployeePage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CActivityREmployeeTableContentPanel(id));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
