package sk.qbsw.sed.fw.page;

/**
 * Mode for detail page
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public enum ECRUMode {
	READ("detail"), UPDATE("update"), CREATE("new"),;

	private String code;

	private ECRUMode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
