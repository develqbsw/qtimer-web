package sk.qbsw.sed.component.stats;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.codelist.CProjectDuration;
import sk.qbsw.sed.common.utils.CDateRange;
import sk.qbsw.sed.common.utils.CDateRangeUtils;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;

/**
 * SubPage: /home SubPage title: Dashboard
 * 
 * PieChart (Koláčový graf) - Rozdelenie projektov
 */
public class CPieChart extends CPanel {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private ITimesheetClientService timesheetService;

	private List<CProjectDuration> projectDurationList;

	private CFeedbackPanel errorPanel;

	Label pieGraphDataScript;
	Model<String> pieGraphDataScriptModel;

	Label maxValueProjectName1;
	Label maxValueInPercent1;
	Model<String> maxValueProjectName1Model;
	Model<String> maxValueInPercent1Model;

	Label maxValueProjectName2;
	Label maxValueInPercent2;
	Model<String> maxValueProjectName2Model;
	Model<String> maxValueInPercent2Model;

	Label maxValueProjectName3;
	Label maxValueInPercent3;
	Model<String> maxValueProjectName3Model;
	Model<String> maxValueInPercent3Model;

	TimeRanges timeRange = TimeRanges.THIS_MONTH;
	boolean includeToday = true;

	public CPieChart(String id, CFeedbackPanel errorPanel) {
		super(id);
		this.errorPanel = errorPanel;

		init(false, null, timeRange);
	}

	private void init(boolean refresh, AjaxRequestTarget target, TimeRanges timeRange) {

		this.timeRange = timeRange;
		RequiredDates requiredDates = new RequiredDates(timeRange);

		try {
			projectDurationList = timesheetService.getDataForGraphOfProjects(requiredDates.dateFrom, requiredDates.dateTo);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			errorPanel.error(getString(e.getModel().getServerCode()));
		}

		final PieGraphData pieGraphData = new PieGraphData();
		for (CProjectDuration projectDuration : projectDurationList) {
			pieGraphData.add(projectDuration.getProjectName(), projectDuration.getDuration());
		}

		pieGraphDataScriptModel = Model.of("function getData_pieChart() { return JSON.parse(" + pieGraphData.getValue() + "); } ");

		maxValueProjectName1Model = Model.of(pieGraphData.getMaxValues().get(0).getLabel());
		maxValueInPercent1Model = Model.of(pieGraphData.getValueOfDatacellInPercent(pieGraphData.getMaxValues().get(0)));
		maxValueProjectName2Model = Model.of(pieGraphData.getMaxValues().get(1).getLabel());
		maxValueInPercent2Model = Model.of(pieGraphData.getValueOfDatacellInPercent(pieGraphData.getMaxValues().get(1)));
		maxValueProjectName3Model = Model.of(pieGraphData.getMaxValues().get(2).getLabel());
		maxValueInPercent3Model = Model.of(pieGraphData.getValueOfDatacellInPercent(pieGraphData.getMaxValues().get(2)));

		if (refresh) {

			pieGraphDataScript.setDefaultModel(pieGraphDataScriptModel);

			maxValueProjectName1.setDefaultModel(maxValueProjectName1Model);
			maxValueInPercent1.setDefaultModel(maxValueInPercent1Model);

			maxValueProjectName2.setDefaultModel(maxValueProjectName2Model);
			maxValueInPercent2.setDefaultModel(maxValueInPercent2Model);

			maxValueProjectName3.setDefaultModel(maxValueProjectName3Model);
			maxValueInPercent3.setDefaultModel(maxValueInPercent3Model);

			target.add(pieGraphDataScript);
			target.add(maxValueProjectName1);
			target.add(maxValueInPercent1);
			target.add(maxValueProjectName2);
			target.add(maxValueInPercent2);
			target.add(maxValueProjectName3);
			target.add(maxValueInPercent3);
			target.appendJavaScript("updatePieChart();");

		} else {

			pieGraphDataScript = new Label("pieGraphDataScript", pieGraphDataScriptModel);
			pieGraphDataScript.setOutputMarkupId(true);

			maxValueProjectName1 = new Label("maxValueProjectName1", maxValueProjectName1Model);
			maxValueProjectName1.setOutputMarkupId(true);
			maxValueInPercent1 = new Label("maxValuePercentage1", maxValueInPercent1Model);
			maxValueInPercent1.setOutputMarkupId(true);

			maxValueProjectName2 = new Label("maxValueProjectName2", maxValueProjectName2Model);
			maxValueProjectName2.setOutputMarkupId(true);
			maxValueInPercent2 = new Label("maxValuePercentage2", maxValueInPercent2Model);
			maxValueInPercent2.setOutputMarkupId(true);

			maxValueProjectName3 = new Label("maxValueProjectName3", maxValueProjectName3Model);
			maxValueProjectName3.setOutputMarkupId(true);
			maxValueInPercent3 = new Label("maxValuePercentage3", maxValueInPercent3Model);
			maxValueInPercent3.setOutputMarkupId(true);

			this.add(pieGraphDataScript.setEscapeModelStrings(false));
			this.add(maxValueProjectName1);
			this.add(maxValueInPercent1);
			this.add(maxValueProjectName2);
			this.add(maxValueInPercent2);
			this.add(maxValueProjectName3);
			this.add(maxValueInPercent3);
		}
	}

