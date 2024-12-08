package com.klef.JobPortal.services;

import com.klef.JobPortal.dtos.MessageDto;
import com.klef.JobPortal.model.ConnectionInfo;
import com.klef.JobPortal.model.Connections;
import com.klef.JobPortal.model.Users;
import com.klef.JobPortal.repository.ConnectionRepository;
import com.klef.JobPortal.repository.UserRepository;
import com.klef.JobPortal.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConnectionService {
    private final ConnectionRepository connectionsRepo;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionService.class);
    private final UserRepository userRepo;

    public ConnectionService(ConnectionRepository connectionsRepo, UserRepository userRepo) {
        this.connectionsRepo = connectionsRepo;
        this.userRepo = userRepo;
    }

    public ResponseEntity<MessageDto> addConnection(ConnectionInfo connection, String userName) {
        try {
            // Validate if primary user exists
            Optional<Users> primaryUser = userRepo.findUserByUserName(userName);

            // Validate if target user exists
            Optional<Users> targetUser = userRepo.findUserByUserName(connection.userName);

            if(primaryUser.isEmpty() || targetUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageDto("User not found"));
            }

            // Fetch existing connections for the primary user
            Connections primaryConnections = connectionsRepo.findByUserName(userName);

            if (primaryConnections == null) {
                // Create a new connection entry if none exists
                primaryConnections = new Connections();
                primaryConnections.userName = userName;
                primaryConnections.connections = new ArrayList<>();
                primaryConnections.createdAt = DateUtils.generateTimeStamp();
                primaryConnections.isActive = true;
            }

            // Check if the connection already exists
            Optional<ConnectionInfo> existingPrimaryConnection = primaryConnections.connections.stream()
                    .filter(conn -> conn.userName.equals(connection.userName))
                    .findFirst();

            if (existingPrimaryConnection.isPresent()) {
                // Update the timestamp of the existing connection
                if (existingPrimaryConnection.get().status.equals("REJECTED")) {
                    // Update the status from REJECTED to PENDING
                    existingPrimaryConnection.get().status = "PENDING";
                }
                existingPrimaryConnection.get().updatedAt = DateUtils.generateTimeStamp();
            } else {
                // Add new connection info if it doesn't already exist
                ConnectionInfo primaryToTargetConnection = new ConnectionInfo();
                primaryToTargetConnection.userName = connection.userName;
                primaryToTargetConnection.firstName = targetUser.get().firstName;
                primaryToTargetConnection.lastName = targetUser.get().lastName;
                primaryToTargetConnection.status = "PENDING";
                primaryToTargetConnection.createdAt = DateUtils.generateTimeStamp();
                primaryToTargetConnection.updatedAt = DateUtils.generateTimeStamp();
                primaryConnections.connections.add(primaryToTargetConnection);
            }

            // Fetch existing connections for the target user
            Connections targetConnections = connectionsRepo.findByUserName(connection.userName);

            if (targetConnections == null) {
                // Create a new connection entry for the target user if none exists
                targetConnections = new Connections();
                targetConnections.userName = connection.userName;
                targetConnections.connections = new ArrayList<>();
                targetConnections.createdAt = DateUtils.generateTimeStamp();
                targetConnections.isActive = true;
            }

            // Check if the reverse connection already exists
            Optional<ConnectionInfo> existingTargetConnection = targetConnections.connections.stream()
                    .filter(conn -> conn.userName.equals(userName))
                    .findFirst();

            if (existingTargetConnection.isPresent()) {
                // Update the timestamp of the existing connection
                if (existingTargetConnection.get().status.equals("REJECTED")) {
                    existingTargetConnection.get().status = "REQUEST";
                }
                existingTargetConnection.get().updatedAt = DateUtils.generateTimeStamp();
            } else {
                // Add new connection info for the reverse connection
                ConnectionInfo targetToPrimaryConnection = new ConnectionInfo();
                targetToPrimaryConnection.userName = userName;
                targetToPrimaryConnection.firstName = primaryUser.get().firstName;
                targetToPrimaryConnection.lastName = primaryUser.get().lastName;
                targetToPrimaryConnection.status = "REQUEST";
                targetToPrimaryConnection.createdAt = DateUtils.generateTimeStamp();
                targetToPrimaryConnection.updatedAt = DateUtils.generateTimeStamp();
                targetConnections.connections.add(targetToPrimaryConnection);
            }

            // Save the updated connection entries back to the database
            connectionsRepo.save(primaryConnections);
            connectionsRepo.save(targetConnections);

            return ResponseEntity.ok(new MessageDto("Connection added or updated successfully"));
        } catch (Exception e) {
            logger.error("Error adding connection: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDto("Failed to add connection: " + e.getMessage()));
        }
    }

    public ResponseEntity<Connections> getConnections(String user) {
        try {
            Connections connections = connectionsRepo.findByUserName(user);
            if (connections == null || connections.connections == null) {
                return ResponseEntity.ok(new Connections()); // Return an empty model if no connections found
            }

            // Filter the connections by status: ACCEPTED

            // Set the filtered connections back to the Connections model
            connections.connections = connections.connections.stream()
                    .filter(connection -> "ACCEPTED".equalsIgnoreCase(connection.status))
                    .toList();

            return ResponseEntity.ok(connections);
        } catch (Exception e) {
            logger.error("Error getting connections: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Connections());
        }
    }



    public ResponseEntity<MessageDto> checkConnection(String userName, String user) {
        try{
            Connections connections = connectionsRepo.findByUserName(user);
            Optional<ConnectionInfo> connection = connections.connections.stream()
                    .filter(conn -> conn.userName.equals(userName))
                    .findFirst();

            if (connection.isPresent()) {
                if(connection.get().status.equals("ACCEPTED")) {
                    return ResponseEntity.ok(new MessageDto("You are already connected with " + userName));
                }
                else if(connection.get().status.equals("REJECTED")) {
                  return ResponseEntity.status(204).body(new MessageDto("You Request got Rejected"));
                }
                else {
                    return ResponseEntity.status(202).body(new MessageDto("You have a connection request from " + userName));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDto("You have no connection with " + userName));
            }
        } catch (Exception e) {
            logger.error("Error checking connection: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDto("Failed to check connection: " + e.getMessage()));
        }
    }

    public ResponseEntity<List<ConnectionInfo>> getConnectionRequests(String userName) {
        try {
            // Fetch the connections for the primary user
            Connections primaryConnections = connectionsRepo.findByUserName(userName);

            logger.info("Fetched {}", primaryConnections.connections);

            if (primaryConnections.connections == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ArrayList<>());
            }

            // Filter connections with status "REQUEST"
            List<ConnectionInfo> requestConnections = primaryConnections.connections.stream()
                    .filter(conn -> "REQUEST".equals(conn.status))
                    .collect(Collectors.toList());

            logger.info("Fetched {}", requestConnections);

            if (requestConnections.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ArrayList<>());
            }

            return ResponseEntity.ok(requestConnections);
        } catch (Exception e) {
            logger.error("Error fetching connections: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());
        }
    }

    public ResponseEntity<MessageDto> updateConnection(String userName, String status, String user) {
        try {
            // Fetch the connections for the target user
            Connections connections = connectionsRepo.findByUserName(user);
            Connections connectionsUser = connectionsRepo.findByUserName(userName);

            // Find the connection information for the given userName
            Optional<ConnectionInfo> connection = connections.connections.stream()
                    .filter(conn -> conn.userName.equals(userName))
                    .findFirst();

            Optional<ConnectionInfo> connectionUser = connectionsUser.connections.stream()
                    .filter(conn -> conn.userName.equals(user))
                    .findFirst();

            if (connection.isPresent() && connectionUser.isPresent()) {
                if ("REMOVE".equalsIgnoreCase(status)) {
                    // Set both connection statuses to empty if status is "REMOVE"
                    connection.get().status = "";
                    connectionUser.get().status = "";

                    // Update the timestamp for both connections
                    connection.get().updatedAt = DateUtils.generateTimeStamp();
                    connectionUser.get().updatedAt = DateUtils.generateTimeStamp();

                    // Decrement the connections count for both users
                    Optional<Users> userEntityOptional = userRepo.findUserByUserName(user); // Fetch the requesting user
                    Optional<Users> targetUserEntityOptional = userRepo.findUserByUserName(userName); // Fetch the target user

                    // Check if both users exist
                    if (userEntityOptional.isPresent() && targetUserEntityOptional.isPresent()) {
                        Users userEntity = userEntityOptional.get();
                        Users targetUserEntity = targetUserEntityOptional.get();

                        // Decrement the connectionsCount for both users
                        userEntity.connectionsCount--;
                        targetUserEntity.connectionsCount--;

                        // Save the updated entities
                        userRepo.save(userEntity);
                        userRepo.save(targetUserEntity);
                    } else {
                        // Handle case where one or both users do not exist
                        logger.error("User or target user not found: user={}, targetUser={}", user, userName);
                        throw new IllegalArgumentException("User or target user not found");
                    }
                } else {
                    // Update the status of the connection if not "REMOVE"
                    connection.get().status = status;
                    connectionUser.get().status = status;

                    // Update the timestamp for both connections
                    connection.get().updatedAt = DateUtils.generateTimeStamp();
                    connectionUser.get().updatedAt = DateUtils.generateTimeStamp();

                    // If status is ACCEPTED, update connection counts
                    if ("ACCEPTED".equalsIgnoreCase(status)) {
                        Optional<Users> userEntityOptional = userRepo.findUserByUserName(user); // Fetch the requesting user
                        Optional<Users> targetUserEntityOptional = userRepo.findUserByUserName(userName); // Fetch the target user

                        // Check if both users exist
                        if (userEntityOptional.isPresent() && targetUserEntityOptional.isPresent()) {
                            Users userEntity = userEntityOptional.get();
                            Users targetUserEntity = targetUserEntityOptional.get();

                            // Increment the connectionsCount for both users
                            userEntity.connectionsCount++;
                            targetUserEntity.connectionsCount++;

                            // Save the updated entities
                            userRepo.save(userEntity);
                            userRepo.save(targetUserEntity);
                        } else {
                            // Handle case where one or both users do not exist
                            logger.error("User or target user not found: user={}, targetUser={}", user, userName);
                            throw new IllegalArgumentException("User or target user not found");
                        }
                    }
                }

                // Save the updated connections back to the database
                connectionsRepo.save(connections);
                connectionsRepo.save(connectionsUser);

                // Return success response
                return ResponseEntity.ok(new MessageDto("Connection status updated successfully"));
            } else {
                // Return error if connection not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageDto("Connection not found"));
            }
        } catch (Exception e) {
            // Log the error and return a failure response
            logger.error("Error updating connection status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDto("Failed to update connection status: " + e.getMessage()));
        }
    }

    public ResponseEntity<Integer> countConnections(String user) {
        try {
            Connections connections = connectionsRepo.findByUserName(user);

            if (connections == null || connections.connections == null) {
                return ResponseEntity.ok(0); // Return 0 if no connections found
            }

            // Filter connections by status: ACCEPTED and count them
            int acceptedCount = (int) connections.connections.stream()
                    .filter(connection -> "ACCEPTED".equalsIgnoreCase(connection.status))
                    .count();

            return ResponseEntity.ok(acceptedCount);
        } catch (Exception e) {
            logger.error("Error counting accepted connections: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
        }
    }

}
