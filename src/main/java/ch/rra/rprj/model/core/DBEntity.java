package ch.rra.rprj.model.core;

import ch.rra.rprj.model.DBException;
import ch.rra.rprj.model.DBMgr;

import javax.persistence.JoinTable;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.*;

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

    public String getIcon() { return "glyphicon-cog"; }

    public HashMap<String, Object> getValues() {
        return getValues(false);
    }
    public HashMap<String, Object> getValues(boolean with_metadata) {
        return getValues(new HashMap<>(),with_metadata);
    }
    public HashMap<String, Object> getValues(HashMap<String, Object> hashMap, boolean with_metadata) {

        // TODO skip fields ManyToMany like DBEGroup.users, IF data is a proxy (?)

        List<Field> fields = new ArrayList<>();
        Field[] this_fields = getClass().getDeclaredFields();
        fields.addAll(Arrays.asList(this_fields));
        if(getClass().getSuperclass().getSimpleName().equals("DBEObject")) {
            Field[] super_fields = getClass().getSuperclass().getDeclaredFields();
            fields.addAll(Arrays.asList(super_fields));
        }
        for (Field field : fields) {
            String field_name = field.getName();
            String method_name = "get"
                + (field_name.substring(0,1).toUpperCase().equals(field_name.substring(0,1)) ?
                    field_name :
                    field_name.substring(0,1).toUpperCase() + field_name.substring(1).toLowerCase());

            // Value
            Method method;
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
            if(value==null || value instanceof HashSet)
                continue;
            // Column Name
            String column_name = getColumnName(field);
            if(column_name.equals(""))
                continue;
            hashMap.put(field_name, value);
        }
        if(with_metadata) {
            hashMap.put("_class", getClass().getSimpleName());
            hashMap.put("_icon", getIcon());
        }
        return hashMap;
    }

    public String toTypeScript() {
        String prefix = "";//"Base";
        String class_name = getClass().getSimpleName().equals("DBEObjectReal") ? "DBEObject" : getClass().getSimpleName();
        String super_class = !getClass().getSimpleName().equals("DBEObjectReal") && getClass().getSuperclass().getSimpleName().equals("DBEObject") ?
                                " extends DBEObject" : " extends DBEntity";
        Vector<String> superConstructor = new Vector<>();
        Vector<String> sbConstructor1 = new Vector<>();
        Vector<String> sbConstructor2 = new Vector<>();
        String ret = "";
        ret += "export class "+prefix+class_name+super_class+" {\n";
        ret += "  _class: string = '" + class_name + "';\n";
        ret += "  _icon: string = '" + getIcon() + "';\n";
        ret += "\n";

        List<Field> fields = new ArrayList<>();
        List<Field> fieldsSuperclass = new ArrayList<>();

        if(getClass().getSuperclass().getSimpleName().equals("DBEObject")) {
            Field[] super_fields = getClass().getSuperclass().getDeclaredFields();
            if(getClass().getSimpleName().equals("DBEObjectReal"))
                fields.addAll(Arrays.asList(super_fields));
            else
                fieldsSuperclass.addAll(Arrays.asList(super_fields));
        }

        // if(fieldsSuperclass.size()>0) {
            //sbConstructor2.add("// DBEObject");
        // }
        for (Field field : fieldsSuperclass) {
            String field_name = field.getName();
            String field_type = _getTSType(field);
            superConstructor.add(field_name);
            sbConstructor1.add(field_name+"?: "+field_type);
            //sbConstructor2.add("this."+field_name+" = "+field_name+";");
        }
        if(fieldsSuperclass.size()>0) {
            sbConstructor2.add("// "+class_name);
        }


        Field[] this_fields = getClass().getDeclaredFields();
        fields.addAll(Arrays.asList(this_fields));
        for (Field field : fields) {
            String field_name = field.getName();
            String field_type = _getTSType(field);
            ret += "  "+field_name+"?: "+field_type+" = null;\n";
            sbConstructor1.add(field_name+"?: "+field_type);
            sbConstructor2.add("this."+field_name+" = "+field_name+";");
        }
        ret += "\n";
        ret += "  constructor("+String.join(", ",sbConstructor1)+") {\n";
        ret += "    super("+String.join(", ",superConstructor)+");\n";
        ret += "    " + String.join("\n    ", sbConstructor2) +"\n";
        ret += "  }\n";
        ret += "}\n";
        return ret;
    }

    private String _getTSType(Field field) {
        String field_type = "any";
        switch (field.getType().getSimpleName()) {
            case "Set":
                field_type = "any[]";
                break;
            case "Integer":
                field_type = "number";
                break;
            case "String":
            case "Date":
            case "Time":
            case "Timestamp":
                field_type = "string";
                break;
            default:
                System.out.println("// " + field.getName() + "->" + field.getType().getSimpleName());
                break;
        }
        return field_type;
    }

    public String getColumnName(String field_name) {
        try {
            return getColumnName(getClass().getField(field_name));
        } catch (NoSuchFieldException e) {
            return field_name;
        }
    }
    public String getColumnName(Field field) {
        String column_name = field.getName();
        Annotation[] field_annotations = field.getAnnotations();
        for(Annotation an : field_annotations) {
            // System.out.println("  " +an.toString());
            if(an instanceof javax.persistence.Column) {
                column_name = ((javax.persistence.Column)an).name();
                break;
            } else if(an instanceof JoinTable) {
                column_name = "";
                break;
            }
        }
        return column_name;
    }

    public void beforeInsert(DBMgr dbMgr) throws DBException {}
    public void afterInsert(DBMgr dbMgr) throws DBException {}

    public void beforeUpdate(DBMgr dbMgr) throws DBException {}
    public void afterUpdate(DBMgr dbMgr) throws DBException {}

    public void beforeDelete(DBMgr dbMgr) throws DBException {}
    public void afterDelete(DBMgr dbMgr) throws DBException {}
}
