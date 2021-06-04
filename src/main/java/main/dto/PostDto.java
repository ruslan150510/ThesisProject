package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    @JsonProperty("id")
    private int postId;

    private long timestamp;

    @JsonProperty("user")
    private UserDto userDto;

    private String title;

    @JsonProperty("announce")
    private String text;

    private int likeCount;

    private int dislikeCount;

    private int commentCount;

    private int viewCount;
}
