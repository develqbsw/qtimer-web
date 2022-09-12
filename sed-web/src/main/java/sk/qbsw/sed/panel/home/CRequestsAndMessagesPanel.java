package sk.qbsw.sed.panel.home;

import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /home SubPage title: Dashboard
 * 
 * RequestsAndMessagesPanel obsahuje panely:
 * 
 * - Panel správ (napr. upozornenie na potrebu potvrdenia výkazu) 
 * - Panel tlačidiel - Panel Žiadosti
 */
public class CRequestsAndMessagesPanel extends CPanel {

	private static final long serialVersionUID = 1L;
	private boolean generateMessages = false;
	private CMessagesPanel messagesPanel;

	public CRequestsAndMessagesPanel(String id, CFeedbackPanel errorPanel, List<CViewOrganizationTreeNodeRecord> organizationTree, CTimerButtonsPanel timerButtonsPanel) {
		super(id);

		generateMessages = CSedSession.get().getUser().getClientInfo().getGenerateMessages();

		if (generateMessages) {
			messagesPanel = new CMessagesPanel("messagesPanel");
			add(messagesPanel);
		} else {
			add(new WebMarkupContainer("messagesPanel"));
		}

		add(timerButtonsPanel);

		add(new CRequestsPanel("requestsPanel", errorPanel, organizationTree) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				super.onBeforeRender();
				if (generateMessages && messagesPanel != null && messagesPanel.hasMessages()) {
					this.add(new AttributeAppender("class", new Model("withMessagesPanel"), " "));
				}
			}
		});
	}
}
