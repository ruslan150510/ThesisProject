package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String code;
    private String password;
    private String captcha;

    @JsonProperty(value = "captcha_secret")
    private String captchaSecret;
}
