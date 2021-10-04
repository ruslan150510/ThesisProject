package main.api.response;

import lombok.Data;

@Data
public class ErrorPasswordResponse {
    private String code;
    private String password;
    private String captcha;
}
