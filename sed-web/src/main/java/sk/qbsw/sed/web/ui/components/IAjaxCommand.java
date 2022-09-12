package sk.qbsw.sed.web.ui.components;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * interface for executing external code
 *
 * @author farkas.roman
 * @since 2.3.0
 * @version 2.3.0
 */
public interface IAjaxCommand extends Serializable {
	/**
	 *
	 * @param target input AjaxRequestTarget
	 */
	void execute(AjaxRequestTarget target);
}
