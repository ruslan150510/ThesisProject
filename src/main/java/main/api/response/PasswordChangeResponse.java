package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeResponse {
    private boolean result;

    @JsonProperty("errors")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorPasswordResponse errorPasswordResponse;
}
