package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class CUserSystemEmailContainer implements Serializable {

	private List<CUserSystemEmailRecord> selectedUsers;

	public List<CUserSystemEmailRecord> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<CUserSystemEmailRecord> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}
}
