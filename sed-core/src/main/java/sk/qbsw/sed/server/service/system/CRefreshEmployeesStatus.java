package sk.qbsw.sed.server.service.system;

import org.springframework.beans.factory.annotation.Autowired;

import sk.qbsw.sed.client.service.business.IEmployeesStatusService;

public class CRefreshEmployeesStatus {

	@Autowired
	private IEmployeesStatusService employeesStatusService;

	public void processService() {
		employeesStatusService.clear();
	}
}
