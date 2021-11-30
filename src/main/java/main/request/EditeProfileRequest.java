package main.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditeProfileRequest {
    @JsonProperty(value = "photo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String multipartFile;

    @JsonProperty(value = "name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    @JsonProperty(value = "email")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String email;

    @JsonProperty(value = "password")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String password;

    @JsonProperty(value = "removePhoto")
    int removePhoto;
}
