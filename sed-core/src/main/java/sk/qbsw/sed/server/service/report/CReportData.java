package sk.qbsw.sed.server.service.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IRequestReasonConstants;
import sk.qbsw.sed.client.model.IUserTypes;
import sk.qbsw.sed.client.ui.screen.report.IReportConstants;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.report.IReportData;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.framework.report.model.CSummaryValues;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.dao.IMonthEmploeeWorksheetDao;
import sk.qbsw.sed.server.dao.IProjectDao;
import sk.qbsw.sed.server.dao.IRequestReasonDao;
import sk.qbsw.sed.server.dao.IRequestTypeDao;
import sk.qbsw.sed.server.dao.ISummaryWorksheetDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.IViewEmployeesDao;
import sk.qbsw.sed.server.dao.IViewMonthSheetDao;
import sk.qbsw.sed.server.dao.IViewWeekSheetDao;
import sk.qbsw.sed.server.model.brw.CViewTimeStamp;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.codelist.CProject;
import sk.qbsw.sed.server.model.codelist.CRequestReason;
import sk.qbsw.sed.server.model.codelist.CRequestType;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.report.CMonthReportDailyRecord;
import sk.qbsw.sed.server.model.report.CPvPData;
import sk.qbsw.sed.server.model.report.useful.CActivityData;
import sk.qbsw.sed.server.model.report.useful.CProjectData;
import sk.qbsw.sed.server.model.report.useful.CUserData;
import sk.qbsw.sed.server.service.codelist.IActivityConstant;
import sk.qbsw.sed.server.util.CDateServerUtils;
import sk.qbsw.sed.server.util.CLocaleUtils;

/**
 * Fills XLS template by data with respect to type of report
 * 
 * @author egyud, rosenberg
 * 
 */
@Service
public class CReportData implements IReportData {
	
	private final Logger logger = Logger.getLogger(CReportData.class.getName());

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IViewEmployeesDao viewUserDao;

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IActivityDao activityDao;

	@Autowired
	private IRequestReasonDao requestReasonDao;

	@Autowired
	private IRequestTypeDao requestTypeDao;

	@Autowired
	private IViewWeekSheetDao<CReportModel> weekSheetDao;

	@Autowired
	private IViewMonthSheetDao<CReportModel> monthSheetDao;

	@Autowired
	private ISummaryWorksheetDao<CReportModel> summarySheetDao;

	@Autowired
	private IMonthEmploeeWorksheetDao<CViewTimeStamp> employeeMonthSheetDao;

	@Autowired
	private MessageSource messages;

	@Autowired
	private IHolidayDao holidayDao;

	@Transactional(readOnly = true)
	@Override
	public CComplexInputReportModel getModel(final Long reportType, final Set<Long> userIds, final Calendar dateFrom, final Calendar dateTo, final boolean alsoNotConfirmed, final String screenType)
			throws CBusinessException {
		CComplexInputReportModel complexModel = new CComplexInputReportModel();

		CUser tmpUser = null;
		if (userIds != null && !userIds.isEmpty()) {
			tmpUser = this.userDao.findById(userIds.iterator().next());
			complexModel.setClientId(tmpUser.getClient().getId());
		}

		List<CReportModel> reportRows = null;
		if (IReportConstants.BASIC_SHORTLY_REPORT.equals(reportType)) {
			reportRows = this.monthSheetDao.findByUsersAndPeriod(userIds, dateFrom, dateTo, alsoNotConfirmed, screenType);
			complexModel.setReportRows(reportRows);

		} else if (IReportConstants.BASIC_WEEKLY_REPORT.equals(reportType)) {
			reportRows = this.weekSheetDao.findByUsersAndPeriod(userIds, dateFrom, dateTo, alsoNotConfirmed, screenType);
			complexModel.setReportRows(reportRows);

		} else if (IReportConstants.SUMMARY_REPORT.equals(reportType)) {
			reportRows = this.summarySheetDao.findByUsersAndPeriod(userIds, dateFrom, dateTo, alsoNotConfirmed, screenType);
			String invalidDataUserName = summaryReportDataChecker(reportRows);
			if (invalidDataUserName != null) {

				// spravime to takto - chybova sprava na gui bude, vygeneruje sa
				// prazdny vykaz
				reportRows = new ArrayList<>();
			}
			complexModel.setReportRows(reportRows);

		} else if (IReportConstants.MONTH_EMPLOYEE_REPORT.equals(reportType)) {
			// spracovanie viacerych zamestancov naraz ?!? - spracuvame udaje jedneho, lebo aj ked by sme udaje vedeli
			// pripravit, nemame ich kam dat! museli by sme zmenit naplnanie vystupneho reportu
			processUserMonthData(tmpUser, dateFrom, dateTo, complexModel, alsoNotConfirmed, screenType);
		} else if (IReportConstants.MONTH_ACCOUNTANT_REPORT.equals(reportType)) {
			complexModel.setDateFrom(CDateUtils.convertToDateString(dateFrom));
			complexModel.setDateTo(CDateUtils.convertToDateString(dateTo));
			complexModel.setWorkingDays(CDateServerUtils.getWorkingDaysCheckHolidays(dateFrom.getTime(), dateTo.getTime(), holidayDao.findAllValidClientsHolidays(complexModel.getClientId(), null)));
			processAccountantMonthData(userIds, dateFrom, dateTo, complexModel, alsoNotConfirmed, screenType);
		} else {
			complexModel.setReportRows(reportRows);

			logger.error("Unknown report type: " + reportType);
			throw new CBusinessException("Unknown report type: " + reportType);
		}

		this.setCodebooks(complexModel, tmpUser);

		return complexModel;
	}

	/**
	 * Fills code book: projects, activities, employees
	 * 
	 * @param complexModel
	 */
	private void setCodebooks(final CComplexInputReportModel complexModel, final CUser tmpUser) {
		if (complexModel.getReportRows().size() > 0) {
			List<CProject> projects = this.projectDao.findAll(complexModel.getClientId());
			for (CProject project : projects) {
				CProjectData projectData = new CProjectData();
				projectData.setPrjId(project.getEviproCode());
				projectData.setPrjName(project.getName());
				projectData.setPrjGroup(project.getGroup());
				projectData.setPrjFinder(getProjectFinderFromProjectName(project.getName(), project.getEviproCode(), project.getGroup()));
				complexModel.getProjectsData().add(projectData);
			}

			List<CUser> users = this.userDao.findAllEmployees(tmpUser, IUserTypes.EMPLOYEE, false);
			for (CUser user : users) {
				CUserData userData = new CUserData();
				userData.setEmployeeId(user.getEmployeeCode());
				userData.setEmployeeName(user.getSurname() + " " + user.getName());
				complexModel.getUsersData().add(userData);
			}

			List<CActivity> activities = this.activityDao.findAll(complexModel.getClientId(), true, true, false);
			for (CActivity activity : activities) {
				CActivityData activityData = new CActivityData();
				activityData.setActivityName(activity.getName());
				complexModel.getActivitiesData().add(activityData);
			}
		}
	}

	private String getProjectFinderFromProjectName(String prjName, String prjId, String group) {
		String[] part = prjName.split(" ");
		if (part.length > 0) {
			int idx1 = part[0].indexOf(".");
			if (idx1 != -1) {
				String check = part[0].substring(0, idx1 + 1);
				if (part[0].equals(check + prjId)) {
					return part[0];
				}
			}
		}

		return group + "." + prjId;
	}

	private void processUserMonthData(CUser user, final Calendar dateFrom, final Calendar dateTo, CComplexInputReportModel complexModel, final boolean alsoNotConfirmed, final String screenType)
			throws CBusinessException {
		String userName = (user.getSurname() == null ? "" : user.getSurname()) + " " + (user.getName() == null ? "" : user.getName());
		complexModel.setReportName(userName);
		complexModel.setMonth("" + (dateFrom.get(Calendar.MONTH) + 1));
		complexModel.setYear("" + dateFrom.get(Calendar.YEAR));

		// find all "raw" rows ...
		List<CViewTimeStamp> reportRawRows = this.employeeMonthSheetDao.findByUserAndPeriod(user.getId(), dateFrom, dateTo, alsoNotConfirmed, screenType);
		// ... group ones
		Map<Long, LinkedList<CMonthReportDailyRecord>> groupedResult = this.groupModel(reportRawRows, false);
		// set to model
		complexModel.getReportRows().addAll(this.convertMonthDayModel(groupedResult));

		// add missing (also holidays) days of the month
		List<CHoliday> clientHolidays = this.holidayDao.findAllValidClientsHolidays(user.getClient().getId(), dateFrom.get(Calendar.YEAR));
		addMissingMonthDays(complexModel.getReportRows(), user, dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), clientHolidays);

