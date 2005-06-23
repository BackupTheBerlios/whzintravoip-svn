package de.fh_zwickau.pti.whzintravoip.sip_server;

import java.util.Properties;

import net.sf.ehcache.CacheManager;
import net.sf.hibernate.cache.CacheProvider;
import net.sf.hibernate.cache.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.util.Set;
import java.util.Iterator;



public class UserCacheProvider implements CacheProvider {

    private CacheManager manager;

    private static final Logger log = Logger.getLogger("PacketCaller.log");

    public UserCacheProvider() {
        PropertyConfigurator.configure("/log4j.properties");
    }

    public Cache buildCache(String name, Properties properties) throws CacheException {

        try {
            log.info("Build Cache!");
            net.sf.ehcache.Cache cache = manager.getCache(name);
            log.info("Cache is: " + cache.toString());
            if (cache == null) {

                log.warn("Could not find configuration [" + name + "]; using defaults.");
                log.info("Add Cache with Name" + name);
                manager.addCache(name);
                cache = manager.getCache(name);
                log.info("Cache ID: " + cache.toString());
                log.debug("started EHCache region: " + name);
            }
            log.info("Return the cache object!");
            return new EhCache(cache);
            }
        catch (net.sf.ehcache.CacheException e) {
            log.error("Error during build cache: " + e.toString());
            throw new CacheException(e);
        }

    }

    /**
     * Returns the next timestamp.
     */
    public long nextTimestamp() {
        return Timestamper.next();
    }

    /**
     * Callback to perform any necessary initialization of the underlying cache implementation
     * during SessionFactory construction.
     *
     * @param properties current configuration settings.
     */
    public void start(Properties properties) throws CacheException {
        try {
            log.info("Create Cache Manager!");
            /**
            Set s = properties.entrySet();
            for (Iterator i = s.iterator(); i.hasNext(); ) {
                Object key = i.next();
                log.info(key.toString() + " : " + properties.get(key));
            }*/
            manager = CacheManager.create();

        } catch (net.sf.ehcache.CacheException e) {
            log.error("Error during start Cache: " + e.toString());
            throw new CacheException(e);
        }
    }

    /**
     * Callback to perform any necessary cleanup of the underlying cache implementation
     * during SessionFactory.close().
     */
    public void stop() {
        if (manager != null) {
            manager.shutdown();
            manager = null;
        }
    }

}
