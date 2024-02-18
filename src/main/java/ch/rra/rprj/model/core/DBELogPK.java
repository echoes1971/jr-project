package ch.rra.rprj.model.core;

import java.io.Serializable;
import java.sql.Date;

public class DBELogPK  implements Serializable {
    protected String ip;
    protected Date data;

    public DBELogPK() {}

    public DBELogPK(String ip, Date data) {
        this.ip = ip;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        DBELogPK dbelogpk = (DBELogPK) o;
        return this.ip.equals(dbelogpk.ip) && this.data.equals(dbelogpk.data);
    }

    @Override
    public int hashCode() {
        return ip.hashCode() * data.hashCode();
    }
}
