package main.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.io.Serializable;

@Entity
@Table(name = "post_votes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostVotes
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    private LocalDateTime time;

    private Byte value;
}
