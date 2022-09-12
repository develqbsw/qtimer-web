package sk.qbsw.sed.server.service.system;

import sk.qbsw.sed.server.service.security.ICodeGenerator;

public interface IAppCodeGenerator extends ICodeGenerator {

	public String generatePinSalt();

	public String generatePasswordSalt();

	public String generatePassword();
}
