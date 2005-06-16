package de.fh_zwickau.pti.whzintravoip.sip_server.hibernate;

import net.sf.hibernate.Session;
import net.sf.hibernate.*;
import net.sf.hibernate.cfg.Configuration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;
import java.util.Iterator;
import java.util.List;

/**
 *
 * <p>Überschrift: User Mapping Class</p>
 *
 * <p>Beschreibung: This Class will be used to map / get User Objects to / from
 * the Database via Hibernate.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Knight / Blurb
 * @version 0.0.2
 */

public class UserMapping {

    /**
     * Init the logger named PacketCaller.log
     */
    private static final Logger logger = Logger.getLogger("PacketCaller.log");

    /**
     * Session Factory
     */
    private SessionFactory sessionFactory;

    /**
     * init the Logger and init the sessionFactory
     */
    public UserMapping() {
        PropertyConfigurator.configure("/log4j.properties");
        try {
            logger.info("Initializing Hibernate!");
            sessionFactory = new Configuration().configure().
                             buildSessionFactory();
            logger.info("Finished Initializing Hibernate!");
        } catch (HibernateException ex) {
            logger.error("Error initializing Hibernate: " + ex.toString());
        }
    }

    /**
     * Map a User Object to Database.
     *
     * @param aUser User The user which you want to map.
     * @return boolean Is everything going right?
     * @throws Exception
     */
    public boolean mapUserObject(User aUser) throws Exception {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            //Create new instance of Contact and set values in it by reading them from form object
            logger.info("Inserting User to Database");
            session.save(aUser);
            logger.info("Inserting User is Done!");
        } catch (Exception ex) {
            logger.error("Error during Save: " + ex.toString());
            return false;
        } finally {
            // Actual contact insertion will happen at this step
            session.flush();
            session.close();
        }
        return true;
    }

    /**
     * Get the User Object from database specified by his userIP.
     *
     * @param userIP String The userIP from the desired User Object
     * @return User The desired User Object
     * @throws Exception
     */
    public User getUserWithIp(String userIP) throws Exception {
        Session session = null;
        Transaction trx = null;
        User user = null;
        logger.info("Gettin User from Database with IP: " + userIP);
        try {
            session = sessionFactory.openSession();
            trx = session.beginTransaction();
            String hql =
                    "select user from User as user where user.userip = :userip";
            Query query = session.createQuery(hql);
            query.setString("userip", userIP);
            Iterator it = query.iterate();
            if (it.hasNext()) {
                user = (User) it.next();
            }
            trx.commit();
        } catch (HibernateException ex) {
            if (trx != null) {
                try {
                    trx.rollback();
                } catch (HibernateException exRb) {
                    logger.error("Error during getUserWithIP: " + exRb.toString());
                    return null;
                }
            }
            return null;
        } finally {
            session.flush();
            session.close();
        }
        logger.info("Getting User successful!");
        return user;
    }

    /**
     * Update a UserObject to a new State.
     *
     * @param userIP String The userIP of the UserObject you want to update.
     * @param newState String The desired new state of the UserObject
     * @return boolean Is everything going right?
     * @throws Exception
     */
    public boolean updateUserWithIP(String userIP, String newState)
            throws Exception
    {
        Session session = null;
        Transaction trx = null;
        logger.info("Update User from Database with IP: " + userIP + " to state: " + newState);
        try {
            session = sessionFactory.openSession();
            trx = session.beginTransaction();
            String hql =
                    "select user from User as user where user.userip = :userip";
            Query query = session.createQuery(hql);
            query.setString("userip", userIP);
            Iterator it = query.iterate();
            if (it.hasNext()) {
                User user = (User) it.next();
                user.setStatus(newState);
                session.update(user);
            }
            trx.commit();
        } catch (HibernateException ex) {
            if (trx != null) {
                try {
                    trx.rollback();
                } catch (HibernateException exRb) {
                    logger.error("Error during updateUserWithIP: " + exRb.toString());
                    return false;
                }
            }
            return false;
        } finally {
            session.flush();
            session.close();
        }
        logger.info("Getting User successful!");
        return true;
    }

    /**
     * Get all registered Users from Database.
     *
     * @return List The List of the registered Users
     * @throws Exception If Something is going wrong.
     */
    public List getAllUsers() throws Exception
    {
        Session session = null;
        Transaction trx = null;
        logger.info("Get all registered Users!");
        List userList;
        try {
            session = sessionFactory.openSession();
            trx = session.beginTransaction();
            userList = session.find("from User");
            trx.commit();
        } catch (HibernateException ex) {
            if (trx != null) {
                try {
                    trx.rollback();
                } catch (HibernateException exRb) {
                    logger.error("Error during updateUserWithIP: " + exRb.toString());
                    return null;
                }
            }
            return null;
        } finally {
            session.flush();
            session.close();
        }
        logger.info("Getting User successful!");
        return userList;
    }
}
