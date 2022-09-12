package sk.qbsw.sed.panel.timesheet;

import java.util.Date;

import sk.qbsw.sed.client.model.IActivityConstant;

public class CTimesheetValidations {

	private CTimesheetValidations() {
		// Auto-generated constructor stub
	}

	public static String validateActivityDuration(Long activity, Long reason, Date timeFrom, Date timeTo) {
		long lDuration = 0l;

		if (timeFrom != null && timeTo != null) {
			long v1 = CTimesheetValidations.getDuration(timeFrom);
			long v2 = CTimesheetValidations.getDuration(timeTo);
			lDuration = (v2 - v1);
		}

		return CTimesheetValidations.validateActivityDuration(activity, reason, lDuration);
	}

	private static String validateActivityDuration(Long activity, Long reason, long lDuration) {
		// v systeme sa minuty pocitaju takto (0 minut 59 sekund je 1 minuta!)
		long hours8 = 1000l * 3600l * 8l - 60000l;
		long hours4 = 1000l * 3600l * 4l - 60000l;

		if (IActivityConstant.NOT_WORK_HOLIDAY.equals(activity) && (hours8 != lDuration) && (hours4 != lDuration)) {
			return "activity.activity_duration_error";
		}
		if (IActivityConstant.NOT_WORK_REPLWORK.equals(activity) && (hours8 != lDuration)) {
			return "activity.activity_duration_error";
		}
		if (IActivityConstant.NOT_WORK_SICKNESS.equals(activity) && (hours8 != lDuration)) {
			return "activity.activity_duration_error";
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private static long getDuration(Date dDate) {
		int h = dDate.getHours();
		int m = dDate.getMinutes();

		long result = (h * 3600l + m * 60l) * 1000l;

		return result;
	}
}
