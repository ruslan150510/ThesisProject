package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.github.cage.IGenerator;
import com.github.cage.image.Painter;
import main.api.response.AuthCheckResponse;
import main.api.response.CaptchaResponse;
import main.api.response.ErrorsResponse;
import main.api.response.UserRegistrationResponse;
import main.model.CaptchaCodes;
import main.model.User;
import main.model.repository.CaptchaCodesRepository;
import main.model.repository.UserRepository;
import main.request.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Random;

@Service
public class AuthCheckService {
    private static final String IMAGE_START_STRING = "data:image/png;base64, ";

    private static final String EMAIL = "Этот e-mail уже зарегистрирован";
    private static final String NAME = "Имя указано неверно";
    private static final String PASSWORD = "Пароль короче 6-ти символов";
    private static final String CAPTCHA = "Код с картинки введён неверно";

//    private final AuthenticationManager authenticationManager;

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    public AuthCheckService(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }

    public AuthCheckResponse getStatus() {
        AuthCheckResponse authCheckResponse = new AuthCheckResponse();
        authCheckResponse.setResult(false);
        return authCheckResponse;
    }

    public CaptchaResponse getSecretCode() throws IOException {
        LocalDateTime time = ZonedDateTime.now().toLocalDateTime();
        captchaCodesRepository.deleteByOldRecord(time.minusHours(1)
                .atOffset(ZoneOffset.UTC).toLocalDateTime());

        Cage oldCage = new GCage();
        Random rnd = new Random();
        Painter painter = new Painter(100, 35, oldCage.getPainter().getBackground(),
                oldCage.getPainter().getQuality(), oldCage.getPainter().getEffectConfig(),
                rnd);
        IGenerator<Font> fonts = oldCage.getFonts();
        IGenerator<Color> foregrounds = oldCage.getForegrounds();
        String format = oldCage.getFormat();
        Float compressRatio = oldCage.getCompressRatio();
        IGenerator<String> tokenGenerator = oldCage.getTokenGenerator();
        Cage cage = new Cage(painter, fonts, foregrounds, format, compressRatio, tokenGenerator, rnd);

        String secret = cage.getTokenGenerator().next();
        byte[] readString = cage.draw(secret);
        String imageEncoding = Base64.getEncoder().encodeToString(readString);

        CaptchaCodes captchaCodes = new CaptchaCodes();
        captchaCodes.setTime(time.atOffset(ZoneOffset.UTC).toLocalDateTime());
        captchaCodes.setCode(secret);
        captchaCodes.setSecretCode(secret);
        captchaCodesRepository.save(captchaCodes);

        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setSecret(secret);
        captchaResponse.setImage(IMAGE_START_STRING + imageEncoding);
        return captchaResponse;
    }

    public UserRegistrationResponse userRegistrationResponse(UserRequest userRequest) {
        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
        LocalDateTime time = ZonedDateTime.now().toLocalDateTime();
        if (userRequest.getCaptchaSecret().equals(userRequest.getCaptcha()) &&
                (!userRepository.findByEmail(userRequest.getEmail()).isPresent()) &&
                (userRequest.getPassword().length() > 5) &&
                (userRequest.getUserName().replaceAll("[0-9]", "")
                        .replaceAll(" ", "").length() > 0)) {
            User user = new User();
            user.setEmail(userRequest.getEmail());
            user.setName(userRequest.getUserName());
            user.setPassword(userRequest.getPassword());
            user.setCode(userRequest.getCaptcha());
            user.setIsModerator((byte) 0);
            user.setRegTime(time.atOffset(ZoneOffset.UTC).toLocalDateTime());
            userRepository.save(user);
            userRegistrationResponse.setResult(true);
        } else {
            ErrorsResponse errorsResponse = new ErrorsResponse();
            errorsResponse.setEmail(
                    userRepository.findByEmail(userRequest.getEmail()).isPresent() ? EMAIL : null);
            errorsResponse.setName(userRequest.getUserName().replaceAll("[0-9]", "")
                    .replaceAll(" ", "").length() == 0 ? NAME : null);
            errorsResponse.setPassword(userRequest.getPassword().length() < 6 ? PASSWORD : null);
            errorsResponse.setCaptcha(
                    !userRequest.getCaptchaSecret().equals(userRequest.getCaptcha()) ? CAPTCHA : null);
            userRegistrationResponse.setResult(false);
            userRegistrationResponse.setErrors(errorsResponse);
        }
        return userRegistrationResponse;
    }

//    public LoginResponse loginResponse(LoginRequest loginRequest){
//        Authentication authentication =
//                authenticationManager.authenticate(
//                        new UsernamePasswordAuthenticationToken(
//                                loginRequest.getEmail(),
//                                loginRequest.getPassword()
//                        )
//                );
//        SecurityContextHolder.getContext()
//                .setAuthentication(authentication);
//        org.springframework.security.core.userdetails.User user =
//                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
//        return getLoginResponse(user.getUsername());
//    }

//    public LoginResponse getLoginResponse(String name){
//        User currentUser = userRepository
//                .findByEmail(name).orElseThrow(
//                        ()-> new UsernameNotFoundException(name));
//        UserLoginResponse userLoginResponse =
//                new UserLoginResponse();
//        userLoginResponse.setEmail(currentUser.getEmail());
//        userLoginResponse.setModeration(currentUser.getIsModerator()==1);
//        userLoginResponse.setId(currentUser.getId());
//        userLoginResponse.setPhoto(currentUser.getPhoto());
//        userLoginResponse.setName(currentUser.getName());
//
//        LoginResponse loginResponse = new LoginResponse();
//        loginResponse.setResult(true);
//        loginResponse.setUserLoginResponse(userLoginResponse);
//        return loginResponse;
//    }
}
