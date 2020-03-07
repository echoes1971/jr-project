package ch.rra.rprj.model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

/*
mysql> desc rprj_log;
+-------+--------------+------+-----+------------+-------+
| Field | Type         | Null | Key | Default    | Extra |
+-------+--------------+------+-----+------------+-------+
| ip    | varchar(16)  | NO   | PRI | NULL       |       |
| data  | date         | NO   | PRI | 0000-00-00 |       |
| ora   | time         | NO   |     | 00:00:00   |       |
| count | int(11)      | NO   |     | 0          |       |
| url   | varchar(255) | YES  |     | NULL       |       |
| note  | varchar(255) | NO   |     |            |       |
| note2 | text         | NO   |     | NULL       |       |
+-------+--------------+------+-----+------------+-------+
*/

// See: https://stackoverflow.com/questions/3585034/how-to-map-a-composite-key-with-hibernate/3588400#3588400

@Entity
@IdClass(DBELogPK.class)
@Table(name="rprj_log")
public class DBELog extends DBEntity {
    @Id
    @Column(name = "ip", columnDefinition="VARCHAR(16)")
    private String ip;

    @Id
    @Column(name = "data", columnDefinition="DATE")
    private Date data;

    @Column(name="ora", columnDefinition="TIME")
    private Time ora;
    @Column(name="count", columnDefinition="INT(11)")
    private Integer count;
    @Column(name="url", columnDefinition="VARCHAR(255)")
    private String url;
    @Column(name="note", nullable=false, columnDefinition="VARCHAR(255)")
    private String note;
    @Column(name="note2", columnDefinition="TEXT")
    private String note2;

    public DBELog() { }

    public DBELog(String ip, Date data, Time ora, Integer count, String url, String note, String note2) {
        this.ip = ip;
        this.data = data;
        this.ora = ora;
        this.count = count;
        this.url = url;
        this.note = note;
        this.note2 = note2;
    }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public Time getOra() { return ora; }
    public void setOra(Time ora) { this.ora = ora; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getNote2() { return note2; }
    public void setNote2(String note2) { this.note2 = note2; }

    @Override
    public String toString() {
        return "DBELog{" +
                "ip='" + ip + '\'' +
                ", data=" + data +
                ", ora=" + ora +
                ", count=" + count +
                ", url='" + url + '\'' +
                ", note='" + note + '\'' +
                ", note2='" + note2 + '\'' +
                '}';
    }
}
