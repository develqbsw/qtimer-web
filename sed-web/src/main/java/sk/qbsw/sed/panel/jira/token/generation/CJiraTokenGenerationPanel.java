package sk.qbsw.sed.panel.jira.token.generation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.codelist.CJiraTokenGenerationRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.communication.service.IJiraTokenGenerationClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /jiraTokenGeneration SubPage title: Generovanie JIRA tokenu
 * 
 * Panel CJiraTokenGenerationPanel
 */
public class CJiraTokenGenerationPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4921616548940272344L;

	private Form<CJiraTokenGenerationRecord> form;
	
	private CompoundPropertyModel<CJiraTokenGenerationRecord> jiraTokenGenerationModel;
	
	private CJiraTokenGenerationRecord jiraTokenGenerationRecord;
	
	private AjaxFallbackButton generateBtn;

	@SpringBean
	private IJiraTokenGenerationClientService jiraTokenGenerationClientService;

	public CJiraTokenGenerationPanel(String id) {
		super(id);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		CFeedbackPanel errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);
		
		jiraTokenGenerationRecord = new CJiraTokenGenerationRecord();
		
		jiraTokenGenerationModel = new CompoundPropertyModel<>(jiraTokenGenerationRecord);

		form = new CJiraTokenGenerationForm<>("jiraTokenGenerationForm", jiraTokenGenerationModel);
		form.setOutputMarkupId(true);
		add(form);
		
		generateBtn = new AjaxFallbackButton("generateBtn", form) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form2) {

				CJiraTokenGenerationRecord model = form.getModelObject();
				String jiraAccessToken = null;
				
				try {
					jiraAccessToken = jiraTokenGenerationClientService.generateJiraAccessToken(model);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				}
				
				// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
				target.appendJavaScript("Main.runCustomCheck();FormElements.init();SedApp.init();");
				
				if (jiraAccessToken != null) {
					CJiraTokenGenerationPanel.this.success(CStringResourceReader.read("jira.token.successfully.generated"));
					generateBtn.setEnabled(false);
					target.add(generateBtn);
				}
				
				target.add(getFeedbackPanel());
				
				CLoggedUserRecord loggedUser = CSedSession.get().getUser();
				loggedUser.setJiraAccessToken(jiraAccessToken);
			}

			@Override
			public void onError(AjaxRequestTarget target, Form<?> form2) {
				super.onError(target, form);
				target.appendJavaScript("afterError();");
				target.add(form);
			}
		};

		generateBtn.setOutputMarkupId(true);
		add(generateBtn);

	}
}
