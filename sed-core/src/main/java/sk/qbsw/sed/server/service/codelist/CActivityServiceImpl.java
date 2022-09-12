package sk.qbsw.sed.server.service.codelist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IListBoxValueTypes;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.codelist.IActivityService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.dao.IActivityInitialDao;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.codelist.CActivityInitial;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.IServerClientLocalizationKeys;

/**
 * Service for management of Activity code list
 * 
 * @author Dalibor Rak
 * @since 0.1
 * @version 0.1
 * 
 */
@Service(value = "activityService")
public class CActivityServiceImpl implements IActivityService {
	private static final String INDENT = "";

	@Autowired
	private IActivityDao activityDao;

	@Autowired
	private IActivityInitialDao activityInitialDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IClientDao clientDao;

	/**
	 * @see IActivityService#add(CActivityRecord)
	 */
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	@Override
	public Long add(final CActivityRecord record) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		// check
		this.checkActivityExistenceByNameAdd(changedBy, record.getName());

		// SAVE GAME
		final CActivity toChange = new CActivity();

		// sets changed by
		toChange.setChangedBy(changedBy);
		toChange.setChangeTime(Calendar.getInstance());

		// sets parameters
		toChange.setName(record.getName());
		toChange.setNote(record.getNote());
		toChange.setOrder(record.getOrder());
		toChange.setValid(record.getActive());
		toChange.setWorking(Boolean.TRUE); // vzdy bude pracovna
		toChange.setClient(changedBy.getClient());
		toChange.setChangeable(Boolean.TRUE); // vzdy sa bude dat zmenit

		if (record.getTimeMin() != null) {
			Calendar timeMin = Calendar.getInstance();
			timeMin.setTime(record.getTimeMin());
			toChange.setTimeMin(timeMin);
		}

		if (record.getTimeMax() != null) {
			Calendar timeMax = Calendar.getInstance();
			timeMax.setTime(record.getTimeMax());

			if (timeMax.get(Calendar.HOUR_OF_DAY) == 0 && timeMax.get(Calendar.MINUTE) == 0) {
				throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_ACTIVITY_TIME_MAX_ZERO);
			}

