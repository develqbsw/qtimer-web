package sk.qbsw.sed.panel.home;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.message.CMessageRecord;
import sk.qbsw.sed.client.model.message.IMessageType;
import sk.qbsw.sed.communication.service.IMessageClientService;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /home SubPage title: Dashboard
 * 
 * MessagesPanel - panel správ (napr. upozornenie na potrebu potvrdenia výkazu)
 */
public class CMessagesPanel extends CPanel {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private IMessageClientService messageService;

	private List<String> messagesList;

	/**
	 * create new messages panel
	 */
	public CMessagesPanel(String id) {
		super(id);
		Locale locale = CSedSession.get().getLocale();

		ListView<String> messagesListView = new ListViewOfMessages(getMessages(locale));

		add(messagesListView);
	}

	private String formatMonth(Calendar cal, Locale locale) {
		DateFormat formatter = new SimpleDateFormat("MMMM", locale);
		return formatter.format(cal.getTime());
	}

	private class ListViewOfMessages extends ListView<String> {

		private static final long serialVersionUID = 1L;

		private ListViewOfMessages(List<String> messages) {
			super("messagesList", messages);
		}

		@Override
		protected void populateItem(ListItem<String> item) {
			final String message = (String) item.getModelObject();

			item.add(new Label("message", message));
		}
	}

	private List<String> getMessages(Locale locale) {
		messagesList = new ArrayList<>();

		try {
			List<CMessageRecord> messages = messageService.getMessages(locale);

			if (messages.isEmpty()) {
				CMessagesPanel.this.add(AttributeModifier.append("style", "display: none;"));
			}

			for (CMessageRecord message : messages) {
				if (IMessageType.GENERATE_MONTH_REPORT.equals(message.getMessageType())) {
					String monthString = formatMonth(message.getFrom(), locale);
					messagesList.add(MessageFormat.format(getString("messagesPanel.content.messageGenerateMonthReport"), monthString + " " + message.getFrom().get(Calendar.YEAR)));
				} else if (IMessageType.CONFIRM_TIMESTAMPS.equals(message.getMessageType())) {
					messagesList.add(getString("messagesPanel.content.messageConfirmTimestamps"));
				} else if (IMessageType.CONFIRM_SUBORDINATE_TIMESTAMPS.equals(message.getMessageType())) {
					messagesList.add(getString("messagesPanel.content.messageConfirmSubordinateTimestamps"));
				}
			}
		} catch (Exception e) {
			Logger.getLogger(CMessagesPanel.class).error(e);
			messagesList.add(getString("messagesPanel.content.noNewMessage"));
		}

		return messagesList;
	}

	public boolean hasMessages() {
		return !(messagesList == null || messagesList.isEmpty());
	}
}
