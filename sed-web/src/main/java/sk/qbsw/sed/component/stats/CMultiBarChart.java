package sk.qbsw.sed.component.stats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.CStatsFilter;
import sk.qbsw.sed.client.model.CStatsRecord;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.common.utils.CDateRange;
import sk.qbsw.sed.common.utils.CDateRangeUtils;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.component.calendar.CDateRangePicker;
import sk.qbsw.sed.component.calendar.CDateRangePicker.SupportedDefaults;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.page.home.CStatsPage;
import sk.qbsw.sed.panel.timesheet.editable.CChooseEmployeesPanel;
import sk.qbsw.sed.panel.timesheet.editable.CChooseEmployeesPanel.SupportedPageProperties;

/**
 * SubPage: /stats SubPage title: Štatistiky
 * 
 * MultiBarChart (stĺpcový graf)
 */
public class CMultiBarChart extends CPanel {

	private static final long serialVersionUID = 1L;
	
	private static final String NBSP_4X = "&nbsp;&nbsp;&nbsp;&nbsp;";
	private static final String I_FA_CHECK = "<i class='fa fa-check'></i>";

	private enum EShowOptions {
		EMPLOYEE_PROJECT, EMPLOYEE_ACTIVITY, PROJECT_EMPLOYEE, PROJECT_ACTIVITY, ACTIVITY_EMPLOYEE, ACTIVITY_PROJECT
	}

	private Label barGraphDataScript;
	private Model<String> barGraphDataScriptModel;

	private EShowOptions showOptions = EShowOptions.PROJECT_ACTIVITY;

	@SpringBean
	private ITimesheetClientService timesheetService;

	private static final int MIN_BARS_IN_GRAPH = 10;

	private final CStatsFilter filter = new CStatsFilter();
	private CompoundPropertyModel<CStatsFilter> filterModel;

	private Map<Long, Map<Long, Long>> dataForGraphMap;
	private List<CCodeListRecord> allProjects;
	private List<CCodeListRecord> allEmployees;
	private List<CCodeListRecord> allActivities;
	private Label pageTitleSmall;

	private CDropDownChoice<CCodeListRecord> projectChoice;
	private CDropDownChoice<CCodeListRecord> activityChoice;

	private AjaxFallbackLink<Void> showEmployeeProject;
	private AjaxLink<Void> showEmployeeActivity;
	private AjaxLink<Void> showProjectEmployee;
	private AjaxLink<Void> showProjectActivity;
	private AjaxLink<Void> showActivityEmployee;
	private AjaxLink<Void> showActivityProject;

