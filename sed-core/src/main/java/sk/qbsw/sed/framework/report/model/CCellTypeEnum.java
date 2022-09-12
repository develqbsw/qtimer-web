package sk.qbsw.sed.framework.report.model;

public enum CCellTypeEnum {

	TYPE_DATE(0), TYPE_TIME(1), TYPE_DURATION(2), TYPE_STRING(3), TYPE_LONG(4), TYPE_INTEGER(5), TYPE_PERCENT(6), TYPE_CUSTOM(99);

	private int type;

	private CCellTypeEnum(final int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}
}
