package sk.qbsw.sed.server.util;

import java.util.Calendar;

import org.apache.log4j.Logger;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.common.utils.CDateRange;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CJiraException;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.jira.CJiraAuthenticationConfigurator;
import sk.qbsw.sed.server.jira.CJiraUtils;
import sk.qbsw.sed.server.model.domain.CTmpTimeSheetRecord;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.domain.ITimeSheetRecord;

public class CTimesheetUtils {

	private static final Integer MAX_RESULTS = 50;

	private static final String TIMETRACKING = "timetracking";
	
	private static final String SUMMARY = "summary";
	
	private static final String TIME_SPENT_SECONDS = "timeSpentSeconds";
	
	private CTimesheetUtils() {

	}

	// @Transactional(readOnly = false)
	public static void setIssueSummaryFromJira(ITimeSheetRecord timeSheetRecord, final CJiraAuthenticationConfigurator jira, final IUserDao userDao) throws CSecurityException {
		CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		if (loggedUser.getJiraAccessToken() != null) {
			try {
				setIssueSummaryFromJiraViaAccessToken(timeSheetRecord, loggedUser.getJiraAccessToken(), jira);
			} catch (CBusinessException e) {
				Logger.getLogger(CTimesheetUtils.class).info(e);
				// Jira access token je neplatny, vyprsala mu platnost alebo bol v jire zruseny zmazem ho z db
				CUser user = userDao.findById(loggedUser.getUserId());
				user.setJiraAccessToken(null);
				user.setAutoLoginToken(null);
				final Calendar changeTime = Calendar.getInstance();
				user.setChangeTime(changeTime);
				userDao.saveOrUpdate(user);
			}
		} else {
			Logger.getLogger(CTimesheetUtils.class).info("setIssueSummaryFromJira - jiraAccessToken is null for user: " + loggedUser.getSurname());
		}
	}

	private static void setIssueSummaryFromJiraViaAccessToken(ITimeSheetRecord timeSheetRecord, final String jiraAccessToken, final CJiraAuthenticationConfigurator jira) throws CBusinessException {
		String issueId = null;
		String phase = timeSheetRecord.getPhase();
		String note = timeSheetRecord.getNote();

		Logger.getLogger(CTimesheetUtils.class).info("setIssueSummaryFromJiraViaAccessToken phase: " + phase + ", note: " + note);

		// 1.) ak etapa_id má presne formát písmená pomlčka čísla (napr. MOK-1726) a
		// poznámka je prázdna, vtedy vyplniť poznámku hodnotou summary z jira orezané na max. 1000 znakov
		if (phase != null && phase.split("-").length == 2 && (note == null || "".equals(note))) {
			issueId = phase;
		} else if (note != null && note.split("-").length == 2) {
			// 2.) ak poznámka má presne formát písmená pomlčka čísla (napr. MOK-1726),
			// doplniť do poznámky dvojbodka medzera hodnota summary z jira orezané na max. 1000 znakov
			issueId = note;
		}

		if (issueId == null || issueId.split("-").length != 2) {
			return;
		}

		String summary;
		String pkey = issueId.split("-")[0].toUpperCase();
		String issuenum = issueId.split("-")[1];

		if (!issuenum.matches("[0-9]+")) {
			return;
		}

		String response = null;

		try {
			response = CJiraUtils.makeJiraRequest(jiraAccessToken, jira, pkey, issuenum);
		} catch (CJiraException e) {
			if ("net.oauth.OAuthProblemException: token_rejected".equals(e.getCause().toString())) {
				Logger.getLogger(CTimesheetUtils.class).info("Jira access token je neplatny, vyprsala mu platnost alebo bol v jire zruseny. " + e.getMessage());
				throw new CBusinessException("Jira access token je neplatny, vyprsala mu platnost alebo bol v jire zruseny.");
			}

			// nechceme vyhadzovat systemovku ak sa request nepodaril
			Logger.getLogger(CTimesheetUtils.class).info(e);
			return;
		}

		if (response != null) {
			/*
			 * example of response
			 * {"expand":"renderedFields,names,schema,transitions,operations,editmeta,changelog","id":"84809",
			 * "self":"https://jira.qbsw.sk/rest/api/2/issue/84809","key":"SED-500", 
			 * "fields":{"summary":"Prehľad časových značiek, Ziadosti - vyber zamestnancov",
			 * "timetracking":{"remainingEstimate":"0m","timeSpent":"2h","remainingEstimateSeconds":0,"timeSpentSeconds":7200}}}
			 */

			// SED-692 Prenos špeciálnych znakov z JIRA (", ')
			response = response.replaceAll("\\\\\"", "\"");

			if (response.contains(TIMETRACKING)) {
				summary = response.substring(response.indexOf(SUMMARY) + 10, response.indexOf(TIMETRACKING) - 3);
			} else {
				summary = response.substring(response.indexOf(SUMMARY) + 10, response.length() - 3);
			}

			Logger.getLogger(CTimesheetUtils.class).info("setIssueSummaryFromJira summary: " + summary);

			if (summary.length() > 1000) {
				summary = summary.substring(0, 989);
			}

			if (note == null) {
				timeSheetRecord.setPhase(phase.toUpperCase());
			}

			timeSheetRecord.setNote((note == null || "".equals(note)) ? summary : note.toUpperCase() + ": " + summary);

			if (timeSheetRecord instanceof CTmpTimeSheetRecord && response.indexOf(TIME_SPENT_SECONDS) != -1) {
				// toto ma zaujima len pri generovani casovych znaciek
				String timeSpentSeconds = response.substring(response.indexOf(TIME_SPENT_SECONDS) + 18, response.length() - 3);
				timeSheetRecord.setJiraTimeSpentSeconds(Long.valueOf(timeSpentSeconds));
			}
		}
	}

