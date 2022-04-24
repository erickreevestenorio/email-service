package com.exercise.email.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailResponse extends BaseResponse {

    @Builder
    public EmailResponse(String requestId) {
        super(requestId);
    }
}
