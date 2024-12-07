package com.klef.JobPortal.Controller;

import com.klef.JobPortal.dtos.MessageDto;
import com.klef.JobPortal.handler.ConnectionHandler;
import com.klef.JobPortal.model.ConnectionInfo;
import com.klef.JobPortal.model.Connections;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("v1/api/connections")
public class ConnectionController {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionController.class);
    private final ConnectionHandler connectionHandler;

    public ConnectionController(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @PostMapping("/addConnection")
    public ResponseEntity<MessageDto> addConnection(@RequestBody ConnectionInfo connection, HttpServletRequest request) {
        return connectionHandler.addConnection(connection, request);
    }

    @GetMapping("/get")
    public ResponseEntity<Connections> getConnections(HttpServletRequest request) {
        return connectionHandler.getConnections(request);
    }

    @GetMapping("/get/{userName}")
    public ResponseEntity<Connections> getConnectionsOnUserName(@PathVariable("userName") String userName,HttpServletRequest request) {
        return connectionHandler.getConnectionsOnUserName(userName, request);
    }

    @GetMapping("checkConnection/{userName}")
    public ResponseEntity<MessageDto> checkConnection(@PathVariable("userName") String userName, HttpServletRequest request) {
        return connectionHandler.checkConnection(userName, request);
    }

    @GetMapping("/getConnectionRequests")
    public ResponseEntity<List<ConnectionInfo>> getConnectionRequests(HttpServletRequest request) {
        return connectionHandler.getConnectionRequests(request);
    }

    @PutMapping("/updateConnection/{userName}/{status}")
    public ResponseEntity<MessageDto> updateConnection(@PathVariable("userName") String userName, @PathVariable("status") String status, HttpServletRequest request) {
        return connectionHandler.updateConnection(userName, status, request);
    }

    @GetMapping("/countConnections")
    public ResponseEntity<Integer> countConnections(HttpServletRequest request) {
        return connectionHandler.countConnections(request);
    }

    @GetMapping("/countConnections/{userName}")
    public ResponseEntity<Integer> countUserConnections(@PathVariable("userName") String userName,HttpServletRequest request) {
        return connectionHandler.countUserConnections(userName, request);
    }

}
