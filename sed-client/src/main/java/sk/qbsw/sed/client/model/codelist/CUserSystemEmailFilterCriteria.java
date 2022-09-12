package sk.qbsw.sed.client.model.codelist;

@SuppressWarnings("serial")
public class CUserSystemEmailFilterCriteria implements IUserSystemEmailFilterCriteria {

	private String name;
	private Boolean selected;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
