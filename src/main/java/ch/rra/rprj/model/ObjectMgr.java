package ch.rra.rprj.model;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ObjectMgr extends DBMgr {

    public boolean canRead(DBEObject obj) {
        return obj.canRead(' ')
            || (obj.canRead('G') && hasGroup(obj.getGroupId()))
            || (obj.canRead('U')
                && getDbeUser()!=null && getDbeUser().getId()==obj.getId());
    }
    public boolean canWrite(DBEObject o) {
        User u = getDbeUser();
        if(u!=null && u.getId()==o.getCreator())
            return true;
        return o.canWrite(' ')
                || (o.canWrite('G') && hasGroup(o.getGroupId()))
                || (o.canWrite('U') && u!=null && u.getId()==o.getId());
    }
    public boolean canExecute(DBEObject o) {
        User u = getDbeUser();
        if(u!=null && u.getId()==o.getCreator())
            return true;
        return o.canExecute(' ')
                || (o.canExecute('G') && hasGroup(o.getGroupId()))
                || (o.canExecute('U') && u!=null && u.getId()==o.getId());
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

    public DBEObject objectById(String id) {
        // TODO search all the subclasses of DBEObject
        return null;
    }
}
