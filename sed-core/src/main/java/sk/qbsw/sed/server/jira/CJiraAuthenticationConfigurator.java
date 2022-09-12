package sk.qbsw.sed.server.jira;

import org.springframework.stereotype.Service;

/**
 * 
 * Class for storing JIRA authentication credentials from config.properties
 * 
 * @author lobb
 *
 */
@Service("jiraAuthenticationConfigurator")
public class CJiraAuthenticationConfigurator {

	private String baseUrl;
	private String consumerKey;
	private String consumerPrivateKey;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerPrivateKey() {
		return consumerPrivateKey;
	}

	public void setConsumerPrivateKey(String consumerPrivateKey) {
		this.consumerPrivateKey = consumerPrivateKey;
	}
}
