package sk.qbsw.sed.server.service.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.client.model.restriction.CEmployeeActivityLimitsData;
import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.client.model.restriction.IDayTypeConstant;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.business.IActivityTimeLimitsService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.dao.IActivityIntervalDao;
import sk.qbsw.sed.server.dao.IActivityRestrictionGroupDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.IXUserActivityRestrictionDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.restriction.CActivityInterval;
import sk.qbsw.sed.server.model.restriction.CActivityRestrictionGroup;
import sk.qbsw.sed.server.model.restriction.CXUserActivityRestriction;

/**
 * Service for setting/modifying the user activity limits.
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
@Service(value = "activityTimeLimitsService")
public class CActivityTimeLimitsServiceImpl implements IActivityTimeLimitsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CActivityTimeLimitsServiceImpl.class);

	@Autowired
	IActivityRestrictionGroupDao activityRestrictionGroupDao;

	@Autowired
	IActivityIntervalDao activityIntervalDao;

	@Autowired
	IUserDao userDao;

	@Autowired
	IActivityDao activityDao;

	@Autowired
	IActivityRestrictionGroupDao groupDao;

	@Autowired
	IXUserActivityRestrictionDao xUserActivityRestrictionDao;

	// ---- LIMITS GROUPS ----

	/**
	 * @see IActivityTimeLimitsService#loadGroupDetail(Long)
	 */
	@Transactional(readOnly = true)
	@Override
	public CGroupsAIData loadGroupDetail(Long groupId) throws CBusinessException {
		CActivityRestrictionGroup group = activityRestrictionGroupDao.findById(groupId);
		return convertGroupEntity(group);
	}

	/**
	 * @see IActivityTimeLimitsService#saveGroup(CGroupsAIData)
	 */
	@Transactional
	@Override
	public CGroupsAIData saveGroup(CGroupsAIData data) throws CBusinessException {
		final CLoggedUserRecord loggedUserRecord = CServletSessionUtils.getLoggedUser();
		final CUser loggedUser = this.userDao.findById(loggedUserRecord.getUserId());

		CActivityRestrictionGroup entity;
		if (data.getId() != null) {
			entity = activityRestrictionGroupDao.findById(data.getId());
		} else {
			entity = new CActivityRestrictionGroup();
			entity.setClient(loggedUser.getClient());
		}
		entity.setName(data.getName());
		entity.setValid(data.getValid());
		entity.setActivity(activityDao.findById(data.getActivityId()));

		entity.setChangedBy(loggedUser);
		entity.setChangeTime(Calendar.getInstance());
		entity.setProjectGroup(data.getProjectGroup());

		this.activityRestrictionGroupDao.saveOrUpdate(entity);

		return convertGroupEntity(entity);
	}

	/**
	 * @see IActivityTimeLimitsService#deleteGroup(Long)
	 */
	@Transactional
	@Override
	public Boolean deleteGroup(Long groupId) throws CBusinessException {
		Boolean retVal = Boolean.TRUE;
		try {
			this.activityRestrictionGroupDao.delete(groupId);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
			retVal = Boolean.FALSE;
		}
		return retVal;
	}

	/**
	 * @see IActivityTimeLimitsService#getGroups(Long)
	 */
	@Transactional(readOnly = true)
	public List<CCodeListRecord> getGroups(Long clientId) throws CBusinessException {
		List<CActivityRestrictionGroup> groups = this.activityRestrictionGroupDao.findByClient(clientId);
		return this.convertListGroupEntities(groups);
	}

	/**
	 * @see IActivityTimeLimitsService#getValidActivityGroups(Long, Long, Boolean)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<CCodeListRecord> getValidActivityGroups(Long clientId, Long activityId, Boolean validFlag) throws CBusinessException {
		List<CActivityRestrictionGroup> groups = this.activityRestrictionGroupDao.findByClientActivityValidFlag(clientId, activityId, validFlag);
		return this.convertListGroupEntities(groups);
	}

	private List<CCodeListRecord> convertListGroupEntities(final List<CActivityRestrictionGroup> groups) {
		final List<CCodeListRecord> retVal = new ArrayList<>();
		for (final CActivityRestrictionGroup group : groups) {
			final CCodeListRecord newRec = new CCodeListRecord();
			newRec.setId(group.getId());
			newRec.setName(group.getName());
			newRec.setDescription(group.getName());
			newRec.setType(null);
			retVal.add(newRec);
		}
		return retVal;
	}

	private CGroupsAIData convertGroupEntity(CActivityRestrictionGroup entity) {
		CGroupsAIData data = new CGroupsAIData();

		data.setId(entity.getId());
		data.setName(entity.getName());
		data.setValid(entity.getValid());
		data.setActivityId(entity.getActivity().getId());
		data.setActivityName(entity.getActivity().getName());
		data.setProjectGroup(entity.getProjectGroup());

		return data;
	}

	// ---- LIMITS ----
	/**
	 * @see IActivityTimeLimitsService#loadIntervalDetail(Long)
	 */
	@Transactional(readOnly = true)
	@Override
	public CActivityIntervalData loadIntervalDetail(Long intervalId) throws CBusinessException {
		CActivityInterval interval = activityIntervalDao.findById(intervalId);
		return convertIntervalEntity(interval);
	}

	/**
	 * @see IActivityTimeLimitsService#saveInterval(CActivityIntervalData)
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public CActivityIntervalData saveInterval(CActivityIntervalData data) throws CBusinessException {
		final CLoggedUserRecord loggedUserRecord = CServletSessionUtils.getLoggedUser();
		final CUser loggedUser = this.userDao.findById(loggedUserRecord.getUserId());

		CActivityInterval entity;
		if (data.getId() != null) {
			entity = activityIntervalDao.findById(data.getId());
		} else {
			entity = new CActivityInterval();
			entity.setClient(loggedUser.getClient());
		}
		entity.setName(data.getName());
		entity.setValid(data.getValid());
		entity.setActivity(activityDao.findById(data.getActivityId()));

		if (data.getTimeFrom() != null) {
			Calendar timeFrom = Calendar.getInstance();
			timeFrom.setTime(data.getTimeFrom());
			entity.setTime_from(timeFrom);
		} else {
			entity.setTime_from(null);
		}

		if (data.getTimeTo() != null) {
			Calendar timeto = Calendar.getInstance();
			timeto.setTime(data.getTimeTo());
			entity.setTime_to(timeto);
		} else {
			entity.setTime_to(null);
		}

		if (data.getDayTypeId().longValue() != 0 && data.getDayTypeId().longValue() != 1) {
			throw new CBusinessException(CClientExceptionsMessages.DAY_TYPE_INVALID_VALUE);
		}
		entity.setDateType(data.getDayTypeId());
		entity.setGroup(groupDao.findById(data.getGroupId()));

		if (data.getDateValidFrom() != null) {
			Calendar dateFrom = Calendar.getInstance();
			dateFrom.setTime(data.getDateValidFrom());
			entity.setDate_from(dateFrom);
		} else {
			entity.setDate_from(null);
		}

		if (data.getDateValidTo() != null) {
			Calendar dateTo = Calendar.getInstance();
			dateTo.setTime(data.getDateValidTo());
			entity.setDate_to(dateTo);
		} else {
			entity.setDate_to(null);
		}

		entity.setChangedBy(loggedUser);
		entity.setChangeTime(Calendar.getInstance());

		if (IDayTypeConstant.DAY_TYPE_FREEDAY.equals(entity.getDateType())) {
			// interval pre nepracovny den musi zacinat 00:00 a koncit 23:59
			if (!((entity.getTime_from().get(Calendar.HOUR_OF_DAY) == 0 && entity.getTime_from().get(Calendar.MINUTE) == 0)
					&& (entity.getTime_to().get(Calendar.HOUR_OF_DAY) == 23 && entity.getTime_to().get(Calendar.MINUTE) == 59))) {
				throw new CBusinessException(CClientExceptionsMessages.INTERVAL_FREE_DAY_NOT_CORRECT);
			}
		} else {
			// interval pre pracovny den musi zacinat 00:00 alebo koncit 23:59
			if (!((entity.getTime_from().get(Calendar.HOUR_OF_DAY) == 0 && entity.getTime_from().get(Calendar.MINUTE) == 0)
					|| (entity.getTime_to().get(Calendar.HOUR_OF_DAY) == 23 && entity.getTime_to().get(Calendar.MINUTE) == 59))) {
				throw new CBusinessException(CClientExceptionsMessages.INTERVAL_WORK_DAY_NOT_CORRECT);
			}
		}

		this.activityIntervalDao.saveOrUpdate(entity);

		return this.convertIntervalEntity(entity);

	}

	/**
	 * @see IActivityTimeLimitsService#deleteInterval(Long)
	 */
	@Transactional
	public Boolean deleteInterval(Long intervalId) throws CBusinessException {
		Boolean retVal = Boolean.TRUE;
		try {
			this.activityIntervalDao.delete(intervalId);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
			retVal = Boolean.FALSE;
		}
		return retVal;
	}

	private CActivityIntervalData convertIntervalEntity(CActivityInterval entity) {
		CActivityIntervalData data = new CActivityIntervalData();

		data.setId(entity.getId());
		data.setName(entity.getName());
		data.setValid(entity.getValid());
		data.setActivityId(entity.getActivity().getId());

		if (entity.getDate_from() != null) {
			data.setDateValidFrom(entity.getDate_from().getTime());
		}
		
		if (entity.getDate_to() != null) {
			data.setDateValidTo(entity.getDate_to().getTime());
		}
		
		data.setDayTypeId(entity.getDateType());
		data.setGroupId(entity.getGroup().getId());
		if (entity.getTime_from() != null) {
			data.setTimeFrom(entity.getTime_from().getTime());
		}
		
		if (entity.getTime_to() != null) {
			data.setTimeTo(entity.getTime_to().getTime());
		}
		
		return data;
	}

	// --- EMPLOYEE LIMITS ---
	/**
	 * @see IActivityTimeLimitsService#loadEmployeeLimitsDetail(Long)
	 */
	@Transactional(readOnly = true)
	@Override
	public CEmployeeActivityLimitsData loadEmployeeLimitsDetail(Long employeeId) throws CBusinessException {

		return NTloadEmployeeLimitsDetail(employeeId);
	}

	/**
	 * @see IActivityTimeLimitsService#saveEmployeeLimits(CEmployeeActivityLimitsData)
	 */
	@Transactional
	@Override
	public CEmployeeActivityLimitsData saveEmployeeLimits(CEmployeeActivityLimitsData data) throws CBusinessException {
		Long employeeId = data.getEmployeeId();

		CUser employee = this.userDao.findById(employeeId);

		if (data.getEmployeeAssignedLimits() == null || data.getEmployeeAssignedLimits().isEmpty()) {
			// no limits
			this.xUserActivityRestrictionDao.deleteByUserId(employeeId);
		} else {
			List<CXUserActivityRestriction> oldEmployeeLimits = this.xUserActivityRestrictionDao.findByProjectGroupAndUser(employeeId, null);
			List<CCodeListRecord> newLimitsData = data.getEmployeeAssignedLimits();

			// there are some limits:
			// - remove not associated limits
			for (CXUserActivityRestriction oldLimit : oldEmployeeLimits) {
				Long oldLimitGroupId = oldLimit.getRestrictionGroup().getId();
				boolean contains = false;
				for (CCodeListRecord newLimitData : newLimitsData) {
					// look at
					// convertEmployeeLimitEntity(CActivityRestrictionGroup)
					if (newLimitData.getId().equals(oldLimitGroupId)) {
						contains = true;
					}
				}
				
				if (!contains) {
					// remove employee limit
					this.xUserActivityRestrictionDao.deleteById(oldLimit.getId());
				}
			}

			// - add missing limits
			for (CCodeListRecord newLimitData : newLimitsData) {
				Long newLimitGroupId = newLimitData.getId();
				boolean contains = false;
				for (CXUserActivityRestriction oldLimit : oldEmployeeLimits) {
					if (oldLimit.getRestrictionGroup().getId().equals(newLimitGroupId)) {
						contains = true;
					}
				}
				
				if (!contains) {
					// add newLimitGroupId
					CXUserActivityRestriction newLimitRecord = new CXUserActivityRestriction();
					newLimitRecord.setRestrictionGroup(this.groupDao.findById(newLimitGroupId));
					newLimitRecord.setUser(employee);
					this.xUserActivityRestrictionDao.saveOrUpdate(newLimitRecord);
				}
			}

		}

		// return actualized employee limits
		return NTloadEmployeeLimitsDetail(employeeId);
	}

	/**
	 * @see IActivityTimeLimitsService#deleteEmployeeLimits(Long)
	 */
	@Transactional
	public Boolean deleteEmployeeLimits(Long employeeId) throws CBusinessException {
		Boolean retVal = Boolean.TRUE;
		try {
			this.xUserActivityRestrictionDao.deleteByUserId(employeeId);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
			retVal = Boolean.FALSE;
		}
		return retVal;
	}

	private List<CCodeListRecord> convertEmployeeLimitEntityList(List<CActivityRestrictionGroup> list) {
		List<CCodeListRecord> retVal = new ArrayList<>();

		if (list != null) {
			for (CActivityRestrictionGroup group : list) {
				retVal.add(convertEmployeeLimitEntity(group));
			}
		}

		return retVal;
	}

	private CCodeListRecord convertEmployeeLimitEntity(CActivityRestrictionGroup group) {
		CCodeListRecord retVal = new CCodeListRecord();

		retVal.setId(group.getId());
		retVal.setName(group.getName());

		return retVal;
	}

	private CEmployeeActivityLimitsData NTloadEmployeeLimitsDetail(Long employeeId) throws CBusinessException {
		CEmployeeActivityLimitsData retVal = new CEmployeeActivityLimitsData();

		// load all valid clients limits
		final CLoggedUserRecord loggedUserRecord = CServletSessionUtils.getLoggedUser();
		final CUser loggedUser = this.userDao.findById(loggedUserRecord.getUserId());
		Long clientId = loggedUser.getClient().getId();
		Long activityId = null;
		Boolean validFlag = Boolean.TRUE;

		// find employee limits
		List<CActivityRestrictionGroup> assignedLimitGroups = new ArrayList<>();
		List<CActivityRestrictionGroup> notAssignedLimitGroups = new ArrayList<>();

		List<CActivityRestrictionGroup> clientValidLimitGroups = this.groupDao.findByClientActivityValidFlag(clientId, activityId, validFlag);
		List<CXUserActivityRestriction> employeeLimits = this.xUserActivityRestrictionDao.findByProjectGroupAndUser(employeeId, null);

		if (employeeLimits != null && !employeeLimits.isEmpty()) {
			for (CActivityRestrictionGroup group : clientValidLimitGroups) {
				boolean contains = false;
				for (CXUserActivityRestriction employeeLimit : employeeLimits) {
					if (employeeLimit.getRestrictionGroup().getId().equals(group.getId())) {
						contains = true;
					}
				}
				if (contains) {
					assignedLimitGroups.add(group);
				} else {
					notAssignedLimitGroups.add(group);
				}
			}
		} else {
			notAssignedLimitGroups = clientValidLimitGroups;
		}

		// prepare output values
		retVal.setEmployeeId(employeeId);
		retVal.setEmployeeAssignedLimits(convertEmployeeLimitEntityList(assignedLimitGroups));
		retVal.setEmployeeNotAssignedLimits(convertEmployeeLimitEntityList(notAssignedLimitGroups));

		return retVal;
	}
}
