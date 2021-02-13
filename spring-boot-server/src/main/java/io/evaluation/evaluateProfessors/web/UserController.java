package io.evaluation.evaluateProfessors.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.evaluation.evaluateProfessors.domain.Role;
import io.evaluation.evaluateProfessors.domain.User;
import io.evaluation.evaluateProfessors.dto.UserRegistrationDto;
import io.evaluation.evaluateProfessors.payload.JWTLoginSucessReponse;
import io.evaluation.evaluateProfessors.payload.LoginRequest;
import io.evaluation.evaluateProfessors.security.JwtTokenProvider;
import io.evaluation.evaluateProfessors.services.MapValidationErrorService;
import io.evaluation.evaluateProfessors.services.UserService;
import io.evaluation.evaluateProfessors.validator.UserValidator;

import static io.evaluation.evaluateProfessors.security.SecurityConstants.TOKEN_PREFIX;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;



    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result){
        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null) return errorMap;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = TOKEN_PREFIX +  tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JWTLoginSucessReponse(true,jwt));
       
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto user, BindingResult result){
        // Validate passwords match
        userValidator.validate(user,result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null)return errorMap;

        User newUser = userService.saveUser(user);

        return  new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }
    
    public static int getRole(Authentication auth) {
    	if(!(auth instanceof AnonymousAuthenticationToken)){
    		if(auth.getAuthorities().stream().anyMatch(
					a -> a.getAuthority().equals("ROLE_PROFESSOR")))
    			return 2;
    		else if (auth.getAuthorities().stream().anyMatch(
        					a -> a.getAuthority().equals("ROLE_ADMIN")))
        		return 1;
    		else if (auth.getAuthorities().stream().anyMatch(
        					a -> a.getAuthority().equals("ROLE_STUDENT")))
        		return 3;
    		else
    			return -1;
    	} 
    	else
    		return -1;
    }
}
