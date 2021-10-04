package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageUploadResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorsImageUpload errors;
}
