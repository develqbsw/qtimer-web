package sk.qbsw.sed.server.service.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.restriction.CRequestReasonData;
import sk.qbsw.sed.client.model.restriction.CRequestReasonListsData;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.business.IRequestReasonService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.IRequestReasonDao;
import sk.qbsw.sed.server.dao.IRequestTypeDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.codelist.CRequestReason;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.codelist.IActivityConstant;

/**
 * Service for read/write client request reasons
 * 
 * @author Ladislav Rosenberg
 * @version 1.6.6.2
 * @since 1.6.6.2
 */
@Service(value = "requestReasonService")
public class CRequestReasonServiceImpl implements IRequestReasonService {

	@Autowired
	IRequestReasonDao requestReasonDao;

	@Autowired
	IUserDao userDao;

	@Autowired
	IClientDao clientDao;

	@Autowired
	IRequestTypeDao requestTypeDao;

	@Autowired
	IActivityDao activityDao;

	/**
	 * @see IRequestReasonService#getDetail(Long)
	 */
	@Transactional(readOnly = true)
	public CRequestReasonData getDetail(Long recordId) {
		return this.convertEntity(this.requestReasonDao.findById(recordId));
	}

	/**
	 * @see IRequestReasonService#save(CRequestReasonData)
	 */
	@Transactional
	public CRequestReasonData save(CRequestReasonData data) throws CSecurityException, CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		CRequestReason entity = null;
		if (data.getId() == null) {
			entity = new CRequestReason();
			entity.setClient(this.clientDao.findById(loggedUser.getClientInfo().getClientId()));
			entity.setSystem(Boolean.FALSE);
		} else {
			entity = this.requestReasonDao.findById(data.getId());
			if (entity.getSystem()) {
				throw new CBusinessException(CClientExceptionsMessages.REQUEST_REASON_NOT_EDITABLE_RECORD);
			}
		}

		setDataToEntity(data, entity);

		entity.setChangeTime(Calendar.getInstance());
		entity.setChangedBy(changedBy);

		this.requestReasonDao.saveOrUpdate(entity);

