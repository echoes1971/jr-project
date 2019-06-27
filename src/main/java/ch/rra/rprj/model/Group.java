package ch.rra.rprj.model;

/*
mysql> desc rprj_groups;
+-------------+--------------+------+-----+---------+-------+
| Field       | Type         | Null | Key | Default | Extra |
+-------------+--------------+------+-----+---------+-------+
| id          | varchar(16)  | NO   | PRI | NULL    |       |
| name        | varchar(255) | NO   |     | NULL    |       |
| description | text         | YES  |     | NULL    |       |
+-------------+--------------+------+-----+---------+-------+
 */

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.query.Query;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="rprj_groups")
public class Group extends DBEntity {
    @Id
    @GeneratedValue(generator="UUID")
    @GenericGenerator(
            name="UUID",
            strategy="ch.rra.rprj.model.IdGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(16)")
    private String id;

    @Column(name="name", unique=true, updatable=false)
    private String name;

    @Column(name="description", columnDefinition="TEXT")
    private String description;

    @ManyToMany(mappedBy = "groups", cascade = {CascadeType.ALL})
    private Set<User> users = new HashSet<>();


    public Group() {}
    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getId() { return id; }
    //public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public void beforeInsert(DBMgr dbMgr) throws DBException {
        SessionFactory sessionFactory = dbMgr.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        // Check that name is unique
        boolean uniqueName = this.checkUniqueName(session);
        System.out.println("Check uniqueName:\t" + uniqueName);

        if (!uniqueName) {
            //throw new DBException("Name '" + this.name + "' not unique!");
            // Delete duplicates
            /**/
            Query query = session.createQuery("FROM Group where name = :name");
            query.setParameter("name", this.name);
            List<Group> results2 = (List<Group>) query.list();
            for (Group u1 : results2) {
                session.delete(u1);
            }
             /**/
        }
        tx.commit();
        session.close();
    }

    @Override
    public void afterDelete(DBMgr dbMgr) throws DBException {
        System.out.println("Group.afterDelete: start.");
        //super.afterDelete(dbMgr);
        String sql = "delete from rprj_users_groups where group_id='" + this.id + "'";
        System.out.println("Group.afterDelete: " + sql);
        boolean ret = dbMgr.db_execute(sql);
        if(!ret)
            throw new DBException("Unable to delete record from rprj_users_groups");
        System.out.println("Group.afterDelete: end.");
    }

    private boolean checkUniqueName(Session session) {
        Query query = session.createQuery(
                "Select COUNT(*), U.name FROM Group U where name = :name " +
                "GROUP BY U.name");
        query.setParameter("name", this.name);
        return query.list().size() == 0;
    }
}
