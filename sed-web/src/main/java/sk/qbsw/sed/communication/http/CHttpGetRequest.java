package sk.qbsw.sed.communication.http;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;

import sk.qbsw.sed.communication.model.CInvokerInfo;

public class CHttpGetRequest extends AHttpApiRequest {
	private static final String CONTENT_PARAMETER = "payload";

	public CHttpGetRequest(CInvokerInfo info) {
		super(info);
	}

	@Override
	public Map<String, String> getHeaders() {
		return CRequestModificator.modificate(super.getHeaders(), info);
	}

	@Override
	protected String makeOneCall(String url, ContentType contentType, String entity) throws IOException {
		CloseableHttpClient httpClient = CRequestModificator.createClient();

		String fullURL = url;
		if (entity != null) {
			fullURL = url + "?" + CONTENT_PARAMETER + "=" + URLEncoder.encode(entity, "UTF-8");
		}

		HttpGet getRequest = new HttpGet(fullURL);
		getRequest.setConfig(getRequestConfig());
		getRequest.addHeader("accept", contentType.getMimeType());

		if (getHeaders() != null) {
			for (Entry<String, String> headerItem : getHeaders().entrySet()) {
				getRequest.addHeader(headerItem.getKey(), headerItem.getValue());
			}
		}

		HttpResponse response = execute(httpClient, getRequest);

		String content = getEntityContent(response);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new CApiHttpException("Failed : HTTP error code:" + response.getStatusLine().getStatusCode(), null, response.getStatusLine().getStatusCode(), content);
		}

		httpClient.close();

		return content;
	}
}
