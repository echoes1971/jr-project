package ch.rra.rprj.model;

import ch.rra.rprj.model.cms.*;
import ch.rra.rprj.model.contacts.DBECompany;
import ch.rra.rprj.model.contacts.DBECountry;
import ch.rra.rprj.model.contacts.DBEPeople;
import ch.rra.rprj.model.core.*;
import junit.framework.TestCase;

public class TypeScriptTest extends TestCase {
    // ./mvnw -Dtest=TypeScriptTest#testTypescript test

    /**
     * Not a real test, just to generate Typescript classes
     */
    public void testTypescript() {
        DBEntity dbe;

        dbe = new DBEObjectReal();
        System.out.println(dbe.toTypeScript());

        System.out.println("/* **** Core **** */");
        System.out.println();
        dbe = new User();
        System.out.println(dbe.toTypeScript());
        dbe = new Group();
        System.out.println(dbe.toTypeScript());
        dbe = new DBELog();
        System.out.println(dbe.toTypeScript());
        dbe = new DBEDBVersion();
        System.out.println(dbe.toTypeScript());

        System.out.println("/* **** Contacts **** */");
        dbe = new DBECompany();
        System.out.println(dbe.toTypeScript());
        dbe = new DBECountry();
        System.out.println(dbe.toTypeScript());
        dbe = new DBEPeople();
        System.out.println(dbe.toTypeScript());

        System.out.println("/* **** CMS **** */");
        dbe = new DBEFolder();
        System.out.println(dbe.toTypeScript());
        dbe = new DBELink();
        System.out.println(dbe.toTypeScript());
        dbe = new DBENews();
        System.out.println(dbe.toTypeScript());
        dbe = new DBENote();
        System.out.println(dbe.toTypeScript());
        dbe = new DBEPage();
        System.out.println(dbe.toTypeScript());
    }
}
