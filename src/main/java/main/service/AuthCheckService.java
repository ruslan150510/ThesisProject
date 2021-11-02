package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.github.cage.IGenerator;
import com.github.cage.image.Painter;
import main.api.response.*;
import main.model.CaptchaCodes;
import main.model.User;
import main.model.repository.CaptchaCodesRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import main.request.ChangePasswordRequest;
import main.request.LoginRequest;
import main.request.UserRequest;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthCheckService {
    private static final String IMAGE_START_STRING = "data:image/png;base64, ";

    private static final String EMAIL = "Этот e-mail уже зарегистрирован";
    private static final String NAME = "Имя указано неверно";
    private static final String PASSWORD = "Пароль короче 6-ти символов";
    private static final String CAPTCHA = "Код с картинки введён неверно";
    private static final String PHOTO = "Фото слишком большое, нужно не более 5 Мб";
    private static final String CODE = "Ссылка для восстановления пароля устарела. " +
            "<a href=\"/auth/restore\">Запросить ссылку снова</a>";

    private static final String DONT_ADD_PATH_TO_SAVE_IMAGE = "src\\main\\resources";
    private static final String PATH_TO_SAVE_IMAGE = "\\upload";

    private static final Integer PASSWORD_LENGTH = 6;
    private static final long IMAGE_MAX_SIZE = 5 * 1024 * 1024;

    private static final Integer IMAGE_HEIGHT_AND_WIDTH = 36;

    private final AuthenticationManager authenticationManager;

    private static final String EMAIL_FROM = "support@gmail.com";
    private static final String EMAIL_SUBJECT = "Password restore";

    @Autowired
    private MailSender mailSender;

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    public AuthCheckService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse getStatus(Principal principal) {
        if (principal == null) {
            return new LoginResponse();
        } else {
            return getLoginResponse(principal.getName());
        }
    }

    public CaptchaResponse getSecretCode() throws IOException {
        CaptchaCodes captchaCodes = new CaptchaCodes();
        CaptchaResponse captchaResponse = new CaptchaResponse();

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

        String secret = cage.getTokenGenerator().next()
        .substring(0, cage.getTokenGenerator().next().length() - 4);
        byte[] readString = cage.draw(secret);
        String imageEncoding = Base64.getEncoder().encodeToString(readString);

        captchaCodes.setTime(time.atOffset(ZoneOffset.UTC).toLocalDateTime());
        captchaCodes.setCode(secret);
        captchaCodes.setSecretCode(secret);
        captchaCodesRepository.save(captchaCodes);

        captchaResponse.setSecret(secret);
        captchaResponse.setImage(IMAGE_START_STRING + imageEncoding);
        return captchaResponse;
    }

    public UserRegistrationResponse userRegistrationResponse(UserRequest userRequest) {
        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
        LocalDateTime time = ZonedDateTime.now().toLocalDateTime();
        if (userRequest.getCaptchaSecret().equals(userRequest.getCaptcha()) &&
                (!userRepository.findByEmail(userRequest.getEmail()).isPresent()) &&
                (userRequest.getPassword().length() >= PASSWORD_LENGTH) &&
                (userRequest.getUserName().replaceAll("[0-9]", "")
                        .replaceAll(" ", "").length() > 0)) {
            User user = new User();
            user.setEmail(userRequest.getEmail());
            user.setName(userRequest.getUserName());
            user.setPassword(passwordEncoder().encode(userRequest.getPassword()));
            user.setCode(userRequest.getCaptcha());
            user.setIsModerator((byte) 0);
            user.setRegTime(time.atOffset(ZoneOffset.UTC).toLocalDateTime());
            userRepository.save(user);
            userRegistrationResponse.setResult(true);
        } else {
            ErrorsRegistrationResponse errorsRegistrationResponse = new ErrorsRegistrationResponse();
            errorsRegistrationResponse.setEmail(
                    userRepository.findByEmail(userRequest.getEmail()).isPresent() ? EMAIL : null);
            errorsRegistrationResponse.setName(userRequest.getUserName().replaceAll("[0-9]", "")
                    .replaceAll(" ", "").length() == 0 ? NAME : null);
            errorsRegistrationResponse.setPassword(userRequest.getPassword().length() < PASSWORD_LENGTH
                    ? PASSWORD : null);
            errorsRegistrationResponse.setCaptcha(
                    !userRequest.getCaptchaSecret().equals(userRequest.getCaptcha()) ? CAPTCHA : null);
            userRegistrationResponse.setResult(false);
            userRegistrationResponse.setErrors(errorsRegistrationResponse);
        }
        return userRegistrationResponse;
    }

    public LoginResponse loginResponse(LoginRequest loginRequest) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getEmail(),
                                    loginRequest.getPassword()
                            )
                    );
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            org.springframework.security.core.userdetails.User user =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            return getLoginResponse(user.getUsername());
        } catch (Exception exception) {
            return new LoginResponse();
        }
    }

    public LoginResponse getLoginResponse(String email) {
        User currentUser = userRepository
                .findByEmail(email).orElseThrow(
                        () -> new UsernameNotFoundException(email));
        UserLoginResponse userLoginResponse =
                new UserLoginResponse();
        userLoginResponse.setEmail(currentUser.getEmail());
        userLoginResponse.setModeration(currentUser.getIsModerator() == 1);
        userLoginResponse.setSetting(currentUser.getIsModerator() == 1);
        userLoginResponse.setId(currentUser.getId());
        userLoginResponse.setPhoto(currentUser.getPhoto());
        userLoginResponse.setName(currentUser.getName());
        userLoginResponse.setModerationCount(
                currentUser.getIsModerator() == 1 ?
                        postRepository.findNewPost().size() : 0);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userLoginResponse);
        return loginResponse;
    }

    public Response logoutResponse(Principal principal) {
        if (principal != null) {
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        }
        Response logoutResponse = new Response();
        logoutResponse.setResult(true);
        return logoutResponse;
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    public UserRegistrationResponse changeProfile(Principal principal,
                                                  String email,
                                                  int removePhoto,
                                                  MultipartFile multipartFile,
                                                  String name,
                                                  String password) throws IOException {
        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                () -> new UsernameNotFoundException(principal.getName()));
        boolean changePhoto = (!multipartFile.isEmpty() && multipartFile.getSize() < IMAGE_MAX_SIZE)
                || (removePhoto == 1);
        String imageFormat = changePhoto ? multipartFile.getOriginalFilename()
                .substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1) : "";
        if (((password == null)
                || (password != null && password.length() < PASSWORD_LENGTH))
                && (user.getEmail().equals(email) ||
                (!user.getEmail().equals(email)
                        && !userRepository.findByEmailExcludId(email, user.getId()).isPresent()))
                && ((user.getName().equals(name)) ||
                (!user.getName().equals(name)
                        && name.replaceAll("[0-9]", "")
                        .replaceAll(" ", "").length() > 0))
                && ((multipartFile.isEmpty())
                || (!multipartFile.isEmpty() && multipartFile.getSize() < IMAGE_MAX_SIZE))) {
            if (!user.getName().equals(name)) {
                user.setName(name);
            }
            if (!user.getEmail().equals(email)) {
                user.setEmail(email);
            }
            if (password != null && password.length() < PASSWORD_LENGTH) {
                user.setPassword(passwordEncoder().encode(password));
            }
            if (changePhoto) {
                if (removePhoto == 1) {
                    Files.deleteIfExists(Path.of(user.getPhoto()));
                    user.setPhoto("");
                } else {
                    user.setPhoto(extracted(multipartFile, user.getPhoto(), changePhoto));
                }
            }
            userRepository.save(user);
            userRegistrationResponse.setResult(true);
        } else {
            ErrorsRegistrationResponse errorsRegistrationResponse = new ErrorsRegistrationResponse();
            errorsRegistrationResponse.setEmail(!user.getEmail().equals(email)
                    && userRepository.findByEmailExcludId(email, user.getId()).isPresent() ? EMAIL : null);
            errorsRegistrationResponse.setName(!user.getName().equals(name) &&
                    name.replaceAll("[0-9]", "")
                            .replaceAll(" ", "").length() == 0 ? NAME : null);
            errorsRegistrationResponse.setPassword(password != null &&
                    password.length() < PASSWORD_LENGTH ? PASSWORD : null);
            errorsRegistrationResponse.setPhoto(!multipartFile.isEmpty() &&
                    multipartFile.getSize() > IMAGE_MAX_SIZE ? PHOTO : null);
            userRegistrationResponse.setResult(false);
            userRegistrationResponse.setErrors(errorsRegistrationResponse);
        }
        return userRegistrationResponse;
    }

    private String extracted(MultipartFile multipartFile, String path, boolean changePhoto) throws IOException {
        if (path != null) {
            Files.deleteIfExists(Path.of(path));
        }
        String formatName = multipartFile.getOriginalFilename().substring(
                multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        String randomNameFolder = UUID.randomUUID().toString();
        String fullPath = DONT_ADD_PATH_TO_SAVE_IMAGE +
                PATH_TO_SAVE_IMAGE + "\\" +
                randomNameFolder.substring(0, 2);
        Files.createDirectory(Paths.get(fullPath));
        fullPath = fullPath + "\\" + randomNameFolder.substring(2, 4);
        Files.createDirectory(Paths.get(fullPath));
        fullPath = fullPath + "\\" + randomNameFolder.substring(4, 6);
        Files.createDirectory(Paths.get(fullPath));
        String fileName = randomNameFolder.substring(6);
        fullPath = fullPath +
                "\\" + fileName + "." + formatName;
        Path filePath = Paths.get(fullPath);
        if (changePhoto) {
            try (OutputStream os = Files.newOutputStream(filePath)) {
                os.write(multipartFile.getBytes());
            }
        } else {
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            BufferedImage newImage = Scalr.resize(image,
                    Scalr.Method.AUTOMATIC,
                    IMAGE_HEIGHT_AND_WIDTH,
                    IMAGE_HEIGHT_AND_WIDTH);
            File file = new File(fileName);
            ImageIO.write(newImage, formatName, file);
        }
        return filePath.toString().replace(DONT_ADD_PATH_TO_SAVE_IMAGE, "");
    }

    public String uploadResponse(Principal principal, MultipartFile multipartFile) throws IOException {
        ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                () -> new UsernameNotFoundException(principal.getName()));
        boolean changePhoto = false;
        String imageFormat = multipartFile.getOriginalFilename().substring(
                multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        if ((multipartFile.getSize() < IMAGE_MAX_SIZE) &&
                ((ImageFormat.jpg.equals(imageFormat)) ||
                        (ImageFormat.png.equals(imageFormat)))) {
            return extracted(multipartFile, "", changePhoto);
        } else {
            return "";
        }
    }

    public Response restorePassword(String email) {
        Response response = new Response();
        try {
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException(email));
            String code = UUID.randomUUID().toString();
            user.setCode(code);
            userRepository.save(user);
            mailSender.send(email,
                    String.format("http://localhost:8080/login/change-password/%s", code));
            response.setResult(true);
        } catch (Exception exception) {
            response.setResult(false);
        }
        return response;
    }

    public PasswordChangeResponse passwordChangeResponse(ChangePasswordRequest changePasswordRequest) {
        PasswordChangeResponse passwordChangeResponse = new PasswordChangeResponse();
        if ((changePasswordRequest.getPassword().length() >= PASSWORD_LENGTH) &&
                (changePasswordRequest.getCaptcha().equals(changePasswordRequest.getCaptchaSecret()))
                && (userRepository.findByCode(changePasswordRequest.getCode()).isPresent())) {
            User user = userRepository.findByCode(changePasswordRequest.getCode()).get();
            user.setPassword(passwordEncoder().encode(changePasswordRequest.getPassword()));
            user.setCode(null);
            userRepository.save(user);
            passwordChangeResponse.setResult(true);
        } else {
            ErrorPasswordResponse errorPasswordResponse = new ErrorPasswordResponse();
            errorPasswordResponse.setPassword(
                    changePasswordRequest.getPassword().length() < PASSWORD_LENGTH ? PASSWORD : null);
            errorPasswordResponse.setCode(
                    !userRepository.findByCode(changePasswordRequest.getCode()).isPresent() ? CODE : null);
            errorPasswordResponse.setCaptcha(
                    !changePasswordRequest.getCaptcha().equals(changePasswordRequest.getCaptchaSecret()) ?
                            CAPTCHA : null);
        }
        return passwordChangeResponse;
    }
}
