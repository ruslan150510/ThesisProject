package main.api.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorsRegistrationResponse {
    private String email;

    private String name;

    public String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String captcha;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String photo;
}
