package com.assignment.todo;

import javax.servlet.http.HttpSession;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoRepository todoRepo; // gets the todoRepo from the TodoRepository class to use the methods to
                                           // connect to db
    private final UserRepo userRepo; // to get specific user results from db

    public TodoController(TodoRepository todoRepo, UserRepo userRepo) {
        this.todoRepo = todoRepo;
        this.userRepo = userRepo;
    }

    // uses HTTP session to allo user to login
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpSession session, @RequestBody User user) {
        try {
            User authenticatedUser = userRepo.findByUsernameAndPassword(user.getUsername(), user.getPassword());
            if (authenticatedUser == null || !authenticatedUser.getUsername().equals(user.getUsername())
                    || !authenticatedUser.getPassword().equals(user.getPassword())) {
                throw new Exception("Could not validate user"); // for the case if user not found or username/ password do not
                                                                // match, this throws execption
            }
            session.setAttribute("user", authenticatedUser); // set the user attribute of the session object 
                                                             // authenticatedUser object which happens with only correct username/pass
            return ResponseEntity.ok(authenticatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // as soon as the user logs in, the todos saved in the database for the user
    // need to be displayed using this method
    @GetMapping
    public Page<TodoValue> getTodos(HttpSession session,@RequestParam(value = "page", defaultValue = "1") int page, // to allow for pagination of results
            @RequestParam(value = "size", defaultValue = "5") int size) {
        User user = (User) session.getAttribute("user"); // gets the attribute 'user' from the session object
        PageRequest x = PageRequest.of(page, size, Sort.by("id").descending());
        Page<TodoValue> items = todoRepo.findByUserId(user.getId(), x); // gets just specific page todo items for
                                                                              // the user based on the request from
                                                                              // frontend
        Page<TodoValue> allItems = todoRepo.findByUserId(user.getId(), PageRequest.of(0, // retrieve all todo items of
                                                                                        // the user by creating new Page
                                                                                        // request from 0
                (int)items.getTotalElements(), Sort.by("id").descending()));
        return allItems;
    }

    // after retrieiving the todo items for the user, the user can now create a new
    // todo item
    @PutMapping
    public TodoValue add(HttpSession session, @RequestBody TodoValue todo) throws Exception {
        try {
            User user = (User) session.getAttribute("user"); // the function validates the user session and then creates
                                                             // a new TodoItem object and sets the user field to the
                                                             // logged-in user.
            if (user == null) {
                throw new Exception("Invalid session");
            }
            todo.setUser(user); // set the user for the added todo value
            todo.setUserId(user.getId()); // set the userId for the added todo value
            todo.setCompleted(false); // set the completed value to false for the added todo value
            TodoValue addedItem = todoRepo.save(todo);
            return addedItem;
        } catch (Exception e) { // if the user cant be validated properly then throw the exception
            throw e;
        }
    }

    // after creating a new todo item, the user can also update the todo item
    @PutMapping("/{id}") // the frontend needs to send the id of the todo item to be updated
    public TodoValue update(@PathVariable Long id, @RequestBody TodoValue todo, HttpSession session)
            throws Exception {
        if (session.getAttribute("user") == null) {
            throw new Exception();      //session validation
        }
        //first find the item from the database and throw exception if not found
        TodoValue findItem = todoRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("TodoItem", "id", id));
        findItem.setContent(todo.getContent()); // update the content of the todo item
        return todoRepo.save(findItem);     //save the updated todo item
    }

    // after updating the todo item, the items can also be deleted
    @DeleteMapping("/{id}") // the frontend needs to send the id of the todo item to be deleted
    public void delete(@PathVariable Long id, HttpSession session) throws Exception {
        if (session.getAttribute("user") == null) {
            throw new Exception(); //session validation
        }
        //first find the item from the database and throw exception if not found
        TodoValue todo = todoRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("TodoItem", "id", id));
        todoRepo.delete(todo); //delete the todo item
    }

    // helps during getting the user from the session on the frontend
    @GetMapping("/user")
    public User geUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user;
    }

    //function to toggle the completed status of the todo item
    @PatchMapping("/{id}")
    public TodoValue toggle(@PathVariable Long id, HttpSession session) throws Exception {
        if (session.getAttribute("user") == null) {
            throw new Exception(); //session validation
        }
        //first find the item from the database and throw exception if not found
        TodoValue todo = todoRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("TodoItem", "id", id));
        todo.setCompleted(!todo.isCompleted()); //toggle the completed status of the todo item
        return todoRepo.save(todo); //save the updated state
    }
}