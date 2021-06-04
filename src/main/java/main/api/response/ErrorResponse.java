package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
    @JsonProperty("email")
    private static final String EMAIL = "Этот e-mail уже зарегистрирован";
    @JsonProperty("name")
    private static final String NAME = "Имя указано неверно";
    @JsonProperty("password")
    private static final String PASSWORD = "Пароль короче 6-ти символов";
    @JsonProperty("captcha")
    private static final String CAPTCHA = "Код с картинки введён неверно";
}
