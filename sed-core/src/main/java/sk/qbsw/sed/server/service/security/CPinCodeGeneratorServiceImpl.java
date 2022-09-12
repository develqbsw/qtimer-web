package sk.qbsw.sed.server.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.service.business.IPinCodeGeneratorService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.system.IAppCodeGenerator;

@Service(value = "pinCodeGeneratorService")
public class CPinCodeGeneratorServiceImpl implements IPinCodeGeneratorService {
	
	@Autowired
	IUserDao userDao;

	@Autowired
	IPinCodeGeneratorBaseService pinCodeGenerator;

	@Autowired
	private IAppCodeGenerator appCodeGenerator;

	/**
	 * @see IPinCodeGeneratorService#getGeneratedPIN(String, String)
	 */
	@Transactional
	public String getGeneratedPIN(String login, String oldPinValue) throws CBusinessException {
		CUser user = this.userDao.findByLogin(login);
		long mstime = System.currentTimeMillis();
		long mstime2 = (mstime / 1000);
		mstime = mstime - (mstime2 * 1000);

		if (user.getPinCodeSalt() == null) {
			user.setPinCodeSalt(this.appCodeGenerator.generatePinSalt());
		}

		String pinCode = this.pinCodeGenerator.generateUniquePinCode(mstime, 4, this.userDao.getPinCodes(user.getClient().getId()), this.userDao.getPinCodeSalts(user.getClient().getId()), oldPinValue,
				user.getPinCodeSalt());

		return pinCode;
	}
}
