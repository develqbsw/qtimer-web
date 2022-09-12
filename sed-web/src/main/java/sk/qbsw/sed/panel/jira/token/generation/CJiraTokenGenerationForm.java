package sk.qbsw.sed.panel.jira.token.generation;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.codelist.CJiraTokenGenerationRecord;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.communication.service.IJiraTokenGenerationClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;

public class CJiraTokenGenerationForm<T extends CRequestRecord> extends CStatelessForm<CJiraTokenGenerationRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2702828519112644276L;

	@SpringBean
	private IJiraTokenGenerationClientService jiraTokenGenerationClientService;
	
	public CJiraTokenGenerationForm(String id, final IModel<CJiraTokenGenerationRecord> jiraTokenGenerationRecord) {

		super(id, jiraTokenGenerationRecord);

		CJiraTokenGenerationRecord record = null;

		try {
			record = this.jiraTokenGenerationClientService.getJiraTokenGenerationLink();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		ExternalLink jiraLink = new ExternalLink("jiraLink", record != null ? record.getJiraLink() : "", "JIRA link") {

			private static final long serialVersionUID = 4593823932392110103L;

			@Override
			protected void onComponentTag(ComponentTag componentTag) {
				super.onComponentTag(componentTag);
				componentTag.put("target", "_blank");
			}

		};

		add(jiraLink);
	
		final CTextField<String> verificationCodeField = new CTextField<>("verificationCode", EDataType.TEXT);
		verificationCodeField.setOutputMarkupId(true);
		add(verificationCodeField);
		
		final CTextField<String> tokenField = new CTextField<>("token", EDataType.TEXT);
		tokenField.setOutputMarkupId(true);
		add(tokenField);
		
		final CTextField<String> secretField = new CTextField<>("secret", EDataType.TEXT);
		secretField.setOutputMarkupId(true);
		add(secretField);
		
		// nastavím poliam hodnotu
		tokenField.getModel().setObject(record != null ? record.getToken() : "");
		secretField.getModel().setObject(record != null ? record.getSecret() : "");
		
		// skryjem polia
		tokenField.setVisible(false);
		secretField.setVisible(false);
		
	}
}