	public CMultiBarChart(String id, CFeedbackPanel errorPanel, final Label pageTitleSmall) {
		super(id);
		registerFeedbackPanel(errorPanel);
		this.pageTitleSmall = pageTitleSmall;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CDateRange previousMonth = CDateRangeUtils.getDateRangeForPreviousMonth();
		filter.setDateFrom(previousMonth.getDateFrom());
		filter.setDateTo(previousMonth.getDateTo());

		filterModel = new CompoundPropertyModel<>(filter);
		final Form<CStatsFilter> filterForm = new Form<>("filter", filterModel);

		final WebMarkupContainer date = new CDateRangePicker("date", SupportedDefaults.STATS);
		final TextField<String> hiddenDate = new TextField<>("dateInput");
		hiddenDate.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				refresh(target, true);
			}
		});

		date.add(hiddenDate);

		filterForm.add(date);
		add(filterForm);

		add(new CChooseEmployeesPanel("chooseEmployees", filter, this, pageTitleSmall, SupportedPageProperties.STATS, getFeedbackPanel(), null));

		prepareData(null);
		barGraphDataScriptModel = Model.of("function getData_barChart() { return JSON.parse(" + getMultiBarChartData() + "); } ");

		barGraphDataScript = new Label("barGraphDataScript", barGraphDataScriptModel);
		barGraphDataScript.setOutputMarkupId(true);

		this.add(barGraphDataScript.setEscapeModelStrings(false));

		List<CCodeListRecord> projectOptions = new ArrayList<>();
		projectOptions.addAll(allProjects);
		Collections.sort(projectOptions, new ComparatorByAlphabetical());
		projectOptions.add(0, new CCodeListRecord(ISearchConstants.ALL, CStringResourceReader.read("multiBarChart.filter.project.all")));

		final Model<CCodeListRecord> projectModel = new Model<>();
		projectModel.setObject(projectOptions.get(0)); // dropdownu sa nastavi defaultna hodnota "Vsetky projekty"
		filterModel.getObject().setActivityId(projectModel.getObject().getId()); // hodnotu z projectModel nasetujem do filterModel,
		// aby sa to prejavilo pri filtrovani

		projectChoice = new CDropDownChoice<>("projectId", projectModel, projectOptions);
		projectChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				filterModel.getObject().setProjectId(projectModel.getObject().getId());
				refresh(target, false);
			}
		});
		filterForm.add(projectChoice);

		List<CCodeListRecord> activityOptions = new ArrayList<>();
		activityOptions.addAll(allActivities);
		Collections.sort(activityOptions, new ComparatorByAlphabetical());
		activityOptions.add(0, new CCodeListRecord(ISearchConstants.ALL, CStringResourceReader.read("multiBarChart.filter.activity.all")));

		final Model<CCodeListRecord> activityModel = new Model<>();
		activityModel.setObject(activityOptions.get(0)); // dropdownu sa nastavi defaultna hodnota "Vsetky aktivity"
		filterModel.getObject().setActivityId(activityModel.getObject().getId()); // hodnotu z activityModel nasetujem do filterModel,
		// aby sa to prejavilo pri filtrovani

		activityChoice = new CDropDownChoice<>("activityId", activityModel, activityOptions);
		activityChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				filterModel.getObject().setActivityId(activityModel.getObject().getId());
				refresh(target, false);
			}
		});
		filterForm.add(activityChoice);

		final WebMarkupContainer showOptions = new WebMarkupContainer("showOptions");
		showOptions.setOutputMarkupId(true);
		add(showOptions);

		showEmployeeProject = new AjaxFallbackLink<Void>("showEmployeeProject") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				CMultiBarChart.this.resetShowOptions(target);
				CMultiBarChart.this.showOptions = EShowOptions.EMPLOYEE_PROJECT;
				showEmployeeProject.setBody(Model.of("<i class='fa fa-check'></i><span>" + getString("multiBarChart.label.showEmployeeProject") + "</span>"));
				target.add(showOptions);
				refresh(target, false);
			}
		};
		showEmployeeProject.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showEmployeeProject")));

		showEmployeeActivity = new AjaxLink<Void>("showEmployeeActivity") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				CMultiBarChart.this.resetShowOptions(target);
				CMultiBarChart.this.showOptions = EShowOptions.EMPLOYEE_ACTIVITY;
				setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showEmployeeActivity")));
				target.add(showOptions);
				refresh(target, false);
			}
		};
		showEmployeeActivity.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showEmployeeActivity")));

		showProjectEmployee = new AjaxLink<Void>("showProjectEmployee") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				CMultiBarChart.this.resetShowOptions(target);
				CMultiBarChart.this.showOptions = EShowOptions.PROJECT_EMPLOYEE;
				setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showProjectEmployee")));
				target.add(showOptions);
				refresh(target, false);
			}
		};
		showProjectEmployee.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showProjectEmployee")));

		showProjectActivity = new AjaxLink<Void>("showProjectActivity") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				CMultiBarChart.this.resetShowOptions(target);
				CMultiBarChart.this.showOptions = EShowOptions.PROJECT_ACTIVITY;
				setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showProjectActivity")));
				target.add(showOptions);
				refresh(target, false);
			}
		};
		showProjectActivity.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showProjectActivity")));

	showActivityEmployee = new AjaxLink<Void>("showActivityEmployee") {
	    /** serial uid */
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		CMultiBarChart.this.resetShowOptions(target);
		CMultiBarChart.this.showOptions = EShowOptions.ACTIVITY_EMPLOYEE;
		setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showActivityEmployee")));
		target.add(showOptions);
		refresh(target, false);
	    }
	};
		showActivityEmployee.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showActivityEmployee")));

		showActivityProject = new AjaxLink<Void>("showActivityProject") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				CMultiBarChart.this.resetShowOptions(target);
				CMultiBarChart.this.showOptions = EShowOptions.ACTIVITY_PROJECT;
				setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showActivityProject")));
				target.add(showOptions);
				refresh(target, false);
			}
		};
		showActivityProject.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showActivityProject")));

		if (CMultiBarChart.this.showOptions.equals(EShowOptions.EMPLOYEE_PROJECT)) {
			showEmployeeProject.setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showEmployeeProject")));
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.EMPLOYEE_ACTIVITY)) {
			showEmployeeActivity.setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showEmployeeActivity")));
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.PROJECT_EMPLOYEE)) {
			showProjectEmployee.setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showProjectEmployee")));
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.PROJECT_ACTIVITY)) {
			showProjectActivity.setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showProjectActivity")));
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.ACTIVITY_EMPLOYEE)) {
			showActivityEmployee.setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showActivityEmployee")));
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.ACTIVITY_PROJECT)) {
			showActivityProject.setBody(Model.of(I_FA_CHECK + getString("multiBarChart.label.showActivityProject")));
		}

		showEmployeeProject.setEscapeModelStrings(false);
		showEmployeeActivity.setEscapeModelStrings(false);
		showProjectEmployee.setEscapeModelStrings(false);
		showProjectActivity.setEscapeModelStrings(false);
		showActivityEmployee.setEscapeModelStrings(false);
		showActivityProject.setEscapeModelStrings(false);

		showEmployeeProject.setOutputMarkupId(true);
		showEmployeeActivity.setOutputMarkupId(true);
		showProjectEmployee.setOutputMarkupId(true);
		showProjectActivity.setOutputMarkupId(true);
		showActivityEmployee.setOutputMarkupId(true);
		showActivityProject.setOutputMarkupId(true);

		showOptions.add(showEmployeeProject);
		showOptions.add(showEmployeeActivity);
		showOptions.add(showProjectEmployee);
		showOptions.add(showProjectActivity);
		showOptions.add(showActivityEmployee);
		showOptions.add(showActivityProject);
    }

	private void resetShowOptions(AjaxRequestTarget target) {
		if (CMultiBarChart.this.showOptions.equals(EShowOptions.EMPLOYEE_PROJECT)) {
			showEmployeeProject.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showEmployeeProject")));
			target.add(showEmployeeProject);
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.EMPLOYEE_ACTIVITY)) {
			showEmployeeActivity.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showEmployeeActivity")));
			target.add(showEmployeeActivity);
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.PROJECT_EMPLOYEE)) {
			showProjectEmployee.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showProjectEmployee")));
			target.add(showProjectEmployee);
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.PROJECT_ACTIVITY)) {
			showProjectActivity.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showProjectActivity")));
			target.add(showProjectActivity);
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.ACTIVITY_EMPLOYEE)) {
			showActivityEmployee.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showActivityEmployee")));
			target.add(showActivityEmployee);
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.ACTIVITY_PROJECT)) {
			showActivityProject.setBody(Model.of(NBSP_4X + getString("multiBarChart.label.showActivityProject")));
			target.add(showActivityProject);
		}
	}

    public void refresh(AjaxRequestTarget target, boolean alsoListboxes) {
		if (target != null) {

			if (alsoListboxes) {
				filter.setProjectId(null);
				filter.setActivityId(null);
			}

			prepareData(target);

			if (alsoListboxes) {
				// este musim nanovo naplnit listbox projektov/aktivit podla
				// noveho rozsahu

				setChoicesProject();
				target.add(projectChoice);

				setChoicesActivity();
				target.add(activityChoice);
			}

			barGraphDataScriptModel = Model.of("function getData_barChart() { return JSON.parse(" + getMultiBarChartData() + "); } ");

			barGraphDataScript.setDefaultModel(barGraphDataScriptModel);

			target.add(barGraphDataScript);
			target.appendJavaScript("update();");
		} else {
			setResponsePage(CStatsPage.class);
		}
    }

    private String getMultiBarChartData() {

	if (dataForGraphMap.values().isEmpty()) {
	    return " '[ { \"key\" : \"\", \"values\" : [ { \"x\": \"empty_1\" , \"y\":0 } ]	} ] '";
	}

	Map<Long, String> map = new HashMap<>();
	String ret = " '[ ";

	List<CCodeListRecord> xAxisList = new ArrayList<>();
	List<CCodeListRecord> keyList = new ArrayList<>();

	if (CMultiBarChart.this.showOptions.equals(EShowOptions.EMPLOYEE_PROJECT)) {
	    xAxisList.addAll(allEmployees);
	    keyList.addAll(allProjects);
	} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.EMPLOYEE_ACTIVITY)) {
	    xAxisList.addAll(allEmployees);
	    keyList.addAll(allActivities);
	} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.PROJECT_EMPLOYEE)) {
	    xAxisList.addAll(allProjects);
	    keyList.addAll(allEmployees);
	} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.PROJECT_ACTIVITY)) {
	    xAxisList.addAll(allProjects);
	    keyList.addAll(allActivities);
	} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.ACTIVITY_EMPLOYEE)) {
	    xAxisList.addAll(allActivities);
	    keyList.addAll(allEmployees);
	} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.ACTIVITY_PROJECT)) {
	    xAxisList.addAll(allActivities);
	    keyList.addAll(allProjects);
	}

	for (CCodeListRecord key : keyList) {

	    String data = "[";

	    for (CCodeListRecord xAxis : xAxisList) {
		Long duration = dataForGraphMap.get(xAxis.getId()).get(key.getId());

		if (!"[".equals(data)) {
		    data += " , ";
		}

		if (duration == null) {
		    data += "{ \"x\": \"" + xAxis.getName() + "\"" + " , \"y\":" + "0 }";
		} else {
		    data += "{ \"x\": \"" + xAxis.getName() + "\"" + " , \"y\":" + BigDecimal.valueOf(duration)
			    .divide(BigDecimal.valueOf(1000L * 3600), 2, RoundingMode.HALF_UP).toString() + "}";
		}
	    }

	    if (xAxisList.size() < MIN_BARS_IN_GRAPH) {
		for (int i = 1; i <= MIN_BARS_IN_GRAPH - xAxisList.size(); i++) {
		    data += " , ";

		    data += "{ \"x\": \"empty_" + i + "\"" + " , \"y\":" + "0 }";
		}
	    }

	    data += "]";

	    map.put(key.getId(), data);
	}

	for (CCodeListRecord key : keyList) {
	    if (!" '[ ".equals(ret)) {
		ret += " , ";
	    }
	    ret += "	{ " + " 		\"key\" : \"" + key.getName() + "\", " + "		\"values\" : "
		    + map.get(key.getId()) + "	} ";
	}

	ret += "] '";

	return ret;
    }

	private void prepareData(AjaxRequestTarget target) {
		List<CStatsRecord> data = new ArrayList<>();

		try {
			data = timesheetService.getDataForGraphOfStats(filter);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), target, getPage());
		}

		dataForGraphMap = new HashMap<>();
		allProjects = new ArrayList<>();
		allEmployees = new ArrayList<>();
		allActivities = new ArrayList<>();

		for (CStatsRecord record : data) {

			final Long projectId = record.getProjectId();
			final String projectName = record.getProjectName();
			final Long employeeId = record.getEmployeeId();
			final String employeeName = record.getEmployeeName();
			final Long duration = record.getDuration();
			final Long activityId = record.getActivityId();
			final String activityName = record.getActivityName();

			Long key1 = null;
			Long key2 = null;

			if (CMultiBarChart.this.showOptions.equals(EShowOptions.EMPLOYEE_PROJECT)) {
				key1 = employeeId;
				key2 = projectId;
			} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.EMPLOYEE_ACTIVITY)) {
				key1 = employeeId;
				key2 = activityId;
			} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.PROJECT_EMPLOYEE)) {
				key1 = projectId;
				key2 = employeeId;
			} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.PROJECT_ACTIVITY)) {
				key1 = projectId;
				key2 = activityId;
			} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.ACTIVITY_EMPLOYEE)) {
				key1 = activityId;
				key2 = employeeId;
			} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.ACTIVITY_PROJECT)) {
				key1 = activityId;
				key2 = projectId;
			}

			if (dataForGraphMap.containsKey(key1)) {

				Long value = dataForGraphMap.get(key1).get(key2);

				if (value != null) {
					dataForGraphMap.get(key1).put(key2, duration + value);
				} else {
					dataForGraphMap.get(key1).put(key2, duration);
				}

			} else {
				Map<Long, Long> m = new HashMap<>();
				m.put(key2, duration);
				dataForGraphMap.put(key1, m);
			}

			CCodeListRecord employee = new CCodeListRecord();
			employee.setId(employeeId);
			employee.setName(employeeName);
			if (!allEmployees.contains(employee)) {
				allEmployees.add(employee);
			}

			CCodeListRecord project = new CCodeListRecord();
			project.setId(projectId);
			project.setName(projectName);
			if (!allProjects.contains(project)) {
				allProjects.add(project);
			}

			CCodeListRecord activity = new CCodeListRecord();
			activity.setId(activityId);
			activity.setName(activityName);
			if (!allActivities.contains(activity)) {
				allActivities.add(activity);
			}
		}

		if (CMultiBarChart.this.showOptions.equals(EShowOptions.EMPLOYEE_PROJECT)) {
			Collections.sort(allEmployees, new ComparatorByDuration());
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.EMPLOYEE_ACTIVITY)) {
			Collections.sort(allEmployees, new ComparatorByDuration());
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.PROJECT_EMPLOYEE)) {
			Collections.sort(allProjects, new ComparatorByDuration());
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.PROJECT_ACTIVITY)) {
			Collections.sort(allProjects, new ComparatorByDuration());
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.ACTIVITY_EMPLOYEE)) {
			Collections.sort(allActivities, new ComparatorByDuration());
		} else if (CMultiBarChart.this.showOptions.equals(EShowOptions.ACTIVITY_PROJECT)) {
			Collections.sort(allActivities, new ComparatorByDuration());
		}
	}

	private class ComparatorByDuration implements Comparator<CCodeListRecord> {
		@Override
		public int compare(CCodeListRecord o1, CCodeListRecord o2) {
			Long duration1 = 0L;
			Long duration2 = 0L;

			for (Long record : dataForGraphMap.get(o1.getId()).values()) {
				duration1 += record;
			}

			for (Long record : dataForGraphMap.get(o2.getId()).values()) {
				duration2 += record;
			}

			return duration2.compareTo(duration1);
		}
	}

	private class ComparatorByAlphabetical implements Comparator<CCodeListRecord> {
		@Override
		public int compare(CCodeListRecord o1, CCodeListRecord o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	private void setChoicesProject() {
		List<CCodeListRecord> projectOptions = new ArrayList<>();
		projectOptions.addAll(allProjects);
		Collections.sort(projectOptions, new ComparatorByAlphabetical());
		projectOptions.add(0, new CCodeListRecord(ISearchConstants.ALL, CStringResourceReader.read("multiBarChart.filter.project.all")));

		projectChoice.getModel().setObject(projectOptions.get(0)); // dropdownu sa nastavi defaultna hodnota "Vsetky projekty"

		projectChoice.setChoices(projectOptions);
	}

	private void setChoicesActivity() {
		List<CCodeListRecord> activityOptions = new ArrayList<>();
		activityOptions.addAll(allActivities);
		Collections.sort(activityOptions, new ComparatorByAlphabetical());
		activityOptions.add(0, new CCodeListRecord(ISearchConstants.ALL, CStringResourceReader.read("multiBarChart.filter.activity.all")));

		activityChoice.getModel().setObject(activityOptions.get(0)); // dropdownu sa nastavi defaultna hodnota "Vsetky aktivity"

		activityChoice.setChoices(activityOptions);
	}
}
