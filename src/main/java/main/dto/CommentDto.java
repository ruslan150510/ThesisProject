package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @JsonProperty("id")
    private Integer commentId;

    private long timestamp;

    private String text;

    @JsonProperty("user")
    private UserIdDto userIdDto;
}
