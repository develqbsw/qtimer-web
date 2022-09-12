package sk.qbsw.sed.server.service.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.service.business.IOrganizationTreeService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.IOrganizationTreeDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.IViewOrganizationTreeDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.tree.org.CViewOrganizationTreeNode;

/**
 * Organization Tree service.
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@Service(value = "organizationTreeService")
public class COrganizationTreeServiceImpl implements IOrganizationTreeService {
	
	@Autowired
	private IClientDao clientDao;

	@Autowired
	private IOrganizationTreeDao treeDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IViewOrganizationTreeDao viewOrgTreeDao;

	@Transactional(readOnly = true)
	@Override
	public List<CViewOrganizationTreeNodeRecord> loadTreeByClient(final Long clientId, final Boolean onlyValid) {
		List<CViewOrganizationTreeNodeRecord> retVal = new ArrayList<>();
		List<CViewOrganizationTreeNode> list = this.viewOrgTreeDao.findByClientValidity(clientId, onlyValid);

		for (CViewOrganizationTreeNode org : list) {
			retVal.add(org.convert());
		}

		return retVal;
	}

	@Transactional(readOnly = true)
	@Override
	public List<CViewOrganizationTreeNodeRecord> loadTreeByClientUser(final Long clientId, final Long userId, final Boolean onlyValid, final Boolean withoutMe) {
		CUser adminUser = this.userDao.findClientAdministratorAccount(clientId);
		List<CViewOrganizationTreeNodeRecord> retVal = new ArrayList<>();
		List<CViewOrganizationTreeNode> list;

		// check administrator request
		if (userId.equals(adminUser.getId())) {
			// use loadTreeByClient method
			list = this.viewOrgTreeDao.findByClientValidity(clientId, onlyValid);
		} else {
			list = this.viewOrgTreeDao.findByClientUserValidity(clientId, userId, onlyValid, withoutMe);
		}

		for (CViewOrganizationTreeNode org : list) {
			retVal.add(org.convert());
		}

		return retVal;
	}

	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	@Override
	public void move(final Long treeNodeFrom, final Long treeNodeTo, final String mode, final Date timestamp) throws CBusinessException {
		// client to change
		final CClient client = this.treeDao.findById(treeNodeFrom).getClient();

		// check timestamp validity
		if (timestamp.getTime() < client.getChangeTime().getTimeInMillis()) {
			throw new CBusinessException(CClientExceptionsMessages.OLD_RECORD_SHOWN);
		}

		// the move
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		this.treeDao.move(treeNodeFrom, treeNodeTo, mode, loggedUser.getUserId());

		// changes time of last change
		client.setChangedBy(this.userDao.findById(loggedUser.getUserId()));
		client.setChangeTime(Calendar.getInstance());
		this.clientDao.saveOrUpdate(client);
	}
}
