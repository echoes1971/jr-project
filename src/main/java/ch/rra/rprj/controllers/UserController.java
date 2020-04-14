package ch.rra.rprj.controllers;

import ch.rra.rprj.model.ObjectMgr;
import ch.rra.rprj.model.core.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

@RestController
public class UserController {

    UserController() {

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

    private User getCurrentUser(HttpSession httpSession) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        return objMgr.getDbeUser();
    }

    // Aggregate Root

    @GetMapping("/ping")
    String ping(HttpSession httpSession) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        Integer counter = httpSession.getAttribute("counter") == null ? 0 : (Integer) httpSession.getAttribute("counter");
        counter++;
        httpSession.setAttribute("counter", counter);
        return "pong: " + counter;
    }

//    @PostMapping(
//            value = "/createPerson", consumes = "application/json", produces = "application/json")

//    @GetMapping("/login")
//    String login_get(HttpSession httpSession, @RequestParam String login, @RequestParam String pwd) {
//        ObjectMgr objMgr = getObjectMgr(httpSession);
//        User user = objMgr.login(login,pwd);
//
//        return user!=null ? user.toString() : "";
//    }

    // curl -d '{"login":"adm","pwd":"adm"}' -H "Content-Type: application/json" http://localhost:8080/login
    @PostMapping("/login")
    User login(HttpSession httpSession, @RequestBody HashMap<String,String> hm, HttpServletRequest httpServletRequest) {
        ObjectMgr objMgr = getObjectMgr(httpSession);
        User user = objMgr.login(hm.get("login"),hm.get("pwd"));

        if(user!=null) {
            user.setPwd(null);
            user.setGroups(null);
        }

        return user;
    }

    @GetMapping("/currentUser")
    String currentUser(HttpSession httpSession) {
        User user = getCurrentUser(httpSession);

        return user!=null ? user.toString() : "";
    }

//    @GetMapping("/users")
//    List<User> all(HttpSession httpSession) {
//        ObjectMgr objMgr = (ObjectMgr) httpSession.getAttribute("objMgr");
//        return objMgr.db_version();
//    }
//
//    @PostMapping("/users")
//    User newUser(@RequestBody User newUser) {
//        return repository.save(newUser);
//    }
//
//    @GetMapping("/users/{id}")
//    User one(@PathVariable String id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new UserNotFoundException(id));
//    }
//
//    @PutMapping("/users/{id}")
//    User replaceUser(@RequestBody User newUser, @PathVariable String id) {
//        return repository.findById(id)
//                .map(user -> {
//                    user.setFullname(newUser.getFullname());
//                    return repository.save(user);
//                })
//                .orElseGet(() -> {
//                    return repository.save(newUser);
//                });
//    }
//
//    @DeleteMapping("/users/{id}")
//    void deleteUser(@PathVariable String id) {
//        repository.deleteById(id);
//    }
}
