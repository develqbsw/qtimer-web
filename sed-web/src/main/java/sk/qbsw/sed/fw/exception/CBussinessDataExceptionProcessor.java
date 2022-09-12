package sk.qbsw.sed.fw.exception;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sk.qbsw.sed.client.response.EErrorCode;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.fw.component.IComponentContainer;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.fw.utils.CWicketUtils;
import sk.qbsw.sed.utils.CAuthenticatedSession;

public class CBussinessDataExceptionProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(CBussinessDataExceptionProcessor.class);

	private CBussinessDataExceptionProcessor() {
		// Auto-generated constructor stub
	}

	public static void process(CBussinessDataException exception, AjaxRequestTarget target, IComponentContainer container) {
		process(exception, container.getSession(), target, container.getPage(), container);
	}

	public static void process(CBussinessDataException exception, Session session, AjaxRequestTarget target, Page page) {
		process(exception, session, target, page, null);
	}

	public static void process(CBussinessDataException exception, Session session, AjaxRequestTarget target, Page page, IComponentContainer container) {
		if (exception != null && session != null) {
			LOGGER.info("CBussinessDataException --> serverCode: " + exception.getModel().getServerCode());

			if (EClientErrorCode.NEEDS_TO_LOGIN.equals(exception.getModel().getClientCode()) || EErrorCode.USER_NOT_LOGGED.toString().equals(exception.getModel().getServerCode())) {
				if (session instanceof CAuthenticatedSession) {
					session.invalidate();
				}
				throw new PageExpiredException("");

			}
			if (StringUtils.isNotBlank(exception.getModel().getDescription()) || StringUtils.isNotBlank(exception.getModel().getServerCode())) {
				StringBuilder message = new StringBuilder();

				if (exception.getModel().getServerCode() != null) {
					// specialny charakter # je ak chcem zo servra poslat argumenty pre chybovu hlasku
					String[] errorCode = exception.getModel().getServerCode().split(CClientExceptionsMessages.MESSAGE_ARGUMENTS_SEPARATOR);

					Object[] arguments = new Object[errorCode.length - 1];

					for (int i = 1; i < errorCode.length; i++) {
						arguments[i - 1] = errorCode[i];
					}

					message.append(MessageFormat.format(CStringResourceReader.read(errorCode[0]), arguments));
				}
				if (container != null) {
					container.error(message.toString());
				} else {
					session.error(message.toString());
				}
			} else {
				if (exception.getModel().getClientCode() != null) {
					session.error(page.getString(exception.getModel().getClientCode().getMessageKey()));
				}
			}
		}
		if (target != null) {
			if (container != null) {
				CWicketUtils.refreshFeedback(target, container);
			} else {
				CWicketUtils.refreshFeedback(target, page);
			}

		} else if (page != null) {
			// page refresh
		}
	}
}
