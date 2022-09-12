package sk.qbsw.sed.server.service.business;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CJiraTokenGenerationRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.business.IJiraTokenGenerationService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CJiraException;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.jira.CJiraAuthenticationConfigurator;
import sk.qbsw.sed.server.jira.CJiraUtils;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * @author Ľudovít Kováč
 */
@Service(value = "jiraTokenGenerationService")
public class CJiraTokenGenerationServiceImpl implements IJiraTokenGenerationService {

	@Autowired
	private IUserDao userDao;
	
	@Autowired
	private CJiraAuthenticationConfigurator jiraAuthenticationConfigurator;
	
	@Override
	public CJiraTokenGenerationRecord getJiraTokenGenerationLink() throws CBusinessException {

		CJiraTokenGenerationRecord record = null;
		
		try {
			record = CJiraUtils.getJiraTokenGenerationLink(jiraAuthenticationConfigurator);
		} catch (CJiraException e) {
			Logger.getLogger(this.getClass()).info("Nepodarilo sa vygenerovať link: " + e.getMessage(), e);
			throw new CBusinessException(CClientExceptionsMessages.JIRA_TOKEN_GENERATION_LINK_ERROR);
		}
		
		return record;
	}
	
	@Override
	@Transactional
	public String generateJiraAccessToken(final CJiraTokenGenerationRecord record) throws CBusinessException {

		try {
			
			String jiraAccessToken = CJiraUtils.generateJiraAccessToken(jiraAuthenticationConfigurator, record);

			final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
			
			CUser user = this.userDao.findById(loggedUser.getUserId());
			
			user.setJiraAccessToken(jiraAccessToken);
			final Calendar changeTime = Calendar.getInstance();
			user.setChangeTime(changeTime);
			this.userDao.saveOrUpdate(user);

			return jiraAccessToken; 

		} catch (CJiraException e) {
			Logger.getLogger(this.getClass()).info("Nepodarilo sa vygenerovať token: " + e.getMessage(), e);
			throw new CBusinessException(CClientExceptionsMessages.JIRA_ACCESS_TOKEN_GENERATION_ERROR);
		}
	}
}
