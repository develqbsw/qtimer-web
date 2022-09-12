package sk.qbsw.sed.server.dao;

import java.util.List;
import java.util.Map;

import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.model.codelist.CEmailWithLanguage;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * Dao for sending email
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 *
 */
public interface INotificationDao {
	
	/**
	 * Sends notification regarding new password of the user
	 * 
	 * @param changedUser  user with new password
	 * @param purePassword not encrypted password
	 * @param purePIN      not encrypted PIN
	 */
	public void sendRenewPassword(CUser changedUser, String purePassword, String purePIN, boolean fromUserService) throws CBusinessException;

	public void sendNewUserWithoutPassword(CUser changedUser, String purePIN) throws CBusinessException;

	/**
	 * Sends notification regarding new password of the user
	 * 
	 * @param admin         user client administrator
	 * @param receptionUser generated reception user
	 */
	public void sendGeneratedPassword(CUser admin, CUser receptionUser, String pureAPassword, String purePassword) throws CBusinessException;

	/**
	 * Sends notification about missing users separated by reasons
	 * 
	 * @param receptionUsed     reception user
	 * @param holidayList       contains list of missing employees on holidays
	 * @param freePaidList      contains list of missing employees on free paid
	 * @param sickList          contains list of sick employees
	 * @param workbreakList     contains list of employees with workBreak (60%)
	 * @param busTripList       contains list of employees on bus trip
	 * @param staffTrainingList contains list of employees at staff training
	 * @param workAtHomeList    contains list of employees that works at home
	 * @param noRequestList     string contains list of missing employees without
	 *                          reason
	 * @param checkDatetime     check date, time
	 * @throws CBusinessException
	 */
	public void sendMissigEmployeesEmail(String email, List<String> holidayList, List<String> freePaidList, List<String> sickList, List<String> workbreakList, List<String> busTripList,
			List<String> staffTrainingList, List<String> workAtHomeList, List<String> noRequestList, String checkDatetime, List<String> outOfOfficeList, List<String> doctorVisitList)
			throws CBusinessException;

	/**
	 * Sends notification about processing the request
	 * 
	 * @param email
	 * @param model map of parameters values
	 * @throws CBusinessException in error case
	 */
	public void sendNotificationAboutUserRequest(CEmailWithLanguage email, Map<String, Object> model, String attachment, String attachmentFilename) throws CBusinessException;

	/**
	 * Posle email o žiadoasti aj s tlačidlami pre schvalenie/zrusenie ziadosti
	 * 
	 * @param email
	 * @param model
	 * @param id
	 * @param code
	 * @param subordinatesWithApprovedRequest
	 * @throws CBusinessException
	 */
	public void sendNotificationAboutUserRequestWithLinks(CEmailWithLanguage email, Map<String, Object> model, String id, String code, List<String> subordinatesWithApprovedRequest)
			throws CBusinessException;

	/**
	 * na dany email posle varovanie ze sa neodhlásil z práce
	 * 
	 * @param email
	 * @throws CSecurityException
	 */
	public void sendWarningToEmployee(List<CEmailWithLanguage> to, CUser warned, Map<String, Object> model) throws CBusinessException;
}
