package sk.qbsw.sed.component.form;

import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.communication.service.IRequestClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.page.ANonAuthenticatedPage;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /approveRequest
 */
@MountPath(CApproveRequestPage.PATH_SEGMENT)
public class CApproveRequestPage extends ANonAuthenticatedPage {

	public static final String PATH_SEGMENT = "approveRequest";
	private static final long serialVersionUID = 1L;

	@SpringBean
	private IRequestClientService requestClientService;

	public CApproveRequestPage(PageParameters parameters) {
		super(parameters);

		StringValue locale = parameters.get("locale");
		StringValue requestId = parameters.get("requestId");
		StringValue requestCode = parameters.get("requestCode");

		CSedSession.get().setLocale(new Locale(locale.toString()));

		Boolean result = false;

		try {
			result = requestClientService.approveRequestFromEmail(requestId.toString(), requestCode.toString());
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		String resultKey = "page.approveRequest.content.confirmation.unsuccessful";

		if (result) {
			resultKey = "page.approveRequest.content.confirmation.successful";
		}

		Label confirmationResult = new Label("confirmationResult", Model.of(getString(resultKey)));

		if (!result) {
			confirmationResult.add(new AttributeModifier("style", "color: red;"));
		}

		add(confirmationResult);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
