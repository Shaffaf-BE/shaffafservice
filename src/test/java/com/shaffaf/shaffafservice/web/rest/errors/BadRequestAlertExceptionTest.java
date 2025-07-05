package com.shaffaf.shaffafservice.web.rest.errors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;

/**
 * Test class for BadRequestAlertException to verify the detail field is properly set.
 */
class BadRequestAlertExceptionTest {

    @Test
    void testBadRequestAlertExceptionSetsDetailCorrectly() {
        String errorMessage = "Union member with this phone number already exists for this project";
        String entityName = "unionMember";
        String errorKey = "duplicatemember";

        BadRequestAlertException exception = new BadRequestAlertException(errorMessage, entityName, errorKey);

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ProblemDetailWithCause problemDetail = exception.getProblemDetailWithCause();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
        assertThat(problemDetail.getDetail()).isEqualTo(errorMessage);
        assertThat(problemDetail.getProperties()).containsEntry("message", "error." + errorKey);
        assertThat(problemDetail.getProperties()).containsEntry("params", entityName);
    }

    @Test
    void testBadRequestAlertExceptionWithNullMessage() {
        String entityName = "unionMember";
        String errorKey = "duplicatemember";

        BadRequestAlertException exception = new BadRequestAlertException(null, entityName, errorKey);

        ProblemDetailWithCause problemDetail = exception.getProblemDetailWithCause();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getDetail()).isNull();
    }

    @Test
    void testBadRequestAlertExceptionWithEmptyMessage() {
        String errorMessage = "";
        String entityName = "unionMember";
        String errorKey = "duplicatemember";

        BadRequestAlertException exception = new BadRequestAlertException(errorMessage, entityName, errorKey);

        ProblemDetailWithCause problemDetail = exception.getProblemDetailWithCause();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getDetail()).isEqualTo(errorMessage);
    }
}
