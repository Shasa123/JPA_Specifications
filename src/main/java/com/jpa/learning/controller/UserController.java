package com.jpa.learning.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpa.learning.entity.FilterCriteria;
import com.jpa.learning.entity.Person;
import com.jpa.learning.entity.User;
import com.jpa.learning.service.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/users")
    public List<User> findAll(){
        try {
        List<FilterCriteria> filters = new ArrayList<>();
        filters.add(new FilterCriteria("datevalue","NOT_BETWEEN","date","2003-04-02 21:02:44,2003-06-04 21:02:44"));
 //       filters.add(new FilterCriteria("intvalue","BETWEEN","integer","230,670"));
//        filters.add(new FilterCriteria("intvalue","EQUALS","integer","230"));
//        filters.add(new FilterCriteria("name","STARTS_WITH","string","J"));
//        filters.add(new FilterCriteria("datevalue","BEFORE","date","2003-05-03 21:02:44"));
//        filters.add(new FilterCriteria("datevalue","EQUALS_TO","date","2003-05-03 21:02:44"));
//        Subquery<User> subquery = userService.createQuery(User.class, filters,"entityId");
//        userService.getParentQuery(Person.class,filters ,"entityId");
        userService.getParentQuery2(filters ,"entityId");
//        userService.createQuery(User.class, filters,"entityId");
        }
        catch (Exception e) {
           System.out.println(e.getCause());
           e.printStackTrace();
        }
        return userService.findAll();
    }
    
    @PostMapping("/users")
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }
    
    public static void main(String[] args) throws ParseException {
        
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2003-05-03 21:02:44");
        System.out.println(date.toString());
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2003-05-03 21:02:44"));
    }
}
