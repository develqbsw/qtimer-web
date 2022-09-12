package sk.qbsw.sed.server.jira;

import java.util.ArrayList;

import sk.qbsw.sed.server.exception.CJiraException;

/**
 * @since v1.0
 */
public class JIRAOAuthClient {
	
	private static final String CALLBACK_URI = "";
	private static String consumerKey;
	private static String consumerPrivateKey;

	public enum Command {
		REQUEST_TOKEN("requestToken"), ACCESS_TOKEN("accessToken"), REQUEST("request");

		private String name;

		Command(final String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static String[] jiraOAuthClient(ArrayList<String> arguments) throws CJiraException {
		String[] retVal = new String[2];

		if (arguments.isEmpty()) {
			throw new IllegalArgumentException("No command specified. Use one of " + getCommandNames());
		}
		String action = arguments.get(0);
		if (Command.REQUEST_TOKEN.getName().equals(action)) {
			String baseUrl = arguments.get(1);
			String callBack = "oob";
			if (arguments.size() == 3) {
				callBack = arguments.get(2);
			}
			AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(consumerKey, consumerPrivateKey, baseUrl, callBack);
			// STEP 1: Get request token
			TokenSecretVerifierHolder requestToken = jiraoAuthClient.getRequestToken();
			retVal[0] = requestToken.token;
			retVal[1] = requestToken.secret;
		} else if (Command.ACCESS_TOKEN.getName().equals(action)) {
			String baseUrl = arguments.get(1);
			AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(consumerKey, consumerPrivateKey, baseUrl, CALLBACK_URI);
			String requestToken = arguments.get(2);
			String tokenSecret = arguments.get(3);
			String verifier = arguments.get(4);
			String accessToken = jiraoAuthClient.swapRequestTokenForAccessToken(requestToken, tokenSecret, verifier);
			retVal[0] = accessToken;
		} else if (Command.REQUEST.getName().equals(action)) {
			AtlassianOAuthClient jiraoAuthClient = new AtlassianOAuthClient(consumerKey, consumerPrivateKey, null, CALLBACK_URI);
			String accessToken = arguments.get(1);
			String url = arguments.get(2);
			String responseAsString = jiraoAuthClient.makeAuthenticatedRequest(url, accessToken);
			retVal[0] = responseAsString;
		} else {
			System.out.println("Command " + action + " not supported. Only " + getCommandNames() + " are supported.");
		}

		return retVal;
	}

	private static String getCommandNames() {
		String names = "";
		for (Command value : Command.values()) {
			names += value.getName() + " ";
		}
		return names;
	}

	public static String getConsumerKey() {
		return consumerKey;
	}

	public static void setConsumerKey(String consumerKey) {
		JIRAOAuthClient.consumerKey = consumerKey;
	}

	public static String getConsumerPrivateKey() {
		return consumerPrivateKey;
	}

	public static void setConsumerPrivateKey(String consumerPrivateKey) {
		JIRAOAuthClient.consumerPrivateKey = consumerPrivateKey;
	}
}
