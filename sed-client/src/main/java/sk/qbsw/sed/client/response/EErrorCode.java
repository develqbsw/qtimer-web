package sk.qbsw.sed.client.response;

/**
 * Enum to handle error code returned by API
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public enum EErrorCode {
	SYSTEM_ERROR(-1), BAD_PARAMS(-2), WRONG_USER_OR_PASSWORD(-3), USER_NOT_LOGGED(-4), NOT_AUTHORIZED(-5);

	private Integer code;

	private EErrorCode(Integer code) {
		this.code = code;

	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}
}