	public void refresh(AjaxRequestTarget target, TimeRanges timeRange, boolean includeToday) {

		this.includeToday = includeToday;
		init(true, target, timeRange);
	}

	private static class PieGraphData implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<DataCell> data = new ArrayList<>();

		private DataCell empty = new DataCell("", 0L, "");
		private DataCell maxValue1 = empty;
		private DataCell maxValue2 = empty;
		private DataCell maxValue3 = empty;

		public void add(String label, Long value) {
			String stringValue = BigDecimal.valueOf(value).divide(BigDecimal.valueOf(1000L * 3600), 2, RoundingMode.HALF_UP).toString();
			DataCell newDataCell = new DataCell(label, value, stringValue);
			data.add(newDataCell);
			updateMaxValues(newDataCell);
		}

		private void updateMaxValues(DataCell newDataCell) {

			if (maxValue1 == null || maxValue1.getValue().longValue() < newDataCell.getValue().longValue()) {
				maxValue3 = maxValue2;
				maxValue2 = maxValue1;
				maxValue1 = newDataCell;
			} else if (maxValue2 == null || maxValue2.getValue().longValue() < newDataCell.getValue().longValue()) {
				maxValue3 = maxValue2;
				maxValue2 = newDataCell;
			} else if (maxValue3 == null || maxValue3.getValue().longValue() < newDataCell.getValue().longValue()) {
				maxValue3 = newDataCell;
			}
		}

		public String getValue() {
			String ret = "	' [	";
			for (int i = 0; i < data.size(); i++) {
				ret += " { \"label\": \"" + data.get(i).getLabel() + "\", \"value\": " + data.get(i).getStringValue() + " }  ";
				if (i + 1 != data.size()) {
					ret += "	,	";
				}
			}

			ret += " ]' ";
			return ret;
		}

		public String getValueOfDatacellInPercent(DataCell datacell) {

			long totalValue = 0;
			for (DataCell cell : data) {
				totalValue += cell.value;
			}

			if (totalValue == 0 || datacell.getValue().longValue() == 0) {
				return "";
			}

			return BigDecimal.valueOf(datacell.getValue()).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalValue), 2, RoundingMode.HALF_UP).toString() + "%";

		}

		private class DataCell implements Serializable {

			private static final long serialVersionUID = 1L;

			String label;
			String stringValue;
			Long value;

			public DataCell(String label, Long value, String stringValue) {
				this.label = label;
				this.value = value;
				this.stringValue = stringValue;
			}

			public String getLabel() {
				return label;
			}

			public String getStringValue() {
				return stringValue;
			}

			public Long getValue() {
				return value;
			}
		}

		private List<DataCell> getMaxValues() {
			List<DataCell> ret = new ArrayList<>();
			ret.add(maxValue1);
			ret.add(maxValue2);
			ret.add(maxValue3);
			return ret;
		}
	}

	private class RequiredDates {
		Calendar dateFrom;
		Calendar dateTo;

		public RequiredDates(TimeRanges range) {
			super();
			dateFrom = Calendar.getInstance();
			dateTo = Calendar.getInstance();
			setDates(range);
		}

		private void setDates(TimeRanges range) {
			switch (range) {
			case THIS_MONTH: {
				getDatesForThisMonth();
				break;
			}
			case THIS_WEEK: {
				getDatesForThisWeek();
				break;
			}
			case LAST_MONTH: {
				getDatesForLastMonth();
				break;
			}
			case LAST_WEEK: {
				getDatesForLastWeek();
			}
			}
		}

		private void getDatesForLastWeek() {
			CDateRange previousWeek = CDateRangeUtils.getDateRangeForPreviousWeek();

			dateFrom = previousWeek.getDateFrom();
			dateTo = previousWeek.getDateTo();

		}

		private void getDatesForLastMonth() {
			CDateRange previousMonth = CDateRangeUtils.getDateRangeForPreviousMonth();

			dateFrom = previousMonth.getDateFrom();
			dateTo = previousMonth.getDateTo();
		}

		private void getDatesForThisWeek() {

			CDateRange thisWeek = CDateRangeUtils.getDateRangeForThisWeek();

			Calendar cal2 = Calendar.getInstance();
			if (!includeToday) {
				cal2.add(Calendar.DATE, -1);
			}

			dateFrom = thisWeek.getDateFrom();
			dateTo = cal2;
		}

		private void getDatesForThisMonth() {
			CDateRange thisMonth = CDateRangeUtils.getDateRangeForThisMonth();

			Calendar cal2 = Calendar.getInstance();
			if (!includeToday) {
				cal2.add(Calendar.DATE, -1);
			}

			dateFrom = thisMonth.getDateFrom();
			dateTo = cal2;
		}
	}
}
