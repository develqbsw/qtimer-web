package sk.qbsw.sed.web.grid.toolbar;

import org.apache.wicket.Component;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.datagrid.DataGrid;
import com.inmethod.grid.toolbar.paging.PagingNavigator;
import com.inmethod.grid.toolbar.paging.PagingToolbar;

/**
 * 
 * trieda za ucelom vediet ziskat pagingNavigator pre potreby vediet skocit na
 * prvu stranku po zmene filtra tabulky
 * 
 * @author lobb
 *
 * @param <D>
 * @param <T>
 * @param <S>
 */
public class CPagingToolbar<D extends IDataSource<T>, T, S> extends PagingToolbar<D, T, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PagingNavigator pagingNavigator;

	/**
	 * constructor
	 * 
	 * @param grid
	 */
	public CPagingToolbar(DataGrid<D, T, S> grid) {
		super(grid);
	}

	@Override
	protected Component newPagingNavigator(String id) {
		pagingNavigator = new PagingNavigator(id, getDataGrid());
		return pagingNavigator;
	}

	public PagingNavigator getPagingNavigator() {
		return pagingNavigator;
	}
}
