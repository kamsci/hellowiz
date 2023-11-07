package com.hellowiz.api.api.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;

import java.util.Set;
import java.util.stream.Collectors;

public class ErrorResponse {
    private String error;

    private Set<String> violations;

    public ErrorResponse(String error) {
        this.error = error;
    }

    @JsonCreator
    public ErrorResponse(@JsonProperty("error") String error, @JsonProperty("validations") Set<String> violations) {
        this.error = error;
        this.violations = violations;
    }
    @JsonProperty
    public String getError() {
        return error;
    }

    @JsonProperty
    public void setError(String error) {
        this.error = error;
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Set<String> getViolations() {
        return this.violations;
    }

    public void setViolationsFromConstraints(Set<ConstraintViolation<?>> violations) {
        this.violations = getMessagesFromViolations(violations);
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setViolations(Set<String> violations) {
        this.violations = violations;
    }

    @Override
    public String toString() {
        String violationsString = "[]";
        if (this.violations != null) {
            violationsString = this.violations.toString();
        }
        return "ErrorResponse{" +
            "error='" + error + '\'' +
            ", violations=" + violationsString +
            '}';
    }

    public static Set<String> getMessagesFromViolations(Set<ConstraintViolation<?>> violations) {
        return violations.stream().map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());
    }
}
