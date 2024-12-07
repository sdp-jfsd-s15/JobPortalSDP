package com.klef.JobPortal.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class ListToStringConverter implements AttributeConverter<List<String>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return attribute != null ? String.join(SEPARATOR, attribute) : null;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return dbData != null && !dbData.isEmpty() ? Arrays.asList(dbData.split(SEPARATOR)) : null;
    }
}
