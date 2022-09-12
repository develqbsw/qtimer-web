package sk.qbsw.sed.component.jstree;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.util.string.StringValue;

import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.utils.CWicketUtils;

/**
 * Panel with jstree component, tree.js file need to be attached to the html
 * 
 * @author Peter Bozik
 *
 * @param <T>
 */
public class CJSTreePanel<T> extends Panel {
	private static final long serialVersionUID = 1L;
	private final AbstractAjaxBehavior dataProviderBehaviour;
	private final AbstractDefaultAjaxBehavior nodeMovedBehaviour;
	private final AbstractDefaultAjaxBehavior nodeSelectedBehaviour;
	private WebMarkupContainer treePanel;
	private final CJSTreeProvider<T> dataProvider;
	private ITreeNodeSelected<T> selectedListner;
	private boolean draggable = true;
	private Boolean onlyValid = true;

	public CJSTreePanel(String id, CJSTreeProvider<T> dataProvider) {
		super(id);
		this.dataProvider = dataProvider;
		dataProviderBehaviour = new AbstractAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onRequest() {

				RequestCycle requestCycle = RequestCycle.get();
				try {
					CJSTreePanel.this.dataProvider.loadData(onlyValid);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				}
				String json = CJSTreePanel.this.dataProvider.getTreeModelInJson();
				requestCycle.scheduleRequestHandlerAfterCurrent(new TextRequestHandler("application/json", "UTF-8", json));
			}
		};
		nodeMovedBehaviour = new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				StringValue idValue = getRequest().getRequestParameters().getParameterValue("id");
				StringValue idParent = getRequest().getRequestParameters().getParameterValue("parent_id");
				StringValue idParentOld = getRequest().getRequestParameters().getParameterValue("parent_old_id");

				Long nodeToOpen = getLongObject(idParentOld);
				try {
					Long treeNodeTo = getLongObject(idParent);

					if (treeNodeTo == null) {
						// presuvanie na najvyssiu uroven
						treeNodeTo = -1L;
					}

					CJSTreePanel.this.dataProvider.moveNode(getLongObject(idValue), treeNodeTo);
					CWicketUtils.refreshFeedback(target, getPage());
					// ak bol uzol presunuty, otvorim strom po refreshe nad novym parentom
					nodeToOpen = getLongObject(idParent);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), target, getPage());

				}
				// po uspesnom ale i neuspesnom presune sa refreshne cely strom (aj s udajmi zo serveru)
				target.appendJavaScript("refreshTree('" + CJSTreePanel.this.getMarkupId() + "','" + nodeToOpen + "','" + dataProviderBehaviour.getCallbackUrl() + "','"
						+ nodeMovedBehaviour.getCallbackUrl() + "','" + nodeSelectedBehaviour.getCallbackUrl() + "');");
			}
		};
		nodeSelectedBehaviour = new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				StringValue idValue = getRequest().getRequestParameters().getParameterValue("id");
				Long id = getLongObject(idValue);
				if (id != null && selectedListner != null) {
					selectedListner.selected(target, CJSTreePanel.this.dataProvider.getById(id));
				}
			}
		};
	}

	/**
	 * disable or enable drag and drop
	 * 
	 * @param draggable
	 */
	public void setDraggable(AjaxRequestTarget target, boolean draggable) {
		this.draggable = draggable;
		if (target != null) {
			target.appendJavaScript("setDraggable(" + draggable + ");");
		}
	}

	public boolean isDraggable() {
		return draggable;
	}

	public void setNodeSelectedListener(ITreeNodeSelected listener) {
		this.selectedListner = listener;
	}

	@Override
	protected void onInitialize() {

		super.onInitialize();
		treePanel = new WebMarkupContainer("tree");
		treePanel.setOutputMarkupId(true);
		add(treePanel);

		treePanel.add(dataProviderBehaviour);
		treePanel.add(nodeMovedBehaviour);
		treePanel.add(nodeSelectedBehaviour);
	}

	private Long getLongObject(StringValue object) {
		if (object != null && StringUtils.isNotBlank(object.toString()) && StringUtils.isNumeric(object.toString())) {
			return object.toLongObject();
		}
		return null;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(OnDomReadyHeaderItem.forScript("setDraggable(" + draggable + ");"));
		response.render(OnDomReadyHeaderItem.forScript("initTree('" + this.getMarkupId() + "','" + dataProviderBehaviour.getCallbackUrl() + "','" + nodeMovedBehaviour.getCallbackUrl() + "','"
				+ nodeSelectedBehaviour.getCallbackUrl() + "');"));
	}

	public void setOnlyValid(Boolean onlyValid) {
		this.onlyValid = onlyValid;
	}

	public Boolean getOnlyValid() {
		return onlyValid;
	}
}
