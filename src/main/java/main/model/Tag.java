package main.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Tags")
public class Tag
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String value;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @JoinTable(name = "Tags2Post",
            joinColumns = { @JoinColumn(name = "tags_id") },
            inverseJoinColumns = { @JoinColumn(name = "posts_id") }
    )
    private List<Post> postList = new ArrayList<>();

    public Tag(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Tag() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(getId(), tag.getId()) && Objects.equals(getValue(),
                tag.getValue()) && Objects.equals(getPostList(), tag.getPostList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getValue(), getPostList());
    }
}
