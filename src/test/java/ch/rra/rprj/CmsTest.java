package ch.rra.rprj;

import ch.rra.rprj.model.DBException;
import ch.rra.rprj.model.ObjectMgr;
import ch.rra.rprj.model.cms.DBENote;
import ch.rra.rprj.model.core.DBEObject;
import ch.rra.rprj.model.core.DBEntity;
import ch.rra.rprj.model.core.User;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CmsTest extends TestCase {
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

    // ./mvnw -Dtest=CmsTest#testNotes test
    public void testNotes() {

        System.out.println("**** Test Notes");
        int initial_objects_count = objMgr.search(new DBENote()).size();
        int final_objects_count   = initial_objects_count;

        // Create Test Users
        String[] usernames = {"user01"};
        //String[] usernames = {"user01","user02","user03","user04","user05"};
        Vector<User> testUsers = _createTestUsers(usernames);

        List<DBEObject> objects = new ArrayList<DBEObject>();

        System.out.println("* Insert");
        String[] object_names = {"note one", "note two", "note three", "note four", "note five"};
        testUsers.stream().forEach(testUser -> {
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (String name: object_names) {
                DBENote obj = new DBENote(name, "description of object '" + name + "'");
                //obj.setDefaultValues(objMgr);
                //obj.setPermissions("rwxrwxrwx");
                System.out.println("obj: " + obj);
                try {
                    obj = (DBENote) objMgr.insert(obj);
                    System.out.println("-> " + obj);
                    objects.add(obj);
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("");

        System.out.println("* Search");
        testUsers.stream().forEach(testUser -> {
            System.out.println("* User: " + testUser.getLogin() + " " + testUser.getId());
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            List<DBEntity> res = objMgr.search(new DBENote());
            System.out.println("res: "+res.size());
            if(res.size()!=object_names.length) fail("Error with privileges");
            res.stream().forEach((dbe) -> {
                System.out.println(" " + dbe);
            });
            System.out.println("");
        });

        testUsers.stream().forEach(testUser -> {
            System.out.println("* User: " + testUser.getLogin() + " " + testUser.getId());
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            System.out.println("* Delete 1");
            List<DBEObject> deleted_objects = new ArrayList<DBEObject>();
            objects.stream().forEach(obj -> {
                try {
                    obj = (DBEObject) objMgr.delete(obj);
                    deleted_objects.add(obj);
                    System.out.println("-> " + obj);
                } catch (DBException e) {
                    //e.printStackTrace();
                }
            });
            //final_objects_count   = objMgr.search(new DBEObject()).size();
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

    private Vector<User> _createTestUsers(String[] user_names) {
        Vector<User> testUsers = new Vector<User>();
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
}
