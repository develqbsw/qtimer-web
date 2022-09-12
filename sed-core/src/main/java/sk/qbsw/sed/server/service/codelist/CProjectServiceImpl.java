package sk.qbsw.sed.server.service.codelist;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IListBoxValueTypes;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.client.model.codelist.CResultProjectsGroups;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.codelist.IProjectService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IProjectDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CProject;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.IServerClientLocalizationKeys;

/**
 * Service for management of Activity code list
 * 
 * @author Dalibor Rak
 * @since 0.1
 * @version 0.1
 */
@Service(value = "projectService")
public class CProjectServiceImpl implements IProjectService {
	private static final String INDENT = "";

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IUserDao userDao;

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public Long add(final CProjectRecord record) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		// check
		this.checkExistenceByNameAdd(changedBy, record.getName());
		this.checkExistenceByCombinationGroupAndProjectId(changedBy, record.getGroup(), record.getCode());

		// SAVE GAME
		final CProject toChange = new CProject();

		// sets changed by
		toChange.setChangedBy(changedBy);
		toChange.setChangeTime(Calendar.getInstance());

		// sets parameters
		toChange.setName(record.getName());
		toChange.setNote(record.getNote());
		toChange.setOrder(record.getOrder());
		toChange.setValid(record.getActive());
		toChange.setClient(changedBy.getClient());
		toChange.setEviproCode(record.getCode());
		toChange.setGroup(record.getGroup());

		// only one project can be default
		this.secureUniqueFlagDefault(loggedUser, record, toChange);
		toChange.setFlagDefault(record.getFlagDefault());

		this.projectDao.saveOrUpdate(toChange);

		return toChange.getId();
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public Long addOrUpdate(final CProjectRecord record, List<Long> projectIds) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		CProject toChange;

		// existence check
		final List<CProject> projects = this.projectDao.findByName(changedBy.getClient().getId(), record.getName());

		if (!projects.isEmpty()) {
			// exists!!
			toChange = projects.get(0);
		} else {
			// doesn't exists
			toChange = new CProject();
		}

		// SAVE GAME

		// sets changed by
		toChange.setChangedBy(changedBy);
		toChange.setChangeTime(Calendar.getInstance());

		// sets parameters
		toChange.setName(record.getName());
		toChange.setNote(record.getNote());
		toChange.setOrder(record.getOrder());
		toChange.setValid(record.getActive());
		toChange.setClient(changedBy.getClient());

		toChange.setGroup(record.getGroup());
		toChange.setEviproCode(record.getCode());
		if (null == toChange.getId()) {
			toChange.setFlagDefault(Boolean.FALSE);
		}

		this.projectDao.saveOrUpdate(toChange);

		if (projectIds != null && projectIds.contains(toChange.getId())) {
			projectIds.remove(toChange.getId());
		}

