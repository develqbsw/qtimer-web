package sk.qbsw.sed.fw.exception;

import sk.qbsw.sed.client.response.CResponseBase;

public class CBussinessDataExceptionModel {
	private String description;
	private final String key;
	private final String serverCode;
	private final EClientErrorCode clientCode;

	public CBussinessDataExceptionModel(CResponseBase errorResponse) {
		if (errorResponse != null) {
			if (errorResponse.getErrorMessage() != null && !"".equals(errorResponse.getErrorMessage())) {
				this.serverCode = errorResponse.getErrorMessage();
			} else {
				this.serverCode = errorResponse.getError();
			}
		} else {
			this.serverCode = null;
			this.description = null;
		}
		this.key = null;
		this.clientCode = null;
	}

	public CBussinessDataExceptionModel(EClientErrorCode clientCode) {
		this.clientCode = clientCode;
		this.key = null;
		this.serverCode = null;
		this.description = null;
	}

	public String getDescription() {
		return description;
	}

	public String getServerCode() {
		return serverCode;
	}

	public EClientErrorCode getClientCode() {
		return clientCode;
	}
}
