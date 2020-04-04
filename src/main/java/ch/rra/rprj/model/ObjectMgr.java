package ch.rra.rprj.model;

import ch.rra.rprj.model.cms.DBEFolder;
import ch.rra.rprj.model.cms.DBENote;
import ch.rra.rprj.model.cms.DBEPage;
import ch.rra.rprj.model.core.*;
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

    // TODO add all the subclasses of DBEntity with ID
    private List<Class> typesWithId = Arrays.asList(new Class[]{
            User.class,
            Group.class,
            DBEFolder.class,
            DBENote.class,
            DBEObjectReal.class,
            DBEPage.class
    });
    // TODO add all the sublasses and remove DBEObject
    private List<Class> registeredObjectTypes = Arrays.asList(new Class[]{
            DBEFolder.class,
            DBENote.class,
            DBEObjectReal.class,
            DBEPage.class
    });

    public boolean canRead(DBEObject obj) {
        return obj.canRead(' ')
            || (obj.canRead('G') && hasGroup(obj.getGroup_id()))
            || (obj.canRead('U')
                && getDbeUser()!=null && getDbeUser().getId().equals(obj.getOwner()));
    }
    public boolean canWrite(DBEObject o) {
        User u = getDbeUser();
        if(u!=null && u.getId().equals(o.getCreator()))
            return true;
        return o.canWrite(' ')
                || (o.canWrite('G') && hasGroup(o.getGroup_id()))
                || (o.canWrite('U') && u!=null && u.getId().equals(o.getOwner()));
    }
    public boolean canExecute(DBEObject o) {
        User u = getDbeUser();
        if(u!=null && u.getId().equals(o.getCreator()))
            return true;
        return o.canExecute(' ')
                || (o.canExecute('G') && hasGroup(o.getGroup_id()))
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
            ((DBEObject) search).setDeleted_by(null);
        List<DBEntity> res = super.search(search, uselike, orderby);
        return res.stream().filter(x -> !(x instanceof DBEObject) || this.canRead((DBEObject) x)).collect(Collectors.toList());
    }

    public DBEntity dbeById(String id) {
        DBEntity ret = null;
        // Search all the subclasses of DBEntity with an ID
        Vector<String> qs = new Vector<>();
        for (Class klass : typesWithId) {
            DBEntity dbe;
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
        String sql = String.join(" union ", qs);

        HashMap<String,Object> hm = new HashMap<>();
        hm.put("id", id);
        List res = this.db_query(sql,hm,null,false);
        Object[] values = (Object[]) res.get(0);

        Class myclass = typesWithId.stream().filter(k -> k.getSimpleName().equals(values[0])).collect(Collectors.toList()).get(0);
        try {
            DBEntity search = (DBEntity) myclass.newInstance();
            Method method = myclass.getMethod("setId", String.class);
            method.invoke(search, id);
            List<DBEntity> res2 = this.search(search, false, null);
            if(res2.size()==1) ret = res2.get(0);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public DBEObject objectById(String id) { return objectById(id,true); }
    public DBEObject objectById(String id, boolean ignore_deleted) {
        DBEObject ret = null;
        // Search all the subclasses of DBEObject
        String[] column_list = {"id"
                ,"owner","group_id","permissions","creator"
                ,"creation_date","last_modify","last_modify_date"
                ,"deleted_by","deleted_date"
                ,"father_id","name","description"};
        Vector<String> qs = new Vector<>();
        for (Class klass : registeredObjectTypes) {
            DBEntity dbe;
            try {
                dbe = (DBEntity) klass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            // TODO re-enable the following when there will be more subclasses
            //if (klass.getName() == "DBEObject" || !(dbe instanceof DBEObject)) continue;
            qs.add("select '" + klass.getSimpleName() + "' as classname,"
                    + String.join(",",column_list)
                    + " from "+dbe.getTableName() + " "
                    + " where id = :id "
                    + (ignore_deleted ? "and deleted_by is null " : "")
                    );
        }
        String sql = String.join(" union ", qs);
        logger.debug(sql);
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("id", id);
        List res = this.db_query(sql,hm,DBEObjectReal.class,false);
        logger.debug("res: "+res.size());
        if(res.size()==1) {
            return (DBEObject) res.get(0);
        }
        // TODO Verify this for DBENotes
        //printObjectList(res);
/*
        Object[] values = (Object[]) res.get(0);

        HashMap<String,Object> hmValues = new HashMap<>();
        int i=1;
        for(String col : column_list) {
            logger.info(col+"=>"+values[i]);
            hmValues.put(col, values[i++]);
        }

        Class myclass = registeredObjectTypes.stream().filter(k -> k.getSimpleName().equals(values[0])).collect(Collectors.toList()).get(0);
        try {
            DBEObject search = (DBEObject) myclass.newInstance();
            search.setId(id);
            List<DBEntity> res2 = this.search(search, false, null, ignore_deleted);
            if(res2.size()==1) ret = (DBEObject) res2.get(0);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
*/
        return ret;
    }
    public DBEObject fullObjectById(String id) { return fullObjectById(id,true); }
    public DBEObject fullObjectById(String id, boolean ignore_deleted) {
        DBEObject ret = null;
        // Search all the subclasses of DBEObject
        String[] column_list = {"id"
                ,"owner","group_id","permissions","creator"
                ,"creation_date","last_modify","last_modify_date"
                ,"deleted_by","deleted_date"
                ,"father_id","name","description"};
        Vector<String> qs = new Vector<String>();
        for (Class klass : registeredObjectTypes) {
            DBEntity dbe = null;
            try {
                dbe = (DBEntity) klass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            // TODO re-enable the following when there will be more subclasses
            //if (klass.getName() == "DBEObject" || !(dbe instanceof DBEObject)) continue;
            qs.add("select '" + klass.getSimpleName() + "' as classname,"
                    +String.join(",",column_list)
                    + " from "+dbe.getTableName() + " "
                    + " where id = :id "
                    + (ignore_deleted ? "and deleted_by is null " : "")
            );
        }
        //qs.stream().forEach(s -> { System.out.println("s: " + s); });
        String sql = String.join(" union ", qs);
        logger.debug("fullObjectById: sql="+sql);
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("id", id);
        List res = this.db_query(sql,hm,null,false);
        logger.debug("fullObjectById: res="+res.size());
        //printObjectList(res);
        Object[] values = (Object[]) res.get(0);

        Class myclass = registeredObjectTypes.stream().filter(k -> k.getSimpleName().equals(values[0])).collect(Collectors.toList()).get(0);
        //logger.info("fullObjectById: myclass="+myclass);
        try {
            DBEObject search = (DBEObject) myclass.newInstance();
            search.setId(id);
            List<DBEntity> res2 = this.search(search, false, null, ignore_deleted);
            if(res2.size()==1) ret = (DBEObject) res2.get(0);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public List<DBEObject> objectByName(String name) { return objectByName(name,true); }
    public List<DBEObject> objectByName(String name, boolean ignore_deleted) {
        // Search all the subclasses of DBEObject
        String[] column_list = {"id"
                ,"owner","group_id","permissions","creator"
                ,"creation_date","last_modify","last_modify_date"
                ,"deleted_by","deleted_date"
                ,"father_id","name","description"
        };
        Vector<String> qs = new Vector<>();
        for (Class klass : registeredObjectTypes) {
            DBEntity dbe;
            try {
                dbe = (DBEntity) klass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            // TODO re-enable the following when there will be more subclasses
            //if (klass.getName() == "DBEObject" || !(dbe instanceof DBEObject)) continue;
            qs.add("select '" + klass.getSimpleName() + "' as classname,"
                    + String.join(",",column_list)
                    + " from "+dbe.getTableName() + " "
                    + " where name = :name "
                    + (ignore_deleted ? "and deleted_by is null " : "")
            );
        }
        String sql = String.join(" union ", qs);
        logger.debug(sql);
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("name", name);
        List res = this.db_query(sql,hm,DBEObjectReal.class,false);
        if(res.size()>0) {
            logger.debug("objectByName: res=" + res.size());
            return (List<DBEObject>) res;
        }
        // TODO Verify this for DBENotes
        //printObjectList(res);
/*
        Object[] values = (Object[]) res.get(0);

        HashMap<String,Object> hmValues = new HashMap<>();
        int i=1;
        for(String col : column_list) {
            logger.info(col+"=>"+values[i]);
            hmValues.put(col, values[i++]);
        }

        Class myclass = registeredObjectTypes.stream().filter(k -> k.getSimpleName().equals(values[0])).collect(Collectors.toList()).get(0);
        try {
            DBEObject search = (DBEObject) myclass.newInstance();
            search.setId(id);
            List<DBEntity> res2 = this.search(search, false, null, ignore_deleted);
            if(res2.size()==1) ret = (DBEObject) res2.get(0);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
*/
        return null;
    }
    public List<DBEObject> fullObjectByName(String name) { return fullObjectByName(name,true); }
    public List<DBEObject> fullObjectByName(String name, boolean ignore_deleted) {
        List<DBEObject> ret = new ArrayList<>();
        // Search all the subclasses of DBEObject
        String[] column_list = {"id"
                ,"owner","group_id","permissions","creator"
                ,"creation_date","last_modify","last_modify_date"
                ,"deleted_by","deleted_date"
                ,"father_id","name","description"
        };
        Vector<String> qs = new Vector<>();
        for (Class klass : registeredObjectTypes) {
            DBEntity dbe;
            try {
                dbe = (DBEntity) klass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            // TODO re-enable the following when there will be more subclasses
            //if (klass.getName() == "DBEObject" || !(dbe instanceof DBEObject)) continue;
            qs.add("select '" + klass.getSimpleName() + "' as classname,"
                    +String.join(",",column_list)
                    + " from "+dbe.getTableName() + " "
                    + " where name = :name "
                    + (ignore_deleted ? "and deleted_by is null " : "")
            );
        }
        String sql = String.join(" union ", qs);
        logger.debug("fullObjectByName: sql="+sql);
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("name", name);
        List res = this.db_query(sql,hm,null,false);
        logger.debug("fullObjectByName: res="+res.size());
        //printObjectList(res);
        res.forEach(values -> {
            Object[] _values = (Object[]) values;
            //logger.info("fullObjectByName: _values="+_values[0]+" "+_values[1]);

            Class myclass = registeredObjectTypes.stream().filter(k -> k.getSimpleName().equals(_values[0])).collect(Collectors.toList()).get(0);
            logger.debug("fullObjectById: myclass="+myclass);
            try {
                DBEObject search = (DBEObject) myclass.newInstance();
                search.setId((String) _values[1]);
                logger.debug("fullObjectByName: search="+search);
                List<DBEntity> res2 = this.search(search, false, null, ignore_deleted);
                logger.debug("fullObjectByName: res2="+res2.size());
                res2.stream().forEach(dbe -> {
                    logger.debug("fullObjectByName: dbe="+dbe);
                    ret.add((DBEObject) dbe);
                });
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return ret;
    }
}
