package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.dto.CommentDto;
import main.dto.UserDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    @JsonProperty("id")
    private int postId;

    private long timestamp;

    @JsonProperty("active")
    private Byte isActive;

    @JsonProperty("user")
    private UserDto userDto;

    private String title;

    private String text;

    private int likeCount;

    private int dislikeCount;

    private int viewCount;

    @JsonProperty("comments")
    private List<CommentDto> commentDtoList = new ArrayList<>();

    private Set<String> tags = new HashSet<>();
}