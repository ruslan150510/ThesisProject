package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CaptchaCodes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(columnDefinition = "TINYTEXT", nullable = false)
    private String code;

    @Column(name = "secret_code", columnDefinition = "TINYTEXT", nullable = false)
    private String secretCode;

    public Integer getId() {
        return id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getCode() {
        return code;
    }

    public String getSecretCode() {
        return secretCode;
    }
}
