package com.rrom.task_management.dto;

import com.rrom.task_management.dto.request.TaskCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskCreateDtoTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDTO() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Valid Title");
        dto.setDescription("Desc");

        Set<ConstraintViolation<TaskCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
