package sk.qbsw.sed.panel.timesheet.editable;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.inmethod.grid.IDataSource;

import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.model.timestamp.ITimestampScreenType;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.IAjaxCommand;
import sk.qbsw.sed.web.ui.components.panel.CConfirmDialogPanel;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;

public class CTimesheetConfirmButtonPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String TITLE = "title";
	
	private static final String TIMESHEET_DIALOG_CONFIRM = "timesheet.dialog.confirm";

	@SpringBean
	private ITimesheetClientService timesheetService;

	private CompoundPropertyModel<CSubrodinateTimeStampBrwFilterCriteria> viewTimeStampFilterModel;
	private CSedDataGrid<IDataSource<CTimeStampRecord>, CTimeStampRecord, String> table;
	private CConfirmDialogPanel modalPanel;
	private CModalBorder modal;

	public CTimesheetConfirmButtonPanel(String id, CompoundPropertyModel<CSubrodinateTimeStampBrwFilterCriteria> viewTimeStampFilterModel,
			CSedDataGrid<IDataSource<CTimeStampRecord>, CTimeStampRecord, String> table, CConfirmDialogPanel modalPanel, CModalBorder modal, CFeedbackPanel feedback) {
		super(id);
		setOutputMarkupId(true);
		this.viewTimeStampFilterModel = viewTimeStampFilterModel;
		this.table = table;
		this.modalPanel = modalPanel;
		this.modal = modal;
		registerFeedbackPanel(feedback);
		this.add(new AttributeAppender(TITLE, getString("common.button.confirmCancelConfirmation")));

		// add containers to panel
		add(createConfirmOptionsForEmployee());
		add(createConfirmOptionsForSuperior());
		add(createConfirmOptionsForAdmin());
	}

	private WebMarkupContainer createConfirmOptionsForEmployee() {

		final WebMarkupContainer employeeConfirmOptions = new WebMarkupContainer("employeeConfirmOptions") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE) || CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITHOUT_SUB);
			}
		};
		employeeConfirmOptions.setOutputMarkupId(true);

		// link to confirm own timesheet records (1->2)
		AjaxFallbackLink<Object> confirmTimesheetRecordsEmployee = new AjaxFallbackLink<Object>("confirmTimesheetRecordsEmployee") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				IAjaxCommand commandToExectute = getConfirmCommand(ITimestampScreenType.MY_TIMESHEET_SCREEN, false, false);
				showModal(target, commandToExectute, TIMESHEET_DIALOG_CONFIRM);
				target.add(table);
				target.add(employeeConfirmOptions);
			}
		};

		// link to cancel own timesheet records (2->1)
		AjaxFallbackLink<Object> cancelTimesheetRecordsEmployee = new AjaxFallbackLink<Object>("cancelTimesheetRecordsEmployee") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				IAjaxCommand commandToExectute = getCancelCommand(ITimestampScreenType.MY_TIMESHEET_SCREEN, false, false);
				showModal(target, commandToExectute, "timesheet.dialog.cancel");
				target.add(table);
				target.add(employeeConfirmOptions);
			}

		};

		employeeConfirmOptions.add(confirmTimesheetRecordsEmployee);
		employeeConfirmOptions.add(cancelTimesheetRecordsEmployee);

		return employeeConfirmOptions;
	}

	private WebMarkupContainer createConfirmOptionsForSuperior() {

		final WebMarkupContainer superiorConfirmOptions = new WebMarkupContainer("superiorConfirmOptions") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				// Ak employee nema podriadenych toto tlacidlo mu vobec nezobrazime
				return CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITH_SUB);
			}
		};
		superiorConfirmOptions.setOutputMarkupId(true);

		// link to confirm own timesheet records (1 -> 2)
		AjaxFallbackLink<Object> confirmTimesheetRecordsSuperior = new AjaxFallbackLink<Object>("confirmTimesheetRecordsSuperior") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				IAjaxCommand commandToExectute = getConfirmCommand(ITimestampScreenType.MY_TIMESHEET_SCREEN, false, false);
				showModal(target, commandToExectute, TIMESHEET_DIALOG_CONFIRM);
				target.add(superiorConfirmOptions);
			}
		};

		// link to confirm only employees timesheet records (2 -> 3)
		AjaxFallbackLink<Object> confirmTimesheetRecordsSuperiorOnlyEmployees = new AjaxFallbackLink<Object>("confirmTimesheetRecordsSuperiorOnlyEmployees") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				IAjaxCommand commandToExectute = getConfirmCommand(ITimestampScreenType.SUBORDINATE_TIMESHEET_SCREEN, false, false);
				showModal(target, commandToExectute, TIMESHEET_DIALOG_CONFIRM);
				target.add(superiorConfirmOptions);

			}
		};

		// link to confirm own and employees timesheet records (1,2->3)
		AjaxFallbackLink<Object> confirmTimesheetRecordsSuperiorAlsoEmployees = new AjaxFallbackLink<Object>("confirmTimesheetRecordsSuperiorAlsoEmployees") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				IAjaxCommand commandToExectute = getConfirmCommand(ITimestampScreenType.SUBORDINATE_TIMESHEET_SCREEN, true, false);
				showModal(target, commandToExectute, TIMESHEET_DIALOG_CONFIRM);
				target.add(superiorConfirmOptions);
			}
		};

		// link to cancel timesheet records confirmed by superior and confirmed by employee(2,3->1)
		AjaxFallbackLink<Object> cancelTimesheetRecordsSuperior = new AjaxFallbackLink<Object>("cancelTimesheetRecordsSuperior") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				IAjaxCommand commandToExectute = getCancelCommand(ITimestampScreenType.SUBORDINATE_TIMESHEET_SCREEN, true, false);
				showModal(target, commandToExectute, "timesheet.dialog.cancel");
				target.add(superiorConfirmOptions);
			}
		};

		// add tooltips
		confirmTimesheetRecordsSuperiorOnlyEmployees.add(AttributeModifier.replace(TITLE, getString("timesheet.confirmTimesheetRecordsSuperiorOnlyEmployees.tooltip")));
		confirmTimesheetRecordsSuperiorAlsoEmployees.add(AttributeModifier.replace(TITLE, getString("timesheet.confirmTimesheetRecordsSuperiorAlsoEmployees.tooltip")));

		// fill list of options for superior
		superiorConfirmOptions.add(confirmTimesheetRecordsSuperior);
		superiorConfirmOptions.add(confirmTimesheetRecordsSuperiorOnlyEmployees);
		superiorConfirmOptions.add(confirmTimesheetRecordsSuperiorAlsoEmployees);
		superiorConfirmOptions.add(cancelTimesheetRecordsSuperior);

		return superiorConfirmOptions;
	}

	private WebMarkupContainer createConfirmOptionsForAdmin() {
		// admin

		final WebMarkupContainer adminConfirmOptions = new WebMarkupContainer("adminConfirmOptions") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				// Ak employee nema podriadenych toto tlacidlo mu vobec nezobrazime
				return CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN);
			}
		};
		adminConfirmOptions.setOutputMarkupId(true);

		// link to confirm only confirmed by superior (3 -> 4)
		AjaxFallbackLink<Object> confirmTimesheetRecordsAdmin = new AjaxFallbackLink<Object>("confirmTimesheetRecordsAdmin") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				IAjaxCommand commandToExectute = getConfirmCommand(ITimestampScreenType.SUBORDINATE_TIMESHEET_ADMIN_SCREEN, false, false);
				showModal(target, commandToExectute, TIMESHEET_DIALOG_CONFIRM);
				target.add(adminConfirmOptions);
			}
		};

		// link to confirm confirmed by superior and confirmed by employee (2, 3 -> 4)
		AjaxFallbackLink<Object> confirmTimesheetRecordsAdminAlsoSuperior = new AjaxFallbackLink<Object>("confirmTimesheetRecordsAdminAlsoSuperior") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				IAjaxCommand commandToExectute = getConfirmCommand(ITimestampScreenType.SUBORDINATE_TIMESHEET_ADMIN_SCREEN, false, true);
				showModal(target, commandToExectute, TIMESHEET_DIALOG_CONFIRM);
				target.add(adminConfirmOptions);
			}
		};

		// link to confirm confirmed by superior and confirmed by employee and new (1,2,3 -> 4)
		AjaxFallbackLink<Object> confirmTimesheetRecordsAdminAlsoEmployeesAlsoSuperrior = new AjaxFallbackLink<Object>("confirmTimesheetRecordsAdminAlsoEmployeesAlsoSuperrior") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				IAjaxCommand commandToExectute = getConfirmCommand(ITimestampScreenType.SUBORDINATE_TIMESHEET_ADMIN_SCREEN, true, true);
				showModal(target, commandToExectute, TIMESHEET_DIALOG_CONFIRM);
				target.add(adminConfirmOptions);
			}
		};

		// link to cancel timesheet records confirmed by admin, confirmed by superior and confirmed by employee (2,3,4->1)
		AjaxFallbackLink<Object> cancelTimesheetRecordsAdmin = new AjaxFallbackLink<Object>("cancelTimesheetRecordsAdmin") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				IAjaxCommand commandToExectute = getCancelCommand(ITimestampScreenType.SUBORDINATE_TIMESHEET_ADMIN_SCREEN, true, true);
				showModal(target, commandToExectute, "timesheet.dialog.cancel");
				target.add(adminConfirmOptions);
			}
		};

		// add tooltips
		confirmTimesheetRecordsAdmin.add(AttributeModifier.replace(TITLE, getString("timesheet.confirmTimesheetRecordsAdminAlsoEmployees.tooltip")));
		confirmTimesheetRecordsAdminAlsoSuperior.add(AttributeModifier.replace(TITLE, getString("timesheet.confirmTimesheetRecordsAdminAlsoSuperior.tooltip")));
		confirmTimesheetRecordsAdminAlsoEmployeesAlsoSuperrior.add(AttributeModifier.replace(TITLE, getString("timesheet.confirmTimesheetRecordsAdminAlsoEmployeesAlsoSuperrior.tooltip")));

		// fill list of options for admin
		adminConfirmOptions.add(confirmTimesheetRecordsAdmin);
		adminConfirmOptions.add(confirmTimesheetRecordsAdminAlsoSuperior);
		adminConfirmOptions.add(confirmTimesheetRecordsAdminAlsoEmployeesAlsoSuperrior);
		adminConfirmOptions.add(cancelTimesheetRecordsAdmin);

		return adminConfirmOptions;
	}

	private List<Calendar> confirmTimesheet(String screenType, boolean alsoEmployees, boolean alsoSuperiors) throws CBussinessDataException {
		CSubrodinateTimeStampBrwFilterCriteria filter = viewTimeStampFilterModel.getObject();
		Long userId = CSedSession.get().getUser().getUserId();
		return timesheetService.confirmTimesheetRecords(screenType, filter.getEmplyees(), filter.getDateFrom(), filter.getDateTo(), userId, alsoEmployees, alsoSuperiors);
	}

	private void cancelTimesheet(String screenType, boolean alsoEmployees, boolean alsoSuperiors) throws CBussinessDataException {
		CSubrodinateTimeStampBrwFilterCriteria filter = viewTimeStampFilterModel.getObject();
		Long userId = CSedSession.get().getUser().getUserId();

		timesheetService.cancelTimesheetRecords(screenType, filter.getEmplyees(), filter.getDateFrom(), filter.getDateTo(), userId, alsoEmployees, alsoSuperiors);
	}

	private void showModal(AjaxRequestTarget target, final IAjaxCommand commandToExecute, String titleCode) {

		modal.getTitleModel().setObject(CStringResourceReader.read(titleCode));
		modalPanel.setAction(commandToExecute);
		modal.show(target);
	}

	private IAjaxCommand getConfirmCommand(final String screenType, final boolean alsoEmployees, final boolean alsoSuperiors) {
		return new IAjaxCommand() {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void execute(AjaxRequestTarget target) {
				try {
					List<Calendar> res = confirmTimesheet(screenType, alsoEmployees, alsoSuperiors);
					getFeedbackPanel().success(CStringResourceReader.read("timesheet.confirm.msg"));
					if (!res.isEmpty()) {
						for (Calendar c : res) {
							getFeedbackPanel().error(MessageFormat.format(getString("timesheet.date.didntConfirm"), CDateUtils.convertToDateString(c)));
						}
					}
					target.add(table);
					target.add(getFeedbackPanel());
				} catch (CBussinessDataException e) {
					// 
				}
			}
		};
	}

	private IAjaxCommand getCancelCommand(final String screenType, final boolean alsoEmployees, final boolean alsoSuperiors) {
		return new IAjaxCommand() {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void execute(AjaxRequestTarget target) {
				try {
					cancelTimesheet(screenType, alsoEmployees, alsoSuperiors);
					getFeedbackPanel().success(CStringResourceReader.read("timesheet.cancel.msg"));
					target.add(table);
					target.add(getFeedbackPanel());
				} catch (CBussinessDataException e) {
					// 
				}
			}
		};
	}
}
