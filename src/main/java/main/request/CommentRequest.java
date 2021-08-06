package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    @JsonProperty(value = "parent_id")
    private Integer parentId;
    @JsonProperty(value = "post_id")
    private Integer postId;
    private String text;
}
