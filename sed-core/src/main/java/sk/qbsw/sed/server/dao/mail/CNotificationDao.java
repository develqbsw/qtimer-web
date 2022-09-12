package sk.qbsw.sed.server.dao.mail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Repository;
import org.springframework.ui.velocity.VelocityEngineUtils;

import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.INotificationDao;
import sk.qbsw.sed.server.model.INotificationLabels;
import sk.qbsw.sed.server.model.INotificationValue;
import sk.qbsw.sed.server.model.codelist.CEmailWithLanguage;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.util.CLocaleUtils;

/**
 * Notification about password change in the
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Repository(value = "notificationDao")
public class CNotificationDao implements INotificationDao {
	
	/**
	 * Velocity reference
	 */
	@Autowired
	private VelocityEngine velocityEngine;

	private final Logger logger = Logger.getLogger(CNotificationDao.class.getName());
	
	private static final String UTF_8 = "UTF-8";
	
	private static final String NOTIFICATION_MAIL_SENDING_PROBLEM = "Notification mail sending problem";

	/**
	 * Mailing support
	 */
	@Autowired
	private JavaMailSender mailSender;

	/**
	 * Messaging support
	 */
	@Autowired
	private MessageSource messages;

	/**
	 * Reply addres of sent mail
	 */
	private String replyAddress;
	private String environmentPrefix;
	private String environment;

	/**
	 * Path to velocity template used for sending email
	 */
	private String velocityMacroPath;

	/**
	 * Path to velocity template used for sending email
	 */
	private String velocityMacroPathPasswdGenerated;

	/**
	 * Path to velocity template used for new user
	 */
	private String velocityMacroPathNewUserWithoutPassword;

	/**
	 * Path to velocity template used for sending email about missing employees
	 */
	private String velocityMacroPathMissingEmployees;

	/**
	 * Path to velocity template used for sending email about missing employees by
	 * some reasons
	 */
	private String velocityMacroPathMissingEmployeesByReason;

	/**
	 * Path to velocity template used for sending email about user requests
	 */
	private String velocityMacroPathUserRequestNotification;

	private String velocityMacroPathUserWarningNotification;

	// setters/getters
	public void setReplyAddress(String replyAddress) {
		this.replyAddress = replyAddress;
	}

	public String getVelocityMacroPathUserWarningNotification() {
		return velocityMacroPathUserWarningNotification;
	}

	public void setVelocityMacroPathUserWarningNotification(String velocityMacroPathUserWarningNotification) {
		this.velocityMacroPathUserWarningNotification = velocityMacroPathUserWarningNotification;
	}

	public String getReplyAddress() {
		return replyAddress;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getEnvironmentPrefix() {
		return environmentPrefix;
	}

	public void setEnvironmentPrefix(String environmentPrefix) {
		this.environmentPrefix = environmentPrefix;
	}

	public void setVelocityMacroPath(String velocityMacroPath) {
		this.velocityMacroPath = velocityMacroPath;
	}

	public String getVelocityMacroPath() {
		return velocityMacroPath;
	}

	public void setVelocityMacroPathPasswdGenerated(String velocityMacroPathPasswdGenerated) {
		this.velocityMacroPathPasswdGenerated = velocityMacroPathPasswdGenerated;
	}

	public String getVelocityMacroPathPasswdGenerated() {
		return velocityMacroPathPasswdGenerated;
	}

	public void setVelocityMacroPathMissingEmployees(String velocityMacroPathMissingEmployees) {
		this.velocityMacroPathMissingEmployees = velocityMacroPathMissingEmployees;
	}

	public String getVelocityMacroPathMissingEmployees() {
		return velocityMacroPathMissingEmployees;
	}

	public String getVelocityMacroPathMissingEmployeesByReason() {
		return velocityMacroPathMissingEmployeesByReason;
	}

	public void setVelocityMacroPathMissingEmployeesByReason(String velocityMacroPathMissingEmployeesByReason) {
		this.velocityMacroPathMissingEmployeesByReason = velocityMacroPathMissingEmployeesByReason;
	}

	public String getVelocityMacroPathUserRequestNotification() {
		return velocityMacroPathUserRequestNotification;
	}

	public void setVelocityMacroPathUserRequestNotification(String velocityMacroPathUserRequestNotification) {
		this.velocityMacroPathUserRequestNotification = velocityMacroPathUserRequestNotification;
	}

	public String getVelocityMacroPathNewUserWithoutPassword() {
		return velocityMacroPathNewUserWithoutPassword;
	}

	public void setVelocityMacroPathNewUserWithoutPassword(String velocityMacroPathNewUserWithoutPassword) {
		this.velocityMacroPathNewUserWithoutPassword = velocityMacroPathNewUserWithoutPassword;
	}

	/**
	 * @see INotificationDao#sendRenewPassword(CUser, String)
	 */
	@Override
	public void sendRenewPassword(CUser changedUser, String purePassword, String purePIN, boolean fromUserService) throws CBusinessException {
		try {
			final MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);
			helper.setFrom(replyAddress);

			// checks if to is filled
			String to = changedUser.getEmail();
			if (to != null && to.length() > 0) {
				helper.setTo(to);
			}

			Locale locale = CLocaleUtils.getLocale(changedUser.getLanguage());

			final Map<String, Object> model = new HashMap<>();
			// labels for translation
			this.setTemplateTranslations(model, locale);
			// values
			model.put(INotificationValue.LOGIN, changedUser.getLoginLong());
			model.put(INotificationValue.PASSWORD, purePassword);
			model.put(INotificationValue.PIN, purePIN != null ? purePIN : messages.getMessage("messages.renew_pasword.empty_pin", null, locale));

			final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, velocityMacroPath, model);

			// uses property file to read subject
			String subject;
			if (fromUserService) {
				subject = messages.getMessage("messages.login_information.subject", null, locale);
			} else {
				subject = messages.getMessage("messages.renew_pasword.subject", null, locale);
			}
			helper.setSubject(subject + getSubjectPostfix());
			helper.setText(body, true);

			mailSender.send(message);
		} catch (Exception e) {
			logger.info(NOTIFICATION_MAIL_SENDING_PROBLEM, e);
			throw new CBusinessException(CClientExceptionsMessages.MAIL_SENDING_ERROR);
		}
	}

	@Override
	public void sendNewUserWithoutPassword(CUser changedUser, String purePIN) throws CBusinessException {
		try {
			final MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);
			helper.setFrom(replyAddress);

			// checks if to is filled
			String to = changedUser.getEmail();
			if (to != null && to.length() > 0) {
				helper.setTo(to);
			}

			Locale locale = CLocaleUtils.getLocale(changedUser.getLanguage());

			final Map<String, Object> model = new HashMap<>();
			// labels for translation
			this.setTemplateTranslations(model, locale);
			// values
			model.put(INotificationValue.LOGIN, changedUser.getLoginLong());
			model.put(INotificationValue.PIN, purePIN != null ? purePIN : messages.getMessage("messages.renew_pasword.empty_pin", null, locale));

			final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, velocityMacroPathNewUserWithoutPassword, model);

			// uses property file to read subject
			String subject = messages.getMessage("messages.login_information.subject", null, locale);

			helper.setSubject(subject + getSubjectPostfix());
			helper.setText(body, true);

			mailSender.send(message);
		} catch (Exception e) {
			logger.info(NOTIFICATION_MAIL_SENDING_PROBLEM, e);
			throw new CBusinessException(CClientExceptionsMessages.MAIL_SENDING_ERROR);
		}
	}

	/**
	 * @see INotificationDao#sendGeneratedPassword(CUser, CUser)
	 */
	@Override
	public void sendGeneratedPassword(CUser admin, CUser receptionUser, String pureAPassword, String purePassword) throws CBusinessException {
		try {
			Locale locale = CLocaleUtils.getLocale(admin.getLanguage());
			final MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);
			helper.setFrom(replyAddress);

			// checks if to is filled
			String to = admin.getEmail();
			if (to != null && to.length() > 0) {
				helper.setTo(to);
			}

			final Map<String, Object> model = new HashMap<>();
			// labels for translation
			this.setTemplateTranslations(model, locale);
			// values
			model.put(INotificationValue.ALOGIN, admin.getLoginLong());
			model.put(INotificationValue.APASSWORD, pureAPassword);
			model.put(INotificationValue.LOGIN, receptionUser.getLoginLong());
			model.put(INotificationValue.PASSWORD, purePassword);

			// uses velocity to process template
			final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, velocityMacroPathPasswdGenerated, model);

			// uses property file to read subject
			String subject = messages.getMessage("messages.password_generated.subject", null, locale);
			helper.setSubject(subject + getSubjectPostfix());
			helper.setText(body, true);

			mailSender.send(message);
		} catch (Exception e) {
			logger.info(NOTIFICATION_MAIL_SENDING_PROBLEM, e);
			throw new CBusinessException(CClientExceptionsMessages.MAIL_SENDING_ERROR);
		}
	}

	/**
	 * @see INotificationDao#sendMissigEmployeesEmail(String, ArrayList, ArrayList,
	 *      ArrayList, ArrayList, ArrayList,ArrayList, ArrayList, ArrayList, String)
	 */
	@Override
	public void sendMissigEmployeesEmail(String email, List<String> holidayList, List<String> freePaidList, List<String> sickList, List<String> workbreakList, List<String> busTripList,
			List<String> staffTrainingList, List<String> workAtHomeList, List<String> noRequestList, String checkDatetime, List<String> outOfOfficeList, 
			List<String> doctorVisitList) throws CBusinessException {
		try {
			Locale locale = CLocaleUtils.getLocale();
			final MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);
			helper.setFrom(replyAddress);

			// checks if to is filled
			String to = email;
			if (to != null && to.length() > 0) {
				helper.setTo(to);
			}

			final Map<String, Object> model = new HashMap<>();
			// labels for translation
			this.setTemplateTranslations(model, locale);
			// values
			model.put(INotificationValue.CHECK_DATE, checkDatetime);
			model.put(INotificationValue.LIST_EMPLOYEES_ON_HOLIDAYS, holidayList);
			model.put(INotificationValue.LIST_EMPLOYEES_ON_FREE_PAID, freePaidList);
			model.put(INotificationValue.LIST_EMPLOYEES_SICKED, sickList);
			model.put(INotificationValue.LIST_EMPLOYEES_WORKBREAK, workbreakList);
			model.put(INotificationValue.LIST_EMPLOYEES_BUSTRIP, busTripList);
			model.put(INotificationValue.LIST_EMPLOYEES_STAFF_TRAINING, staffTrainingList);
			model.put(INotificationValue.LIST_EMPLOYEES_WORK_AT_HOME, workAtHomeList);
			model.put(INotificationValue.LIST_EMPLOYEES_OUT_OF_OFFICE, outOfOfficeList);
			model.put(INotificationValue.LIST_EMPLOYEES_DOCTOR_VISIT, doctorVisitList);
			model.put(INotificationValue.LIST_EMPLOYEES, noRequestList);

			// uses velocity to process template
			final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, velocityMacroPathMissingEmployeesByReason, model);

			// uses property file to read subject
			String subject = messages.getMessage("messages.missing_employees.subject", null, locale) + " " + checkDatetime;
			helper.setSubject(subject + getSubjectPostfix());
			helper.setText(body, true);

			mailSender.send(message);
		} catch (Exception e) {
			logger.info(NOTIFICATION_MAIL_SENDING_PROBLEM, e);
			throw new CBusinessException(CClientExceptionsMessages.MAIL_SENDING_ERROR);
		}
	}

	@Override
	public void sendNotificationAboutUserRequest(CEmailWithLanguage emailWithLanguage, Map<String, Object> model, String attachment, String attachmentFilename) throws CBusinessException {
		try {
			Locale locale = CLocaleUtils.getLocale(emailWithLanguage.getLanguage());
			final MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);
			helper.setFrom(replyAddress);

			helper.addTo(emailWithLanguage.getEmail());

			model.put("LINKS", "");
			// labels for translation
			this.setTemplateTranslations(model, locale);
			// BODY: uses velocity to process template
			final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, velocityMacroPathUserRequestNotification, model);

			// SUBJECT: uses property file to read subject
			String subject = messages.getMessage("messages.email_application_subject", null, locale) + " " + model.get(INotificationValue.REQUEST_TITLE);
			helper.setSubject(subject + getSubjectPostfix());
			helper.setText(body, true);

			if (attachment != null) {
				InputStream stream = new ByteArrayInputStream(attachment.getBytes(StandardCharsets.UTF_8));
				helper.addAttachment(attachmentFilename, new ByteArrayResource(IOUtils.toByteArray(stream)));
				stream.close();
			}

			mailSender.send(message);
		} catch (Exception e) {
			logger.info(NOTIFICATION_MAIL_SENDING_PROBLEM, e);
			throw new CBusinessException(CClientExceptionsMessages.MAIL_SENDING_ERROR);
		}

	}

	@Override
	public void sendNotificationAboutUserRequestWithLinks(CEmailWithLanguage emailWithLanguage, Map<String, Object> model, String id, String code, List<String> subordinatesWithApprovedRequest)
			throws CBusinessException {
		try {
			Locale locale = CLocaleUtils.getLocale(emailWithLanguage.getLanguage());
			final MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);
			helper.setFrom(replyAddress);

			helper.addTo(emailWithLanguage.getEmail());

			String links = "<br>";

			String urlApprove = environmentPrefix + "/approveRequest?requestId=" + id + "&requestCode=" + code + "&locale=" + emailWithLanguage.getLanguage();
			String urlReject = environmentPrefix + "/rejectRequest?requestId=" + id + "&requestCode=" + code + "&locale=" + emailWithLanguage.getLanguage();

			links += "<table><tr style=\"height: 20px;\"><td align=\"center\">&nbsp; &nbsp; &nbsp; &nbsp;&nbsp;"
					+ "<a style=\"border:1px solid; border-color: #000000; font-family: Verdana, Arial, Helvetica, sans-serif; font-size:10px; font-weight: bold; width:90;height:20; background-color:#f0f0f0;\" href=\""
					+ urlApprove + "\">" + "&nbsp;&nbsp;" + messages.getMessage("messages.approveRequestFromLink", null, locale) + "&nbsp;&nbsp;" + "</a>&nbsp;"
					+ "<a style=\"border:1px solid; border-color: #000000; font-family: Verdana, Arial, Helvetica, sans-serif; font-size:10px; font-weight: bold; width:90;height:20px; background-color:#f0f0f0;\" href=\""
					+ urlReject + "\">" + "&nbsp;&nbsp;" + messages.getMessage("messages.rejectRequestFromLink", null, locale) + "&nbsp;&nbsp;" + "</a>" + "</td></tr></table>";

			links += "<br>";

			if (!subordinatesWithApprovedRequest.isEmpty()) {
				links += "<table><tr style=\"height: 20px;\"><td style=\"text-decoration:underline;\">" + messages.getMessage("messages.subordinatesWithApprovedRequest", null, locale)
						+ "</td></tr></table>";
				links += "<table>";

				for (String name : subordinatesWithApprovedRequest) {
					links += name;
				}
				links += "</table>";
			}

			model.put("LINKS", links);

			// labels for translation
			this.setTemplateTranslations(model, locale);
			// BODY: uses velocity to process template
			String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, velocityMacroPathUserRequestNotification, model);

			// SUBJECT: uses property file to read subject
			String subject = messages.getMessage("messages.email_application_subject", null, locale) + " " + model.get(INotificationValue.REQUEST_TITLE);
			helper.setSubject(subject + getSubjectPostfix());
			helper.setText(body, true);

			mailSender.send(message);
		} catch (Exception e) {
			logger.info(NOTIFICATION_MAIL_SENDING_PROBLEM, e);
			throw new CBusinessException(CClientExceptionsMessages.MAIL_SENDING_ERROR);
		}
	}

	/**
	 * @param model  input/output model
	 * @param locale input locale
	 */
	private void setTemplateTranslations(Map<String, Object> model, Locale locale) {
		model.put(INotificationLabels.LABEL_ABSENTEES_NO_REQUEST, messages.getMessage("label.absentees_no_request", null, locale));
		model.put(INotificationLabels.LABEL_BARRIERS_TO_WORK, messages.getMessage("label.barriers_to_work", null, locale));
		model.put(INotificationLabels.LABEL_BEST_REGARDS, messages.getMessage("label.best_regards", null, locale));
		model.put(INotificationLabels.LABEL_BUSINESS_TRIP, messages.getMessage("label.business_trip", null, locale));
		model.put(INotificationLabels.LABEL_DATE, messages.getMessage("label.date", null, locale));
		model.put(INotificationLabels.LABEL_DATES, messages.getMessage("label.dates", null, locale));
		model.put(INotificationLabels.LABEL_EMPLOYEE, messages.getMessage("label.employee", null, locale));
		model.put(INotificationLabels.LABEL_HOLIDAY, messages.getMessage("label.holiday", null, locale));
		model.put(INotificationLabels.LABEL_LIST_ABSENTEES, messages.getMessage("label.list_absentees", null, locale));
		model.put(INotificationLabels.LABEL_LIST_ABSENTEES_DATE_TIME, messages.getMessage("label.list_absentees_date_time", null, locale));
		model.put(INotificationLabels.LABEL_LOGIN, messages.getMessage("label.login", null, locale));
		model.put(INotificationLabels.LABEL_LOGIN_DATA_ADMIN, messages.getMessage("label.login_data_admin", null, locale));
		model.put(INotificationLabels.LABEL_LOGIN_DATA_RECEPTION, messages.getMessage("label.login_data_reception", null, locale));
		model.put(INotificationLabels.LABEL_NUM_WORK_DAYS, messages.getMessage("label.num_work_days", null, locale));
		model.put(INotificationLabels.LABEL_PASSWORD, messages.getMessage("label.password", null, locale));
		model.put(INotificationLabels.LABEL_PIN, messages.getMessage("label.pin", null, locale));
		model.put(INotificationLabels.LABEL_PLACE, messages.getMessage("label.place", null, locale));
		model.put(INotificationLabels.LABEL_REASON, messages.getMessage("label.reasonname", null, locale));
		model.put(INotificationLabels.LABEL_NOTE, messages.getMessage("label.note", null, locale));
		model.put(INotificationLabels.LABEL_REPLWORK, messages.getMessage("label.replwork", null, locale));
		model.put(INotificationLabels.LABEL_RQ_NEW_STATUS, messages.getMessage("label.rq_new_status", null, locale));
		model.put(INotificationLabels.LABEL_RQ_OLD_STATUS, messages.getMessage("label.rq_old_status", null, locale));
		model.put(INotificationLabels.LABEL_RQ_TYPE, messages.getMessage("label.rq_type", null, locale));
		model.put(INotificationLabels.LABEL_SICKLEAVE, messages.getMessage("label.sickleave", null, locale));
		model.put(INotificationLabels.LABEL_SYSTEM_EES, messages.getMessage("label.system_ees", null, locale));
		model.put(INotificationLabels.LABEL_YEAR, Calendar.getInstance().get(Calendar.YEAR));
		model.put(INotificationLabels.LABEL_TRAINING, messages.getMessage("label.training", null, locale));
		model.put(INotificationLabels.LABEL_USER_PASSWORD_PIN_IN_EES, messages.getMessage("label.user_password_pin_in_ees", null, locale));
		model.put(INotificationLabels.LABEL_USER_PIN_IN_EES, messages.getMessage("label.user_pin_in_ees", null, locale));
		model.put(INotificationLabels.LABEL_WORK_FROM_HOME, messages.getMessage("label.work_from_home", null, locale));
		model.put(INotificationLabels.LABEL_REPLACEMENT, messages.getMessage("label.replacement", null, locale));
		model.put(INotificationLabels.LABEL_OUT_OF_OFFICE, messages.getMessage("label.out_of_office", null, locale));
		model.put(INotificationLabels.LABEL_DOCTOR_VISIT, messages.getMessage("label.doctor_visit", null, locale));
		model.put(INotificationLabels.LABEL_HALFDAY, messages.getMessage("label.halfday", null, locale));
		
		model.put(INotificationLabels.VALUE_YES, messages.getMessage("value.yes", null, locale));
		model.put(INotificationLabels.VALUE_NO, messages.getMessage("value.no", null, locale));
	
	}

	@Override
	public void sendWarningToEmployee(List<CEmailWithLanguage> to, CUser warned, Map<String, Object> model) throws CBusinessException {
		try {
			Locale locale = CLocaleUtils.getLocale(warned.getLanguage());
			final MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);
			helper.setFrom(replyAddress);

			for (CEmailWithLanguage e : to) {
				helper.addTo(e.getEmail());
			}

			helper.addCc(warned.getEmail());

			// labels for translation
			this.setTemplateTranslations(model, locale);
			// BODY: uses velocity to process template
			final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, velocityMacroPathUserWarningNotification, model);

			Format formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

			// SUBJECT: uses property file to read subject
			String subject = messages.getMessage("messages.nonSignedOffEmployeesSubject", null, locale) + ", " + formatter.format(new Date());
			helper.setSubject(subject + getSubjectPostfix());
			helper.setText(body, true);

			mailSender.send(message);
		} catch (Exception e) {
			logger.info(NOTIFICATION_MAIL_SENDING_PROBLEM, e);
			throw new CBusinessException(CClientExceptionsMessages.MAIL_SENDING_ERROR);
		}
	}

	private String getSubjectPostfix() {
		return "prod".equals(environment) ? "" : " - TEST";
	}
}
