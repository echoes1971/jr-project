package ch.rra.rprj.model;

import java.util.UUID;

public class DBEntity {

    public String getNextUuid() {
        UUID uuid = UUID.randomUUID();
        String ret = uuid.toString().replaceAll("-","");
        ret = ret.substring(ret.length()-16);
        //System.out.println("ret = " + ret + " (" + ret.length() + ")");
        return ret;
    }

    public void beforeInsert(DBMgr dbMgr) throws DBException {}
    public void afterInsert(DBMgr dbMgr) throws DBException {}

    public void beforeUpdate(DBMgr dbMgr) throws DBException {}
    public void afterUpdate(DBMgr dbMgr) throws DBException {}

    public void beforeDelete(DBMgr dbMgr) throws DBException {}
    public void afterDelete(DBMgr dbMgr) throws DBException {}
}
