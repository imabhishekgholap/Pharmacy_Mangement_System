package com.pharmacy.security.controller;

import com.pharmacy.security.entity.User;

import com.pharmacy.security.jwtCreation.UserService;
import com.pharmacy.security.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class UserController
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/register") //+ @CrossOrigin(origins = "http://localhost:5173")
    public User register(@Valid @RequestBody User user){
        return userService.register(user);
    }

    @GetMapping("/users")
    public List<User> userList(){
        return userRepository.findAll();
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){
        return userService.authenticateAndGetToken(user);
   }
    
    @PutMapping("/users/{userName}")
    public User updateUser(@PathVariable String userName, @Valid @RequestBody User user) {
        return userService.updateUser(userName, user);
    }
    
    @GetMapping("/users/getUser/{userName}")
    public User getuser(@PathVariable String userName) {
    	return userRepository.findByUserName(userName);
    }
    
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    

}