package sk.qbsw.sed.fw.component.renderer;

public abstract class CBooleanChoiceRenderer extends CChoiceRenderer<Boolean> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(Boolean o) {
		return getValue(o);
	}

	protected abstract String getValue(Boolean o);
}
