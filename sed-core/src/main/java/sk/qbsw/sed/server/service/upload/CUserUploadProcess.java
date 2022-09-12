package sk.qbsw.sed.server.service.upload;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.qbsw.sed.client.model.IUserTypes;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.service.business.IUserService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.client.ui.screen.codelist.upload.IUploadConstant;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

/**
 * 
 * @author rosenberg
 * 
 */
@Service
public class CUserUploadProcess extends AUploadProcess {
	
	@Autowired
	private IUserService userService;

	/**
	 * values[0] - the employee code values[1] - the employee name values[2] - the
	 * employee project name values[3] - the employee project code values[4] - the
	 * employee leader status: ano /nie values[5] - the employee email
	 */
	@Override
	protected String writeItemData(Object[] values, List<Long> objectIds) {
		CUserDetailRecord record = new CUserDetailRecord();

		try {

			if (values.length < 6 || values[5] == null || "".equals((String) values[5])) {
				return IUploadConstant.UPLOAD_RESULT_ERR2 + "  chybný záznam: " + ((String) values[1]).trim();
			}

			// fk_user_type numeric(10) NOT NULL, -ok
			// c_login character varying(20) NOT NULL, -ok
			// c_password character varying(100) NOT NULL, -ok
			// c_name character varying(50) NOT NULL, -ok
			// c_surname character varying(50) NOT NULL, -ok
			// c_flag_valid boolean NOT NULL,
			// fk_client numeric(10) NOT NULL,
			// c_contact_email character varying(50) NOT NULL,
			// c_emp_code character varying(10),
			// c_contact_phone character varying(50),
			// c_contact_mobile character varying(50),
			// c_contact_street character varying(50),
			// c_contact_street_number character varying(10),
			// c_contact_zip character varying(10),
			// c_contact_country character varying(50),
			// c_image_path character varying(1000),
			// c_note character varying(1000),
			// c_flag_main boolean NOT NULL,
			// fk_user_changedby numeric(10) NOT NULL,
			// c_datetime_changed timestamp without time zone NOT NULL,
			// fk_city numeric(10),
			// c_autologin_token character varying(50),

			List<String> nameParts = getNameParts(((String) values[1]).trim());

			String employeeCode = ((String) values[0]).trim();
			String surname = !nameParts.isEmpty() ? nameParts.get(0) : "";
			String name = nameParts.size() > 1 ? nameParts.get(nameParts.size() - 1) : "";
			String email = ((String) values[5]).trim();

			record.setEmployeeCode(employeeCode);
			record.setName(name);
			record.setSurname(surname);
			record.setEmail(email);

			// user type
			record.setUserType(IUserTypes.EMPLOYEE); // default value
			// record.setUserTypeString(name); // added by system!
			record.setIsMain(Boolean.FALSE); // default value
			record.setIsValid(Boolean.TRUE); // default value

			// !!! default login should be email value!
			record.setLogin(email);
			record.setEditTime(Boolean.FALSE); // default value
			record.setAllowedAlertnessWork(Boolean.FALSE); // default value

			this.userService.add(record);

		} catch (CBusinessException e) {
			// problem s duplicitnym pouzivatelom pre viacere organizacie
			// je rieseny na urovni dao metody,
			// hladajucej pouzivatela podla mena a nalezitosti k organizacii
			if (e.getMessage().indexOf(CClientExceptionsMessages.LOGIN_USED) > -1) {
				// nothing to do! - je tam a nic neriesime
			} else {
				return IUploadConstant.UPLOAD_RESULT_ERR4;
			}
		}

		return IUploadConstant.UPLOAD_RESULT_OK;
	}

	private List<String> getNameParts(String name) {
		List<String> parts = new ArrayList<>();

		String[] tmpParts = name.split("\\ ");
		if (tmpParts != null && tmpParts.length > 0) {
			for (int i = 0; i < tmpParts.length; i++) {
				String tmp = tmpParts[i].trim();
				if (!"".equals(tmp))
					parts.add(tmp);
			}
		}

		return parts;
	}

	@Override
	protected void postProcessingInErrorCase(List<Long> objectIds) throws CSecurityException {
		// nothing to do
	}

	@Override
	protected void preProcessing(List<Long> objectIds) throws CSecurityException {
		// nothing to do
	}
}
