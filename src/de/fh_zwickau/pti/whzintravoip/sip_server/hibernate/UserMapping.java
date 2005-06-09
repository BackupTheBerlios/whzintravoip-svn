package de.fh_zwickau.pti.whzintravoip.sip_server.hibernate;

import net.sf.hibernate.Session;
import net.sf.hibernate.*;
import net.sf.hibernate.cfg.Configuration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;
import java.util.Iterator;

public class UserMapping {

    /**
     * Init the logger named PacketCaller.log
     */
    private static final Logger logger = Logger.getLogger("PacketCaller.log");


    public UserMapping() {
        PropertyConfigurator.configure("/log4j.properties");
    }

    public boolean mapUserObject(User aUser) throws Exception {
        Session session = null;
        try {
            // This step will read hibernate.cfg.xml and prepare hibernate for use
            SessionFactory sessionFactory = new Configuration().configure().
                                            buildSessionFactory();
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

    public User getUserWithIp(String userIP) throws Exception {
        Session session = null;
        Transaction trx = null;
        User user = null;
        logger.info("Gettin User from Database with IP: " + userIP);
        try {
            SessionFactory sessionFactory = new Configuration().configure().
                                            buildSessionFactory();
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
                } catch (HibernateException exRb) {}
            }
            throw new RuntimeException(ex.getMessage());
        } finally {
            session.flush();
            session.close();
        }
        return user;
    }
}
