package sk.qbsw.sed.server.service.system;

import org.springframework.stereotype.Component;

import sk.qbsw.sed.server.service.security.CCodeGenerator;

@Component(value = "appCodeGenerator")
public class CAppCodeGenerator extends CCodeGenerator implements IAppCodeGenerator {

	public String generatePasswordSalt() {
		return generateMessage(3, 1);
	}

	public String generatePinSalt() {
		return generateMessage(3, 1);
	}

	public String generatePassword() {
		return generateMessage(6, 1);
	}
}
