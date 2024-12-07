package com.klef.JobPortal.handler;

import com.klef.JobPortal.dtos.CheckUserDto;
import com.klef.JobPortal.security.JwtVerifier;
import com.klef.JobPortal.dtos.MessageDto;
//import com.klef.JobPortal.model.Events;
import com.klef.JobPortal.dtos.UserDto;
import com.klef.JobPortal.model.Events;
import com.klef.JobPortal.model.Users;
import com.klef.JobPortal.repository.EventsRepository;
import com.klef.JobPortal.services.UserService;
import com.klef.JobPortal.utils.DateUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class UserHandler {
    private final UserService userService;
    private final EventsRepository eventRepo;
    private final Events event = new Events();

    public UserHandler(UserService userService, EventsRepository eventRepo) {
        this.userService = userService;
        this.eventRepo = eventRepo;
    }

    public ResponseEntity<MessageDto> addUser(Users user,HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        event.userName = jwtClaims.get("cognito:username", String.class);
        event.eventType = "add user";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);

        return userService.addUser(user);
    }

    public ResponseEntity<Users> getUserDetails(String userName, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);
        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        event.userName = jwtClaims.get("cognito:username", String.class);
        event.eventType = "get user";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);

        return userService.getUserDetails(userName);
    }

    public ResponseEntity<CheckUserDto> checkUser(String userName, HttpServletRequest request) {
        return userService.checkUser(userName);
    }

    public ResponseEntity<MessageDto> updateUser(String userName, Users user, HttpServletRequest request) {
        return userService.updateUser(userName, user);
    }

    public ResponseEntity<List<UserDto>> searchUsers(String searchKeyword, HttpServletRequest request) {
        return userService.searchUsers(searchKeyword);
    }
}
