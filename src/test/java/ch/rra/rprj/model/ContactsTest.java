package ch.rra.rprj.model;

import ch.rra.rprj.model.ObjectMgr;
import ch.rra.rprj.model.cms.DBENote;
import ch.rra.rprj.model.contacts.DBECountry;
import ch.rra.rprj.model.core.DBEntity;
import junit.framework.TestCase;

import java.util.List;

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
}
