package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;

public class CLoadTreeByClientResponseContent extends AResponseContent {

	private static final long serialVersionUID = 1L;

	private List<CViewOrganizationTreeNodeRecord> list;

	public List<CViewOrganizationTreeNodeRecord> getList() {
		return list;
	}

	public void setList(List<CViewOrganizationTreeNodeRecord> list) {
		this.list = list;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
