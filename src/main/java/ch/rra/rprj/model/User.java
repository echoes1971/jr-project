package ch.rra.rprj.model;


/*
mysql> desc rprj_users;
+----------+--------------+------+-----+---------+-------+
| Field    | Type         | Null | Key | Default | Extra |
+----------+--------------+------+-----+---------+-------+
| id       | varchar(16)  | NO   | PRI | NULL    |       |
| login    | varchar(255) | NO   |     | NULL    |       |
| pwd      | varchar(255) | NO   |     | NULL    |       |
| pwd_salt | varchar(4)   | YES  |     |         |       |
| fullname | text         | YES  |     | NULL    |       |
| group_id | varchar(16)  | NO   | MUL | NULL    |       |
+----------+--------------+------+-----+---------+-------+
6 rows in set (0.00 sec)
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
@Table(name="rprj_users")
public class User extends DBEntity {
    @Id
    @GeneratedValue(generator="UUID")
    @GenericGenerator(
            name="UUID",
            strategy="ch.rra.rprj.model.IdGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(16)")
    private String id;

    @Column(name="login", unique=true, updatable=false)
    private String login;
    private String pwd;
    private String pwd_salt;

    @Column(name="fullname", columnDefinition="TEXT")
    private String fullname;

    @Column(name="group_id", columnDefinition="VARCHAR(16)")
    private String group_id;


    @ManyToMany(cascade = { CascadeType.ALL } )
    @JoinTable(
            name = "rprj_users_groups",
            joinColumns = {
                    @JoinColumn(
                            name = "user_id",
                            referencedColumnName = "id") },
            inverseJoinColumns = {
                    @JoinColumn(
                            name = "group_id",
                            referencedColumnName = "id") }
    )
    private Set<Group> groups = new HashSet<>();

    public User() {}
    public User(String login, String pwd, String fullname, String group_id) {
        this.login = login;
        this.pwd = pwd;
        this.pwd_salt = "";
        this.fullname = fullname;
        this.group_id = group_id;
    }

    public String getId() {
        return id;
    }
//    public void setId(String id) { this.id = id; }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd_salt() {
        return pwd_salt;
    }
    public void setPwd_salt(String pwd_salt) {
        this.pwd_salt = pwd_salt;
    }

    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGroup_id() {
        return group_id;
    }
    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", pwd='" + pwd + '\'' +
                ", pwd_salt='" + pwd_salt + '\'' +
                ", fullname='" + fullname + '\'' +
                ", group_id='" + group_id + '\'' +
                ", groups=" + groups +
                '}';
    }

    @Override
    public void beforeInsert(DBMgr dbMgr) throws DBException {
        SessionFactory sessionFactory = dbMgr.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        // Check that login name is unique
        boolean uniqueLogin = this._checkUniqueLogin(session);
        System.out.println("Check: " + uniqueLogin);

        // TODO NO! Throw an exception here!!!
        if(!uniqueLogin) {
            //throw new DBException("Login '" + this.login + "' not unique!");
            // Delete duplicates
            /**/
            Query query = session.createQuery("FROM User U where login = :login");
            query.setParameter("login", this.login);
            List<User> results2 = (List<User>) query.list();
            for(User u1 : results2) {
                session.delete(u1);
            }
             /**/
        }
        tx.commit();
        session.close();
    }

    @Override
    public void afterInsert(DBMgr dbMgr) throws DBException {
        super.afterInsert(dbMgr);

        // Create user's own group
        this._createGroup(dbMgr);
        // Check that the association with group_id exists in the association table
        this._checkGroupAssociation(dbMgr);
    }

    @Override
    public void afterUpdate(DBMgr dbMgr) throws DBException {
        //super.afterUpdate(dbMgr);
        this._checkGroupAssociation(dbMgr);
    }

    @Override
    public void beforeDelete(DBMgr dbMgr) throws DBException {
        super.beforeDelete(dbMgr);
        HashSet<Group> newgroups = new HashSet<>();
        for(Group g : this.getGroups()) {
            if(!g.getId().startsWith("-"))
                newgroups.add(g);
        }
        this.setGroups(newgroups);
    }

/*
    @Override
    public void afterDelete(DBMgr dbMgr) throws DBException {
        System.out.println("User.afterDelete: start.");
        System.out.println("User.afterDelete: id=" + this.id);
        this._deleteGroup(dbMgr);
        System.out.println("User.afterDelete: end.");
    }
*/
/*
    function _after_delete(&$dbmgr) {
        $cerca=new DBEUserGroup();
        $cerca->setValue('user_id',$this->getValue('id'));
        $lista = $dbmgr->search($cerca, $uselike=0);
        foreach($lista as $ass) {
            $dbmgr->delete($ass);
        }
        $this->_deleteGroup($dbmgr);
    }
*/
    private void _createGroup(DBMgr dbMgr) throws DBException {
        Group group = new Group(this.getLogin(), "Private group for " + this.getLogin());
        //Set<User> users = new HashSet<>();
        //users.add(this);
        //group.setUsers(users);
        //group.getUsers().add(this);
        group = (Group) dbMgr.insert(group);
        dbMgr.db_execute(
                "insert into rprj_users_groups values ('" + this.id + "','" + group.getId() + "')");
        this.getGroups();
        //this.setGroup_id(group.getId());
    }
    private void _deleteGroup(DBMgr dbMgr) {
        SessionFactory sessionFactory = dbMgr.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        /*
        Group group = (Group) session.get(Group.class, this.getGroup_id());
        session.delete(group);
        */
/*
        // Delete private group
        String hql = "Select user_id, group_id FROM rprj_groups"
                + " where name = '" + this.login + "'";
        hql = "FROM Group WHERE name = :name";
        Query query = session.createQuery(hql);
        query.setParameter("name",this.login);
        List<Group> results = (List<Group>) query.list();
        for(Group g : results) {
            System.out.println("User._deleteGroup: deleting " + g.toString());
            session.delete(g);
        }
*/
        /*
        for(Group g : this.getGroups()) {
            System.out.println("User._deleteGroup: g=" + g.toString());
            if(g.getUsers().size() == 1
                    && ((User) g.getUsers().iterator().next()).getId()==this.getId()
                    && g.getName()==this.getLogin()
            ) {
                System.out.println("User._deleteGroup: deleting " + g.toString());
                session.delete(g);
            }
        }
        */

        tx.commit();
        session.close();
    }
    private void _checkGroupAssociation(DBMgr dbMgr) throws DBException {
        String hql = "Select user_id, group_id FROM rprj_users_groups"
                    + " where user_id = '" + this.id + "'"
                    + "   and group_id = '" + this.group_id + "'";
        List results = dbMgr.db_query(hql);
        if(results.size()==0) {
            System.out.println("insert into rprj_users_groups values ('" + this.id + "','" + this.group_id + "')");
            dbMgr.db_execute(
                    "insert into rprj_users_groups values ('" + this.id + "','" + this.group_id + "')");
        }
    }
    private boolean _checkUniqueLogin(Session session) {
        String hql = "Select COUNT(*), U.login FROM User U where login = :login " +
                "GROUP BY U.login";
        Query query = session.createQuery(hql);
        query.setParameter("login", this.login);
        List results = query.list();

        return results.size() == 0;
    }

}
