package sk.qbsw.sed.framework.report.model;

import java.io.Serializable;

public class CCellDefinition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6202861528157398340L;

	private CCellTypeEnum cellType;

	private CCellDefinition(final CCellTypeEnum cellType) {
		super();
		this.cellType = cellType;
	}

	public static CCellDefinition getInstance(final CCellTypeEnum cellType) {
		return new CCellDefinition(cellType);
	}

	public CCellTypeEnum getCellType() {
		return this.cellType;
	}

	public void setCellType(final CCellTypeEnum cellType) {
		this.cellType = cellType;
	}
}
