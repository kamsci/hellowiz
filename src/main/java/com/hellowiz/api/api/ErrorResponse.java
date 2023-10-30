package com.hellowiz.api.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
    private String error;

    @JsonCreator
    public ErrorResponse(@JsonProperty("error") String errorMessage) {
        this.error = errorMessage;
    }

    @JsonProperty
    public String getError() {
        return error;
    }

    @JsonProperty
    public void setError(String error) {
        this.error = error;
    }
}
