package main.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@ConfigurationProperties(prefix = "production")
@Data
public class CloudinaryConfig {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
    private Boolean secure;

    @Bean
    public Cloudinary getConnect() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName, "api_key", apiKey,
                "api_secret", apiSecret, "secure", secure));
    }
}
