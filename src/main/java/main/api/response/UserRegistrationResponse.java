package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegistrationResponse {
    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorsResponse errors;
}
