package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequest {
    @JsonProperty("e_mail")
    private String email;

    private String password;

    @JsonProperty("name")
    private String userName;

    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
