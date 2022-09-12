package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CMyTimesheetGenerateBrwFilterCriteria;
import sk.qbsw.sed.client.service.brw.IBrwMyTimeStampGenerateService;
import sk.qbsw.sed.client.service.brw.IBrwTimeStampService;
import sk.qbsw.sed.client.service.business.ITmpTimesheetService;
import sk.qbsw.sed.common.utils.CDateRange;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.ITmpTimesheetRecordDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CJiraException;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.jira.CJiraAuthenticationConfigurator;
import sk.qbsw.sed.server.jira.CJiraUtils;
import sk.qbsw.sed.server.jira.Worklog;
import sk.qbsw.sed.server.jira.WorklogResult;
import sk.qbsw.sed.server.model.domain.CTmpTimeSheetRecord;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.util.CTimesheetUtils;

/**
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.4.1
 */
@Service(value = "brwMyTimeStampGenerateService")
public class CBrwMyTimeStampGenerateServiceImpl implements IBrwMyTimeStampGenerateService {

	@Autowired
	private ITmpTimesheetRecordDao tmpTimeStampDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private ITmpTimesheetService tmpTimesheetService;

	@Autowired
	private CJiraAuthenticationConfigurator jiraAuthenticationConfigurator;

	@Autowired
	private IBrwTimeStampService brwTimeStampService;

	@Transactional
	public List<CTmpTimeSheet> fetch(int startRow, int count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBusinessException, CJiraException {
		final List<CTmpTimeSheet> retVal = new ArrayList<>();
		CUser loggedUser = this.userDao.findById(this.getUserId());
		CMyTimesheetGenerateBrwFilterCriteria crit = (CMyTimesheetGenerateBrwFilterCriteria) criteria;

		final List<CTmpTimeSheetRecord> tmpTimeStamps = this.tmpTimeStampDao.findAll(loggedUser, crit, startRow, count, sortProperty, sortAsc);
		for (final CTmpTimeSheetRecord ts : tmpTimeStamps) {
			retVal.add(ts.convert());
		}

		if (crit.getGenerateFromJira()) {
			CDateRange dateRange = new CDateRange();
			Calendar dateFrom = Calendar.getInstance();
			dateFrom.setTime(crit.getDateFrom());
			dateRange.setDateFrom(dateFrom);
			Calendar dateTo = Calendar.getInstance();
			dateTo.setTime(crit.getDateTo());
			dateRange.setDateTo(dateTo);

			String[][] issues = CTimesheetUtils.getJiraIssues(dateRange, jiraAuthenticationConfigurator, userDao);

			if (issues != null) {

				Long summaryDurationInMinutes = brwTimeStampService.getWorkTimeInInterval(loggedUser.getId(), crit.getDateFrom(), crit.getDateTo()) / (1000 * 60);

				for (int i = 0; i < issues.length; i++) {

					String json = CJiraUtils.makeJiraWorklogRequest(loggedUser.getJiraAccessToken(), jiraAuthenticationConfigurator, issues[i][0]);
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
					WorklogResult worklogResult = gson.fromJson(json, WorklogResult.class);

					for (Worklog worklog : worklogResult.getWorklogs()) {

						// zaujímajú ma iba worklogy prihláseného používateľa v
						// zadanom časovom rozmedzí
						if (worklog.getAuthor().getName().equals(loggedUser.getLoginLong()) && worklog.getStarted().after(crit.getDateFrom()) && worklog.getStarted().before(crit.getDateTo())) {

							CTmpTimeSheet tmpTimeSheet = new CTmpTimeSheet();

							if (worklog.getTimeSpentSeconds() != null) {
								tmpTimeSheet.setDurationInMinutes(worklog.getTimeSpentSeconds() / 60);// nacitam
								// cas z worklogu
								tmpTimeSheet.setDurationInPercent(CDateUtils.getMinutesAsPercent(tmpTimeSheet.getDurationInMinutes(), summaryDurationInMinutes)); // cas
								// z worklogu preklopim na %
							} else {
								tmpTimeSheet.setDurationInMinutes(Long.valueOf(0));
								tmpTimeSheet.setDurationInPercent(Long.valueOf(0));
							}

							if (crit.isJiraKeyToPhase()) {
								tmpTimeSheet.setNote(issues[i][1]);

								// ak by bol JIRA kľúč dlhší ako 30 znakov,
								// tak ho skrátim na 30
								if (issues[i][0].length() > 29) {
									tmpTimeSheet.setPhase(issues[i][0].substring(0, 29));
								} else {
									tmpTimeSheet.setPhase(issues[i][0]);
								}
							} else {
								tmpTimeSheet.setNote(issues[i][0] + " - " + issues[i][1]);
							}

							tmpTimeSheet.setDateFrom(crit.getDateFrom());
							tmpTimeSheet.setDateTo(crit.getDateTo());
							tmpTimeSheet.setSummaryDurationInMinutes(summaryDurationInMinutes);

							if (crit.getDefaultActivity() != null) {
								tmpTimeSheet.setActivity(crit.getDefaultActivity());
							}
							tmpTimeSheet.setOutside(crit.getDefaultOutside());
							tmpTimeSheet.setHomeOffice(crit.getDefaultHomeOffice());

							retVal.add(tmpTimeSheet);

						}
					}
				}
			}
		}

		return retVal;
	}

	@Transactional(rollbackForClassName = "CBusinessException")
	public CTmpTimeSheet add(CTmpTimeSheet record) throws CBusinessException {
		final CLockRecord newRecord = this.tmpTimesheetService.add(record);
		final CTmpTimeSheetRecord timeStamp = this.tmpTimeStampDao.findById(newRecord.getId());
		return timeStamp.convert();
	}

	@Transactional
	public CTmpTimeSheet update(CTmpTimeSheet record) throws CBusinessException {
		final CLockRecord newRecord = this.tmpTimesheetService.update(record);
		final CTmpTimeSheetRecord timeStamp = this.tmpTimeStampDao.findById(newRecord.getId());
		return timeStamp.convert();
	}

	@Transactional
	public void delete(CTmpTimeSheet record) throws CBusinessException {

		this.tmpTimesheetService.deleteById(record.getId());

	}

	@Transactional
	public void deleteAllForUser() throws CBusinessException {

		this.tmpTimesheetService.deleteAll();

	}

	/**
	 * Gets id of logged user
	 * 
	 * @param crit
	 * @return
	 * @throws CSecurityException
	 */
	private Long getUserId() throws CSecurityException {
		Long userId = null;
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		userId = loggedUser.getUserId();
		return userId;
	}
}