		return this.convertEntity(entity);
	}

	private CRequestReasonData convertEntity(CRequestReason entity) {
		CRequestReasonData data = new CRequestReasonData();

		data.setId(entity.getId());
		data.setClientId(entity.getClient().getId());
		data.setCode(entity.getCode());
		data.setName(entity.getReasonName());
		data.setRequestTypeId(entity.getRequestType().getId());
		data.setValid(entity.getValid());
		data.setFlagSystem(entity.getSystem());

		return data;
	}

	private void setDataToEntity(CRequestReasonData data, CRequestReason entity) {
		entity.setCode(data.getCode());
		entity.setReasonName(data.getName());
		entity.setRequestType(this.requestTypeDao.findRecordById(data.getRequestTypeId()));
		entity.setValid(data.getValid());
	}

	/**
	 * @throws CSecurityException
	 * @see IRequestReasonService#getReasonLists(Long)
	 */
	@Transactional(readOnly = true)
	public CRequestReasonListsData getReasonLists(Long clientId) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		List<CRequestReason> systemSicknessTypeEntities = this.requestReasonDao.findSystemRecords(new Long(IRequestTypeConstant.RQTYPE_SICKNESS_ID));
		List<CCodeListRecord> systemSicknessReasonList = convertEntityList(systemSicknessTypeEntities);

		List<CRequestReason> systemWorkbreakTypeEntities = this.requestReasonDao.findSystemRecords(new Long(IRequestTypeConstant.RQTYPE_WORKBREAK_ID));
		List<CCodeListRecord> systemWorkbreakReasonList = convertEntityList(systemWorkbreakTypeEntities);

		List<CRequestReason> systemHomeOfficeTypeEntities = this.requestReasonDao.findSystemRecords(new Long(IRequestTypeConstant.RQTYPE_WORK_AT_HOME_ID));
		List<CCodeListRecord> systemHomeOfficeReasonList = convertEntityList(systemHomeOfficeTypeEntities);

		List<CRequestReason> homeOfficeTypeEntities = this.requestReasonDao.findValidByClientRequestType(loggedUser.getClientInfo().getClientId(),
				new Long(IRequestTypeConstant.RQTYPE_WORK_AT_HOME_ID), Boolean.TRUE);
		List<CCodeListRecord> homeOfficeReasonList = convertEntityList(homeOfficeTypeEntities);

		List<CRequestReason> sickTypeEntities = this.requestReasonDao.findValidByClientRequestType(loggedUser.getClientInfo().getClientId(), new Long(IRequestTypeConstant.RQTYPE_SICKNESS_ID),
				Boolean.TRUE);
		List<CCodeListRecord> sickReasonList = convertEntityList(sickTypeEntities);

		List<CRequestReason> workbreakTypeEntities = this.requestReasonDao.findValidByClientRequestType(loggedUser.getClientInfo().getClientId(), new Long(IRequestTypeConstant.RQTYPE_WORKBREAK_ID),
				Boolean.TRUE);
		List<CCodeListRecord> workbreakReasonList = convertEntityList(workbreakTypeEntities);

		CRequestReasonListsData retVal = new CRequestReasonListsData();
		retVal.setSicknessReasonList(systemSicknessReasonList);
		retVal.setSicknessReasonList(sickReasonList);

		retVal.setWorkbreakReasonList(systemWorkbreakReasonList);
		retVal.setWorkbreakReasonList(workbreakReasonList);

		retVal.setHomeofficeReasonList(systemHomeOfficeReasonList);
		retVal.setHomeofficeReasonList(homeOfficeReasonList);

		return retVal;
	}

	/**
	 * @throws CSecurityException
	 * @see IRequestReasonService#getReasonListsForListbox(Long)
	 */
	@Transactional(readOnly = true)
	public List<CCodeListRecord> getReasonListsForListbox() throws CSecurityException {

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		CActivity activityWorkbreak = this.activityDao.findById(IActivityConstant.NOT_WORK_WORKBREAK);
		CActivity activitySickness = this.activityDao.findById(IActivityConstant.NOT_WORK_SICKNESS);

		List<CRequestReason> systemSicknessTypeEntities = this.requestReasonDao.findSystemRecords(new Long(IRequestTypeConstant.RQTYPE_SICKNESS_ID));
		List<CCodeListRecord> systemSicknessReasonList = convertEntityList(activitySickness.getName(), systemSicknessTypeEntities);

		List<CRequestReason> systemWorkbreakTypeEntities = this.requestReasonDao.findSystemRecords(new Long(IRequestTypeConstant.RQTYPE_WORKBREAK_ID));
		List<CCodeListRecord> systemWorkbreakReasonList = convertEntityList(activityWorkbreak.getName(), systemWorkbreakTypeEntities);

		List<CRequestReason> sickTypeEntities = this.requestReasonDao.findValidByClientRequestType(loggedUser.getClientInfo().getClientId(), new Long(IRequestTypeConstant.RQTYPE_SICKNESS_ID),
				Boolean.TRUE);
		List<CCodeListRecord> sickReasonList = convertEntityList(activitySickness.getName(), sickTypeEntities);

		List<CRequestReason> workbreakTypeEntities = this.requestReasonDao.findValidByClientRequestType(loggedUser.getClientInfo().getClientId(), new Long(IRequestTypeConstant.RQTYPE_WORKBREAK_ID),
				Boolean.TRUE);
		List<CCodeListRecord> workbreakReasonList = convertEntityList(activityWorkbreak.getName(), workbreakTypeEntities);

		List<CCodeListRecord> reasonList = new ArrayList<>();

		CCodeListRecord empty = new CCodeListRecord();
		empty.setName("");
		reasonList.add(empty);

		reasonList.addAll(systemSicknessReasonList);
		reasonList.addAll(sickReasonList);
		reasonList.addAll(systemWorkbreakReasonList);
		reasonList.addAll(workbreakReasonList);

		return reasonList;
	}

	private List<CCodeListRecord> convertEntityList(String group, List<CRequestReason> entities) {
		List<CCodeListRecord> lsValuesList = new ArrayList<>();
		for (CRequestReason entity : entities) {
			CCodeListRecord tmp = new CCodeListRecord();
			tmp.setId(entity.getId());
			String name = group + ": " + entity.getReasonName();
			tmp.setDescription(name);
			tmp.setName(name);
			lsValuesList.add(tmp);
		}

		return lsValuesList;
	}

	private List<CCodeListRecord> convertEntityList(List<CRequestReason> entities) {
		List<CCodeListRecord> lsValuesList = new ArrayList<>();
		for (CRequestReason entity : entities) {
			CCodeListRecord tmp = new CCodeListRecord();
			tmp.setId(entity.getId());
			String name = entity.getReasonName();
			tmp.setDescription(name);
			tmp.setName(name);
			lsValuesList.add(tmp);
		}

		return lsValuesList;
	}
}
