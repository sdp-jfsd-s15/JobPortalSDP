package com.klef.JobPortal.handler;

import com.klef.JobPortal.dtos.MessageDto;
import com.klef.JobPortal.model.ConnectionInfo;
import com.klef.JobPortal.model.Connections;
import com.klef.JobPortal.model.Events;
import com.klef.JobPortal.repository.EventsRepository;
import com.klef.JobPortal.security.JwtVerifier;
import com.klef.JobPortal.services.ConnectionService;
import com.klef.JobPortal.utils.DateUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final ConnectionService connectionService;
    private final EventsRepository eventRepo;
    private final Events event = new Events();

    public ConnectionHandler(ConnectionService connectionService, EventsRepository eventRepo) {
        this.connectionService = connectionService;
        this.eventRepo = eventRepo;
    }
    public ResponseEntity<MessageDto> addConnection(ConnectionInfo connection, HttpServletRequest request) {
        logger.info("ConnectionHandler.addConnection START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);
        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String user = jwtClaims.get("cognito:username", String.class);
        event.userName = user;
        event.eventType = "get user";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("ConnectionHandler.addConnection END");
        return connectionService.addConnection(connection, user);
    }

    public ResponseEntity<Connections> getConnections(HttpServletRequest request) {
        logger.info("ConnectionHandler.getConnections START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);
        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String user = jwtClaims.get("cognito:username", String.class);
        event.userName = user;
        event.eventType = "get user";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("ConnectionHandler.getConnections END");
        return connectionService.getConnections(user);
    }

    public ResponseEntity<MessageDto> checkConnection(String userName, HttpServletRequest request) {
        logger.info("ConnectionHandler.checkConnection START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);
        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String user = jwtClaims.get("cognito:username", String.class);
        event.userName = user;
        event.eventType = "get user";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("ConnectionHandler.checkConnection END");
        return connectionService.checkConnection(userName, user);
    }

    public ResponseEntity<List<ConnectionInfo>> getConnectionRequests(HttpServletRequest request) {
        logger.info("ConnectionHandler.getConnectionRequests START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);
        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String user = jwtClaims.get("cognito:username", String.class);
        event.userName = user;
        event.eventType = "get user";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("ConnectionHandler.getConnectionRequests END");
        return connectionService.getConnectionRequests(user);
    }

    public ResponseEntity<MessageDto> updateConnection(String userName, String status, HttpServletRequest request) {
        logger.info("ConnectionHandler.updateConnection START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);
        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String user = jwtClaims.get("cognito:username", String.class);
        event.userName = user;
        event.eventType = "get user";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("ConnectionHandler.updateConnection END");
        return connectionService.updateConnection(userName, status, user);
    }

    public ResponseEntity<Integer> countConnections(HttpServletRequest request) {
        logger.info("ConnectionHandler.countConnections START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);
        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String user = jwtClaims.get("cognito:username", String.class);
        event.userName = user;
        event.eventType = "get user";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("ConnectionHandler.countConnections END");
        return connectionService.countConnections(user);
    }

    public ResponseEntity<Integer> countUserConnections(String userName, HttpServletRequest request) {
        logger.info("ConnectionHandler.countUserConnections START");
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
        logger.info("ConnectionHandler.countUserConnections END");
        return connectionService.countConnections(userName);
    }

    public ResponseEntity<Connections> getConnectionsOnUserName(String userName, HttpServletRequest request) {
        logger.info("ConnectionHandler.getConnectionsOnUserName START");
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
        logger.info("ConnectionHandler.getConnectionsOnUserName END");
        return connectionService.getConnections(userName);
    }
}
