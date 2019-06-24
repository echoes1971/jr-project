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

        // Save
        try {
            user = (User) dbMgr.insert(user);
            System.out.println("User: " + user.toString());

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

            System.out.println("**** Test Delete");
            user = (User) dbMgr.delete(user);
            System.out.println("User: " + user.toString());
            dbMgr.listUsers();
            dbMgr.listGroups();
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
        dbMgr.listUsersGroups();
        System.out.println("===============================");
        /**/
        try {
            dbMgr.delete(user);
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }
        /**/
        dbMgr.listUsersGroups();
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
        String[] user_names = { "user01", "user02", "user03" };
        String[] group_names = { "group01", "group02", "group03", "group04" };

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

        // Delete users and groups
        System.out.println("* Delete users and groups");
        try {
            for(Group x : groups) { dbMgr.delete(x); }
            for(User x : users) { dbMgr.delete(x); }
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }

        dbMgr.listUsers();
        dbMgr.listGroups();
    }

    public void printObjectList(List objects) {
        try {
            for(Object[] obj : (List<Object[]>) objects) {
                System.out.print("Employee:");
                for (Object o : obj) {
                    System.out.print(" " + o);
                }
                System.out.println("");
            }
        } catch (ClassCastException cce) {
            try {
                for (HashMap hm : (List<HashMap>) objects) {
                    System.out.print("hm>");
                    for(Object k : hm.keySet()) {
                        System.out.print(" " + k + ": " + hm.get(k));
                    }
                    System.out.println("");
                    //System.out.println("hm> " + hm);
                }
            } catch(ClassCastException cce2) {
                for (Object obj : objects) {
                    System.out.println("obj> " + obj);
                }
            }
        }
    }

}
