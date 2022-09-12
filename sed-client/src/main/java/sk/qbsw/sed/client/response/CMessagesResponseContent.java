package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.message.CMessageRecord;

public class CMessagesResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CMessageRecord> messages;

	public List<CMessageRecord> getMessages() {
		return messages;
	}

	public void setMessages(List<CMessageRecord> messages) {
		this.messages = messages;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
