package sk.qbsw.sed.fw.component.feedback;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.settings.IApplicationSettings;

import sk.qbsw.sed.fw.CFrameworkConfiguration;
import sk.qbsw.sed.fw.component.IComponentContainer;
import sk.qbsw.sed.fw.component.form.input.IComponentLabel;
import sk.qbsw.sed.fw.utils.CJavascriptUtils;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * A panel that displays {@link org.apache.wicket.feedback.FeedbackMessage}s in
 * a list view. The feedback panel uses a customized way to present the
 * messages. Note this class is based on
 * {@link org.apache.wicket.markup.html.panel.FeedbackPanel}.
 * 
 * Panel pre zobrazenie chýb a upozornení, predvolene skrytý
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 */
public class CFeedbackPanel extends Panel implements IFeedback {

	private final CFeedbackFilter filterMsgs = new CFeedbackFilter();

	private final Set<Component> acceptableComponents;

	/**
	 * List for messages.
	 */
	private final class MessageListView extends ListView<FeedbackMessage> {
		private static final long serialVersionUID = 1L;

		/**
		 * @see org.apache.wicket.Component#Component(String)
		 */
		public MessageListView(final String id) {
			super(id);
			setDefaultModel(newFeedbackMessagesModel());
		}

		@Override
		protected IModel<FeedbackMessage> getListItemModel(final IModel<? extends List<FeedbackMessage>> listViewModel, final int index) {
			return new AbstractReadOnlyModel<FeedbackMessage>() {
				private static final long serialVersionUID = 1L;

				/**
				 * WICKET-4258 Feedback messages might be cleared already.
				 * 
				 * @see IApplicationSettings#setFeedbackMessageCleanupFilter(org.apache.wicket.feedback.IFeedbackMessageFilter)
				 */
				@Override
				public FeedbackMessage getObject() {
					if (index >= listViewModel.getObject().size()) {
						return null;
					} else {
						return listViewModel.getObject().get(index);
					}
				}
			};
		}

		@Override
		protected void populateItem(final ListItem<FeedbackMessage> listItem) {
			final FeedbackMessage message = listItem.getModelObject();
			message.markRendered();
			final Component label = newMessageDisplayComponent("message", message);
			final AttributeModifier levelModifier = AttributeModifier.append("class", getLevelClass(message.getLevel()));
			label.add(levelModifier);
			listItem.add(levelModifier);
			listItem.add(label);
		}
	}

	private static final long serialVersionUID = 1L;

	/** Message view */
	private final MessageListView messageListView;

	/**
	 * @see org.apache.wicket.Component#Component(String)
	 */
	public CFeedbackPanel(final String id) {
		this(id, null);
	}

	public CFeedbackPanel(final String id, IFeedbackMessageFilter filter) {
		this(id, filter, null);

	}

