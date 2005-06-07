package de.fh_zwickau.pti.whzintravoip.db_access;

import java.util.*;

import net.sf.hibernate.*;
import net.sf.hibernate.cfg.*;

/**
 * <p>Überschrift: </p>
 *
 * <p>Beschreibung: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class db_work {

    private SessionFactory sessionFactory;


    public db_work() {
        try {
            System.out.println("Initializing Hibernate");
            sessionFactory = new Configuration().configure().
                             buildSessionFactory();
            System.out.println("Finished Initializing Hibernate");
        } catch (HibernateException ex) {
            ex.printStackTrace();
            System.exit(5);
        }
    }

    public User findUser(String initial) {
        Session sess = null;
        Transaction trx = null;
        User user = null;
        try {
            sess = sessionFactory.openSession();
            trx = sess.beginTransaction();
            String hql =
                    "select user from User as user where user.initial = :initial";
            Query query = sess.createQuery(hql);
            query.setString("initial", initial);
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
            try {
                sess.close();
            } catch (Exception exCl) {}
        }
        return user;
    }

    public void addUser(User user) {
        Session sess = null;
        Transaction trx = null;
        try {
            sess = sessionFactory.openSession();
            trx = sess.beginTransaction();
            sess.save(user);
            trx.commit();
        } catch (HibernateException ex) {
            if (trx != null) {
                try {
                    trx.rollback();
                } catch (HibernateException exRb) {}
            }
            throw new RuntimeException(ex.getMessage());
        } finally {
            try {
                sess.close();
            } catch (Exception exCl) {}
        }
    }

    public void delUser(User user){
        Session sess = null;
        Transaction trx = null;
        try {
            sess = sessionFactory.openSession();
            trx = sess.beginTransaction();
            sess.delete(user);
            trx.commit();
        } catch (HibernateException ex) {
            if (trx != null) {
                try {
                    trx.rollback();
                } catch (HibernateException exRb) {}
            }
            throw new RuntimeException(ex.getMessage());
        } finally {
            try {
                sess.close();
            } catch (Exception exCl) {}
        }
    }

    public List getAllUser() {
        Session sess = null;
        Transaction trx = null;
        List users = null;
        try {
            sess = sessionFactory.openSession();
            trx = sess.beginTransaction();
            users = sess.find("from User");
            trx.commit();
        } catch (HibernateException ex) {
            if (trx != null) {
                try {
                    trx.rollback();
                } catch (HibernateException exRb) {}
            }
            throw new RuntimeException(ex.getMessage());
        } finally {
            try {
                sess.close();
            } catch (Exception exCl) {}
        }
        return users;
    }

    public void addBuddy(Buddys buddy) {
        Session sess = null;
        Transaction trx = null;
        try {
            sess = sessionFactory.openSession();
            trx = sess.beginTransaction();
            sess.save(buddy);
            trx.commit();
        } catch (HibernateException ex) {
            if (trx != null) {
                try {
                    trx.rollback();
                } catch (HibernateException exRb) {}
            }
            throw new RuntimeException(ex.getMessage());
        } finally {
            try {
                sess.close();
            } catch (Exception exCl) {}
        }
    }

    public List getBuddys(int user_id) {
        Session sess = null;
        Transaction trx = null;
        List users = null;
        Iterator itUser = null, itBuds = null;
        try {
            sess = sessionFactory.openSession();
            trx = sess.beginTransaction();
            String hql =
                    "select buddys from Buddys as buddys where buddys.user_id = '" +
                    user_id + "'";
            itBuds = sess.iterate(hql);
            while (itBuds.hasNext()) {
                Buddys bud = (Buddys) itBuds.next();
                if (users == null) {
                    users = new ArrayList();
                }
                hql = "select user from User as user where user.idUser = '" +
                      bud.getBuddy_id() + "'";
                itUser = sess.iterate(hql);
                if (itUser.hasNext()) {
                    users.add(itUser.next());
                }
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
            try {
                sess.close();
            } catch (Exception exCl) {}
        }
        return users;
    }

    public Buddys getBuddy(int userID, int buddyID) {
        Buddys bud = null;
        Session sess = null;
        Transaction trx = null;
        Iterator itBuds = null;
        try {
            sess = sessionFactory.openSession();
            trx = sess.beginTransaction();
            String hql =
                    "select buddys from Buddys as buddys where buddys.user_id = '" +
                    userID + "' and buddys.buddy_id = '" + buddyID + "'";
            itBuds = sess.iterate(hql);
            if (itBuds.hasNext()) {
                bud = (Buddys) itBuds.next();
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
            try {
                sess.close();
            } catch (Exception exCl) {}
        }
        return bud;
    }

    public void delBuddy(Buddys bud) {
        Session sess = null;
        Transaction trx = null;
        try {
            sess = sessionFactory.openSession();
            trx = sess.beginTransaction();
            sess.delete(bud);
            trx.commit();
        } catch (HibernateException ex) {
            if (trx != null) {
                try {
                    trx.rollback();
                } catch (HibernateException exRb) {}
            }
            throw new RuntimeException(ex.getMessage());
        } finally {
            try {
                sess.close();
            } catch (Exception exCl) {}
        }
    }
}
