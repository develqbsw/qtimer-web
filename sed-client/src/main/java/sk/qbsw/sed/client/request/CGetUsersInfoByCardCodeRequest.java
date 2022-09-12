package sk.qbsw.sed.client.request;

import java.util.List;

public class CGetUsersInfoByCardCodeRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<String> cardCodes;

	public CGetUsersInfoByCardCodeRequest() {
		super();
	}

	public CGetUsersInfoByCardCodeRequest(List<String> cardCodes) {
		super();
		this.cardCodes = cardCodes;
	}

	public List<String> getCardCodes() {
		return cardCodes;
	}

	public void setCardCodes(List<String> cardCodes) {
		this.cardCodes = cardCodes;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
