package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Decision;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModerationRequest {
    @JsonProperty(value = "post_id")
    private Integer postId;

    private Decision decision;
}
