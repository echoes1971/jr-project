package ch.rra.rprj.model;

import org.hibernate.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.Transaction;

import javax.persistence.EntityNotFoundException;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBMgr {
    private SessionFactory sessionFactory;

    private boolean verbose;
    private User dbeUser;

    public DBMgr() {
        verbose = false;
    }

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
                }
            } catch(ClassCastException cce2) {
                for (Object obj : objects) {
                    System.out.println("obj> " + obj);
                }
            }
        }
    }


    public User getDbeUser() { return dbeUser; }
    public void setDbeUser(User dbeUser) { this.dbeUser = dbeUser; }

    public boolean db_execute(String sql) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            int res = session.createSQLQuery(sql).executeUpdate();
            if(verbose) System.out.println("DBMgr.db_execute: res="+res);
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
    public List<DBEntity> db_query(String sql) {
        return db_query(sql,new HashMap<String,Object>(), null,
                false);
    }
    public List<DBEntity> db_query(String sql, HashMap<String,Object> hm,
                                   Class klass, boolean initializeLazyObjects) {
        Session session = sessionFactory.openSession();
        NativeQuery q = session.createNativeQuery(sql);
        if(klass!=null) q.addEntity(klass);
        if(hm!=null) {
            for(String k : hm.keySet()) {
                q.setParameter(k, hm.get(k));
            }
        }
        List<DBEntity> dbes = q.getResultList();
        // This to force the load of lazy objects :-(
        if(initializeLazyObjects)
            dbes.stream().forEach((DBEntity dbe) -> dbe.toString());
        session.close();
        return dbes;
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

    public List<DBEntity> search(DBEntity search) throws DBException {
        return search(search, true, null);
    }
    public List<DBEntity> search(DBEntity search, boolean uselike, String orderby) throws DBException {
        // tablename
        Annotation[] annotations = search.getClass().getAnnotations();
        String entityname = search.getClass().getSimpleName();
        String tablename = "";
        for (Annotation an : annotations) {
            if(verbose) System.out.println("" + an.toString());
            if(an instanceof javax.persistence.Table) {
                tablename = ((Table) an).name();
                break;
            }
        }
        if(verbose) System.out.println("entityname: " + entityname);
        if(verbose) System.out.println("tablename: " + tablename);

        // Columns
        Field[] fields = search.getClass().getDeclaredFields();
        HashMap<String,Object> hashMap = new HashMap<String,Object>();
        List<String> clauses = new ArrayList<String>();
        for (Field field : fields) {
            String field_name = field.getName();
            String method_name = "get"
                    + field_name.substring(0,1).toUpperCase()
                    + field_name.substring(1).toLowerCase();
            Method method = null;
            Object value = null;
            try {
                method = search.getClass().getMethod(method_name);
                value = method.invoke(search);
            } catch (NoSuchMethodException e) {
                if(verbose) System.out.println("ERROR: field_name.method_name NOT FOUND!");
                continue;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            String column_name = field.getName();
            Annotation[] field_annotations = field.getAnnotations();
            for(Annotation an : field_annotations) {
                //if(verbose) System.out.println("  " +an.toString());
                if(an instanceof javax.persistence.Column) {
                    column_name = ((javax.persistence.Column)an).name();
                    break;
                } else if(an instanceof JoinTable) {
                    column_name = "";
                    break;
                }
            }
            if(column_name.equals(""))
                continue;
            if(verbose) System.out.println("" + field.toString());
            if(verbose) System.out.println(" name:\t" + field_name);
            //if(verbose) System.out.println(" method name:\t" + method_name);
            if(verbose) System.out.println(" get:\t" + method);
            //if(verbose) System.out.println(" " + field);
            if(verbose) System.out.println(" column:" + column_name);
            if(verbose) System.out.println(" value:\t" + value + (value!=null ? " ("+value.getClass()+")" : ""));
            if(value!=null) {
                if(uselike && value instanceof String) {
                    clauses.add(field_name + " LIKE :" + field_name);
                    value = "%" + value + "%";
                } else
                    clauses.add(field_name + " = :" + field_name);
                hashMap.put(field_name, value);
            }
        }

        String hql = "SELECT * FROM " + tablename;
        if(clauses.size()>0) {
            hql += " WHERE ";
            for(int i=0 ; i<clauses.size() ; i++) {
                hql += clauses.get(i);
                if(i < (clauses.size()-1))
                    hql += " AND ";
            }
        }

        if(orderby!=null && orderby.length()>0)
            hql += " ORDER BY " + orderby;

        return this.db_query(hql, hashMap, search.getClass(),true);
    }
}
