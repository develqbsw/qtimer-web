package sk.qbsw.sed.client.model.restriction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

/**
 * Data object for client server communication
 * 
 * @author rosenberg
 * @version 1.6.6.2
 * @since 1.6.6.2
 */
@SuppressWarnings("serial")
public class CRequestReasonListsData implements Serializable {
	
	private List<CCodeListRecord> sicknessReasonList;
	private List<CCodeListRecord> workbreakReasonList;
	private List<CCodeListRecord> homeofficeReasonList;

	public CRequestReasonListsData() {
		sicknessReasonList = new ArrayList<>();
		workbreakReasonList = new ArrayList<>();
		homeofficeReasonList = new ArrayList<>();
	}

	public List<CCodeListRecord> getSicknessReasonList() {
		return sicknessReasonList;
	}

	/**
	 * Appends list records to exists list records
	 * 
	 * @param sicknessReasonList
	 */
	public void setSicknessReasonList(List<CCodeListRecord> sicknessReasonList) {
		this.sicknessReasonList.addAll(sicknessReasonList);
	}

	public List<CCodeListRecord> getWorkbreakReasonList() {
		return workbreakReasonList;
	}

	public void setWorkbreakReasonList(List<CCodeListRecord> workbreakReasonList) {
		this.workbreakReasonList.addAll(workbreakReasonList);
	}

	public List<CCodeListRecord> getHomeofficeReasonList() {
		return homeofficeReasonList;
	}

	public void setHomeofficeReasonList(List<CCodeListRecord> homeofficeReasonList) {
		this.homeofficeReasonList.addAll(homeofficeReasonList);
	}
}
