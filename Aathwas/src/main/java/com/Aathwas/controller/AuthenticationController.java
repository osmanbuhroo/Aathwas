package com.Aathwas.controller;

import com.Aathwas.model.AuthRequest;
import com.Aathwas.repository.UserRepository;
import com.Aathwas.service.OtpService;
import com.Aathwas.service.UserDetailsService;
import com.Aathwas.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "api/client/auth/")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtTokenUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private   UserRepository userRepository;

    @RequestMapping({ "hello" })
    public String firstPage() {
        return "Hello World";
    }

    @RequestMapping(value = "requestOtp/{phoneNo}", method = RequestMethod.GET)
    public Map<String, Object> getOtp(@PathVariable String phoneNo) {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            // Check if the user's phone number is present in the database

            if (userRepository.existsByPhoneNumber(phoneNo)) {
                // Generate OTP for login
                String otp = otpService.generateOtp(phoneNo);
                returnMap.put("otp", otp);
                returnMap.put("status", "success");
                returnMap.put("message", "Otp sent successfully");
            } else {
                returnMap.put("status", "failed");
                returnMap.put("message", "User not registered. Please go to registration page.");
            }
        } catch (Exception e) {
            returnMap.put("status", "failed");
            returnMap.put("message", e.getMessage());
        }

        return returnMap;
    }

    @RequestMapping(value = "verifyOtp/",method = RequestMethod.POST)
    public Map<String,Object> verifyOtp(@RequestBody AuthRequest authenticationRequest){
        Map<String,Object> returnMap=new HashMap<>();
        try{
            //verify otp
            if(authenticationRequest.getOtp().equals(otpService.getCacheOtp(authenticationRequest.getPhoneNo()))){
                String jwtToken = createAuthenticationToken(authenticationRequest);
                returnMap.put("status","success");
                returnMap.put("message","Otp verified successfully");
                returnMap.put("jwt",jwtToken);
                otpService.clearOtp(authenticationRequest.getPhoneNo());

            }else{
                returnMap.put("status","success");
                returnMap.put("message","Otp is either expired or incorrect");
            }

        } catch (Exception e){
            returnMap.put("status","failed");
            returnMap.put("message",e.getMessage());
        }

        return returnMap;
    }

    //create auth token
    public String createAuthenticationToken(AuthRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getPhoneNo(), "")
            );
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getPhoneNo());
        return jwtTokenUtil.generateToken(userDetails);
    }
}