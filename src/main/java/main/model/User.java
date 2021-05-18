package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name ="users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "is_moderator", nullable = false)
    private Byte isModerator;

    @Column(name = "reg_time", nullable = false)
    private LocalDateTime regTime;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String code;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @OneToMany(cascade = CascadeType.ALL, mappedBy  =  "user")
    private List<Post> postList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<PostComments> commentList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<PostVotes> postVotes  = new ArrayList<>();

    public User() {}

    public User(Integer id, Byte isModerator, LocalDateTime regTime, String name,
                String email, String password, String code, String photo) {
        this.id = id;
        this.isModerator = isModerator;
        this.regTime = regTime;
        this.name = name;
        this.email = email;
        this.password = password;
        this.code = code;
        this.photo = photo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Byte getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(Byte isModerator) {
        this.isModerator = isModerator;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void addPost(Post post) {
        postList.add(post);
        post.setUser(this);
    }

    public void removePost(Post post) {
        post.setUser(null);
        this.postList.remove(post);
    }

    public List<PostComments> getCommentList() {
        return commentList;
    }

    public void addCommentsList(PostComments postComments) {
        commentList.add(postComments);
    }

    public void removeCommentsList(PostComments postComments) {
        commentList.remove(postComments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getIsModerator(),
                user.getIsModerator()) && Objects.equals(getRegTime(), user.getRegTime())
                && Objects.equals(getName(), user.getName()) && Objects.equals(getEmail(),
                user.getEmail()) && Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getCode(), user.getCode()) && Objects.equals(getPhoto(),
                user.getPhoto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIsModerator(), getRegTime(), getName(), getEmail(),
                getPassword(), getCode(), getPhoto());
    }
}
