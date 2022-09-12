package sk.qbsw.sed.web.ui.components;

public class CPageMeta {

	private String titleKey;

	public CPageMeta(String title) {
		super();
		this.titleKey = title;
	}

	/**
	 * @return the titleKey
	 */
	public String getTitleKey() {
		return titleKey;
	}

	/**
	 * @param titleKey the titleKey to set
	 */
	public void setTitleKey(String title) {
		this.titleKey = title;
	}
}
