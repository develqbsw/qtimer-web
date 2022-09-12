package sk.qbsw.sed.server.service.system;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;

import sk.qbsw.sed.client.model.ILanguageConstant;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.nameday.INameDayService;

/**
 * class for refresh name day cache called by trigger every midnight
 * 
 * @author lobb
 *
 */
public class CRefreshNamesdayCache implements IRefreshNamesdayCache {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private INameDayService nameDayService;

	@Override
	public void processService() {

		// delete cache
		Cache cache = cacheManager.getCache("namesdayCache");
		if (cache instanceof EhCacheCache) {
			EhCacheCache cacheE = (EhCacheCache) cache;
			cacheE.getNativeCache().removeAll();
		}

		// put values for the day in cache
		try {
			nameDayService.getNamesday(ILanguageConstant.SK);
			nameDayService.getNamesday(ILanguageConstant.EN);
		} catch (CBusinessException e) {
			Logger.getLogger(CRefreshNamesdayCache.class).error("Getting of namesday failed!", e);
		}
	}
}
