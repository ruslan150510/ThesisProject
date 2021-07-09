package main.api.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorsResponse {
    private String email;

    private String name;

    public String password;

    public String captcha;
//
//    public static String getEMAIL() {
//        return EMAIL;
//    }
//
//    public static String getNAME() {
//        return NAME;
//    }
//
//    public static String getPASSWORD() {
//        return PASSWORD;
//    }
//
//    public static String getCAPTCHA() {
//        return CAPTCHA;
//    }
}
