package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.io.Serializable;

@Entity
@Table(name = "posts_votes")
public class PostsVotes
{
    @EmbeddedId
    private Key id;

    private LocalDateTime time;

    private Byte value;

    @Embeddable
    public class Key implements Serializable {
        @ManyToOne
        private User user;

        @ManyToOne
        private Post post;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key key = (Key) o;
            return Objects.equals(getUser(), key.getUser()) && Objects.equals(getPost(), key.getPost());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getUser(), getPost());
        }
    }

    public PostsVotes(Key id, LocalDateTime time, Byte value) {
        this.id = id;
        this.time = time;
        this.value = value;
    }

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(Byte value) {
        this.value = value;
    }
}
