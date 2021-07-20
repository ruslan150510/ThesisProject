package main.controller;

import main.api.response.AuthCheckResponse;
import main.api.response.CaptchaResponse;
import main.api.response.LoginResponse;
import main.api.response.UserRegistrationResponse;
import main.request.LoginRequest;
import main.request.UserRequest;
import main.service.AuthCheckService;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
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

//    @PostMapping("/auth/login")
//    @ResponseBody
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
//        return ResponseEntity.ok(authCheckService.loginResponse(loginRequest));
//    }
}
