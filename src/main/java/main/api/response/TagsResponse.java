package main.api.response;

import java.util.ArrayList;
import java.util.List;

public class TagsResponse {
    private List<TagsListResponse> tags = new ArrayList<>();

    public List<TagsListResponse> getTags() {
        return tags;
    }

    public void addTags(TagsListResponse tag) {
        this.tags.add(tag);
    }
//    {
//"tags":
//[
//{"name":"Java", "weight":1},
//{"name":"Spring", "weight":0.56},
//{"name":"Hibernate", "weight":0.22},
//{"name":"Hadoop", "weight":0.17},
//]
//}
}
