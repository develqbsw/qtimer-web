package sk.qbsw.sed.client.service.business;

import sk.qbsw.sed.client.model.codelist.CJiraTokenGenerationRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

/**
 * @author Ľudovít Kováč
 * 
 */
public interface IJiraTokenGenerationService {

	public CJiraTokenGenerationRecord getJiraTokenGenerationLink() throws CBusinessException;
	
	public String generateJiraAccessToken(final CJiraTokenGenerationRecord record) throws CBusinessException;

}
