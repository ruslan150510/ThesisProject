package main.controller;

import main.api.response.*;
import main.request.LoginRequest;
import main.request.UserRequest;
import main.service.AuthCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private final AuthCheckService authCheckService;

    public ApiAuthController(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }

    @GetMapping("/auth/check")
    private ResponseEntity<LoginResponse> authCheck(Principal principal) {
        return ResponseEntity.ok(authCheckService.getStatus(principal));
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

    @PostMapping("/auth/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authCheckService.loginResponse(loginRequest));
    }

    @GetMapping("/auth/logout")
    public ResponseEntity<LogoutResponse> logout(Principal principal){
        return ResponseEntity.ok(authCheckService.logoutResponse(principal));
    }
}