	/**
	 * 
	 * @return String[][] 0 - key, 1 - summary, 2 - timeSpentSeconds
	 */
	public static String[][] getJiraIssues(final CDateRange dateRange, final CJiraAuthenticationConfigurator jira, final IUserDao userDao) throws CSecurityException {
		CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		if (loggedUser.getJiraAccessToken() != null) {
			try {
				return getJiraIssues(loggedUser.getJiraAccessToken(), jira, loggedUser.getLogin(), dateRange);
			} catch (CBusinessException e) {
				Logger.getLogger(CTimesheetUtils.class).info(e);
				// Jira access token je neplatny, vyprsala mu platnost alebo bol v jire zruseny zmazem ho z db
				CUser user = userDao.findById(loggedUser.getUserId());
				user.setJiraAccessToken(null);
				user.setAutoLoginToken(null);
				final Calendar changeTime = Calendar.getInstance();
				user.setChangeTime(changeTime);
				userDao.saveOrUpdate(user);
			}
		} else {
			Logger.getLogger(CTimesheetUtils.class).info("getJiraIssues - jiraAccessToken is null for user: " + loggedUser.getSurname());
		}

		return null;
	}

	private static String[][] getJiraIssues(final String jiraAccessToken, final CJiraAuthenticationConfigurator jira, final String assignee, final CDateRange dateRange) throws CBusinessException {

		String response = null;
		String[][] retVal = null;

		try {
			response = CJiraUtils.makeJiraSearchRequest(jiraAccessToken, jira, assignee, dateRange, 0, MAX_RESULTS);
		} catch (CJiraException e) {
			if ("net.oauth.OAuthProblemException: token_rejected".equals(e.getCause().toString())) {
				Logger.getLogger(CTimesheetUtils.class).info("Jira access token je neplatny, vyprsala mu platnost alebo bol v jire zruseny. " + e.getMessage());
				throw new CBusinessException("Jira access token je neplatny, vyprsala mu platnost alebo bol v jire zruseny.");
			}

			// nechceme vyhadzovat systemovku ak sa request nepodaril
			Logger.getLogger(CTimesheetUtils.class).info(e);
			return null;
		}

		if (response != null) {
			/*
			 * example of response
			 * {"expand":"schema,names","startAt":0,"maxResults":50,"total":3,"issues":
			 * [{"expand":"editmeta,renderedFields,transitions,changelog,operations","id":
			 * "87862","self":"https://jira.qbsw.sk/rest/api/2/issue/87862","key":"SED-640",
			 * "fields":{"summary":"Casovac- ak viackrat kliknem na zacat nocu alebo zmenit
			 * existujucu => systemova chyba","timetracking":{"remainingEstimate":"0m",
			 * "timeSpent":"4h","remainingEstimateSeconds":0,"timeSpentSeconds ":14400}}},
			 * {"expand":"editmeta,renderedFields,transitions,changelog,operations","id":
			 * "87845","self":"https://jira.qbsw.sk/rest/api/2/issue/87845","key":"SED-637",
			 * "fields":{"summary":"Vykaz prace - pri vyhladavani textu nebrat do uvahy
			 * diakritiku","timetracking":{}}},
			 * {"expand":"editmeta,renderedFields,transitions,changelog,operations","id":
			 * "94311","self":"https://jira.qbsw.sk/rest/api/2/issue/94311","key":
			 * "ISSP-12524","fields":{"summary":"(Pripomienka VU 114) Kontrola poľa
			 * \"Predpokladaný dátum zmeny\" pre \"Žiadosť o zmenu
			 * účtu\"","timetracking":{}}}]}
			 */

			retVal = parseJiraSearchResponse(response);
		}

		if (response != null) {
			Integer[] resultsCount = findResultsCountFromResponse(response);
			// 0 - startAt, 1 - maxResults, 2 - total

			// maxResults < total
			if (resultsCount != null && (resultsCount[1] < resultsCount[2])) {

				// (startAt + maxResults) < total
				while ((resultsCount[0] + resultsCount[1]) < resultsCount[2]) {

					String nextResponse = null;
					// startAt = startAt + maxResults
					resultsCount[0] = resultsCount[0] + resultsCount[1];

					try {
						nextResponse = CJiraUtils.makeJiraSearchRequest(jiraAccessToken, jira, assignee, dateRange, resultsCount[0], resultsCount[1]);
					} catch (CJiraException e) {
						if ("net.oauth.OAuthProblemException: token_rejected".equals(e.getCause().toString())) {
							Logger.getLogger(CTimesheetUtils.class).info("Jira access token je neplatny, vyprsala mu platnost alebo bol v jire zruseny. " + e.getMessage());
							throw new CBusinessException("Jira access token je neplatny, vyprsala mu platnost alebo bol v jire zruseny.");
						}

						// nechceme vyhadzovat systemovku ak sa request
						// nepodaril
						Logger.getLogger(CTimesheetUtils.class).info(e);
					}

					if (response != null) {
						/*
						 * example of response
						 * {"expand":"schema,names","startAt":0,"maxResults":50,"total":3,"issues":
						 * [{"expand":"editmeta,renderedFields,transitions,changelog,operations","id":
						 * "87862","self":"https://jira.qbsw.sk/rest/api/2/issue/87862","key":"SED-640",
						 * "fields":{"summary":"Casovac- ak viackrat kliknem na zacat nocu alebo zmenit
						 * existujucu => systemova chyba","timetracking":{"remainingEstimate":"0m",
						 * "timeSpent":"4h","remainingEstimateSeconds":0,"timeSpentSeconds ":14400}}},
						 * {"expand":"editmeta,renderedFields,transitions,changelog,operations","id":
						 * "87845","self":"https://jira.qbsw.sk/rest/api/2/issue/87845","key":"SED-637",
						 * "fields":{"summary":"Vykaz prace - pri vyhladavani textu nebrat do uvahy
						 * diakritiku","timetracking":{}}},
						 * {"expand":"editmeta,renderedFields,transitions,changelog,operations","id":
						 * "94311","self":"https://jira.qbsw.sk/rest/api/2/issue/94311","key":
						 * "ISSP-12524","fields":{"summary":"(Pripomienka VU 114) Kontrola poľa
						 * \"Predpokladaný dátum zmeny\" pre \"Žiadosť o zmenu účtu\"","timetracking":{}}}]}
						 */
						if (concatenateTwoArrays(retVal, parseJiraSearchResponse(nextResponse)) != null) {
							retVal = concatenateTwoArrays(retVal, parseJiraSearchResponse(nextResponse));
						}
					}
				}
			}
		}

		return retVal;
	}

