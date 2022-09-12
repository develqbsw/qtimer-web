package sk.qbsw.sed.web.ui.tree.content;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.ProviderSubset;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import sk.qbsw.sed.client.model.ISubordinateBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.component.stats.CMultiBarChart;
import sk.qbsw.sed.component.tree.CustomizedCheckedFolder;
import sk.qbsw.sed.panel.requests.CRequestTablePanel;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.ui.tree.CCheckedEmployeeTreeProviderSubset;
import sk.qbsw.sed.web.ui.tree.CEmployeeTreeProvider;

public class CEmployeeTreeCheckedContent implements IDetachable {

	private static final long serialVersionUID = 1L;

	private ProviderSubset<CViewOrganizationTreeNodeRecord> checked;

	private ISubordinateBrwFilterCriteria filter;

	private Panel panelToRefresh;

	private Label pageTitleSmall;

	private String pageTitleSmallString;

	public CEmployeeTreeCheckedContent(ITreeProvider<CViewOrganizationTreeNodeRecord> provider, ISubordinateBrwFilterCriteria filter, Panel panelToRefresh, Label pageTitleSmall, String pageTitleSmallString) {
		checked = new CCheckedEmployeeTreeProviderSubset(provider, filter);
		this.filter = filter;
		this.panelToRefresh = panelToRefresh;
		this.pageTitleSmall = pageTitleSmall;
		this.pageTitleSmallString = pageTitleSmallString;
	}

	@Override
	public void detach() {
		checked.detach();
	}

	protected boolean isChecked(CViewOrganizationTreeNodeRecord node) {
		return checked.contains(node);
	}

	protected void check(CViewOrganizationTreeNodeRecord node, boolean check) {
		if (check) {
			checked.add(node);
		} else {
			checked.remove(node);
		}
	}

	public Component newContentComponent(String id, final AbstractTree<CViewOrganizationTreeNodeRecord> tree, IModel<CViewOrganizationTreeNodeRecord> model) {
		return new CustomizedCheckedFolder<CViewOrganizationTreeNodeRecord>(id, tree, model) {
			/**
				 * 
				 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				CViewOrganizationTreeNodeRecord mo = getModelObject();
				if (isChecked(mo)) {
					// pridaj do zoznamu
					filter.getEmplyees().add(mo.getUserId());
				} else {
					// odober so zoznamu
					filter.getEmplyees().remove(mo.getUserId());
				}

				String selectedEmployees = null;

				for (final Iterator<Long> iterator = filter.getEmplyees().iterator(); iterator.hasNext();) {
					final Long employeeId = iterator.next();

					CViewOrganizationTreeNodeRecord record = ((CEmployeeTreeProvider) tree.getProvider()).getById(employeeId);

					if (record == null) {
						// pre pripad ak mam vybranych zamestnancov ktori su neni moji podriadeni a otvorim vybrat zamestnancov
						iterator.remove();
					} else {
						if (selectedEmployees == null) {
							selectedEmployees = record.getName() + " " + record.getSurname();
						} else {
							selectedEmployees += ", " + record.getName() + " " + record.getSurname();
						}
					}
				}

				if (selectedEmployees != null) {
					pageTitleSmall.setDefaultModelObject(pageTitleSmallString + " " + selectedEmployees);
				} else {
					pageTitleSmall.setDefaultModelObject("");
				}

				if (filter instanceof CSubrodinateTimeStampBrwFilterCriteria) {
					// pre vykaz prace chcem dynamicky zobrazovat/skryvat stlpec zamestnanec

					if (filter.getEmplyees().size() <= 1) {
						// skryjem stlpec zamestnanec
						((CSedDataGrid) panelToRefresh).showColumn("employee", false, target);
					} else {
						// zobrazim stlpec zamestnanec
						((CSedDataGrid) panelToRefresh).showColumn("employee", true, target);
					}
				}

				// refresh tabulky / grafu
				if (panelToRefresh instanceof CMultiBarChart) {
					((CMultiBarChart) panelToRefresh).refresh(target, true);
				} else if (panelToRefresh instanceof CRequestTablePanel) {
					if (((CRequestTablePanel) panelToRefresh).isGraphVisible()) {
						((CRequestTablePanel) panelToRefresh).updateGraph(target, true);
					} else {
						target.add(((CRequestTablePanel) panelToRefresh).getTable());
					}
				} else {
					target.add(panelToRefresh);
				}
				target.add(pageTitleSmall);
			}

			@Override
			protected IModel<Boolean> newCheckBoxModel(final IModel<CViewOrganizationTreeNodeRecord> model) {
				return new IModel<Boolean>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Boolean getObject() {
						return isChecked(model.getObject());
					}

					@Override
					public void setObject(Boolean object) {
						check(model.getObject(), object);
					}

					@Override
					public void detach() {
						// do nothing
					}
				};
			}
		};
	}

	public void updateProviderSubset(ITreeProvider<CViewOrganizationTreeNodeRecord> provider) {
		((CCheckedEmployeeTreeProviderSubset) checked).update(filter);
	}
}
