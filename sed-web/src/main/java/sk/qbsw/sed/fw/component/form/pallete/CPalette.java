package sk.qbsw.sed.fw.component.form.pallete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.extensions.markup.html.form.palette.component.Recorder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CPalette<T> extends Palette<T> {
	private static final long serialVersionUID = 6962784629546969362L;

	private Button addButton;
	private Button removeButton;
	private final List<IPaletteChange> changeListeners;

	private Component choicesComponent;

	private final IModel<? extends Collection<? extends T>> choicesAllModel;

	public CPalette(final String id, final ListModel<T> model, final IModel<? extends Collection<? extends T>> choicesModel, final IChoiceRenderer<T> choiceRenderer, final int rows,
			final boolean allowOrder) {
		super(id, model, choicesModel, choiceRenderer, rows, allowOrder);
		setOutputMarkupId(true);
		this.choicesAllModel = choicesModel;
		changeListeners = new ArrayList<>();
	}

	public CPalette(final String id, final ListModel<T> model, final IModel<? extends Collection<? extends T>> choicesCurrentModel, final IModel<? extends Collection<? extends T>> choicesAllModel,
			final IChoiceRenderer<T> choiceRenderer, final int rows, final boolean allowOrder) {
		super(id, model, choicesCurrentModel, choiceRenderer, rows, allowOrder);
		this.choicesAllModel = choicesAllModel;
		setOutputMarkupId(true);
		changeListeners = new ArrayList<>();
	}

	@Override
	protected Component newChoicesComponent() {
		choicesComponent = super.newChoicesComponent();
		return choicesComponent;
	}

	/** {@inheritDoc} */
	@Override
	protected Component newAddComponent() {
		addButton = new Button("addButton") {

			private static final long serialVersionUID = 0L;

			@Override
			protected void onComponentTag(final ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("onclick", CPalette.this.getAddOnClickJS());
			}
		};

		addButton.add(AttributeModifier.replace("class", "paletteButton add"));
		addButton.setOutputMarkupId(true);
		return addButton;
	}

	/** {@inheritDoc} */
	@Override
	protected Component newRemoveComponent() {
		removeButton = new Button("removeButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(final ComponentTag tag) {
				super.onComponentTag(tag);
				tag.getAttributes().put("onclick", CPalette.this.getRemoveOnClickJS());
			}
		};

		removeButton.add(AttributeModifier.replace("class", "paletteButton remove"));
		removeButton.setOutputMarkupId(true);
		return removeButton;
	}

	public void addChangeListner(IPaletteChange changeListener) {
		changeListeners.add(changeListener);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Recorder<T> newRecorderComponent() {
		Recorder recorder = new Recorder<T>("recorder", this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void updateModel() {
				super.updateModel();
				CPalette.this.updateModel();
			}

			@Override
			protected List<T> getUnselectedList() {
				final Collection<? extends T> choices = getPalette().getChoices();
				final IChoiceRenderer<T> renderer = getPalette().getChoiceRenderer();
				final List<T> unselected = new ArrayList<>(Math.max(1, choices.size() - getSelectedIds().size()));

				for (final T choice : choices) {
					final String choiceId = renderer.getIdValue(choice, 0);

					if (getSelectedIds().contains(choiceId) == false) {
						unselected.add(choice);
					}
				}
				return unselected;
			}

			@Override
			protected List<T> getSelectedList() {
				if (getSelectedIds().isEmpty()) {
					return Collections.emptyList();
				}

				List<String> selectedIds = getSelectedIds();
				final IChoiceRenderer<T> renderer = getPalette().getChoiceRenderer();
				final List<T> selected = new ArrayList<>(getSelectedIds().size());
				final Collection<? extends T> choices = getAllChoices();

				if (choices != null && !choices.isEmpty()) {
					final Map<T, String> idForChoice = new HashMap<>(choices.size());

					// reduce number of method calls by building a lookup table
					for (final T choice : choices) {
						idForChoice.put(choice, renderer.getIdValue(choice, 0));
					}

					for (final String id : selectedIds) {
						for (final T choice : choices) {
							final String idValue = idForChoice.get(choice);
							if (id.equals(idValue)) // null-safe compare
							{
								selected.add(choice);
								break;
							}
						}
					}
				}

				return selected;
			}
		};

		recorder.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				for (IPaletteChange changeListener : changeListeners) {
					changeListener.selectionChanged(target);
				}

			}
		});
		return recorder;
	}

	/**
	 * @return the choicesComponent
	 */
	@Override
	public Component getChoicesComponent() {
		return choicesComponent;
	}

	private Collection<? extends T> getAllChoices() {
		if (choicesAllModel == null) {
			return null;
		}

		return choicesAllModel.getObject();
	}
}
