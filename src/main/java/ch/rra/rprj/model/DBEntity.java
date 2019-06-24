package ch.rra.rprj.model;

public class DBEntity {
    
    public void beforeInsert(DBMgr dbMgr) throws DBException {}
    public void afterInsert(DBMgr dbMgr) throws DBException {}

    public void beforeUpdate(DBMgr dbMgr) throws DBException {}
    public void afterUpdate(DBMgr dbMgr) throws DBException {}

    public void beforeDelete(DBMgr dbMgr) throws DBException {}
    public void afterDelete(DBMgr dbMgr) throws DBException {}
}
