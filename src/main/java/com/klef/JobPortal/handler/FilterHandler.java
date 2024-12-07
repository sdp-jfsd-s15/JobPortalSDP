package com.klef.JobPortal.handler;

import com.klef.JobPortal.dtos.FilterResponseDto;
import com.klef.JobPortal.model.Events;
import com.klef.JobPortal.repository.EventsRepository;
import com.klef.JobPortal.security.JwtVerifier;
import com.klef.JobPortal.services.FilterService;
import com.klef.JobPortal.utils.DateUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterHandler {
    private static final Logger logger = LoggerFactory.getLogger(FilterHandler.class);
    private final FilterService filterService;
    private final EventsRepository eventRepo;

    private final Events event = new Events();

    public FilterHandler(FilterService filterService, EventsRepository eventRepo) {
        this.filterService = filterService;
        this.eventRepo = eventRepo;
    }

    public FilterResponseDto getAllFilters(HttpServletRequest request) {
        logger.info("FilterHandler.getAllFilters START");
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

        logger.info("FilterHandler.getAllFilters END");
        return filterService.getAllFilters();
    }
}
