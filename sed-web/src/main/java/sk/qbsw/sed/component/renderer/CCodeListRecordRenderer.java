package sk.qbsw.sed.component.renderer;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.ui.localization.IServerClientLocalizationKeys;
import sk.qbsw.sed.fw.component.renderer.CChoiceRenderer;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CCodeListRecordRenderer extends CChoiceRenderer<CCodeListRecord> {
	private static final long serialVersionUID = 1L;

	@Override
	public String getIdValue(CCodeListRecord object, int index) {
		if (object.getId() == null) {
			return "";
		}
		return object.getId().toString();
	}

	@Override
	public Object getDisplayValue(CCodeListRecord object) {
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
}
