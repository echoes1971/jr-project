package ch.rra.rprj.model;

import org.hibernate.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.Transaction;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;

public class DBMgr {
    private SessionFactory sessionFactory;

    public DBMgr() {}

    public boolean setUp() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            return true;
        } catch(Exception e) {
            System.out.println("**************************");
            System.out.println(e);
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
            return false;
        }

    }

    public void tearDown() throws Exception {
        if(sessionFactory!=null) {
            sessionFactory.close();
        }
    }

    public SessionFactory getSessionFactory() { return sessionFactory; }

    public int listUsers() {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM User");
        List<User> users = (List<User>) query.list();
        for(User u : users) {
            System.out.println(u.toString());
        }
        System.out.println("Users: " + users.size());
        System.out.println("");
        session.close();
        return users.size();
    }

    public int listGroups() {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM Group");
        List<Group> dbes = (List<Group>) query.list();
        for(Group dbe : dbes) {
            System.out.println(dbe.toString());
        }
        System.out.println("Groups: " + dbes.size());
        System.out.println("");
        session.close();
        return dbes.size();
    }

    public boolean db_execute(String sql) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            int res = session.createSQLQuery(sql).executeUpdate();
            System.out.println("DBMgr.db_execute: res="+res);
            tx.commit();
        } catch (HibernateException he) {
            if(tx!=null) tx.rollback();
            he.printStackTrace();
            return false;
        } finally {
            session.close();
        }
        return true;
    }
    public List db_query(String sql) {
        Session session = sessionFactory.openSession();
        List objs = (List) session.createSQLQuery(sql).list();
        session.close();
        return objs;
    }

    public int listUsersGroups() {
        List objs = this.db_query("SELECT user_id, group_id FROM rprj_users_groups");
        printObjectList(objs);
        System.out.println("Objects: " + objs.size());
        System.out.println("");
        return objs.size();
    }

    public void printObjectList(List objects) {
        try {
            for(Object[] obj : (List<Object[]>) objects) {
                System.out.print("Object:");
                for (Object o : obj) {
                    System.out.print(" " + o);
                }
                System.out.println("");
            }
        } catch (ClassCastException cce) {
            try {
                for (HashMap hm : (List<HashMap>) objects) {
                    System.out.print("hm>");
                    for(Object k : hm.keySet()) {
                        System.out.print(" " + k + ": " + hm.get(k));
                    }
                    System.out.println("");
                    //System.out.println("hm> " + hm);
                }
            } catch(ClassCastException cce2) {
                for (Object obj : objects) {
                    System.out.println("obj> " + obj);
                }
            }
        }
    }

    public DBEntity refresh(DBEntity dbe) throws DBException {
        Session session = sessionFactory.openSession();
        try {
            session.refresh(dbe);
        } catch (HibernateException he) {
            he.printStackTrace();
            return null;
        } catch(EntityNotFoundException enfex) {
            // RRA: maybe because has already been refreshed in memory by the previous passage?
            // RRA: so i'll ignore it
            enfex.printStackTrace();
            //return null;
        } finally {
            session.close();
        }
        return dbe;
    }

    public DBEntity insert(DBEntity dbe) throws DBException {
        dbe.beforeInsert(this);
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(dbe);
            tx.commit();
        } catch (HibernateException he) {
            if(tx!=null) tx.rollback();
            he.printStackTrace();
            return null;
        } finally {
            session.close();
        }
        dbe.afterInsert(this);
        return dbe;
    }

    public DBEntity update(DBEntity dbe) throws DBException {
        dbe.beforeUpdate(this);
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.update(dbe);
            tx.commit();
        } catch (HibernateException he) {
            if(tx!=null) tx.rollback();
            he.printStackTrace();
            return null;
        } finally {
            session.close();
        }
        dbe.afterUpdate(this);
        return dbe;
    }

    public DBEntity delete(DBEntity dbe) throws DBException {
        dbe.beforeDelete(this);
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.delete(dbe);
            tx.commit();
        } catch (HibernateException he) {
            if(tx!=null) tx.rollback();
            he.printStackTrace();
            return null;
        } finally {
            session.close();
        }
        dbe.afterDelete(this);
        return dbe;
    }
}
