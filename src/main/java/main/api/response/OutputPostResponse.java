package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class OutputPostResponse {
    private int count;

    @JsonProperty("posts")
    private List<PostResponse> postList = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PostResponse> getPostList() {
        return postList;
    }

    public void addPostList(PostResponse postList) {
        this.postList.add(postList);
    }
}
//"count": 390,
//        "posts": [
//        {
//        "id": 345,
//        "timestamp": 1592338706,
//        "user":
//        {
//        "id": 88,
//        "name": "Дмитрий Петров"
//        },
//        "title": "Заголовок поста",
//        "announce": "Текст анонса поста без HTML-тэгов",
//        "likeCount": 36,
//        "dislikeCount": 3,
//        "commentCount": 15,
//        "viewCount": 55
//        },
//        {...}
//        ]
//{"count":4,
//        "posts":[
//                {
//                    "timestamp":0,
//        "title":"post_1",
//        "announce":"about new post",
//        "likeCount":0,
//        "dislikeCount":0,
//        "commentCount":0,
//        "viewCount":0,
//        "id":1,
//        "user":{
//                        "id":1,
//        "name":"Ruslan"}
//        },
//        {
//            "timestamp":0,
//        "title":"post_2",
//        "announce":"2 about new post",
//        "likeCount":0,
//        "dislikeCount":0,
//        "commentCount":0,
//        "viewCount":1,
//        "id":2,
//        "user":{
//                "id":2,
//        "name":"root"}
//        },{
//    "timestamp":0,
//        "title":"post_2",
//        "announce":"2 about new post",
//        "likeCount":0,
//        "dislikeCount":0,
//        "commentCount":0,
//        "viewCount":1,
//        "id":3,
//        "user":{
//        "id":3,
//        "name":"user"}
//        },{"timestamp":0,
//        "title":"post_2",
//        "announce":"2 about new post",
//        "likeCount":0,
//        "dislikeCount":0,
//        "commentCount":0,
//        "viewCount":1,
//        "id":4,
//        "user":{
//        "id":4,
//        "name":"user1"}
//        }]}