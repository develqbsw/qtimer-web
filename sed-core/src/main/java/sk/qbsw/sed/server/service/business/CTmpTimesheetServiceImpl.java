package sk.qbsw.sed.server.service.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTmpTimeStampAddRecord;
import sk.qbsw.sed.client.model.timestamp.IMyTimesheetGenerateBrwFilterCriteria;
import sk.qbsw.sed.client.service.business.ITmpTimesheetService;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.dao.IProjectDao;
import sk.qbsw.sed.server.dao.ITmpTimesheetRecordDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.jira.CJiraAuthenticationConfigurator;
import sk.qbsw.sed.server.model.domain.CTmpTimeSheetRecord;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.CTimeUtils;
import sk.qbsw.sed.server.util.CTimesheetUtils;

/**
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.4.1
 */
@Service(value = "tmpTimestampService")
public class CTmpTimesheetServiceImpl implements ITmpTimesheetService {

	@Autowired
	private IUserDao userDao;

	/**
	 * timesheet dao
	 */
	@Autowired
	private ITmpTimesheetRecordDao tmpTimesheetDao;

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IActivityDao activityDao;

	@Autowired
	private CJiraAuthenticationConfigurator jiraAuthenticationConfigurator;

	@Override
	@Transactional(readOnly = true)
	public CTmpTimeStampAddRecord getNewEmptyRecord(Long userId, IFilterCriteria filter) throws CBusinessException {
		IMyTimesheetGenerateBrwFilterCriteria crit = (IMyTimesheetGenerateBrwFilterCriteria) filter;

		CTmpTimeStampAddRecord newRecord = new CTmpTimeStampAddRecord();
		newRecord.setDateFrom(crit.getDateFrom());
		newRecord.setDateTo(crit.getDateTo());
		return newRecord;
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public CLockRecord add(final CTmpTimeSheet tableRecord) throws CBusinessException {
		return addNoTransaction(tableRecord);
	}

	@Override
	public CLockRecord addNoTransaction(final CTmpTimeSheet tableRecord) throws CBusinessException {
		CTmpTimeSheetRecord add = convert(tableRecord);

		add.setId(null);

		// logic
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		add.setOwnerId(changedBy.getId());
		add.setClientId(changedBy.getClient().getId());
		add.setCreatedById(changedBy.getId());
		add.setChangedById(changedBy.getId());
		add.setChangeTime(Calendar.getInstance());

		add.setDateGenerateAction(null);

		CTimesheetUtils.setIssueSummaryFromJira(add, jiraAuthenticationConfigurator, userDao);

		// ak je cas=0 minutes tak ho napln z JIRA
		Long minutes = add.getDurationInMinutes();
		Long jiraSeconds = add.getJiraTimeSpentSeconds();
		if (minutes.equals(new Long(0l))) { // ak je cas 0

			if (jiraSeconds != null && !jiraSeconds.equals(new Long(0l))) { // a cas z jiry je != 0
				add.setDurationInMinutes(jiraSeconds / 60);// nacitam cas z JIRA
				add.setDurationInPercent(CDateUtils.getMinutesAsPercent(add.getDurationInMinutes(), add.getSummaryDurationInMinutes())); // cas z JIRA preklopim na %
			} else {
				throw new CBusinessException("timestampGenerate.error.durationIsNull");

			}
		}

		this.tmpTimesheetDao.saveOrUpdate(add);

		// prepares return value
		final CLockRecord retVal = new CLockRecord();
		retVal.setId(add.getId());
		retVal.setLastChangeDate(add.getChangeTime().getTime());

		return retVal;
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public CLockRecord update(final CTmpTimeSheet tableRecord) throws CBusinessException {
		return updateNoTransaction(tableRecord);
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void updateDurationCorrected(final CTmpTimeSheet tableRecord) throws CBusinessException {

		final CTmpTimeSheetRecord update = convert(tableRecord);

		this.tmpTimesheetDao.saveOrUpdate(update);
	}

	@Override
	public CLockRecord updateNoTransaction(final CTmpTimeSheet tableRecord) throws CBusinessException {
		final CTmpTimeSheetRecord update = convert(tableRecord);

		// logic
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		update.setChangedById(changedBy.getId());
		update.setChangeTime(Calendar.getInstance());

		update.setDateGenerateAction(null);
		CTimesheetUtils.setIssueSummaryFromJira(update, jiraAuthenticationConfigurator, userDao);

		this.tmpTimesheetDao.saveOrUpdate(update);

		// prepares return value
		final CLockRecord retVal = new CLockRecord();
		retVal.setId(update.getId());
		retVal.setLastChangeDate(update.getChangeTime().getTime());

		return retVal;
	}

	private CTmpTimeSheetRecord convert(final CTmpTimeSheet tmpTimeSheet) {
		CTmpTimeSheetRecord record;

		Long recordId = tmpTimeSheet.getId();

		if (null != recordId) {
			CTmpTimeSheetRecord tmpRecord = this.tmpTimesheetDao.findById(recordId);
			record = tmpRecord;
		} else {
			record = new CTmpTimeSheetRecord();
			record.setId(recordId);
		}

		record.setProject(projectDao.findById(tmpTimeSheet.getProjectId()));
		record.setActivity(activityDao.findById(tmpTimeSheet.getActivityId()));
		record.setNote(tmpTimeSheet.getNote());
		record.setPhase(tmpTimeSheet.getPhase());

		Calendar cdateFrom = Calendar.getInstance();
		cdateFrom.setTime(tmpTimeSheet.getDateFrom());
		CTimeUtils.convertToStartDate(cdateFrom);
		record.setDateFrom(cdateFrom);

		Calendar cdateTo = Calendar.getInstance();
		cdateTo.setTime(tmpTimeSheet.getDateTo());
		CTimeUtils.convertToEndDate(cdateTo);
		record.setDateTo(cdateTo);

		record.setOutside(tmpTimeSheet.getOutside());
		record.setHomeOffice(tmpTimeSheet.getHomeOffice());
		record.setValid(Boolean.TRUE); // default values
		record.setGenerated(Boolean.FALSE); // default values

		record.setDurationInMinutes(tmpTimeSheet.getDurationInMinutes());
		record.setDurationInPercent(tmpTimeSheet.getDurationInPercent());
		record.setSummaryDurationInMinutes(tmpTimeSheet.getSummaryDurationInMinutes());

		return record;
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void deleteAll() throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		this.tmpTimesheetDao.deletePreparedDistributionsByUser(loggedUser.getUserId());
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void deleteById(final Long recordId) throws CBusinessException {
		this.tmpTimesheetDao.deleteById(recordId);
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void createCopyById(final Long recordId) throws CBusinessException {
		CTmpTimeSheetRecord record = this.tmpTimesheetDao.findById(recordId);

		CTmpTimeSheetRecord copyOfRecord = createCopyOfRecord(record);
		this.tmpTimesheetDao.saveOrUpdate(copyOfRecord);
	}

	@Override
	@Transactional(readOnly = true, rollbackForClassName = "CBusinessException")
	public List<CCodeListRecord> findRealizedGenerationProcesses(Long userId) {
		List<CTmpTimeSheetRecord> list = this.tmpTimesheetDao.findRealizedGenerationProcesses(userId, 10);
		return convertToCodeList(list);
	}

	private CCodeListRecord convertToCodeList(final CTmpTimeSheetRecord item) {
		final CCodeListRecord newRec = new CCodeListRecord();
		newRec.setId(item.getId());
		newRec.setName(CDateUtils.convertToDateString(item.getDateGenerateAction()) + "   (" + CDateUtils.convertToDateString(item.getDateFrom()) + "-"
				+ CDateUtils.convertToDateString(item.getDateTo()) + ")");
		newRec.setDescription(CDateUtils.convertToDateString(item.getDateGenerateAction()) + "   (" + CDateUtils.convertToDateString(item.getDateFrom()) + "-"
				+ CDateUtils.convertToDateString(item.getDateTo()) + ")");

		return newRec;
	}

	private List<CCodeListRecord> convertToCodeList(final List<CTmpTimeSheetRecord> input) {
		final List<CCodeListRecord> retVal = new ArrayList<>();
		for (final CTmpTimeSheetRecord item : input) {
			final CCodeListRecord newRec = this.convertToCodeList(item);
			retVal.add(newRec);
		}
		return retVal;
	}

	private CTmpTimeSheetRecord createCopyOfRecord(CTmpTimeSheetRecord input) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		CTmpTimeSheetRecord output = new CTmpTimeSheetRecord();

		output.setOwnerId(new Long(input.getOwnerId()));
		output.setClientId(new Long(input.getClientId()));
		output.setCreatedById(new Long(loggedUser.getUserId()));
		output.setChangedById(new Long(loggedUser.getUserId()));
		output.setChangeTime(Calendar.getInstance());

		if (input.getProject() != null) {
			output.setProject(input.getProject());
		}
		if (input.getActivity() != null) {
			output.setActivity(input.getActivity());
		}
		if (input.getPhase() != null) {
			output.setPhase(new String(input.getPhase()));
		}
		if (input.getNote() != null) {
			output.setNote(new String(input.getNote()));
		}

		output.setOutside(new Boolean(input.getOutside()));
		output.setHomeOffice(new Boolean(input.getHomeOffice()));
		output.setValid(new Boolean(input.getValid()));
		output.setGenerated(new Boolean(input.getGenerated()));

		output.setDateFrom((Calendar) input.getDateFrom().clone());
		output.setDateTo((Calendar) input.getDateTo().clone());
		if (input.getDateGenerateAction() != null) {
			output.setDateGenerateAction((Calendar) input.getDateGenerateAction().clone());
		}
		if (input.getDurationInMinutes() != null) {
			output.setDurationInMinutes(new Long(input.getDurationInMinutes()));
		}
		if (input.getSummaryDurationInMinutes() != null) {
			output.setSummaryDurationInMinutes(new Long(input.getSummaryDurationInMinutes()));
		}

		return output;
	}
}
