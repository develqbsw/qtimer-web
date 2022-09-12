package sk.qbsw.sed.fw.component.input;

import org.apache.wicket.Component;

import sk.qbsw.sed.fw.panel.IUnDisabled;

/**
 * Input border enabled also in Detail mode.
 * 
 * @author Ľubomír Grňo
 *
 */
public class CInputBorderEnabled extends CInputBorder implements IUnDisabled {

	public CInputBorderEnabled(String id, Component component) {
		super(id, component);
	}
}
