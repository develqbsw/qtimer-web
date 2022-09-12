package sk.qbsw.sed.fw.exception;

public enum EClientErrorCode {

	CONNECTION_FALIED(1, "error.connection.failed"), NEEDS_TO_LOGIN(2, ""), NOT_IMPLEMENTED(3, ""), BAD_URL(4, "error.connection.bad_url");

	private Integer code;
	private String messageKey;

	private EClientErrorCode(Integer code, String messageKey) {
		this.code = code;
		this.messageKey = messageKey;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessageKey() {
		return messageKey;
	}
}
