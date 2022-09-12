package sk.qbsw.sed.communication.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.communication.model.CInvokerInfo;

/**
 * 
 * @author Peter Bozik
 * @version 1.2.0
 * @since 1.1.0
 */
public class CRequestModificator {
	
	private CRequestModificator() {

	}

	public static Map<String, String> modificate(Map<String, String> headers, CInvokerInfo info) {
		if (headers == null) {
			headers = new HashMap<>();
		}
		
		if (info != null && StringUtils.isNotBlank(info.getAuthToken())) {
			headers.put("sed_security_token", info.getAuthToken());
		}
		
		headers.put("Cookie", "JSESSIONID=04F25C1841B6306E21F1CA6E378F7DDD");
		return headers;
	}

	/**
	 * Create client for both https and http connections
	 * 
	 * @return
	 */
	public static CloseableHttpClient createClient() {
		return HttpClientBuilder.create().build();
	}

	public static GsonBuilder createBuilder(GsonBuilder builder) {
		if (builder == null) {
			builder = new GsonBuilder();
		}
		builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		return builder;
	}
}
