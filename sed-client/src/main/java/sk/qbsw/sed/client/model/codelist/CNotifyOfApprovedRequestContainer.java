package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class CNotifyOfApprovedRequestContainer implements Serializable {
	private Long userRequestId;
	private List<CUserSystemEmailRecord> selectedUsers;

	public Long getUserRequestId() {
		return userRequestId;
	}

	public void setUserRequestId(Long userRequestId) {
		this.userRequestId = userRequestId;
	}

	public List<CUserSystemEmailRecord> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<CUserSystemEmailRecord> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}
}
