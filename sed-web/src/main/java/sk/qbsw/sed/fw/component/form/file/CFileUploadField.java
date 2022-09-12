package sk.qbsw.sed.fw.component.form.file;

import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import sk.qbsw.sed.fw.component.form.input.IComponentLabel;

public class CFileUploadField extends FileUploadField implements IComponentLabel {
	private static final long serialVersionUID = 1L;
	private IModel<String> label;

	public CFileUploadField(String id, IModel<List<FileUpload>> model) {
		super(id, model);
	}

	@Override
	public String getComponentLabel() {
		if (label != null) {
			return label.getObject();
		} else {
			return "";
		}
	}

	@Override
	public void setComponentLabelKey(String labelId) {
		label = new StringResourceModel(labelId, this, null);
	}

	@Override
	public void setComponentLabelKey(IModel<String> model) {
		this.label = model;
	}
}
