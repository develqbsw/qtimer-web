package sk.qbsw.sed.server.service.system;

import org.springframework.stereotype.Service;

import sk.qbsw.sed.client.service.system.ISystemInfoService;

@Service(value = "systemInfoService")
public class CSystemInfoServiceImpl implements ISystemInfoService {
	
	private String version;
	private String environment;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
}
