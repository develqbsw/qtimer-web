package sk.qbsw.sed.fw.component.form.input;

import org.apache.wicket.markup.html.form.PasswordTextField;

public class CPasswordTextField extends PasswordTextField {

	private static final long serialVersionUID = 1L;

	public CPasswordTextField(String id) {
		super(id);
	}

	/**
	 * we does not want trim
	 */
	@Override
	public String getInput() {
		String[] input = getInputAsArray();
		if (input == null || input.length == 0) {
			return null;
		} else {
			return input[0];
		}
	}
}
