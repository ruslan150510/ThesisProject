package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.dto.PostDto;

import java.util.ArrayList;
import java.util.List;

public class OutputPostResponse {
    private int count;

    @JsonProperty("posts")
    private List<PostDto> postList = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PostDto> getPostList() {
        return postList;
    }

    public void setPostList(List<PostDto> postList) {
        this.postList = postList;
    }
}