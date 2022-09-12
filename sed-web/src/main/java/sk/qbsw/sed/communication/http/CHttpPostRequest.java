package sk.qbsw.sed.communication.http;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import sk.qbsw.sed.communication.model.CInvokerInfo;

public class CHttpPostRequest extends AHttpApiRequest {

	public CHttpPostRequest(CInvokerInfo info) {
		super(info);
	}

	@Override
	public Map<String, String> getHeaders() {
		return CRequestModificator.modificate(super.getHeaders(), info);
	}

	@Override
	public String makeOneCall(String url, ContentType contentType, String entityInJSon) throws IOException {
		CloseableHttpClient httpClient = CRequestModificator.createClient();

		HttpPost postRequest = new HttpPost(url);
		postRequest.setConfig(getRequestConfig());
		postRequest.addHeader("accept", contentType.getMimeType());

		if (getHeaders() != null) {
			for (Entry<String, String> headerItem : getHeaders().entrySet()) {
				postRequest.addHeader(headerItem.getKey(), headerItem.getValue());
			}
		}

		if (entityInJSon != null) {
			StringEntity input = new StringEntity(entityInJSon, contentType.getCharset().name());
			input.setContentType(contentType.toString());
			postRequest.setEntity(input);
		}

		HttpResponse response = execute(httpClient, postRequest);

		String content = getEntityContent(response);

		// 2XX response is correct
		int responseCode = response.getStatusLine().getStatusCode();
		
		if (responseCode < 200 || responseCode > 299) {
			throw new CApiHttpException("Failed : HTTP error code:" + response.getStatusLine().getStatusCode(), null, response.getStatusLine().getStatusCode(), content);
		}

		httpClient.close();

		return content;
	}
}
