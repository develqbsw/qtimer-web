package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.client.model.codelist.CJiraTokenGenerationRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IJiraTokenGenerationClientService {

	public CJiraTokenGenerationRecord getJiraTokenGenerationLink() throws CBussinessDataException;
	
	public String generateJiraAccessToken(final CJiraTokenGenerationRecord jiraTokenGenerationRecord) throws CBussinessDataException;

}
