package sk.qbsw.sed.panel.timesheet;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.communication.service.IActivityClientService;
import sk.qbsw.sed.communication.service.IProjectClientService;
import sk.qbsw.sed.communication.service.IRequestReasonClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.timesheet.editable.CTimesheetEditableTablePanel;

/**
 * SubPage: /timesheet SubPage title: Výkaz práce
 * 
 * Panel TimesheetTableContentPanel obsahuje panely:
 * 
 * TimesheetEditableTablePanel - Prehľady 
 * TimesheetContentPanel - Pridať / Detail / Editovať
 */
public class CTimesheetTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Label pageTitleSmall;

	@SpringBean
	private IUserClientService userService;

	@SpringBean
	private IActivityClientService activityService;

	@SpringBean
	private IProjectClientService projectService;

	@SpringBean
	private IRequestReasonClientService requestReasonService;

	private Label remainingVacation;

	public CTimesheetTableContentPanel(String id, Label pageTitleSmall, Label remainingVacation) {
		super(id);
		this.pageTitleSmall = pageTitleSmall;
		this.remainingVacation = remainingVacation;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		List<CCodeListRecord> usersList = null;
		List<CCodeListRecord> activityList = null;
		List<CCodeListRecord> projectList = null;
		List<CCodeListRecord> requestReasonList = null;

		try {
			usersList = userService.listSubordinateUsers(Boolean.TRUE, Boolean.TRUE);
			activityList = activityService.getValidRecordsForUser(getuser().getUserId());
			projectList = projectService.getValidRecordsForUserCached(getuser().getUserId());
			requestReasonList = requestReasonService.getReasonListsForListbox();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		LoadableDetachableModel<String> model = new LoadableDetachableModel<String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				return CStringResourceReader.read("tabTitle.new");
			}
		};
		Label tabTitle = new Label("tabTitle", model);
		tabTitle.setOutputMarkupId(true);
		add(tabTitle);

		CTimesheetContentPanel tabPanel = new CTimesheetContentPanel("timesheet", tabTitle, usersList, activityList, projectList, requestReasonList, remainingVacation);
		tabPanel.setOutputMarkupId(true);
		add(tabPanel);

		CTimesheetEditableTablePanel tablePanel = new CTimesheetEditableTablePanel("timesheetTable", tabPanel, tabTitle, pageTitleSmall, usersList, activityList, projectList, requestReasonList, remainingVacation);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
		tabPanel.setPanelToRefresh(tablePanel.getTable(), tablePanel.getFeedbackPanel(), tablePanel.getModalPanelEdit());
	}
}
