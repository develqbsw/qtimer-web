package sk.qbsw.sed.component.calendar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;

import sk.qbsw.sed.common.utils.CDateRange;
import sk.qbsw.sed.common.utils.CDateUtils;

public class CDateRangePicker extends WebMarkupContainer {
	
    private static final long serialVersionUID = 1L;
    private Properties prop = null;
    private boolean showTimePicker = false;
    private boolean addToInput = false;
    private String dateRangeComponent = "";

    private SupportedDefaults selectedDefault;

    private CDateRange dateRange;
    
    private static final String RANGES = " ranges: { ";
    
    private static final String YESTERDAY = "yesterday";
    
    private static final String LAST_WEEK = "lastWeek";
    
    private static final String LAST_MONTH = "lastMonth";
    
    private static final String TODAY = "today";
    
    private static final String ACTUAL_WEEK = "actualWeek";

    private static final String NEXT_WEEK = "nextWeek";
    
    private static final String NEXT_30_DAYS = "next30Days";
    
    private static final String ACTUAL_MONTH = "actualMonth";
    
	public enum SupportedDefaults {
		VYKAZ_PRACE, ZIADOSTI, SPRACOVANIE_NEPRITOMNOSTI, STATS, GENERATE
	}

	public CDateRangePicker(String id, SupportedDefaults selectedDefault, boolean addToInput, boolean showTimePicker) {
		super(id);
		this.selectedDefault = selectedDefault;
		this.showTimePicker = showTimePicker;
		this.addToInput = addToInput;
		init();
	}

	public CDateRangePicker(String id, SupportedDefaults selectedDefault, boolean addToInput) {
		super(id);
		this.selectedDefault = selectedDefault;
		this.addToInput = addToInput;
		init();
	}

	public CDateRangePicker(String id, SupportedDefaults selectedDefault) {
		super(id);
		this.selectedDefault = selectedDefault;
		init();
	}

	public CDateRangePicker(String id, SupportedDefaults selectedDefault, CDateRange dateRange) {
		super(id);
		this.selectedDefault = selectedDefault;
		this.dateRange = dateRange;
		init();
	}

	public CDateRangePicker(String id) {
		super(id);
		init();
	}

