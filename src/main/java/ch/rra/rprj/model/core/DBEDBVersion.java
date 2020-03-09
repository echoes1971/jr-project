package ch.rra.rprj.model.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*
mysql> desc rprj_dbversion;
+------------+--------------+------+-----+---------+-------+
| Field      | Type         | Null | Key | Default | Extra |
+------------+--------------+------+-----+---------+-------+
| model_name | varchar(100) | NO   | PRI | NULL    |       |
| version    | int(11)      | NO   |     | NULL    |       |
+------------+--------------+------+-----+---------+-------+
2 rows in set (0.02 sec)
*/

@Entity
@Table(name="rprj_dbversion")
public class DBEDBVersion extends DBEntity {

    @Id
    @Column(name = "model_name", updatable = false, nullable = false, columnDefinition = "VARCHAR(100)")
    private String model_name;

    @Column(name="version", columnDefinition="INT(11)")
    private Integer version;

    public DBEDBVersion() {}
    public DBEDBVersion(String model_name, Integer version) {
        this.model_name = model_name;
        this.version = version;
    }

    public String getModelName() { return model_name; }
    public void setModelName(String model_name) { this.model_name = model_name; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public String toString() {
        return "DBEDBVersion{" +
                "model_name='" + model_name + '\'' +
                ", version=" + version + "" +
                '}';
    }

}
