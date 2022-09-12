package sk.qbsw.sed.panel.timesheet.editable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.column.editable.DropDownChoiceColumn;
import com.inmethod.grid.column.editable.EditablePropertyColumn;

import sk.qbsw.sed.client.model.IActivityConstant;
import sk.qbsw.sed.client.model.IListBoxValueTypes;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.communication.service.IActivityClientService;
import sk.qbsw.sed.communication.service.IBrwTimeStampService;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.component.calendar.CDateRangePicker;
import sk.qbsw.sed.component.calendar.CDateRangePicker.SupportedDefaults;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CRemainingVacationUtils;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.timesheet.CTimesheetContentPanel;
import sk.qbsw.sed.panel.timesheet.CTimesheetValidations;
import sk.qbsw.sed.panel.timesheet.editable.CChooseEmployeesPanel.SupportedPageProperties;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.CDurationPropertyColumn;
import sk.qbsw.sed.web.grid.column.editable.CEditDeleteSubmitCancelColumn;
import sk.qbsw.sed.web.grid.column.editable.CEditableCheckBoxColumn;
import sk.qbsw.sed.web.grid.column.editable.CEditableDateColumn;
import sk.qbsw.sed.web.grid.column.editable.CEditableTimeColumn;
import sk.qbsw.sed.web.grid.column.editable.CSelectActivityColumn;
import sk.qbsw.sed.web.grid.column.editable.CSelectProjectColumn;
import sk.qbsw.sed.web.grid.datasource.CTimestampDataSource;
import sk.qbsw.sed.web.grid.toolbar.CPagingWithTimesheetSummaryToolbar;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.IAjaxCommand;
import sk.qbsw.sed.web.ui.components.panel.CConfirmDialogPanel;
import sk.qbsw.sed.web.ui.components.panel.CMassEditDialogPanel;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;
import sk.qbsw.sed.web.ui.components.panel.ChooseDefaultTimestampFormModel;

/**
 * SubPage: /timesheet SubPage title: Výkaz práce
 * 
 * Panel TimesheetEditableTablePanel - Prehľady (tabuľka)
 */
public class CTimesheetEditableTablePanel extends CPanel {
	private static final long serialVersionUID = 1L;

	// zdroj dát pre tabuľku: public.v_timestamps
	private CSedDataGrid<IDataSource<CTimeStampRecord>, CTimeStampRecord, String> table;

	private Label remainingVacation;

	/** modal confirm dialog */
	private final CModalBorder modal;

	/** content panel for confirm dialog */
	private final CConfirmDialogPanel modalPanel;

	/** modal mass edit dialog */
	private final CModalBorder modalEdit;

	/** content panel for mass timesheet record edit */
	private final CMassEditDialogPanel modalPanelEdit;

	/** error panel */
	private CFeedbackPanel errorPanel;

	private final CSubrodinateTimeStampBrwFilterCriteria viewTimeStampFilter = new CSubrodinateTimeStampBrwFilterCriteria();

	@SpringBean
	private ITimesheetClientService timesheetService;

	@SpringBean
	private IBrwTimeStampService brwTimeStampService;

	@SpringBean
	private IUserClientService userService;

	@SpringBean
	private IActivityClientService activityService;

	private final CTimesheetContentPanel tabPanel;

	private final Label tabTitle;

	private CompoundPropertyModel<CSubrodinateTimeStampBrwFilterCriteria> viewTimeStampFilterModel;

	private FormComponent<CCodeListRecord> projectComponent;
	private FormComponent<CCodeListRecord> activityComponent;
	private FormComponent phaseComponent;
	private boolean workingBefore;

	private IModel<CTimeStampRecord> rowModelToCancel = null;
	private WebMarkupContainer rowComponentToCancel = null;

	private boolean isEditingActions = false;

	private boolean showMassEditButton = true;
	
	private static final String ON_CHANGE = "onchange";

	private static final String TIME_FROM = "timeFrom";

	public boolean isShowMassEditButton() {
		return showMassEditButton;
	}

