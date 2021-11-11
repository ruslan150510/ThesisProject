package main.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthCheckService {
    @Value("${send.localhost}")
    private String LOCALHOST_RESTORE;

    @Value("${production.cloud_name}")
    private String CLOUD_NAME;
    @Value("${production.api_key}")
    private String API_KEY;
    @Value("${production.api_secret}")
    private String API_SECRET;
    @Value("${production.secure}")
    private Boolean SECURE;

    private static final String IMAGE_START_STRING = "data:image/png;base64, ";

    private static final String EMAIL = "Этот e-mail уже зарегистрирован";
    private static final String NAME = "Имя указано неверно";
    private static final String PASSWORD = "Пароль короче 6-ти символов";
    private static final String CAPTCHA = "Код с картинки введён неверно";
    private static final String PHOTO = "Фото слишком большое, нужно не более 5 Мб";
    private static final String CODE = "Ссылка для восстановления пароля устарела. " +
            "<a href=\"/auth/restore\">Запросить ссылку снова</a>";

    private static final Boolean IS_AVATAR = true;

    private static final Integer PASSWORD_LENGTH = 6;
    private static final long IMAGE_MAX_SIZE = 5 * 1024 * 1024 * 8;

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
                                                  String password) throws Exception {
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
                    user.setPhoto(extracted(multipartFile, user.getPhoto(), IS_AVATAR));
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

    private String extracted(MultipartFile multipartFile, String path, boolean isAvatar) throws Exception {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET,
                "secure", SECURE));
        String fullPath = "";
//        if (path != null) {
//            if (isAvatar)
//            path = path.substring(path.lastIndexOf("/") + 1);
//            cloudinary.api().deleteAllResources(ObjectUtils.asMap("public_id", path));
//            cloudinary.api().deleteAllResources(ObjectUtils.emptyMap());
//        }
        String formatName = multipartFile.getOriginalFilename().substring(
                multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        String randomNameFolder = UUID.randomUUID().toString();
        fullPath = randomNameFolder.substring(0, 2);
        Files.createDirectory(Paths.get(fullPath));
        fullPath = fullPath + "/" + randomNameFolder.substring(2, 4);
        Files.createDirectory(Paths.get(fullPath));
        fullPath = fullPath + "/" + randomNameFolder.substring(4, 6);
//        Files.createDirectory(Paths.get(fullPath));
        String fileName = randomNameFolder.substring(6);
        Map params = ObjectUtils.asMap("public_id", fullPath);//fileName +"." + formatName);
        String fileUrl;
        cloudinary.uploader().upload(multipartFile.getBytes(), params);
        if (isAvatar) {
            fileUrl = cloudinary.url().transformation(new Transformation()
                    .width(IMAGE_HEIGHT_AND_WIDTH).height(IMAGE_HEIGHT_AND_WIDTH)
                    .crop("fill")).generate(fullPath);
            path = path.replaceAll("https://res.cloudinary.com/" + CLOUD_NAME + "/image/upload/c_fill,h_36,w_36/v1/", "");
        } else {
            fileUrl = cloudinary.url().generate(fullPath);
            path = path.replaceAll(String.format("https://res.cloudinary.com/%s/image/upload/v1/", CLOUD_NAME), "");
        }
        cloudinary.api().deleteAllResources(ObjectUtils.asMap("public_id", path));
        cloudinary.api().deleteAllResources(ObjectUtils.emptyMap());
        return fileUrl;
    }

    public String uploadResponse(Principal principal, MultipartFile multipartFile) throws Exception {
        ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                () -> new UsernameNotFoundException(principal.getName()));
        boolean changePhoto = false;
        String imageFormat = multipartFile.getOriginalFilename().substring(
                multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        int getImageSize = (int) multipartFile.getSize();
        if ((getImageSize < IMAGE_MAX_SIZE) &&
                ((imageFormat.toLowerCase().indexOf(ImageFormat.jpg.toString()) > -1) ||
                        (imageFormat.toLowerCase().indexOf(ImageFormat.png.toString()) > -1))) {
            return extracted(multipartFile, "", false);
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
                    String.format("http://%s/login/change-password/%s", LOCALHOST_RESTORE, code));
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
