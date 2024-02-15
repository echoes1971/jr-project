package ch.rra.rprj.model;

import ch.rra.rprj.model.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.Time;
import java.util.*;

public class DBMgr {

    protected DBConnectionProvider conn;

    public DBConnectionProvider getConn() {
        return conn;
    }

    public void setConn(DBConnectionProvider conn) {
        this.conn = conn;
    }

    private Logger log;

    private User dbeUser;
    private Set<Group> user_groups_list;

    public DBMgr() { log = LoggerFactory.getLogger(getClass()); }

    public boolean setUp() throws Exception {

        // Customize here.
        // See: https://www.digitalocean.com/community/tutorials/hibernate-tutorial-for-beginners
        Properties props = new Properties();
//        try {
            props.load(getClass().getResourceAsStream("/application.properties"));
//            log.debug("Project.getPropertiesFromFile: properties=${properties.size()}")
//            return properties
//        } catch(e) {
//            log.error("Project.getPropertiesFromFile: Error to read project properties file '${fileurl}'!");
//            log.error("Project.getPropertiesFromFile: $e");
////            return null
//        }
        //props.put("hibernate.connection.driver_class", "org.mariadb.jdbc.Driver");
//        props.put("hibernate.connection.url", "jdbc:mariadb://127.0.0.1:3306/rproject?zeroDateTimeBehavior=convertToNull");
//        props.put("hibernate.connection.username", "root");
        //props.put("hibernate.connection.password", "pankaj123");
        //props.put("hibernate.current_session_context_class", "thread");

        conn.connect();
        return true;
    }

    public void tearDown() throws Exception {
        conn.disconnect();
    }




    public User getDbeUser() { return dbeUser; }
    public void setDbeUser(User dbeUser) { this.dbeUser = dbeUser; }

    public Set<Group> getUserGroupsList() { return this.user_groups_list; }
    public void setUserGroupsList(Set<Group> user_groups_list) { this.user_groups_list = user_groups_list; }

    public boolean hasGroup(String group_id) {
        return this.user_groups_list.stream().anyMatch((group) -> group.getId().equals(group_id));
    }
    //function addGroup($group_id) { if(!in_array($group_id,$this->user_groups_list)) $this->user_groups_list[]=$group_id; }

    public User login(String login, String pwd) {
        User search = new User(login, pwd,null,null);
        List<DBEntity> res = this.search(search, false, null);

        User user = null;

        if(res.size()==1) {
            user = (User) res.get(0);
            log.info("groups: " + user.getGroups());
        }
        this.setDbeUser(user);
        this.setUserGroupsList(user==null ? null : user.getGroups());
        return user;
    }

    public Integer db_version() {
        DBEDBVersion searchDBE = new DBEDBVersion("rprj",null);
        Integer ret = -1;
        List<DBEntity> res = this.search(searchDBE);
        if(res.size()==1) ret = ((DBEDBVersion) res.get(0)).getVersion();
        return ret;
    }

    public boolean db_execute(String sql) { return conn.db_execute(sql); }
    public List<DBEntity> db_query(String sql) {
        return conn.db_query(sql, new HashMap<>(), null, false);
    }
    public List<DBEntity> db_query(String sql, HashMap<String,Object> hm, Class klass, boolean initializeLazyObjects) {
        return conn.db_query(sql, hm, klass, initializeLazyObjects);
    }

    public DBEntity refresh(DBEntity dbe) throws DBException { return conn.refresh(dbe); }
    public DBEntity insert(DBEntity dbe) throws DBException { return conn.insert(dbe, this); }
    public DBEntity update(DBEntity dbe) throws DBException { return conn.update(dbe, this); }
    public DBEntity delete(DBEntity dbe) throws DBException { return conn.delete(dbe, this); }

    public List<DBEntity> search(DBEntity search) { return search(search, true, null); }
    public List<DBEntity> search(DBEntity search, boolean uselike, String orderby) {
        // tablename
        String entityname = search.getClass().getSimpleName();
        String tablename = search.getTableName();
        log.debug("entityname: " + entityname);
        log.debug("tablename: " + tablename);

        // Columns
        HashMap<String,Object> hashMap = new HashMap<>();
        List<String> clauses = new ArrayList<>();
        _getClausesAndValues(search, uselike, hashMap, clauses);

        String hql = "SELECT * FROM " + tablename;
        if(clauses.size()>0) {
            hql += " WHERE ";
            for(int i=0 ; i<clauses.size() ; i++) {
                hql += clauses.get(i);
                if(i < (clauses.size()-1))
                    hql += " AND ";
            }
        }

        if(orderby!=null && orderby.length()>0)
            hql += " ORDER BY " + orderby;

        return this.db_query(hql, hashMap, search.getClass(),true);
    }

    public boolean exists(DBEntity search) throws DBException {
        List<DBEntity> res = this.search(search,false,null);
        return res.size()>0;
    }

    public DBELog log(String ip, String note, String note2) {
        Date date = new Date(new java.util.Date().getTime());
        Time hour = new Time(new java.util.Date().getTime());
        //System.out.println("Date: " + date);
        //System.out.println("Time: " + hour);

        // 1. Search today's entry
        DBELog search = new DBELog();
        search.setIp(ip);
        search.setData(date);
        List<DBEntity> res = this.search(search, false, null);

        DBELog ret = new DBELog();
        ret.setIp(ip);
        ret.setData(date);
        ret.setOra(hour);
        if(res.size()==0) {
            DBELog search2 = new DBELog();
            search2.setIp(ip);
            List<DBEntity> res2 = this.search(search2, false, "url desc");
            if(res2.size()>0) {
                ret.setUrl(((DBELog)res2.get(0)).getUrl());
            }
            ret.setCount(1);
            if(note!=null && note.length()>0) ret.setNote(note);
            if(note2!=null && note2.length()>0) ret.setNote2(hour+"-"+note2);
            try {
                ret = (DBELog) this.insert(ret);
            } catch (DBException e) {
                e.printStackTrace();
                ret = null;
            }
        } else {
            DBELog oldEntry = (DBELog) res.get(0);
            ret.setCount(oldEntry.getCount() + 1);
            ret.setNote(oldEntry.getNote());
            if(note2!=null && note2.length()>0)
                ret.setNote2(oldEntry.getNote2()+"\n"+hour+"-"+note2);
            try {
                ret = (DBELog) this.update(ret);
            } catch (DBException e) {
                e.printStackTrace();
                ret = null;
            }
        }
        return ret;
    }

    protected void _getClausesAndValues(DBEntity search, boolean uselike, HashMap<String, Object> hashMap, List<String> clauses) {
        HashMap<String, Object> hmValues = search.getValues(false);
        hmValues.forEach((k,v) -> {
            if(uselike && v instanceof String) {
                clauses.add(k + " LIKE :" + k);
                v = "%" + v + "%";
            } else
                clauses.add(k + " = :" + k);
            hashMap.put(k,v);
        });
    }
}
