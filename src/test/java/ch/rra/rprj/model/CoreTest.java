package ch.rra.rprj.model;

import ch.rra.rprj.model.cms.DBENote;
import ch.rra.rprj.model.core.*;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CoreTest extends TestCase {
    private ObjectMgr objMgr;

    @Override
    protected void setUp() throws Exception {
        objMgr = new ObjectMgr();
        objMgr.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        objMgr.tearDown();
    }

    public void testGenerator() {
        IdGenerator gen = new IdGenerator();
        String myid = gen.generateMyId();
        System.out.println("UUID: " + myid + " (" + myid.length() + ")");
    }

    public void testUsers() {
        int initial_users = objMgr.listUsers();
        int initial_groups = objMgr.listGroups();
        int initial_usersgroups = objMgr.listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test Create");
        User user = new User("roberto", "echoestrade", "Roberto R.A.", "-3");
        System.out.println("Saving: " + user.toString());

        List users_groups;
        List groups;
        // Save
        try {
            user = (User) objMgr.insert(user);
            System.out.println("User: " + user.toString());
            objMgr.listGroups();
            users_groups = objMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups where user_id='" + user.getId() + "'");
            objMgr.printObjectList(users_groups);
            if (users_groups.size() != 2) fail("Not all associations created");

            System.out.println("**** Test Read");
            objMgr.listUsers();
            objMgr.listGroups();
            objMgr.listUsersGroups();

            if (false) {
                System.out.println("**** Test Update");
                user.setFullname("Mr.Echoes");
                user = (User) objMgr.update(user);
                System.out.println("User: " + user.toString());
                objMgr.listUsers();
            }

            user = (User) objMgr.refresh(user);
            System.out.println("Refreshed user: " + user.toString());

            System.out.println("**** Test Delete");
            user = (User) objMgr.delete(user);
            System.out.println("Deleted user: " + user.toString());
            System.out.println();
            objMgr.listUsers();
            objMgr.listGroups();
            users_groups = objMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups where user_id='" + user.getId() + "'");
            objMgr.printObjectList(users_groups);
            if (users_groups.size() != 0) {
                objMgr.db_execute("delete FROM rprj_users_groups where user_id='" + user.getId() + "'");
                fail("Not all associations were deleted");
            }
            groups = objMgr.db_query("SELECT id, name, description FROM rprj_groups where name='" + user.getLogin() + "'");
            objMgr.printObjectList(groups);
            if (groups.size() != 0) {
                objMgr.db_execute("delete FROM rprj_groups where name='" + user.getLogin() + "'");
                fail("Not all groups were deleted");
            }
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }

        current_users = objMgr.listUsers();
        current_groups = objMgr.listGroups();
        current_usersgroups = objMgr.listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");
        if (initial_users != current_users || initial_groups != current_groups || initial_usersgroups != current_usersgroups) {
            fail("Not all rows deleted!!!!");
        }
    }

    public void testManyToMany2() {
        int initial_users = objMgr.listUsers();
        int initial_groups = objMgr.listGroups();
        int initial_usersgroups = objMgr.listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test ManyToMany");
        Group group = new Group("a_group", "test many to many");
        User user = new User("roberto", "echoestrade", "Roberto R.A.", "-3");
        System.out.println("Saving: " + user.toString());

        List users_groups;

        // Save
        try {
            group = (Group) objMgr.insert(group);
            System.out.println("Group 1: " + group.toString());
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }
        try {
            user = (User) objMgr.insert(user);
            //user = (User) dbMgr.refresh(user);
            user.getGroups().add(group);
            user = (User) objMgr.update(user);
            //group = user.getGroups().iterator().next();
            System.out.println("User: " + user.toString());
            System.out.println("Group 2: " + group.toString());
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }
        if (user.getGroup_id().equals(group.getId())) {
            fail("User has the extra group as foreign key: " + group.toString());
        }

        System.out.println("**** Test Read");
        objMgr.listUsers();
        objMgr.listGroups();
        objMgr.listUsersGroups();

        try {
            user = (User) objMgr.refresh(user);
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }

        /**/
        try {
            objMgr.delete(group);
            user.getGroups().remove(group);
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }
        users_groups = objMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups");
        objMgr.printObjectList(users_groups);
        System.out.println("===============================");
        /**/
        try {
            objMgr.delete(user);
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }
        /**/
        users_groups = objMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups");
        objMgr.printObjectList(users_groups);

        current_users = objMgr.listUsers();
        current_groups = objMgr.listGroups();
        current_usersgroups = objMgr.listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");
        if (initial_users != current_users || initial_groups != current_groups || initial_usersgroups != current_usersgroups) {
            fail("Not all rows deleted!!!!");
        }
    }

    public void testGroups() {
        int initial_users = objMgr.listUsers();
        int initial_groups = objMgr.listGroups();
        int initial_usersgroups = objMgr.listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test Create");
        Group group = new Group("roberto", "Roberto's Group");
        System.out.println("Saving: " + group.toString());

        // Save
        try {
            group = (Group) objMgr.insert(group);
            System.out.println("Group: " + group.toString());

            System.out.println("**** Test Read");
            objMgr.listGroups();

            System.out.println("**** Test Update");
            group.setDescription("Mr.Echoes");
            group = (Group) objMgr.update(group);
            System.out.println("Group: " + group.toString());
            objMgr.listGroups();

            System.out.println("**** Test Delete");
            group = (Group) objMgr.delete(group);
            System.out.println("Group: " + group.toString());
            objMgr.listGroups();
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }

        current_users = objMgr.listUsers();
        current_groups = objMgr.listGroups();
        current_usersgroups = objMgr.listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");
        if (initial_users != current_users || initial_groups != current_groups || initial_usersgroups != current_usersgroups) {
            fail("Not all rows deleted!!!!");
        }
    }

    // ./mvnw -Dtest=CoreTest#testUserGroup test
    public void testUserGroup() {
        int initial_users = objMgr.listUsers();
        int initial_groups = objMgr.listGroups();
        int initial_usersgroups = objMgr.listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test User Group");
        //String[] user_names = { "user01", "user02", "user03" };
        //String[] group_names = { "group01", "group02", "group03", "group04" };
        String[] user_names = {"user01", "user02", "user03", "user04", "user05", "user06", "user07", "user08", "user09"
                , "user0A", "user0B", "user0C", "user0D", "user0E", "user0F"
        };
        String[] group_names = {"group01", "group02", "group03", "group04", "group05", "group06", "group07", "group08", "group09"};

        // Create users and groups
        System.out.println("* Create users and groups");
        Vector<User> testUsers = _createTestUsers(user_names);
        Vector<Group> testGroups = _createTestGroups(group_names);

        current_users = objMgr.listUsers();
        current_groups = objMgr.listGroups();
        current_usersgroups = objMgr.listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");

        Vector<User> users2 = new Vector<>();
        try {
            int skip = -1;
            for (User u : testUsers) {
                int step = -1;
                u = (User) objMgr.refresh(u);
                for (Group g : testGroups) {
                    step++;
                    System.out.println("step=" + step + "\tskip=" + skip);
                    if (step == skip) continue;
//                    u = (User) dbMgr.refresh(u);
                    System.out.println("u1=" + u);
                    System.out.println("g=" + g);
                    u.getGroups().add(g);
                    u = (User) objMgr.update(u);
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
                if (u != null)
                    users2.add(u);
                skip++;
            }
            current_users = objMgr.listUsers();
            current_groups = objMgr.listGroups();
            current_usersgroups = objMgr.listUsersGroups();
            System.out.println("Users:\t\t" + initial_users + " => " + current_users);
            System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
            System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
            System.out.println("==============================================================");
        } catch (DBException dbex) {
            dbex.printStackTrace();
            fail("Unable to create associations");
        }

        // Delete users and groups
        _deleteTestUsers(users2);
        current_users = objMgr.listUsers();
        current_groups = objMgr.listGroups();
        current_usersgroups = objMgr.listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");

        _deleteTestGroups(testGroups);
        current_users = objMgr.listUsers();
        current_groups = objMgr.listGroups();
        current_usersgroups = objMgr.listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");

        if (initial_users != current_users || initial_groups != current_groups || initial_usersgroups != current_usersgroups) {
            fail("Not all rows deleted!!!!");
        }
    }

    // ./mvnw -Dtest=CoreTest#testSearch test
    public void testSearch() {
        System.out.println("**** Test Search");
        User searchDBE = new User();
        //searchDBE.setLogin("adm");
        //searchDBE.setPwd("adm");
        //searchDBE.setFullname("Administrator");
        searchDBE.setFullname("dmin");

        List<DBEntity> res = objMgr.search(searchDBE);
        System.out.println("res=" + res.size());

        if (res.size() == 0) fail("Administrator not found");

        for (DBEntity dbe : res) {
            System.out.println(dbe.toString());
        }
    }

    // ./mvnw -Dtest=CoreTest#testDBVersion test
    public void testDBVersion() {
        System.out.println("**** Test DB Version");

        System.out.println("DB Version: " + objMgr.db_version());
    }

    // ./mvnw -Dtest=CoreTest#testExists test
    public void testExists() {
        System.out.println("**** Test Exists");
        User searchDBE = new User();
        searchDBE.setFullname("Administrator");

        boolean exists = false;
        try {
            exists = objMgr.exists(searchDBE);
        } catch (DBException e) {
            e.printStackTrace();
        }
        if (!exists) fail("Administrator does not exists!");

        System.out.println("**** Test Does Not Exists");
        searchDBE = new User();
        searchDBE.setFullname("___dmin");

        exists = false;
        try {
            exists = objMgr.exists(searchDBE);
        } catch (DBException e) {
            e.printStackTrace();
        }
        if (exists) fail("User " + searchDBE.getFullname() + " exists!");
    }

    // ./mvnw -Dtest=CoreTest#testLogin test
    public void testLogin() {
        int initial_users = objMgr.listUsers();
        int initial_groups = objMgr.listGroups();
        int initial_usersgroups = objMgr.listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test Login");

        // Create test users
        String[] user_names = {"user01", "user02"};
        Vector<User> testUsers = _createTestUsers(user_names);

        User myuser;
        System.out.println("* Loggin existing user...");
        myuser = objMgr.login("user01", "pwd_user01");
        if(myuser==null) fail("Unable to login existing user");
        System.out.println(myuser);
        System.out.println("* Loggin existing user wrong pwd...");
        myuser = objMgr.login("user02", "wd_user0");
        if(myuser!=null) fail("Logged in with wrong password");
        System.out.println(myuser);
        System.out.println("* Loggin non existing user...");
        myuser = objMgr.login("__user01", "pwd_user01");
        if(myuser!=null) fail("Logged in non existing user");
        System.out.println(myuser);

        // Delete test users
        _deleteTestUsers(testUsers);
        current_users = objMgr.listUsers();
        current_groups = objMgr.listGroups();
        current_usersgroups = objMgr.listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");

        if (initial_users != current_users || initial_groups != current_groups || initial_usersgroups != current_usersgroups) {
            fail("Not all rows deleted!!!!");
        }

    }

    private Vector<Group> _createTestGroups(String[] group_names) {
        Vector<Group> groups = new Vector<>();
        try {
            for (String s : group_names) {
                groups.add((Group) objMgr.insert(new Group(
                        s,
                        "Group " + s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                ));
            }
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }
        return groups;
    }
    private void _deleteTestGroups(Vector<Group> testGroups) {
        try {
            for (Group x : testGroups) {
                //System.out.println("Deleting " + x.toString());
                x = (Group) objMgr.refresh(x);
                if (x == null) {
                    //System.out.println("         already deleted.");
                    continue;
                }
                //System.out.println("Deleting refreshed " + x.toString());
                objMgr.delete(x);
            }
        } catch (DBException dbex) {
            dbex.printStackTrace();
            fail("Error while deleting groups");
        }
    }
    private Vector<User> _createTestUsers(String[] user_names) {
        Vector<User> testUsers = new Vector<>();
        //System.out.println("* Create test users and groups");
        try {
            for(String s : user_names) {
                testUsers.add((User) objMgr.insert(new User(
                        s, "pwd_" + s,
                        "User " + s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase(),
                        "-3"))
                );
            }
        } catch(DBException dbex) {
            dbex.printStackTrace();
        }
        return testUsers;
    }
    private void _deleteTestUsers(Vector<User> testUsers) {
        //System.out.println("* Delete Delete test users");
        try {
            for (User x : testUsers) {
                x = (User) objMgr.refresh(x);
                if (x == null) {
                    continue;
                }
                //System.out.println("Deleting " + x.toString());
                objMgr.delete(x);
            }
        } catch (DBException dbex) {
            dbex.printStackTrace();
            fail("Error while deleting users");
        }
    }

    // ./mvnw -Dtest=CoreTest#testLog test
    public void testLog() {
        System.out.println("**** Test Log");

        String ip = "192.168.56.110";
        String ip2 = "192.168.56.111";

        DBELog log = objMgr.log(ip,"note one", "note two");
        System.out.println("log: "+log);
        if(log==null)
            fail("Unable to create log entry");

        log = objMgr.log(ip,"note one", "note three");
        System.out.println("log: "+log);
        if(log==null)
            fail("Unable to update log entry");

        DBELog log2 = objMgr.log(ip2,"nota uno", "nota due");
        System.out.println("log2: "+log2);
        if(log2==null)
            fail("Unable to create log entry");

        try {
            objMgr.delete(log);
            objMgr.delete(log2);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    // ./mvnw -Dtest=CoreTest#testObject test
    public void testObject() {
        System.out.println("**** Test Object");
        int initial_objects_count = objMgr.search(new DBENote()).size();
        int final_objects_count   = initial_objects_count;

        // Create Test Users
        String[] usernames = {"user01"};
        //String[] usernames = {"user01","user02","user03","user04","user05"};
        Vector<User> testUsers = _createTestUsers(usernames);

        List<DBEObject> objects = new ArrayList<>();

        System.out.println("* Insert");
        String[] object_names = {"object one", "object two", "object three", "object four", "object five"};
        testUsers.stream().forEach(testUser -> {
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (String name: object_names) {
                DBEObject obj = new DBENote(name, "description of object '" + name + "'");
                //obj.setDefaultValues(objMgr);
                //obj.setPermissions("rwxrwxrwx");
                System.out.println("obj: " + obj);
                try {
                    obj = (DBEObject) objMgr.insert(obj);
                    System.out.println("-> " + obj);
                    objects.add(obj);
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println();

        System.out.println("* Search");
        testUsers.stream().forEach(testUser -> {
            System.out.println("* User: " + testUser.getLogin() + " " + testUser.getId());
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            List<DBEntity> res = objMgr.search(new DBENote());
            System.out.println("res: "+res.size());
            if(res.size()!=object_names.length) fail("Error with privileges");
            res.stream().forEach((dbe) -> System.out.println(" " + dbe));
            System.out.println();
        });

        testUsers.stream().forEach(testUser -> {
            System.out.println("* User: " + testUser.getLogin() + " " + testUser.getId());
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            System.out.println("* Delete 1");
            List<DBEObject> deleted_objects = new ArrayList<>();
            objects.stream().forEach(obj -> {
                try {
                    obj = (DBEObject) objMgr.delete(obj);
                    deleted_objects.add(obj);
                    System.out.println("-> " + obj);
                } catch (DBException e) {
                    //e.printStackTrace();
                }
            });
            //final_objects_count   = objMgr.search(new DBENote()).size();
            //System.out.println("Objects count: " + initial_objects_count + " -> " + final_objects_count);

            System.out.println("* Delete 2");
            deleted_objects.stream().forEach(obj -> {
                try {
                    obj = (DBEObject) objMgr.delete(obj);
                    System.out.println("-> " + obj);
                } catch (DBException e) {
                    //e.printStackTrace();
                }
            });
        });

        // Delete test users
        _deleteTestUsers(testUsers);


        final_objects_count   = objMgr.search(new DBENote()).size();
        System.out.println("Objects count: " + initial_objects_count + " -> " + final_objects_count);
        if(initial_objects_count!=final_objects_count) fail("Not all objects were deleted");
    }

    // ./mvnw -Dtest=CoreTest#testDbeById test
    public void testDbeById() {
        System.out.println("**** Test Search DBE By ID");

        System.out.println("* Search");
        // Test User and Group
        objMgr.login("adm","adm");
        DBEntity dbe = objMgr.dbeById("-3");
        System.out.println("dbe: " + dbe);
        if(dbe==null) fail("Group not found by ID");
        dbe = objMgr.dbeById("-1");
        System.out.println("dbe: " + dbe);
        if(dbe==null) fail("User not found by ID");
    }

    // ./mvnw -Dtest=CoreTest#testObjectById test
    public void testObjectById() {
        System.out.println("**** Test Search By ID");
        int initial_objects_count = objMgr.search(new DBENote()).size();
        int final_objects_count   = initial_objects_count;

        // Create Test Users
        String[] usernames = {"user01"};
        //String[] usernames = {"user01","user02","user03","user04","user05"};
        Vector<User> testUsers = _createTestUsers(usernames);

        List<DBEObject> objects = new ArrayList<>();

        System.out.println("* Insert");
        String[] object_names = {"object one", "object two", "object three", "object four", "object five"};
        testUsers.stream().forEach(testUser -> {
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (String name: object_names) {
                DBEObject obj = new DBENote(name, "description of object '" + name + "'");
                System.out.println("obj: " + obj);
                try {
                    obj = (DBEObject) objMgr.insert(obj);
                    System.out.println("-> " + obj);
                    objects.add(obj);
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println();

        System.out.println("* Search");
        testUsers.stream().forEach(testUser -> {
            System.out.println("* User: " + testUser.getLogin() + " " + testUser.getId());
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (DBEObject o : objects) {
                DBEObject obj = objMgr.objectById(o.getId());
                System.out.println("Partial:\t" + obj);
                //if (obj == null) fail("Error searching object");
                DBEObject obj2 = objMgr.fullObjectById(o.getId());
                System.out.println("Full:\t" + obj2);
                if (obj2 == null) fail("Error searching full object");
            }
        });

        testUsers.stream().forEach(testUser -> {
            System.out.println("* User: " + testUser.getLogin() + " " + testUser.getId());
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            System.out.println("* Delete 1");
            List<DBEObject> deleted_objects = new ArrayList<>();
            objects.stream().forEach(obj -> {
                try {
                    obj = (DBEObject) objMgr.delete(obj);
                    deleted_objects.add(obj);
                    System.out.println("-> " + obj);
                } catch (DBException e) {
                    //e.printStackTrace();
                }
            });

            System.out.println("* Delete 2");
            deleted_objects.stream().forEach(obj -> {
                try {
                    obj = (DBEObject) objMgr.delete(obj);
                    System.out.println("-> " + obj);
                } catch (DBException e) {
                    //e.printStackTrace();
                }
            });
        });

        // Delete test users
        _deleteTestUsers(testUsers);


        final_objects_count   = objMgr.search(new DBENote()).size();
        System.out.println("Objects count: " + initial_objects_count + " -> " + final_objects_count);
        if(initial_objects_count!=final_objects_count) fail("Not all objects were deleted");
    }

    // ./mvnw -Dtest=CoreTest#testObjectByName test
    public void testObjectByName() {
        System.out.println("**** Test Search By NAME");
        int initial_objects_count = objMgr.search(new DBENote()).size();
        int final_objects_count   = initial_objects_count;

        // Create Test Users
        String[] usernames = {"user01"};
        //String[] usernames = {"user01","user02","user03","user04","user05"};
        Vector<User> testUsers = _createTestUsers(usernames);

        List<DBEObject> objects = new ArrayList<>();

        System.out.println("* Insert");
        String[] object_names = {"object one", "object two", "object three", "object four", "object five"};
        testUsers.stream().forEach(testUser -> {
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (String name: object_names) {
                DBEObject obj = new DBENote(name, "description of object '" + name + "'");
                //System.out.println("obj: " + obj);
                try {
                    obj = (DBEObject) objMgr.insert(obj);
                    System.out.println("-> " + obj);
                    objects.add(obj);
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println();

        System.out.println("* Search");
        testUsers.stream().forEach(testUser -> {
            System.out.println("* User: " + testUser.getLogin() + " " + testUser.getId());
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (DBEObject o : objects) {
                List<DBEObject> objs = objMgr.objectByName(o.getName());
                objs.forEach(obj -> System.out.println("Partial:" + obj));
                if (objs.size()==0) fail("Error searching objects");

                List<DBEObject> objs2 = objMgr.fullObjectByName(o.getName());
                objs2.forEach(obj2 -> System.out.println("Full:\t" + obj2));
                if (objs2.size()==0) fail("Error searching full objects");
            }
        });

        // Delete objects
        testUsers.stream().forEach(testUser -> {
            System.out.println("* User: " + testUser.getLogin() + " " + testUser.getId());
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            System.out.println("* Delete 1");
            List<DBEObject> deleted_objects = new ArrayList<>();
            objects.stream().forEach(obj -> {
                try {
                    obj = (DBEObject) objMgr.delete(obj);
                    deleted_objects.add(obj);
                    System.out.println("-> " + obj);
                } catch (DBException e) {
                    //e.printStackTrace();
                }
            });

            System.out.println("* Delete 2");
            deleted_objects.stream().forEach(obj -> {
                try {
                    obj = (DBEObject) objMgr.delete(obj);
                    System.out.println("-> " + obj);
                } catch (DBException e) {
                    //e.printStackTrace();
                }
            });
        });

        // Delete test users
        _deleteTestUsers(testUsers);


        final_objects_count   = objMgr.search(new DBENote()).size();
        System.out.println("Objects count: " + initial_objects_count + " -> " + final_objects_count);
        if(initial_objects_count!=final_objects_count) fail("Not all objects were deleted");
    }
}
