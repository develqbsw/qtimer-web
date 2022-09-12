package sk.qbsw.sed.panel.timestampGenerate.editable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.editable.EditablePropertyColumn;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CTimeStampGenerateFilterCriteria;
import sk.qbsw.sed.common.utils.CDateRange;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.communication.service.IBrwTimeStampGenerateService;
import sk.qbsw.sed.communication.service.IBrwTimeStampService;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.component.calendar.CDateRangePicker;
import sk.qbsw.sed.component.calendar.CDateRangePicker.SupportedDefaults;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CRadioChoice;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.timestampGenerate.tab.CTimestampGenerateTabPanel;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.CDurationPercentPropertyColumn;
import sk.qbsw.sed.web.grid.column.editable.CEditDeleteSubmitCancelColumn;
import sk.qbsw.sed.web.grid.column.editable.CEditableCheckBoxColumn;
import sk.qbsw.sed.web.grid.column.editable.CSelectActivityColumn;
import sk.qbsw.sed.web.grid.column.editable.CSelectProjectColumn;
import sk.qbsw.sed.web.grid.datasource.CGenerateTimestampDataSource;
import sk.qbsw.sed.web.grid.datasource.CTimestampDataSource;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.IAjaxCommand;
import sk.qbsw.sed.web.ui.components.panel.CConfirmDialogPanel;
import sk.qbsw.sed.web.ui.components.panel.CMassEditDialogPanel;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;

/**
 * SubPage: /timestampGenerate SubPage title: Generovanie výkazu práce
 * 
 * TimestampGenerateEditableTablePanel - Prehľady (tabuľka)
 */
public class CTimestampGenerateEditableTablePanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** modal confirm dialog */
	private CModalBorder modal;

	/** content panel for confirm dialog */
	private CConfirmDialogPanel modalPanel;

	/** modal mass edit dialog */
	private final CModalBorder modalEdit;

	/** content panel for mass timesheet record edit */
	private final CMassEditDialogPanel modalPanelEdit;

	private CRadioChoice<CCodeListRecord> dataInputType;
	private CTextField<String> remaining;
	private CTextField<String> duration;
	private Long totalTime = 0l;

	private CCodeListRecord selected;
	public static final Long DATAINPUTTYPE_PERCENT = 1001l;
	public static final Long DATAINPUTTYPE_DURATION = 1002l;

	@SpringBean
	private ITimesheetClientService timesheetService;

	@SpringBean
	private IBrwTimeStampGenerateService brwTimeStampGenerateService;

	@SpringBean
	private IBrwTimeStampService brwTimeStampService;

	private final CTimeStampGenerateFilterCriteria filter = new CTimeStampGenerateFilterCriteria();
	private CompoundPropertyModel<CTimeStampGenerateFilterCriteria> filterModel;

	// zdroj dát pre tabuľku: public.t_tmp_time_sheet_record
	private CSedDataGrid<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String> table;
	private CTimestampGenerateTabPanel tabPanel;
	private CFeedbackPanel feedbackPanel;
	private Form<CTimeStampGenerateFilterCriteria> filterForm;

	private CDurationPercentPropertyColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String, String> columnDurationPercent;
	private CDurationPercentPropertyColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String, String> columnDurationMinutes;
	private final List<IGridColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String>> columns = new ArrayList<>();

	private boolean allowAction = true;
	private AjaxFallbackLink<Object> generateFromJIRA;

	private CModalBorder defaultTimestampModal;
	private CChooseDefaultTimestampPanel defaultTimestampModalPanel;

	public CTimestampGenerateEditableTablePanel(String id, Label tabTitle, final Label pageTitleSmall, List<CCodeListRecord> activityList, List<CCodeListRecord> projectList) {
		super(id);

		initModal();
		initFeedbackPanel();

		List<CTmpTimeSheet> resultList = null;

		try {
			resultList = brwTimeStampGenerateService.fetch(0, Integer.MAX_VALUE, "id", true, filter);
		} catch (CBussinessDataException e) {
			Logger.getLogger(CTimestampDataSource.class).error(e);
			throw new CSystemFailureException(e);
		}

		if (!resultList.isEmpty()) {
			filter.setDateFrom(resultList.get(0).getDateFrom());
			filter.setDateTo(resultList.get(0).getDateTo());
		} else {
			filter.setDateFrom(new Date());
			filter.setDateTo(new Date());
		}

		add(getForm());
		add(getConfirmLink());
		table = createTable(projectList, activityList);
		add(table);

		if (usingMinutes()) {
			hideColumn(columnDurationPercent);
		}
		if (usingPercent()) {
			hideColumn(columnDurationMinutes);
		}

		generateFromJIRA = new AjaxFallbackLink<Object>("generateFromJIRA") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				defaultTimestampModalPanel.setAction(new IAjaxCommand() {
					/** serial uid */
					private static final long serialVersionUID = 1L;

					@Override
					public void execute(AjaxRequestTarget target) {
						filterModel.getObject().setGenerateFromJira(true);
						target.add(table);

						// update remaining
						target.add(remaining);

						generateFromJIRA.setEnabled(false);
						generateFromJIRA.add(AttributeModifier.append("class", "disabled"));
						target.add(generateFromJIRA);

						setDefaultDurationInForm(target);
					}
				});

				defaultTimestampModalPanel.uncheckHomeOffice();
				target.add(defaultTimestampModalPanel);
				target.appendJavaScript("Main.runCustomCheck();");
				defaultTimestampModal.show(target);
			}

			@Override
			public boolean isVisible() {
				return CSedSession.get().getUserModel().getRecord().getJiraAccessToken() != null;
			}
		};
		generateFromJIRA.add(new AttributeAppender("title", getString("timestampGenerate.button.generateFromJIRA")));
		add(generateFromJIRA);

		defaultTimestampModal = new CModalBorder("defaultTimestampModal");
		defaultTimestampModal.setOutputMarkupId(true);
		defaultTimestampModal.getTitleModel().setObject(CStringResourceReader.read("timestampGenerate.dialog.generate"));
		defaultTimestampModalPanel = new CChooseDefaultTimestampPanel("defaultTimestampModalPanel", defaultTimestampModal, activityList, filterModel);
		defaultTimestampModalPanel.setOutputMarkupId(true);

		defaultTimestampModal.add(defaultTimestampModalPanel);
		add(defaultTimestampModal);

		modalEdit = new CModalBorder("modalWindowEdit");
		modalEdit.setOutputMarkupId(true);
		modalEdit.getTitleModel().setObject(CStringResourceReader.read("common.title.massedit"));
		modalPanelEdit = new CMassEditDialogPanel("contentEdit", modalEdit, activityList, projectList);
		modalPanelEdit.setOutputMarkupId(true);
		modalEdit.add(modalPanelEdit);
		add(modalEdit);
	}

	private void showColumn(IGridColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String> columnToShow) {
		columnToShow.setGrid(table);
		if (!columns.contains(columnToShow)) {
			columns.add(columnToShow);
		}
	}

	private void hideColumn(IGridColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String> columnToHide) {
		columnToHide.setGrid(table);
		columns.remove(columnToHide);
	}

	private void initModal() {
		modal = new CModalBorder("modalWindow");
		modal.setOutputMarkupId(true);
		modalPanel = new CConfirmDialogPanel("content", modal);
		modal.add(modalPanel);
		add(modal);
	}

	private void initFeedbackPanel() {
		this.feedbackPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(feedbackPanel);
		add(feedbackPanel);
	}

	private Form<CTimeStampGenerateFilterCriteria> getForm() {
		filterModel = new CompoundPropertyModel<>(filter);

		filterForm = new Form<>("filter", filterModel);
		add(filterForm);

		CDateRange dateRange = new CDateRange();
		Calendar dateFrom = Calendar.getInstance();
		dateFrom.setTime(filter.getDateFrom());
		dateRange.setDateFrom(dateFrom);
		Calendar dateTo = Calendar.getInstance();
		dateTo.setTime(filter.getDateTo());
		dateRange.setDateTo(dateTo);

		final WebMarkupContainer date = new CDateRangePicker("date", SupportedDefaults.GENERATE, dateRange);
		final TextField<String> hiddenDate = new TextField<>("dateInput");
		hiddenDate.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				generateFromJIRA.setEnabled(true);
				generateFromJIRA.add(AttributeModifier.replace("class", "btn btn-green"));
				target.add(generateFromJIRA);

				// load total minutes
				try {
					allowAction = true;
					getTotalDurationInMinutes();

					if (usingPercent()) {
						recalculateMinutesAccordingToPercentForTableRows();
					} else if (usingMinutes()) {
						recalculatePercentAccordingToMinutesForTableRows();
					}

					// nastav pre kazdy zaznam aktualny dateFrom a dateTo
					setDateFromDatePicker();

					// najprv dotiahni data aby sa podla nich mohol vypocitat zvysok
					target.add(table);

					// update duration
					String durationString = CDateUtils.getMinutesAsString(totalTime);
					duration.getModel().setObject(durationString);
					target.add(duration);

					// update remaining
					target.add(remaining);

					setDefaultDurationInForm(target);

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimestampGenerateEditableTablePanel.this);
				}
			}
		});

		date.add(hiddenDate);

		List<CCodeListRecord> options = new ArrayList<>();
		final CCodeListRecord p = new CCodeListRecord(DATAINPUTTYPE_PERCENT, CStringResourceReader.read("timestampGenerate.form.percent"));
		final CCodeListRecord d = new CCodeListRecord(DATAINPUTTYPE_DURATION, CStringResourceReader.read("timestampGenerate.form.duration"));
		options.add(p);
		options.add(d);

		selected = p;

		dataInputType = new CRadioChoice<CCodeListRecord>("dataInputType", Model.of(p), options) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getPrefix() {
				return "<label class=\"btn btn-default\">";
			}

			@Override
			public String getSuffix() {
				return "</label>";
			}
		};

		dataInputType.add(new AjaxFormChoiceComponentUpdatingBehavior() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				selected = (CCodeListRecord) getComponent().getDefaultModelObject();

				// refreshe zostatok
				target.add(remaining);

				// ukaz spravny stlpec pre duration
				if (usingMinutes()) {
					hideColumn(columnDurationPercent);
					showColumn(columnDurationMinutes);
				} else if (usingPercent()) {
					hideColumn(columnDurationMinutes);
					showColumn(columnDurationPercent);
				}

				target.add(table);

				setDefaultDurationInForm(target);
			}
		});

		IModel<String> durationModel = new Model<>();
		duration = new CTextField<>("duration", durationModel, EDataType.TEXT);
		duration.setEnabled(false);
		duration.setOutputMarkupId(true);

		IModel<String> remainingModel = new Model<>();
		remaining = new CTextField<String>("remaining", remainingModel, EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected String getModelValue() {

				return getRemainingAsString();

			}
		};
		remaining.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// do nothing
			}
		});

		remaining.setEnabled(false);
		remaining.setOutputMarkupId(true);

		// update fields on load
		initDurationAndRemaining(duration, remaining);

		// add form inputs
		filterForm.add(date);
		filterForm.add(dataInputType);
		filterForm.add(duration);
		filterForm.add(remaining);

		return filterForm;
	}

	private CSedDataGrid<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String> createTable(List<CCodeListRecord> projectList, List<CCodeListRecord> activityList) {

		// edit button
		columns.add(new CEditDeleteSubmitCancelColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String>("esd", Model.of(getString("table.column.actions"))) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitted(AjaxRequestTarget target, IModel<CTmpTimeSheet> rowModel, WebMarkupContainer rowComponent) {
				if (totalTime > 0) {
					try {

						if (rowModel.getObject().getNote() != null) {
							if (rowModel.getObject().getNote().length() > 1000) {
								getFeedbackPanel().error(CStringResourceReader.read("timestampGenerate.error.note.limit"));
								target.add(getFeedbackPanel());
								return;
							}
						}

						Long minutes = rowModel.getObject().getDurationInMinutes();
						Long percent = rowModel.getObject().getDurationInPercent();

						// ak sa vyplnali percenta tak to preklop na minuty
						if (usingPercent()) {
							minutes = CDateUtils.getPercentAsMinutes(rowModel.getObject().getDurationInPercent(), totalTime).longValue();
							rowModel.getObject().setDurationInMinutes(minutes);
						}

						// ak sa vyplnali minuty tak to preklop na percenta
						if (usingMinutes()) {
							percent = CDateUtils.getMinutesAsPercent(minutes, totalTime).longValue();
							rowModel.getObject().setDurationInPercent(percent);
						}

						if (rowModel.getObject().getId() == null) {
							// zaznam z Jiry
							CTmpTimeSheet tmp = brwTimeStampGenerateService.add(rowModel.getObject());

							int index = ((CGenerateTimestampDataSource) table.getDataSource()).deleteRow(rowModel.getObject());
							rowModel.setObject(tmp);
							((CGenerateTimestampDataSource) table.getDataSource()).insertRow(index, tmp);
						} else {
							rowModel.setObject(brwTimeStampGenerateService.update(rowModel.getObject()));
						}
						target.add(table);
						target.add(remaining);
						super.onSubmitted(target, rowModel, rowComponent);
						target.add(rowComponent);

						setDefaultDurationInForm(target);

					} catch (CBussinessDataException e) {
						CBussinessDataExceptionProcessor.process(e, target, CTimestampGenerateEditableTablePanel.this);
					}
				}
			}

			@Override
			protected void onDelete(AjaxRequestTarget target, final IModel<CTmpTimeSheet> rowModel, WebMarkupContainer rowComponent) {
				try {
					((CGenerateTimestampDataSource) table.getDataSource()).deleteRow(rowModel.getObject());

					if (rowModel.getObject().getId() != null) {
						brwTimeStampGenerateService.delete(rowModel.getObject());
					}

					target.add(table);
					target.add(remaining);

					setDefaultDurationInForm(target);

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimestampGenerateEditableTablePanel.this);
				}
			}

			@Override
			protected void onCancel(AjaxRequestTarget target, IModel<CTmpTimeSheet> rowModel, WebMarkupContainer rowComponent) {
				super.onCancel(target, rowModel, rowComponent);
				if (getFeedbackPanel() != null) {
					target.add(getFeedbackPanel());
				}
			}

			@Override
			protected void onEdit(AjaxRequestTarget target, IModel<CTmpTimeSheet> rowModel, WebMarkupContainer rowComponent) {
				super.onEdit(target, rowModel, rowComponent);
				target.appendJavaScript("Main.runCustomCheck();");
				
				// toto tu musí byť, aby sa pri kliknutí na edit zobrazili vyhľadávacie polia pre zoznam projektov a aktivít
				// oprava konzolovej chyby query function not defined for Select2 s2id_xxxxx
				target.appendJavaScript("$('.search-select-proj').select2({placeholder: '',allowClear: false});");
				target.appendJavaScript("$('.search-select-act').select2({placeholder: '',allowClear: false});");
			}

		}.setInitialSize(85));

		columnDurationMinutes = new CDurationPercentPropertyColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String, String>(new StringResourceModel("table.column.duration", this, null),
				"durationInMinutesString", this) {

			/**
			* 
			*/
			private static final long serialVersionUID = 1L;

			@Override
			protected void addValidators(FormComponent<String> component) {

				component.setRequired(true);
				component.add(new AttributeModifier("onChange", "parseTimeDuration(this);"));
			}
		};
		columns.add(columnDurationMinutes);

		columnDurationPercent = new CDurationPercentPropertyColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String, String>(new Model<String>("%"), "durationInPercent", this) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void addValidators(FormComponent<String> component) {

				component.setRequired(true);
			}
		};
		columns.add(columnDurationPercent);

		// project col
		final CSelectProjectColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, CCodeListRecord, String> columnProject = 
				new CSelectProjectColumn<>(new StringResourceModel("table.column.project", this, null), "project", projectList);
		columns.add(columnProject);

		// activity col
		final CSelectActivityColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, CCodeListRecord, String> columnActivity = 
				new CSelectActivityColumn<>(new StringResourceModel("table.column.activity", this, null), "activity", activityList);
		columns.add(columnActivity);

		// outside col
		CEditableCheckBoxColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String> columnOutside = 
				new CEditableCheckBoxColumn<>(new StringResourceModel("table.column.outsideWorkplace", this, null), "outside");
		columnOutside.setInitialSize(80);
		columns.add(columnOutside);

		// home office col
		CEditableCheckBoxColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String> columnHomeOffice = 
				new CEditableCheckBoxColumn<>(new StringResourceModel("table.column.homeOffice", this, null), "homeOffice");
		columnHomeOffice.setInitialSize(80);
		columns.add(columnHomeOffice);
		
		// phase col
		columns.add(new EditablePropertyColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String, String>(new StringResourceModel("table.column.phase", this, null), "phase") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void addValidators(FormComponent component) {
				component.add(StringValidator.maximumLength(30));
			}
		}.setInitialSize(185));

		// note col
		columns.add(new EditablePropertyColumn<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String, String>(new StringResourceModel("table.column.note", this, null), "note").setInitialSize(300));

		CGenerateTimestampDataSource tableDatasource = new CGenerateTimestampDataSource(filterModel);

		CSedDataGrid<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String> table = new CSedDataGrid<>("grid", tableDatasource, columns, 100);

		AjaxFallbackLink<Object> timesheetMassEdit = new AjaxFallbackLink<Object>("timesheetMassEdit") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				modalPanelEdit.setAction(new IAjaxCommand() {
					/** serial uid */
					private static final long serialVersionUID = 1L;

					@Override
					public void execute(AjaxRequestTarget target) {

						boolean changeActivity = modalPanelEdit.getForm().getModelObject().getActivityChecked();
						boolean changeProject = modalPanelEdit.getForm().getModelObject().getProjectChecked();
						boolean changePhase = modalPanelEdit.getForm().getModelObject().getPhaseChecked();
						boolean changeNote = modalPanelEdit.getForm().getModelObject().getNoteChecked();

						CCodeListRecord activityValue = modalPanelEdit.getForm().getModelObject().getActivity();
						CCodeListRecord projectValue = modalPanelEdit.getForm().getModelObject().getProject();
						String phaseValue = modalPanelEdit.getForm().getModelObject().getPhase();
						String noteValue = modalPanelEdit.getForm().getModelObject().getNote();

						if (changeNote) {
							if (noteValue != null) {
								if (noteValue.length() > 1000) {
									getFeedbackPanel().error(CStringResourceReader.read("timestampGenerate.error.note.limit"));
									target.add(getFeedbackPanel());
									return;
								}
							}
						}

						List<IModel<CTmpTimeSheet>> rowModels = table.getRowModels();

						if (!changeProject) {

							List<IModel<CTmpTimeSheet>> rowModelsCopyForCheck = new ArrayList<>(rowModels);

							for (Iterator<IModel<CTmpTimeSheet>> iterator = rowModelsCopyForCheck.iterator(); iterator.hasNext();) {

								IModel<CTmpTimeSheet> rowModel = iterator.next();
								CTmpTimeSheet rowModelObject = rowModel.getObject();

								if (rowModelObject.getProjectId() == null) {
									getFeedbackPanel().error(CStringResourceReader.read("timestampGenerate.masschange.project.notnull"));
									target.add(getFeedbackPanel());
									return;
								}
							}
						}
						
						for (Iterator<IModel<CTmpTimeSheet>> iterator = rowModels.iterator(); iterator.hasNext();) {
							IModel<CTmpTimeSheet> rowModel = iterator.next();
							CTmpTimeSheet rowModelObject = rowModel.getObject();

							if (changeActivity) {
								rowModelObject.setActivity(activityValue);
							}

							if (changeProject) {
								rowModelObject.setProject(projectValue);
							}

							if (changePhase) {
								rowModelObject.setPhase(phaseValue);
							}

							if (changeNote) {
								rowModelObject.setNote(noteValue);
							}
							
							try {
								
								if (rowModel.getObject().getId() == null) {
									
									// záznam z browsáku, ktorý ešte nie je uložený na databáze
									CTmpTimeSheet tmp = brwTimeStampGenerateService.add(rowModel.getObject());
	
									int index = ((CGenerateTimestampDataSource) table.getDataSource()).deleteRow(rowModel.getObject());
									
									rowModel.setObject(tmp);
									
									((CGenerateTimestampDataSource) table.getDataSource()).insertRow(index, tmp);
									
								} else {
									rowModel.setObject(brwTimeStampGenerateService.update(rowModel.getObject()));
								}
								
							} catch (CBussinessDataException e) {
								CBussinessDataExceptionProcessor.process(e, target, CTimestampGenerateEditableTablePanel.this);
							}
						}
						target.add(table);
					}
				});
				
				refreshModalEditPanel(target);
				modalEdit.show(target);
			}
		};
		this.add(new AttributeAppender("title", getString("common.button.edit.records")));
		add(timesheetMassEdit);

		return table;
	}

	private AjaxFallbackLink<Object> getConfirmLink() {
		AjaxFallbackLink<Object> confirmLink = new AjaxFallbackLink<Object>("confirm") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				List<IModel<CTmpTimeSheet>> list = table.getRowModels();
				for (IModel<CTmpTimeSheet> item : list) {
					if (item.getObject().getActivityId() == null || item.getObject().getProjectId() == null) {
						getFeedbackPanel().error(CStringResourceReader.read("timestampGenerate.error.noActivity"));
						target.add(getFeedbackPanel());
						return;
					}
				}

				if (getSumOfDurationInMinutesFromTable().equals(0l) || getSumOfDurationInPercentFromTable().equals(0l)) // ak je duration 0
				{
					target.add(getFeedbackPanel());
					getFeedbackPanel().error(CStringResourceReader.read("timestampGenerate.error.totalDurationIs0"));
				} else if ((usingPercent() && !getSumOfDurationInPercentFromTable().equals(100l)) || // ak je viac ako 100%
				(usingMinutes() && !getSumOfDurationInMinutesFromTable().equals(totalTime)) // ak je viac ako totalTime
				) {
					target.add(getFeedbackPanel());
					feedbackPanel.error(CStringResourceReader.read("timestampGenerate.error.durationIsNot100"));
				}

				else {
					IAjaxCommand commandToExectute = getCommandGenerateTimestamps();
					showModal(target, commandToExectute, "timestampGenerate.dialog.confirm");
				}

			}
		};
		confirmLink.add(new AttributeAppender("title", getString("common.button.generate")));
		return confirmLink;
	}

	// GUI LOGIC
	public void initDurationAndRemaining(CTextField<String> duration, CTextField<String> remaining) {
		try {
			getTotalDurationInMinutes();

			// update duration
			String totalDurationString = CDateUtils.getMinutesAsString(totalTime);
			duration.getModel().setObject(totalDurationString);

			// update remaining
			if (totalTime > 0) {
				if (usingPercent()) {
					remaining.getModel().setObject("100%");
				} else if (usingMinutes()) {
					remaining.getModel().setObject(totalDurationString);
				}
			} else {
				remaining.getModel().setObject("");
			}

		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, null, CTimestampGenerateEditableTablePanel.this);
		}
	}

	private void getTotalDurationInMinutes() throws CBussinessDataException {
		Calendar from = Calendar.getInstance();
		from.setTime(filterModel.getObject().getDateFrom());

		Calendar to = Calendar.getInstance();
		to.setTime(filterModel.getObject().getDateTo());

		totalTime = brwTimeStampService.getWorkTimeInInterval(CSedSession.get().getUser().getUserId(), from.getTime(), to.getTime());

		totalTime = totalTime / (1000 * 60);
	}

	private String getRemainingAsString() {

		String retVal = "";

		if (DATAINPUTTYPE_PERCENT.equals(selected.getId())) {
			Long usedPercent = getSumOfDurationInPercentFromTable();

			Long result = 100 - usedPercent;

			retVal = result.toString() + " %";
		} else if (DATAINPUTTYPE_DURATION.equals(selected.getId())) {
			Long usedMinutes = getSumOfDurationInMinutesFromTable();

			retVal = CDateUtils.getMinutesAsString(totalTime - usedMinutes);
		}

		return retVal;
	}

	public Long getSumOfDurationInMinutesFromTable() {

		List<IModel<CTmpTimeSheet>> rowModels = table.getRowModels();

		Long retVal = 0l;

		for (IModel<CTmpTimeSheet> tmp : rowModels) {
			if (tmp.getObject() != null)
				retVal += tmp.getObject().getDurationInMinutes();
		}

		return retVal;
	}

	public Long getSumOfDurationInPercentFromTable() {

		List<IModel<CTmpTimeSheet>> rowModels = table.getRowModels();

		Long retVal = 0l;

		for (IModel<CTmpTimeSheet> tmp : rowModels) {
			if (tmp.getObject() != null)
				retVal += tmp.getObject().getDurationInPercent();
		}

		return retVal;
	}

	private void updateTotaltimeForTableRows() throws CBussinessDataException {

		for (IModel<CTmpTimeSheet> tmp : table.getRowModels()) {
			CTmpTimeSheet t = tmp.getObject();
			if (t.getId() != null) {
				t.setSummaryDurationInMinutes(totalTime);
				brwTimeStampGenerateService.update(t);
			}
		}
	}

	private void recalculateMinutesAccordingToPercentForTableRows() throws CBussinessDataException {

		for (IModel<CTmpTimeSheet> tmp : table.getRowModels()) {
			CTmpTimeSheet t = tmp.getObject();
			if (t.getId() != null) {
				t.setDurationInMinutes(CDateUtils.getPercentAsMinutes(t.getDurationInPercent(), totalTime).longValue());
				brwTimeStampGenerateService.update(t);
			}
		}
	}

	private void recalculatePercentAccordingToMinutesForTableRows() throws CBussinessDataException {

		for (IModel<CTmpTimeSheet> tmp : table.getRowModels()) {
			CTmpTimeSheet t = tmp.getObject();
			if (t.getId() != null) {
				t.setDurationInPercent(CDateUtils.getMinutesAsPercent(t.getDurationInMinutes(), totalTime));
				brwTimeStampGenerateService.update(t);
			}
		}
	}

	private void setDateFromDatePicker() throws CBussinessDataException {

		for (IModel<CTmpTimeSheet> tmp : table.getRowModels()) {
			CTmpTimeSheet t = tmp.getObject();
			if (t.getId() != null) {
				t.setDateFrom(filterModel.getObject().getDateFrom());
				t.setDateTo(filterModel.getObject().getDateTo());
				brwTimeStampGenerateService.update(t);
			}
		}
	}

	// MODAL
	private void showModal(AjaxRequestTarget target, final IAjaxCommand commandToExecute, String titleCode) {

		modal.getTitleModel().setObject(CStringResourceReader.read(titleCode));
		modalPanel.setAction(commandToExecute);
		modal.show(target);
	}

	private IAjaxCommand getCommandGenerateTimestamps() {
		return new IAjaxCommand() {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void execute(AjaxRequestTarget target) {
				try {
					if (allowAction) {
						allowAction = false;

						updateTotaltimeForTableRows(); // updatni vsetkym riadkom totalTime k aktualnemu stavu

						CLoggedUserRecord user = CSedSession.get().getUserModel().getRecord();
						Date from = filterModel.getObject().getDateFrom();
						Date to = filterModel.getObject().getDateTo();

						// vygenerovanie cas.znaciek
						timesheetService.generateUserTimestampsFromPreparedItems(user.getUserId(), from, to, totalTime);
						((CGenerateTimestampDataSource) table.getDataSource()).clearResultList();

						feedbackPanel.success(CStringResourceReader.read("timestampGenerate.action.success"));

						target.add(getFeedbackPanel());
						target.add(table);
						target.add(remaining);

						setDefaultDurationInForm(target);
					}

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimestampGenerateEditableTablePanel.this);
					feedbackPanel.error(CStringResourceReader.read("timestampGenerate.action.fail"));
					allowAction = true;
				}
				
			}

		};
	}

	public Boolean usingMinutes() {
		return selected.getId().equals(DATAINPUTTYPE_DURATION);
	}

	public Boolean usingPercent() {
		return selected.getId().equals(DATAINPUTTYPE_PERCENT);
	}

	public Long getTotalTime() {
		return totalTime;
	}

	public void setTabPanel(CTimestampGenerateTabPanel tabPanel) {
		this.tabPanel = tabPanel;
	}

	public CompoundPropertyModel<CTimeStampGenerateFilterCriteria> getFilterModel() {
		return filterModel;
	}

	private void setDefaultDurationInForm(AjaxRequestTarget target) {
		target.appendJavaScript("Main.runCustomCheck();Main.runQTimerCheck();FormElements.init();SedApp.init();");
		target.add(tabPanel);
		this.refreshModalEditPanel(target);
	}

	public CSedDataGrid<IDataSource<CTmpTimeSheet>, CTmpTimeSheet, String> getTable() {
		return table;
	}

	public CTextField<String> getRemaining() {
		return remaining;
	}

	public void setAllowAction(boolean allowAction) {
		this.allowAction = allowAction;
	}

	public CMassEditDialogPanel getModalPanelEdit() {
		return modalPanelEdit;
	}

	private void refreshModalEditPanel(AjaxRequestTarget target) {
		
		if (modalPanelEdit.getProjectCheckBox().getModelObject().booleanValue()) {
			modalPanelEdit.getProjectCheckBox().setModelObject(Boolean.FALSE);
			modalPanelEdit.getProjectContainer().setEnabled(false);
			target.appendJavaScript("$('.search-select-projects2').select2({placeholder: '',allowClear: false});");
		} else {
			modalPanelEdit.getProjectContainer().setEnabled(false);
		}
		target.add(modalPanelEdit.getProjectContainer());

		if (modalPanelEdit.getActivityCheckBox().getModelObject().booleanValue()) {
			modalPanelEdit.getActivityCheckBox().setModelObject(Boolean.FALSE);
			modalPanelEdit.getActivityContainer().setEnabled(false);
			target.appendJavaScript("$('.search-select-activities2').select2({placeholder: '',allowClear: false});");
		} else {
			modalPanelEdit.getActivityContainer().setEnabled(false);
		}
		target.add(modalPanelEdit.getActivityContainer());
	}
	
}