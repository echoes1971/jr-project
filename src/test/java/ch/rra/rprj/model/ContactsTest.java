package ch.rra.rprj.model;

import ch.rra.rprj.model.contacts.DBECompany;
import ch.rra.rprj.model.contacts.DBECountry;
import ch.rra.rprj.model.contacts.DBEPeople;
import ch.rra.rprj.model.core.DBEObject;
import ch.rra.rprj.model.core.DBEntity;
import ch.rra.rprj.model.core.User;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ContactsTest extends TestCase {
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

    // ./mvnw -Dtest=ContactsTest#testCountries test
    public void testCountries() {
        List<DBEntity> res;

        System.out.println("**** Test Countries");
        DBECountry search = new DBECountry();

        String[] countryCodes = {"CHE","ESP","FRA","ITA","JPN","USA"};

        for(String code : countryCodes) {
            search.setISO_3166_1_3_Letter_Code(code);
            res = objMgr.search(search, false, "", true);
            res.forEach(x -> System.out.println(x.toString()));
            if (res.size() != 1) fail("Unable to find country: "+code);
        }
    }

    // ./mvnw -Dtest=CmsTest#testCompany test
    public void testCompany() {
        System.out.println("**** Test Company");
        int initial_objects_count = objMgr.search(new DBECompany()).size();
        int final_objects_count   = initial_objects_count;

        // Create Test Users
        //String[] usernames = {"user01"};
        String[] usernames = {"user01","user02","user03","user04","user05"};
        Vector<User> testUsers = _createTestUsers(usernames);

        List<DBEObject> objects = new ArrayList<>();

        System.out.println("* Insert");
        String[] object_names = {"DBECompany one", "DBECompany two", "DBECompany three", "DBECompany four", "DBECompany five"};
        testUsers.stream().forEach(testUser -> {
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (String name: object_names) {
                DBECompany obj = new DBECompany(name, "description of object '" + name + "'");
                obj.setStreet("Rue de Genève, 7");
                obj.setCity("Ecublens");
                obj.setState("Vaud");
                obj.setZip("1024");
                obj.setFk_countrylist_id("167");
                System.out.println("obj: " + obj);
                try {
                    obj = (DBECompany) objMgr.insert(obj);
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

            List<DBEntity> res = objMgr.search(new DBECompany());
            System.out.println("res: "+res.size());
            if((res.size()-initial_objects_count)!=object_names.length) fail("Error with privileges");
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


        final_objects_count   = objMgr.search(new DBECompany()).size();
        System.out.println("Objects count: " + initial_objects_count + " -> " + final_objects_count);
        if(initial_objects_count!=final_objects_count) fail("Not all objects were deleted");
    }

    // ./mvnw -Dtest=CmsTest#testPeople test
    public void testPeople() {
        System.out.println("**** Test People");
        int initial_objects_count = objMgr.search(new DBEPeople()).size();
        int final_objects_count   = initial_objects_count;

        // Create Test Users
        //String[] usernames = {"user01"};
        String[] usernames = {"user01","user02","user03","user04","user05"};
        Vector<User> testUsers = _createTestUsers(usernames);

        List<DBEObject> objects = new ArrayList<>();

        System.out.println("* Insert");
        String[] object_names = {"DBEPeople one", "DBEPeople two", "DBEPeople three", "DBEPeople four", "DBEPeople five"};
        testUsers.stream().forEach(testUser -> {
            objMgr.setDbeUser(testUser);
            objMgr.setUserGroupsList(testUser.getGroups());

            for (String name: object_names) {
                DBEPeople obj = new DBEPeople(name, "description of object '" + name + "'");
                obj.setStreet("Rue de Genève, 7");
                obj.setCity("Ecublens");
                obj.setState("Vaud");
                obj.setZip("1024");
                obj.setFk_countrylist_id("167");
                System.out.println("obj: " + obj);
                try {
                    obj = (DBEPeople) objMgr.insert(obj);
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

            List<DBEntity> res = objMgr.search(new DBEPeople());
            System.out.println("res: "+res.size());
            if((res.size()-initial_objects_count)!=object_names.length) fail("Error with privileges");
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


        final_objects_count   = objMgr.search(new DBEPeople()).size();
        System.out.println("Objects count: " + initial_objects_count + " -> " + final_objects_count);
        if(initial_objects_count!=final_objects_count) fail("Not all objects were deleted");
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
}
