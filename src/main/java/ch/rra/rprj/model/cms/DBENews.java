package ch.rra.rprj.model.cms;

import ch.rra.rprj.model.core.DBEObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/*
mysql> desc rprj_news;
+------------------+--------------+------+-----+---------------------+-------+
| Field            | Type         | Null | Key | Default             | Extra |
+------------------+--------------+------+-----+---------------------+-------+
| id               | varchar(16)  | NO   | PRI | NULL                |       |
| owner            | varchar(16)  | NO   | MUL | NULL                |       |
| group_id         | varchar(16)  | NO   | MUL | NULL                |       |
| permissions      | char(9)      | NO   |     | rwx------           |       |
| creator          | varchar(16)  | NO   | MUL | NULL                |       |
| creation_date    | datetime     | YES  |     | NULL                |       |
| last_modify      | varchar(16)  | NO   | MUL | NULL                |       |
| last_modify_date | datetime     | YES  |     | NULL                |       |
| deleted_by       | varchar(16)  | YES  | MUL | NULL                |       |
| deleted_date     | datetime     | NO   |     | 0000-00-00 00:00:00 |       |
| father_id        | varchar(16)  | YES  | MUL | NULL                |       |
| name             | varchar(255) | NO   |     | NULL                |       |
| description      | text         | YES  |     | NULL                |       |
| html             | text         | YES  |     | NULL                |       |
| fk_obj_id        | varchar(16)  | YES  | MUL | NULL                |       |
| language         | varchar(5)   | YES  |     | en_us               |       |
+------------------+--------------+------+-----+---------------------+-------+
*/

@Entity
@Table(name="rprj_news")
public class DBENews extends DBEObject {
    @Column(name = "html", columnDefinition = "TEXT")
    private String html;

    @Column(name = "fk_obj_id", columnDefinition = "VARCHAR(16)")
    private String fk_obj_id;

    @Column(name = "language", columnDefinition = "VARCHAR(5)")
    private String language;

    public DBENews() {
    }

    public DBENews(String name, String description) {
        super(name, description);
    }

    public DBENews(String id, String owner, String group_id, String permissions, String creator, Timestamp creation_date, String last_modify, Timestamp last_modify_date, String deleted_by, Timestamp deleted_date, String father_id, String name, String description, String html, String fk_obj_id, String language) {
        super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date, deleted_by, deleted_date, father_id, name, description);
        this.html = html;
        this.fk_obj_id = fk_obj_id;
        this.language = language;
    }

    public String getIcon() { return "glyphicon-arrow-right"; }

    public String getHtml() { return html; }
    public void setHtml(String html) { this.html = html; }

    public String getFk_obj_id() { return fk_obj_id; }
    public void setFk_obj_id(String fk_obj_id) { this.fk_obj_id = fk_obj_id; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    @Override
    public String toString() {
        return "DBENews{" +
                "id='" + id + '\'' +
                (owner!=null ? ", owner='" + owner + '\'' : "") +
                (group_id!=null ? ", group_id='" + group_id + '\'' : "") +
                (permissions!=null ? ", permissions='" + permissions + '\'' : "") +
                (creator!=null ? ", creator='" + creator + '\'' : "") +
                (creation_date!=null ? ", creation_date=" + creation_date : "") +
                (last_modify!=null ? ", last_modify='" + last_modify + '\'' : "") +
                (last_modify_date!=null ? ", last_modify_date=" + last_modify_date : "") +
                (deleted_by!=null && deleted_by.length()>0 ?
                        ", deleted_by='" + deleted_by + '\'' +
                                ", deleted_date=" + deleted_date
                        : "") +
                (father_id!=null ? ", father_id='" + father_id + '\'' : "") +
                ", name='" + name + '\'' +
                (description!=null ? ", description='" + description + '\'' : "") +
                (html!=null ? ", html='" + html + '\'' : "") +
                (fk_obj_id!=null ? ", fk_obj_id='" + fk_obj_id + '\'' : "") +
                (language!=null ? ", language='" + language + '\'' : "") +
                '}';
    }
}
