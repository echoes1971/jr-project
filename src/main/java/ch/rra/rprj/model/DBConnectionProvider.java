package ch.rra.rprj.model;

import ch.rra.rprj.model.core.DBEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public abstract class DBConnectionProvider {

    protected String server;
    protected String user;
    protected String pwd;
    protected String dbname;
    protected String schema;
    protected boolean verbose;

    public DBConnectionProvider() {}
    public DBConnectionProvider(Properties props) {}
    public DBConnectionProvider(String server, String user, String pwd, String dbname, String schema, boolean verbose) {
        this.server = server;
        this.user = user;
        this.pwd = pwd;
        this.dbname = dbname;
        this.schema = schema;
        this.verbose = verbose;
    }

    public DBConnectionProvider setVerbose(boolean b) { this.verbose=b; return this; }

    public abstract boolean connect();
    public abstract boolean disconnect();

    public abstract boolean db_execute(String sql);
    public List<DBEntity> db_query(String sql) {
        return db_query(sql, new HashMap<>(), null, false);
    }
    public abstract List<DBEntity> db_query(String sql, HashMap<String,Object> hm, Class klass, boolean initializeLazyObjects);

    public abstract DBEntity refresh(DBEntity dbe) throws DBException;
    public abstract DBEntity insert(DBEntity dbe, DBMgr dbMgr) throws DBException;
    public abstract DBEntity update(DBEntity dbe, DBMgr dbMgr) throws DBException;
    public abstract DBEntity delete(DBEntity dbe, DBMgr dbMgr) throws DBException;
}
