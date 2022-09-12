package sk.qbsw.sed.web.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;

/**
 * Represents a composite behavior allowing the user to attach multiple
 * behaviors to a component at once.
 * 
 * @author David Bernard
 * @author Erik Brakkee
 * @author Marek Martinkovic
 * @version 2.3.0
 * @since 2.3.0
 */
public class CCompositeBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	private final List<Behavior> behaviors;

	/**
	 * varargs contructors
	 * 
	 * @param behaviors
	 */
	public CCompositeBehavior(final Behavior... behaviors) {
		this.behaviors = Arrays.asList(behaviors);
	}

	public CCompositeBehavior(final List<Behavior> behaviors) {
		this.behaviors = behaviors;
	}

	public CCompositeBehavior() {
		behaviors = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterRender(final Component arg0) {
		for (final Behavior behavior : getBehaviors())
			behavior.afterRender(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeRender(final Component arg0) {
		for (final Behavior behavior : getBehaviors())
			behavior.beforeRender(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bind(final Component arg0) {
		for (final Behavior behavior : getBehaviors())
			behavior.bind(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void detach(final Component arg0) {
		for (final Behavior behavior : getBehaviors())
			behavior.detach(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getStatelessHint(final Component arg0) {
		boolean back = true;
		for (final Behavior behavior : getBehaviors())
			back = back && behavior.getStatelessHint(arg0);
		return back;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.behavior.Behavior#isEnabled(org.apache.wicket.
	 * Component)
	 */
	@Override
	public boolean isEnabled(final Component arg0) {
		boolean back = true;
		for (final Behavior behavior : getBehaviors())
			back = back && behavior.isEnabled(arg0);
		return back;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTemporary(final Component component) {
		boolean back = true;
		for (final Behavior behavior : getBehaviors())
			back = back && behavior.isTemporary(component);
		return back;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onComponentTag(final Component aComponent, final ComponentTag aTag) {
		for (final Behavior behavior : getBehaviors())
			behavior.onComponentTag(aComponent, aTag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderHead(final Component component, final IHeaderResponse response) {
		super.renderHead(component, response);
		for (final Behavior behavior : getBehaviors())
			behavior.renderHead(component, response);
	}

	/**
	 * Adds new behavior for execution.
	 *
	 * @param toAdd behavior to add
	 */
	public void addBehaviour(Behavior toAdd) {
		this.behaviors.add(toAdd);
	}

	/**
	 * Gets the behaviors.
	 *
	 * @return the behaviors
	 */
	private List<Behavior> getBehaviors() {
		return behaviors;
	}
}
