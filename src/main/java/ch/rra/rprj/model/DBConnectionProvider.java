package ch.rra.rprj.model;

public abstract class DBConnectionProvider {

    protected String server;
    protected String user;
    protected String pwd;
    protected String dbname;
    protected String schema;
    protected boolean verbose;

    public DBConnectionProvider(String server, String user, String pwd, String dbname, String schema, boolean verbose) {
        this.server = server;
        this.user = user;
        this.pwd = pwd;
        this.dbname = dbname;
        this.schema = schema;
        this.verbose = verbose;
    }

    public DBConnectionProvider setVerbose(boolean b) { this.verbose=b; return this; }
}
