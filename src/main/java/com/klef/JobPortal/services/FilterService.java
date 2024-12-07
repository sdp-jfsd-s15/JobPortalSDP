package com.klef.JobPortal.services;

import com.klef.JobPortal.dtos.FilterResponseDto;
import com.klef.JobPortal.model.Filter;
import com.klef.JobPortal.model.FilterItem;
import com.klef.JobPortal.repository.FilterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilterService {
    private static final Logger logger = LoggerFactory.getLogger(FilterService.class);
    @Autowired
    private FilterRepository filterRepository;

    public FilterResponseDto getAllFilters() {
        logger.info("FilterService.getAllFilters START");
        List<Filter> filters = filterRepository.findAll();

        // Initialize the response DTO
        FilterResponseDto responseDto = new FilterResponseDto();

        // Iterate over filters and classify them based on category
        for (Filter filter : filters) {
            List<String> filterItems = filter.getFilterItems().stream()
                    .map(FilterItem::getFilterName)
                    .collect(Collectors.toList());

            switch (filter.category.toLowerCase()) {
                case "jobs":
                    responseDto.setJobs(filterItems);
                    break;
                case "skills":
                    responseDto.setSkills(filterItems);
                    break;
                case "jobcategory":
                    responseDto.setJobCategory(filterItems);
                    break;
                case "jobtype":
                    responseDto.setJobType(filterItems);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid category");
            }
        }
        logger.info("FilterService.getAllFilters END");
        logger.info("Response DTO: {}", responseDto);
        return responseDto;
    }
}
