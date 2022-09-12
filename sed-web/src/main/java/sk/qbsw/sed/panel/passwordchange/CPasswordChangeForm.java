package sk.qbsw.sed.panel.passwordchange;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import sk.qbsw.sed.client.model.codelist.CPasswordChangeRecord;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CLabel;
import sk.qbsw.sed.fw.component.form.input.CPasswordTextField;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

public class CPasswordChangeForm<T extends CPasswordChangeRecord> extends CStatelessForm<CPasswordChangeRecord> {

	private static final long serialVersionUID = 1L;

	public CPasswordChangeForm(String id, final IModel<CPasswordChangeRecord> recordModel, Panel actualPanel, boolean isReceptionPassword) {
		super(id, recordModel);
		setOutputMarkupId(true);

		CTextField<String> nameField = new CTextField<>("name", EDataType.TEXT);
		nameField.setRequired(true);
		nameField.setEnabled(false);
		add(nameField);

		CTextField<String> loginField = new CTextField<>("login", EDataType.TEXT);
		loginField.setRequired(true);
		loginField.setEnabled(false);
		add(loginField);

		CLabel originalLabel = new CLabel("originalLabel", new Model<String>(), EDataType.TEXT);
		originalLabel.setOutputMarkupId(true);
		originalLabel.setDefaultModelObject(CStringResourceReader.read(isReceptionPassword ? "passwordchange.admin" : "passwordchange.original"));
		add(originalLabel);

		CPasswordTextField originalField = new CPasswordTextField("originalPwd");
		originalField.setRequired(true);
		add(originalField);

		CPasswordTextField newField = new CPasswordTextField("newPwd");
		newField.setRequired(true);
		add(newField);

		CPasswordTextField new2Field = new CPasswordTextField("newPwd2");
		new2Field.setRequired(true);
		add(new2Field);
	}
}
