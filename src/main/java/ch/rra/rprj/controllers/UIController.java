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

    private List<DBEntity> fetchChildren(HttpSession httpSession, DBEFolder father, boolean withoutIndexPage) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        //DBEFolder search = new DBEFolder();
        DBEObject search = new DBEObjectReal();
        search.setFather_id(father.getId());
        List<DBEntity> res = objMgr.search(search,false,"",true);
        if(withoutIndexPage) res = res.stream().filter(x -> !((DBEObject)x).getName().equals("index")).collect(Collectors.toList());
        return father.sortChildren(res);
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

    private DBEObject getCurrentObject(String objId, HttpSession httpSession) {
        DBEObject ret; // = (DBEFolder) httpSession.getAttribute("currentObj");
        //if(ret==null) {
            ObjectMgr objMgr = getObjectMgr(httpSession);
            ret = objMgr.fullObjectById(objId);
            //logger.info(ret + "");
            if(ret instanceof DBEFolder) {
                DBEPage search = new DBEPage();
                search.setFather_id(objId);
                search.setName("index");
                List<DBEntity> res = objMgr.search(search);
                if(res.size()==1) {
                    ret = (DBEObject) res.get(0);
                    //logger.info("=> " + ret.toString());
                }
            }

            //httpSession.setAttribute("currentObj", ret);
        //}
        return ret;
    }

    private List<DBEntity> getTopMenu(HttpSession httpSession) {
        List<DBEntity> ret = (List<DBEntity>) httpSession.getAttribute("topMenu");
        if(ret==null) {
            DBEFolder rootObj = getRootObject(httpSession);
            ret = fetchChildren(httpSession, rootObj, true);
            httpSession.setAttribute("topMenu", ret);
        }
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
        DBEFolder ret = getRootObject(httpSession);
        return ret.getValues();
    }

    @GetMapping("/ui/obj/")
    @ResponseBody
    public HashMap<String,Object> ui_currentobj_empty(HttpSession httpSession) {
        currentObjId = rootObjId;
        //System.out.println("UIController.ui_currentobj: currentObjId="+currentObjId);
        DBEObject ret = getCurrentObject(currentObjId, httpSession);
        return ret.getValues();
    }

    @GetMapping("/ui/obj/{objId}")
    @ResponseBody
    public HashMap<String,Object> ui_currentobj(@PathVariable String objId, HttpSession httpSession) {
        //currentObjId = objId!=null && objId.length()>0 ? objId : rootObjId;
        String _currentObjId = objId==null || objId.equals("null") ? rootObjId : objId;
        //System.out.println("UIController.ui_currentobj: objId="+objId);
        //System.out.println("UIController.ui_currentobj: rootObjId="+rootObjId);
        //System.out.println("UIController.ui_currentobj: _currentObjId="+_currentObjId);
        DBEObject ret = getCurrentObject(_currentObjId, httpSession);
        return ret==null ? new HashMap<>() : ret.getValues();
    }

    @GetMapping("/ui/topmenu")
    @ResponseBody
    public Vector<HashMap<String, Object>> ui_topmenu(HttpSession httpSession) {
        List<DBEntity> res = getTopMenu(httpSession);
        return (Vector<HashMap<String, Object>>)
                res.stream().map(DBEntity::getValues).collect(Collectors.toCollection((Supplier<Vector>) Vector::new));
    }

    @GetMapping("/ui/parentlist/{objId}")
    @ResponseBody
    public Vector<HashMap<String, Object>> ui_parentlist(@PathVariable String objId, HttpSession httpSession) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        Vector<HashMap<String,Object>> ret = new Vector<>();
        DBEObject myCurrentObject = objMgr.fullObjectById(objId);
        while(myCurrentObject!=null && !myCurrentObject.getId().equals(rootObjId)) {
            if(!myCurrentObject.getName().equals("index")) ret.add(0, myCurrentObject.getValues());
            myCurrentObject = objMgr.fullObjectById(myCurrentObject.getFather_id());
        }
        if(myCurrentObject!=null) ret.add(0, myCurrentObject.getValues());
        return ret;
    }
}
