package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "post_comments")
public class PostComments
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "parent_id")
    private Integer parentId;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String text;

    public PostComments(Integer id, Integer parentId, Post post, User user,
                        LocalDateTime time, String text) {
        this.id = id;
        this.parentId = parentId;
        this.post = post;
        this.user = user;
        this.time = time;
        this.text = text;
    }

    public PostComments() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostComments)) return false;
        PostComments that = (PostComments) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getParentId(),
                that.getParentId()) && Objects.equals(getPost(), that.getPost()) &&
                Objects.equals(getUser(), that.getUser()) && Objects.equals(getTime(),
                that.getTime()) && Objects.equals(getText(), that.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getParentId(), getPost(), getUser(), getTime(), getText());
    }
}
