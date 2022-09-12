package sk.qbsw.sed.fw.component;

/**
 * Data type of column and field
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public enum EDataType {
	
	DATE, // adds date picker and formats the value
	DATE_TIME, // adds date picker and formats the value
	DATE_TIME_FILTER_DATE, // adds date picker and formats the value into date_time format
	DATE_RANGE, // adds date range picker
	TEXT, // unaccent sql query
	TIME, // unaccent sql query
	NUMBER, //
	NUMBER_LONG, //
	NUMBER_FILTER, //
	BIG_DECIMAL, //
	BIG_DECIMAL3, //
	DOUBLE, //
	PERCENTAGE, //
	OBJECT, // object as whole entity (dropdown choice...)
	UNDEFINED, //
	CURRENCY_WITHOUT_SYMBOL, //
	EXCHANGE_RATE, //
	;

	static {
		DATE_TIME_FILTER_DATE.setDisplay(DATE_TIME);
		DATE_TIME_FILTER_DATE.setInput(DATE);
	}
	private EDataType display = null;
	private EDataType input = null;

	public EDataType getDisplay() {
		if (display == null) {
			return this;
		}
		return display;
	}

	private void setDisplay(EDataType display) {
		this.display = display;
	}

	public EDataType getInput() {
		if (input == null) {
			return this;
		}
		return input;
	}

	private void setInput(EDataType input) {
		this.input = input;
	}
}
