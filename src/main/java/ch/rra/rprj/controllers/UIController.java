package ch.rra.rprj.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Vector;

@Controller
public class UIController {

    @GetMapping("/")
    public String app() { return "app.html"; }

    // ['-13','Downloads'], ['-14','About us']

    @GetMapping("/ui/rootobj")
    @ResponseBody
    public HashMap<String,String> ui_rootobj() {
        return new HashMap<String, String>() {{
                put("id", "-10");   put("name", "Home");
            }};
    }

    @GetMapping("/ui/topmenu")
    @ResponseBody
    public Vector<HashMap> ui_topmenu() {
        return new Vector<HashMap>(){{
            add(new HashMap<String, String>() {{
                put("id", "-13");   put("name", "Downloads");
            }});
            add(new HashMap<String, String>() {{
                put("id", "-14");   put("name", "About us");
            }});
        }};
    }

    @GetMapping("/ui/parentlist")
    @ResponseBody
    public Vector<HashMap> ui_parentlist() {
        // [{'id':'aaa','name':'Parent 1'},{'id':'aab','name':'Parent 2'},{'id':'aac','name':'Parent 3'}]
        return new Vector<HashMap>(){{
            add(new HashMap<String, String>() {{
                put("id", "aaa");   put("name", "Parent 1");
            }});
            add(new HashMap<String, String>() {{
                put("id", "aab");   put("name", "Parent 2");
            }});
            add(new HashMap<String, String>() {{
                put("id", "aac");   put("name", "Parent 3");
            }});
        }};
    }
}
