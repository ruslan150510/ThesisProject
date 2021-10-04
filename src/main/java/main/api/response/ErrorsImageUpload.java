package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ErrorsImageUpload {
    private String image;

    public ErrorsImageUpload() {
        this.image = "Размер файла превышает допустимый размер";
    }
}
