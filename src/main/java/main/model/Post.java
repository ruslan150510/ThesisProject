package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "posts")
public class Post{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "is_active", nullable = false)
    private Byte isActive;

    @Column(name = "moderation_status", columnDefinition = "enum")
    @Enumerated(EnumType.STRING)
    private Status moderationStatus;

    @Column(name = "moderator_id")
    private Integer moderatorId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @ManyToMany
    @JoinTable(name = "Tag2Post",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private List<Tag> tagList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy  =  "post")
    private List<PostComments> commentList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<PostVotes> postVotesList = new ArrayList<>();

    public Post(Integer id, Byte isActive, Status moderationStatus, Integer moderatorId,
                User user, LocalDateTime time, String title, String text, Integer viewCount) {
        this.id = id;
        this.isActive = isActive;
        this.moderationStatus = moderationStatus;
        this.moderatorId = moderatorId;
        this.user = user;
        this.time = time;
        this.title = title;
        this.text = text;
        this.viewCount = viewCount;
    }

    public Post() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Byte getIsActive() {
        return isActive;
    }

    public void setIsActive(Byte isActive) {
        this.isActive = isActive;
    }

    public Status getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(Status moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public Integer getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(Integer moderatorId) {
        this.moderatorId = moderatorId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void addTag(Tag tag) {
        tagList.add(tag);
        tag.getPostList().add(this);
    }

    public void removeTag(Tag tag) {
        tagList.remove(tag);
        tag.getPostList().remove(this);
    }

    public List<PostComments> getCommentList() {
        return commentList;
    }

    public void addComment(PostComments postComments) {
        commentList.add(postComments);
    }

    public void removeComment(PostComments postComments) {
        commentList.remove(postComments);
    }

    public Integer getCountComment(){
        return commentList.size();
    }

    public List<PostVotes> getPostVotesList() {
        return postVotesList;
    }

    public void addVotes(PostVotes postVotes) {
        postVotesList.add(postVotes);
    }

    public void removeVotes(PostVotes postVotes) {
        postVotesList.remove(postVotes);
    }

    public Integer getCountVotes(){
        return postVotesList.size();
    }

    public Integer getPostVotes(){
        return postVotesList.stream().mapToInt(PostVotes::getValue).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return Objects.equals(getId(), post.getId()) && Objects.equals(getIsActive(),
                post.getIsActive()) && getModerationStatus() == post.getModerationStatus() &&
                Objects.equals(getModeratorId(), post.getModeratorId()) && Objects.equals(getUser(),
                post.getUser()) && Objects.equals(getTime(), post.getTime()) &&
                Objects.equals(getTitle(), post.getTitle()) && Objects.equals(getText(),
                post.getText()) && Objects.equals(getViewCount(), post.getViewCount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIsActive(), getModerationStatus(), getModeratorId(),
                getUser(), getTime(), getTitle(), getText(), getViewCount());
    }
}
