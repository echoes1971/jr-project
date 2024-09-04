package ch.rra.rprj.model;

import ch.rra.rprj.model.cms.DBEFolder;
import ch.rra.rprj.model.cms.DBENote;
import ch.rra.rprj.model.cms.DBEPage;
import ch.rra.rprj.model.contacts.DBECountry;
import ch.rra.rprj.model.core.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.*;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;

public class CoreTest { //extends TestCase {
    private static final Logger log = LogManager.getLogger(CoreTest.class);
    private ObjectMgr objMgr;


    @BeforeSuite
    public void setUpSuite() throws Exception {

//        System.getProperties().stringPropertyNames().stream().sorted().forEach(s -> { System.out.println(s + ":\t" + System.getProperty(s));});
//        user.dir:	C:\Users\rocco\Projects\jr-project
//        user.home:	C:\Users\rocco

        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/application.properties"));
        try {
            props.load(getClass().getResourceAsStream("/local.properties"));
        } catch(IOException | NullPointerException ex) {
            //ex.printStackTrace();
        }
        try {
            props.load(getClass().getResourceAsStream("/dev.properties"));
        } catch(IOException | NullPointerException ex) {
            //ex.printStackTrace();
        }

        for(String k : props.stringPropertyNames()) {
            log.info(k + ": " + props.getProperty(k));
        }

        DBConnectionProvider conn = null;
        switch (props.getProperty("db.conn.provider")) {
            case "hibernate":
                conn = new HDBConnection(props);
            default:
                // ***
        }

        objMgr = new ObjectMgr();
        objMgr.setConn(conn);
//        objMgr.setUp();

        objMgr.setUp();
        _initDB();
        objMgr.tearDown();
    }

    @BeforeTest
    public void setUp() throws Exception {
        objMgr.setUp();
    }

    @AfterTest
    protected void tearDown() throws Exception {
        objMgr.tearDown();
    }

