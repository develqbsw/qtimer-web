package sk.qbsw.sed.server.service.business;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.ILanguageConstant;
import sk.qbsw.sed.client.model.detail.CClientDetailRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationClientRecord;
import sk.qbsw.sed.client.model.security.CClientInfo;
import sk.qbsw.sed.client.service.business.IClientService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.ILegalFormDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.util.CLocaleUtils;

/**
 * Service for management of organization
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Service(value = "clientService")
public class CClientServiceImpl implements IClientService {
	@Autowired
	private IUserDao userDao;

	@Autowired
	private IClientDao clientDao;

	@Autowired
	private ILegalFormDao legalFormDao;

	@Transactional(rollbackForClassName = "CBusinessException")
	@Override
	public Long add(final CRegistrationClientRecord orgToAdd) throws CBusinessException {
		if (orgToAdd == null) {
			return null;
		}

		// checks
		this.checkUniqueIdNo(orgToAdd.getIdNo());
		this.checkUniqueTaxNo(orgToAdd.getTaxNo());
		this.checkUniqueVatNo(orgToAdd.getVatNo());

		// save game
		final CClient newClient = new CClient();
		newClient.setChangedBy(this.userDao.findById(IUserDao.SYSTEM_USER));
		newClient.setChangeTime(Calendar.getInstance());

		newClient.setCity(orgToAdd.getCity());
		newClient.setCountry(orgToAdd.getCountry());

		newClient.setName(orgToAdd.getOrgName());
		newClient.setLegalForm(this.legalFormDao.findById(orgToAdd.getLegalForm()));
		newClient.setIdentificationNumber(orgToAdd.getIdNo());
		newClient.setTaxNumber(orgToAdd.getTaxNo());
		newClient.setTaxVatNumber(orgToAdd.getVatNo());
		newClient.setStreet(orgToAdd.getStreet());
		newClient.setStreetNumber(orgToAdd.getStreetNo());
		newClient.setZip(orgToAdd.getZip());
		newClient.setValid(Boolean.TRUE);
		newClient.setNameShort(orgToAdd.getOrgName());
		newClient.setProjectRequired(Boolean.TRUE); // default true
		newClient.setActivityRequired(Boolean.TRUE); // default true
		// SED-370 pri registracii novej organizacie nastavit automaticky podla aktualneho jazyka.
		newClient.setLanguage(CLocaleUtils.getLocale().toString());
		newClient.setGenerateMessages(false);

		this.clientDao.saveOrUpdate(newClient);

		return newClient.getId();
	}

	/**
	 * Checks existence of user on DB
	 * 
	 * @param login
	 */
	private void checkUniqueIdNo(final String idNo) throws CBusinessException {
		if (idNo == null) {
			return;
		}

		final CClient client = this.clientDao.findByIdNo(idNo);

		if (client != null) {
			throw new CBusinessException(CClientExceptionsMessages.CLIENT_ID_USED);
		}
	}

	/**
	 * Checks existence of user on DB
	 * 
	 * @param login
	 */
	private void checkUniqueTaxNo(final String taxNo) throws CBusinessException {
		if (taxNo == null) {
			return;
		}

		final CClient client = this.clientDao.findByTaxNo(taxNo);

		if (client != null) {
			throw new CBusinessException(CClientExceptionsMessages.CLIENT_TAX_USED);
		}
	}

	/**
	 * Checks existence of user on DB
	 * 
	 * @param login
	 */
	private void checkUniqueVatNo(final String vatNo) throws CBusinessException {
		if (vatNo == null) {
			return;
		}

		final CClient client = this.clientDao.findByVatNo(vatNo);

		if (client != null) {
			throw new CBusinessException(CClientExceptionsMessages.CLIENT_VAT_USED);
		}
	}

	@Transactional(rollbackForClassName = "CBusinessException")
	@Override
	public void updateFlags(final CClientInfo clientInfo) throws CBusinessException {
		final CClient client = this.clientDao.findById(clientInfo.getClientId());
		client.setProjectRequired(clientInfo.getProjectRequired());
		client.setActivityRequired(clientInfo.getActivityRequired());
		this.clientDao.saveOrUpdate(client);

		(CServletSessionUtils.getLoggedUser()).setClientInfo(clientInfo);
	}

	@Transactional(readOnly = true)
	@Override
	public CClientDetailRecord getDetail(Long clientId) throws CBusinessException {
		CClientDetailRecord retVal = new CClientDetailRecord();

		CClient client = this.clientDao.findById(clientId);
		if (client != null) {
			retVal.setClientId(clientId);
			retVal.setName(client.getName());
			retVal.setNameShort(client.getNameShort());
			retVal.setLegalFormId(client.getLegalForm().getId());

			retVal.setStreet(client.getStreet());
			retVal.setStreetNumber(client.getStreetNumber());
			retVal.setCity(client.getCity());
			retVal.setZipCode(client.getZip());

			retVal.setProjectRequiredFlag(client.getProjectRequired());
			retVal.setActivityRequiredFlag(client.getActivityRequired());

			if (client.getTimeAutoEmail() != null) {
				retVal.setTimeAutoEmail(client.getTimeAutoEmail().getTime());
			}
			if (client.getTimeAutoGenerateTimestamps() != null) {
				retVal.setTimeAutoGenerateTimestamps(client.getTimeAutoGenerateTimestamps().getTime());
			}
			if (client.getDayShiftAutoGenerateTimestamps() != null) {
				retVal.setShiftDay(client.getDayShiftAutoGenerateTimestamps());
			}
			if (client.getLegalForm() != null) {
				retVal.setShiftDay(client.getDayShiftAutoGenerateTimestamps());
			}
			if (client.getIntervalStopWorkRec() != null) {
				retVal.setIntervalStopWorkRec(client.getIntervalStopWorkRec().getTime());
			}

			retVal.setLanguage(convertLanguageToCode(client.getLanguage()));
			retVal.setGenerateMessages(client.getGenerateMessages());
			retVal.setBonusVacation(client.getBonusVacation());
		}

		return retVal;
	}

	private Long convertLanguageToCode(String language) {
		if (ILanguageConstant.SK.equals(language)) {
			return ILanguageConstant.ID_SK;
		} else if (ILanguageConstant.EN.equals(language)) {
			return ILanguageConstant.ID_EN;
		} else {
			return -1L;
		}
	}

	@Transactional(rollbackForClassName = "CBusinessException")
	@Override
	public void updateDetail(CClientDetailRecord clientDetail) throws CBusinessException {
		CClient client = this.clientDao.findClientById(clientDetail.getClientId());
		if (client != null) {
			client.setChangedBy(this.userDao.findClientAdministratorAccount(clientDetail.getClientId()));
			client.setChangeTime(Calendar.getInstance());

			if (clientDetail.getName() != null) {
				client.setName(clientDetail.getName());
			}
			if (clientDetail.getNameShort() != null) {
				client.setNameShort(clientDetail.getNameShort());
			}
			client.setLegalForm(this.legalFormDao.findById(clientDetail.getLegalFormId()));

			client.setStreet(clientDetail.getStreet());
			client.setStreetNumber(clientDetail.getStreetNumber());
			client.setCity(clientDetail.getCity());
			client.setZip(clientDetail.getZipCode());

			client.setProjectRequired(clientDetail.getProjectRequiredFlag());
			client.setActivityRequired(clientDetail.getActivityRequiredFlag());

			if (clientDetail.getTimeAutoEmail() != null) {
				Calendar cTimeAutoEmail = Calendar.getInstance();
				cTimeAutoEmail.setTime(clientDetail.getTimeAutoEmail());
				client.setTimeAutoEmail(cTimeAutoEmail);
			} else {
				client.setTimeAutoEmail(null);
			}
			
			if (clientDetail.getTimeAutoGenerateTimestamps() != null) {
				Calendar cTimeAutoGenerate = Calendar.getInstance();
				cTimeAutoGenerate.setTime(clientDetail.getTimeAutoGenerateTimestamps());
				client.setTimeAutoGenerateTimestamps(cTimeAutoGenerate);
			} else {
				client.setTimeAutoGenerateTimestamps(null);
			}
			client.setDayShiftAutoGenerateTimestamps(clientDetail.getShiftDay());
			client.setLanguage(convertCodeToLanguage(clientDetail.getLanguage()));

			if (clientDetail.getIntervalStopWorkRec() != null) {
				Calendar cIntervalStopWorkRec = Calendar.getInstance();
				cIntervalStopWorkRec.setTime(clientDetail.getIntervalStopWorkRec());
				client.setIntervalStopWorkRec(cIntervalStopWorkRec);
			} else {
				client.setIntervalStopWorkRec(null);
			}
			client.setGenerateMessages(clientDetail.getGenerateMessages());
			client.setBonusVacation(clientDetail.getBonusVacation());

			this.clientDao.saveOrUpdate(client);
		} else {
			throw new CBusinessException(CClientExceptionsMessages.CLIENT_NOT_FOUND);
		}
	}

	private String convertCodeToLanguage(Long code) {
		if (ILanguageConstant.ID_SK.equals(code)) {
			return ILanguageConstant.SK;
		} else if (ILanguageConstant.ID_EN.equals(code)) {
			return ILanguageConstant.EN;
		} else {
			return "";
		}
	}
}
