package main.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import main.api.response.AuthCheckResponse;
import main.api.response.CaptchaResponse;
import main.api.response.UserRegistrationResponse;
import main.request.UserRequest;
import main.service.AuthCheckService;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private final AuthCheckService authCheckService;

    public ApiAuthController(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }

    @GetMapping("/auth/check")
    private AuthCheckResponse authCheck() {
        return authCheckService.getStatus();
    }

    @GetMapping("/auth/captcha")
    private CaptchaResponse captchaGenerate() throws IOException {
        return authCheckService.getSecretCode();
    }

    @PostMapping("/auth/register")
    @ResponseBody
    private UserRegistrationResponse userRegistration(@RequestBody UserRequest userRequest){
        return authCheckService.userRegistrationResponse(userRequest);
    }
}
