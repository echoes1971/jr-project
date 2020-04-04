package ch.rra.rprj.model.core;

import ch.rra.rprj.model.DBException;
import ch.rra.rprj.model.DBMgr;
import ch.rra.rprj.model.ObjectMgr;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/*
mysql> desc rprj_objects;
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
+------------------+--------------+------+-----+---------------------+-------+
*/

// See: https://en.wikibooks.org/wiki/Java_Persistence/Inheritance
// See: https://www.baeldung.com/hibernate-inheritance

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class DBEObject extends DBEntity {
    @Id
    @GeneratedValue(generator="UUID")
    @GenericGenerator(
            name="UUID",
            strategy="ch.rra.rprj.model.core.IdGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(16)")
    protected String id;

    @Column(name = "owner", columnDefinition = "VARCHAR(16)")
    protected String owner;
    @Column(name = "group_id", columnDefinition = "VARCHAR(16)")
    protected String group_id;
    @Column(name = "permissions", columnDefinition = "CHAR(9)")
    protected String permissions;

    @Column(name = "creator", columnDefinition = "VARCHAR(16)")
    protected String creator;
    @Column(name = "creation_date", columnDefinition = "DATETIME")
    protected Timestamp creation_date;

    @Column(name = "last_modify", columnDefinition = "VARCHAR(16)")
    protected String last_modify;
    @Column(name = "last_modify_date", columnDefinition = "DATETIME")
    protected Timestamp last_modify_date;

    @Column(name = "deleted_by", columnDefinition = "VARCHAR(16)")
    protected String deleted_by;
    @Column(name = "deleted_date", columnDefinition = "DATETIME")
    protected Timestamp deleted_date;

    @Column(name = "father_id", columnDefinition = "VARCHAR(16)")
    protected String father_id;

    @Column(name = "name", columnDefinition = "VARCHAR(255)")
    protected String name;
    @Column(name="description", columnDefinition="TEXT")
    protected String description;

    public DBEObject() { }

    public DBEObject(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public DBEObject(String id, String owner, String group_id, String permissions,
                     String creator, Timestamp creation_date, String last_modify,
                     Timestamp last_modify_date, String deleted_by, Timestamp deleted_date,
                     String father_id, String name, String description) {
        this.id = id;
        this.owner = owner;
        this.group_id = group_id;
        this.permissions = permissions;
        this.creator = creator;
        this.creation_date = creation_date;
        this.last_modify = last_modify;
        this.last_modify_date = last_modify_date;
        this.deleted_by = deleted_by;
        this.deleted_date = deleted_date;
        this.father_id = father_id;
        this.name = name;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getGroup_id() { return group_id; }
    public void setGroup_id(String group_id) { this.group_id = group_id; }

    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public Timestamp getCreation_date() { return creation_date; }
    public void setCreation_date(Timestamp creation_date) { this.creation_date = creation_date; }

    public String getLast_modify() { return last_modify; }
    public void setLast_modify(String last_modify) { this.last_modify = last_modify; }

    public Timestamp getLast_modify_date() { return last_modify_date; }
    public void setLast_modify_date(Timestamp last_modify_date) { this.last_modify_date = last_modify_date; }

    public String getDeleted_by() { return deleted_by; }
    public void setDeleted_by(String deleted_by) { this.deleted_by = deleted_by; }

    public Timestamp getDeleted_date() { return deleted_date; }
    public void setDeleted_date(Timestamp deleted_date) { this.deleted_date = deleted_date; }

    public String getFather_id() { return father_id; }
    public void setFather_id(String father_id) { this.father_id = father_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isDeleted() { return this.deleted_by!=null && this.deleted_by.length()>0; }
    public boolean canRead(char kind) {
        if(this.permissions==null || this.permissions.length()!=9) return true;
        boolean ret = false;
        switch (kind) {
            case 'U':
                ret = this.permissions.charAt(0+0)=='r';
                break;
            case 'G':
                ret = this.permissions.charAt(0+3)=='r';
                break;
            default:
                ret = this.permissions.charAt(0+6)=='r';
                break;
        }
        return ret;
    }
    public boolean canWrite(char kind) {
        if(this.permissions==null || this.permissions.length()!=9) return true;
        boolean ret = false;
        switch (kind) {
            case 'U':
                ret = this.permissions.charAt(1+0)=='w';
                break;
            case 'G':
                ret = this.permissions.charAt(1+3)=='w';
                break;
            default:
                ret = this.permissions.charAt(1+6)=='w';
                break;
        }
        return ret;
    }
    public boolean canExecute(char kind) {
        if(this.permissions==null || this.permissions.length()!=9) return true;
        boolean ret = false;
        switch (kind) {
            case 'U':
                ret = this.permissions.charAt(2+0)=='x';
                break;
            case 'G':
                ret = this.permissions.charAt(2+3)=='x';
                break;
            default:
                ret = this.permissions.charAt(2+6)=='x';
                break;
        }
        return ret;
    }

    public void setDefaultValues(ObjectMgr objMgr) {
        User user = objMgr.getDbeUser();
        if(user!=null) {
            if(this.owner==null || this.owner.length()==0)
                this.owner = user.getId();
            if(this.group_id==null || this.group_id.length()==0)
                this.group_id = user.getGroup_id();
            this.creator = user.getId();
            this.last_modify = user.getId();
        }
        Timestamp today = new Timestamp((new java.util.Date()).getTime());
        this.creation_date = today;
        this.last_modify_date = today;
        this.deleted_date = null;
        if(this.permissions==null || this.permissions.length()!=9)
            this.permissions = "rwx------";
        if(this.father_id==null) {
            this.father_id = "";
        } else {
            DBEObject father = objMgr.objectById(this.father_id);
            if(father!=null) {
                this.group_id = father.getGroup_id();
                this.permissions = father.getPermissions();
            }
        }
    }

    @Override
    public void beforeInsert(DBMgr dbMgr) throws DBException {
        super.beforeInsert(dbMgr);
        this.setId(this.getNextUuid());
        this.setDefaultValues((ObjectMgr)dbMgr);
        this.setDeleted_date(new Timestamp(0));
    }

    @Override
    public void beforeUpdate(DBMgr dbMgr) throws DBException {
        super.beforeUpdate(dbMgr);
        User user = dbMgr.getDbeUser();
        if(user!=null) this.last_modify = user.getId();
        this.last_modify_date = new Timestamp((new java.util.Date()).getTime());
    }

    @Override
    public void beforeDelete(DBMgr dbMgr) throws DBException {
        super.beforeDelete(dbMgr);
        if(this.isDeleted()) return;
        User user = dbMgr.getDbeUser();
        if(user!=null) this.deleted_by = user.getId();
        this.deleted_date = new Timestamp((new java.util.Date()).getTime());
    }

    @Override
    public String toString() {
        return "DBEObject{" +
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
                '}';
    }
}
