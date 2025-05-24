package com.pharmacy.security.service;

import com.pharmacy.security.entity.User;
import com.pharmacy.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomUserDetailService implements UserDetailsService
{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findByUserName(username);
        if(Objects.isNull(user))
        {
            System.out.println("User not available");
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(user);
    }
}