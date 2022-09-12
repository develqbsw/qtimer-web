package sk.qbsw.sed.fw.component.form.pallete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import sk.qbsw.sed.fw.utils.CDisplayConverterUtils;
import sk.qbsw.sed.fw.utils.CollectionUtils;

/**
 * Pallete panel with own model. There is an issue with compound property model
 * and pallete, which tries to set choices property
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CPalletePanel<T> extends Panel {

	private static final long serialVersionUID = 1L;
	private final boolean readOnly;
	private final ListModel<T> model;
	private final IModel<? extends Collection<? extends T>> choicesModel;
	private final IChoiceRenderer<T> choiceRenderer;
	private final int rows;
	private final boolean allowOrder;
	private CPalette<T> palette;
	private final List<IPaletteChange> changeListeners = new ArrayList<>();
	private IModel<? extends Collection<? extends T>> choicesAllModel;

	public CPalletePanel(String id, boolean readOnly, ListModel<T> model, IModel<? extends Collection<? extends T>> choicesModel, IChoiceRenderer<T> choiceRenderer, int rows, boolean allowOrder) {
		this(id, readOnly, model, choicesModel, choicesModel, choiceRenderer, rows, allowOrder);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CPalletePanel(String id, boolean readOnly, ListModel<T> model, IModel<? extends Collection<? extends T>> choicesModel, IModel<? extends Collection<? extends T>> choicesAllModel,
			IChoiceRenderer<T> choiceRenderer, int rows, boolean allowOrder) {
		super(id, new CompoundPropertyModel<>(new Model()));
		this.readOnly = readOnly;
		this.model = model;
		this.choicesModel = choicesModel;
		this.choicesAllModel = choicesAllModel;
		this.choiceRenderer = choiceRenderer;
		this.rows = rows;
		this.allowOrder = allowOrder;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Fragment frag;
		if (readOnly) {
			frag = createList("content");
		} else {
			frag = createPallete("content");
		}
		add(frag);

	}

	private Fragment createPallete(String id) {
		final Fragment frag = new Fragment(id, "writeFragment", this);
		palette = new CPalette<>("palleteComp", model, choicesModel, choicesAllModel, choiceRenderer, rows, allowOrder);
		frag.add(palette);
		if (CollectionUtils.listSize(changeListeners) > 0) {
			for (IPaletteChange listener : changeListeners) {
				palette.addChangeListner(listener);
			}
			changeListeners.clear();
		}
		return frag;
	}

	private Fragment createList(String id) {
		final Fragment frag = new Fragment(id, "readFragment", this);
		ListView<T> listview = new ListView<T>("listview", model) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<T> item) {
				item.add(new Label("label", CDisplayConverterUtils.getConvertedDisplayValue(choiceRenderer.getDisplayValue(item.getModelObject()), null)));
			}
		};
		frag.add(listview);
		return frag;
	}

	public void addChangeListener(IPaletteChange listener) {
		if (palette != null) {
			palette.addChangeListner(listener);
		} else {
			changeListeners.add(listener);
		}
	}

	/**
	 * @return component with choices
	 */
	public Component getChoicesComponent() {
		return palette.getChoicesComponent();
	}
}
