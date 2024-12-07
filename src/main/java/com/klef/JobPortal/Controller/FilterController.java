package com.klef.JobPortal.Controller;

import com.klef.JobPortal.dtos.FilterResponseDto;
import com.klef.JobPortal.handler.FilterHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/filter")
public class FilterController {
    @Autowired
    private FilterHandler filterHandler;

    @GetMapping("/filters")
    public FilterResponseDto getAllFilters(HttpServletRequest request) {
        return filterHandler.getAllFilters(request);
    }
}