		CSummaryValues processedUserSumValues = new CSummaryValues();
		if (groupedResult.get(user.getId()) != null) {
			// set additional output model values
			processedUserSumValues.setEmployeeName(userName);
			processedUserSumValues.setEmployeeFirstName(user.getName());
			processedUserSumValues.setEmployeeSurname(user.getSurname());

			calcStatisticsValues(user.getClient().getId(), groupedResult.get(user.getId()), processedUserSumValues);
		} else {
			// do nothing
		}

		// ... add marks for free days, when employee worked ...
		markFreeDaysInWork(complexModel.getReportRows(), clientHolidays);

		// ... sort grouped daily records ...
		Collections.sort(complexModel.getReportRows(), new CReportModelComparator());

		complexModel.getUserSummaryParameters().put(user.getId(), processedUserSumValues);
	}

	private void processAccountantMonthData(Set<Long> userIds, final Calendar dateFrom, final Calendar dateTo, CComplexInputReportModel complexModel, final boolean alsoNotConfirmed,
			final String screenType) throws CBusinessException {
		complexModel.setReportName("month_pay_employees");
		complexModel.setMonth("" + (dateFrom.get(Calendar.MONTH) + 1));
		complexModel.setYear("" + dateFrom.get(Calendar.YEAR));

		if (userIds == null || userIds.size() < 0) {
			// last check before processing
			throw new CBusinessException("zoznam zamestnancov pre ktorych ma byt pripraveny vystup je prazdny! Chybu ohlaste spravcovi aplikacie.");
		}

		List<CViewTimeStamp> usersRawRows = new ArrayList<>();
		for (Long userId : userIds) {
			// find all "raw" rows for user
			List<CViewTimeStamp> userRawRows = this.employeeMonthSheetDao.findByUserAndPeriod(userId, dateFrom, dateTo, alsoNotConfirmed, screenType);
			usersRawRows.addAll(userRawRows);
		}
		// ... group ones
		Map<Long, LinkedList<CMonthReportDailyRecord>> groupedResult = this.groupModel(usersRawRows, true);
		// set to model
		complexModel.getReportRows().addAll(this.convertMonthDayModel(groupedResult));

		// select client holidays
		Long clientId = this.userDao.findById(userIds.iterator().next()).getClient().getId();
		List<CHoliday> clientHolidays = this.holidayDao.findAllValidClientsHolidays(clientId, dateFrom.get(Calendar.YEAR));
		String numberClientHolidaysInDateInterval = getNumberClientHolidaysInDateInterval(clientHolidays, dateFrom, dateTo);

		for (Long userId : userIds) {
			if (groupedResult.get(userId) != null) {
				// set additional output model values for all users
				// with records in target date interval
				CSummaryValues userSumValues = new CSummaryValues();

				CUser user = this.userDao.findById(userId);

				userSumValues.setEmployeeName(getUsername(user));
				if (user != null) {
					userSumValues.setEmployeeFirstName(user.getName());
					userSumValues.setEmployeeSurname(user.getSurname());

					if (user.getEmploymentType() != null) {
						userSumValues.setEmploymentTypeId(user.getEmploymentType().getId());
						userSumValues.setEmploymentTypeDescription(user.getEmploymentType().getDescription());
					} else {
						userSumValues.setEmploymentTypeId(Long.valueOf(4));
						userSumValues.setEmploymentTypeDescription("Neuvedený");
					}

				}
				userSumValues.setHolidays(numberClientHolidaysInDateInterval);

				calcStatisticsValues(complexModel.getClientId(), groupedResult.get(userId), userSumValues);

				complexModel.getUserSummaryParameters().put(userId, userSumValues);
			}
		}
	}

	private String getNumberClientHolidaysInDateInterval(List<CHoliday> days, Calendar dateFrom, Calendar dateTo) {
		Integer counter = new Integer(0);
		Integer counterInWork = new Integer(0);
		for (CHoliday day : days) {
			if ((day.getDay().after(dateFrom) || day.getDay().equals(dateFrom)) && (day.getDay().before(dateTo) || day.getDay().equals(dateTo))) {
				counter++;
				if (CDateUtils.isWorkingDay(day.getDay().getTime())) {
					counterInWork++;
				}
			}
		}

		return "" + counterInWork + "/" + counter;
	}

	private String getUsername(CUser user) {
		if (user != null) {
			return (user.getSurname() == null ? "" : user.getSurname()) + " " + (user.getName() == null ? "" : user.getName());
		} else {
			return "";
		}
	}

	/**
	 * Returns names of the users with invalid data or null in case: all data valid
	 * 
	 * @param model
	 * @return
	 */
	private String summaryReportDataChecker(List<CReportModel> model) {
		String retVal = null;
		List<String> userNames = new ArrayList<>();
		for (CReportModel rowModel : model) {
			if (rowModel.getCalendar(7) == null || rowModel.getString(8) == null) {
				String userName = rowModel.getString(3);
				if (userNames.contains(userName) == false) {
					userNames.add(userName);
				}
			}
		}

		if (!userNames.isEmpty()) {

			Locale locale = CLocaleUtils.getLocale();
			retVal = messages.getMessage("messages.list_users_summary_report_error", null, locale) + System.getProperty("line.separator");
			for (String name : userNames) {
				retVal += name + System.getProperty("line.separator");
			}
		}

		return retVal;
	}

	/**
	 * Calculate statistics:
	 * 
	 * Vo vygenerovanom mesacnom vykaze treba doplnit polia (SED-239): </br>
	 * 
	 * <li>Spolu - súčet pracovných aktivít za mesiac a zamestnanca</li>
	 * 
	 * <li>Priemer - priemer odpracovaných hodín za mesiac a to len za pracovné dni,
	 * ktoré zamestnanec odpracoval, teda "Spolu" deleno "Počet odpracovaných prac.
	 * dní"</li>
	 * 
	 * <li>Počet odpracovaných prac. dní - počet pracovných dní, ktoré zamestnanec
	 * odpracoval (do pracovných dní nepočítať čerpanú neprítomnosť aj keď to padlo
	 * na pracovný deň a ani sobotu a nedeľu aj keď bol v práci)</li>
	 * 
	 * <li>Počet dni dovolenky za mesiac: počet dovoleniek, ktoré zamestnanec čerpal
	 * počas mesiaca (dovolenka padla na pracovný deň) Pozn. V prípade pol-dňa
	 * dovolenky sa do počtu započítava hodnota 0,5.</li>
	 * 
	 * <li>Počet dni nahradneho volna za mesiac: počet dní náhradného voľna, ktoré
	 * zamestnanec čerpal počas mesiaca (NV padol na pracovný deň)</li>
	 * 
	 * <li>Počet dni PN za mesiac: počet PN dní, ktoré zamestnanec čerpal počas
	 * mesiaca (PN padol na pracovný deň)</li>
	 * 
	 * <li>Počet dni PvP za mesiac: počet dní strávených na prekážkach v práci,
	 * ktoré zamestnanec čerpal počas mesiaca (PvP padol na pracovný deň)</li>
	 * 
	 * <li>Počet dni na Paragraf za mesiac: počet dní stravených na paragrafe, ktoré
	 * zamestnanec absolvoval počas mesiaca (P padol na pracovný deň) Pozn. Nevadí,
	 * že počas dňa čerpal paragraf len pár hodín.</li>
	 * 
	 * @param groupedRows       input data input grouped rows
	 * @param sumOfWorkHours    output value sum of the work hours
	 * @param dailyAverage      output value daily (work hours) average for the
	 *                          month
	 * @param employeeWorkDays  output value number of the work days
	 * @param employee_D_Days   output value number of the "Dovolenka" days
	 * @param employee_NV_Days  output value number of the "Nahradne volno" days
	 * @param employee_PN_Days  output value number of the "Praceneschopnost" days
	 * @param employee_PvP_Days output value number of the "Prekazka v praci" days
	 * @param employee_P_Days   output value number of the "Paragraf" days
	 */
	private void calcStatisticsValues(Long clientId, LinkedList<CMonthReportDailyRecord> groupedRows, CSummaryValues outputSummaryValues) {

		Locale locale = CLocaleUtils.getLocale();
		final String ONE_HALF_HOLIDAY_STRING = this.messages.getMessage("report.month.half_day_holiday", null, locale);

		// --- COMMON --
		// dovolenka
		BigDecimal sumD_days = BigDecimal.ZERO;
		// nahradne volno
		BigDecimal sumNV_days = BigDecimal.ZERO;
		// praceneschopnost
		BigDecimal sumPN_days = BigDecimal.ZERO;
		// prekazky v praci - vsetky
		BigDecimal sumPvP_General_days = BigDecimal.ZERO;
		// prekazky v praci - navsteva lekara
		BigDecimal sumPvP_PhysicianVisit_days = BigDecimal.ZERO;
		// prekazky v praci - 60 percent
		BigDecimal sumPvP_Percent60_days = BigDecimal.ZERO;
		// prekazky v praci - ostatne: vsetky - navsteva lekara a 60 percent
		BigDecimal sumPvP_Other_days = BigDecimal.ZERO;

		// -- MONTH EMPLOYEE REPORT --
		// pocet pracovnych dni
		BigDecimal numberEmployeeWorkDays = BigDecimal.ZERO;
		// suma odpracovanych minut
		BigDecimal sumOfWorkHours = BigDecimal.ZERO;
		// suma pohotovisti v minutach
		BigDecimal sumAlertnessWorkHours = BigDecimal.ZERO;
		// suma zasahov v minutach
		BigDecimal sumInteractiveWorkHours = BigDecimal.ZERO;

		// -- MONTH ACCOUNTATNT REPORT --
		// pocet pracovnych dni
		BigDecimal numberEmployeeWorkDays_WorkDaysOnly = BigDecimal.ZERO;
		// suma odpracovanych minut cez pracovne dni
		BigDecimal sumOfWorkMinutes_WorkDays = BigDecimal.ZERO;
		// pocet odpracovanych dni cez sviatky/volno
		BigDecimal numberEmployeeWorkDays_HolidaysOnly = BigDecimal.ZERO;
		// suma odpracovanych minut cez cez sviatky/volno
		BigDecimal sumOfWorkMinutes_Holidays = BigDecimal.ZERO;
		// sucet minut stravenych na prekazkach v praci - nasteva lekara
		BigDecimal sumOfPvP_PhysicianVisit_Minutes = BigDecimal.ZERO;
		// sucet minut stravenych na prekazkach v praci - ostatne (mimo lekara a
		// 60%)
		BigDecimal sumOfPvP_Other_Minutes = BigDecimal.ZERO;

		Map<Calendar, BigDecimal> dates = new HashMap<>();
		for (CMonthReportDailyRecord groupedRow : groupedRows) {
			//
			Boolean checkedDayIsHoliday = checkedDayIsHoliday(clientId, groupedRow.getDate());

			if (!BigDecimal.ZERO.equals(groupedRow.getDuration()) || !BigDecimal.ZERO.equals(groupedRow.getWorkAlertnessDuration())
					|| !BigDecimal.ZERO.equals(groupedRow.getWorkInteractiveDuration())) {
				// work day
				if (dates.containsKey(groupedRow.getDate()) == false) {
					// new date, first duration
					dates.put(groupedRow.getDate(), groupedRow.getDuration());
				} else {
					// update exist value for the day
					dates.put(groupedRow.getDate(), dates.get(groupedRow.getDate()).add(groupedRow.getDuration()));
				}

				sumOfWorkHours = sumOfWorkHours.add(groupedRow.getDuration());
				if (CDateUtils.isWorkingDay(groupedRow.getDate().getTime()) && !checkedDayIsHoliday) {
					// normalny pracovny den
					sumOfWorkMinutes_WorkDays = sumOfWorkMinutes_WorkDays.add(groupedRow.getDuration());

				} else {
					// volno / sviatok
					sumOfWorkMinutes_Holidays = sumOfWorkMinutes_Holidays.add(groupedRow.getDuration());
				}

				if (groupedRow.getWorkAlertnessDuration() != null) {
					sumAlertnessWorkHours = sumAlertnessWorkHours.add(groupedRow.getWorkAlertnessDuration());
				}
				if (groupedRow.getWorkInteractiveDuration() != null) {
					sumInteractiveWorkHours = sumInteractiveWorkHours.add(groupedRow.getWorkInteractiveDuration());
				}
			}

			// pocitanie: D, NV, PN , PvP a P
			if (CDateUtils.isWorkingDay(groupedRow.getDate().getTime()) && !checkedDayIsHoliday) {
				// ide o pracovny den - musime preverit: D, NV, PN , PvP a P
				if (IActivityConstant.NOT_WORK_HOLIDAY_CODE.equals(groupedRow.getActivityCode())) {
					// cela dovolenka
					sumD_days = sumD_days.add(BigDecimal.ONE);
				} else if (groupedRow.getNote() != null && groupedRow.getNote().indexOf(ONE_HALF_HOLIDAY_STRING) > -1) {
					// pol dna dovolenky
					sumD_days = sumD_days.add(new BigDecimal("0.5"));
					numberEmployeeWorkDays = numberEmployeeWorkDays.add(new BigDecimal("0.5"));
				} else if (IActivityConstant.NOT_WORK_REPLWORK_CODE.equals(groupedRow.getActivityCode())) {
					// nahradne volno
					sumNV_days = sumNV_days.add(BigDecimal.ONE);
				} else if (IActivityConstant.NOT_WORK_SICKNESS_CODE.equals(groupedRow.getActivityCode())) {
					// praceneschopnost
					sumPN_days = sumPN_days.add(BigDecimal.ONE);
				} else if (groupedRow.containsPvPActivities()) {
					// prekazky v praci - vseobecne
					sumPvP_General_days = sumPvP_General_days.add(BigDecimal.ONE);

					// teraz prejdeme jednotlive typy PvP
					List<CPvPData> listPvPData = groupedRow.getPvpData();
					for (CPvPData pvpdata : listPvPData) {
						if (IActivityConstant.NOT_WORK_WORKBREAK_CODE.equalsIgnoreCase(pvpdata.getActivityCode())) {
							// ine pvp
							sumPvP_Other_days = sumPvP_Other_days.add(BigDecimal.ONE);

							sumOfPvP_Other_Minutes = sumOfPvP_Other_Minutes.add(pvpdata.getPvp_Other_MinutesDuration() == null ? BigDecimal.ZERO : pvpdata.getPvp_Other_MinutesDuration());
						} else if (IActivityConstant.NOT_WORK_WORKBREAK_PHYSICIAN_VISIT_CODE.equals(pvpdata.getActivityCode())) {
							// prekazky v praci - navsteva lekara
							sumPvP_PhysicianVisit_days = sumPvP_PhysicianVisit_days.add(BigDecimal.ONE);

							sumOfPvP_PhysicianVisit_Minutes = sumOfPvP_PhysicianVisit_Minutes
									.add(pvpdata.getPvp_PhysicianVisit_MinutesDuration() == null ? BigDecimal.ZERO : pvpdata.getPvp_PhysicianVisit_MinutesDuration());
						} else if (IActivityConstant.NOT_WORK_WORKBREAK_PERCET60_CODE.equals(pvpdata.getActivityCode())) {
							// prekazky v praci - 60%
							sumPvP_Percent60_days = sumPvP_Percent60_days.add(BigDecimal.ONE);
						}
					}

					// este musime skontrolovat, ci bol zamestnanec
					// v praci aspon minutu
					// ak ano treba pripocitat aj jeden pracovny den
					// (info: Maja)
					if (groupedRow.getDuration() != null && groupedRow.getDuration().doubleValue() > 0.0) {
						numberEmployeeWorkDays = numberEmployeeWorkDays.add(BigDecimal.ONE);

					}

				} else {
					if (BigDecimal.ZERO.equals(groupedRow.getDuration()) && (groupedRow.getWorkAlertnessDuration() != null || groupedRow.getWorkInteractiveDuration() != null)) {
						// SED-719 - Nezarátavať sumu pohotovost/zásah do
						// celkového priemeru, za predpokladu, že sa výkaz
						// pohotovosti/zásahu nachádza v pracovný deň ako
						// jediný.
					} else {
						if (!BigDecimal.ZERO.equals(groupedRow.getDuration())) {
							// normalny pracovny den
							numberEmployeeWorkDays = numberEmployeeWorkDays.add(BigDecimal.ONE);
						}
					}
				}
			}

			// check work hours in free days, also
			if (!CDateUtils.isWorkingDay(groupedRow.getDate().getTime()) || checkedDayIsHoliday) {
				Boolean isNotWorkActivity = IActivityConstant.NOT_WORK_HOLIDAY_CODE.equals(groupedRow.getActivityCode());
				isNotWorkActivity = isNotWorkActivity && (groupedRow.getNote() != null && groupedRow.getNote().indexOf(ONE_HALF_HOLIDAY_STRING) > -1);
				isNotWorkActivity = isNotWorkActivity && (IActivityConstant.NOT_WORK_REPLWORK_CODE.equals(groupedRow.getActivityCode()));
				isNotWorkActivity = isNotWorkActivity && (IActivityConstant.NOT_WORK_SICKNESS_CODE.equals(groupedRow.getActivityCode()));
				isNotWorkActivity = isNotWorkActivity && (IActivityConstant.NOT_WORK_WORKBREAK_CODE.equals(groupedRow.getActivityCode()));
				isNotWorkActivity = isNotWorkActivity && (IActivityConstant.NOT_WORK_WORKBREAK_PHYSICIAN_VISIT_CODE.equals(groupedRow.getActivityCode()));
				isNotWorkActivity = isNotWorkActivity && (IActivityConstant.NOT_WORK_WORKBREAK_PERCET60_CODE.equals(groupedRow.getActivityCode()));

				if (!isNotWorkActivity) {
					// nezapocitavame
					if (groupedRow.getWorkAlertnessDuration() != null && groupedRow.getWorkAlertnessDuration().compareTo(BigDecimal.ZERO) > 0) {
						// pohotovost
					} else if (groupedRow.getWorkInteractiveDuration() != null && groupedRow.getWorkInteractiveDuration().compareTo(BigDecimal.ZERO) > 0) {
						// ani zasah
					} else {
						numberEmployeeWorkDays_HolidaysOnly = numberEmployeeWorkDays_HolidaysOnly.add(BigDecimal.ONE);
					}
				}
			}

		}

		numberEmployeeWorkDays_WorkDaysOnly = numberEmployeeWorkDays;

		// calculate month average
		BigDecimal averageDuration;
		if (numberEmployeeWorkDays.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal bgWorkNum = numberEmployeeWorkDays;
			try {
				averageDuration = sumOfWorkHours.divide(bgWorkNum, RoundingMode.HALF_UP);
			} catch (Exception e) {
				logger.info(e);
				averageDuration = BigDecimal.ZERO;
			}

			// -- EMPLOYEE MONTH REPORT
			// pocet odpracovanych dni
			outputSummaryValues.setNumberEmployeeWorkDays(numberEmployeeWorkDays.toPlainString());
			// sucet hodin
			outputSummaryValues.setSumOfWorkHours(CDateUtils.convertToHourMinuteString(sumOfWorkHours));
			// denny priemer
			outputSummaryValues.setAverageHoursForDay(CDateUtils.convertToHourMinuteString(averageDuration));

		}
		// -- COMMON
		outputSummaryValues.setNumberEmployeeDDays(sumD_days.toPlainString());
		outputSummaryValues.setNumberEmployeeNVDays(sumNV_days.toPlainString());
		outputSummaryValues.setNumberEmployeePNDays(sumPN_days.toPlainString());

		// -- EMPLOYEE MONTH REPORT
		outputSummaryValues.setSumOfAlertnessWorkHours(CDateUtils.convertToHourMinuteString(sumAlertnessWorkHours));
		outputSummaryValues.setSumOfInteractiveWorkHours(CDateUtils.convertToHourMinuteString(sumInteractiveWorkHours));
		outputSummaryValues.setNumberEmployeePvPDays(sumPvP_General_days.toPlainString());

		// -- ACCOUNTANT MONTH REPORT
		// - pocet odpracovanych pracovnych dni
		outputSummaryValues.setNumberEmployeeWorkDays_WorkDaysOnly(numberEmployeeWorkDays_WorkDaysOnly.toPlainString());
		// - pocet hodin odpracovanych pocas pracovnych dni
		outputSummaryValues.setSumOfWorkHours_WorkDays(CDateUtils.convertToHourMinuteString(sumOfWorkMinutes_WorkDays));
		// - pocet volnych/sviatocnych dni v praci
		outputSummaryValues.setNumberEmployeeWorkDays_HolidaysOnly(numberEmployeeWorkDays_HolidaysOnly.toPlainString());
		// - pocet odpracovanych hodin cez volne/sviatocne dni
		outputSummaryValues.setSumOfWorkHours_Holidays(CDateUtils.convertToHourMinuteString(sumOfWorkMinutes_Holidays));
		// - pocet dni stravenych na pvp - navsteva lekara
		outputSummaryValues.setNumberEmployeePvP_PhysicianVist_Days(sumPvP_PhysicianVisit_days.toPlainString());
		// - pocet minut stravenych na pvp - navsteva lekara
		outputSummaryValues.setWorkbreakPhysicianVisitDuration(CDateUtils.convertToHourMinuteString(sumOfPvP_PhysicianVisit_Minutes));
		// - pocet dni stravenych na pvp - ine
		outputSummaryValues.setNumberEmployeePvP_Other_Days(sumPvP_Other_days.toPlainString());
		// - pocet minut stravenych na pvp - ine
		outputSummaryValues.setWorkbreakOtherDuration(CDateUtils.convertToHourMinuteString(sumOfPvP_Other_Minutes));
		// - pocet dni stravenych na prekazkach v praci: 60%
		outputSummaryValues.setNumberEmployeePvP_60Percet_Days(sumPvP_Percent60_days.toPlainString());

	}

	private Boolean checkedDayIsHoliday(Long clientId, Calendar inputCheckedDay) {
		Boolean checkedDayIsHoliday = Boolean.FALSE;

		Calendar checkedDay = (Calendar) inputCheckedDay.clone();
		checkedDay.set(Calendar.HOUR_OF_DAY, 0);
		checkedDay.set(Calendar.MINUTE, 0);
		checkedDay.set(Calendar.SECOND, 0);
		checkedDay.set(Calendar.MILLISECOND, 0);

		Integer selectedYear = checkedDay.get(Calendar.YEAR);
		List<CHoliday> holidays = this.holidayDao.findAllValidClientsHolidays(clientId, selectedYear);
		for (CHoliday holiday : holidays) {
			if (holiday.getDay().equals(checkedDay)) {
				checkedDayIsHoliday = Boolean.TRUE;
				break;
			}
		}

		return checkedDayIsHoliday;
	}

	private void markFreeDaysInWork(List<CReportModel> rows, List<CHoliday> clientHolidays) {
		Locale locale = CLocaleUtils.getLocale();
		Map<Calendar, CHoliday> holidayDays = new HashMap<>();
		for (CHoliday item : clientHolidays) {
			holidayDays.put(item.getDay(), item);
		}
		for (CReportModel row : rows) {
			String activityValue = row.getString(3);
			if (activityValue != null && activityValue.indexOf(":") > -1) {
				Calendar date = row.getCalendar(1);
				if (holidayDays.containsKey(date)) {
					// do stĺpca poznámka vloží názov sviatku + text " - práca v
					// deň pracovného voľna"
					row.setValue(2, holidayDays.get(date).getDescription() + " " + this.messages.getMessage("report.notes.work_in_free_day", null, locale));
				}
			}

			// ak je stĺpec Poznámka prázdny && v stĺpci Pohotovosť alebo Zásah
			// je vyplnený čas && deň je Sobota/Nedeľa alebo sviatok
			else if (row.getString(3) == null && (row.getString(5).indexOf(":") > -1 || row.getString(6).indexOf(":") > -1)
					&& (!CDateUtils.isWorkingDay(row.getCalendar(1).getTime()) || holidayDays.containsKey(row.getCalendar(1)))) {
				Calendar date = row.getCalendar(1);
				if (holidayDays.containsKey(date)) {
					row.setValue(3, IActivityConstant.NOT_WORK_FREE_DAY);
				} else {
					String dayName = CDateUtils.getDayName(row.getCalendar(1)).substring(0, 1);
					row.setValue(3, dayName);
				}
			}
		}
	}

	/*
	 * Metóda addMissingMonthDays pridá do reportu dni, ktoré neboli dotiahnuté z
	 * tabuľky (dni v ktorých nie je pracovná aktivita)
	 */
	private void addMissingMonthDays(List<CReportModel> rows, CUser user, int year, int month, List<CHoliday> clientHolidays) {

		Map<Calendar, CHoliday> holidayDays = new HashMap<>();
		for (CHoliday item : clientHolidays) {
			holidayDays.put(item.getDay(), item);
		}

		Calendar firstDayOfTargetMonth = Calendar.getInstance();
		firstDayOfTargetMonth.set(Calendar.MONTH, month);
		firstDayOfTargetMonth.set(Calendar.YEAR, year);
		firstDayOfTargetMonth.set(Calendar.DAY_OF_MONTH, 1);
		firstDayOfTargetMonth.set(Calendar.HOUR_OF_DAY, 0);
		firstDayOfTargetMonth.set(Calendar.MINUTE, 0);
		firstDayOfTargetMonth.set(Calendar.SECOND, 0);
		firstDayOfTargetMonth.set(Calendar.MILLISECOND, 0);

		Calendar lastDayOfTargetMonth = (Calendar) firstDayOfTargetMonth.clone();
		lastDayOfTargetMonth.set(Calendar.DAY_OF_MONTH, lastDayOfTargetMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

		Map<Calendar, Calendar> rowDays = new HashMap<>();
		for (CReportModel row : rows) {
			if (((Long) row.getObject(0)).equals(user.getId())) {
				rowDays.put(row.getCalendar(1), row.getCalendar(1));
			}
		}
		Calendar checkDate = (Calendar) firstDayOfTargetMonth.clone();

		// prechádzam všetky dni v mesiaci
		while (checkDate.compareTo(lastDayOfTargetMonth) < 1) {
			// ak sa deň nenachádza v HashMape, pridám ho
			if (rowDays.containsKey(checkDate) == false) {
				CReportModel tmp = new CReportModel();
				// userId
				tmp.setValue(0, user.getId());
				// day date
				tmp.setValue(1, (Calendar) checkDate.clone());

				if (CDateUtils.isWorkingDay(checkDate.getTime())) {
					// take a holiday into account?
					if (holidayDays.containsKey(checkDate)) {
						// not work day:
						// note
						tmp.setValue(2, holidayDays.get(checkDate).getDescription());
						// duration/activity code
						tmp.setValue(3, IActivityConstant.NOT_WORK_FREE_DAY);
					} else {
						// is work day: - but any work activity!!!
						// note
						tmp.setValue(2, "(?)");
						// duration/activity code
						tmp.setValue(3, "");
					}
				} else {
					// not work day:
					// note
					tmp.setValue(2, "");
					// duration/activity code
					// put one letter code of Saturday, Sunday
					String dayName = CDateUtils.getDayName(checkDate).substring(0, 1);
					tmp.setValue(3, dayName);
				}

				// day short name
				tmp.setValue(4, CDateUtils.getDayName(checkDate));

				// add prepared item
				rows.add(tmp);
			}
			checkDate.add(Calendar.DAY_OF_MONTH, 1);
		}

	}

	private class CReportModelComparator implements Comparator<CReportModel> {

		@Override
		public int compare(CReportModel o1, CReportModel o2) {
			Long user1 = (Long) ((CReportModel) o1).getObject(0);
			Long user2 = (Long) ((CReportModel) o2).getObject(0);

			if (user1.compareTo(user2) == 0) {
				// user1 == user2
				Calendar cal1 = (Calendar) ((CReportModel) o1).getObject(1);
				Calendar cal2 = (Calendar) ((CReportModel) o2).getObject(1);
				return cal1.compareTo(cal2);
			} else {
				return user1.compareTo(user2);
			}
		}
	}

	/**
	 * Returns grouped model for all users
	 * 
	 * @param data all daily user activities records
	 * @return grouped model for all users
	 */
	private Map<Long, LinkedList<CMonthReportDailyRecord>> groupModel(final List<CViewTimeStamp> data, boolean accountantReport) {

		// referenecia na prechdázajúci deň, aby bolo možné po zmene čísla dňa spätne priradiť poznámku do predchádzajúceho dňa
		CMonthReportDailyRecord previousDayRecord = null;

		// zoznam objektov potrebných na spojenie a vytvorenie poznámky pri pohotovosti/zásahu
		List<CAlertInteracExportNote> notesList = new LinkedList<>();

		Long actualDay = null;
		Long previousDay = null;

		final Map<Long, LinkedList<CMonthReportDailyRecord>> groupUserModels = new LinkedHashMap<>();
		for (final CViewTimeStamp record : data) {
			actualDay = record.getDay();

			LinkedList<CMonthReportDailyRecord> userModel = groupUserModels.get(record.getUserId());
			if (null == userModel) {
				// for the selected user no model exists - create one
				userModel = new LinkedList<>();

				final CMonthReportDailyRecord dayRecord = this.getDayModel(record, accountantReport, notesList);
				userModel.add(dayRecord);

				// uložím si referenciu na tento deň
				previousDayRecord = dayRecord;

				groupUserModels.put(record.getUserId(), userModel);

			} else {
				if (isClientWorkActivity(record, accountantReport)) {
					checkNoteList(actualDay, previousDay, previousDayRecord, notesList);
					// process duration
					previousDayRecord = processWorkUserActivity(userModel, record, accountantReport, notesList);
				} else {
					// set non work activity code or notes
					if (isWorkbreak_60Percet(record)) {
						checkNoteList(actualDay, previousDay, previousDayRecord, notesList);
						previousDayRecord = processWorkbreakPercent60Record(userModel, record, accountantReport, notesList);
					} else if (isHoliday(record)) {
						checkNoteList(actualDay, previousDay, previousDayRecord, notesList);
						previousDayRecord = processHolidayUserActivity(userModel, record, accountantReport, notesList);
					} else {
						checkNoteList(actualDay, previousDay, previousDayRecord, notesList);
						previousDayRecord = processOtherNonWorkingUserActivity(userModel, record, accountantReport, notesList);
					}
				}
			}

			previousDay = actualDay;
		}

		// ak mám niečo uložené v zozname poznámok, zapíšem poznámku do záznamu
		// aktuálneho dňa
		if (notesList != null && !notesList.isEmpty()) {
			// ak by malo byť v poli poznámka viac záznamov, uviesť ich všetky
			// oddelené čiarkou
			String note = previousDayRecord.getNote();
			if (note != null && !note.isEmpty()) {
				note += ", ";
			}
			previousDayRecord.setNote(note + createNoteFromList(notesList));

			// vyprázdnim zoznam poznámok
			notesList.clear();
		}

		return groupUserModels;
	}

	/**
	 * metóda vytvorí poznámku k pohotovosti/zásahu zo zoznamu zgrupuje trvanie
	 * rovnakých aktivít na rovnakom projekte
	 * 
	 * @param notesList
	 * @return
	 */
	private String createNoteFromList(List<CAlertInteracExportNote> notesList) {
		String retVal = "";

		HashMap<CAlertInteracNote, Long> map = new HashMap<>();

		for (int i = 0; i < notesList.size(); i++) {
			CAlertInteracExportNote note = notesList.get(i);

			if (map.isEmpty()) {
				map.put(new CAlertInteracNote(note.getActivityName(), note.getProjectName()), note.getDuration());

			} else if (map.containsKey(new CAlertInteracNote(note.getActivityName(), note.getProjectName()))) {

				for (Map.Entry<CAlertInteracNote, Long> entry : map.entrySet()) {
					if ((new CAlertInteracNote(note.getActivityName(), note.getProjectName())).equals(entry.getKey())) {

						Long duration = entry.getValue();
						entry.setValue(duration + note.getDuration());
					}
				}

			} else {
				map.put(new CAlertInteracNote(note.getActivityName(), note.getProjectName()), note.getDuration());
			}
		}

		for (Map.Entry<CAlertInteracNote, Long> entry : map.entrySet()) {

			CAlertInteracNote key = entry.getKey();
			Long value = entry.getValue();

			if (retVal.equals("")) {
				// príklad: Pohotovosť FM.32.12 - Support services
				// (2012/08)(10:01)
				retVal += key.getActivityName() + " " + key.getProjectName() + "(" + CDateUtils.convertToHourMinuteString(new BigDecimal(value)) + ")";

			} else {
				// každú ďalšiu poznámku oddelím čiarkou
				retVal += "," + key.getActivityName() + " " + key.getProjectName() + "(" + CDateUtils.convertToHourMinuteString(new BigDecimal(value)) + ")";
			}
		}

		return retVal;
	}

	/**
	 * Converts business model to target model (map of objects)
	 * 
	 * <li>[0,Long: userId]</li>
	 * 
	 * <li>[1,String:date(DD.MM.YYYY)]</li>
	 * 
	 * <li>[2,String:notes]</li>
	 * 
	 * <li>[3,String:duration: (h:mm) or "request code"]</li>
	 * 
	 * <li>[4,String:day short name]</li>
	 * 
	 * <li>[5,String:alertness duration: (h:mm)]</li>
	 * 
	 * <li>[6,String:interactive work duration: (h:mm)]</li>
	 * 
	 * 
	 * @param groupUserModels map of users models with month records
	 * @return list of report model
	 */
	private List<CReportModel> convertMonthDayModel(final Map<Long, LinkedList<CMonthReportDailyRecord>> groupUserModels) {
		final List<CReportModel> reportRows = new ArrayList<>(groupUserModels.size());

		for (final Long id : groupUserModels.keySet()) {
			for (final CMonthReportDailyRecord groupRecord : groupUserModels.get(id)) {
				// [0,Long: userId]
				// [1,String:date(DD.MM.YYYY)],
				// [2,String:notes],
				// [3,String:duration: (h:mm) or "request code"],
				// [4,String:day short name]
				// [5,String:alertness duration: (h:mm)],
				// [6,String:interactive work duration: (h:mm)],
				final CReportModel rowModel = new CReportModel();
				rowModel.setValue(0, id);
				rowModel.setValue(1, groupRecord.getDate());

				String groupRecordNote = groupRecord.getNote() == null ? "" : groupRecord.getNote();
				List<CPvPData> listPvpData = groupRecord.getPvpData();
				for (CPvPData pvpData : listPvpData) {
					groupRecordNote = groupRecordNote + ("".equals(groupRecordNote) ? "" : ", ") + (pvpData.getNote() == null ? "" : pvpData.getNote());
				}
				rowModel.setValue(2, groupRecordNote);

				String sDuration;
				BigDecimal bgDuration = groupRecord.getDuration();
				String activityCode = groupRecord.getActivityCode();
				if (BigDecimal.ZERO.equals(bgDuration)) {
					sDuration = activityCode;
				} else {
					int hours = (int) bgDuration.longValue() / 3600000;
					int minutes = (int) ((bgDuration.longValue() % 3600000) / 60000);
					sDuration = CDateUtils.getLeadingZero(hours, 1) + ":" + CDateUtils.getLeadingZero(minutes, 2);
				}

				if ("0:00".equals(sDuration) && listPvpData.size() > 0) {
					sDuration = this.messages.getMessage("report.month.workbreak_short_code", null, CLocaleUtils.getLocale());
				}
				rowModel.setValue(3, sDuration);

				rowModel.setValue(4, CDateUtils.getDayName(groupRecord.getDate()));

				String sAlertnessWorkDuration = "";
				BigDecimal bgAlertnessWorkDuration = groupRecord.getWorkAlertnessDuration();
				if (bgAlertnessWorkDuration != null) {
					int hours = (int) bgAlertnessWorkDuration.longValue() / 3600000;
					int minutes = (int) ((bgAlertnessWorkDuration.longValue() % 3600000) / 60000);
					sAlertnessWorkDuration = CDateUtils.getLeadingZero(hours, 1) + ":" + CDateUtils.getLeadingZero(minutes, 2);
				}
				rowModel.setValue(5, sAlertnessWorkDuration);

				String sInteractiveWorkDuration = "";
				BigDecimal bgInteractiveWorkDuration = groupRecord.getWorkInteractiveDuration();
				if (bgInteractiveWorkDuration != null) {
					int hours = (int) bgInteractiveWorkDuration.longValue() / 3600000;
					int minutes = (int) ((bgInteractiveWorkDuration.longValue() % 3600000) / 60000);
					sInteractiveWorkDuration = CDateUtils.getLeadingZero(hours, 1) + ":" + CDateUtils.getLeadingZero(minutes, 2);
				}
				rowModel.setValue(6, sInteractiveWorkDuration);

				reportRows.add(rowModel);
			}
			for (CReportModel reportRow : reportRows) {
				reportRow.getCalendar(1);
			}
		}

		return reportRows;
	}

	/**
	 * Returns <code>true</code> (<code>false</code>) if record activity is(not)
	 * client work activity
	 * 
	 * @param record input record
	 * @return boolean value
	 */
	private boolean isClientWorkActivity(CViewTimeStamp record, boolean accountantReport) {
		// SED-722
		// pre mesacny vykaz sa ma zaratavat aj navsteva lekara, pre spracovanie
		// miezd sa zaratavat nema
		if (accountantReport) {
			return record.getActivityId() != null && record.getActivityId() > 0 && record.getFlagSum() != null && record.getFlagSum();
		} else {
			return record.getFlagSum() != null && record.getFlagSum();
		}
	}

	private boolean isWorkbreak_60Percet(CViewTimeStamp record) {
		if (record.getReasonId() != null) {
			CRequestReason reason = this.requestReasonDao.findById(IRequestReasonConstants.PERCENT_60_ID);
			if (reason != null) {
				if (IActivityConstant.NOT_WORK_WORKBREAK.equals(record.getActivityId()) && record.getReasonId().equals(reason.getId())) {
					return true;
				}
			}

		}

		return false;
	}

	/**
	 * Returns <code>true</code> (<code>false</code>) if record activity is(not)
	 * work activity of holiday type
	 * 
	 * @param record
	 * @return
	 */
	private boolean isHoliday(CViewTimeStamp record) {
		return IActivityConstant.NOT_WORK_HOLIDAY.equals(record.getActivityId());
	}

	/**
	 * Returns one day model of user activity for month report created from first
	 * record that was found for selected day
	 * 
	 * @param record input record
	 * @return day model
	 */
	private CMonthReportDailyRecord getDayModel(final CViewTimeStamp record, boolean accountantReport, List<CAlertInteracExportNote> notesList) {
		Locale locale = CLocaleUtils.getLocale();
		final CMonthReportDailyRecord dayModel = new CMonthReportDailyRecord();
		Calendar clonedFrom = (Calendar) record.getTimeFrom().clone();
		clonedFrom.set(Calendar.HOUR_OF_DAY, 0);
		clonedFrom.set(Calendar.MINUTE, 0);
		clonedFrom.set(Calendar.SECOND, 0);
		clonedFrom.set(Calendar.MILLISECOND, 0);

		// userId
		dayModel.setUserId(record.getUserId());

		// date
		dayModel.setDate(clonedFrom);

		// note
		if (record.getFlagExport() != null && record.getFlagExport()) {
			int hours = (int) record.getDuration().longValue() / 60;
			int minutes = (int) record.getDuration().longValue() % 60;
			String sDuration = CDateUtils.getLeadingZero(hours, 1) + ":" + CDateUtils.getLeadingZero(minutes, 2);

			dayModel.setNote(record.getActivityName() + " (" + sDuration + ")");
		} else {
			dayModel.setNote("");
		}

		// duration
		dayModel.setDuration(BigDecimal.ZERO); // default value
		if (isClientWorkActivity(record, accountantReport)) {
			// for work activity set time interval
			Calendar from = record.getTimeFrom();
			Calendar to = record.getTimeTo();
			if (from != null && to != null) {
				// closed time interval exists
				// pridavane jednu sekundu
				long duration = to.getTime().getTime() + 1l - from.getTime().getTime();
				dayModel.setDuration(new BigDecimal(duration));
			}
		} else {
			if (isWorkbreak_60Percet(record)) {
				CPvPData tmpPvPData = new CPvPData();
				tmpPvPData.setActivityCode(IActivityConstant.NOT_WORK_WORKBREAK_PERCET60_CODE);
				if (record.getReasonId() != null) {
					CRequestReason reason = this.requestReasonDao.findById(record.getReasonId());
					tmpPvPData.setNote(reason.getReasonName());
				}
				dayModel.getPvpData().add(tmpPvPData);
			} else if (isHoliday(record)) {
				// for 1/2 day holiday: add duration as note!
				StringBuilder bHolidayNote = new StringBuilder("");
				Calendar from = record.getTimeFrom();
				Calendar to = record.getTimeTo();
				if (from != null && to != null) {
					long diff = to.getTimeInMillis() - from.getTimeInMillis();
					// ak je to pol dna dovolenky - nastav poznamku:
					// "pol dna dovolenky"
					if (diff < 14400000) // 4 hours
					{
						bHolidayNote.append(this.messages.getMessage("report.month.half_day_holiday", null, locale));
					} else {
						// whole day - holiday
						dayModel.setActivityCode(IActivityConstant.NOT_WORK_HOLIDAY_CODE);
						bHolidayNote.append(this.messages.getMessage("report.month.full_day_holiday", null, locale));
					}
					// no comment if not closed time step interval
				}

				if (bHolidayNote.toString() != null) {
					dayModel.setNote(bHolidayNote.toString());
				}

			} else {
				// for other non working activities: set activity code
				switch (record.getActivityId().intValue()) {
				case -12: // Prenatálna lekárska starostlivosť
				case -11: // Návšteva lekára alebo Sprevádzanie rodinného príslušníka
				case -10:
					// closed time interval exists
					// pridavane jednu sekundu
					Calendar fromPhysicianVisit = record.getTimeFrom();
					Calendar toPhysicianVisit = record.getTimeTo();
					long durationPhysicianVisit = 0;

					if (fromPhysicianVisit != null && toPhysicianVisit != null) {
						durationPhysicianVisit = toPhysicianVisit.getTime().getTime() + 1l - fromPhysicianVisit.getTime().getTime();
					}
					dayModel.setActivityCode(IActivityConstant.NOT_WORK_WORKBREAK_PHYSICIAN_VISIT_CODE);

					if (accountantReport) {
						CPvPData tmpPvPData = new CPvPData();
						tmpPvPData.setActivityCode(IActivityConstant.NOT_WORK_WORKBREAK_PHYSICIAN_VISIT_CODE);
						tmpPvPData.setPvp_PhysicianVisit_MinutesDuration(new BigDecimal(durationPhysicianVisit));
						dayModel.getPvpData().add(tmpPvPData);
					} else {
						dayModel.setDuration(new BigDecimal(durationPhysicianVisit));
					}
					break;
				case -9: // zasah
					// for interactive work activity set time interval
					Calendar from1 = record.getTimeFrom();
					Calendar to1 = record.getTimeTo();
					if (from1 != null && to1 != null) {
						// closed time interval exists
						// pridavane jednu sekundu
						long duration = to1.getTime().getTime() + 1l - from1.getTime().getTime();
						dayModel.setWorkInteractiveDuration(new BigDecimal(duration));

						// ak je v daný deň záznam v poli pohotovosť alebo
						// zásah,
						// uviesť v poli poznámka medzerou oddelené hodnoty
						// t_ct_activity.c_name t_ct_project.c_name a trvanie
						// daného záznamu s pohotovosťou/zásahom.

						CAlertInteracExportNote alertInteracExportNote = new CAlertInteracExportNote();
						alertInteracExportNote.setActivityId(record.getActivityId());
						alertInteracExportNote.setProjectId(record.getProjectId());
						alertInteracExportNote.setActivityName(record.getActivityName());
						alertInteracExportNote.setProjectName(record.getProjectName());
						alertInteracExportNote.setDuration(duration);

						notesList.add(alertInteracExportNote);
					}
					break;
				case -8: // pohotovost
					// for alertness work activity set time interval
					Calendar from2 = record.getTimeFrom();
					Calendar to2 = record.getTimeTo();
					if (from2 != null && to2 != null) {
						// closed time interval exists
						// pridavane jednu sekundu
						long duration = to2.getTime().getTime() + 1l - from2.getTime().getTime();
						dayModel.setWorkAlertnessDuration(new BigDecimal(duration));

						// ak je v daný deň záznam v poli pohotovosť alebo
						// zásah,
						// uviesť v poli poznámka medzerou oddelené hodnoty
						// t_ct_activity.c_name t_ct_project.c_name a trvanie
						// daného záznamu s pohotovosťou/zásahom.

						CAlertInteracExportNote alertInteracExportNote = new CAlertInteracExportNote();
						alertInteracExportNote.setActivityId(record.getActivityId());
						alertInteracExportNote.setProjectId(record.getProjectId());
						alertInteracExportNote.setActivityName(record.getActivityName());
						alertInteracExportNote.setProjectName(record.getProjectName());
						alertInteracExportNote.setDuration(duration);

						notesList.add(alertInteracExportNote);
					}
					break;
				case -6:
					CPvPData tmpPvPData = new CPvPData();
					tmpPvPData.setActivityCode(IActivityConstant.NOT_WORK_WORKBREAK_CODE);
					if (record.getReasonId() != null) {
						CRequestReason reason = this.requestReasonDao.findById(record.getReasonId());
						tmpPvPData.setNote(reason.getReasonName());
					}
					Calendar from3 = record.getTimeFrom();
					Calendar to3 = record.getTimeTo();
					if (from3 != null && to3 != null) {
						long workbreakOtherDuration = to3.getTime().getTime() + 1l - from3.getTime().getTime();
						tmpPvPData.setPvp_Other_MinutesDuration(new BigDecimal(workbreakOtherDuration));
					}
					dayModel.getPvpData().add(tmpPvPData);
					dayModel.setActivityCode(IActivityConstant.NOT_WORK_WORKBREAK_CODE);
					break;
				case -5:
					dayModel.setActivityCode(IActivityConstant.NOT_WORK_SICKNESS_CODE);
					if (record.getReasonId() != null) {
						CRequestReason reason = this.requestReasonDao.findById(record.getReasonId());
						dayModel.setNote(reason.getReasonName());
					}
					break;
				case -4:
					dayModel.setActivityCode(IActivityConstant.NOT_WORK_REPLWORK_CODE);
					CRequestType requestType = requestTypeDao.findRecordByActivity(record.getActivityId());
					dayModel.setNote(requestType.getDescription());
					break;
				}
			}
		}

		return dayModel;
	}

	/**
	 * Updates user daily record - increases user activity duration for this record
	 * by input record activity time interval
	 * 
	 * @param groupUserModels input group of users models
	 * @param userModel       input userModel - already exists!
	 * @param record          input timestamp record
	 */
	private CMonthReportDailyRecord processWorkUserActivity(LinkedList<CMonthReportDailyRecord> userModel, CViewTimeStamp record, boolean accountantReport, List<CAlertInteracExportNote> notesList) {
		CMonthReportDailyRecord retVal = null;

		final CMonthReportDailyRecord temp = this.getDayModel(record, accountantReport, notesList);
		boolean found = false;
		for (final CMonthReportDailyRecord dayRecord : userModel) {
			if (temp.equals(dayRecord)) {
				// a user record for the selected day exists - add duration
				dayRecord.setDuration(dayRecord.getDuration().add(temp.getDuration()));

				// ak by malo byť v poli poznámka viac záznamov, uviesť ich
				// všetky oddelené čiarkou
				String note = dayRecord.getNote();
				if (note != null && !note.isEmpty() && temp.getNote() != null && !temp.getNote().isEmpty()) {
					note += ", ";
				}
				dayRecord.setNote(note + temp.getNote());

				found = true;
				retVal = dayRecord;
				break;
			}
		}
		if (!found) {
			// user daily record not exists, add temporary created
			userModel.add(temp);
			retVal = temp;
		}

		return retVal;
	}

	private CMonthReportDailyRecord processWorkbreakPercent60Record(LinkedList<CMonthReportDailyRecord> userModel, CViewTimeStamp record, boolean accountantReport,
			List<CAlertInteracExportNote> notesList) {
		CMonthReportDailyRecord retVal = null;

		final CMonthReportDailyRecord temp = this.getDayModel(record, accountantReport, notesList);
		boolean found = false;
		for (final CMonthReportDailyRecord dayRecord : userModel) {
			if (temp.equals(dayRecord)) {
				// TO DO: processPercent60UserActivity - zatial nic - lebo na
				// jeden den moze byt len jedna takato znacka!
				found = true;
				retVal = dayRecord;
				break;
			}
		}
		if (!found) {
			// user daily record not exists, add temporary created
			userModel.add(temp);
			retVal = temp;
		}

		return retVal;
	}

	/**
	 * Updates user daily record - updates notes by adding "Holiday: (h:mm - h:mm)"
	 * in
	 * 
	 * @param groupUserModels input group of users models
	 * @param userModel       input userModel - already exists!
	 * @param record          input timestamp record
	 */
	private CMonthReportDailyRecord processHolidayUserActivity(LinkedList<CMonthReportDailyRecord> userModel, CViewTimeStamp record, boolean accountantReport,
			List<CAlertInteracExportNote> notesList) {
		CMonthReportDailyRecord retVal = null;

		Locale locale = CLocaleUtils.getLocale();
		final CMonthReportDailyRecord temp = this.getDayModel(record, accountantReport, notesList);
		boolean found = false;
		for (final CMonthReportDailyRecord dayRecord : userModel) {
			if (temp.equals(dayRecord)) {
				String dayNote = temp.getNote();
				if (dayNote == null) {
					dayNote = "";
				}
				String recordHolidayNote = "";

				// for 1/2 day holiday: add duration as note!
				Calendar from = record.getTimeFrom();
				Calendar to = record.getTimeTo();
				if (from != null && to != null) {
					long diff = to.getTimeInMillis() - from.getTimeInMillis();
					// ak je to pol dna dovolenky - nastav poznamku:
					// "pol dna dovolenky"
					if (diff < 14400000) // 4 hours
					{
						if (dayNote.indexOf(this.messages.getMessage("report.month.half_day_holiday", null, locale)) == -1) {
							recordHolidayNote = this.messages.getMessage("report.month.half_day_holiday", null, locale) + record.getNote() == null ? "" : record.getNote();
						}
					} else {
						// whole day - holiday
						temp.setActivityCode(IActivityConstant.NOT_WORK_HOLIDAY_CODE);
						dayRecord.setActivityCode(IActivityConstant.NOT_WORK_HOLIDAY_CODE);
						if (dayNote.indexOf(this.messages.getMessage("report.month.full_day_holiday", null, locale)) == -1) {
							recordHolidayNote = this.messages.getMessage("report.month.full_day_holiday", null, locale);
						}
					}
					// no comment if not closed time step interval

					// a user record for the selected day exists - add duration
					if (dayRecord.getDuration() != null) {
						if (temp.getDuration() != null) {
							dayRecord.setDuration(dayRecord.getDuration().add(temp.getDuration()));
						}
						// leave deyRecord.duration untouched
					} else {
						if (temp.getDuration() != null) {
							dayRecord.setDuration(temp.getDuration());
						}
						// leave deyRecord.duration untouched
					}
				}

				dayRecord.setNote(dayNote + recordHolidayNote);

				found = true;
				retVal = dayRecord;
				break;
			}
		}
		if (!found) {
			// user daily record not exists, add temporary created
			userModel.add(temp);
			retVal = temp;
		}

		return retVal;
	}

	/**
	 * Updates user daily record - sets duration value as activity code.</br>
	 * 
	 * <li>D - for holiday</li>
	 * 
	 * <li>PvP - for 60% work mode</li>
	 * 
	 * <li>NV - for holiday as replace work</li>
	 * 
	 * <li>PN - for sickness</li>
	 * 
	 * @param groupUserModels input group of users models
	 * @param userModel       input userModel - already exists!
	 * @param record          input timestamp record
	 */
	private CMonthReportDailyRecord processOtherNonWorkingUserActivity(LinkedList<CMonthReportDailyRecord> userModel, CViewTimeStamp record, boolean accountantReport,
			List<CAlertInteracExportNote> notesList) {
		CMonthReportDailyRecord retVal = null;

		final CMonthReportDailyRecord temp = this.getDayModel(record, accountantReport, notesList);

		boolean found = false;
		for (final CMonthReportDailyRecord dayRecord : userModel) {
			if (temp.equals(dayRecord)) {

				// add code activity to position of the duration value
				// for other non working activities: set activity code,
				switch (record.getActivityId().intValue()) {
				case -12: // Prenatálna lekárska starostlivosť
				case -11: // Návšteva lekára alebo Sprevádzanie rodinného príslušníka
				case -10:
					Calendar fromPhysicianVisit = record.getTimeFrom();
					Calendar toPhysicianVisit = record.getTimeTo();
					long durationPhysicianVisit = 0;

					if (fromPhysicianVisit != null && toPhysicianVisit != null) {
						durationPhysicianVisit = toPhysicianVisit.getTime().getTime() + 1l - fromPhysicianVisit.getTime().getTime();
					}
					dayRecord.setActivityCode(IActivityConstant.NOT_WORK_WORKBREAK_PHYSICIAN_VISIT_CODE);

					if (accountantReport) {
						CPvPData tmpPvPData = new CPvPData();
						tmpPvPData.setActivityCode(IActivityConstant.NOT_WORK_WORKBREAK_PHYSICIAN_VISIT_CODE);
						tmpPvPData.setPvp_PhysicianVisit_MinutesDuration(new BigDecimal(durationPhysicianVisit));
						dayRecord.getPvpData().add(tmpPvPData);
					} else {
						dayRecord.setDuration(new BigDecimal(durationPhysicianVisit));
					}
					break;
				case -9:
				case -8: // zasah ale aj pohotovost
					if (dayRecord.getWorkInteractiveDuration() != null) {
						if (temp.getWorkInteractiveDuration() != null) {
							dayRecord.setWorkInteractiveDuration(dayRecord.getWorkInteractiveDuration().add(temp.getWorkInteractiveDuration()));
						}
					} else {
						if (temp.getWorkInteractiveDuration() != null) {
							dayRecord.setWorkInteractiveDuration(temp.getWorkInteractiveDuration());
						}
					}
					if (dayRecord.getWorkAlertnessDuration() != null) {
						if (temp.getWorkAlertnessDuration() != null) {
							dayRecord.setWorkAlertnessDuration(dayRecord.getWorkAlertnessDuration().add(temp.getWorkAlertnessDuration()));
						}
					} else {
						if (temp.getWorkAlertnessDuration() != null) {
							dayRecord.setWorkAlertnessDuration(temp.getWorkAlertnessDuration());
						}
					}
					break;
				case -6:
					temp.setActivityCode(IActivityConstant.NOT_WORK_WORKBREAK_CODE);
					break;
				case -5:
					temp.setActivityCode(IActivityConstant.NOT_WORK_SICKNESS_CODE);
					break;
				case -4:
					temp.setActivityCode(IActivityConstant.NOT_WORK_REPLWORK_CODE);
					break;
				}

				// ak by malo byť v poli poznámka viac záznamov, uviesť ich
				// všetky oddelené čiarkou
				String note = dayRecord.getNote();
				if (note != null && !note.isEmpty() && temp.getNote() != null && !temp.getNote().isEmpty()) {
					note += ", ";
				}
				dayRecord.setNote(note + temp.getNote());

				found = true;
				retVal = dayRecord;
				break;
			}
		}
		if (!found) {
			// user daily record not exists, add temporary created
			userModel.add(temp);

			retVal = temp;
		}

		return retVal;
	}

	@Transactional(readOnly = true)
	@Override
	public CComplexInputReportModel getModel(Long clientId, Boolean onlyValid) throws CBusinessException {
		CComplexInputReportModel complexModel = new CComplexInputReportModel();

		List<CReportModel> reportRows = this.viewUserDao.findUsersRecordsForEmployeesReport(clientId, onlyValid);

		complexModel.setReportRows(reportRows);

		return complexModel;
	}

	public void checkNoteList(Long actualDay, Long previousDay, CMonthReportDailyRecord previousDayRecord, List<CAlertInteracExportNote> notesList) {
		if (previousDay != null && !actualDay.equals(previousDay)) {
			// ak by malo byť v poli poznámka viac záznamov, uviesť ich všetky
			// oddelené čiarkou
			String note = previousDayRecord.getNote();
			if (note != null && !note.isEmpty()) {
				note += ", ";
			}
			previousDayRecord.setNote(note + createNoteFromList(notesList));

			// vyprázdnim zoznam poznámok
			notesList.clear();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public CComplexInputReportModel getWorkplaceModel(Long clientId, Boolean onlyValid) throws CBusinessException {
		CComplexInputReportModel complexModel = new CComplexInputReportModel();

		List<CReportModel> reportRows = this.userDao.findUsersRecordsForWorkplaceReport(clientId, onlyValid);

		complexModel.setReportRows(reportRows);

		return complexModel;
	}
}
