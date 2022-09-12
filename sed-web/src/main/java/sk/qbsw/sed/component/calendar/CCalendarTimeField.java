package sk.qbsw.sed.component.calendar;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import sk.qbsw.sed.component.converter.CCalendarWithTimeConverter;

public class CCalendarTimeField<C> extends TextField<C> {

	private static final long serialVersionUID = 1L;

	public CCalendarTimeField(String id) {
		super(id);
	}

	public CCalendarTimeField(String id, Class<C> type) {
		super(id, type);
	}

	public CCalendarTimeField(String id, IModel<C> model) {
		super(id, model);
	}

	public CCalendarTimeField(String id, IModel<C> model, Class<C> type) {
		super(id, model, type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Cal> IConverter<Cal> getConverter(final Class<Cal> type) {
		return (IConverter<Cal>) CCalendarWithTimeConverter.INSTANCE;
	}
}
