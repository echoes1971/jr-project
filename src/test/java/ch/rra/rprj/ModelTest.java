package ch.rra.rprj;

import ch.rra.rprj.model.*;
import junit.framework.TestCase;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class ModelTest extends TestCase {
    private DBMgr dbMgr;

    @Override
    protected void setUp() throws Exception {
        dbMgr = new DBMgr();
        dbMgr.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        dbMgr.tearDown();
    }

    public void testGenerator() {
        IdGenerator gen = new IdGenerator();
        String myid = gen.generateMyId();
        System.out.println("UUID: " + myid + " (" + myid.length() + ")");
    }

    public void testUsers() {
        System.out.println("**** Test Create");
        User user = new User("roberto","echoestrade","Roberto R.A.", "-3");
        System.out.println("Saving: " + user.toString());

        List users_groups;
        // Save
        try {
            user = (User) dbMgr.insert(user);
            System.out.println("User: " + user.toString());
            dbMgr.listGroups();
            users_groups = dbMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups where user_id='" + user.getId() + "'");
            dbMgr.printObjectList(users_groups);
            if(users_groups.size()!=2)  fail("Not all associations created");

            System.out.println("**** Test Read");
            dbMgr.listUsers();
            dbMgr.listGroups();
            dbMgr.listUsersGroups();

            if(false) {
                System.out.println("**** Test Update");
                user.setFullname("Mr.Echoes");
                user = (User) dbMgr.update(user);
                System.out.println("User: " + user.toString());
                dbMgr.listUsers();
            }

            user = (User) dbMgr.refresh(user);
            System.out.println("Refreshed user: " + user.toString());

            System.out.println("**** Test Delete");
            user = (User) dbMgr.delete(user);
            System.out.println("Deleted user: " + user.toString());
            System.out.println();
            dbMgr.listUsers();
            dbMgr.listGroups();
            users_groups = dbMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups where user_id='" + user.getId() + "'");
            dbMgr.printObjectList(users_groups);
            if(users_groups.size()!=0) {
                dbMgr.db_execute("delete FROM rprj_users_groups where user_id='" + user.getId() + "'");
                fail("Not all associations deleted");
            }
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }
    }

    public void testManyToMany1() {
        System.out.println("**** Test ManyToMany");
        Group group = new Group("a_group", "test many to many");
        User user = new User("roberto","echoestrade","Roberto R.A.", "-3");
        user.getGroups().add(group);
        System.out.println("Saving: " + user.toString());

        // Save
        try {
            user = (User) dbMgr.insert(user);
            group = user.getGroups().iterator().next();
            System.out.println("User: " + user.toString());
            System.out.println("Group: " + group.toString());
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }

        System.out.println("**** Test Read");
        dbMgr.listUsers();
        dbMgr.listGroups();
        dbMgr.listUsersGroups();

        try {
            dbMgr.delete(group);
            user.getGroups().remove(group);
            dbMgr.delete(user);
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }

        dbMgr.listUsersGroups();
    }

    public void testManyToMany2() {
        System.out.println("**** Test ManyToMany");
        Group group = new Group("a_group", "test many to many");
        User user = new User("roberto","echoestrade","Roberto R.A.", "-3");
        System.out.println("Saving: " + user.toString());

        List users_groups;

        // Save
        try {
            group = (Group) dbMgr.insert(group);
            System.out.println("Group 1: " + group.toString());
            user.getGroups().add(group);
            user = (User) dbMgr.insert(user);
            group = user.getGroups().iterator().next();
            System.out.println("User: " + user.toString());
            System.out.println("Group 2: " + group.toString());
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }
        if(user.getGroup_id()==group.getId()) {
            fail("User has the extra group as foreign key: " + group.toString());
        }

        System.out.println("**** Test Read");
        dbMgr.listUsers();
        dbMgr.listGroups();
        dbMgr.listUsersGroups();

        /**/
        try {
            dbMgr.delete(group);
            user.getGroups().remove(group);
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }
        users_groups = dbMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups");
        dbMgr.printObjectList(users_groups);
        if(users_groups.size()!=3)  fail("Not all rows deleted");
        System.out.println("===============================");
        /**/
        try {
            dbMgr.delete(user);
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }
        /**/
        users_groups = dbMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups");
        dbMgr.printObjectList(users_groups);
        if(users_groups.size()!=3)  fail("Not all rows deleted");
    }

    public void testGroups() {
        System.out.println("**** Test Create");
        Group group = new Group("roberto","Roberto's Group");
        System.out.println("Saving: " + group.toString());

        // Save
        try {
            group = (Group) dbMgr.insert(group);
            System.out.println("Group: " + group.toString());

            System.out.println("**** Test Read");
            dbMgr.listGroups();

            System.out.println("**** Test Update");
            group.setDescription("Mr.Echoes");
            group = (Group) dbMgr.update(group);
            System.out.println("Group: " + group.toString());
            dbMgr.listGroups();

            System.out.println("**** Test Delete");
            group = (Group) dbMgr.delete(group);
            System.out.println("Group: " + group.toString());
            dbMgr.listGroups();
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }
    }

    public void testUserGroup() {
        System.out.println("**** Test Many-to-many");
        //String[] user_names = { "user01", "user02", "user03" };
        //String[] group_names = { "group01", "group02", "group03", "group04" };
        String[] user_names = { "user01" };
        String[] group_names = { "group01", "group02" };

        // Create users and groups
        System.out.println("* Create users and groups");
        Vector<User> users = new Vector<User>();
        Vector<Group> groups = new Vector<Group>();
        try {
            for(String s : user_names) {
                users.add((User) dbMgr.insert(new User(
                    s, "pwd_" + s,
                    "User " + s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase(),
                    "-3"))
                );
            }
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }
        try {
            for(String s : group_names) {
                groups.add((Group) dbMgr.insert(new Group(
                    s,
                    "Group " + s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                ));
            }
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }

        dbMgr.listUsers();
        dbMgr.listGroups();
        dbMgr.listUsersGroups();
        System.out.println("==============================================================");


        // TODO create a many to many relationship and start deleting stuff

        Vector<User> users2 = new Vector<User>();
        try {
            int skip = -1;
            for(User u : users) {
                int step = -1;
                User u1 = u;
                for(Group g : groups) {
                    step++;
                    System.out.println("step="+step+"\tskip="+skip);
                    if(step==skip) continue;
                    u = (User) dbMgr.refresh(u);
                    System.out.println("u="+u);
                    System.out.println("g="+g);
                    u.getGroups().add(g);
                    u1 = (User) dbMgr.update(u1);
                    /*
                    Set<Group> g1 = u.getGroups();
                    g1.add(g);
                    u.setGroups(g1);
                    dbMgr.update(u);
                    */
                    //g.getUsers().add(u);
                    //dbMgr.update(g);
                    /*
                    Set<User> u1 = g.getUsers();
                    u1.add(u);
                    g.setUsers(u1);
                    dbMgr.update(g);
                     */
                }
                users2.add(u1);
                skip++;
            }
            dbMgr.listUsers();
            dbMgr.listGroups();
            dbMgr.listUsersGroups();
            System.out.println("==============================================================");
        } catch(DBException dbex) {
            dbex.printStackTrace();
            fail("Unable to create associations");
        }

        // Delete users and groups
        System.out.println("* Delete users and groups");
        try {
            for(User x : users2) {
                //x = (User) dbMgr.refresh(x);
                System.out.println("Deleting " + x.toString());
                dbMgr.delete(x);
            }
        } catch(DBException dbex) {
            dbex.printStackTrace();
            fail("Error while deleting users");
        }
        dbMgr.listUsers();
        dbMgr.listGroups();
        dbMgr.listUsersGroups();
        System.out.println("==============================================================");
        try {
            for(Group x : groups) {
                System.out.println("Deleting " + x.toString());
                x = (Group) dbMgr.refresh(x);
                if(x==null) {
                    System.out.println("         already deleted.");
                    continue;
                }
                System.out.println("Deleting refreshed " + x.toString());
                dbMgr.delete(x);
            }
        } catch(DBException dbex) {
            dbex.printStackTrace();
            fail("Error while deleting groups");
        }

        dbMgr.listUsers();
        dbMgr.listGroups();
        dbMgr.listUsersGroups();
        System.out.println("==============================================================");
    }

}
