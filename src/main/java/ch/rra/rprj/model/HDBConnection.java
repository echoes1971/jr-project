package ch.rra.rprj.model;

import ch.rra.rprj.model.core.DBEObject;
import ch.rra.rprj.model.core.DBEntity;
import ch.rra.rprj.model.core.Group;
import ch.rra.rprj.model.core.User;
import jakarta.persistence.*;
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
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Hibernate DB Connection
 *
 * See:
 *  https://thorben-janssen.com/flushmode-in-jpa-and-hibernate/
 *  https://vladmihalcea.com/hibernate-hbm2ddl-auto-schema/
 *  https://stackoverflow.com/questions/27899443/detached-entity-passed-to-persist-jpa-inheritance
 *
 */
public class HDBConnection extends DBConnectionProvider {
    private static final Logger log = LogManager.getLogger(HDBConnection.class);

    protected Properties props;
    protected SessionFactory sessionFactory;
    protected StandardServiceRegistry registry;

    private Session session;

    private EntityManager em;
    private EntityTransaction etx;

    public SessionFactory getSessionFactory() { return sessionFactory; }

    public HDBConnection(Properties props) {
        this.props = props;
        session = null;
//        tx = null;
        em = null;
        etx = null;
    }
    public HDBConnection(String server, String user, String pwd, String dbname, String schema, boolean verbose) {
        super(server, user, pwd, dbname, schema, verbose);
    }

    public boolean connect() {
//        Properties props = new Properties();
//        try {
//            props.load(getClass().getResourceAsStream("/application.properties"));
//
//            props.setProperty("hibernate.connection.username", props.getProperty("db.conn.user"));
//            props.setProperty("hibernate.connection.password", props.getProperty("db.conn.pwd"));
//        } catch(IOException ioe) {
//            ioe.printStackTrace();
//            return false;
//        }
        Properties props = this.props;
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

        if(session==null) {
            session = sessionFactory.openSession();
//            session.setFlushMode(FlushModeType.COMMIT);
        }

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
        if(em==null) {
            em = session.getEntityManagerFactory().createEntityManager();
            etx = em.getTransaction(); etx.begin();
        }
        if(etx==null || !etx.isActive()) { etx = em.getTransaction(); etx.begin(); }
        try {
            int res = em.createNativeQuery(sql).executeUpdate();
            etx.commit();
            log.debug("DBMgr.db_execute: res="+res);
        } catch (HibernateException he) {
            if(etx!=null) etx.rollback();
//            if(tx!=null) tx.rollback();
            he.printStackTrace();
            return false;
        } finally {
            em.close();
            em = null;
        }
        return true;
    }
    public List<DBEntity> db_query(String sql, HashMap<String,Object> hm, Class klass, boolean initializeLazyObjects) {
        log.debug("db_query: sql="+sql);
        log.debug("db_query: hm="+hm);
        log.debug("db_query: klass="+klass);
        if(em==null) {
            em = session.getEntityManagerFactory().createEntityManager();
            etx = em.getTransaction(); etx.begin();
        }
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
        em.close();
        em = null;
        return dbes;
    }

    public DBEntity refresh(DBEntity dbe) throws DBException {
        // NOTE be careful, introduces duplicates in the same hibernate session
        if(em==null) {
            em = session.getEntityManagerFactory().createEntityManager();
            etx = em.getTransaction(); etx.begin();
        }
        if(etx==null || !etx.isActive()) { etx = em.getTransaction(); etx.begin(); }
        try {
            em.refresh(dbe);
//            session.flush();
        } catch (HibernateException he) {
            log.error("dbe: "+dbe.toString());
            he.printStackTrace();
            return null;
        } catch(EntityNotFoundException enfex) {
            // RRA: maybe because has already been refreshed in memory by the previous passage?
            // RRA: so i'll ignore it
            //enfex.printStackTrace();
            //return null;
        } finally {
            em.close();
            em = null;
        }
        return dbe;
    }
    public DBEntity insert(DBEntity dbe, DBMgr dbMgr) throws DBException {
        if(em==null) {
            em = session.getEntityManagerFactory().createEntityManager();
            etx = em.getTransaction(); etx.begin();
        }
//        log.info(session.getTransaction().getStatus());
//        if(session.getTransaction().getStatus()!=TransactionStatus.NOT_ACTIVE) {
//            session.flush();
//        }
        if(etx==null || !etx.isActive()) { etx = em.getTransaction(); etx.begin(); }
        try {
//            dbe.beforeInsert(dbMgr);
            if(dbe instanceof DBEObject) {
                // See: https://stackoverflow.com/questions/27899443/detached-entity-passed-to-persist-jpa-inheritance
                // very weird reason
                dbe = em.merge(dbe);
            } else {
                em.persist(dbe);
            }
//            dbe.afterInsert(dbMgr);
            etx.commit();
        } catch (HibernateException he) {
            if (etx != null) etx.rollback();
            log.error("dbe: "+dbe.toString());
            he.printStackTrace();
            return null;
        } finally {
            em.close();
            em = null;
        }
        return dbe;
    }
    public DBEntity update(DBEntity dbe, DBMgr dbMgr) throws DBException {
        if(em==null) {
            em = session.getEntityManagerFactory().createEntityManager();
            etx = em.getTransaction(); etx.begin();
        }
        if(etx==null || !etx.isActive()) { etx = em.getTransaction(); etx.begin(); }
        try {
//            dbe.beforeUpdate(dbMgr);
            em.merge(dbe);
//            dbe.afterUpdate(dbMgr);
            etx.commit();
        } catch (HibernateException he) {
            if(etx!=null) etx.rollback();
            log.error("dbe: "+dbe.toString());
            he.printStackTrace();
            return null;
        } finally {
            em.close();
            em = null;
        }
        return dbe;
    }
    public DBEntity delete(DBEntity dbe, DBMgr dbMgr) throws DBException {
        if(em==null) {
            em = session.getEntityManagerFactory().createEntityManager();
            etx = em.getTransaction(); etx.begin();
        }
        if(etx==null || !etx.isActive()) { etx = em.getTransaction(); etx.begin(); }
        try {
//            dbe.beforeDelete(dbMgr);
            em.remove(dbe);
//            dbe.afterDelete(dbMgr);
            etx.commit();
        } catch (HibernateException he) {
            if(etx!=null) etx.rollback();
            he.printStackTrace();
            return null;
        } finally {
            em.close();
            em = null;
        }
        return dbe;
    }
}
