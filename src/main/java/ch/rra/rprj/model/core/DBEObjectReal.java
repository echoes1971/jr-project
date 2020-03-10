package ch.rra.rprj.model.core;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/*
* For TEST purpose only. The DBEObject class creates issues if not abstract
* See: https://en.wikibooks.org/wiki/Java_Persistence/Inheritance
* See: https://www.baeldung.com/hibernate-inheritance
* */

@Entity
@Table(name="rprj_objects")
public class DBEObjectReal extends DBEObject {
    public DBEObjectReal() {
    }

    public DBEObjectReal(String name, String description) {
        super(name, description);
    }

    public DBEObjectReal(String id, String owner, String group_id, String permissions, String creator, Timestamp creation_date, String last_modify, Timestamp last_modify_date, String deleted_by, Timestamp deleted_date, String father_id, String name, String description) {
        super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date, deleted_by, deleted_date, father_id, name, description);
    }
}
