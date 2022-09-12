package sk.qbsw.sed.component.jstree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;

import com.google.gson.Gson;

import sk.qbsw.sed.fw.exception.CBussinessDataException;

public abstract class CJSTreeProvider<T> implements ITreeProvider<T> {

	private static final long serialVersionUID = 1L;

	public abstract void moveNode(Long treeNodeFrom, Long treeNodeTo) throws CBussinessDataException;

	/**
	 * this should load data into the provider
	 * 
	 * @throws CBussinessDataException
	 */
	public abstract void loadData(Boolean onlyValid) throws CBussinessDataException;

	public final String getTreeModelInJson() {
		Iterator<? extends T> iter = this.getRoots();
		List<CJSTreeModel> roots = new ArrayList<>();
		while (iter.hasNext()) {
			roots.add(transform(iter.next()));
		}
		Gson gson = new Gson();
		return gson.toJson(roots);
	}

	public abstract CJSTreeModel transform(T object);

	public abstract T getById(Long id);
}
