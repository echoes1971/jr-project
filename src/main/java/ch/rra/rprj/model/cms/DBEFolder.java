package ch.rra.rprj.model.cms;

import ch.rra.rprj.model.core.DBEObject;
import ch.rra.rprj.model.core.DBEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
mysql> desc rprj_folders;
+-------------------+--------------+------+-----+---------------------+-------+
| Field             | Type         | Null | Key | Default             | Extra |
+-------------------+--------------+------+-----+---------------------+-------+
| id                | varchar(16)  | NO   | PRI | NULL                |       |
| owner             | varchar(16)  | NO   | MUL | NULL                |       |
| group_id          | varchar(16)  | NO   | MUL | NULL                |       |
| permissions       | char(9)      | NO   |     | rwx------           |       |
| creator           | varchar(16)  | NO   | MUL | NULL                |       |
| creation_date     | datetime     | YES  |     | NULL                |       |
| last_modify       | varchar(16)  | NO   | MUL | NULL                |       |
| last_modify_date  | datetime     | YES  |     | NULL                |       |
| deleted_by        | varchar(16)  | YES  | MUL | NULL                |       |
| deleted_date      | datetime     | NO   |     | 0000-00-00 00:00:00 |       |
| father_id         | varchar(16)  | YES  | MUL | NULL                |       |
| name              | varchar(255) | NO   |     | NULL                |       |
| description       | text         | YES  |     | NULL                |       |
| fk_obj_id         | varchar(16)  | YES  | MUL | NULL                |       |
| childs_sort_order | text         | YES  |     | NULL                |       |
+-------------------+--------------+------+-----+---------------------+-------+
*/

@Entity
@Table(name="rprj_folders")
public class DBEFolder extends DBEObject {

    @Column(name = "fk_obj_id", columnDefinition = "VARCHAR(16)")
    private String fk_obj_id;

    @Column(name = "childs_sort_order", columnDefinition = "TEXT")
    private String childs_sort_order;

    public DBEFolder() {
    }

    public DBEFolder(String name, String description) {
        super(name, description);
    }

    public DBEFolder(String id, String owner, String group_id, String permissions, String creator, Timestamp creation_date, String last_modify, Timestamp last_modify_date, String deleted_by, Timestamp deleted_date, String father_id, String name, String description, String fk_obj_id, String childs_sort_order) {
        super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date, deleted_by, deleted_date, father_id, name, description);
        this.fk_obj_id = fk_obj_id;
        this.childs_sort_order = childs_sort_order;
    }

    public String getIcon() { return "glyphicon-folder-close"; }

    public String getFk_obj_id() { return fk_obj_id; }
    public void setFk_obj_id(String fk_obj_id) { this.fk_obj_id = fk_obj_id; }

    public String getChilds_sort_order() { return childs_sort_order; }
    public void setChilds_sort_order(String childs_sort_order) { this.childs_sort_order = childs_sort_order; }

    public List<DBEObject> sortChildren(List<DBEObject> childs) {
        if(this.childs_sort_order==null || this.childs_sort_order.length()==0) return childs;
        List<String> order = Arrays.asList(this.childs_sort_order.split(","));
        List<String> ids = childs.stream().map(DBEObject::getId).collect(Collectors.toList());
        childs.sort((t0, t1) -> {
            String id1 = t0.getId();
            String id2 = t1.getId();
            int pos1 = order.indexOf(id1);
            int pos2 = order.indexOf(id2);
            if (pos1 < 0 && pos2 < 0) {
                pos1 = ids.indexOf(id1);
                pos2 = ids.indexOf(id2);
            } else if (pos1 < 0) {
                pos1 = pos2 + 1;
            } else if (pos2 < 0) {
                pos2 = pos1 + 1;
            }
            return pos1 < pos2 ? -1 : (pos1 > pos2 ? 1 : 0);
        });
        return childs;
    }

    @Override
    public String toString() {
        return "DBEFolder{" +
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
                (childs_sort_order!=null ? ", childs_sort_order='" + childs_sort_order + '\'' : "") +
                '}';
    }
}
