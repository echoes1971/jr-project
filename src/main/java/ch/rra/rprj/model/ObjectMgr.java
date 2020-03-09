package ch.rra.rprj.model;

import ch.rra.rprj.model.core.DBEObject;
import ch.rra.rprj.model.core.DBEntity;
import ch.rra.rprj.model.core.Group;
import ch.rra.rprj.model.core.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ObjectMgr extends DBMgr {
    private Logger logger;

    public ObjectMgr() { this.logger = LoggerFactory.getLogger(getClass()); }

    public boolean canRead(DBEObject obj) {
        return obj.canRead(' ')
            || (obj.canRead('G') && hasGroup(obj.getGroupId()))
            || (obj.canRead('U')
                && getDbeUser()!=null && getDbeUser().getId().equals(obj.getOwner()));
    }
    public boolean canWrite(DBEObject o) {
        User u = getDbeUser();
        if(u!=null && u.getId().equals(o.getCreator()))
            return true;
        return o.canWrite(' ')
                || (o.canWrite('G') && hasGroup(o.getGroupId()))
                || (o.canWrite('U') && u!=null && u.getId().equals(o.getOwner()));
    }
    public boolean canExecute(DBEObject o) {
        User u = getDbeUser();
        if(u!=null && u.getId().equals(o.getCreator()))
            return true;
        return o.canExecute(' ')
                || (o.canExecute('G') && hasGroup(o.getGroupId()))
                || (o.canExecute('U') && u!=null && u.getId().equals(o.getOwner()));
    }

    @Override
    public DBEntity insert(DBEntity dbe) throws DBException {
        boolean hasPermission = !(dbe instanceof DBEObject) || canWrite((DBEObject) dbe);
        if(!hasPermission) throw new DBException("Privilege error");
        return super.insert(dbe);
    }

    @Override
    public DBEntity update(DBEntity dbe) throws DBException {
        boolean hasPermission = !(dbe instanceof DBEObject) || canWrite((DBEObject) dbe);
        if(!hasPermission) throw new DBException("Privilege error");
        return super.update(dbe);
    }

    @Override
    public DBEntity delete(DBEntity dbe) throws DBException {
        boolean hasPermission = !(dbe instanceof DBEObject) || canWrite((DBEObject) dbe);
        if(!hasPermission) throw new DBException("Privilege error");

        if(!(dbe instanceof DBEObject) || ((DBEObject)dbe).isDeleted())
            return super.delete(dbe);

        // Mark object as deleted
        dbe.beforeDelete(this);
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
        dbe.afterDelete(this);
        return dbe;
    }

    @Override
    public List<DBEntity> search(DBEntity search) {
        return search(search, true, null, true);
    }
    public List<DBEntity> search(DBEntity search, boolean uselike, String orderby, boolean ignore_deleted) {
        if (search instanceof DBEObject && ignore_deleted)
            ((DBEObject) search).setDeletedBy(null);
            //$dbe -> setValue('deleted_date', '0000-00-00 00:00:00');
        List<DBEntity> res = super.search(search, uselike, orderby);
        return res.stream().filter(x -> !(x instanceof DBEObject) || this.canRead((DBEObject) x)).collect(Collectors.toList());
    }

    public DBEntity dbeById(String id) {
        // TODO add all the subclasses of DBEntity with ID
        List<Class> typesWithId = Arrays.asList(new Class[]{
                User.class,
                Group.class,
                DBEObject.class
        });
        DBEntity ret = null;
        // Search all the subclasses of DBEObject
        Vector<String> qs = new Vector<String>();
        for (Class klass : typesWithId) {
            DBEntity dbe = null;
            try {
                dbe = (DBEntity) klass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            qs.add("select '" + klass.getSimpleName() + "' as classname,id"
                            + " from "+dbe.getTableName() + " "
                            + " where id = :id "
            );
        }
        qs.stream().forEach(s -> {
            System.out.println("s: " + s);
        });
        String sql = String.join(" union ", qs);
        logger.debug(sql);
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("id", id);
        List res = this.db_query(sql,hm,null,false);
        Object[] values = (Object[]) res.get(0);

        Class myclass = typesWithId.stream().filter(k -> k.getSimpleName().equals(values[0])).collect(Collectors.toList()).get(0);
        DBEntity search = null;
        try {
            search = (DBEntity) myclass.newInstance();
            Method method = myclass.getMethod("setId",String.class);
            method.invoke(search, id);
            List<DBEntity> res2 = this.search(search, false, null);
            if(res2.size()==1) {
                ret = (DBEntity) res2.get(0);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return ret;
    }
    public DBEObject objectById(String id) { return objectById(id,true); }
    public DBEObject objectById(String id, boolean ignore_deleted) {
        // TODO add all the sublasses and remove DBEObject
        List<Class> registeredTypes = Arrays.asList(new Class[]{
                DBEObject.class
        });
        DBEObject ret = null;
        // Search all the subclasses of DBEObject
        Vector<String> qs = new Vector<String>();
        for (Class klass : registeredTypes) {
            DBEntity dbe = null;
            try {
                dbe = (DBEntity) klass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            // TODO re-enable the following when there will be more subclasses
            //if (klass.getName() == "DBEObject" || !(dbe instanceof DBEObject)) continue;
            qs.add("select '" + klass.getSimpleName() + "' as classname,id"
                    +",owner,group_id,permissions,creator,"
                    +"creation_date,last_modify,last_modify_date,"
                    +"deleted_by,deleted_date,"
                    +"father_id,name,description "
                    + " from "+dbe.getTableName() + " "
                    + " where id = :id "
                    + (ignore_deleted ? "and deleted_by is null " : "")
                    );
        }
        //qs.stream().forEach(s -> { System.out.println("s: " + s); });
        String sql = String.join(" union ", qs);
        logger.debug(sql);
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("id", id);
        List res = this.db_query(sql,hm,null,false);
        //printObjectList(res);
        Object[] values = (Object[]) res.get(0);

        Class myclass = registeredTypes.stream().filter(k -> k.getSimpleName().equals(values[0])).collect(Collectors.toList()).get(0);
        DBEObject search = null;
        try {
            search = (DBEObject) myclass.newInstance();
            search.setId(id);
            List<DBEntity> res2 = this.search(search, false, null, ignore_deleted);
            if(res2.size()==1) ret = (DBEObject) res2.get(0);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
