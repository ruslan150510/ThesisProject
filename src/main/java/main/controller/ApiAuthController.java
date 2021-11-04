package main.controller;

import main.api.response.*;
import main.request.ChangePasswordRequest;
import main.request.LoginRequest;
import main.request.RestoreRequest;
import main.request.UserRequest;
import main.service.AuthCheckService;
import main.service.SettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private final AuthCheckService authCheckService;
    private final SettingsService settingsService;

    public ApiAuthController(AuthCheckService authCheckService, SettingsService settingsService) {
        this.authCheckService = authCheckService;
        this.settingsService = settingsService;
    }

    @GetMapping("/auth/check")
    public ResponseEntity<LoginResponse> authCheck(Principal principal) {
        return ResponseEntity.ok(authCheckService.getStatus(principal));
    }

    @GetMapping("/auth/captcha")
    public ResponseEntity<CaptchaResponse> captchaGenerate() throws IOException {
        return ResponseEntity.ok(authCheckService.getSecretCode());
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public ResponseEntity<UserRegistrationResponse> userRegistration(@RequestBody UserRequest userRequest) {
        if (settingsService.getGlobalSettings().isMultiuserMode()) {
            return ResponseEntity.ok(authCheckService.userRegistrationResponse(userRequest));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authCheckService.loginResponse(loginRequest));
    }

    @GetMapping("/auth/logout")
    public ResponseEntity<Response> logout(Principal principal) {
        return ResponseEntity.ok(authCheckService.logoutResponse(principal));
    }

    @PostMapping(value = "/image", consumes = "multipart/form-data") //требуется авторизация
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseBody
    public ResponseEntity<?> uploadImage(Principal principal,
                                         @RequestParam(value = "image") MultipartFile multipartFile)
            throws IOException {
        String path = authCheckService.uploadResponse(principal, multipartFile);
        if (path.equals("")) {
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setErrors(new ErrorsImageUpload());
            return new ResponseEntity<>(imageUploadResponse, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(path, HttpStatus.OK);
        }
    }

    @PostMapping(value = "/profile/my", consumes = "multipart/form-data") //требуется авторизация
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseBody
    public ResponseEntity<UserRegistrationResponse> changeProfile
            (Principal principal,
             @RequestParam(value = "email") String email,
             @RequestParam(value = "removePhoto") int removePhoto,
             @RequestParam(value = "photo") MultipartFile multipartFile,
             @RequestParam(value = "name") String name,
             @RequestParam(value = "password", required = false) String password) throws IOException {
        return ResponseEntity.ok(authCheckService.changeProfile(principal,
                email,
                removePhoto,
                multipartFile,
                name,
                password));
    }

    @PostMapping(value = "/profile/my", consumes = "application/json") //требуется авторизация
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseBody
    public ResponseEntity<UserRegistrationResponse> changeProfileWithoutImage
            (Principal principal,
             @RequestParam(value = "email") String email,
             @RequestParam(value = "removePhoto") int removePhoto,
             @RequestParam(value = "photo") MultipartFile multipartFile,
             @RequestParam(value = "name") String name,
             @RequestParam(value = "password", required = false) String password) throws IOException {
        return ResponseEntity.ok(authCheckService.changeProfile(principal,
                email,
                removePhoto,
                multipartFile,
                name,
                password));
    }

    @PostMapping("/auth/restore")
    @ResponseBody
    public ResponseEntity<Response> restorePassword(@RequestBody RestoreRequest restoreRequest) {
        return ResponseEntity.ok(authCheckService.restorePassword(restoreRequest.getEmail()));
    }

    @PostMapping("/auth/password")
    @ResponseBody
    public ResponseEntity<PasswordChangeResponse> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(authCheckService.passwordChangeResponse(changePasswordRequest));
    }
}