    private void init() {

	if (addToInput) {
	    dateRangeComponent = "input";
	}

		try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("dateRangePicker_" + this.getLocale().getLanguage() + ".properties")) {
			this.prop = new Properties();

			if (input == null) {
				throw new IOException();
			}
			prop.load(input);
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getName()).info("Unable to find localized properties for locale: " + this.getLocale().getLanguage(), ex);
		}

	this.add(new Behavior() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(Component component, IHeaderResponse response) {
		CDateRangePicker.this.setOutputMarkupId(true);
		response.render(OnDomReadyHeaderItem.forScript("$('#" + CDateRangePicker.this.getMarkupId() + " "
			+ dateRangeComponent + "').daterangepicker(" + "{ " + "    format: 'DD.MM.YYYY', "
			+ "    opens: 'right', " + "	   timePicker: " + showTimePicker + " , "
			+ "	   timePicker12Hour: false, " + "	   timePickerIncrement:    1," + getRanges()
			+ getLocaleForDateRangePicker() + " }, function(start, end, label) { "
		// + " console.log(start.toISOString(), end.toISOString(),
		// label); "
			+ formatSelectedValues() + "			$('#" + CDateRangePicker.this.getMarkupId()
			+ " input').change(); " + "		} " + ")"));

		// Tato cast je tu pretoze po vybere z "ranges" sa kalendar schova okrem pripadu custom. Tymto je zabezpecene ze sa zobrazi vzdy.
		response.render(OnDomReadyHeaderItem.forScript("$('#" + CDateRangePicker.this.getMarkupId()
			+ "').on('show.daterangepicker', function(ev, picker) {" + " picker.showCalendars(); " + "})"));

	    }
	});

    }

    private String getRanges() {

	if (prop != null) {
	    if (selectedDefault != null) {
		switch (selectedDefault) {
		case VYKAZ_PRACE:
		case SPRACOVANIE_NEPRITOMNOSTI:
		    return RANGES

			    + (prop.getProperty(YESTERDAY) != null ? "'" + prop.getProperty(YESTERDAY) + "'"
				    + ": [moment().subtract(1, 'days'), moment().subtract(1, 'days')], " : "")
			    + (prop.getProperty(LAST_WEEK) != null
				    ? "'" + prop.getProperty(LAST_WEEK) + "'"
					    + ": [moment().subtract(1, 'week').startOf('week'), moment().subtract(1, 'week').endOf('week')], "
				    : "")
			    + (prop.getProperty(LAST_MONTH) != null
				    ? "'" + prop.getProperty(LAST_MONTH) + "'"
					    + ": [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')], "
				    : "")
			    + (prop.getProperty(TODAY) != null
				    ? "'" + prop.getProperty(TODAY) + "'" + ": [moment(), moment()], " : "")
			    + (prop.getProperty(ACTUAL_WEEK) != null ? "'" + prop.getProperty(ACTUAL_WEEK) + "'"
				    + ": [moment().startOf('week'), moment().endOf('week')], " : "")
			    + "}, ";
		case GENERATE:
		    return RANGES

			    + (prop.getProperty(YESTERDAY) != null ? "'" + prop.getProperty(YESTERDAY) + "'"
				    + ": [moment().subtract(1, 'days'), moment().subtract(1, 'days')], " : "")
			    + (prop.getProperty(LAST_WEEK) != null
				    ? "'" + prop.getProperty(LAST_WEEK) + "'"
					    + ": [moment().subtract(1, 'week').startOf('week'), moment().subtract(1, 'week').endOf('week')], "
				    : "")
			    + (prop.getProperty(LAST_MONTH) != null
				    ? "'" + prop.getProperty(LAST_MONTH) + "'"
					    + ": [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')], "
				    : "")
			    + (prop.getProperty(TODAY) != null
				    ? "'" + prop.getProperty(TODAY) + "'" + ": [moment(), moment()], " : "")
			    + (prop.getProperty(ACTUAL_WEEK) != null ? "'" + prop.getProperty(ACTUAL_WEEK) + "'"
				    + ": [moment().startOf('week'), moment().endOf('week')], " : "")
			    + "}, " + " 'startDate': '" + CDateUtils.convertToDateString(dateRange.getDateFrom())
			    + "', " + " 'endDate': '" + CDateUtils.convertToDateString(dateRange.getDateTo()) + "', ";
		case ZIADOSTI:
		    return RANGES

			    + (prop.getProperty(TODAY) != null
				    ? "'" + prop.getProperty(TODAY) + "'" + ": [moment(), moment()], " : "")
			    + (prop.getProperty(ACTUAL_WEEK) != null ? "'" + prop.getProperty(ACTUAL_WEEK) + "'"
				    + ": [moment().startOf('week'), moment().endOf('week')], " : "")
			    + (prop.getProperty(NEXT_WEEK) != null
				    ? "'" + prop.getProperty(NEXT_WEEK) + "'"
					    + ": [moment().add(1, 'week').startOf('week'), moment().add(1, 'week').endOf('week')], "
				    : "")
			    + (prop.getProperty(NEXT_30_DAYS) != null ? "'" + prop.getProperty(NEXT_30_DAYS) + "'"
				    + ": [moment(), moment().add(30, 'days')], " : "")

			    + "}," + " 'startDate': moment(), " + " 'endDate': moment().add(30, 'days'), ";
		case STATS:
		    return RANGES

			    + (prop.getProperty("previousYear") != null
				    ? "'" + prop.getProperty("previousYear") + "'"
					    + ": [moment().subtract(1, 'year').startOf('year'), moment().subtract(1, 'year').endOf('year')], "
				    : "")
			    + (prop.getProperty(LAST_MONTH) != null
				    ? "'" + prop.getProperty(LAST_MONTH) + "'"
					    + ": [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')], "
				    : "")
			    + (prop.getProperty(ACTUAL_MONTH) != null ? "'" + prop.getProperty(ACTUAL_MONTH) + "'"
				    + ": [moment().startOf('month'), moment().endOf('month')], " : "")
			    + (prop.getProperty("actualYear") != null ? "'" + prop.getProperty("actualYear") + "'"
				    + ": [moment().startOf('year'), moment().endOf('year')], " : "")
			    + "}, " + " 'startDate':moment().subtract(1, 'month').startOf('month'), "
			    + " 'endDate': moment().subtract(1, 'month').endOf('month'), ";
		default: {
		    return "";
		}
		}
	    } else {
		return RANGES
			+ (prop.getProperty(LAST_MONTH) != null
				? "'" + prop.getProperty(LAST_MONTH) + "'"
					+ ": [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')], "
				: "")
			+ (prop.getProperty(YESTERDAY) != null ? "'" + prop.getProperty(YESTERDAY) + "'"
				+ ": [moment().subtract(1, 'days'), moment().subtract(1, 'days')], " : "")
			+ (prop.getProperty(LAST_WEEK) != null
				? "'" + prop.getProperty(LAST_WEEK) + "'"
					+ ": [moment().subtract(1, 'week').startOf('week').add(1, 'days'), moment().subtract(1, 'week').endOf('week').add(1, 'days')], "
				: "")

			+ (prop.getProperty(TODAY) != null
				? "'" + prop.getProperty(TODAY) + "'" + ": [moment(), moment()], " : "")
			+ (prop.getProperty(ACTUAL_WEEK) != null
				? "'" + prop.getProperty(ACTUAL_WEEK) + "'"
					+ ": [moment().startOf('week').add(1, 'days'), moment().endOf('week').add(1, 'days')], "
				: "")
			+ (prop.getProperty(ACTUAL_MONTH) != null ? "'" + prop.getProperty(ACTUAL_MONTH) + "'"
				+ ": [moment().startOf('month'), moment().endOf('month')], " : "")
			+ (prop.getProperty("tomorrow") != null ? "'" + prop.getProperty("tomorrow") + "'"
				+ ": [moment().add(1, 'days'), moment().add(1, 'days')], " : "")
			+ (prop.getProperty(NEXT_WEEK) != null
				? "'" + prop.getProperty(NEXT_WEEK) + "'"
					+ ": [moment().add(1, 'week').startOf('week').add(1, 'days'), moment().add(1, 'week').endOf('week').add(1, 'days')], "
				: "")
			+ (prop.getProperty("nextMonth") != null
				? "'" + prop.getProperty("nextMonth") + "'"
					+ ": [moment().add(1, 'month').startOf('month'), moment().add(1, 'month').endOf('month')], "
				: "")
			+ (prop.getProperty(NEXT_30_DAYS) != null
				? "'" + prop.getProperty(NEXT_30_DAYS) + "'" + ": [moment(), moment().add(30, 'days')] "
				: "")
			+ "}, ";
	    }

	} else {
	    return "";
	}
    }

    private String getLocaleForDateRangePicker() {
	if (prop != null) {

	    return " locale: { "
		    + (prop.getProperty("applyLabel") != null
			    ? " applyLabel: '" + prop.getProperty("applyLabel") + "', " : "")
		    + (prop.getProperty("cancelLabel") != null
			    ? " cancelLabel: '" + prop.getProperty("cancelLabel") + "', " : "")
		    + (prop.getProperty("fromLabel") != null ? " fromLabel: '" + prop.getProperty("fromLabel") + "', "
			    : "")
		    + (prop.getProperty("toLabel") != null ? " toLabel: '" + prop.getProperty("toLabel") + "', " : "")
		    + (prop.getProperty("customRangeLabel") != null
			    ? " customRangeLabel: '" + prop.getProperty("customRangeLabel") + "', " : "")
		    + ((prop.getProperty("january") != null && prop.getProperty("february") != null
			    && prop.getProperty("march") != null && prop.getProperty("april") != null
			    && prop.getProperty("may") != null && prop.getProperty("june") != null
			    && prop.getProperty("july") != null && prop.getProperty("august") != null
			    && prop.getProperty("september") != null && prop.getProperty("october") != null
			    && prop.getProperty("november") != null && prop.getProperty("december") != null)
				    ? " monthNames: [" + "'" + prop.getProperty("january") + "', " + "'"
					    + prop.getProperty("february") + "', " + "'" + prop.getProperty("march")
					    + "', " + "'" + prop.getProperty("april") + "', " + "'"
					    + prop.getProperty("may") + "', " + "'" + prop.getProperty("june") + "', "
					    + "'" + prop.getProperty("july") + "', " + "'" + prop.getProperty("august")
					    + "', " + "'" + prop.getProperty("september") + "', " + "'"
					    + prop.getProperty("october") + "', " + "'" + prop.getProperty("november")
					    + "', " + "'" + prop.getProperty("december") + "' " + "], "
				    : "")
		    + ((prop.getProperty("monday") != null && prop.getProperty("tuesday") != null
			    && prop.getProperty("wednesday") != null && prop.getProperty("thursday") != null
			    && prop.getProperty("friday") != null && prop.getProperty("saturday") != null
			    && prop.getProperty("sunday") != null)
				    ? " daysOfWeek: [" + "'" + prop.getProperty("sunday") + "', " + "'"
					    + prop.getProperty("monday") + "', " + "'" + prop.getProperty("tuesday")
					    + "', " + "'" + prop.getProperty("wednesday") + "', " + "'"
					    + prop.getProperty("thursday") + "', " + "'" + prop.getProperty("friday")
					    + "', " + "'" + prop.getProperty("saturday") + "' " + "], "
				    : "")
		    + " firstDay: " + (prop.getProperty("firstDay") != null ? prop.getProperty("firstDay") : "0")
		    + "} ";

	} else {
	    return "";
	}
    }

    private String formatSelectedValues() {

	if (showTimePicker) {

	    return "	$('#" + CDateRangePicker.this.getMarkupId()
		    + " span.dateRangePickerSpan').html(start.format('DD.MM.YYYY HH:mm') + ' - ' + end.format('HH:mm')); "
		    + "	$('#" + CDateRangePicker.this.getMarkupId()
		    + " input').val(start.format('DD.MM.YYYY HH:mm') + ' - ' + end.format('HH:mm')); ";

	} else {

	    return "	$('#" + CDateRangePicker.this.getMarkupId()
		    + " span.dateRangePickerSpan').html(start.format('DD.MM.YYYY') + ' - ' + end.format('DD.MM.YYYY')); "
		    + "	$('#" + CDateRangePicker.this.getMarkupId()
		    + " input').val(start.format('DD.MM.YYYY') + ' - ' + end.format('DD.MM.YYYY')); ";

	}
    }
}
