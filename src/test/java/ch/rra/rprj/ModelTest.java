package ch.rra.rprj;

import ch.rra.rprj.model.*;
import junit.framework.TestCase;
import org.hibernate.*;
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
        int initial_users = dbMgr.listUsers();
        int initial_groups = dbMgr.listGroups();
        int initial_usersgroups = dbMgr.listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test Create");
        User user = new User("roberto","echoestrade","Roberto R.A.", "-3");
        System.out.println("Saving: " + user.toString());

        List users_groups;
        List groups;
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
                fail("Not all associations were deleted");
            }
            groups = dbMgr.db_query("SELECT id, name, description FROM rprj_groups where name='" + user.getLogin() + "'");
            dbMgr.printObjectList(groups);
            if(groups.size()!=0) {
                dbMgr.db_execute("delete FROM rprj_groups where name='" + user.getLogin() + "'");
                fail("Not all groups were deleted");
            }
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }

        current_users = dbMgr.listUsers();
        current_groups = dbMgr.listGroups();
        current_usersgroups = dbMgr.listUsersGroups();
        System.out.println("Users:\t\t"+initial_users+" => "+current_users);
        System.out.println("Groups:\t\t"+initial_groups+" => "+current_groups);
        System.out.println("UsersGroups:\t"+initial_usersgroups+" => "+current_usersgroups);
        System.out.println("==============================================================");
        if(initial_users!=current_users || initial_groups!=current_groups || initial_usersgroups!=current_usersgroups) {
            fail("Not all rows deleted!!!!");
        }
    }

    public void testManyToMany2() {
        int initial_users = dbMgr.listUsers();
        int initial_groups = dbMgr.listGroups();
        int initial_usersgroups = dbMgr.listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test ManyToMany");
        Group group = new Group("a_group", "test many to many");
        User user = new User("roberto","echoestrade","Roberto R.A.", "-3");
        System.out.println("Saving: " + user.toString());

        List users_groups;

        // Save
        try {
            group = (Group) dbMgr.insert(group);
            System.out.println("Group 1: " + group.toString());
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }
        try {
            user = (User) dbMgr.insert(user);
            //user = (User) dbMgr.refresh(user);
            user.getGroups().add(group);
            user = (User) dbMgr.update(user);
            //group = user.getGroups().iterator().next();
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

        try {
            user = (User) dbMgr.refresh(user);
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }

        /**/
        try {
            dbMgr.delete(group);
            user.getGroups().remove(group);
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }
        users_groups = dbMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups");
        dbMgr.printObjectList(users_groups);
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

        current_users = dbMgr.listUsers();
        current_groups = dbMgr.listGroups();
        current_usersgroups = dbMgr.listUsersGroups();
        System.out.println("Users:\t\t"+initial_users+" => "+current_users);
        System.out.println("Groups:\t\t"+initial_groups+" => "+current_groups);
        System.out.println("UsersGroups:\t"+initial_usersgroups+" => "+current_usersgroups);
        System.out.println("==============================================================");
        if(initial_users!=current_users || initial_groups!=current_groups || initial_usersgroups!=current_usersgroups) {
            fail("Not all rows deleted!!!!");
        }
    }

    public void testGroups() {
        int initial_users = dbMgr.listUsers();
        int initial_groups = dbMgr.listGroups();
        int initial_usersgroups = dbMgr.listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

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

        current_users = dbMgr.listUsers();
        current_groups = dbMgr.listGroups();
        current_usersgroups = dbMgr.listUsersGroups();
        System.out.println("Users:\t\t"+initial_users+" => "+current_users);
        System.out.println("Groups:\t\t"+initial_groups+" => "+current_groups);
        System.out.println("UsersGroups:\t"+initial_usersgroups+" => "+current_usersgroups);
        System.out.println("==============================================================");
        if(initial_users!=current_users || initial_groups!=current_groups || initial_usersgroups!=current_usersgroups) {
            fail("Not all rows deleted!!!!");
        }
    }

    public void testUserGroup() {
        int initial_users = dbMgr.listUsers();
        int initial_groups = dbMgr.listGroups();
        int initial_usersgroups = dbMgr.listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test User Group");
        //String[] user_names = { "user01", "user02", "user03" };
        //String[] group_names = { "group01", "group02", "group03", "group04" };
        String[] user_names = { "user01", "user02", "user03", "user04", "user05", "user06", "user07", "user08", "user09"
            , "user0A", "user0B", "user0C", "user0D", "user0E", "user0F"
        };
        String[] group_names = { "group01", "group02", "group03", "group04", "group05", "group06", "group07", "group08", "group09" };

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

        current_users = dbMgr.listUsers();
        current_groups = dbMgr.listGroups();
        current_usersgroups = dbMgr.listUsersGroups();
        System.out.println("Users:\t\t"+initial_users+" => "+current_users);
        System.out.println("Groups:\t\t"+initial_groups+" => "+current_groups);
        System.out.println("UsersGroups:\t"+initial_usersgroups+" => "+current_usersgroups);
        System.out.println("==============================================================");


        // TODO create a many to many relationship and start deleting stuff

        Vector<User> users2 = new Vector<User>();
        try {
            int skip = -1;
            for(User u : users) {
                int step = -1;
                u = (User) dbMgr.refresh(u);
                for(Group g : groups) {
                    step++;
                    System.out.println("step="+step+"\tskip="+skip);
                    if(step==skip) continue;
//                    u = (User) dbMgr.refresh(u);
                    System.out.println("u1="+u);
                    System.out.println("g="+g);
                    u.getGroups().add(g);
                    u = (User) dbMgr.update(u);
                    /*
                    Set<Group> g1 = u.getGroups();
                    g1.add(g);
                    u.setGroups(g1);;
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
                if(u!=null)
                    users2.add(u);
                skip++;
            }
            current_users = dbMgr.listUsers();
            current_groups = dbMgr.listGroups();
            current_usersgroups = dbMgr.listUsersGroups();
            System.out.println("Users:\t\t"+initial_users+" => "+current_users);
            System.out.println("Groups:\t\t"+initial_groups+" => "+current_groups);
            System.out.println("UsersGroups:\t"+initial_usersgroups+" => "+current_usersgroups);
            System.out.println("==============================================================");
        } catch(DBException dbex) {
            dbex.printStackTrace();
            fail("Unable to create associations");
        }

        // Delete users and groups
        System.out.println("* Delete users and groups");
        try {
            for(User x : users2) {
                x = (User) dbMgr.refresh(x);
                if(x==null) {
                    continue;
                }
                System.out.println("Deleting " + x.toString());
                dbMgr.delete(x);
            }
        } catch(DBException dbex) {
            dbex.printStackTrace();
            fail("Error while deleting users");
        }
        current_users = dbMgr.listUsers();
        current_groups = dbMgr.listGroups();
        current_usersgroups = dbMgr.listUsersGroups();
        System.out.println("Users:\t\t"+initial_users+" => "+current_users);
        System.out.println("Groups:\t\t"+initial_groups+" => "+current_groups);
        System.out.println("UsersGroups:\t"+initial_usersgroups+" => "+current_usersgroups);
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

        current_users = dbMgr.listUsers();
        current_groups = dbMgr.listGroups();
        current_usersgroups = dbMgr.listUsersGroups();
        System.out.println("Users:\t\t"+initial_users+" => "+current_users);
        System.out.println("Groups:\t\t"+initial_groups+" => "+current_groups);
        System.out.println("UsersGroups:\t"+initial_usersgroups+" => "+current_usersgroups);
        System.out.println("==============================================================");
        if(initial_users!=current_users || initial_groups!=current_groups || initial_usersgroups!=current_usersgroups) {
            fail("Not all rows deleted!!!!");
        }
    }

    // ./mvnw -Dtest=ModelTest#testSearch test
    public void testSearch() {
        System.out.println("**** Test Read");
        dbMgr.listUsers();
        //dbMgr.listGroups();
        //dbMgr.listUsersGroups();

        System.out.println("**** Test Search");
        User searchDBE = new User();
        searchDBE.setLogin("adm");
        //searchDBE.setPwd("adm");
        //searchDBE.setFullname("Administrator");

        try {
            List<DBEntity> res = dbMgr.search(searchDBE);
            //System.out.println("res=" + res);
            for(DBEntity dbe : res) {
                Hibernate.initialize(dbe);
                System.out.println(dbe.toString());
            }
        } catch (DBException e) {
            e.printStackTrace();
        }

        System.out.println("TODO");
    }
}
