package sk.qbsw.sed.panel.requests;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.request.CRequestRecordForGraph;
import sk.qbsw.sed.client.model.request.CSubordinateRequestsBrwFilterCriteria;
import sk.qbsw.sed.communication.service.IBrwRequestClientService;
import sk.qbsw.sed.communication.service.IRequestStatusClientService;
import sk.qbsw.sed.communication.service.IRequestTypeClientService;
import sk.qbsw.sed.component.calendar.CDateRangePicker;
import sk.qbsw.sed.component.calendar.CDateRangePicker.SupportedDefaults;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.timesheet.editable.CChooseEmployeesForRequestsPanel;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.editable.CDetailColumn;
import sk.qbsw.sed.web.grid.datasource.CRequestDataSource;
import sk.qbsw.sed.web.grid.toolbar.CPagingToolbar;

public class CRequestTablePanel extends CPanel {

	private static final long serialVersionUID = 1L;
	
	private static final String TITLE = "title";
	
	private static final String ON_CHANGE = "onchange";

	private static final String DATE_FROM = "dateFrom";

	private static final String FA_TABLE = "fa fa-table";

	private static final String FA_BAR_CHART = "fa fa-bar-chart";

	// zdroj dát pre tabuľku: public.t_request
	private CSedDataGrid<IDataSource<CRequestRecord>, CRequestRecord, String> table;

	private final CRequestPanel tabPanel;

	private final Label tabTitle;

	private CFeedbackPanel errorPanel;

	private final CSubordinateRequestsBrwFilterCriteria requestFilter = new CSubordinateRequestsBrwFilterCriteria();

	private CompoundPropertyModel<CSubordinateRequestsBrwFilterCriteria> requestFilterModel;

	@SpringBean
	private IRequestStatusClientService requestStatusService;

	@SpringBean
	private IRequestTypeClientService requestTypeService;

	@SpringBean
	private IBrwRequestClientService brwRequestClientService;

	private List<CRequestRecordForGraph> resultList;

	private Label graphStatesDataScript;
	private Model<String> graphStatesDataScriptModel;

	private Label graphUsersDataScript;
	private Model<String> graphUsersDataScriptModel;

	private Label graphRequestsDataScript;
	private Model<String> graphRequestsDataScriptModel;

	private Label requestClickedCallbackScript;
	private AbstractDefaultAjaxBehavior requestClickedBehaviour;

	private WebMarkupContainer ganttGraph;
	private AjaxFallbackLink<Object> graphTableSwitch;
	private WebMarkupContainer graphTableSwitchIcon;
	private Model<String> graphTableSwitchModel;

	private Form<CSubordinateRequestsBrwFilterCriteria> requestFilterForm;
	private boolean isGraphVisible = false;

	public CRequestTablePanel(String id, CRequestPanel tabPanelParam, Label tabTitleParam, final Label pageTitleSmall) {
		super(id);
		setOutputMarkupId(true);
		pageTitleSmall.setOutputMarkupId(true);

		this.tabPanel = tabPanelParam;
		this.tabTitle = tabTitleParam;

		this.requestFilterModel = new CompoundPropertyModel<>(requestFilter);

		final List<IGridColumn<IDataSource<CRequestRecord>, CRequestRecord, String>> columns = new ArrayList<>();

		CDetailColumn<IDataSource<CRequestRecord>, CRequestRecord, String> detailColumn = new CDetailColumn<IDataSource<CRequestRecord>, CRequestRecord, String>("detail", Model.of(getString("table.column.actions"))) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSelect(AjaxRequestTarget target, IModel<CRequestRecord> rowModel, WebMarkupContainer rowComponent) {
				showDetail(target, rowModel.getObject().getId());
			}
		};
		detailColumn.setInitialSize(55);
		columns.add(detailColumn);
		
		columns.add(new PropertyColumn<IDataSource<CRequestRecord>, CRequestRecord, String, String>(
				new StringResourceModel("requests.label.employee", this, null), "ownerWholeName", "owner.surname+owner.name").setInitialSize(200));

		columns.add(new PropertyColumn<IDataSource<CRequestRecord>, CRequestRecord, String, String>(
				new StringResourceModel("requests.column.from", this, null), DATE_FROM, DATE_FROM).setInitialSize(100));

		columns.add(new PropertyColumn<IDataSource<CRequestRecord>, CRequestRecord, String, String>(
				new StringResourceModel("requests.column.to", this, null), "dateTo", "dateTo").setInitialSize(100));

