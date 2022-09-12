package sk.qbsw.sed.server.service.report;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.request.CGenerateEmployeesReportRequest;
import sk.qbsw.sed.client.request.CGenerateReportRequest;
import sk.qbsw.sed.client.service.business.IUserService;
import sk.qbsw.sed.client.ui.screen.report.IReportConstants;
import sk.qbsw.sed.framework.generator.AAbstractGenerator;
import sk.qbsw.sed.framework.report.generator.name.IReportNameGenerator;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.util.CStringUtils;

@Service
public class CReportNameGeneratorImpl extends AAbstractGenerator implements IReportNameGenerator {

	private static final String DEFAULT_EXTENSION = ".xls";

	@Autowired
	private IUserService userService;

	@Override
	protected void doInitialize(CGenerateReportRequest request) throws IOException {
		// nothing...
	}

	@Override
	public String generateReportName(CGenerateReportRequest request) throws Exception {
		String reportName;
		final String dateFrom = extractFromDate(request.getDateFrom().getTime());
		final String[] parsedDateFrom = dateFrom.split("-");
		final String dateTo = extractFromDate(request.getDateTo().getTime());
		final String[] parsedDateTo = dateTo.split("-");

		final String reportType = request.getReportType().toString();

		reportName = parsedDateFrom[2] + this.getTwoDigitsValue(parsedDateFrom[1]) + this.getTwoDigitsValue(parsedDateFrom[0]);
		reportName += "_";
		reportName += parsedDateTo[2] + this.getTwoDigitsValue(parsedDateTo[1]) + this.getTwoDigitsValue(parsedDateTo[0]) + "_";

		if (IReportConstants.SUMMARY_REPORT.toString().equals(reportType)) {
			// summary report
			reportName = "summary_report";
			reportName += DEFAULT_EXTENSION;
		} else if (IReportConstants.MONTH_EMPLOYEE_REPORT.toString().equals(reportType)) {
			final CUserDetailRecord user = getUser(request.getUserIds().iterator().next());
			reportName = parsedDateFrom[2];
			String sMonth = parsedDateFrom[1];
			if (sMonth.length() == 1) {
				sMonth = "0" + sMonth;
			}
			reportName += sMonth;
			reportName += "_M_";
			reportName += user.getSurname() + "_" + user.getName() + DEFAULT_EXTENSION;
		} else if (IReportConstants.MONTH_ACCOUNTANT_REPORT.toString().equals(reportType)) {
			reportName = parsedDateFrom[2] + this.getTwoDigitsValue(parsedDateFrom[1]) + this.getTwoDigitsValue(parsedDateFrom[0]);
			reportName += "_";
			reportName += parsedDateTo[2] + this.getTwoDigitsValue(parsedDateTo[1]) + this.getTwoDigitsValue(parsedDateTo[0]);

			if (request.getUserIds().size() > 1) {
				reportName += "_month_pay_employees";
			} else if (request.getUserIds().size() == 1) {
				final CUserDetailRecord user = getUser(request.getUserIds().iterator().next());
				reportName += "_" + user.getSurname() + "_" + user.getName();
			}

			reportName += DEFAULT_EXTENSION;

		} else {
			if (request.getUserIds().size() > 1) {
				reportName += request.getUserIds().size() + "_users" + DEFAULT_EXTENSION;
			} else {
				final CUserDetailRecord user = getUser(request.getUserIds().iterator().next());
				reportName += user.getSurname() + "_" + user.getName() + DEFAULT_EXTENSION;
			}
		}

		reportName = CStringUtils.convertNonAscii(reportName);
		return reportName;
	}

	private String getTwoDigitsValue(final String value, final String prefix) {
		if (value.length() == 1) {
			return prefix + value;
		}
		return value;
	}

	private String getTwoDigitsValue(final String value) {
		return this.getTwoDigitsValue(value, "0");
	}

	private CUserDetailRecord getUser(final Long userId) throws CBusinessException {
		return userService.getUserDetails(userId);
	}

	@SuppressWarnings("deprecation")
	private static String extractFromDate(final Date date) {
		return date.getDate() + "-" + (date.getMonth() + 1) + "-" + (date.getYear() + 1900);
	}

	@Override
	protected void doInitialize(CGenerateEmployeesReportRequest request, boolean employeesReport) throws IOException {
		// nič
	}
}
