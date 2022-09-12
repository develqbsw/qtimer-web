package sk.qbsw.sed.fw.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import sk.qbsw.sed.fw.model.CPanelInRowModel;

public abstract class ARowPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public ARowPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		List<CPanelInRowModel> contents = new ArrayList<>();
		addContents("fw_contentPanel", contents);
		ListView<CPanelInRowModel> listView = new ListView<CPanelInRowModel>("fw_row_contents", contents) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<CPanelInRowModel> item) {
				WebMarkupContainer panel = item.getModelObject().getPanel();
				panel.add(AttributeAppender.append("class", " col-lg-" + item.getModelObject().getWidth() + " "));
				item.add(panel);
			}
		};
		add(listView);
		this.setRenderBodyOnly(true);
	}

	public abstract void addContents(String id, List<CPanelInRowModel> contents);
}