	/**
	 * 
	 * @param response
	 * @return String[][] 0 - key, 1 - summary, 2 - timeSpentSeconds
	 */
	private static String[][] parseJiraSearchResponse(String response) {
		Logger.getLogger(CTimesheetUtils.class).info("parseJiraSearchResponse response: " + response);

		response = response.substring(response.indexOf('['));

		if (!response.contains("key")) {
			// nic nenaslo
			return null;
		}

		String[] issues = response.split("}}},");

		int numberOfIssues = issues.length;

		String[][] retVal = new String[numberOfIssues][];

		for (int i = 0; i < retVal.length; i++) {
			String issue = issues[i];
			if (issue.contains("key") && issue.contains("fields") && issue.contains(SUMMARY) && issue.contains(TIMETRACKING)) {
				String[] issueParsed = new String[3]; // 0 - key, 1 - summary, 2 - timeSpentSeconds
				String key = issue.substring(issue.indexOf("key") + 6, issue.indexOf("fields") - 3);
				String summary = issue.substring(issue.indexOf(SUMMARY) + 10, issue.indexOf(TIMETRACKING) - 3);

				if (summary.length() > 1000) {
					summary = summary.substring(0, 989);
				}

				issueParsed[0] = key;
				issueParsed[1] = summary;

				if (issue.indexOf(TIME_SPENT_SECONDS) != -1) {

					int indexOfTimeSpentSeconds = issue.indexOf(TIME_SPENT_SECONDS);
					String timeSpentSeconds = issue.substring(indexOfTimeSpentSeconds + 18);
					if (timeSpentSeconds.indexOf('}') != -1) {
						timeSpentSeconds = timeSpentSeconds.substring(0, timeSpentSeconds.indexOf('}'));
					}
					issueParsed[2] = timeSpentSeconds;
				}

				retVal[i] = issueParsed;
			} else {
				/*
				 * ak neboli vrátené potrebné polia napr. timetracking, padla by chyba pri
				 * volaní metódy indexOf takýto záznam nepridám
				 */
			}
		}

		// spočítam si issues, ktoré majú popis
		int countOfIssues = 0;
		for (int i = 0; i < retVal.length; i++) {
			if (retVal[i] != null) {
				countOfIssues++;
			}
		}

		// vytvorím si novú návratovú hodnotu, do ktorej vložím len platné issues
		String[][] retValNew = new String[countOfIssues][];

		// index pre pole retValNew
		int index = 0;

		for (int i = 0; i < retVal.length; i++) {
			if (retVal[i] != null) {
				retValNew[index] = retVal[i];
				index++;
			}
		}

		return retValNew;
	}