		return toChange.getId();
	}

	/**
	 * Converts db model to client model
	 * 
	 * @param input
	 * @return
	 */
	private List<CCodeListRecord> convert(final List<CProject> input, final boolean addIndent) {
		final List<CCodeListRecord> retVal = new ArrayList<>();
		for (final CProject project : input) {
			final CCodeListRecord newRec = new CCodeListRecord();
			newRec.setId(project.getId());
			newRec.setName((addIndent ? INDENT : "") + project.getName());
			newRec.setDescription(project.getNote());
			retVal.add(newRec);
		}
		return retVal;
	}

	/**
	 * Converts db model to client model
	 * 
	 * @param input
	 * @return
	 */
	private List<CCodeListRecord> convertToGroups(final List<CProject> input, final boolean addIndent) {
		List<String> tmpGroups = new ArrayList<>();

		for (final CProject project : input) {
			if (project.getGroup() != null && !"".equals(project.getGroup()) && !tmpGroups.contains(project.getGroup())) {
				tmpGroups.add(project.getGroup());
			}
		}
		sortByLocale(tmpGroups);

		final List<CCodeListRecord> retVal = new ArrayList<>();
		for (String groupName : tmpGroups) {
			final CCodeListRecord newRec = new CCodeListRecord();
			newRec.setId(new Long(tmpGroups.indexOf(groupName)));
			newRec.setName((addIndent ? INDENT : "") + groupName);
			newRec.setDescription("");
			retVal.add(newRec);
		}

		return retVal;
	}

	/**
	 * Converts model for client
	 * 
	 * @param project
	 * @return
	 */
	private CProjectRecord convertToRecord(final CProject project) {
		final CProjectRecord retVal = new CProjectRecord();

		retVal.setId(project.getId());
		retVal.setActive(project.getValid());
		retVal.setChangeName(project.getChangedBy().getName());
		retVal.setChangeSurname(project.getChangedBy().getSurname());
		retVal.setChangeTime(project.getChangeTime().getTime());
		retVal.setName(project.getName());
		retVal.setNote(project.getNote());
		retVal.setGroup(project.getGroup());
		retVal.setCode(project.getEviproCode());

		if (project.getOrder() != null) {
			retVal.setOrder((int) project.getOrder());
		} else {
			retVal.setOrder(null);
		}
		retVal.setFlagDefault(project.getFlagDefault());
		retVal.setEviproCode(project.getEviproCode());

		return retVal;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CCodeListRecord> getAllRecords() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final List<CProject> found = this.projectDao.findAll(loggedUser.getClientInfo().getClientId());
		return this.convert(found, false);
	}

	@Override
	@Transactional(readOnly = true)
	public CResultProjectsGroups getAllRecordsWithGroups() throws CSecurityException {
		final CResultProjectsGroups result = new CResultProjectsGroups();

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final List<CProject> found = this.projectDao.findAll(loggedUser.getClientInfo().getClientId());

		final List<CCodeListRecord> projectGroups = this.convertToGroups(found, false);
		result.setProjectGroups(projectGroups);

		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public CProjectRecord getDetail(final Long timeStampId) {
		return this.convertToRecord(this.projectDao.findById(timeStampId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<CCodeListRecord> getValidRecords() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser user = this.userDao.findById(loggedUser.getUserId());
		List<CCodeListRecord> dateRangeList;

		List<CProject> validUserProjects = new ArrayList<>();

		for (CProject p : user.getUserProjects()) {
			if (p.getValid()) {
				validUserProjects.add(p);
			}
		}

		if (!validUserProjects.isEmpty()) {
			// Ak ma nejake svoje projekty dam mu tie
			dateRangeList = this.getGruppedMyProjects(validUserProjects);
		} else {
			// Ak nema dam mu posledne pouzivane
			dateRangeList = this.getGruppedValidRecordsByLastUsed();
		}
		final List<CCodeListRecord> validList = this.getGruppedValidRecords();
		return this.synchronizeGrups(dateRangeList, validList);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CCodeListRecord> getValidRecordsForUser(final Long userId) throws CSecurityException {
		final CUser user = this.userDao.findById(userId);
		List<CCodeListRecord> dateRangeList;

		List<CProject> validUserProjects = new ArrayList<>();

		for (CProject p : user.getUserProjects()) {
			if (p.getValid()) {
				validUserProjects.add(p);
			}
		}

		if (!validUserProjects.isEmpty()) {
			// Ak ma nejake svoje projekty dam mu tie
			dateRangeList = this.getGruppedMyProjects(validUserProjects);
		} else {
			// Ak nema dam mu posledne pouzivane
			dateRangeList = this.getGruppedValidRecordsByLastUsed(userId);
		}
		final List<CCodeListRecord> validList = this.getGruppedValidRecords();
		return this.synchronizeGrups(dateRangeList, validList);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CCodeListRecord> getValidRecords(final Long timesheetId) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final List<CCodeListRecord> dateRangeList = this.getGruppedValidRecordsByLastUsed();
		final List<CProject> found = this.projectDao.findByValidityOrTimeSheet(loggedUser.getClientInfo().getClientId(), true, timesheetId);
		List<CCodeListRecord> retVal = this.convert(found, true);
		retVal.add(0, new CCodeListRecord(ISearchConstants.PROJECT_GROUP_ALL_OTHER, IServerClientLocalizationKeys.LABEL_KEY + "label.group.project.all_projects", "",
				IListBoxValueTypes.PROJECT_GROUP_ALL_OTHER));
		retVal = this.synchronizeGrups(dateRangeList, retVal);
		return retVal;
	}

	/**
	 * checks during add
	 */
	private void checkExistenceByNameAdd(final CUser loggedUser, final String name) throws CBusinessException {
		final List<CProject> projects = this.projectDao.findByName(loggedUser.getClient().getId(), name);

		if (!projects.isEmpty()) {
			throw new CBusinessException(CClientExceptionsMessages.PROJECT_ALREADY_EXISTS);
		}
	}

	private void checkExistenceByCombinationGroupAndProjectId(final CUser loggedUser, final String group, final String projectId) throws CBusinessException {
		final List<CProject> projects = this.projectDao.findByGroupAndProjectId(loggedUser.getClient().getId(), group, projectId);

		if (!projects.isEmpty()) {
			throw new CBusinessException(CClientExceptionsMessages.PROJECT_COMBINATION_GROUP_AND_ID_ALREADY_EXISTS);
		}
	}

	/**
	 * checks during modify
	 */
	private void checkExistenceByNameModify(final CUser loggedUser, final String name, final Long id) throws CBusinessException {
		final List<CProject> projects = this.projectDao.findByName(loggedUser.getClient().getId(), name);

		for (final CProject project : projects) {
			if (!project.getId().equals(id)) {
				throw new CBusinessException(CClientExceptionsMessages.PROJECT_ALREADY_EXISTS);
			}
		}
	}

	/**
	 * Modifies record
	 */
	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void modify(final Long id, final CProjectRecord newRecord, final Date tiestamp) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		// checks
		this.checkExistenceByNameModify(changedBy, newRecord.getName(), id);

		// save game
		final CProject toChange = this.projectDao.findById(id);

		// sets changed by
		toChange.setChangedBy(changedBy);
		toChange.setChangeTime(Calendar.getInstance());

		// sets parameters
		toChange.setName(newRecord.getName());
		toChange.setNote(newRecord.getNote());
		toChange.setOrder(newRecord.getOrder());
		toChange.setValid(newRecord.getActive());

		toChange.setEviproCode(newRecord.getCode());
		toChange.setGroup(newRecord.getGroup());

		// only one project can be default
		this.secureUniqueFlagDefault(loggedUser, newRecord, toChange);
		toChange.setFlagDefault(newRecord.getFlagDefault());

		this.projectDao.saveOrUpdate(toChange);
	}

	private List<CCodeListRecord> getGruppedMyProjects(List<CProject> validUserProjects) throws CSecurityException {
		final List<CCodeListRecord> retVal = this.convert(validUserProjects, true);
		retVal.add(0, new CCodeListRecord(ISearchConstants.PROJECT_GROUP_MY, IServerClientLocalizationKeys.LABEL_KEY + "label.group.project.all_my_projects", "",
				IListBoxValueTypes.PROJECT_GROUP_LAST_USED));
		return retVal;
	}

	private List<CCodeListRecord> getGruppedValidRecordsByLastUsed() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final List<CProject> found = this.projectDao.findAllByLastUsed(loggedUser.getUserId(), true);
		final List<CCodeListRecord> retVal = this.convert(found, true);
		retVal.add(0, new CCodeListRecord(ISearchConstants.PROJECT_GROUP_LAST_USED, IServerClientLocalizationKeys.LABEL_KEY + "label.group.project.last_used", "",
				IListBoxValueTypes.PROJECT_GROUP_LAST_USED));
		return retVal;
	}

	private List<CCodeListRecord> getGruppedValidRecordsByLastUsed(Long userId) throws CSecurityException {
		final List<CProject> found = this.projectDao.findAllByLastUsed(userId, true);
		final List<CCodeListRecord> retVal = this.convert(found, true);
		retVal.add(0, new CCodeListRecord(ISearchConstants.PROJECT_GROUP_LAST_USED, IServerClientLocalizationKeys.LABEL_KEY + "label.group.project.last_used", "",
				IListBoxValueTypes.PROJECT_GROUP_LAST_USED));
		return retVal;
	}

	private List<CCodeListRecord> getGruppedValidRecords() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final List<CProject> found = this.projectDao.findAll(loggedUser.getClientInfo().getClientId(), true);
		final List<CCodeListRecord> retVal = this.convert(found, true);
		retVal.add(0, new CCodeListRecord(ISearchConstants.PROJECT_GROUP_ALL_OTHER, IServerClientLocalizationKeys.LABEL_KEY + "label.group.project.all_projects", "",
				IListBoxValueTypes.PROJECT_GROUP_ALL_OTHER));
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

	@Override
	@Transactional(readOnly = false)
	public List<Long> invalidateLoggedClientProjects() throws CSecurityException {
		List<Long> retVal = new ArrayList<>();

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		List<CProject> activeProjects = this.projectDao.findAll(loggedUser.getClientInfo().getClientId(), true);
		for (CProject project : activeProjects) {
			retVal.add(project.getId());
			project.setValid(Boolean.FALSE);
			this.projectDao.saveOrUpdate(project);
		}

		return retVal;
	}

	@Override
	@Transactional(readOnly = false)
	public void validateSelectedProjects(List<Long> projectIds) throws CSecurityException {
		if (projectIds != null) {
			for (Long projectId : projectIds) {
				CProject project = this.projectDao.findById(projectId);
				project.setValid(Boolean.TRUE);
				this.projectDao.saveOrUpdate(project);
			}
		}
	}

	private void sortByLocale(List<String> list) {
		String sLocale = (String) CServletSessionUtils.getHttpSession().getAttribute("locale");
		Collator collator;
		if (sLocale != null) {
			collator = Collator.getInstance(new Locale(sLocale));
		} else {
			collator = Collator.getInstance(new Locale("sk"));
			collator.setStrength(Collator.IDENTICAL);
		}
		Collections.sort(list, collator);

	}

	private void secureUniqueFlagDefault(CLoggedUserRecord loggedUser, CProjectRecord newRecord, CProject toChange) {
		if (newRecord.getFlagDefault() != null && newRecord.getFlagDefault() && (toChange.getFlagDefault() == null || !toChange.getFlagDefault())) {
			// Je potrebne zabezpecit, ze pri nastaveni flagDefault na true sa predchadzajucemu defaultnemu projektu
			// danej organizacie s hodnotou priznaku true zmeni hodnota na false, aby bol len jeden defaultny.
			final CProject defaultProject = this.projectDao.findDefaultProject(loggedUser.getClientInfo().getClientId());
			if (defaultProject != null) {
				defaultProject.setFlagDefault(Boolean.FALSE);
				this.projectDao.saveOrUpdate(defaultProject);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CResultProjectsGroups getValidRecordsWithGroups() throws CSecurityException {
		final CResultProjectsGroups result = new CResultProjectsGroups();

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final List<CProject> found = this.projectDao.findAll(loggedUser.getClientInfo().getClientId(), true);

		final List<CCodeListRecord> projectGroups = this.convertToGroups(found, false);
		result.setProjectGroups(projectGroups);

		return result;
	}
}
