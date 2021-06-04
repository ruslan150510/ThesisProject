package main.api.response;

public class CaptchaResponse {
    private String secret;
    private String image;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
//"secret": "car4y8cryaw84cr89awnrc",
//"image": "data:image/png;base64, код_изображения_в_base64"