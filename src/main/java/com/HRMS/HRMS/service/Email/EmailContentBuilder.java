package com.HRMS.HRMS.service.Email;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailContentBuilder {

    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    public EmailContentBuilder(TemplateEngine templateEngine, ObjectMapper objectMapper) {
        this.templateEngine = templateEngine;
        this.objectMapper = objectMapper;
    }

    public String buildEmail(String templateName, Object dto) {

        Map<String, Object> variables = objectMapper.convertValue(dto, new TypeReference<Map<String, Object>>() {});

        Context context = new Context();
        context.setVariables(variables);

        return templateEngine.process(templateName, context);
    }
}