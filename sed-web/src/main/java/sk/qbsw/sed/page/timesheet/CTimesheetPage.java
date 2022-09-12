package sk.qbsw.sed.page.timesheet;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.timesheet.CTimesheetTableContentPanel;

/**
 * SubPage: /timesheet SubPage title: Výkaz práce
 */
@AuthorizeInstantiation({ IUserTypeCode.EMPLOYEE, IUserTypeCode.ORG_ADMIN })
@MountPath(CTimesheetPage.PATH_SEGMENT)
public class CTimesheetPage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "timesheet";

	public CTimesheetPage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CTimesheetTableContentPanel(id, getPageTitleSmall(), getRemainingVacationLabel()));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
