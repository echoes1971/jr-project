package ch.rra.rprj.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    UserController() {

    }

    // Aggregate Root

//    @GetMapping("/users")
//    List<User> all() {
//        return repository.findAll();
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
