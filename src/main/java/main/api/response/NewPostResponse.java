package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewPostResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorsPostResponse errors;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
}
