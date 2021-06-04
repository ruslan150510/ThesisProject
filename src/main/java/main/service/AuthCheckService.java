package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.github.cage.IGenerator;
import com.github.cage.image.Painter;
import main.api.response.AuthCheckResponse;
import main.api.response.CaptchaResponse;
import main.api.response.ErrorResponse;
import main.api.response.UserRegistrationResponse;
import main.model.CaptchaCodes;
import main.model.CaptchaCodesRepository;
import main.model.User;
import main.model.UserRepository;
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

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    @Autowired
    private UserRepository userRepository;

    public AuthCheckResponse getStatus() {
        AuthCheckResponse authCheckResponse = new AuthCheckResponse();
        authCheckResponse.setResult(false);
        return authCheckResponse;
    }

    public CaptchaResponse getSecretCode() throws IOException {
        Cage oldCage = new GCage();
        Random rnd = new Random();
        Painter painter = new Painter(100,35, oldCage.getPainter().getBackground(),
                oldCage.getPainter().getQuality(), oldCage.getPainter().getEffectConfig(),
                rnd);
        IGenerator<Font> fonts = oldCage.getFonts();
        IGenerator<Color> foregrounds = oldCage.getForegrounds();
        String format = oldCage.getFormat();
        Float compressRatio = oldCage.getCompressRatio();
        IGenerator<String> tokenGenerator = oldCage.getTokenGenerator();
        Cage cage = new Cage(painter, fonts, foregrounds, format, compressRatio, tokenGenerator, rnd);
        int count = 0;
        LocalDateTime time = ZonedDateTime.now().toLocalDateTime();
        if (captchaCodesRepository.count() > count) {
            count = captchaCodesRepository.findLastId();
        }

        String secret = cage.getTokenGenerator().next();
        byte[] readString = cage.draw(secret);
        String imageEncoding = Base64.getEncoder().encodeToString(readString);

        CaptchaCodes captchaCodes = new CaptchaCodes();
        captchaCodes.setId(++count);
        captchaCodes.setTime(time.atOffset(ZoneOffset.UTC).toLocalDateTime());
        captchaCodes.setCode(secret);
        captchaCodes.setSecretCode(secret);
        captchaCodesRepository.save(captchaCodes);

        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setSecret(secret);
        captchaResponse.setImage(IMAGE_START_STRING + imageEncoding);
        return captchaResponse;
    }

    public UserRegistrationResponse userRegistrationResponse(UserRequest userRequest){
        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
        LocalDateTime time = ZonedDateTime.now().toLocalDateTime();
        int count = 0;
        if (userRepository.findByLastId() > 0)
        {
            count = userRepository.findByLastId();
        }
        User user = new User();
        user.setId(count);
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getUserName());
        user.setPassword(userRequest.getPassword());
        user.setCode(userRequest.getCaptcha());
        user.setIsModerator((byte) 0);
        user.setRegTime(time.atOffset(ZoneOffset.UTC).toLocalDateTime());
        userRepository.save(user);
        if (userRepository.findById(count).isPresent()) {
            userRegistrationResponse.setResult(true);
        }
        else
        {
            ErrorResponse errorResponse = new ErrorResponse();
            userRegistrationResponse.setResult(false);
            userRegistrationResponse.setError(errorResponse);
        }
        return userRegistrationResponse;
    }
}
