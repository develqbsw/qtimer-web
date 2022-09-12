package sk.qbsw.sed.component.jstree;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface ITreeNodeSelected<T> extends Serializable {
	public void selected(AjaxRequestTarget target, T object);
}
