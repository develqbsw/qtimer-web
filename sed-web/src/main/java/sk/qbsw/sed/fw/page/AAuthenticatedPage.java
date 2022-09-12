package sk.qbsw.sed.fw.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageReference;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.ILanguageConstant;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.CJavascriptResources;
import sk.qbsw.sed.fw.component.IComponentContainer;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.model.CPageResourceConfiguration;
import sk.qbsw.sed.fw.model.CPanelInRowModel;
import sk.qbsw.sed.fw.navigation.CSideMenu;
import sk.qbsw.sed.fw.navigation.CSideMenuItemModel;
import sk.qbsw.sed.fw.panel.ARowPanel;
import sk.qbsw.sed.fw.utils.CRemainingVacationUtils;
import sk.qbsw.sed.fw.utils.CWicketUtils;
import sk.qbsw.sed.fw.utils.CollectionUtils;
import sk.qbsw.sed.fw.utils.RolesUtil;
import sk.qbsw.sed.page.CJiraTokenGenerationPage;
import sk.qbsw.sed.page.CPinGenerationPage;
import sk.qbsw.sed.page.appinfo.CAppInfoPage;
import sk.qbsw.sed.page.clientdetail.CClientDetailPage;
import sk.qbsw.sed.page.login.CLoginPage;
import sk.qbsw.sed.page.passwordchange.CPasswordChangePage;
import sk.qbsw.sed.page.userdetail.CUserDetailPage;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * all pages that are visible only after login should extend this class
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public abstract class AAuthenticatedPage extends ABasePage implements IComponentContainer {
	private static final long serialVersionUID = 1L;
	
	private static final String SCRIPT = "script";

	private AAuthenticatedPage backPage;
	private Class<AAuthenticatedPage> backPageCls;
	private List<IModel<CSideMenuItemModel>> bcList;

	private CFeedbackPanel feedbackPanel;
	private boolean wasConstructedAsBackPage = false;
	private final CPageResourceConfiguration resourceConfiguration;

	private Label pageTitleSmall;
	private Label remainingVacationLabel;
	private Label pageAdditionalData;
	private boolean hideWhitePanel = false;

	@SpringBean
	private IUserClientService userService;

	public AAuthenticatedPage(PageParameters parameters, boolean hideWhitePanel) {
		super(parameters);
		this.hideWhitePanel = hideWhitePanel;
		resourceConfiguration = new CPageResourceConfiguration();
		initCommonScripts();
	}

	public AAuthenticatedPage(PageParameters parameters) {
		super(parameters);
		resourceConfiguration = new CPageResourceConfiguration();
		initCommonScripts();
	}

	public Label getPageTitleSmall() {
		return pageTitleSmall;
	}

	public Label getRemainingVacationLabel() {
		return remainingVacationLabel;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		initResources(getResourceConfiguration());
		add(new CSideMenu("sideMenuPanel"));
		add(new Label("pageTitle", getString(getPageKeyReference())));

		add(new ListView<Component>("subMenuComponents", getSubMenuComponents()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Component> item) {
				item.add(item.getModelObject());
			}
		});

		pageTitleSmall = new Label("pageTitleSmall", "");
		pageTitleSmall.setOutputMarkupId(true);
		add(pageTitleSmall);

		remainingVacationLabel = new Label("remainingVacation", getRemainingVacation());
		remainingVacationLabel.setOutputMarkupId(true);
		add(remainingVacationLabel);

		pageAdditionalData = new Label("pageAdditionalData", "");// napr. na
		// dashboard su meniny v hornej liste
		add(pageAdditionalData);

		initializeContent("fw_contentPanel");

		feedbackPanel = new CFeedbackPanel("feedback");
		add(feedbackPanel);
		feedbackPanel.setOutputMarkupId(true);

		initFooter();

		add(renderFooterScripts("footerScripts"));

		final Link logoutLink = new Link<WebPage>("logoutLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				logout();
			}
		};
		add(logoutLink);

		initPluginScripts("pluginScripts", resourceConfiguration.getPluginScripts());
		initPluginScripts("themeScripts", resourceConfiguration.getThemeScripts());
		initJavasriptInitializationCommands("initializationCommand");

		Form<Object> form = new Form<>("form");
		add(form);

		final RadioGroup<String> language = new RadioGroup<>("languageGroup", Model.of(getUser().getLanguage()));
		language.add(new Radio<String>("language_SK", new Model<String>(ILanguageConstant.SK)));
		language.add(new Radio<String>("language_EN", new Model<String>(ILanguageConstant.EN)));
		form.add(language);

		final AjaxFallbackButton save = new AjaxFallbackButton("save", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Injector.get().inject(this);
				
				CUserDetailRecord userDetailRecord = null;
				
				try {
					userDetailRecord = userService.getUserDetails(CSedSession.get().getUser().getUserId());
					userDetailRecord.setLanguage(convertLanguageToCode(language.getModelObject()));
					userService.modify(userDetailRecord);

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), target, getPage());
				}

				CSedSession.get().getUser().setLanguage(language.getModelObject());
				CSedSession.get().setLocale(new Locale(language.getModelObject()));
				
				if (userDetailRecord != null && userDetailRecord.getJiraTokenGeneration() != null) {
					CSedSession.get().getUser().setJiraTokenGeneration(userDetailRecord.getJiraTokenGeneration());
				}
				
				setResponsePage(getPage());
			}
		};
		form.add(save);
	}

	/**
	 * 0 = Už žiadna dovolenka 1 = Už len 1 deň dovolenky 2-4 Už len <n> dni
	 * dovolenky 5+ = Ešte <n> dní dovolenky Pre stavy s ½ dňom: 0,5 – 4,5 = Už len
	 * <n> dňa dovolenky 5,5 + = Ešte <n> dňa dovolenky
	 * 
	 * @return
	 */
	private String getRemainingVacation() {
		CUserDetailRecord user;
		try {
			user = userService.getUserDetails(getUser().getUserId());
			return CRemainingVacationUtils.getText(user.getVacation());
		} catch (CBussinessDataException e) {
			Logger.getLogger(AAuthenticatedPage.class).error(e);
			// nothing, we can live without this
			return "";
		}
	}

	private Long convertLanguageToCode(String language) {
		if (ILanguageConstant.SK.equals(language)) {
			return ILanguageConstant.ID_SK;
		} else if (ILanguageConstant.EN.equals(language)) {
			return ILanguageConstant.ID_EN;
		} else {
			return -1L;
		}
	}

	public Label getPageAdditionalData() {
		return pageAdditionalData;
	}

	private String getPageKeyReference() {
		return "page." + getPageKey() + ".label.title";
	}

	private void initFooter() {
		WebMarkupContainer footerTest = new WebMarkupContainer("footerTest");
		add(footerTest);
		footerTest.setVisible(false);
	}

	private final void initializeContent(final String id) {
		List<WebMarkupContainer> contents = new ArrayList<>();
		addContents(id, contents);
		ListView<WebMarkupContainer> listView = new ListView<WebMarkupContainer>("fw_contents", contents) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<WebMarkupContainer> item) {
				final WebMarkupContainer container = item.getModelObject();
				final WebMarkupContainer container2 = new WebMarkupContainer("container");

				if (hideWhitePanel) {
					container2.add(AttributeAppender.replace("class", ""));
				}

				item.add(container2);
				if (container instanceof ARowPanel) {
					container2.add(container);
				} else {
					container2.add(new ARowPanel(id) {
						private static final long serialVersionUID = 1L;

						@Override
						public void addContents(String id, List<CPanelInRowModel> contents) {
							contents.add(new CPanelInRowModel(container, 12));
						}
					});
				}

			}
		};
		add(listView);
	}

	/**
	 * The container is wrapped in {@link ARowPanel} with maximal width (12) unless
	 * the container is not a {@link ARowPanel} itself
	 * 
	 * @param id
	 * @param contents
	 */
	public abstract void addContents(String id, List<WebMarkupContainer> contents);

	public AAuthenticatedPage getBackPage() {
		return backPage;
	}

	public void setBackPage(AAuthenticatedPage backPage) {
		this.backPage = backPage;
	}

	public void setBackPage(Class<AAuthenticatedPage> backPageCls) {
		this.backPageCls = backPageCls;
	}

	/**
	 * first try instance, then class
	 */
	public void navigateBack() {
		if (this instanceof IBackNavigatableByPageId && ((IBackNavigatableByPageId) this).getPreviousPageId() != null) {
			PageReference reference = new PageReference(((IBackNavigatableByPageId) this).getPreviousPageId());
			Page page = CWicketUtils.navigateBackToNewInstance(this, reference.getPage());
			if (page != null && page instanceof AAuthenticatedPage) {
				((AAuthenticatedPage) page).wasConstructedAsBackPage = true;
			}
			return;
		}
		if (this.backPage != null) {
			setResponsePage(backPage);
			return;
		} else if (this.backPageCls != null) {
			setResponsePage(backPageCls);
			return;
		} else {
			if (CollectionUtils.listSize(bcList) > 1) {
				for (int i = bcList.size() - 2; i > -1; i--) {
					Class<? extends AAuthenticatedPage> pageCls = bcList.get(i).getObject().getPage();
					if (pageCls != null) {

						setResponsePage(pageCls, getPageParameters());
						return;
					}
				}
			}

		}
	}

	protected boolean hasRole(Long role) {
		return RolesUtil.hasRole(getUser(), role);
	}

	protected CLoggedUserRecord getUser() {
		return ((CSedSession) getSession()).getUser();
	}

	private WebMarkupContainer renderFooterScripts(String id) {
		WebMarkupContainer result = new WebMarkupContainer(id);
		final List<String> scripts = new ArrayList<>();
		addScriptToFooter(scripts);
		if (CollectionUtils.listSize(scripts) == 0) {
			result.add(new WebMarkupContainer(SCRIPT));
			result.setVisible(false);
		} else {
			result.add(new RefreshingView<String>(SCRIPT) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(Item<String> item) {
					item.add(AttributeModifier.append("src", item.getModel()));

				}

				@Override
				protected Iterator<IModel<String>> getItemModels() {
					List<IModel<String>> modelList = new ArrayList<>();
					for (String script : scripts) {
						modelList.add(new Model<String>(script));
					}
					return modelList.iterator();
				}

			});
			result.setRenderBodyOnly(true);
		}
		return result;

	}

	/**
	 * The super method have to be called
	 * 
	 * @param scripts
	 */
	protected void addScriptToFooter(List<String> scripts) {

	}

	/**
	 * @return the feedbackPanel
	 */
	@Override
	public CFeedbackPanel getFeedbackPanel() {
		return feedbackPanel;
	}

	private void logout() {
		CSedSession.get().logout();
		setResponsePage(CLoginPage.class);
	}

	protected final boolean isConstructedAsBackPage() {
		return wasConstructedAsBackPage;
	}

	private void initPluginScripts(String idScriptContainer, final List<String> scripts) {
		WebMarkupContainer pluginScriptsContainer = new WebMarkupContainer(idScriptContainer) {
			@Override
			protected void onBeforeRender() {
				if (scripts == null || scripts.isEmpty()) {
					setVisible(false);
				} else {
					final List<IModel<String>> scriptModels = new ArrayList<>();
					Set<String> checkSet = new HashSet<>();
					for (String script : scripts) {
						if (!checkSet.contains(script)) {
							scriptModels.add(new Model<String>(script));
							checkSet.add(script);
						}
					}
					final String prefix = StringUtils.substringBefore(UrlUtils.rewriteToContextRelative(scripts.get(0), RequestCycle.get()), scripts.get(0));
					addOrReplace(new RefreshingView<String>(SCRIPT) {

						@Override
						protected Iterator<IModel<String>> getItemModels() {
							return scriptModels.iterator();
						}

						@Override
						protected void populateItem(Item<String> item) {
							item.add(AttributeAppender.append("src", prefix + item.getModelObject()));
						}

					});
				}
				super.onBeforeRender();
			}
		};
		pluginScriptsContainer.setRenderBodyOnly(true);
		add(pluginScriptsContainer);
	}

	private void initJavasriptInitializationCommands(String idScriptContainer) {

		WebMarkupContainer commandsContainer = new WebMarkupContainer(idScriptContainer) {
			@Override
			protected void onBeforeRender() {
				if (resourceConfiguration.getJsInitializationCommands() == null || resourceConfiguration.getJsInitializationCommands().isEmpty()) {
					setVisible(false);
				}
				super.onBeforeRender();
			}
		};
		final Model<String> commandsModel = new Model<>();
		final Label scriptLabel = new Label(SCRIPT, commandsModel) {
			@Override
			protected void onBeforeRender() {
				StringBuilder sb = new StringBuilder();
				if (resourceConfiguration.getJsInitializationCommands() != null && !resourceConfiguration.getJsInitializationCommands().isEmpty()) {
					sb.append("jQuery(document).ready(function() {\n");
					for (String command : resourceConfiguration.getJsInitializationCommands()) {
						sb.append(command);
						sb.append("\n");
					}
					sb.append("});\n");
				}
				commandsModel.setObject(sb.toString());
				super.onBeforeRender();
			}
		};
		scriptLabel.setEscapeModelStrings(false);
		commandsContainer.add(scriptLabel);
		commandsContainer.setRenderBodyOnly(true);
		add(commandsContainer);
	}

	/**
	 * Initialize common java scripts for the page
	 */
	private void initCommonScripts() {
		List<String> pluginScripts = new ArrayList<>();
		pluginScripts.add(CJavascriptResources.JQUERY);
		pluginScripts.add(CJavascriptResources.BOOTSTRAP);
		pluginScripts.add(CJavascriptResources.BLOCKUI);
		pluginScripts.add(CJavascriptResources.JQUERY_ICHECK);
		pluginScripts.add(CJavascriptResources.MOMENT);
		pluginScripts.add(CJavascriptResources.JQUERY_MOUSE_WHEEL);
		pluginScripts.add(CJavascriptResources.PERFECT_SCROLLBAR);
		pluginScripts.add(CJavascriptResources.BOOTBOX);
		pluginScripts.add(CJavascriptResources.SCROLL_TO);
		pluginScripts.add(CJavascriptResources.JQUERY_SCROLL_TO_FIXED);
		pluginScripts.add(CJavascriptResources.JQUERY_APEAR);
		pluginScripts.add(CJavascriptResources.JQUERY_COOKIE);
		pluginScripts.add(CJavascriptResources.JQUERY_VELOCITY);
		pluginScripts.add(CJavascriptResources.JQUERY_TOUCHE_SWIPE);
		pluginScripts.add(CJavascriptResources.BOOTSTRAP_MODAL);
		pluginScripts.add(CJavascriptResources.BOOTSTRAP_MODALMANAGER);
		pluginScripts.add(CJavascriptResources.JQUERY_INPUT_LIMITER);

		pluginScripts.add(CJavascriptResources.JQUERY_AUTOSIZE);
		pluginScripts.add(CJavascriptResources.SELECT);
		pluginScripts.add(CJavascriptResources.JQUERY_MASKED_INPUT);
		pluginScripts.add(CJavascriptResources.JQUERY_TOUCHSPIN);
		pluginScripts.add(CJavascriptResources.JQUERY_MASK_MONEY);
		pluginScripts.add(CJavascriptResources.BOOTSTRAP_DATEPICKER);
		pluginScripts.add(CJavascriptResources.BOOTSTRAP_TIMEPICKER);
		pluginScripts.add(CJavascriptResources.JQUERY_TAGSIPNPUT);
		pluginScripts.add(CJavascriptResources.BOOTSTRAP_FILEUPLOAD);
		pluginScripts.add(CJavascriptResources.CKEDITOR);
		pluginScripts.add(CJavascriptResources.CKEDITOR_JQUERY);

		if (ILanguageConstant.SK.equals(CSedSession.get().getLocale().getLanguage())) {
			pluginScripts.add(CJavascriptResources.MOMENT_SK);
			pluginScripts.add(CJavascriptResources.BOOTSTRAP_DATEPICKER_SK);
			pluginScripts.add(CJavascriptResources.BOOTSTRAP_DATERANGEPICKER_SK);
			pluginScripts.add(CJavascriptResources.NVD3_SK);
		} else {
			pluginScripts.add(CJavascriptResources.BOOTSTRAP_DATERANGEPICKER);
			pluginScripts.add(CJavascriptResources.NVD3_EN);
		}

		resourceConfiguration.addPluginScripts(pluginScripts);

		resourceConfiguration.addThemeScript(CJavascriptResources.SED_TABLE);
		resourceConfiguration.addThemeScript(CJavascriptResources.SED_MAIN);
		resourceConfiguration.addThemeScript(CJavascriptResources.SED_FORM);
		resourceConfiguration.addThemeScript(CJavascriptResources.SED_APP);

		resourceConfiguration.addInitializationCommand("Main.init();");
		resourceConfiguration.addInitializationCommand("FormElements.init();");
		resourceConfiguration.addInitializationCommand("SedApp.init();");
	}

	public CPageResourceConfiguration getResourceConfiguration() {
		return resourceConfiguration;
	}

	protected void initResources(CPageResourceConfiguration resourceConfiguration) {
	}

	private List<Component> getSubMenuComponents() {
		List<Component> subMenuComponents = new ArrayList<>();

		if (hasRights(CPasswordChangePage.class) && hasRole(IUserTypeCode.ID_CHANGE_PASSWORD)) {
			subMenuComponents.add(getSubMenuComponent(getString("page.passwordChange.label.title"), "fa-lock", CPasswordChangePage.class));
		}

		if (hasRights(CPinGenerationPage.class)) {
			subMenuComponents.add(getSubMenuComponent(getString("page.pinGeneration.button.title"), "fa-key", CPinGenerationPage.class));
		}

		if (hasRights(CUserDetailPage.class)) {
			subMenuComponents.add(getSubMenuComponent(getString("page.userDetail.label.title"), "fa-user", CUserDetailPage.class));
		}

		if (hasRights(CClientDetailPage.class)) {
			subMenuComponents.add(getSubMenuComponent(getString("page.clientDetail.label.title"), "fa-info", CClientDetailPage.class));
		}

		if (hasRights(CAppInfoPage.class)) {
			subMenuComponents.add(getSubMenuComponent(getString("page.appInfo.label.title"), "fa-info", CAppInfoPage.class));
		}

		if (hasRights(CJiraTokenGenerationPage.class) && this.getUser().getJiraAccessToken() == null 
				&& this.getUser().getJiraTokenGeneration().booleanValue() && this.getUser().getClientInfo() != null
				&& this.getUser().getClientInfo().isQbsw()) {
			// tlačidlo Generovanie JIRA tokenu
			subMenuComponents.add(getSubMenuComponent(getString("page.jiraTokenGeneration.button.title"), "fa-key", CJiraTokenGenerationPage.class));
		}
		
		return subMenuComponents;
	}

	private Component getSubMenuComponent(String title, String icon, final Class<? extends AAuthenticatedPage> page) {
		AjaxFallbackLink<Object> subMenuItem = new AjaxFallbackLink<Object>("subMenuItem") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				setResponsePage(page);
			}
		};

		subMenuItem.setBody(Model.of("<div class='col-xs-5 col-lg-2'><button class='btn btn-icon btn-block space10'><i class='fa " + icon + "'></i>" + title + "</button></div>"));
		subMenuItem.setEscapeModelStrings(false);

		return subMenuItem;
	}

	private boolean hasRights(Class<? extends AAuthenticatedPage> page) {
		return page == null ? true : Session.get().getAuthorizationStrategy().isInstantiationAuthorized(page);
	}

	// SED-618 DASHBOARD - po stlaceni F5 refreshnut cely dashboard aj odpracovany cas
	@Override
	public void renderPage() {
		if (hasBeenRendered()) {
			setResponsePage(getPageClass(), getPageParameters());
		} else {
			super.renderPage();
		}
	}
}
