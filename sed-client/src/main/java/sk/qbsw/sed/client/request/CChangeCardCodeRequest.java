package sk.qbsw.sed.client.request;

public class CChangeCardCodeRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long userId;

	private String cardCode;

	public CChangeCardCodeRequest() {
		super();
	}

	public CChangeCardCodeRequest(Long userId, String cardCode) {
		super();
		this.userId = userId;
		this.cardCode = cardCode;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCardCode() {
		return cardCode;
	}

	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
