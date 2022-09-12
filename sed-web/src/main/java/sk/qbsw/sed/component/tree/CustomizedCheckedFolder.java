package sk.qbsw.sed.component.tree;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.CheckedFolder;
import org.apache.wicket.model.IModel;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;

public class CustomizedCheckedFolder<T> extends CheckedFolder<T> {

	private static final long serialVersionUID = 1L;

	public CustomizedCheckedFolder(String id, AbstractTree<T> tree, IModel<T> model) {
		super(id, tree, model);
	}

	@Override
	protected MarkupContainer newLinkComponent(String id, IModel<T> model) {
		MarkupContainer link = super.newLinkComponent(id, model);
		if (!((CViewOrganizationTreeNodeRecord) model.getObject()).getIsValid()) {
			link.add(AttributeModifier.append("style", "opacity: 0.5;"));
		}
		return link;
	}
}