	/*
	 * return Integer[0] - startAt, Integer[1] - maxResults, Integer[2] - total
	 * metóda na získanie startAt, maxResults a total z response
	 */
	private static Integer[] findResultsCountFromResponse(String response) {

		Logger.getLogger(CTimesheetUtils.class).info("findResultsCountFromResponse response: " + response);

		String startAtStr;
		String maxResultsStr;
		String totalStr;
		Integer[] retVal = new Integer[3];

		if (response.contains("startAt") && response.contains("maxResults") && response.contains("total") && response.contains("issues")) {
			startAtStr = response.substring(response.indexOf("startAt") + 9, response.indexOf("maxResults") - 2);
			maxResultsStr = response.substring(response.indexOf("maxResults") + 12, response.indexOf("total") - 2);
			totalStr = response.substring(response.indexOf("total") + 7, response.indexOf("issues") - 2);
		} else {
			return null;
		}

		Integer startAt;
		Integer maxResults;
		Integer total;

		try {

			startAt = Integer.parseInt(startAtStr);
			maxResults = Integer.parseInt(maxResultsStr);
			total = Integer.parseInt(totalStr);

		} catch (Exception e) {
			return null;
		}

		retVal[0] = startAt;
		retVal[1] = maxResults;
		retVal[2] = total;

		return retVal;
	}

	/*
	 * metóda na spojenie dvoch dvojrozmerných polí
	 */
	private static String[][] concatenateTwoArrays(String[][] arr1, String[][] arr2) {

		String[][] retVal = new String[arr1.length + arr2.length][];

		System.arraycopy(arr1, 0, retVal, 0, arr1.length);
		System.arraycopy(arr2, 0, retVal, arr1.length, arr2.length);

		return retVal;
	}
}
