package sk.qbsw.sed.server.service.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.registration.CRegistrationClientRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;
import sk.qbsw.sed.client.model.security.CClientInfo;
import sk.qbsw.sed.client.service.business.IClientService;
import sk.qbsw.sed.client.service.business.IRegistrationService;
import sk.qbsw.sed.client.service.business.IUserService;
import sk.qbsw.sed.client.service.codelist.IActivityService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

/**
 * Service for registering new client
 * 
 * @author Dalibor Rak
 * @since 0.1
 * @version 0.1
 * 
 */
@Service(value = "registrationService")
public class CRegistrationServiceImpl implements IRegistrationService {
	
	@Autowired
	private IUserService userService;

	@Autowired
	private IClientService clientService;

	@Autowired
	private IActivityService activityService;

	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void register(final CRegistrationClientRecord org, final CRegistrationUserRecord user) throws CBusinessException {
		final Long clientId = this.clientService.add(org);
		final CClientInfo clientInfo = new CClientInfo();
		clientInfo.setClientId(clientId);

		// creates user
		user.setClientInfo(clientInfo);
		this.userService.addByRegistration(user);

		// initializes activities
		this.activityService.initialize(clientId);
	}
}