	public void setShowMassEditButton(boolean showMassEditButton) {
		this.showMassEditButton = showMassEditButton;
	}

	AjaxFallbackLink<Object> timesheetMassEdit = null;

	public AjaxFallbackLink<Object> getTimesheetMassEdit() {
		return timesheetMassEdit;
	}

	public void setTimesheetMassEdit(AjaxFallbackLink<Object> timesheetMassEdit) {
		this.timesheetMassEdit = timesheetMassEdit;
	}

	private boolean fromOtherEmployees = false;

	private CTimesheetConfirmButtonPanel cTimesheetConfirmButtonPanel;

	public boolean getFromOtherEmployees() {
		return this.fromOtherEmployees;
	}

	public void setEditingActions(boolean isEditingActions) {
		this.isEditingActions = isEditingActions;
	}

	public boolean isEditingActions() {
		return isEditingActions;
	}

	public void setFromOtherEmployeess(boolean fromOtherEmployees) {
		this.fromOtherEmployees = fromOtherEmployees;
	}

	public CTimesheetEditableTablePanel(String id, CTimesheetContentPanel tabPanelParam, Label tabTitleParam, final Label pageTitleSmall, List<CCodeListRecord> usersList,
			List<CCodeListRecord> activityList, List<CCodeListRecord> projectList, List<CCodeListRecord> requestReasonList, Label remainingVacation) {
		super(id);
		setOutputMarkupId(true);

		this.tabPanel = tabPanelParam;
		this.tabTitle = tabTitleParam;
		this.remainingVacation = remainingVacation;

		viewTimeStampFilterModel = new CompoundPropertyModel<>(viewTimeStampFilter);

		modal = new CModalBorder("modalWindow");
		modal.setOutputMarkupId(true);
		modal.getTitleModel().setObject(CStringResourceReader.read("timesheet.dialog.delete.title"));
		modalPanel = new CConfirmDialogPanel("content", modal);
		modal.add(modalPanel);
		add(modal);

		modalEdit = new CModalBorder("modalWindowEdit");
		modalEdit.setOutputMarkupId(true);
		modalEdit.getTitleModel().setObject(CStringResourceReader.read("common.title.filtered.massedit"));

		List<CCodeListRecord> workActivityList = null;

		try {
			workActivityList = getWorkingActivities(activityService.getValidRecordsForUser(getuser().getUserId()));
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		modalPanelEdit = new CMassEditDialogPanel("contentEdit", modalEdit, workActivityList, projectList);
		modalPanelEdit.setOutputMarkupId(true);
		modalEdit.add(modalPanelEdit);
		add(modalEdit);

		IModel<List<CCodeListRecord>> choicesModelUsers = new ListModel<>(usersList);
		IModel<List<CCodeListRecord>> choicesModelRequestReason = new ListModel<>(requestReasonList);

		final List<IGridColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String>> columns = new ArrayList<>();

		columns.add(new CEditDeleteSubmitCancelColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String>("esd", Model.of(getString("table.column.actions"))) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitted(AjaxRequestTarget target, IModel<CTimeStampRecord> rowModel, WebMarkupContainer rowComponent) {
				try {
					Long activity = rowModel.getObject().getActivityId();
					Date timeFrom = rowModel.getObject().getTimeFrom();
					Date timeTo = rowModel.getObject().getTimeTo();
					Long reason = rowModel.getObject().getRequestReasonId();

					String errorMsg = CTimesheetValidations.validateActivityDuration(activity, reason, timeFrom, timeTo);
					if (errorMsg != null) {
						error(getString(errorMsg));
						target.add(errorPanel);
						return;
					}

					if (rowModel.getObject().getNote() != null) {
						if (rowModel.getObject().getNote().length() > 1000) {
							getFeedbackPanel().error(CStringResourceReader.read("timestampGenerate.error.note.limit"));
							if (getFeedbackPanel() != null) {
								target.add(getFeedbackPanel());
								return;
							}
						}
					}

					brwTimeStampService.update(rowModel.getObject());

					super.onSubmitted(target, rowModel, rowComponent);
					updateRemainingVacation(target);
					target.add(table);

					rowModelToCancel = null;
					rowComponentToCancel = null;
					isEditingActions = false;
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimesheetEditableTablePanel.this);
				}
				if (getFeedbackPanel() != null) {
					target.add(getFeedbackPanel());
				}
			}

			@Override
			protected void onDelete(AjaxRequestTarget target, final IModel<CTimeStampRecord> rowModel, WebMarkupContainer rowComponent) {
				if (!isEditingActions) {
					modal.getTitleModel().setObject(CStringResourceReader.read("timesheet.dialog.delete.title"));
					modalPanel.setAction(new IAjaxCommand() {
						/** serial uid */
						private static final long serialVersionUID = 1L;

						@Override
						public void execute(AjaxRequestTarget target) {
							try {
								timesheetService.delete(rowModel.getObject().getId());
								updateRemainingVacation(target);
								target.add(table);
								target.add(getFeedbackPanel());
							} catch (CBussinessDataException e) {
								CBussinessDataExceptionProcessor.process(e, target, CTimesheetEditableTablePanel.this);
							}
						}
					});

					modal.show(target);
				}
			}

			@Override
			protected void onCancel(AjaxRequestTarget target, IModel<CTimeStampRecord> rowModel, WebMarkupContainer rowComponent) {
				rowModelToCancel = null;
				rowComponentToCancel = null;
				isEditingActions = false;

				CTimeStampRecord record = null;
				try {
					record = brwTimeStampService.getDetail(rowModel.getObject().getId());
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimesheetEditableTablePanel.this);
				}
				rowModel.setObject(record);
				target.add(rowComponent);

				super.onCancel(target, rowModel, rowComponent);
			}

			@Override
			protected void onEdit(AjaxRequestTarget target, IModel<CTimeStampRecord> rowModel, WebMarkupContainer rowComponent) {
				if (!isEditingActions) {
					if (rowModelToCancel != null) {
						onCancel(target, rowModelToCancel, rowComponentToCancel);
					}
					isEditingActions = true;
					rowModelToCancel = rowModel;
					rowComponentToCancel = rowComponent;

					super.onEdit(target, rowModel, rowComponent);
					workingBefore = rowModel.getObject().getShowProject();

					// toto tu musí byť, aby sa pri kliknutí na edit zobrazili vyhľadávacie polia pre zoznam projektov a aktivít
					// oprava konzolovej chyby query function not defined for Select2 s2id_xxxxx
					target.appendJavaScript("$('.search-select-proj').select2({placeholder: '',allowClear: false});");
					target.appendJavaScript("$('.search-select-act').select2({placeholder: '',allowClear: false});");
				}
			}
		}.setInitialSize(85));

		final DropDownChoiceColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, CCodeListRecord, String> columnEmployee = new DropDownChoiceColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, CCodeListRecord, String>(
				new StringResourceModel("table.column.employee", this, null), "employee", "userSurname", choicesModelUsers) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void addValidators(FormComponent component) {
				component.setRequired(true);
			}
		};
		columns.add(columnEmployee);

		CEditableDateColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String> columnDate = new CEditableDateColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String>(
				new StringResourceModel("table.column.date", this, null), "date", TIME_FROM, new DateConverter(false) {

					@Override
					protected DateTimeFormatter getFormat(Locale paramLocale) {
						return DateTimeFormat.forPattern(CDateUtils.DATE_FORMAT_DD_MM_YYYY);
					}

					@Override
					public String getDatePattern(Locale paramLocale) {
						return CDateUtils.DATE_FORMAT_DD_MM_YYYY;
					}
				}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void addValidators(FormComponent component) {
				component.setRequired(true);
			}
		};
		columnDate.setInitialSize(80);
		columns.add(columnDate);

		CEditableTimeColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String> columnDateFrom = new CEditableTimeColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String>(
				new StringResourceModel("table.column.timeFrom", this, null), TIME_FROM, TIME_FROM, new DateConverter(false) {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected DateTimeFormatter getFormat(Locale paramLocale) {
						return DateTimeFormat.forPattern(CDateUtils.DATE_FORMAT_HH_mm);
					}

					@Override
					public String getDatePattern(Locale paramLocale) {
						return CDateUtils.DATE_FORMAT_HH_mm;
					}
				}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void addValidators(FormComponent component) {
				component.setRequired(true);
			}
		};
		columnDateFrom.setInitialSize(70);
		columns.add(columnDateFrom);

		columns.add(new CEditableTimeColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String>(new StringResourceModel("table.column.timeTo", this, null), "timeTo", "timeTo",
				new DateConverter(false) {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected DateTimeFormatter getFormat(Locale paramLocale) {
						return DateTimeFormat.forPattern(CDateUtils.DATE_FORMAT_HH_mm);
					}

					@Override
					public String getDatePattern(Locale paramLocale) {
						return CDateUtils.DATE_FORMAT_HH_mm;
					}
				}).setInitialSize(70));

		columns.add(new CDurationPropertyColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String, String>(new StringResourceModel("table.column.duration", this, null), "duration", "duration"));

		CSelectProjectColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, CCodeListRecord, String> columnProject = new CSelectProjectColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, CCodeListRecord, String>(
				new StringResourceModel("table.column.project", this, null), "project", "projectName", projectList) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void addValidators(FormComponent<CCodeListRecord> component) {
				CTimesheetEditableTablePanel.this.projectComponent = component;
			}
		};
		columns.add(columnProject);

		// activity
		CSelectActivityColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, CCodeListRecord, String> columnActivity = 
				new CSelectActivityColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, CCodeListRecord, String>(new StringResourceModel("table.column.activity", this, null), "activity", "activityName", activityList) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void addValidators(final FormComponent<CCodeListRecord> component) {
				CTimesheetEditableTablePanel.this.activityComponent = component;

				component.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {

					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						boolean selectedActivityIsWorking = IListBoxValueTypes.WORKING.equals(component.getModelObject().getType());
						boolean showProject = selectedActivityIsWorking || IActivityConstant.NOT_WORK_ALERTNESSWORK.equals(component.getModelObject().getId())
								|| IActivityConstant.NOT_WORK_INTERACTIVEWORK.equals(component.getModelObject().getId());
						if (workingBefore != showProject) {
							// spravím refresh "projectContainer"
							target.add(projectComponent.getParent());

							if (showProject) {
								target.appendJavaScript("$('.search-select-proj').select2({placeholder: '',allowClear: false});");
							}
							
							phaseComponent.setVisible(showProject);
							phaseComponent.setDefaultModelObject(null);
							target.add(phaseComponent);
						}
						workingBefore = showProject;
					}
				});
			}
		};

		columns.add(columnActivity);

		CEditableCheckBoxColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String> columnOutside = 
				new CEditableCheckBoxColumn<>(new StringResourceModel("table.column.outsideWorkplace", this, null), "outsideWorkplace", "flagOutsideWorkplace");
		columnOutside.setInitialSize(80);
		columns.add(columnOutside);

		CEditableCheckBoxColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String> columnHomeOffice = 
				new CEditableCheckBoxColumn<>(new StringResourceModel("table.column.homeOffice", this, null), "homeOffice", "flagHomeOffice");
		columnHomeOffice.setInitialSize(80);
		columns.add(columnHomeOffice);

		EditablePropertyColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String, String> columnPhase = 
				new EditablePropertyColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String, String>(new StringResourceModel("table.column.phase", this, null), "phase", "phase") {
			
			@Override
			protected void addValidators(FormComponent component) {
				CTimesheetEditableTablePanel.this.phaseComponent = component;
				boolean selectedActivityIsWorking = IListBoxValueTypes.WORKING.equals(activityComponent.getModelObject().getType());
				boolean showProject = selectedActivityIsWorking || IActivityConstant.NOT_WORK_ALERTNESSWORK.equals(activityComponent.getModelObject().getId())
						|| IActivityConstant.NOT_WORK_INTERACTIVEWORK.equals(activityComponent.getModelObject().getId());
				component.add(StringValidator.maximumLength(30));
				phaseComponent.setVisible(showProject);
				phaseComponent.setOutputMarkupPlaceholderTag(true);
			}
		};
		columnPhase.setInitialSize(115);
		columns.add(columnPhase);

		columns.add(new EditablePropertyColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String, String>(new StringResourceModel("table.column.note", this, null), "note", "note")
				.setInitialSize(300));

		DropDownChoiceColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, CCodeListRecord, String> columnReason = 
				new DropDownChoiceColumn<>(new StringResourceModel("table.column.reason", this, null), "requestReason", "reasonName", choicesModelRequestReason);
		columnReason.setInitialSize(200);
		columns.add(columnReason);

		columns.add(new PropertyColumn<IDataSource<CTimeStampRecord>, CTimeStampRecord, String, String>(new StringResourceModel("table.column.status", this, null), "status", "status"));

		table = new CSedDataGrid<IDataSource<CTimeStampRecord>, CTimeStampRecord, String>("grid", new CTimestampDataSource(viewTimeStampFilterModel, this), columns) {
			
			@Override
			protected void onDoubleRowClicked(AjaxRequestTarget target, IModel<CTimeStampRecord> rowModel) {
				try {
					// update nadpisu
					tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.detail"));
					target.add(tabTitle);

					// update formulara
					tabPanel.setEntityID(rowModel.getObject().getId());
					tabPanel.setModeDetail(true, rowModel.getObject().getId(), rowModel.getObject().getEmployee());
					tabPanel.clearFeedbackMessages();
					target.add(tabPanel);

					target.add(table);
					
					// prepnutie tabu
					target.appendJavaScript("$('#tab-2').click()");

					// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
					target.appendJavaScript("Main.runCustomCheck();Main.runQTimerCheck();FormElements.init();SedApp.init();");
					
					refreshModalEditPanel(target);
					
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
					getFeedbackPanel().error(getString(e.getModel().getServerCode()));
				}
			}
		};

		CPagingWithTimesheetSummaryToolbar<IDataSource<CTimeStampRecord>, CTimeStampRecord, String> toolbar = new CPagingWithTimesheetSummaryToolbar<>(table, viewTimeStampFilterModel);
		table.addBottomToolbar(toolbar);
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		final Form<CSubrodinateTimeStampBrwFilterCriteria> viewTimeStampFilterForm = new Form<>("filter", viewTimeStampFilterModel);

		viewTimeStampFilterModel.getObject().setDateFrom(new Date());
		viewTimeStampFilterModel.getObject().setDateTo(new Date());

		add(new CChooseEmployeesPanel("chooseEmployees", viewTimeStampFilterModel.getObject(), table, pageTitleSmall, SupportedPageProperties.VYKAZ_PRACE, getFeedbackPanel(), this));

		final WebMarkupContainer date = new CDateRangePicker("date", SupportedDefaults.VYKAZ_PRACE);
		final TextField<String> hiddenDate = new TextField<>("dateInput");
		hiddenDate.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				table.setFirstPage();
				target.add(table);
				target.add(getFeedbackPanel());
			}
		});

		date.add(hiddenDate);

		viewTimeStampFilterForm.add(date);

		List<CCodeListRecord> activityTypeOptions = new ArrayList<>();
		activityTypeOptions.add(new CCodeListRecord(ISearchConstants.ALL, CStringResourceReader.read("timesheet.filter.activity.all")));
		activityTypeOptions.add(new CCodeListRecord(ISearchConstants.ALL_WORKING, CStringResourceReader.read("timesheet.filter.activity.allworking")));
		activityTypeOptions.add(new CCodeListRecord(ISearchConstants.ALL_NON_WORKING, CStringResourceReader.read("timesheet.filter.activity.allnonworking")));

		// ak má používateľ povolenú pohotovosť resp. zásah tak zobrazím vo filtri možnosť Pohotovosť/Zásah
		if (CSedSession.get().getUser().isAllowedAlertnessWork()) {
			activityTypeOptions.add(new CCodeListRecord(ISearchConstants.ALL_ALERTNESS_INTERACT_WORK, CStringResourceReader.read("timesheet.filter.activity.alertnesswork")));
		}

		final Model<CCodeListRecord> activityTypeModel = new Model<>();
		activityTypeModel.setObject(activityTypeOptions.get(1)); // dropdownu sa nastavi defaultna hodnota "Vsetky pracovne" aktivity
		// hodnotu z activityTypeModel nasetujem do viewTimeStampFilterModel, aby sa to prejavilo pri filtrovani
		viewTimeStampFilterModel.getObject().setActivityId(activityTypeModel.getObject().getId());

		final CDropDownChoice<CCodeListRecord> activityTypeChoice = new CDropDownChoice<>("activityId", activityTypeModel, activityTypeOptions);
		activityTypeChoice.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				viewTimeStampFilterModel.getObject().setActivityId(activityTypeModel.getObject().getId());
				table.setFirstPage();
				target.add(table);
				target.add(getFeedbackPanel());
			}
		});
		viewTimeStampFilterForm.add(activityTypeChoice);

		final TextField<String> search = new TextField<>("search", new Model<String>());
		search.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				viewTimeStampFilterModel.getObject().setSearchText(search.getInput());
				table.setFirstPage();
				target.add(table);
				target.add(getFeedbackPanel());
			}
		});
		viewTimeStampFilterForm.add(search);
		search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));

		add(viewTimeStampFilterForm);

		WebMarkupContainer chooseColumns = new WebMarkupContainer("chooseColumns");
		chooseColumns.setOutputMarkupId(true);
		chooseColumns.add(new AttributeAppender("title", getString("common.button.chooseColumns")));

		// vytvorim zoznam stlpcov
		chooseColumns.add(new ListView<Component>("columnComponents", table.getColumnComponentsForHiding()) {
			/**
				 * 
				 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Component> item) {
				item.add(item.getModelObject());
			}
		});

		add(chooseColumns);
		// skryjem stlpce ktore na zaciatku nechcem
		table.showColumn(columnReason, false);
		if (IUserTypeCode.EMPLOYEE.equals(CSedSession.get().getUser().getRoleCode())) {
			// pre employee pri otvoreni obrazovky tento stlpec chcem skryty, pre admina zobrazeny
			table.showColumn(columnEmployee, false);
		}

		add(new CTimesheetGenerateReportButtonPanel("generateReport", viewTimeStampFilter, getFeedbackPanel()));

		this.cTimesheetConfirmButtonPanel = new CTimesheetConfirmButtonPanel("confirmTimesheetRecords", viewTimeStampFilterModel, table, modalPanel, modal, super.getFeedbackPanel());
		add(this.cTimesheetConfirmButtonPanel);

		timesheetMassEdit = new AjaxFallbackLink<Object>("timesheetMassEdit") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				modalPanelEdit.setAction(new IAjaxCommand() {
					/** serial uid */
					private static final long serialVersionUID = 1L;

					@Override
					public void execute(AjaxRequestTarget target) {

						// ak nie sú vybrané Všetky pracovné alebo Pohotovosť/Zásah
						if (!(ISearchConstants.ALL_WORKING.equals(activityTypeChoice.getModelObject().getId()) || ISearchConstants.ALL_ALERTNESS_INTERACT_WORK.equals(activityTypeChoice.getModelObject().getId()))) {
							getFeedbackPanel().error(CStringResourceReader.read("timesheet.masschange.error.msg"));
							target.add(getFeedbackPanel());
						} else {
							try {

								ChooseDefaultTimestampFormModel chooseDefaultTimestampFormModel = modalPanelEdit.getForm().getModelObject();

								if (chooseDefaultTimestampFormModel.getNoteChecked()) {
									if (chooseDefaultTimestampFormModel.getNote() != null) {
										if (chooseDefaultTimestampFormModel.getNote().length() > 1000) {
											getFeedbackPanel().error(CStringResourceReader.read("timestampGenerate.error.note.limit"));
											target.add(getFeedbackPanel());
											return;
										}
									}
								}

								Long numberOfChangedRecords = brwTimeStampService.massChangeTimestamps(viewTimeStampFilter, modalPanelEdit.getForm().getModelObject());

								if (numberOfChangedRecords > 0L) {
									getFeedbackPanel().success(CStringResourceReader.read("timesheet.confirm.records.msg"));
								}
								target.add(getFeedbackPanel());
								target.add(table);
							} catch (CBussinessDataException e) {
								CBussinessDataExceptionProcessor.process(e, target, CTimesheetEditableTablePanel.this);
							}
						}
					}
				});
				target.appendJavaScript("$('.search-select-projects2').select2({placeholder: '',allowClear: false});");
				target.appendJavaScript("$('.search-select-activities2').select2({placeholder: '',allowClear: false});");
				modalEdit.show(target);
			}

			@Override
			public boolean isVisible() {
				return showMassEditButton && !IUserTypeCode.ORG_ADMIN.equals(CSedSession.get().getUser().getRoleCode());
			}
		};
		this.timesheetMassEdit.setOutputMarkupId(true);
		this.timesheetMassEdit.add(new AttributeAppender("title", getString("common.button.filtered.massedit")));
		add(this.timesheetMassEdit);
	}

	public CSedDataGrid<IDataSource<CTimeStampRecord>, CTimeStampRecord, String> getTable() {
		return table;
	}

	/**
	 * update ostavajucich dni dovolenky
	 *
	 * @param target
	 * @throws CBussinessDataException
	 */
	private void updateRemainingVacation(AjaxRequestTarget target) throws CBussinessDataException {
		CUserDetailRecord user = userService.getUserDetails(getuser().getUserId());
		remainingVacation.setDefaultModelObject(CRemainingVacationUtils.getText(user.getVacation()));
		target.add(remainingVacation);
	}

	public CTimesheetConfirmButtonPanel getcTimesheetConfirmButtonPanel() {
		return cTimesheetConfirmButtonPanel;
	}

	public void setcTimesheetConfirmButtonPanel(CTimesheetConfirmButtonPanel cTimesheetConfirmButtonPanel) {
		this.cTimesheetConfirmButtonPanel = cTimesheetConfirmButtonPanel;
	}

	/**
	 * 
	 * From the list of all activities returns list of only working activities
	 */
	private List<CCodeListRecord> getWorkingActivities(List<CCodeListRecord> activityList) {
		List<CCodeListRecord> working = new ArrayList<>();

		for (CCodeListRecord activity : activityList) {
			if (!IListBoxValueTypes.NON_WORKING.equals(activity.getType()))
				working.add(activity);
		}
		return working;
	}

	public CMassEditDialogPanel getModalPanelEdit() {
		return modalPanelEdit;
	}
	
	private void refreshModalEditPanel(AjaxRequestTarget target) {
		
		if (this.modalPanelEdit.getProjectCheckBox().getModelObject().booleanValue()) {
			this.modalPanelEdit.getProjectContainer().setEnabled(true);
			target.add(this.modalPanelEdit.getProjectContainer());
			target.appendJavaScript("$('.search-select-projects2').select2({placeholder: '',allowClear: false});");
		} else {
			this.modalPanelEdit.getProjectContainer().setEnabled(false);
		}
		target.add(this.modalPanelEdit.getProjectContainer());

		if (this.modalPanelEdit.getActivityCheckBox().getModelObject().booleanValue()) {
			this.modalPanelEdit.getActivityContainer().setEnabled(true);
			target.add(this.modalPanelEdit.getActivityContainer());
			target.appendJavaScript("$('.search-select-activities2').select2({placeholder: '',allowClear: false});");
		} else {
			this.modalPanelEdit.getActivityContainer().setEnabled(false);
		}
		target.add(this.modalPanelEdit.getActivityContainer());
	}
}
