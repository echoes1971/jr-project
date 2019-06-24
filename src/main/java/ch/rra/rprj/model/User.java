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


    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "rprj_users_groups",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "group_id") }
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
        boolean uniqueLogin = this.checkUniqueLogin(session);
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

        this._createGroup(dbMgr);
    }
/*
    function _after_insert(&$dbmgr) {
        $this->_checkGroupAssociation($dbmgr);
    }
    function _after_update(&$dbmgr) {
        $this->_checkGroupAssociation($dbmgr);
    }
*/
    @Override
    public void afterDelete(DBMgr dbMgr) throws DBException {
        this._deleteGroup(dbMgr);
    }
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

    function _checkGroupAssociation(&$dbmgr) {
        $ug=new DBEUserGroup();
        $ug->setValue('user_id',$this->getValue('id'));
        $ug->setValue('group_id',$this->getValue('group_id'));
        $exists = $dbmgr->exists($ug);
        if(!$exists)
            $dbmgr->insert($ug);
    }
*/
    private void _createGroup(DBMgr dbMgr) throws DBException {
        Group group = new Group(this.getLogin(), "Private group for " + this.getLogin());
        Set<User> users = new HashSet<>();
        users.add(this);
        group.setUsers(users);
        //group.getUsers().add(this);
        group = (Group) dbMgr.insert(group);
        this.setGroup_id(group.getId());
    }
    private void _deleteGroup(DBMgr dbMgr) {
        SessionFactory sessionFactory = dbMgr.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Group group = (Group) session.get(Group.class, this.getGroup_id());
        session.delete(group);

        tx.commit();
        session.close();
    }

    private boolean checkUniqueLogin(Session session) {
        String hql = "Select COUNT(*), U.login FROM User U where login = :login " +
                "GROUP BY U.login";
        Query query = session.createQuery(hql);
        query.setParameter("login", this.login);
        List results = query.list();

        return results.size() == 0;
    }

}
