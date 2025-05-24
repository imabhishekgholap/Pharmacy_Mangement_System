package com.pharmacy.security.jwtCreation;

import com.mongodb.DuplicateKeyException;
import com.pharmacy.security.entity.User;
import com.pharmacy.security.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public User updateUser(String userName, User updatedUser) {
    	User existingUser = userRepository.findByUserName(userName);
    	if (existingUser == null) {
    	    throw new RuntimeException("User not found with userName: " + userName);
    	}

        // Optional: Check if new username is already taken by another user
        if (!existingUser.getUserName().equals(updatedUser.getUserName())
                && userRepository.existsByUserName(updatedUser.getUserName())) {
            throw new RuntimeException("Username already taken");
        }

        existingUser.setUserName(updatedUser.getUserName());
        existingUser.setContactNumber(updatedUser.getContactNumber());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());

        // Only update password if it’s changed
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    public User register(User user) {
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("Username already taken");
        }

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Username already exists");
        }
    }

    
    
    public String authenticateAndGetToken(User user) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));

        if (authenticate.isAuthenticated()) {
            // Get full user with role
            User dbUser = userRepository.findByUserName(user.getUserName());

            if (dbUser == null) {
                throw new RuntimeException("User not found");
            }
            return jwtService.generateToken(dbUser);
           
        }
        return "failure";
    }
}