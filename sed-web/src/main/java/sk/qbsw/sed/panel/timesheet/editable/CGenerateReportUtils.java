package sk.qbsw.sed.panel.timesheet.editable;

import java.util.Calendar;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.request.CGenerateEmployeesReportRequest;
import sk.qbsw.sed.client.request.CGenerateReportRequest;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.client.ui.screen.report.IReportConstants;
import sk.qbsw.sed.framework.generator.IGenerator;

public class CGenerateReportUtils {

	private CGenerateReportUtils() {
		// Auto-generated constructor stub
	}

	public static CGenerateReportRequest getParams(Long reportType, CSubrodinateTimeStampBrwFilterCriteria filter, CLoggedUserRecord loggedUser, boolean alsoNotConfirmed) {

		Calendar from = Calendar.getInstance();
		from.setTime(filter.getDateFrom());

		Calendar to = Calendar.getInstance();
		to.setTime(filter.getDateTo());

		if (IReportConstants.MONTH_EMPLOYEE_REPORT.equals(reportType)) {
			from.set(Calendar.DAY_OF_MONTH, 1);
			to = (Calendar) from.clone();
			to.set(Calendar.DAY_OF_MONTH, to.getActualMaximum(Calendar.DAY_OF_MONTH));
		}

		String screenType;

		if (IUserTypeCode.ORG_ADMIN.equals(loggedUser.getRoleCode())) {
			screenType = IGenerator.EMPLOYEES_REPORT_SCREEN;
		} else if (filter.getEmplyees().contains(loggedUser.getUserId()) && filter.getEmplyees().size() == 1) {
			screenType = IGenerator.MY_REPORT_SCREEN;
		} else {
			screenType = IGenerator.SUBORDINATE_REPORT_SCREEN;
		}

		CGenerateReportRequest request = new CGenerateReportRequest();
		request.setAlsoNotConfirmed(alsoNotConfirmed);
		request.setReportType(reportType);
		request.setDateFrom(from);
		request.setDateTo(to);
		request.setScreenType(screenType);
		request.setUserIds(filter.getEmplyees());

		return request;
	}

	public static CGenerateEmployeesReportRequest getParams(Long clientId, Boolean onlyValid) {

		CGenerateEmployeesReportRequest request = new CGenerateEmployeesReportRequest();
		request.setClientId(clientId);
		request.setOnlyValid(onlyValid);

		return request;
	}
}
