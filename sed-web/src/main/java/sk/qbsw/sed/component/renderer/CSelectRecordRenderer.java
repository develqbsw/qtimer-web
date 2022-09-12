package sk.qbsw.sed.component.renderer;

import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.ui.localization.IServerClientLocalizationKeys;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

public class CSelectRecordRenderer implements IOptionRenderer<CCodeListRecord> {
	private static final long serialVersionUID = 1L;

	@Override
	public String getDisplayValue(CCodeListRecord object) {
		if (object.getName() != null) {
			String displayValue = object.getName();
			final int serverLabelsKeyIndex = displayValue.indexOf(IServerClientLocalizationKeys.LABEL_KEY);

			if (serverLabelsKeyIndex != -1) {
				displayValue = CStringResourceReader.read(displayValue.substring(IServerClientLocalizationKeys.LABEL_KEY.length(), displayValue.length()));
				return displayValue;
			}

			return object.getName();
		} else {
			return "";
		}
	}

	@Override
	public IModel<CCodeListRecord> getModel(CCodeListRecord object) {
		return Model.of(object);
	}
}
