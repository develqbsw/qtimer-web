package sk.qbsw.sed.component.stats;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.codelist.CAttendanceDuration;
import sk.qbsw.sed.client.response.CGetInfoForMobileTimerResponseContent;
import sk.qbsw.sed.client.response.CGetSumAndAverageTimeResponseContent;
import sk.qbsw.sed.common.utils.CDateRange;
import sk.qbsw.sed.common.utils.CDateRangeUtils;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.page.home.CHomePage;

/**
 * Page: /home Page title: Dashboard
 * 
 * BarChart - Odpracovaný čas
 */
public class CBarChart extends CPanel {

    private static final long serialVersionUID = 1L;
    
    private static final String CLASS = "class";
    
    private static final String STYLE = "style";
    
    private CFeedbackPanel errorPanel;

    private Label priemerTyzden;
    private Label priemerMesiac;
    private Model<String> priemerTyzdenModel;
    private Model<String> priemerMesiacModel;

    private WebMarkupContainer tyzdenDiv;
    private WebMarkupContainer mesiacDiv;

    private Label sumDen;
    private Label sumTyzden;
    private Label sumMesiac;

    private Model<String> sumDenModel;
    private Model<String> sumTyzdenModel;
    private Model<String> sumMesiacModel;

    private Label sparklineWeek;
    private Label sparklineMonth;
    private Model<String> sparklineWeekModel;
    private Model<String> sparklineMonthModel;

    private Label barGraphDataScript;
    private Model<String> barGraphDataScriptModel;

    private Label selectTimeRangeLabel;
    private Model<String> selectTimeRangeLabelModel;

    private WebMarkupContainer thisMonthLi;
    private WebMarkupContainer thisWeekLi;
    private WebMarkupContainer lastMonthLi;
    private WebMarkupContainer lastWeekLi;

    private WebMarkupContainer timeRangeButtons;

    private WebMarkupContainer includeTodaySwitchIcon;

    private Model<String> active = new Model<>("active");
    private Model<String> empty = new Model<>("");

    private boolean includeToday = true;

    private TimeRanges timeRange = TimeRanges.THIS_MONTH;

    private CPieChart pairedGraph;

    @SpringBean
    private ITimesheetClientService timesheetService;

    private AjaxFallbackLink<Object> refreshButton = null;
    private AjaxFallbackLink<Object> thisMonthButton = null;
    private AjaxFallbackLink<Object> thisWeekButton = null;
    private AjaxFallbackLink<Object> lastMonthButton = null;
    private AjaxFallbackLink<Object> lastWeekButton = null;
    private AjaxFallbackLink<Object> includeTodaySwitch = null;

	public CBarChart(String id, CFeedbackPanel errorPanel) {
		super(id);
		this.errorPanel = errorPanel;
	}

    @Override
	protected void onInitialize() {
		super.onInitialize();

		init();

		refreshButton = new AjaxFallbackLink<Object>("refreshButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				refresh(target, timeRange);
			}

		};

