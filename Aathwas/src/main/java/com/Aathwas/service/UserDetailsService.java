package com.Aathwas.service;

import com.Aathwas.repository.UserRepository;
import com.Aathwas.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        User user = repository.findByPhoneNumber(phoneNo); // Corrected method name to findByPhoneNumber
        if (user == null) {
            // If the user is not found, you should throw UsernameNotFoundException
            throw new UsernameNotFoundException("User not found with phone number: " + phoneNo);
        }
        // Return the UserDetails with the appropriate authorities.
         return new org.springframework.security.core.userdetails.User(user.getPhoneNumber(), "", new ArrayList<>());
    }
}