	/**
	 * @see org.apache.wicket.Component#Component(String)
	 * 
	 * @param id
	 * @param filter
	 */
	public CFeedbackPanel(final String id, IFeedbackMessageFilter filter, Component[] acceptableComponents) {
		super(id);
		setEscapeModelStrings(false);
		this.acceptableComponents = new HashSet();
		if (acceptableComponents != null && acceptableComponents.length > 0) {
			for (Component component : acceptableComponents) {
				this.acceptableComponents.add(component);
			}

		}
		
		WebMarkupContainer messagesContainer = new WebMarkupContainer("messagesContainer") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(anyMessage());
			}
		};
		add(messagesContainer);
		messageListView = new MessageListView("messages");
		messageListView.setVersioned(false);
		messagesContainer.add(messageListView);
		setOutputMarkupId(true);
		
		if (filter != null) {
			setFilter(filter);
		}
		setFilter(filterMsgs);
	}

	public void addAcceptableComponent(Component component) {
		this.acceptableComponents.add(component);
	}

	/**
	 * Search messages that this panel will render, and see if there is any message
	 * of level ERROR or up. This is a convenience method; same as calling
	 * 'anyMessage(FeedbackMessage.ERROR)'.
	 * 
	 * @return whether there is any message for this panel of level ERROR or up
	 */
	public final boolean anyErrorMessage() {
		return anyMessage(FeedbackMessage.ERROR);
	}

	/**
	 * Search messages that this panel will render, and see if there is any message.
	 * 
	 * @return whether there is any message for this panel
	 */
	public final boolean anyMessage() {
		return anyMessage(FeedbackMessage.UNDEFINED);
	}

	/**
	 * Search messages that this panel will render, and see if there is any message
	 * of the given level.
	 * 
	 * @param level the level, see FeedbackMessage
	 * @return whether there is any message for this panel of the given level
	 */
	public final boolean anyMessage(int level) {
		List<FeedbackMessage> msgs = getCurrentMessages();

		for (FeedbackMessage msg : msgs) {
			if (msg.isLevel(level)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return Model for feedback messages on which you can install filters and
	 *         other properties
	 */
	public final FeedbackMessagesModel getFeedbackMessagesModel() {
		return (FeedbackMessagesModel) messageListView.getDefaultModel();
	}

	/**
	 * @return The current message filter
	 */
	public final IFeedbackMessageFilter getFilter() {
		return getFeedbackMessagesModel().getFilter();
	}

	/**
	 * @return The current sorting comparator
	 */
	public final Comparator<FeedbackMessage> getSortingComparator() {
		return getFeedbackMessagesModel().getSortingComparator();
	}

	/**
	 * @see org.apache.wicket.Component#isVersioned()
	 */
	@Override
	public boolean isVersioned() {
		return false;
	}

	/**
	 * Sets a filter to use on the feedback messages model
	 * 
	 * @param filter The message filter to install on the feedback messages model
	 * 
	 * @return FeedbackPanel this.
	 */
	public final CFeedbackPanel setFilter(IFeedbackMessageFilter filter) {
		getFeedbackMessagesModel().setFilter(filter);
		return this;
	}

	/**
	 * @param maxMessages The maximum number of feedback messages that this feedback
	 *                    panel should show at one time
	 * 
	 * @return FeedbackPanel this.
	 */
	public final CFeedbackPanel setMaxMessages(int maxMessages) {
		messageListView.setViewSize(maxMessages);
		return this;
	}

	/**
	 * Sets the comparator used for sorting the messages.
	 * 
	 * @param sortingComparator comparator used for sorting the messages.
	 * 
	 * @return FeedbackPanel this.
	 */
	public final CFeedbackPanel setSortingComparator(Comparator<FeedbackMessage> sortingComparator) {
		getFeedbackMessagesModel().setSortingComparator(sortingComparator);
		return this;
	}

	/**
	 * Gets the css class for the given message.
	 * 
	 * @param message the message
	 * @return the css class; by default, this returns feedbackPanel + the message
	 *         level, eg 'feedbackPanelERROR', but you can override this method to
	 *         provide your own
	 */
	protected String getCSSClass(final FeedbackMessage message) {
		return "feedbackPanel" + message.getLevelAsString();
	}

	public String getLevelClass(int level) {
		String cls = "";
		if (level == FeedbackMessage.INFO) {
			cls = "alert-info";
		} else if (level == FeedbackMessage.ERROR) {
			cls = "alert-danger";
		} else if (level == FeedbackMessage.WARNING) {
			cls = "alert-warning";
		} else if (level == FeedbackMessage.SUCCESS) {
			cls = "alert-success";
		}
		return cls;
	}

	public String getLabel(int level) {
		String label = "";
		if (level == FeedbackMessage.INFO) {
			label = CStringResourceReader.read("label.feedback.message.info");
		} else if (level == FeedbackMessage.ERROR) {
			label = CStringResourceReader.read("label.feedback.message.error");
		} else if (level == FeedbackMessage.WARNING) {
			label = CStringResourceReader.read("label.feedback.message.warning");
		}
		return label;
	}

	/**
	 * Gets the currently collected messages for this panel.
	 * 
	 * @return the currently collected messages for this panel, possibly empty
	 */
	protected final List<FeedbackMessage> getCurrentMessages() {
		final List<FeedbackMessage> messages = messageListView.getModelObject();
		return Collections.unmodifiableList(messages);
	}

	/**
	 * Gets a new instance of FeedbackMessagesModel to use.
	 * 
	 * @return Instance of FeedbackMessagesModel to use
	 */
	protected FeedbackMessagesModel newFeedbackMessagesModel() {
		return new FeedbackMessagesModel(this);
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		filterMsgs.clearMessages();
	}

	/**
	 * Generates a component that is used to display the message inside the feedback
	 * panel. This component must handle being attached to <code>span</code> tags.
	 * 
	 * By default a {@link Label} is used.
	 * 
	 * Note that the created component is expected to respect feedback panel's
	 * {@link #getEscapeModelStrings()} value
	 * 
	 * @param id      parent id
	 * @param message feedback message
	 * @return component used to display the message
	 */
	protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {

		Serializable serializable = message.getMessage();
		if (message.getReporter() instanceof IComponentLabel) {
			StringBuilder tmpSB = new StringBuilder();
			tmpSB.append(((IComponentLabel) message.getReporter()).getComponentLabel());
			tmpSB.append(" - ");
			tmpSB.append(serializable);
			serializable = tmpSB.toString();
		}
		
		Label label = new Label(id, (serializable == null) ? "" : serializable.toString());
		label.setEscapeModelStrings(CFeedbackPanel.this.getEscapeModelStrings());
		return label;
	}

	private final class CFeedbackFilter implements IFeedbackMessageFilter {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Set<String> messagesString = new HashSet<>();

		@Override
		public boolean accept(FeedbackMessage message) {
			if (message.isRendered()) {
				return false;
			}
			
			if (message.getReporter() instanceof IComponentContainer) {
				CFeedbackPanel panel = ((IComponentContainer) message.getReporter()).getFeedbackPanel();
				if (panel != null && !panel.equals(CFeedbackPanel.this)) {
					return false;
				}
			}
			
			if (!CFrameworkConfiguration.DISPLAY_COMPONENT_ERROR_MESSAGES_IN_FEEDBACKPANEL && message.getReporter() instanceof IComponentLabel) {
				return false;
			}
			
			if (!messagesString.add(message.toString())) {
				message.markRendered();
				return false;

			}
			return true;
		}

		public void clearMessages() {
			messagesString = new HashSet<>();
		}

	}

	@Override
	public void renderHead(IHeaderResponse response) {
		if (anyMessage()) {
			response.render(OnDomReadyHeaderItem.forScript(CJavascriptUtils.goToTop()));
		}
	}
}
