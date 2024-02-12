package ch.rra.rprj.model.cms;

import ch.rra.rprj.model.core.DBEObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.sql.Timestamp;

/*
mysql> desc rprj_notes;
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
| fk_obj_id        | varchar(16)  | YES  | MUL | NULL                |       |
+------------------+--------------+------+-----+---------------------+-------+
*/

@Entity
@Table(name="rprj_notes")
public class DBENote extends DBEObject {

    @Column(name = "fk_obj_id", columnDefinition = "VARCHAR(16)")
    private String fk_obj_id;

    public DBENote() {}

    public DBENote(String name, String description) {
        super(name,description);
    }

    public DBENote(String id, String owner, String group_id, String permissions, String creator, Timestamp creation_date, String last_modify, Timestamp last_modify_date, String deleted_by, Timestamp deleted_date, String father_id, String name, String description, String fk_obj_id) {
        super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date, deleted_by, deleted_date, father_id, name, description);
        this.fk_obj_id = fk_obj_id;
    }

    public String getIcon() { return "glyphicon-circle-arrow-right"; }

    public String getFk_obj_id() { return fk_obj_id; }
    public void setFk_obj_id(String fk_obj_id) { this.fk_obj_id = fk_obj_id; }

    @Override
    public String toString() {
        return "DBENote{" +
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
                (fk_obj_id!=null ? ", fk_obj_id='" + fk_obj_id + '\'' : "") +
                '}';
    }
}
