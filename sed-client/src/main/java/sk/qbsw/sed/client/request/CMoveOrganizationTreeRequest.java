package sk.qbsw.sed.client.request;

import java.util.Date;

public class CMoveOrganizationTreeRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long treeNodeFrom;

	private Long treeNodeTo;

	private String mode;

	private Date timestamp;

	public CMoveOrganizationTreeRequest() {
		super();
	}

	public CMoveOrganizationTreeRequest(Long treeNodeFrom, Long treeNodeTo, String mode, Date timestamp) {
		super();
		this.treeNodeFrom = treeNodeFrom;
		this.treeNodeTo = treeNodeTo;
		this.mode = mode;
		this.timestamp = timestamp;
	}

	public Long getTreeNodeFrom() {
		return treeNodeFrom;
	}

	public void setTreeNodeFrom(Long treeNodeFrom) {
		this.treeNodeFrom = treeNodeFrom;
	}

	public Long getTreeNodeTo() {
		return treeNodeTo;
	}

	public void setTreeNodeTo(Long treeNodeTo) {
		this.treeNodeTo = treeNodeTo;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
