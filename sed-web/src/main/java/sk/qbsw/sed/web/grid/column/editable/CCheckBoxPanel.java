package sk.qbsw.sed.web.grid.column.editable;

import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.editable.CheckBoxPanel;

/**
 * 
 * @author lobb
 *
 *         od klasickeho CheckBoxPanel sa lisi v tom ze ma class="grey"
 */
public class CCheckBoxPanel<M, I, S> extends CheckBoxPanel<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CCheckBoxPanel(String id, final IModel<Boolean> model, IModel<I> rowModel, AbstractColumn<M, I, S> column) {
		super(id, model, rowModel, column);
	}
}