			toChange.setTimeMax(timeMax);
		}

		toChange.setHoursMax(record.getHoursMax());
		toChange.setFlagExport(record.getFlagExport());
		toChange.setFlagSum(record.getFlagSum());

		// only one activity can be default
		this.secureUniqueFlagDefault(loggedUser, record, toChange);
		toChange.setFlagDefault(record.getFlagDefault());

		this.activityDao.saveOrUpdate(toChange);

		return toChange.getId();
	}

	/**
	 * @see IActivityService#addOrUpdate(CActivityRecord)
	 */
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	@Override
	public Long addOrUpdate(final CActivityRecord record) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		// check
		CActivity toChange;

		// existence check
		try {
			this.checkActivityExistenceByNameAdd(changedBy, record.getName());
			// doesn't exists
			toChange = new CActivity();
		} catch (CBusinessException e) {
			if (e.getMessage().equals(CClientExceptionsMessages.ACTIVITY_ALREADY_EXISTS)) {
				// exists!!
				final List<CActivity> activities = this.activityDao.findByName(loggedUser.getClientInfo().getClientId(), record.getName());
				toChange = activities.get(0);
			} else {
				Logger.getLogger(this.getClass()).info(e);
				// another exception - throw it!
				throw new CBusinessException(e.getMessage());
			}
		}

		// sets changed by
		toChange.setChangedBy(changedBy);
		toChange.setChangeTime(Calendar.getInstance());

		// sets parameters
		toChange.setName(record.getName());
		toChange.setNote(record.getNote());
		toChange.setOrder(record.getOrder());
		toChange.setValid(record.getActive());
		toChange.setWorking(Boolean.TRUE); // vzdy bude pracovna
		toChange.setClient(changedBy.getClient());
		toChange.setChangeable(Boolean.TRUE); // vzdy sa bude dat zmenit
		if (toChange.getId() == null) {
			toChange.setFlagDefault(Boolean.FALSE); // ziadna nebude defaultna
		}

		this.activityDao.saveOrUpdate(toChange);

		return toChange.getId();
	}

	/**
	 * Converts db model to client model
	 * 
	 * @param input
	 * @return
	 */
	private List<CCodeListRecord> convert(final List<CActivity> input, final boolean addIndent) {
		final List<CCodeListRecord> retVal = new ArrayList<>();
		for (final CActivity activity : input) {
			final CCodeListRecord newRec = new CCodeListRecord();
			newRec.setId(activity.getId());
			newRec.setName((addIndent ? INDENT : "") + activity.getName());
			newRec.setDescription(activity.getNote());
			newRec.setType(activity.getWorking() ? IListBoxValueTypes.WORKING : IListBoxValueTypes.NON_WORKING);
			retVal.add(newRec);
		}
		return retVal;
	}

	@Transactional(readOnly = true)
	@Override
	public List<CCodeListRecord> getAllRecords() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		final List<CActivity> found = this.activityDao.findAll(loggedUser.getClientInfo().getClientId(), true);

		return this.convert(found, false);
	}

	/**
	 * @see IActivityService#getDetail(Long)
	 */
	@Transactional(readOnly = true)
	@Override
	public CActivityRecord getDetail(final Long timeStampId) {
		return this.activityDao.findById(timeStampId).convert();
	}

	@Transactional(readOnly = true)
	@Override
	public List<CCodeListRecord> getValidNonWorkingRecords() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final List<CActivity> found = this.activityDao.findAll(loggedUser.getClientInfo().getClientId(), true, false, true);
		return this.convert(found, true);
	}

	@Transactional(readOnly = true)
	@Override
	public List<CCodeListRecord> getValidRecords() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser user = this.userDao.findById(loggedUser.getUserId());
		List<CCodeListRecord> dateRangeList;

		List<CActivity> validUserActivities = user.getUserActivities().stream().filter(activity -> activity.getValid()).collect(Collectors.toList());

		if (!validUserActivities.isEmpty()) {
			// Ak ma nejake svoje projekty dam mu tie
			dateRangeList = this.getGruppedMyActivities(validUserActivities);
		} else {
			// Ak nema dam mu posledne pouzivane
			dateRangeList = this.getGruppedValidRecordsByLastUsed(false);
		}
		final List<CCodeListRecord> validList = this.getGruppedValidRecords(false);
		return this.synchronizeGrups(dateRangeList, validList);
	}

	@Transactional(readOnly = true)
	@Override
	public List<CCodeListRecord> getValidWorkingRecordsForUser() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser user = this.userDao.findById(loggedUser.getUserId());
		List<CCodeListRecord> dateRangeList;

		List<CActivity> validUserActivities = user.getUserActivities().stream().filter(activity -> activity.getValid() && activity.getWorking()).collect(Collectors.toList());

		if (!validUserActivities.isEmpty()) {
			// Ak ma nejake svoje projekty dam mu tie
			dateRangeList = this.getGruppedMyActivities(validUserActivities);
		} else {
			// Ak nema dam mu posledne pouzivane
			dateRangeList = this.getGruppedValidRecordsByLastUsed(true);
		}
		final List<CCodeListRecord> validList = this.getGruppedValidRecords(true);
		return this.synchronizeGrups(dateRangeList, validList);
	}

	@Transactional(readOnly = true)
	@Override
	public List<CCodeListRecord> getValidRecordsForLimits() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		final List<CActivity> found = this.activityDao.findAllForLimits(loggedUser.getClientInfo().getClientId(), Boolean.TRUE, true);

		return this.convert(found, true);
	}

	@Transactional(readOnly = true)
	@Override
	public List<CCodeListRecord> getValidRecords(final Long timesheetId) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		final List<CActivity> found = this.activityDao.findByValidityOrTimeSheet(loggedUser.getClientInfo().getClientId(), Boolean.TRUE, timesheetId);

		return this.convert(found, true);
	}

	@Transactional(readOnly = true)
	@Override
	public List<CCodeListRecord> getValidWorkingRecords() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		final List<CActivity> found = this.activityDao.findAll(loggedUser.getClientInfo().getClientId(), true, true, false);

		return this.convert(found, true);
	}

	/**
	 * checks during add
	 */
	private void checkActivityExistenceByNameAdd(final CUser loggedUser, final String name) throws CBusinessException {
		final List<CActivity> activities = this.activityDao.findByName(loggedUser.getClient().getId(), name);

		if (!activities.isEmpty()) {
			throw new CBusinessException(CClientExceptionsMessages.ACTIVITY_ALREADY_EXISTS);
		}
	}

	/**
	 * checks during add
	 */
	private void checkActivityExistenceByNameModify(final CUser loggedUser, final String name, final Long id) throws CBusinessException {
		final List<CActivity> activities = this.activityDao.findByName(loggedUser.getClient().getId(), name);

		for (final CActivity cActivity : activities) {
			if (!cActivity.getId().equals(id)) {
				throw new CBusinessException(CClientExceptionsMessages.ACTIVITY_ALREADY_EXISTS);
			}
		}
	}

	@Transactional(readOnly = false)
	@Override
	public void initialize(final Long clientId) {
		final CUser changedBy = this.userDao.findById(IUserDao.SYSTEM_USER);
		final Calendar changeTime = Calendar.getInstance();

		final List<CActivityInitial> toCopy = this.activityInitialDao.findAll();
		for (final CActivityInitial activity : toCopy) {
			final CActivity act = new CActivity();
			act.setChangedBy(changedBy);
			act.setChangeTime(changeTime);
			act.setClient(this.clientDao.findById(clientId));
			act.setName(activity.getName());
			act.setNote(activity.getNote());
			act.setOrder(activity.getOrder());
			act.setValid(activity.getValid());
			act.setWorking(activity.getWorking());
			act.setChangeable(activity.getChangeable());
			act.setFlagDefault(Boolean.FALSE);

			this.activityDao.saveOrUpdate(act);
		}

	}

	/**
	 * @see IActivityService#modify(Long, CActivityRecord)
	 */
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	@Override
	public void modify(final Long id, final CActivityRecord newRecord) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		// checks
		this.checkActivityExistenceByNameModify(changedBy, newRecord.getName(), id);

		// save game
		final CActivity toChange = this.activityDao.findById(id);

		// sets changed by
		toChange.setChangedBy(changedBy);
		toChange.setChangeTime(Calendar.getInstance());

		// sets parameters
		toChange.setName(newRecord.getName());
		toChange.setNote(newRecord.getNote());
		toChange.setOrder(newRecord.getOrder());
		toChange.setValid(newRecord.getActive());

		if (newRecord.getTimeMin() != null) {
			Calendar timeMin = Calendar.getInstance();
			timeMin.setTime(newRecord.getTimeMin());
			toChange.setTimeMin(timeMin);
		} else {
			toChange.setTimeMin(null);
		}

		if (newRecord.getTimeMax() != null) {
			Calendar timeMax = Calendar.getInstance();
			timeMax.setTime(newRecord.getTimeMax());

			if (timeMax.get(Calendar.HOUR_OF_DAY) == 0 && timeMax.get(Calendar.MINUTE) == 0) {
				throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_ACTIVITY_TIME_MAX_ZERO);
			}

			toChange.setTimeMax(timeMax);
		} else {
			toChange.setTimeMax(null);
		}

		toChange.setHoursMax(newRecord.getHoursMax());
		toChange.setFlagExport(newRecord.getFlagExport());
		toChange.setFlagSum(newRecord.getFlagSum());

		// only one activity can be default
		this.secureUniqueFlagDefault(loggedUser, newRecord, toChange);
		toChange.setFlagDefault(newRecord.getFlagDefault());

		this.activityDao.saveOrUpdate(toChange);
	}

	private void secureUniqueFlagDefault(CLoggedUserRecord loggedUser, CActivityRecord newRecord, CActivity toChange) {
		if (newRecord.getFlagDefault() != null && newRecord.getFlagDefault() && (toChange.getFlagDefault() == null || !toChange.getFlagDefault())) {
			// Je potrebne zabezpecit, ze pri nastaveni flagDefault na true sa
			// predchadzajucej defaultnej aktivite
			// danej organizacie s hodnotou priznaku true zmeni hodnota na
			// false, aby bol len jeden defaultny.
			final CActivity defaultActivity = this.activityDao.findDefaultActivity(loggedUser.getClientInfo().getClientId(), false);
			if (defaultActivity != null) {
				defaultActivity.setFlagDefault(Boolean.FALSE);
				this.activityDao.saveOrUpdate(defaultActivity);
			}
		}
	}

	private List<CCodeListRecord> getGruppedMyActivities(List<CActivity> validUserActivities) throws CSecurityException {
		final List<CCodeListRecord> retVal = this.convert(validUserActivities, true);
		retVal.add(0, new CCodeListRecord(ISearchConstants.ACTIVITY_GROUP_MY, IServerClientLocalizationKeys.LABEL_KEY + "label.group.activity.all_my_activities", "",
				IListBoxValueTypes.ACTIVITY_GROUP_LAST_USED));
		return retVal;
	}

	private List<CCodeListRecord> getGruppedValidRecordsByLastUsed(boolean onlyWorking) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		List<CActivity> found = this.activityDao.findAllByLastUsed(loggedUser.getUserId(), true);
		if (onlyWorking) {
			found = found.stream().filter(activity -> activity.getWorking()).collect(Collectors.toList());
		}
		final List<CCodeListRecord> retVal = this.convert(found, true);
		retVal.add(0, new CCodeListRecord(ISearchConstants.ACTIVITY_GROUP_LAST_USED, IServerClientLocalizationKeys.LABEL_KEY + "label.group.activity.last_used", "",
				IListBoxValueTypes.ACTIVITY_GROUP_LAST_USED));
		return retVal;
	}

	private List<CCodeListRecord> getGruppedValidRecords(boolean onlyWorking) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		List<CActivity> found = this.activityDao.findAll(loggedUser.getClientInfo().getClientId(), true, true);

		if (onlyWorking) {
			found = found.stream().filter(activity -> activity.getWorking()).collect(Collectors.toList());
		}
		final List<CCodeListRecord> retVal = this.convert(found, true);
		retVal.add(0, new CCodeListRecord(ISearchConstants.ACTIVITY_GROUP_ALL_OTHER, IServerClientLocalizationKeys.LABEL_KEY + "label.group.activity.all_activities", "",
				IListBoxValueTypes.ACTIVITY_GROUP_ALL_OTHER));
		return retVal;
	}

	private List<CCodeListRecord> synchronizeGrups(final List<CCodeListRecord> dateRangeList, final List<CCodeListRecord> validList) {
		final List<CCodeListRecord> retVal = new ArrayList<>(dateRangeList.size() + validList.size());
		final List<Long> cache = new ArrayList<>(validList.size());
		for (final CCodeListRecord record : validList) {
			cache.add(record.getId());
		}
		if (dateRangeList.size() > 1) {
			retVal.addAll(dateRangeList);
		}
		for (final CCodeListRecord record : dateRangeList) {
			final int cacheIndex = cache.indexOf(record.getId());
			if (cacheIndex != -1) {
				validList.remove(cacheIndex);
				cache.remove(cacheIndex);
			}
		}
		if (validList.size() > 1) {
			retVal.addAll(validList);
		}
		return retVal;
	}
}
