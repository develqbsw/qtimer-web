package sk.qbsw.sed.component.calendar;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import sk.qbsw.sed.component.converter.CCalendarConverter;

public class CCalendarField<Calendar> extends TextField<Calendar> {

	private static final long serialVersionUID = 1L;

	private CCalendarConverter calendarConverter = new CCalendarConverter();

	public CCalendarField(String id) {
		super(id);
	}

	public CCalendarField(String id, Class<Calendar> type) {
		super(id, type);
	}

	public CCalendarField(String id, IModel<Calendar> model) {
		super(id, model);
	}

	public CCalendarField(String id, IModel<Calendar> model, Class<Calendar> type) {
		super(id, model, type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C> IConverter<C> getConverter(java.lang.Class<C> type) {
		return (IConverter<C>) this.calendarConverter;
	}
}