		columns.add(new PropertyColumn<IDataSource<CRequestRecord>, CRequestRecord, String, String>(
				new StringResourceModel("requests.column.workdays", this, null), "workDays", "numberWorkDays").setInitialSize(140));

		columns.add(new PropertyColumn<IDataSource<CRequestRecord>, CRequestRecord, String, String>(
				new StringResourceModel("requests.label.type", this, null), "typeDescription", "type").setInitialSize(150));

		columns.add(new PropertyColumn<IDataSource<CRequestRecord>, CRequestRecord, String, String>(
				new StringResourceModel("requests.label.state", this, null), "statusDescription", "status").setInitialSize(120));

		columns.add(
				new PropertyColumn<IDataSource<CRequestRecord>, CRequestRecord, String, String>(
						new StringResourceModel("requests.label.reasonname", this, null), "requestReasonDescription", "reason").setInitialSize(120));

		table = new CSedDataGrid<IDataSource<CRequestRecord>, CRequestRecord, String>("grid", new CRequestDataSource(requestFilterModel), columns) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onDoubleRowClicked(AjaxRequestTarget target, IModel<CRequestRecord> rowModel) {
				showDetail(target, rowModel.getObject().getId());
			}

		};
		
		table.addBottomToolbar(new CPagingToolbar<IDataSource<CRequestRecord>, CRequestRecord, String>(table));
		table.setOutputMarkupPlaceholderTag(true);
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		requestFilterForm = new Form<>("filter", requestFilterModel);

		// date filtration
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		to.add(Calendar.DATE, 30);
		requestFilterModel.getObject().setDateFrom(from.getTime());
		requestFilterModel.getObject().setDateTo(to.getTime());

		add(new CChooseEmployeesForRequestsPanel("chooseEmployees", requestFilter, CRequestTablePanel.this, pageTitleSmall));

		final WebMarkupContainer date = new CDateRangePicker("date", SupportedDefaults.ZIADOSTI);
		final TextField<String> hiddenDate = new TextField<>("dateInput");
		hiddenDate.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				table.setFirstPage();
				target.add(table);
				table.update();
				updateGraph(target, true);
			}
		});
		date.add(hiddenDate);

		requestFilterForm.add(date);

		// filter
		CCodeListRecord defaultStateOption = new CCodeListRecord(null, CStringResourceReader.read("requests.all.states"));
		List<CCodeListRecord> stateOptions = new ArrayList<>();
		stateOptions.add(defaultStateOption);

		CCodeListRecord defaultTypeOption = new CCodeListRecord(null, CStringResourceReader.read("requests.all.types"));
		List<CCodeListRecord> typeOptions = new ArrayList<>();
		typeOptions.add(defaultTypeOption);
		try {
			stateOptions.addAll(requestStatusService.getValidRecords());
			typeOptions.addAll(requestTypeService.getValidRecords());
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		final Model<CCodeListRecord> stateModel = new Model<>();
		stateModel.setObject(defaultStateOption);

		final CDropDownChoice<CCodeListRecord> stateChoice = new CDropDownChoice<>("stateId", stateModel, stateOptions);
		stateChoice.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				requestFilterModel.getObject().setStateId(stateModel.getObject().getId());
				table.setFirstPage();
				target.add(table);
				updateGraph(target, true);
			}
		});
		requestFilterForm.add(stateChoice);

		final Model<CCodeListRecord> typeModel = new Model<>();
		typeModel.setObject(defaultTypeOption);

		final CDropDownChoice<CCodeListRecord> typeChoice = new CDropDownChoice<>("typeId", typeModel, typeOptions);
		typeChoice.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				requestFilterModel.getObject().setTypeId(typeModel.getObject().getId());
				table.setFirstPage();
				target.add(table);
				updateGraph(target, true);
			}
		});
		requestFilterForm.add(typeChoice);

		add(requestFilterForm);
		
		WebMarkupContainer chooseColumns = new WebMarkupContainer("chooseColumns");
		chooseColumns.setOutputMarkupId(true);
		chooseColumns.add(new AttributeAppender(TITLE, getString("common.button.chooseColumns")));

		// vytvorím zoznam stĺpcov
		chooseColumns.add(new ListView<Component>("columnComponents", table.getColumnComponentsForHiding()) {

			@Override
			protected void populateItem(ListItem<Component> item) {
				item.add(item.getModelObject());
			}
		});

		add(chooseColumns);
		
		table.showColumn(detailColumn, false);

		graphStatesDataScript = new Label("graphStatesDataScript", graphStatesDataScriptModel);
		graphStatesDataScript.setOutputMarkupId(true);
		graphStatesDataScript.setEscapeModelStrings(false);

		graphUsersDataScript = new Label("graphUsersDataScript", graphUsersDataScriptModel);
		graphUsersDataScript.setOutputMarkupId(true);
		graphUsersDataScript.setEscapeModelStrings(false);

		graphRequestsDataScript = new Label("graphRequestsDataScript", graphRequestsDataScriptModel);
		graphRequestsDataScript.setOutputMarkupId(true);
		graphRequestsDataScript.setEscapeModelStrings(false);

		requestClickedBehaviour = new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				StringValue idValue = getRequest().getRequestParameters().getParameterValue("id");
				if (idValue != null && StringUtils.isNotBlank(idValue.toString()) && StringUtils.isNumeric(idValue.toString())) {
					Long id = idValue.toLongObject();

					showDetail(target, id);
				}
			}
		};

		ganttGraph = new WebMarkupContainer("ganttChart");
		ganttGraph.setOutputMarkupPlaceholderTag(true);
		ganttGraph.add(requestClickedBehaviour);

		requestClickedCallbackScript = new Label("requestClickedCallbackScript");
		requestClickedCallbackScript.setOutputMarkupId(true);
		requestClickedCallbackScript.setEscapeModelStrings(false);

		graphTableSwitchModel = new Model<>(FA_BAR_CHART);

		updateGraph(null, false);

		graphTableSwitchIcon = new WebMarkupContainer("includeTodaySwitchIcon");
		graphTableSwitchIcon.setOutputMarkupId(true);
		graphTableSwitchIcon.add(new AttributeModifier("class", graphTableSwitchModel));
		graphTableSwitchIcon.add(new AttributeAppender(TITLE, getString("requests.button.graph.tooltip")));

		graphTableSwitch = new AjaxFallbackLink<Object>("graphTableSwitch") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				isGraphVisible = !isGraphVisible;

				if (isGraphVisible) {
					graphTableSwitchModel = new Model<>(FA_TABLE);
				} else {
					graphTableSwitchModel = new Model<>(FA_BAR_CHART);
				}
				table.setVisible(!isGraphVisible);
				ganttGraph.setVisible(isGraphVisible);
				graphTableSwitchIcon.add(new AttributeModifier("class", graphTableSwitchModel));
				graphTableSwitchIcon.add(new AttributeModifier(TITLE, isGraphVisible ? getString("requests.button.table.tooltip") : getString("requests.button.graph.tooltip")));

				target.add(graphTableSwitchIcon);
				target.add(table);
				updateGraph(target, true);
			}
		};
		graphTableSwitch.setOutputMarkupId(true);
		graphTableSwitch.add(graphTableSwitchIcon);
		add(graphTableSwitch);

	}

    /**
     * Metoda vykresli Gantt graf podla hodnot z filtra.
     * 
     * @param target
     * @param refresh
     */
    public void updateGraph(AjaxRequestTarget target, boolean refresh) {

	List<Long> userList = new ArrayList<>();
	String tooltipHeader;
	String tooltipText;

	StringBuilder requests = new StringBuilder();
	StringBuilder users = new StringBuilder().append("[");
	StringBuilder states = new StringBuilder().append("{'-1': 'bar-weekend', '1' : 'bar-proposed', '2' : 'bar-rejected', '3' : 'bar-approved', '4' : 'bar-rejected',};");

	setResultList(target == null || !isGraphVisible);

	requests.append("[");
	for (CRequestRecordForGraph request : resultList) {

	    if (!userList.contains(request.getOwnerId())) {
		userList.add(request.getOwnerId());
	    }

	    if (request.getHalfday()) {
		request.getDateTo().set(Calendar.HOUR_OF_DAY, 12);
	    }

	    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d. MMMM", Session.get().getLocale());
	    if (request.getHalfday()) {
		tooltipText = dateFormat.format(request.getDateFrom().getTime()) + " - "
			+ CStringResourceReader.read("requests.panel.halfday");
	    } else {
		if (DateUtils.isSameDay(request.getDateFrom(), request.getDateTo())) {
		    // for one day request we want in tooltip just one date
		    tooltipText = dateFormat.format(request.getDateFrom().getTime());
		} else {
		    tooltipText = dateFormat.format(request.getDateFrom().getTime()) + " - "
			    + dateFormat.format(request.getDateTo().getTime());
		}
	    }

	    if (request.getDateFrom().getTimeInMillis() < requestFilterModel.getObject().getDateFrom().getTime()) {
		// chceme aby graf zacinal od datumu vo filtri
		request.getDateFrom().setTime(requestFilterModel.getObject().getDateFrom());
	    }

	    String from = request.getDateFrom().getTime().toString().replaceAll("CET", "").replaceAll("CEST", "");
	    String to = request.getDateTo().getTime().toString().replaceAll("CET", "").replaceAll("CEST", "");

	    tooltipHeader = request.getTypeDescription() == null ? getString("daytype.value.dayoff")
		    : request.getTypeDescription();

	    if (request.getStatusDescription() != null) {
		tooltipHeader += " - " + request.getStatusDescription();
	    }

	    requests.append("{'startDate':new Date('" + from + "'),'endDate':new Date('" + to + "'),'taskName':'"
		    + request.getOwnerSurname() + " " + request.getOwnerName() + "','status':'"
		    + request.getStatusId().intValue() + "', 'id': '" + request.getId() + "', 'type': '" + tooltipHeader
		    + "', 'tooltipText': '" + tooltipText + "'}");
	    users.append("'" + request.getOwnerSurname() + " " + request.getOwnerName() + "'");

	    if (request.equals(resultList.get(resultList.size() - 1))) {
		requests.append("]");
		users.append("]");
	    } else {
		requests.append(",");
		users.append(",");
	    }
	}

		graphStatesDataScriptModel = Model.of("var taskStatus = " + states + ";");

		if (resultList.isEmpty()) {

			graphUsersDataScriptModel = Model.of("var taskNames = '';");
			graphRequestsDataScriptModel = Model.of("var tasks = '';");

		} else {

			graphUsersDataScriptModel = Model.of("var taskNames = " + users + ";");
			graphRequestsDataScriptModel = Model.of("var tasks = " + requests + ";");

			ganttGraph.add(new AttributeModifier("style", new Model<String>() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					return "height: " + (50 + userList.size() * 50) + "px; width: 100%;";
				}
			}));
		}

		graphStatesDataScript.setDefaultModel(graphStatesDataScriptModel);
		graphUsersDataScript.setDefaultModel(graphUsersDataScriptModel);
		graphRequestsDataScript.setDefaultModel(graphRequestsDataScriptModel);

		if (refresh && target != null) {
			// aktualizuje hodnoty premennych pre vykreslenie grafu
			target.add(graphStatesDataScript);
			target.add(graphUsersDataScript);
			target.add(graphRequestsDataScript);
			target.add(ganttGraph);
			target.appendJavaScript("createGraph(tasks, taskNames, taskStatus);");

			requestClickedCallbackScript.setDefaultModel(Model.of("var requestClickedCallback='" + requestClickedBehaviour.getCallbackUrl() + "';"));
			target.add(requestClickedCallbackScript);
		} else {
			add(graphStatesDataScript);
			add(graphUsersDataScript);
			add(graphRequestsDataScript);
			add(requestClickedCallbackScript);
			add(ganttGraph);
		}

    }

	private void setResultList(boolean empty) {
		if (empty) {
			resultList = new ArrayList<>();
		} else {
			try {
				resultList = brwRequestClientService.loadDataForGraph(requestFilterModel.getObject());
			} catch (CBussinessDataException e) {
				Logger.getLogger(CRequestTablePanel.class).error(e);
				throw new CSystemFailureException(e);
			}
		}
	}

	public CSedDataGrid<IDataSource<CRequestRecord>, CRequestRecord, String> getTable() {
		return table;
	}

	public boolean isGraphVisible() {
		return isGraphVisible;
	}

	private void showDetail(AjaxRequestTarget target, Long id) {
		// update nadpisu
		tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.detail"));
		target.add(tabTitle);

		// update formulara
		tabPanel.setEntityID(id);
		tabPanel.setModeDetail(true);
		tabPanel.setModeAdd(false);
		tabPanel.clearFeedbackMessages();
		target.add(tabPanel);
		target.appendJavaScript("Main.runQTimerCheck();");

		// prepnutie tabu
		target.appendJavaScript("$('#tab-2').click()");
	}
}
