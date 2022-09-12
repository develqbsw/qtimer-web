package sk.qbsw.sed.fw.component;

import java.io.Serializable;

import org.apache.wicket.Page;
import org.apache.wicket.Session;

import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;

public interface IComponentContainer {
	public CFeedbackPanel getFeedbackPanel();

	public Page getPage();

	public Session getSession();

	public void error(Serializable message);
}
