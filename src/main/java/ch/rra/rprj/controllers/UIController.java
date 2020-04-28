package ch.rra.rprj.controllers;

import ch.rra.rprj.model.ObjectMgr;
import ch.rra.rprj.model.cms.DBEFolder;
import ch.rra.rprj.model.cms.DBEPage;
import ch.rra.rprj.model.core.DBEObject;
import ch.rra.rprj.model.core.DBEObjectReal;
import ch.rra.rprj.model.core.DBEntity;
import ch.rra.rprj.model.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Controller
public class UIController {
    private Logger logger;

    public UIController() { this.logger = LoggerFactory.getLogger(getClass()); }

    @Value("${rprj.rootobj.id}")
    private String rootObjId;

    private String currentObjId;

    private List<DBEObject> fetchChildren(ObjectMgr objMgr, DBEObject father, boolean withoutIndexPage) {
        List<DBEObject> res = father.fetchChildren(objMgr);
        if(withoutIndexPage) res = res.stream().filter(x -> !x.getName().equals("index")).collect(Collectors.toList());
        return res;
    }

    private ObjectMgr getObjectMgr(HttpSession httpSession) {
        ObjectMgr ret = (ObjectMgr) httpSession.getAttribute("objMgr");
        if (ret==null) {
            ret = new ObjectMgr();
            try {
                ret.setUp();
                httpSession.setAttribute("objMgr", ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //System.out.println(httpSession.getId() + ":" + ret);
        return ret;
    }

    private DBEFolder getRootObject(HttpSession httpSession) {
        DBEFolder ret = (DBEFolder) httpSession.getAttribute("rootObj");
        if(ret==null) {
            ObjectMgr objMgr = getObjectMgr(httpSession);
            ret = (DBEFolder) objMgr.fullObjectById(rootObjId);
            httpSession.setAttribute("rootObj", ret);
        }
        return ret;
    }

    private DBEObject getCurrentObject(String objId, ObjectMgr objMgr) {
        DBEObject ret = objMgr.fullObjectById(objId);
        if(ret instanceof DBEFolder) {
            DBEPage search = new DBEPage();
            search.setFather_id(objId);
            search.setName("index");
            List<DBEntity> res = objMgr.search(search);
            if(res.size()==1) {
                ret = (DBEObject) res.get(0);
            }
        }
        return ret;
    }

    private List<DBEObject> getTopMenu(HttpSession httpSession) {
        List<DBEObject> ret = (List<DBEObject>) httpSession.getAttribute("topMenu");
        if(ret==null) {
            ObjectMgr objMgr = getObjectMgr(httpSession);
            DBEFolder rootObj = getRootObject(httpSession);
            ret = fetchChildren(objMgr, rootObj, true);
            httpSession.setAttribute("topMenu", ret);
        }
        return ret;
    }

    private Vector<DBEObject> getParentsList(String objId, ObjectMgr objMgr) {
        Vector<DBEObject> ret = new Vector<>();
        DBEObject myCurrentObject = objMgr.fullObjectById(objId);
        while(myCurrentObject!=null && !myCurrentObject.getId().equals(rootObjId)) {
            if(!myCurrentObject.getName().equals("index")) ret.add(0, myCurrentObject);
            myCurrentObject = objMgr.fullObjectById(myCurrentObject.getFather_id());
        }
        if(myCurrentObject!=null) ret.add(0, myCurrentObject);
        logger.info("ret:" + ret.size());
        ret.forEach(o -> {
            logger.info(" " + o.getId() + "\t" + o.getFather_id() + "\t" + o.getName());
        });
        return ret;
    }

    private User getCurrentUser(HttpSession httpSession) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        return objMgr.getDbeUser();
    }

//    @GetMapping("/aaa")
//    public String app() { return "/cippa.jsp"; }

    @GetMapping("/ui/rootobj")
    @ResponseBody
    public HashMap<String,Object> ui_rootobj(HttpSession httpSession) {
        return getRootObject(httpSession).getValues(true);
    }

/*
    @GetMapping("/ui/obj/")
    @ResponseBody
    public HashMap<String,Object> ui_currentobj_empty(HttpSession httpSession) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        currentObjId = rootObjId;
        DBEObject ret = getCurrentObject(currentObjId, objMgr);
        return ret.getValues();
    }
*/
    @GetMapping("/ui/obj/{objId}")
    @ResponseBody
    public HashMap<String,Object> ui_currentobj(@PathVariable String objId, HttpSession httpSession) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        String _currentObjId = objId==null || objId.equals("null") ? rootObjId : objId;
        DBEObject ret = getCurrentObject(_currentObjId, objMgr);
        return ret==null ? new HashMap<>() : ret.getValues(true);
    }

    @GetMapping("/ui/topmenu")
    @ResponseBody
    public Vector<HashMap<String, Object>> ui_topmenu(HttpSession httpSession) {
        List<DBEObject> res = getTopMenu(httpSession);
        return (Vector<HashMap<String, Object>>)
                res.stream().map(x -> x.getValues(true)).collect(Collectors.toCollection((Supplier<Vector>) Vector::new));
    }

    @GetMapping("/ui/parentlist/{objId}")
    @ResponseBody
    public List<HashMap<String, Object>> ui_parentlist(@PathVariable String objId, HttpSession httpSession) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        return getParentsList(objId, objMgr).stream().map(x -> x.getValues(true)).collect(Collectors.toList());
    }

    @GetMapping("/ui/menutree/{objId}")
    @ResponseBody
    public HashMap<String,List<HashMap<String, Object>>> ui_menuTree(@PathVariable String objId, HttpSession httpSession) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        HashMap<String,List<HashMap<String, Object>>> ret = new HashMap<>();
        getParentsList(objId,objMgr).forEach(o -> {
            logger.info("Fetching children for: " + o.getId());
            ret.put(o.getId(), fetchChildren(objMgr,o,true).stream().map(x -> x.getValues(true)).collect(Collectors.toList()));
        });
        return ret;
    }
}
