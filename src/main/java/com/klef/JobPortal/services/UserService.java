package com.klef.JobPortal.services;

import com.klef.JobPortal.dtos.AllUsersDto;
import com.klef.JobPortal.dtos.CheckUserDto;
import com.klef.JobPortal.dtos.MessageDto;
import com.klef.JobPortal.dtos.UserDto;
import com.klef.JobPortal.model.Users;
import com.klef.JobPortal.repository.UserRepository;
import com.klef.JobPortal.utils.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    public ResponseEntity<MessageDto> addUser(Users user) {
        try {
            // Set timestamps
            user.setCreatedAt(DateUtils.generateTimeStamp());
            user.setUpdatedAt(DateUtils.generateTimeStamp());
            user.setActive(true);

            // Save user to the repository
            userRepo.save(user);

            MessageDto responseMessage = new MessageDto("User added successfully");
            return ResponseEntity.ok(responseMessage);

        } catch (Exception e) {
            MessageDto errorMessage = new MessageDto("Failed to add user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    public ResponseEntity<Users> getUserDetails(String userName) {
        try {
            Optional<Users> user = userRepo.findUserByUserName(userName);
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return 500 if an error occurs
        }
    }

    public ResponseEntity<CheckUserDto> checkUser(String userName) {
        try {
            Optional<Users> user = userRepo.findUserByUserName(userName);
            if (user.isPresent()) {
                CheckUserDto responseUser = new CheckUserDto();
                responseUser.role = user.get().role.toString();
                responseUser.message = "User exists";
                return ResponseEntity.ok(responseUser);
            } else {
                CheckUserDto responseUser = new CheckUserDto();
                responseUser.role = "";
                responseUser.message = "User does not exist";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseUser);
            }
        } catch (Exception e) {
            CheckUserDto responseUser = new CheckUserDto();
            responseUser.role = "";
            responseUser.message = "Failed to check user: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseUser);
        }
    }

    public ResponseEntity<MessageDto> updateUser(String userName,Users updatedData) {
        try {
            Optional<Users> existingUserOptional  = userRepo.findUserByUserName(userName);

            if (existingUserOptional.isPresent()) {
                Users existingUser = existingUserOptional.get();

                // Update fields directly
                existingUser.createdAt = DateUtils.generateTimeStamp(); // Set createdAt if this is intended
                existingUser.updatedAt = DateUtils.generateTimeStamp();
                existingUser.isActive = true;

                // Update other fields as needed from updatedData
                existingUser.firstName = updatedData.firstName;
                existingUser.lastName = updatedData.lastName;
                existingUser.middleName = updatedData.middleName;
                existingUser.summary = updatedData.summary;
                existingUser.gender = updatedData.gender;
                existingUser.about = updatedData.about;
                existingUser.contactInfo = updatedData.contactInfo;

                // Update the connections count if needed
//                existingUser.updateConnectionsCount();
//
//                // Save the updated user object
//                userRepo.save(existingUser);

                return ResponseEntity.ok(new MessageDto("User updated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageDto("User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageDto("Failed to update user: " + e.getMessage()));
        }
    }

    public ResponseEntity<List<UserDto>> searchUsers(String searchText) {
        try {
            return ResponseEntity.ok(userRepo.searchUsers(searchText));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return 500 if an error occurs
        }
    }

    public AllUsersDto getAllUsers() {
        try {
            // Get the total number of users
            List<Users> users = userRepo.findAll();

            // Map users to UserInfo DTO
            List<AllUsersDto.UserInfo> userDetails = users.stream()
                    .map(user -> new AllUsersDto.UserInfo(
                            user.user_id,
                            user.userName,
                            user.email,
                            user.role,
                            user.firstName,
                            user.middleName,
                            user.lastName,
                            user.contactInfo.getPhone()
                    ))
                    .toList();

            // Return the DTO
            return new AllUsersDto(userDetails);
        } catch (Exception e) {
            // Log the exception (if logger is available)
            return null;
        }
    }

}
