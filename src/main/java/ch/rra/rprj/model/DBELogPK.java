package ch.rra.rprj.model;

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
}
