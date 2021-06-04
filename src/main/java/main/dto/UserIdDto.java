package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserIdDto {
    @JsonProperty("id")
    private Integer userId;

    @JsonProperty("name")
    private String userName;

    private String photo;
}
