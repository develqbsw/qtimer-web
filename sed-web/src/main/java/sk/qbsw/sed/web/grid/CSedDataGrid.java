package sk.qbsw.sed.web.grid;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssUrlReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.CoreLibrariesContributor;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.datagrid.DataGrid;
import com.inmethod.grid.toolbar.NoRecordsToolbar;

import sk.qbsw.sed.web.grid.toolbar.CPagingToolbar;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * 
 * @author Pavol Lobb
 *
 */
public class CSedDataGrid<D extends IDataSource<T>, T, S> extends DataGrid<D, T, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CPagingToolbar<D, T, S> bottomToolbar;

	// povodny js subor bolo potrebne trochu upravit, zmena je na riadku 685
	private static final JavaScriptHeaderItem JS_SCRIPT_JQ_SED = new JavaScriptUrlReferenceHeaderItem("assets-sed/plugins/inmethod-grid/script-jq-sed.js", null, true, "UTF-8", null);
	private static final CssHeaderItem STYLE_GRID = new CssUrlReferenceHeaderItem("assets/css/style-grid.css", "screen", null);

	/**
	 * contains links for hiding / showing columns
	 */
	private List<Component> columnComponentsForHiding = null;

	public CSedDataGrid(String id, D dataSource, List<IGridColumn<D, T, S>> columns, int rowsPerPage) {
		super(id, dataSource, columns);
		setRowsPerPage(rowsPerPage);
		init();
	}

	public CSedDataGrid(String id, D dataSource, List<IGridColumn<D, T, S>> columns) {
		super(id, dataSource, columns);
		setRowsPerPage(CSedSession.get().getUser().getTableRows());
		init();
	}

	private void init() {
		addBottomToolbar(new NoRecordsToolbar<D, T, S>(this));

		setAllowSelectMultiple(false);

		setSelectToEdit(true);
		setClickRowToSelect(true);
		setClickRowToDeselect(false);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		CoreLibrariesContributor.contributeAjax(getApplication(), response);
		if (isUseYui()) {
			response.render(JavaScriptHeaderItem.forReference(JS_YAHOO));
			response.render(JavaScriptHeaderItem.forReference(JS_EVENT));
			response.render(JavaScriptHeaderItem.forReference(JS_DOM));
			response.render(JavaScriptHeaderItem.forReference(JS_SCRIPT));
		} else {
			// jquery is already part of Wicket. So there is nothing more to include.
			response.render(JS_SCRIPT_JQ_SED);
		}
		response.render(STYLE_GRID);
	}

	public List<IModel<T>> getRowModels() {
		List<IModel<T>> rowModels = new ArrayList<>();

		WebMarkupContainer body = (WebMarkupContainer) get("form:bodyContainer:body:row");
		if (body != null) {
			for (Component component : body) {
				IModel<T> model = (IModel<T>) component.getDefaultModel();
				rowModels.add(model);
			}
		}

		return rowModels;
	}

	public List<Component> getColumnComponentsForHiding() {
		if (columnComponentsForHiding == null) {
			columnComponentsForHiding = new ArrayList<>();

			for (final IGridColumn<D, T, S> column : getAllColumns()) {

				AjaxFallbackLink<Object> showColumn = new AjaxFallbackLink<Object>("showColumn") {
					/** serial uid */
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						if (getColumnState().getColumnVisibility(column.getId())) {
							getColumnState().setColumnVisibility(column.getId(), false);
							setBody(Model.of("&nbsp;&nbsp;&nbsp;&nbsp;" + ((AbstractColumn) column).getHeaderModel().getObject()));
						} else {
							getColumnState().setColumnVisibility(column.getId(), true);
							setBody(Model.of("<i class='fa fa-check'></i>" + ((AbstractColumn) column).getHeaderModel().getObject()));
						}
						target.add(CSedDataGrid.this);
						target.add(this);
					}
				};

				showColumn.setBody(Model.of("<i class='fa fa-check'></i>" + ((AbstractColumn) column).getHeaderModel().getObject()));
				showColumn.setEscapeModelStrings(false);
				columnComponentsForHiding.add(showColumn);
			}
		}

		return columnComponentsForHiding;
	}

	public void showColumn(String columnId, boolean show, AjaxRequestTarget target) {
		IGridColumn<D, T, S> column = getColumnById(columnId);
		AjaxFallbackLink link = (AjaxFallbackLink) columnComponentsForHiding.get(getAllColumns().indexOf(column));

		if (show) {
			if (column != null) {
				link.setBody(Model.of("<i class='fa fa-check'></i>" + ((AbstractColumn<D, T, S>) column).getHeaderModel().getObject()));
			}
			getColumnState().setColumnVisibility(columnId, true);
		} else {
			if (column != null) {
				link.setBody(Model.of("&nbsp;&nbsp;&nbsp;&nbsp;" + ((AbstractColumn<D, T, S>) column).getHeaderModel().getObject()));
			}
			getColumnState().setColumnVisibility(columnId, false);
		}

		target.add(link);
	}

	public void showColumn(IGridColumn<D, T, S> column, boolean show) {
		AjaxFallbackLink link = (AjaxFallbackLink) columnComponentsForHiding.get(getAllColumns().indexOf(column));

		if (show) {
			link.setBody(Model.of("<i class='fa fa-check'></i>" + ((AbstractColumn<D, T, S>) column).getHeaderModel().getObject()));
			getColumnState().setColumnVisibility(column.getId(), true);
		} else {
			link.setBody(Model.of("&nbsp;&nbsp;&nbsp;&nbsp;" + ((AbstractColumn<D, T, S>) column).getHeaderModel().getObject()));
			getColumnState().setColumnVisibility(column.getId(), false);
		}
	}

	private IGridColumn<D, T, S> getColumnById(String columnId) {
		for (final IGridColumn<D, T, S> column : getAllColumns()) {
			if (column.getId().equals(columnId)) {
				return column;
			}
		}
		return null;
	}

	@Override
	protected void onRowClicked(AjaxRequestTarget target, IModel<T> rowModel) {
		// nothing
	}

	protected void onDoubleRowClicked(AjaxRequestTarget target, IModel<T> rowModel) {
		// nothing
	}

	/**
	 * Adds a toolbar to the bottom section (below the actual data).
	 * 
	 * @see CPagingToolbar
	 * @param toolbar toolbar instance
	 */
	public void addBottomToolbar(CPagingToolbar<D, T, S> bottomToolbar) {
		this.bottomToolbar = bottomToolbar;
		super.addBottomToolbar(bottomToolbar);
	}

	/**
	 * nastavi tabulku na prvu stranku malo by sa volat pri kazdej zmene filtra
	 * tabulky
	 */
	public void setFirstPage() {
		bottomToolbar.getPagingNavigator().getPageable().setCurrentPage(0);
	}

	@Override
	protected void onRowPopulated(final WebMarkupContainer rowComponent) {
		super.onRowPopulated(rowComponent);

		// SED-869 - pridanie double click listenera na riadok
		rowComponent.add(new AjaxFormSubmitBehavior(getForm(), "dblclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				// do nothing
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				// do nothing
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				IModel<T> model = (IModel<T>) rowComponent.getDefaultModel();

				IGridColumn<D, T, S> lastClickedColumn = getLastClickedColumn();
				if (lastClickedColumn != null) {
					if (onCellClicked(target, model, lastClickedColumn)) {
						return;
					}
					if (lastClickedColumn.cellClicked(model)) {
						return;
					}
				}

				onDoubleRowClicked(target, model);
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);

				CharSequence columnParameter = "return {'column': Wicket.$(attrs.c).imxtClickedColumn}";
				attributes.getDynamicExtraParameters().add(columnParameter);

				CharSequence precondition = "return InMethod.XTable.canSelectRow(attrs.event);";
				AjaxCallListener ajaxCallListener = new AjaxCallListener();
				ajaxCallListener.onPrecondition(precondition);
				attributes.setAllowDefault(true);
				attributes.getAjaxCallListeners().add(ajaxCallListener);
			}

			@Override
			public CharSequence getCallbackScript() {
				return getCallbackFunction(CallbackParameter.context("col"));
			}
		});
	}
}