		thisMonthButton = new AjaxFallbackLink<Object>("thisMonthButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				refresh(target, TimeRanges.THIS_MONTH);
			}

		};

		thisWeekButton = new AjaxFallbackLink<Object>("thisWeekButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				refresh(target, TimeRanges.THIS_WEEK);
			}

		};

		lastMonthButton = new AjaxFallbackLink<Object>("lastMonthButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				refresh(target, TimeRanges.LAST_MONTH);
			}

		};

		lastWeekButton = new AjaxFallbackLink<Object>("lastWeekButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				refresh(target, TimeRanges.LAST_WEEK);
			}

		};

		includeTodaySwitch = new AjaxFallbackLink<Object>("includeTodaySwitch") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				includeToday ^= true;
				refresh(target, timeRange);
			}

			@Override
			public boolean isEnabled() {
				return TimeRanges.THIS_MONTH.equals(timeRange) || TimeRanges.THIS_WEEK.equals(timeRange);
			}

		};
		includeTodaySwitch.add(new AttributeModifier(STYLE, new Model<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				String style;
				if (TimeRanges.LAST_MONTH == timeRange || TimeRanges.LAST_WEEK == timeRange || !includeToday) {
					style = "color:white; display: inline-block; opacity: 0.5";
				} else {
					style = "color:white; display: inline-block;";
				}
				return style;
			}
		}));

		add(refreshButton);
		thisWeekLi.add(thisWeekButton);
		thisMonthLi.add(thisMonthButton);
		lastMonthLi.add(lastMonthButton);
		lastWeekLi.add(lastWeekButton);

		includeTodaySwitch.add(sumDen);
		includeTodaySwitch.add(includeTodaySwitchIcon);
		add(includeTodaySwitch);
	}

	private void init() {
		init(false, null, TimeRanges.THIS_MONTH);
	}

	private void refresh(AjaxRequestTarget target, TimeRanges timeRange) {
		if (target != null) {
			init(true, target, timeRange);
		} else {
			setResponsePage(CHomePage.class);
		}
	}

	private void init(boolean refresh, AjaxRequestTarget target, TimeRanges range) {

		this.timeRange = range;

		List<CAttendanceDuration> attendanceDurationWeek = null;
		List<CAttendanceDuration> attendanceDurationMonth = null;
		CGetSumAndAverageTimeResponseContent sumAndAverageTimeWeek = null;
		CGetSumAndAverageTimeResponseContent sumAndAverageTimeMonth = null;
		Long sumToday = Long.valueOf(0);

		try {
			if (TimeRanges.THIS_MONTH.equals(timeRange) || TimeRanges.THIS_WEEK.equals(timeRange)) {
				CGetInfoForMobileTimerResponseContent infoForMobileTimer = timesheetService.getInfoForMobileTimer(includeToday);

				sumAndAverageTimeMonth = new CGetSumAndAverageTimeResponseContent();
				sumAndAverageTimeMonth.setAverageDuration(infoForMobileTimer.getAverageDurationPerMonth());
				sumAndAverageTimeMonth.setSumOfWorkHours(infoForMobileTimer.getSumOfWorkHoursPerMonth());

				sumAndAverageTimeWeek = new CGetSumAndAverageTimeResponseContent();
				sumAndAverageTimeWeek.setAverageDuration(infoForMobileTimer.getAverageDurationPerWeek());
				sumAndAverageTimeWeek.setSumOfWorkHours(infoForMobileTimer.getSumOfWorkHoursPerWeek());

				sumToday = infoForMobileTimer.getSumOfWorkHoursPerDay();

				CDateRange thisMonth = CDateRangeUtils.getDateRangeForThisMonth();
				CDateRange thisWeek = CDateRangeUtils.getDateRangeForThisWeek();

				attendanceDurationMonth = timesheetService.getDataForGraphOfAttendance(thisMonth.getDateFrom(), thisMonth.getDateTo());
				attendanceDurationWeek = timesheetService.getDataForGraphOfAttendance(thisWeek.getDateFrom(), thisWeek.getDateTo());

				if (!includeToday) {

					SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
					Calendar cal = Calendar.getInstance();
					String today = formatter.format(cal.getTime());

					for (int i = 0; i < attendanceDurationWeek.size(); i++) {
						if (today.equals(formatter.format(attendanceDurationWeek.get(i).getDay().getTime()))) {
							attendanceDurationWeek.get(i).setDuration(0L);
							break;
						}
					}

					for (int i = 0; i < attendanceDurationMonth.size(); i++) {
						if (today.equals(formatter.format(attendanceDurationMonth.get(i).getDay().getTime()))) {
							attendanceDurationMonth.get(i).setDuration(0L);
							break;
						}
					}
				}

			} else if (TimeRanges.LAST_MONTH.equals(timeRange)) {

				CDateRange previousMonth = CDateRangeUtils.getDateRangeForPreviousMonth();
				sumAndAverageTimeMonth = timesheetService.getSumAndAverageTime(previousMonth.getDateFrom(), previousMonth.getDateTo());
				attendanceDurationMonth = timesheetService.getDataForGraphOfAttendance(previousMonth.getDateFrom(), previousMonth.getDateTo());

			} else if (TimeRanges.LAST_WEEK.equals(timeRange)) {

				CDateRange previousWeek = CDateRangeUtils.getDateRangeForPreviousWeek();
				sumAndAverageTimeWeek = timesheetService.getSumAndAverageTime(previousWeek.getDateFrom(), previousWeek.getDateTo());
				attendanceDurationWeek = timesheetService.getDataForGraphOfAttendance(previousWeek.getDateFrom(), previousWeek.getDateTo());
			}

			switch (timeRange) {
			case THIS_MONTH: {
				selectTimeRangeLabelModel = Model.of(getString("chart.bar.this-month"));
				break;
			}
			case THIS_WEEK: {
				selectTimeRangeLabelModel = Model.of(getString("chart.bar.this-week"));
				break;
			}
			case LAST_MONTH: {
				selectTimeRangeLabelModel = Model.of(getString("chart.bar.last-month"));
				break;
			}
			case LAST_WEEK: {
				selectTimeRangeLabelModel = Model.of(getString("chart.bar.last-week"));
				break;
			}
			}
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			errorPanel.error(getString(e.getModel().getServerCode()));
		}

		final BarGraphData barGraphData = new BarGraphData("Čas(hod)", "Priemer", true, attendanceDurationWeek, attendanceDurationMonth, timeRange, sumAndAverageTimeWeek, sumAndAverageTimeMonth,
				sumToday);

		priemerTyzdenModel = Model.of(barGraphData.getAverageThisWeek());
		priemerMesiacModel = Model.of(barGraphData.getAverageThisMonth());

		sumDenModel = Model.of(barGraphData.getSumToday());
		sumTyzdenModel = Model.of(barGraphData.getSumThisWeek());
		sumMesiacModel = Model.of(barGraphData.getSumThisMonth());

		sparklineWeekModel = Model.of(barGraphData.getSparkWeek());
		sparklineMonthModel = Model.of(barGraphData.getSparkMonth());

		barGraphDataScriptModel = Model.of("function getData_barChart() { return JSON.parse(" + barGraphData.getValue() + "); } ");

		if (refresh) {

			setClassForTimeRangeElements(target);
			setClassForIncludeTodaySwitch();

			sumDen.setDefaultModel(sumDenModel);
			sumTyzden.setDefaultModel(sumTyzdenModel);
			sumMesiac.setDefaultModel(sumMesiacModel);
			priemerTyzden.setDefaultModel(priemerTyzdenModel);
			priemerMesiac.setDefaultModel(priemerMesiacModel);
			sparklineWeek.setDefaultModel(sparklineWeekModel);
			sparklineMonth.setDefaultModel(sparklineMonthModel);
			barGraphDataScript.setDefaultModel(barGraphDataScriptModel);
			selectTimeRangeLabel.setDefaultModel(selectTimeRangeLabelModel);

			target.add(sumDen);
			target.add(sumTyzden);
			target.add(sumMesiac);
			target.add(priemerTyzden);
			target.add(priemerMesiac);
			target.add(tyzdenDiv);
			target.add(mesiacDiv);
			target.add(sparklineWeek);
			target.appendJavaScript("$('.spark-week').sparkline('html',  { type:'bar', barColor:'#a5e5dd', tooltipFormatter: fortmatTime });");
			target.add(sparklineMonth);
			target.appendJavaScript("$('.spark-month').sparkline('html', { type:'bar', barColor:'#a5e5dd', barWidth: '3px', tooltipFormatter: fortmatTime});");
			target.add(barGraphDataScript);
			target.appendJavaScript("update();");
			target.add(timeRangeButtons);
			target.add(includeTodaySwitch);

			refreshPairedElement(target);

		} else {
			final String inactive = "opacity: 0.5;";

			sumDen = new Label("sumDen", sumDenModel);
			sumDen.setOutputMarkupId(true);

			sumTyzden = new Label("sumTyzden", sumTyzdenModel);
			sumTyzden.setOutputMarkupId(true);

			sumMesiac = new Label("sumMesiac", sumMesiacModel);
			sumMesiac.setOutputMarkupId(true);

			priemerTyzden = new Label("priemerTyzden", priemerTyzdenModel);
			priemerTyzden.setOutputMarkupId(true);

			tyzdenDiv = new WebMarkupContainer("tyzdenDiv");
			tyzdenDiv.setOutputMarkupId(true);
			tyzdenDiv.add(new AttributeModifier(STYLE, new Model<String>() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					String style;
					if (TimeRanges.LAST_MONTH == timeRange) {
						style = inactive;
					} else {
						style = "";
					}
					return style;
				}
			}));

			priemerMesiac = new Label("priemerMesiac", priemerMesiacModel);
			priemerMesiac.setOutputMarkupId(true);

			mesiacDiv = new WebMarkupContainer("mesiacDiv");
			mesiacDiv.setOutputMarkupId(true);
			mesiacDiv.add(new AttributeModifier(STYLE, new Model<String>() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					String style;
					if (TimeRanges.LAST_WEEK == timeRange) {
						style = inactive;
					} else {
						style = "";
					}
					return style;
				}
			}));

			sparklineWeek = new Label("sparkline-week", sparklineWeekModel);
			sparklineWeek.setOutputMarkupId(true);
			sparklineWeek.add(new AttributeModifier(STYLE, new Model<String>() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					String style;
					if (TimeRanges.LAST_MONTH == timeRange) {
						style = "opacity: 0;";
					} else {
						style = "";
					}
					return style;
				}
			}));

			sparklineMonth = new Label("sparkline-month", sparklineMonthModel);
			sparklineMonth.setOutputMarkupId(true);
			sparklineMonth.add(new AttributeModifier(STYLE, new Model<String>() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					String style;
					if (TimeRanges.LAST_WEEK == timeRange) {
						style = "opacity: 0;";
					} else {
						style = "";
					}
					return style;
				}
			}));

			barGraphDataScript = new Label("barGraphDataScript", barGraphDataScriptModel);
			barGraphDataScript.setOutputMarkupId(true);

			selectTimeRangeLabel = new Label("selectTimeRangeLabel", selectTimeRangeLabelModel);
			selectTimeRangeLabel.setOutputMarkupId(true);

			thisMonthLi = new WebMarkupContainer("thisMonthLi");
			thisMonthLi.setOutputMarkupId(true);

			thisWeekLi = new WebMarkupContainer("thisWeekLi");
			thisWeekLi.setOutputMarkupId(true);

			lastMonthLi = new WebMarkupContainer("lastMonthLi");
			lastMonthLi.setOutputMarkupId(true);

			lastWeekLi = new WebMarkupContainer("lastWeekLi");
			lastWeekLi.setOutputMarkupId(true);

			timeRangeButtons = new WebMarkupContainer("timeRangeButtons");
			timeRangeButtons.setOutputMarkupId(true);

			includeTodaySwitchIcon = new WebMarkupContainer("includeTodaySwitchIcon");
			includeTodaySwitchIcon.setOutputMarkupId(true);

			this.add(sumDen);
			tyzdenDiv.add(sumTyzden);
			mesiacDiv.add(sumMesiac);
			tyzdenDiv.add(priemerTyzden);
			mesiacDiv.add(priemerMesiac);
			this.add(tyzdenDiv);
			this.add(mesiacDiv);
			this.add(sparklineWeek);
			this.add(sparklineMonth);
			this.add(barGraphDataScript.setEscapeModelStrings(false));
			timeRangeButtons.add(selectTimeRangeLabel);
			timeRangeButtons.add(thisMonthLi);
			timeRangeButtons.add(thisWeekLi);
			timeRangeButtons.add(lastMonthLi);
			timeRangeButtons.add(lastWeekLi);
			this.add(timeRangeButtons);
		}
	}

    private static class BarGraphData implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String nameLeft;
	private final String nameRight;
	private final Boolean bar;
	private final String averageWeek;
	private final String averageMonth;
	private final String sumToday;
	private final String sumWeek;
	private final String sumMonth;
	private final String sparkWeek;
	private final String sparkMonth;
	private List<CAttendanceDuration> attendanceDuration;
	private final List<CAttendanceDuration> attendanceDurationWeek;
	private final List<CAttendanceDuration> attendanceDurationMonth;
	private final TimeRanges timeRange;
	private final CGetSumAndAverageTimeResponseContent sumAndAverageTimeWeek;
	private final CGetSumAndAverageTimeResponseContent sumAndAverageTimeMonth;

		public BarGraphData(String nameLeft, String nameRight, Boolean bar, List<CAttendanceDuration> attendanceDurationWeek, List<CAttendanceDuration> attendanceDurationMonth, TimeRanges timeRange,
				CGetSumAndAverageTimeResponseContent sumAndAverageTimeWeek, CGetSumAndAverageTimeResponseContent sumAndAverageTimeMonth, Long sumToday) {
			this.nameLeft = nameLeft;
			this.nameRight = nameRight;
			this.bar = bar;
			this.attendanceDurationWeek = attendanceDurationWeek;
			this.attendanceDurationMonth = attendanceDurationMonth;
			this.sparkMonth = getSparkData(attendanceDurationMonth);
			this.sparkWeek = getSparkData(attendanceDurationWeek);
			this.sumToday = getFormatedHoursFromMillis(sumToday);
			this.averageWeek = getFormatedHoursFromMillis(sumAndAverageTimeWeek == null ? 0L : sumAndAverageTimeWeek.getAverageDuration());
			this.averageMonth = getFormatedHoursFromMillis(sumAndAverageTimeMonth == null ? 0L : sumAndAverageTimeMonth.getAverageDuration());
			this.sumWeek = getFormatedHoursFromMillis(sumAndAverageTimeWeek == null ? 0L : sumAndAverageTimeWeek.getSumOfWorkHours());
			this.sumMonth = getFormatedHoursFromMillis(sumAndAverageTimeMonth == null ? 0L : sumAndAverageTimeMonth.getSumOfWorkHours());
			this.timeRange = timeRange;
			this.sumAndAverageTimeWeek = sumAndAverageTimeWeek;
			this.sumAndAverageTimeMonth = sumAndAverageTimeMonth;
		}

	public String getValue() {

	    Long averageForTimeRange = null;

			if (timeRange != null) {
				switch (timeRange) {
				case THIS_WEEK:
					attendanceDuration = attendanceDurationWeek;
					averageForTimeRange = sumAndAverageTimeWeek.getAverageDuration();
					break;
				case LAST_WEEK:
					attendanceDuration = attendanceDurationWeek;
					averageForTimeRange = sumAndAverageTimeWeek.getAverageDuration();
					break;
				case THIS_MONTH:
					attendanceDuration = attendanceDurationMonth;
					averageForTimeRange = sumAndAverageTimeMonth.getAverageDuration();
					break;
				case LAST_MONTH:
					attendanceDuration = attendanceDurationMonth;
					averageForTimeRange = sumAndAverageTimeMonth.getAverageDuration();
					break;

				default:
					break;
				}
			}

	    StringBuilder sb = new StringBuilder();
	    sb.append("[");
	    for (int i = 0; i < attendanceDuration.size(); i++) {
		if (i != 0) {
		    sb.append(" , ");
		}
		sb.append("[ ");
		sb.append(attendanceDuration.get(i).getDay().get(Calendar.DAY_OF_MONTH));
		sb.append(" , ");
		sb.append(BigDecimal.valueOf(attendanceDuration.get(i).getDuration()).divide(BigDecimal.valueOf(1000L * 3600), 2, RoundingMode.HALF_UP).toString());
		sb.append("]");
	    }
	    sb.append("]");
	    String dataLeft = sb.toString();

	    sb = new StringBuilder();
	    sb.append("[");
	    for (int i = 0; i < attendanceDuration.size(); i++) {
		if (i != 0) {
		    sb.append(" , ");
		}
		sb.append("[ ");
		sb.append(attendanceDuration.get(i).getDay().get(Calendar.DAY_OF_MONTH));
		sb.append(" , ");
		sb.append(BigDecimal.valueOf(averageForTimeRange).divide(BigDecimal.valueOf(1000L * 3600), 2, RoundingMode.HALF_UP).toString());
		sb.append("]");
	    }
	    sb.append("]");
	    String dataRight = sb.toString();

	    sb = new StringBuilder();
	    sb.append(" '[ ");
	    sb.append("	{ ");
	    sb.append(" 		\"key\" : \"");
	    sb.append(nameLeft);
	    sb.append("\", ");
	    sb.append("		\"bar\" : ");
	    sb.append(bar.booleanValue());
	    sb.append(", ");
	    sb.append("		\"values\" : ");
	    sb.append(dataLeft);
	    sb.append("	}, ");
	    sb.append("	{ ");
	    sb.append(" 		\"key\" : \"");
	    sb.append(nameRight);
	    sb.append("\", ");
	    sb.append("		\"color\" : \"red\", ");
	    sb.append("		\"values\" : ");
	    sb.append(dataRight);
	    sb.append("	} ");
	    sb.append("] '");
	    return sb.toString();
	}

	private String getSparkData(List<CAttendanceDuration> attendanceDurations) {
	    String ret = "";

	    if (attendanceDurations != null) {
		for (int i = 0; i < attendanceDurations.size(); i++) {
		    if (i != 0) {
			ret += " , ";
		    }
		    ret += BigDecimal.valueOf(attendanceDurations.get(i).getDuration()).divide(BigDecimal.valueOf(1000L * 60), 0, RoundingMode.HALF_UP).toString();
		}
	    }

	    return ret;
	}

	private String getFormatedHoursFromMillis(Long milsec) {

	    BigDecimal value = BigDecimal.valueOf(milsec).divide(BigDecimal.valueOf(1000L * 3600), 2, RoundingMode.HALF_UP);
	    DecimalFormat formatter = new DecimalFormat();
	    formatter.setMinimumIntegerDigits(2);

	    return value.setScale(0, RoundingMode.DOWN) + ":" + formatter.format(value.subtract(value.setScale(0, RoundingMode.DOWN))
			    .multiply(BigDecimal.valueOf(60)).setScale(0, RoundingMode.HALF_UP));
	}

		public String getSumToday() {
			return sumToday;
		}

		public String getAverageThisWeek() {
			return averageWeek;
		}

		public String getAverageThisMonth() {
			return averageMonth;
		}

		public String getSparkWeek() {
			return sparkWeek;
		}

		public String getSparkMonth() {
			return sparkMonth;
		}

		public String getSumThisWeek() {
			return sumWeek;
		}

		public String getSumThisMonth() {
			return sumMonth;
		}
    }

	private void setClassForTimeRangeElements(AjaxRequestTarget target) {

		switch (timeRange) {
		case THIS_MONTH:
			thisMonthLi.add(new AttributeModifier(CLASS, active));
			thisWeekLi.add(new AttributeModifier(CLASS, empty));
			lastMonthLi.add(new AttributeModifier(CLASS, empty));
			lastWeekLi.add(new AttributeModifier(CLASS, empty));
			break;
		case THIS_WEEK:
			thisMonthLi.add(new AttributeModifier(CLASS, empty));
			thisWeekLi.add(new AttributeModifier(CLASS, active));
			lastMonthLi.add(new AttributeModifier(CLASS, empty));
			lastWeekLi.add(new AttributeModifier(CLASS, empty));
			break;
		case LAST_MONTH:
			thisMonthLi.add(new AttributeModifier(CLASS, empty));
			thisWeekLi.add(new AttributeModifier(CLASS, empty));
			lastMonthLi.add(new AttributeModifier(CLASS, active));
			lastWeekLi.add(new AttributeModifier(CLASS, empty));
			break;
		case LAST_WEEK:
			thisMonthLi.add(new AttributeModifier(CLASS, empty));
			thisWeekLi.add(new AttributeModifier(CLASS, empty));
			lastMonthLi.add(new AttributeModifier(CLASS, empty));
			lastWeekLi.add(new AttributeModifier(CLASS, active));
			break;
		}

		target.add(thisMonthLi);
		target.add(thisWeekLi);
		target.add(lastMonthLi);
		target.add(lastWeekLi);
	}

	private void setClassForIncludeTodaySwitch() {

		Model<String> includeTodaySwitchModel;
		if (includeToday) {
			includeTodaySwitchModel = new Model<>("fa fa-eye");
		} else {
			includeTodaySwitchModel = new Model<>("fa fa-eye-slash");
		}

		includeTodaySwitchIcon.add(new AttributeModifier(CLASS, includeTodaySwitchModel));
	}

	public void setPairedElement(Object o) {

		this.pairedGraph = (CPieChart) o;

	}

	public void refreshPairedElement(AjaxRequestTarget target) {

		if (pairedGraph != null) {
			pairedGraph.refresh(target, timeRange, includeToday);
		}
	}
}
