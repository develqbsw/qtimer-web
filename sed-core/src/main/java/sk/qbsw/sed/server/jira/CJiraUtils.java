package sk.qbsw.sed.server.jira;

import java.util.ArrayList;
import java.util.Calendar;

import com.google.common.collect.Lists;

import sk.qbsw.sed.client.model.codelist.CJiraTokenGenerationRecord;
import sk.qbsw.sed.common.utils.CDateRange;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.server.exception.CJiraException;
import sk.qbsw.sed.server.jira.JIRAOAuthClient.Command;

/**
 * 
 * @author lobb
 *
 */
public class CJiraUtils {

	private CJiraUtils() {

	}

	public static CJiraTokenGenerationRecord getJiraTokenGenerationLink(final CJiraAuthenticationConfigurator jiraAuthenticationConfigurator) throws CJiraException {

		JIRAOAuthClient.setConsumerKey(jiraAuthenticationConfigurator.getConsumerKey());
		JIRAOAuthClient.setConsumerPrivateKey(jiraAuthenticationConfigurator.getConsumerPrivateKey());

		ArrayList<String> arguments = Lists.newArrayList(Command.REQUEST_TOKEN.getName(), jiraAuthenticationConfigurator.getBaseUrl());
		String[] array = JIRAOAuthClient.jiraOAuthClient(arguments);
		String token = array[0];
		String secret = array[1];
		
		return new CJiraTokenGenerationRecord(jiraAuthenticationConfigurator.getBaseUrl() + "/plugins/servlet/oauth/authorize?oauth_token=" + token, null, token, secret);
		
	}
	
	public static String generateJiraAccessToken(final CJiraAuthenticationConfigurator jiraAuthenticationConfigurator, final CJiraTokenGenerationRecord record) throws CJiraException {
		ArrayList<String> arguments = Lists.newArrayList(Command.ACCESS_TOKEN.getName(), jiraAuthenticationConfigurator.getBaseUrl(), record.getToken(), record.getSecret(), record.getVerificationCode());
		return JIRAOAuthClient.jiraOAuthClient(arguments)[0];		
	}

	/**
	 * 
	 * pre zadane jira issue id (pkey-issueenum) vrati popis so summary a
	 * timetracking
	 * 
	 * @param accessToken
	 * @param jiraAuthenticationConfigurator
	 * @param pkey
	 * @param issuenum
	 * @return
	 * @throws CJiraException
	 */
	public static String makeJiraRequest(final String accessToken, final CJiraAuthenticationConfigurator jiraAuthenticationConfigurator, final String pkey, final String issuenum)
			throws CJiraException {

		JIRAOAuthClient.setConsumerKey(jiraAuthenticationConfigurator.getConsumerKey());
		JIRAOAuthClient.setConsumerPrivateKey(jiraAuthenticationConfigurator.getConsumerPrivateKey());

		ArrayList<String> arguments = Lists.newArrayList(Command.REQUEST.getName(), accessToken,
				jiraAuthenticationConfigurator.getBaseUrl() + "/rest/api/2/issue/" + pkey + "-" + issuenum + "?fields=summary,timetracking");
		return JIRAOAuthClient.jiraOAuthClient(arguments)[0];
	}

	/**
	 * 
	 * pre zadaneho assignee vrati vsetky jeho issue s resolutiondate v zadanom
	 * casovom rozsahu
	 * 
	 * @param accessToken
	 * @param jiraAuthenticationConfigurator
	 * @param assignee
	 * @param dateRange
	 * @return
	 * @throws CJiraException
	 */
	public static String makeJiraSearchRequest(final String accessToken, final CJiraAuthenticationConfigurator jiraAuthenticationConfigurator, final String assignee, final CDateRange dateRange,
			Integer startAt, Integer maxResults) throws CJiraException {

		JIRAOAuthClient.setConsumerKey(jiraAuthenticationConfigurator.getConsumerKey());
		JIRAOAuthClient.setConsumerPrivateKey(jiraAuthenticationConfigurator.getConsumerPrivateKey());

		/*
		 * JQL na výber názvov issues ktoré sú alebo boli priradené na aktuálne
		 * prihláseného používateľa a zároveň boli vytvorené pred koncom zadaného
		 * intervalu a nie sú v stavoch closed a resolved alebo sú v stave resolved od
		 * začiatku zadaného intervalu
		 * 
		 * https://jira.qbsw.sk/rest/api/2/search/?jql=(((assignee = ludovit.kovac) OR
		 * (assignee was in (ludovit.kovac))) AND (((created <= 2017-04-16) AND ((status
		 * not in (closed)) AND (status not in (resolved)))) OR (resolved >=
		 * 2017-04-10)))&fields=summary,timetracking&startAt=0&maxResults=50
		 */

		Calendar dateToPlusOneDay = dateRange.getDateTo();
		dateToPlusOneDay.add(Calendar.DAY_OF_YEAR, 1); // aby dotiahlo záznamy v
		// intervale jedného dňa

		String jql = "(((assignee%20%3D%20" + assignee + ")%20OR%20(assignee%20was%20in%20(" + assignee + ")))%20AND%20(((created%20%3C%3D%20" + CDateUtils.convertToDateStringForJira(dateToPlusOneDay)
				+ ")%20AND%20((status%20not%20in%20(closed))%20AND%20(status%20not%20in%20(resolved))))%20OR%20(resolved%20%3E%3D%20" + CDateUtils.convertToDateStringForJira(dateRange.getDateFrom())
				+ ")))&fields=summary,timetracking&startAt=" + startAt + "&maxResults=" + maxResults;

		ArrayList<String> arguments = Lists.newArrayList(Command.REQUEST.getName(), accessToken, jiraAuthenticationConfigurator.getBaseUrl() + "/rest/api/2/search/?jql=" + jql);
		return JIRAOAuthClient.jiraOAuthClient(arguments)[0];
	}

	/**
	 * @return - vráti worklog zadaného bugu/tasku
	 */
	public static String makeJiraWorklogRequest(final String accessToken, final CJiraAuthenticationConfigurator jiraAuthenticationConfigurator, final String issueKey) throws CJiraException {

		JIRAOAuthClient.setConsumerKey(jiraAuthenticationConfigurator.getConsumerKey());
		JIRAOAuthClient.setConsumerPrivateKey(jiraAuthenticationConfigurator.getConsumerPrivateKey());

		// https://jira.qbsw.sk/rest/api/2/issue/ISSP-3183/worklog

		ArrayList<String> arguments = Lists.newArrayList(Command.REQUEST.getName(), accessToken, jiraAuthenticationConfigurator.getBaseUrl() + "/rest/api/2/issue/" + issueKey + "/worklog");
		return JIRAOAuthClient.jiraOAuthClient(arguments)[0];
	}
}
