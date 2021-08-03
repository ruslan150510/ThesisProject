package main.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginResponse {
    private boolean result;

    @JsonProperty("user")
    @JsonIgnore
    private UserLoginResponse userLoginResponse;
}
