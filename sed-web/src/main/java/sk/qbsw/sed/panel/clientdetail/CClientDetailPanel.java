package sk.qbsw.sed.panel.clientdetail;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.detail.CClientDetailRecord;
import sk.qbsw.sed.communication.service.IClientDetailClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /clientDetail SubPage title: Detaily organizácie
 * 
 * Panel CClientDetailPanel - Detail / Editovať
 */
public class CClientDetailPanel extends CPanel {

	private static final long serialVersionUID = 1L;

	private CClientDetailForm<CClientDetailRecord> form;

	private CClientDetailRecord detailRecord;

	private CompoundPropertyModel<CClientDetailRecord> clientDetailModel;

	private Long entityID;

	private AjaxFallbackLink<Object> linkEdit;

	private AjaxFallbackButton saveButton;

	private AjaxFallbackButton backButton;

	private final Label tabTitle;

	@SpringBean
	private IClientDetailClientService service;

	public CClientDetailPanel(String id, Long clientId, Label tabTitle) {
		super(id);
		this.tabTitle = tabTitle;
		this.entityID = clientId;
		setDetailRecord();
	}

	protected void onInitialize() {
		super.onInitialize();
		this.setOutputMarkupId(true);

		clientDetailModel = new CompoundPropertyModel<>(detailRecord);
		form = new CClientDetailForm<>("clientDetailForm", clientDetailModel, this, false);
		form.setModeDetail(true);
		add(form);

		linkEdit = new AjaxFallbackLink<Object>("edit") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.edit"));
				target.add(tabTitle);

				// update formulara
				setModeDetail(false);
				target.add(CClientDetailPanel.this);

				target.appendJavaScript("Main.runCustomCheck();FormElements.init();SedApp.init();");
			}
		};
		linkEdit.add(new AttributeAppender("title", getString("button.edit")));
		add(linkEdit);

		saveButton = new AjaxFallbackButton("submitBtn", form) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {

				try {
					CClientDetailRecord newOne = (CClientDetailRecord) form.getModelObject();
					service.updateDetail(newOne);
					target.add(CClientDetailPanel.this);

					tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.detail"));
					target.add(tabTitle);

					updateModel();
					setModeDetail(true);
					target.add(form);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CClientDetailPanel.this);
					onError(target, form);
				}
			}

			@Override
			public void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.appendJavaScript("afterError();");
				target.add(form);
			}
		};
		saveButton.setVisible(false);
		add(saveButton);

		backButton = new AjaxFallbackButton("backButton", form) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.detail"));
				target.add(tabTitle);

				CClientDetailPanel.this.updateModel();
				setModeDetail(true);
				target.add(CClientDetailPanel.this);
			}
		};
		backButton.setVisible(false);
		add(backButton);
	}

	public void updateModel() {
		setDetailRecord();

		form.setDefaultModelObject(detailRecord);
	}

	private void setDetailRecord() {
		try {
			this.detailRecord = service.getDetail(entityID);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}
	}

	public void setModeDetail(boolean isModeDetail) {
		if (isModeDetail) {
			// detail - linkEdit show and enable
			linkEdit.setEnabled(true);
			linkEdit.add(AttributeModifier.replace("class", "btn btn-green"));
			linkEdit.setVisible(true);
		} else {
			linkEdit.setEnabled(false);
			linkEdit.add(AttributeModifier.append("class", "disabled"));
			linkEdit.setVisible(true);
		}
		saveButton.setVisible(!isModeDetail);
		backButton.setVisible(!isModeDetail);
		form.setModeDetail(isModeDetail);
	}
}
