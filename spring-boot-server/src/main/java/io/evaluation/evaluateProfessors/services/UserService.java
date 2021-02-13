package io.evaluation.evaluateProfessors.services;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.evaluation.evaluateProfessors.domain.ERole;
import io.evaluation.evaluateProfessors.domain.Role;
import io.evaluation.evaluateProfessors.domain.User;
import io.evaluation.evaluateProfessors.dto.UserRegistrationDto;
import io.evaluation.evaluateProfessors.exceptions.UsernameAlreadyExistsException;
import io.evaluation.evaluateProfessors.exceptions.RoleException;

import io.evaluation.evaluateProfessors.repositories.UserRepository;
import io.jsonwebtoken.lang.Collections;
import io.evaluation.evaluateProfessors.repositories.RoleRepository;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;



    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser (UserRegistrationDto newUser){
       try{
			User user =new User ();
			user.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            //Username has to be unique (exception)
			user.setUsername(newUser.getUsername());
			user.setFullName(newUser.getFullName());
            // Make sure that password and confirmPassword match
            // We don't persist or show the confirmPassword
            newUser.setConfirmPassword("");
    		Set<String> strRoles = new HashSet<String>(Arrays.asList(newUser.getRoles().split(",")));
    		Set<Role> roles=new HashSet<Role>();
    		if (strRoles.isEmpty()) {
    			Role studentRole = roleRepository.findByName(ERole.ROLE_STUDENT)
    					.orElseThrow(() -> new RoleException("Error: Role  Student 1 is not found."));
    			roles.add(studentRole);
    		} else {
    			strRoles.forEach(role -> {
    				switch (role) {
    				case "admin":
    					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
    							.orElseThrow(() -> new RoleException("Error: Role  admin is not found."));
    					roles.add(adminRole);

    					break;
    				case "professor":
    					Role profRole = roleRepository.findByName(ERole.ROLE_PROFESSOR)
    							.orElseThrow(() -> new RoleException("Error: Role prof is not found."));
    					roles.add(profRole);

    					break;
    				case "student":
    					Role studentRole = roleRepository.findByName(ERole.ROLE_STUDENT)
    							.orElseThrow(() -> new RoleException("Error: Role  student 2 is not found."));
    					roles.add(studentRole);

    					break;
    				default:{
    					Role studentRole3 = roleRepository.findByName(ERole.ROLE_STUDENT)
    							.orElseThrow(() -> new RoleException("Error: Role student 3 is not found."));
    					roles.add(studentRole3);
    					
    					}
    					break;
    					
    				}
    			});
    		}

    		user.setRoles(roles);
    		
    		
            return userRepository.save(user);
       
        catch (Exception e){
            throw new UsernameAlreadyExistsException("Username '"+newUser.getUsername()+"' already exists");
        }
    }



}