    // .\mvnw.cmd -Dtest=CoreTest#testInitDB test
    //@Test
    private void _initDB() {

        Integer db_version = objMgr.db_version();
        System.out.println("DB Version: " + db_version);

        if(db_version>0) return;

        System.out.println(JsonParser.parseString("[1,'ccc',false]"));
        InputStream is = getClass().getResourceAsStream("/initialDB.json");
//        log.info("is: "+is);
        Reader reader = new InputStreamReader(is);
//        log.info("reader: "+reader);
        JsonElement initialDB = JsonParser.parseReader(reader);

        for(JsonElement jsonArray : initialDB.getAsJsonObject().get("countries").getAsJsonArray()) {
            int i = 0;
            JsonArray ja = (JsonArray) jsonArray;
            DBECountry newDBE = new DBECountry(
                    ja.get(i++).getAsString(), ja.get(i++).getAsString(),ja.get(i++).getAsString(),ja.get(i++).getAsString(),ja.get(i++).getAsString(),
                    ja.get(i++).getAsString(), ja.get(i++).getAsString(),ja.get(i++).getAsString(),ja.get(i++).getAsString(),ja.get(i++).getAsString(),
                    ja.get(i++).getAsString(), ja.get(i++).getAsString(),ja.get(i++).getAsString(),ja.get(i++).getAsString()
            );
            System.out.println(newDBE.toString());
            try {
                objMgr.insert(newDBE);
            } catch(DBException dbex) {
//                dbex.printStackTrace();
                break;
            }
//            break;
        }

        HashMap<String, Group> groups = new HashMap();
        for(JsonElement jsonArray : initialDB.getAsJsonObject().get("groups").getAsJsonArray()) {
            int i = 0;
            JsonArray ja = (JsonArray) jsonArray;
            Group newDBE = new Group(ja.get(0).getAsString(),ja.get(1).getAsString(),ja.get(2).getAsString());
            try {
                newDBE = (Group) objMgr.insert(newDBE);
                groups.put(newDBE.getId(), newDBE);
            } catch(DBException dbex) {
                dbex.printStackTrace();
                break;
            }
            System.out.println(newDBE.toString());
//            break;
        }

        JsonArray users_groups = initialDB.getAsJsonObject().get("users_groups").getAsJsonArray();
        for(JsonElement jsonArray : initialDB.getAsJsonObject().get("users").getAsJsonArray()) {
            int i = 0;
            JsonArray ja = (JsonArray) jsonArray;
//            System.out.println(" ja: " + ja);
            User newDBE = new User(
                    ja.get(0).getAsString(),ja.get(1).getAsString(),ja.get(2).getAsString(),
                    ja.get(4).getAsString(),ja.get(5).getAsString()
            );
            for(JsonElement je : users_groups) {
                JsonArray ug = (JsonArray) je;
                /*
                System.out.println(" newDBE.getId(): " + newDBE.getId());
                System.out.println(" user_group: " + ug.toString());
                System.out.println(" user_group[0]: " + ug.get(0).getAsString());
                System.out.println(" user_group[1]: " + groups.get(ug.get(1).getAsString()));
                 */
                if(!ug.get(0).getAsString().equals(newDBE.getId())) continue;

                Group g = groups.get(ug.get(1).getAsString());
//                System.out.println(" group: " + g);
                newDBE.addGroup(g);
            }
            System.out.println(newDBE.toString());
            try {
                objMgr.insert(newDBE);
                objMgr.setDbeUser(newDBE);
                //objMgr.setUserGroupsList(newDBE.getGroups());
            } catch(DBException dbex) {
                dbex.printStackTrace();
                break;
            }
//            break;
        }

        for(JsonElement jsonArray : initialDB.getAsJsonObject().get("folders").getAsJsonArray()) {
            int i = 0;
            JsonArray ja = (JsonArray) jsonArray;
            //System.out.println(" ja: " + ja);
            DBEFolder newDBE = new DBEFolder(
                    ja.get(i++).getAsString(), // id
                    ja.get(i++).getAsString(), // owner
                    ja.get(i++).getAsString(), // group_id
                    ja.get(i++).getAsString(), // permissions
                    // Creator
                    ja.get(i++).getAsString(),
                    ja.get(i++).isJsonNull() ? null : Timestamp.valueOf(ja.get(i-1).getAsString()),
                    // Last modify
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(),
                    ja.get(i++).isJsonNull() ? null : Timestamp.valueOf(ja.get(i-1).getAsString()),
                    // Deleted by
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(),
                    ja.get(i++).isJsonNull() || ja.get(i-1).getAsString().equals("0000-00-00 00:00:00") ? null : Timestamp.valueOf(ja.get(i-1).getAsString()),

                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(),
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(),
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(),
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(),
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString()
            );
            System.out.println(newDBE.toString());
            try {
                objMgr.insert(newDBE);
            } catch(DBException dbex) {
                dbex.printStackTrace();
                break;
            }
//            break;
        }

        for(JsonElement jsonArray : initialDB.getAsJsonObject().get("pages").getAsJsonArray()) {
            int i = 0;
            JsonArray ja = (JsonArray) jsonArray;
            //System.out.println(" ja: " + ja);
            DBEPage newDBE = new DBEPage(
                    ja.get(i++).getAsString(), // id
                    ja.get(i++).getAsString(), // owner
                    ja.get(i++).getAsString(), // group_id
                    ja.get(i++).getAsString(), // permissions
                    // Creator
                    ja.get(i++).getAsString(),
                    ja.get(i++).isJsonNull() ? null : Timestamp.valueOf(ja.get(i-1).getAsString()),
                    // Last modify
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(),
                    ja.get(i++).isJsonNull() ? null : Timestamp.valueOf(ja.get(i-1).getAsString()),
                    // Deleted by
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(),
                    ja.get(i++).isJsonNull() || ja.get(i-1).getAsString().equals("0000-00-00 00:00:00") ? null : Timestamp.valueOf(ja.get(i-1).getAsString()),

                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(), // father_id
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(), // name
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(), // description
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(), // html
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString(), // fk_obj_id
                    ja.get(i++).isJsonNull() ? null : ja.get(i-1).getAsString()  // language
            );
            System.out.println(newDBE.toString());
            try {
                objMgr.insert(newDBE);
            } catch(DBException dbex) {
                dbex.printStackTrace();
                break;
            }
//            break;
        }

        for(JsonElement jsonArray : initialDB.getAsJsonObject().get("db_version").getAsJsonArray()) {
            int i = 0;
            JsonArray ja = (JsonArray) jsonArray;
            DBEDBVersion newDBE = new DBEDBVersion(ja.get(0).getAsString(),ja.get(1).getAsInt());
            try {
                objMgr.insert(newDBE);
            } catch(DBException dbex) {
                dbex.printStackTrace();
                break;
            }
            System.out.println(newDBE.toString());
//            break;
        }
    }
    // .\mvnw.cmd -Dtest=CoreTest#testInitDB test
    //@Test
    public void testInitDB() {
        _initDB();
    }

