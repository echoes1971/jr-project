package ch.rra.rprj.model;

import ch.rra.rprj.model.core.DBEntity;
import ch.rra.rprj.model.core.Group;
import ch.rra.rprj.model.core.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.SelectionQuery;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Hibernate DB Connection
 */
public class HDBConnection extends DBConnectionProvider {
    private static final Logger log = LogManager.getLogger(HDBConnection.class);

    protected SessionFactory sessionFactory;
    protected StandardServiceRegistry registry;

    private Session session;
//    private Transaction tx;

    EntityManager em;
    EntityTransaction etx;

    public SessionFactory getSessionFactory() { return sessionFactory; }

    public HDBConnection(Properties props) {
        session = null;
//        tx = null;
        em = null;
        etx = null;
    }
    public HDBConnection(String server, String user, String pwd, String dbname, String schema, boolean verbose) {
        super(server, user, pwd, dbname, schema, verbose);
    }

    public boolean connect() {
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/application.properties"));

            props.setProperty("hibernate.connection.username", props.getProperty("db.conn.user"));
            props.setProperty("hibernate.connection.password", props.getProperty("db.conn.pwd"));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        registry = new StandardServiceRegistryBuilder()
                .configure()
                .applySettings(props)
                .build();
        try {
            MetadataSources sources = new MetadataSources(registry);
            Metadata metadata = sources.getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch(Exception e) {
            System.out.println("**************************");
            System.out.println(e.getMessage());
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
            return false;
        }

        if(session==null) session = sessionFactory.openSession();
//        if(tx==null) tx = session.beginTransaction();

        return true;
    }
    public boolean disconnect() {
//        tx = null;
        if(em!=null) em.close();
        em = null;
        etx = null;
        if(session!=null) {
            session.close();
            session = null;
        }

        if(sessionFactory!=null) {
            sessionFactory.close();
        }
        if(registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        return true;
    }


    public boolean db_execute(String sql) {
        try {
//            int res = session.createMutationQuery(sql).executeUpdate();
            if(em==null) em = session.getEntityManagerFactory().createEntityManager();
            if(etx==null) etx = em.getTransaction();
            etx.begin();
            int res = em.createNativeQuery(sql).executeUpdate();
//            int res = session.createNativeQuery(sql).executeUpdate();
            etx.commit();
            log.debug("DBMgr.db_execute: res="+res);
        } catch (HibernateException he) {
            if(etx!=null) etx.rollback();
//            if(tx!=null) tx.rollback();
            he.printStackTrace();
            return false;
        } finally {
//            session.close();
        }
        return true;
    }
    public List<DBEntity> db_query(String sql, HashMap<String,Object> hm, Class klass, boolean initializeLazyObjects) {
        log.debug("db_query: sql="+sql);
        log.debug("db_query: hm="+hm);
        log.debug("db_query: klass="+klass);
        if(em==null) em = session.getEntityManagerFactory().createEntityManager();
        Query q = em.createNativeQuery(sql, klass==null ? DBEntity.class : klass);
//        if(klass!=null) q.addEntity(klass);
        if(hm!=null) {
            for (String k : hm.keySet()) {
                //logger.debug(k + ": " + hm.get(k) + " " + (hm.get(k)).getClass().getName());
                q.setParameter(k, hm.get(k));
            }
        }
        List<DBEntity> dbes = q.getResultList();
        // This to force the load of lazy objects :-(
        if(initializeLazyObjects)
            dbes.stream().forEach(Object::toString);
        return dbes;
    }

    public DBEntity refresh(DBEntity dbe) throws DBException {
        try {
            session.refresh(dbe);
//            session.flush();
        } catch (HibernateException he) {
            //he.printStackTrace();
            return null;
        } catch(EntityNotFoundException enfex) {
            // RRA: maybe because has already been refreshed in memory by the previous passage?
            // RRA: so i'll ignore it
            //enfex.printStackTrace();
            //return null;
        } finally {
//            session.close();
        }
        return dbe;
    }
    public DBEntity insert(DBEntity dbe, DBMgr dbMgr) throws DBException {
        if(etx==null || !etx.isActive()) { etx = em.getTransaction(); etx.begin(); }
//        if(tx==null || !tx.isActive()) tx = session.beginTransaction();
        try {
//            dbe.beforeInsert(dbMgr);
            session.persist(dbe);
//            dbe.afterInsert(dbMgr);
            etx.commit();
        } catch(PersistenceException pe) {
            pe.printStackTrace();
//            try {
//                session.merge(dbe);
//                tx.commit();
//            } catch (HibernateException he) {
                if (etx != null) etx.rollback();
//                he.printStackTrace();
//                return null;
//            }
            return null;
        } finally {
//            session.close();
        }
        return dbe;
    }
    public DBEntity update(DBEntity dbe, DBMgr dbMgr) throws DBException {
        if(em==null) em = session.getEntityManagerFactory().createEntityManager();
        if(etx==null || !etx.isActive()) { etx = em.getTransaction(); etx.begin(); }
//        if(tx==null || !tx.isActive()) tx = session.beginTransaction();
        try {
//            dbe.beforeUpdate(dbMgr);
            session.merge(dbe); //.update(dbe);
//            dbe.afterUpdate(dbMgr);
            etx.commit();
        } catch (HibernateException he) {
            if(etx!=null) etx.rollback();
            he.printStackTrace();
            return null;
        }
        return dbe;
    }
    public DBEntity delete(DBEntity dbe, DBMgr dbMgr) throws DBException {
        if(etx==null || !etx.isActive()) { etx = em.getTransaction(); etx.begin(); }
//        if(tx==null || !tx.isActive()) tx = session.beginTransaction();
        try {
//            dbe.beforeDelete(dbMgr);
            session.remove(dbe);
//            dbe.afterDelete(dbMgr);
            etx.commit();
        } catch (HibernateException he) {
            if(etx!=null) etx.rollback();
            he.printStackTrace();
            return null;
        }
        return dbe;
    }


    // **** FOR TESTING PURPOSES
    public int listUsers() {
        SelectionQuery query = session.createSelectionQuery("FROM User");
        List<User> users = (List<User>) query.list();
//        session.flush();
        for(User u : users) {
            System.out.println(u.toString());
        }
        System.out.println("Users: " + users.size());
        System.out.println();
        return users.size();
    }
    public int listGroups() {
        SelectionQuery query = session.createSelectionQuery("FROM Group");
        List<Group> dbes = (List<Group>) query.list();
        for(Group dbe : dbes) {
            System.out.println(dbe.toString());
        }
        System.out.println("Groups: " + dbes.size());
        System.out.println();
        return dbes.size();
    }
    public int listUsersGroups() {
        List objs = this.db_query("SELECT user_id, group_id FROM rprj_users_groups", new HashMap<String,Object>(), Object.class,false);
        printObjectList(objs);
        System.out.println("Objects: " + objs.size());
        System.out.println();
        return objs.size();
    }
    public void printObjectList(List objects) {
        try {
            for(Object[] obj : (List<Object[]>) objects) {
                System.out.print("Object:");
                for (Object o : obj) {
                    System.out.print(" " + o);
                }
                System.out.println();
            }
        } catch (ClassCastException cce) {
            try {
                for (HashMap hm : (List<HashMap>) objects) {
                    System.out.print("hm>");
                    for(Object k : hm.keySet()) {
                        System.out.print(" " + k + ": " + hm.get(k));
                    }
                    System.out.println();
                }
            } catch(ClassCastException cce2) {
                for (Object obj : objects) {
                    System.out.println("obj> " + obj);
                }
            }
        }
    }
}
