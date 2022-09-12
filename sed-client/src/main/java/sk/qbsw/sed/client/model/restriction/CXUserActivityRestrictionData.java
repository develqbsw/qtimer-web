package sk.qbsw.sed.client.model.restriction;

import java.io.Serializable;

import sk.qbsw.sed.client.model.abstractclass.ADataForEditableColumn;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

@SuppressWarnings("serial")
public class CXUserActivityRestrictionData extends ADataForEditableColumn implements Serializable {

	private Long id;
	private String name;
	private Boolean assigned;

	public CXUserActivityRestrictionData() {
	}

	public CXUserActivityRestrictionData(CCodeListRecord rec, Boolean b) {
		this.id = rec.getId();
		this.name = rec.getName();
		this.assigned = b;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getAssigned() {
		return assigned;
	}

	public void setAssigned(Boolean assigned) {
		this.assigned = assigned;
	}

	@Override
	public Boolean isActive() {
		return getAssigned();
	}

	public CCodeListRecord convert() {
		CCodeListRecord retVal = new CCodeListRecord();
		retVal.setId(this.getId());
		retVal.setName(this.getName());
		return retVal;
	}
}
