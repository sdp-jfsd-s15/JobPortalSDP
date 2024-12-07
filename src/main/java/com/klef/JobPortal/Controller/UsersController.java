package com.klef.JobPortal.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klef.JobPortal.dtos.CheckUserDto;
import com.klef.JobPortal.dtos.MessageDto;
import com.klef.JobPortal.dtos.UserDto;
import com.klef.JobPortal.handler.UserHandler;
import com.klef.JobPortal.model.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("v1/api/users")
public class UsersController {
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final UserHandler userHandler;

    public UsersController(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @PostMapping(path = "/add")
    public ResponseEntity<MessageDto> createEmployee(
            @RequestBody Users user,
            HttpServletRequest request){
        return userHandler.addUser(user, request);
    }

    @GetMapping(path = "/getDetails/{userName}")
    public ResponseEntity<Users> getUserDetails(@PathVariable("userName") String userName, HttpServletRequest request) {
        return userHandler.getUserDetails(userName, request);
    }

    @GetMapping(path = "/checkUser/{userName}")
    public ResponseEntity<CheckUserDto> checkUser(@PathVariable("userName") String userName, HttpServletRequest request) {
        return userHandler.checkUser(userName, request);
    }

    @PutMapping(path = "/updateUser/{userName}")
    public ResponseEntity<MessageDto> updateUser(@PathVariable("userName") String userName, @RequestBody Users users, HttpServletRequest request) {
        return userHandler.updateUser(userName, users, request);
    }

    @GetMapping(path = "/searchUsers")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam("searchKeyword") String searchKeyword, HttpServletRequest request) {
        return userHandler.searchUsers(searchKeyword, request);
    }
}
