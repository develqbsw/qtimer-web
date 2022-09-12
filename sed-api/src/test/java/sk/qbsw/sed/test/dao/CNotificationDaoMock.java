package sk.qbsw.sed.test.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.INotificationDao;
import sk.qbsw.sed.server.model.codelist.CEmailWithLanguage;
import sk.qbsw.sed.server.model.domain.CUser;

@Repository(value = "notificationDaoMock")
public class CNotificationDaoMock implements INotificationDao {

	@Override
	public void sendRenewPassword(CUser changedUser, String purePassword,
			String purePIN, boolean fromUserService) throws CBusinessException {
		
	}

	@Override
	public void sendNewUserWithoutPassword(CUser changedUser, String purePIN)
			throws CBusinessException {
		
	}

	@Override
	public void sendGeneratedPassword(CUser admin, CUser receptionUser,
			String pureAPassword, String purePassword)
			throws CBusinessException {
		
	}

	@Override
	public void sendNotificationAboutUserRequest(CEmailWithLanguage email,
			Map<String, Object> model, String attachment, String attachmentFilename)
			throws CBusinessException {
		
	}

	@Override
	public void sendNotificationAboutUserRequestWithLinks(
			CEmailWithLanguage email, Map<String, Object> model, String id,
			String code, List<String> subordinatesWithApprovedRequest)
			throws CBusinessException {
		
	}

	@Override
	public void sendWarningToEmployee(List<CEmailWithLanguage> to,
			CUser warned, Map<String, Object> model) throws CBusinessException {
		
	}

	@Override
	public void sendMissigEmployeesEmail(String email,
			List<String> holidayList, List<String> freePaidList,
			List<String> sickList, List<String> workbreakList,
			List<String> busTripList, List<String> staffTrainingList,
			List<String> workAtHomeList, List<String> noRequestList, 
			String checkDatetime, List<String> outOfOfficeList, 
			List<String> doctorVisitList) throws CBusinessException {		
	}

}
