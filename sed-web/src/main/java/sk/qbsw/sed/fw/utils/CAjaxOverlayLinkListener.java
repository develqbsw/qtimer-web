package sk.qbsw.sed.fw.utils;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;

public class CAjaxOverlayLinkListener extends AjaxCallListener {
	private static final long serialVersionUID = 1L;
	private Component panelToRender;

	public CAjaxOverlayLinkListener(Component overlayedPanel) {
		this.panelToRender = overlayedPanel;
	}

	@Override
	public CharSequence getBeforeHandler(Component component) {
		return "tableBeforeLoad('" + panelToRender.getMarkupId() + "')";
	}

	@Override
	public CharSequence getCompleteHandler(Component component) {
		return "tableAfterLoad('" + panelToRender.getMarkupId() + "')";
	}

	@Override
	public CharSequence getFailureHandler(Component component) {
		return "tableAfterLoad('" + panelToRender.getMarkupId() + "')";
	}
}
