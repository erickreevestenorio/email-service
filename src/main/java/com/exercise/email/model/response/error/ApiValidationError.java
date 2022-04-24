package com.exercise.email.model.response.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiValidationError implements ApiSubError {
    @JsonIgnore
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    public ApiValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }

    public ApiValidationError(String object, String field, String message) {
        this.object = object;
        this.field = field;
        this.message = message;
    }
}
