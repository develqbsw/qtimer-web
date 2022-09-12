package sk.qbsw.sed.fw.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.stereotype.Service;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.communication.service.impl.CUserClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.web.ui.CSedSession;

@Service
public class CCacheUtils {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private CUserClientService userService;

	private final Logger logger = Logger.getLogger(CCacheUtils.class.getName());

	public static final String CACHE_ACTIVITY = "validActivitiesForUser";
	public static final String CACHE_ACTIVITY_WORKING = "validWorkingActivitiesForUser";
	public static final String CACHE_PROJECT = "validProjectsForUser";

	public void deleteCacheForOrg(String cacheName) {

		Cache cache = cacheManager.getCache(cacheName);
		if (cache instanceof EhCacheCache) {
			EhCacheCache cacheE = (EhCacheCache) cache;

			for (Long userId : getAllUsersForOrg()) {
				cacheE.getNativeCache().remove(userId);
			}
		}
	}

	private List<Long> getAllUsersForOrg() {
		List<Long> users = new ArrayList<>();
		try {
			List<CCodeListRecord> l = this.userService.getAllValidEmployees();
			CLoggedUserRecord loggedUser = CSedSession.get().getUser();

			for (CCodeListRecord record : l) {
				users.add(record.getId());
			}

			if (loggedUser.getRoles().contains(IUserTypeCode.ID_ORG_ADMIN)) {// admin nie je employee, preto ho treba manualne pridat, aby sa cache refreshla aj prenho
				users.add(loggedUser.getUserId());
			}

		} catch (CBussinessDataException e) {
			logger.error("Failed to load users, therefore cache will not be deleted.", e);
		}
		return users;
	}
}
