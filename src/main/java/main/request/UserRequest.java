package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

//"e_mail":"ru@mail.ru",
// "password":"123456",
// "name":"Ru",
// "captcha":"agejorerro",
// "captcha_secret":"agejoremo"