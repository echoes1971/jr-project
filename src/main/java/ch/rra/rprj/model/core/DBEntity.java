package ch.rra.rprj.model.core;

import ch.rra.rprj.model.DBException;
import ch.rra.rprj.model.DBMgr;

import javax.persistence.JoinTable;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class DBEntity {

    public String getNextUuid() {
        UUID uuid = UUID.randomUUID();
        String ret = uuid.toString().replaceAll("-","");
        ret = ret.substring(ret.length()-16);
        //System.out.println("ret = " + ret + " (" + ret.length() + ")");
        return ret;
    }

    public String getTableName() {
        String ret = "";
        Annotation[] annotations = this.getClass().getAnnotations();
        for (Annotation an : annotations) {
            //System.out.println("" + an.toString());
            if(an instanceof Table) {
                ret = ((Table) an).name();
                break;
            }
        }
        return ret;
    }

    public HashMap<String, Object> getValues() {
        return getValues(new HashMap<String,Object>());
    }
    public HashMap<String, Object> getValues(HashMap<String, Object> hashMap) {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            String field_name = field.getName();
            String method_name = "get"
                    + field_name.substring(0,1).toUpperCase()
                    + field_name.substring(1).toLowerCase();
            Method method = null;
            Object value = null;
            try {
                method = getClass().getMethod(method_name);
                value = method.invoke(this);
            } catch (NoSuchMethodException e) {
                // System.out.println("ERROR: field_name.method_name NOT FOUND!");
                continue;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            String column_name = field.getName();
            Annotation[] field_annotations = field.getAnnotations();
            for(Annotation an : field_annotations) {
                //// System.out.println("  " +an.toString());
                if(an instanceof javax.persistence.Column) {
                    column_name = ((javax.persistence.Column)an).name();
                    break;
                } else if(an instanceof JoinTable) {
                    column_name = "";
                    break;
                }
            }
            if(column_name.equals(""))
                continue;
            // System.out.println("" + field.toString());
            // System.out.println(" name:\t" + field_name);
            //// System.out.println(" method name:\t" + method_name);
            // System.out.println(" get:\t" + method);
            //// System.out.println(" " + field);
            // System.out.println(" column:" + column_name);
            // System.out.println(" value:\t" + value + (value!=null ? " ("+value.getClass()+")" : ""));
            if(value==null || value instanceof HashSet)
                continue;
            hashMap.put(field_name, value);
        }
        return hashMap;
    }

    public void beforeInsert(DBMgr dbMgr) throws DBException {}
    public void afterInsert(DBMgr dbMgr) throws DBException {}

    public void beforeUpdate(DBMgr dbMgr) throws DBException {}
    public void afterUpdate(DBMgr dbMgr) throws DBException {}

    public void beforeDelete(DBMgr dbMgr) throws DBException {}
    public void afterDelete(DBMgr dbMgr) throws DBException {}
}
