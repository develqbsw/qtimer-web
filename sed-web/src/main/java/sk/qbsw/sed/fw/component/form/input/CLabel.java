package sk.qbsw.sed.fw.component.form.input;

import java.io.Serializable;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;

import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.converter.CConverterFactory;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CLabel extends Label {
	private static final long serialVersionUID = 1L;
	private final EDataType inputType;
	private final IModel model;

	public CLabel(String id, EDataType inputType) {
		super(id);
		this.inputType = inputType;
		this.model = null;
	}

	public CLabel(String id, IModel model, EDataType inputType) {
		super(id, model);
		this.inputType = inputType;
		this.model = model;
		initComp();
	}

	public CLabel(final String id, Serializable label, EDataType inputType) {
		this(id, new Model<Serializable>(label), inputType);
	}

	@SuppressWarnings("unchecked")
	protected void initComp() {
		// do nothing
	}

	public EDataType getEDataType() {
		return inputType;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IConverter getConverter(Class type) {
		IConverter converter = CConverterFactory.getConverter(type, inputType);
		if (converter != null) {
			return converter;
		}
		return super.getConverter(type);
	}

	public void setTooltip(String text) {
		// do nothing
	}

	public void setReadOnly() {
		// do nothing
	}
}
