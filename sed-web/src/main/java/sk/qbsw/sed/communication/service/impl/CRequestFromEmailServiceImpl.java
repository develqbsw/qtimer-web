package sk.qbsw.sed.communication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import sk.qbsw.sed.client.request.CApproveRequestFromEmailRequest;
import sk.qbsw.sed.client.request.CRejectRequestFromEmailRequest;
import sk.qbsw.sed.client.response.CBooleanResponse;
import sk.qbsw.sed.client.response.CBooleanResponseContent;
import sk.qbsw.sed.communication.service.IRequestFromEmailService;
import sk.qbsw.sed.model.CSystemSettings;

@Service(value = "requestFromEmailService")
public class CRequestFromEmailServiceImpl implements IRequestFromEmailService {

	@Autowired
	private CSystemSettings settings;

	@Override
	public Boolean approveRequestFromEmail(String requestId, String requestCode) {

		CApproveRequestFromEmailRequest request = new CApproveRequestFromEmailRequest();
		request.setRequestCode(requestCode);
		request.setRequestId(requestId);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Content-Type", "application/json");
		HttpEntity<Object> entity = new HttpEntity<>(request, headers);
		RestTemplate rest = new RestTemplate();

		String apiUrl = settings.getApiUrl();

		ResponseEntity<CBooleanResponse> a = rest.exchange(apiUrl + "/request/approveRequestFromEmail", HttpMethod.POST, entity, CBooleanResponse.class);

		return ((CBooleanResponseContent) a.getBody().getContent()).getValue();
	}

	@Override
	public Boolean rejectRequestFromEmail(String requestId, String requestCode) {

		CRejectRequestFromEmailRequest request = new CRejectRequestFromEmailRequest();
		request.setRequestCode(requestCode);
		request.setRequestId(requestId);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Content-Type", "application/json");
		HttpEntity<Object> entity = new HttpEntity<>(request, headers);
		RestTemplate rest = new RestTemplate();

		String apiUrl = settings.getApiUrl();

		ResponseEntity<CBooleanResponse> a = rest.exchange(apiUrl + "/request/rejectRequestFromEmail", HttpMethod.POST, entity, CBooleanResponse.class);

		return ((CBooleanResponseContent) a.getBody().getContent()).getValue();
	}
}