    // ./mvnw -Dtest=CoreTest#testGenerator test
    @Test
    public void testGenerator() {
        IdGenerator gen = new IdGenerator();
        String myid = gen.generateMyId();
        System.out.println("UUID: " + myid + " (" + myid.length() + ")");
    }

    // ./mvnw -Dtest=CoreTest#testLog test
    @Test
    public void testLog() {
        System.out.println("**** Test Log");

        String ip = "192.168.56.110";
        String ip2 = "192.168.56.111";

        DBELog log = objMgr.log(ip,"note one", "note two");
        System.out.println("log: "+log);
        assert log!=null : "Unable to create log entry";

        log = objMgr.log(ip,"note one", "note three");
        System.out.println("log: "+log);
        assert log!=null : "Unable to update log entry";

        DBELog log2 = objMgr.log(ip2,"nota uno", "nota due");
        System.out.println("log2: "+log2);
        assert log2!=null : "Unable to create log entry";

        try {
            objMgr.delete(log);
            objMgr.delete(log2);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUsers() {
        int initial_users = _listUsers();
        int initial_groups = _listGroups();
        int initial_usersgroups = _listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test Create");
        User user = new User("ramonserrano", "cippalippa", "Roberto R.A.", "-3");
        System.out.println("Saving: " + user.toString());

        List users_groups;
        List groups;
        // Save
        try {
            user = (User) objMgr.insert(user);
            System.out.println("User: " + user.toString());
            _listGroups();

            users_groups = objMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups where user_id='" + user.getId() + "'",new HashMap<String,Object>(), Object.class,false);
            _printObjectList(users_groups);

            assert users_groups.size()==2 : "Not all associations created";
            //if (users_groups.size() != 2) fail("Not all associations created");

            System.out.println("**** Test Read");
            _listUsers();
            _listGroups();
            _listUsersGroups();

            if (false) {
                System.out.println("**** Test Update");
                user.setFullname("Mr.Echoes");
                user = (User) objMgr.update(user);
                System.out.println("User: " + user.toString());
                _listUsers();
            }

            user = (User) objMgr.refresh(user);
            System.out.println("Refreshed user: " + user.toString());

            System.out.println("**** Test Delete");
            user = (User) objMgr.delete(user);
            System.out.println("Deleted user: " + user.toString());
            System.out.println();
            _listUsers();
            _listGroups();
            users_groups = objMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups where user_id='" + user.getId() + "'",new HashMap<String,Object>(), Object.class,false);
            _printObjectList(users_groups);
            if (users_groups.size() != 0) {
                objMgr.db_execute("delete FROM rprj_users_groups where user_id='" + user.getId() + "'");
                assert users_groups.size() != 0 : "Not all associations were deleted";
            }
            groups = objMgr.db_query("SELECT id, name, description FROM rprj_groups where name='" + user.getLogin() + "'",new HashMap<String,Object>(), Object.class,false);
            _printObjectList(groups);
            if (groups.size() != 0) {
                objMgr.db_execute("delete FROM rprj_groups where name='" + user.getLogin() + "'");
                assert groups.size() != 0 : "Not all groups were deleted";
            }
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }

        current_users = _listUsers();
        current_groups = _listGroups();
        current_usersgroups = _listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");
        assert initial_users==current_users && initial_groups==current_groups && initial_usersgroups==current_usersgroups : "Not all rows deleted!!!!";
    }

    @Test
    public void testManyToMany2() {
        int initial_users = _listUsers();
        int initial_groups = _listGroups();
        int initial_usersgroups = _listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test ManyToMany");
        Group group = new Group("a_group", "test many to many");
        User user = new User("ramonserrano", "cippalippa", "Roberto R.A.", "-3");
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
        assert !user.getGroup_id().equals(group.getId()) : "User has the extra group as foreign key: " + group.toString();

        System.out.println("**** Test Read");
        _listUsers();
        _listGroups();
        _listUsersGroups();

        // Why?
        try {
            log.info("user: "+user.toString());
            user = (User) objMgr.refresh(user);
            log.info("  --> "+user.toString());
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }

        /**/
        /**/
        try {
            objMgr.delete(user);
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }
        /**/
        users_groups = objMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups");
        _printObjectList(users_groups);
        System.out.println("===============================");
        try {
            /* TODO fix this
            [(conn=8) Cannot delete or update a parent row: a foreign key constraint fails
                    (`rproject`.`rprj_users_groups`, CONSTRAINT `FKrthf9phmby8n9bnk0x23at7el` FOREIGN KEY (`group_id`) REFERENCES `rprj_groups` (`id`))]
            [delete from rprj_groups where id=?]
             */
            objMgr.delete(group);
            user.getGroups().remove(group);
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }
        users_groups = objMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups");
        _printObjectList(users_groups);

        current_users = _listUsers();
        current_groups = _listGroups();
        current_usersgroups = _listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");
        assert initial_users==current_users && initial_groups==current_groups && initial_usersgroups==current_usersgroups : "Not all rows deleted!!!!";
    }

    @Test
    public void testGroups() {
        int initial_users = _listUsers();
        int initial_groups = _listGroups();
        int initial_usersgroups = _listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test Create");
        Group group = new Group("ramonserrano", "Roberto's Group");
        System.out.println("Saving: " + group.toString());

        // Save
        try {
            group = (Group) objMgr.insert(group);
            System.out.println("Group: " + group.toString());

            System.out.println("**** Test Read");
            _listGroups();

            System.out.println("**** Test Update");
            group.setDescription("Mr.Echoes");
            group = (Group) objMgr.update(group);
            System.out.println("Group: " + group.toString());
            _listGroups();

            System.out.println("**** Test Delete");
            group = (Group) objMgr.delete(group);
            System.out.println("Group: " + group.toString());
            _listGroups();
        } catch (DBException dbex) {
            dbex.printStackTrace();
        }

        current_users = _listUsers();
        current_groups = _listGroups();
        current_usersgroups = _listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");
        assert initial_users==current_users && initial_groups==current_groups && initial_usersgroups==current_usersgroups : "Not all rows deleted!!!!";
    }

    // ./mvnw -Dtest=CoreTest#testUserGroup test
    @Test
    public void testUserGroup() {
        int initial_users = _listUsers();
        int initial_groups = _listGroups();
        int initial_usersgroups = _listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test User Group");
        //String[] user_names = { "user01", "user02", "user03" };
        //String[] group_names = { "group01", "group02", "group03", "group04" };
        String[] user_names = {
                "user01"
                , "user02", "user03", "user04", "user05", "user06", "user07", "user08", "user09"
                , "user0A", "user0B", "user0C", "user0D", "user0E", "user0F"
        };
        String[] group_names = {
                "group01"
                , "group02", "group03", "group04", "group05"
                , "group06", "group07", "group08", "group09"
        };

        // Create users and groups
        System.out.println("* Create users and groups");
        Vector<User> testUsers = _createTestUsers(user_names);
        Vector<Group> testGroups = _createTestGroups(group_names);

        current_users = _listUsers();
        current_groups = _listGroups();
        current_usersgroups = _listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");

        Vector<User> users2 = new Vector<>();
        try {
            int skip = -1;
            for (User u : testUsers) {
                int step = -1;
//                u = (User) objMgr.refresh(u);
                for (Group g : testGroups) {
                    step++;
                    System.out.println("step=" + step + "\tskip=" + skip);
                    if (step == skip) continue;
//                    u = (User) objMgr.refresh(u);
                    System.out.println("u1=" + u);
                    System.out.println("g=" + g);
                    u.getGroups().add(g);
//                    u = (User) objMgr.update(u);
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
                u = (User) objMgr.update(u);
                if (u != null)
                    users2.add(u);
                skip++;
            }
            current_users = _listUsers();
            current_groups = _listGroups();
            current_usersgroups = _listUsersGroups();
            System.out.println("Users:\t\t" + initial_users + " => " + current_users);
            System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
            System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
            System.out.println("==============================================================");
        } catch (DBException dbex) {
            dbex.printStackTrace();
//            assert false : "Unable to create associations";
        }

        // Delete users and groups
        _deleteTestUsers(users2);
        current_users = _listUsers();
        current_groups = _listGroups();
        current_usersgroups = _listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");

        _deleteTestGroups(testGroups);
        current_users = _listUsers();
        current_groups = _listGroups();
        current_usersgroups = _listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");

        assert initial_users==current_users && initial_groups==current_groups && initial_usersgroups==current_usersgroups : "Not all rows deleted!!!!";
    }

    // ./mvnw -Dtest=CoreTest#testSearch test
    @Test
    public void testSearch() {
        System.out.println("**** Test Search");
        User searchDBE = new User();
        //searchDBE.setLogin("adm");
        //searchDBE.setPwd("adm");
        //searchDBE.setFullname("Administrator");
        searchDBE.setFullname("dmin");

        List<DBEntity> res = objMgr.search(searchDBE);
        System.out.println("res=" + res.size());

        assert res.size()>0 : "Administrator not found";

        for (DBEntity dbe : res) {
            System.out.println(dbe.toString());
        }
    }

    // ./mvnw -Dtest=CoreTest#testDBVersion test
    @Test
    public void testDBVersion() {
        System.out.println("**** Test DB Version");

        System.out.println("DB Version: " + objMgr.db_version());
    }

    // ./mvnw -Dtest=CoreTest#testExists test
    @Test
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
        assert exists : "Administrator does not exists!";

        System.out.println("**** Test Does Not Exists");
        searchDBE = new User();
        searchDBE.setFullname("___dmin");

        exists = false;
        try {
            exists = objMgr.exists(searchDBE);
        } catch (DBException e) {
            e.printStackTrace();
        }
        assert !exists : "User " + searchDBE.getFullname() + " exists!";
    }

    // ./mvnw -Dtest=CoreTest#testLogin test
    @Test
    public void testLogin() {
        int initial_users = _listUsers();
        int initial_groups = _listGroups();
        int initial_usersgroups = _listUsersGroups();

        int current_users = initial_users;
        int current_groups = initial_groups;
        int current_usersgroups = initial_usersgroups;

        System.out.println("**** Test Login");

        // Create test users
        String[] user_names = {"user01", "user02"};
        Vector<User> testUsers = _createTestUsers(user_names);
        System.out.println("");
        System.out.println("** Current:");
        _listUsers();
        _listGroups();
        _listUsersGroups();

        User myuser;
        System.out.println("* Loggin existing user...");
        myuser = objMgr.login("user01", "pwd_user01");
        assert myuser!=null : "Unable to login existing user";
        System.out.println(myuser);
        System.out.println("* Loggin existing user wrong pwd...");
        myuser = objMgr.login("user02", "wd_user0");
        assert myuser==null : "Logged in with wrong password";
        System.out.println(myuser);
        System.out.println("* Loggin non existing user...");
        myuser = objMgr.login("__user01", "pwd_user01");
        assert myuser==null : "Logged in non existing user";
        System.out.println(myuser);

        // Delete test users
        _deleteTestUsers(testUsers);
        current_users = _listUsers();
        current_groups = _listGroups();
        current_usersgroups = _listUsersGroups();
        System.out.println("Users:\t\t" + initial_users + " => " + current_users);
        System.out.println("Groups:\t\t" + initial_groups + " => " + current_groups);
        System.out.println("UsersGroups:\t" + initial_usersgroups + " => " + current_usersgroups);
        System.out.println("==============================================================");

        assert initial_users==current_users && initial_groups==current_groups && initial_usersgroups==current_usersgroups : "Not all rows deleted!!!!";
    }

    @Test
    public void testListUsers() {
        int ret = _listUsers();
        log.info("ret: "+ret);
        ret = _listGroups();
        log.info("ret: "+ret);
        ret = _listUsersGroups();
        log.info("ret: "+ret);
    }

    private int _listUsers() {
        List<DBEntity> users = objMgr.search(new User());
        for(DBEntity u : users) {
            System.out.println(u.toString());
        }
        System.out.println("Users: " + users.size());
        System.out.println();
        return users.size();
    }
    private int _listGroups() {
        List<DBEntity> users = objMgr.search(new Group());
        for(DBEntity u : users) {
            System.out.println(u.toString());
        }
        System.out.println("Groups: " + users.size());
        System.out.println();
        return users.size();
    }
    private int _listUsersGroups() {
        List objs = objMgr.db_query("SELECT user_id, group_id FROM rprj_users_groups", new HashMap<String,Object>(), Object.class,false);
        _printObjectList(objs);
        System.out.println("Objects: " + objs.size());
        System.out.println();
        return objs.size();
    }
    private void _printObjectList(List objects) {
        try {
            for(Object[] obj : (List<Object[]>) objects) {
                System.out.print("Object:");
                for (Object o : obj) {
                    System.out.print(" " + o);
                }
                System.out.println();
            }
        } catch (ClassCastException cce) {
            try {
                for (HashMap hm : (List<HashMap>) objects) {
                    System.out.print("hm>");
                    for(Object k : hm.keySet()) {
                        System.out.print(" " + k + ": " + hm.get(k));
                    }
                    System.out.println();
                }
            } catch(ClassCastException cce2) {
                for (Object obj : objects) {
                    System.out.println("obj> " + obj);
                }
            }
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
            assert false : "Error while deleting groups";
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
            assert false : "Error while deleting users";
        }
    }


    // ./mvnw -Dtest=CoreTest#testObject test
    @Test
    public void testObject() {
        System.out.println("**** Test Object");
        int initial_objects_count = objMgr.search(new DBENote()).size();
        int final_objects_count   = initial_objects_count;

        // Create Test Users
        String[] usernames = {"user01","user02","user03","user04","user05"};
        Vector<User> testUsers = _createTestUsers(usernames);

        List<DBEObject> objects = new ArrayList<>();

        System.out.println("* Insert");
        String[] object_names = {"object one", "object two", "object three", "object four", "object five"};
        testUsers.stream().forEach(testUser -> {
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (String name: object_names) {
                DBEObject obj = new DBENote(name, testUser.getLogin() + "'s object '" + name + "'");
                //obj.setDefaultValues(objMgr);
                //obj.setPermissions("rwxrwxrwx");
                System.out.println("obj: " + obj);
                try {
                    obj = (DBEObject) objMgr.insert(obj);
                    System.out.println("-> " + obj);
                    assert obj.getId()!=null : "ID not created";
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
            System.out.println("initial_objects_count: "+initial_objects_count);
            System.out.println("object_names.length: "+object_names.length);
            System.out.println("res: "+res.size());
            assert (res.size()-initial_objects_count)==object_names.length : "Error with privileges";
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
                    log.info("obj: " + obj.toString());
                    obj = (DBEObject) objMgr.delete(obj);
                    assert obj.getDeleted_by()!=null && !obj.getDeleted_by().isEmpty() : "Deleted_by not populated!";
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


        int objects_created = objects.size();
        System.out.println("Objects created: " + objects_created);
        assert objects_created==(object_names.length*usernames.length) : "No object was created.";
        final_objects_count   = objMgr.search(new DBENote()).size();
        System.out.println("Objects count: " + initial_objects_count + " -> " + final_objects_count);
        assert initial_objects_count==final_objects_count : "Not all objects were deleted";
    }

    // ./mvnw -Dtest=CoreTest#testDbeById test
    @Test
    public void testDbeById() {
        System.out.println("**** Test Search DBE By ID");

        System.out.println("* Search");
        // Test User and Group
        objMgr.login("adm","adm");
        DBEntity dbe = objMgr.dbeById("-3");
        System.out.println("dbe: " + dbe);
        assert dbe!=null : "Group not found by ID";
        dbe = objMgr.dbeById("-1");
        System.out.println("dbe: " + dbe);
        assert dbe!=null : "User not found by ID";
    }

    // ./mvnw -Dtest=CoreTest#testObjectById test
    @Test
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
        for(User testUser : testUsers) {
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
        }
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
                assert obj2!=null : "Error searching full object";
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
        assert initial_objects_count==final_objects_count : "Not all objects were deleted";
    }

    // ./mvnw -Dtest=CoreTest#testObjectByName test
    @Test
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
        try {
            objMgr.insert(new DBENote("kezzo", "funziona kazzo!"));
        } catch(DBException e) {
            e.printStackTrace();
        }
        for(User testUser: testUsers) {
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (String name: object_names) {
                DBENote obj = new DBENote(name, "description of object '" + name + "'");
                //System.out.println("obj: " + obj);
                try {
                    obj = (DBENote) objMgr.insert(obj);
                    System.out.println("-> " + obj);
                    if(obj!=null) objects.add(obj);
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println();

        System.out.println("* Search");
        testUsers.stream().forEach(testUser -> {
            System.out.println("* User: " + testUser.getLogin() + " " + testUser.getId());
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (DBEObject o : objects) {
                List<DBEObject> objs = objMgr.objectByName(o.getName());
                objs.forEach(obj -> System.out.println("Partial:" + obj));
                assert objs.size()!=0 : "Error searching objects";

                List<DBEObject> objs2 = objMgr.fullObjectByName(o.getName());
                objs2.forEach(obj2 -> System.out.println("Full:\t" + obj2));
                assert objs2.size()>0 : "Error searching full objects";
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


        System.out.println("Objects created: "+objects.size()+" / "+object_names.length);
        assert objects.size()==object_names.length : "Not all objects were created!";
        final_objects_count   = objMgr.search(new DBENote()).size();
        System.out.println("Objects count: " + initial_objects_count + " -> " + final_objects_count);
        assert initial_objects_count==final_objects_count : "Not all objects were deleted!";
    }
}
