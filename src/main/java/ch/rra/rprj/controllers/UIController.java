package ch.rra.rprj.controllers;

import ch.rra.rprj.model.ObjectMgr;
import ch.rra.rprj.model.cms.DBEFolder;
import ch.rra.rprj.model.core.DBEntity;
import ch.rra.rprj.model.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    private List<DBEntity> fetchChildren(HttpSession httpSession, DBEFolder father) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        DBEFolder search = new DBEFolder();
        search.setFather_id(father.getId());
        List<DBEntity> res = objMgr.search(search,false,"",true);
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

    private List<DBEntity> getTopMenu(HttpSession httpSession) {
        List<DBEntity> ret = (List<DBEntity>) httpSession.getAttribute("topMenu");
        if(ret==null) {
            DBEFolder rootObj = getRootObject(httpSession);
            ret = fetchChildren(httpSession, rootObj);
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

    @GetMapping("/ui/topmenu")
    @ResponseBody
    public Vector<HashMap<String, Object>> ui_topmenu(HttpSession httpSession) {
        List<DBEntity> res = getTopMenu(httpSession);
        return (Vector<HashMap<String, Object>>) res.stream().map(DBEntity::getValues).collect(Collectors.toCollection((Supplier<Vector>) Vector::new));
    }

    @GetMapping("/ui/parentlist")
    @ResponseBody
    public Vector<HashMap<String, String>> ui_parentlist() {
        // [{'id':'aaa','name':'Parent 1'},{'id':'aab','name':'Parent 2'},{'id':'aac','name':'Parent 3'}]
        return new Vector<>() {{
            add(new HashMap<>() {{
                put("id", "aaa");
                put("name", "Parent 1");
                put("icon", "glyphicon-folder-close");
            }});
            add(new HashMap<>() {{
                put("id", "aab");
                put("name", "Parent 2");
                put("icon", "glyphicon-folder-close");
            }});
            add(new HashMap<>() {{
                put("id", "aac");
                put("name", "Parent 3");
                put("icon", "glyphicon-folder-close");
            }});
        }};
    }
}
