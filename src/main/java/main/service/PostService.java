package main.service;

import liquibase.pro.packaged.x;
import main.api.response.Mode;
import main.api.response.OutputPostResponse;
import main.api.response.PostResponse;
import main.api.response.UserResponse;
import main.model.*;
import org.apache.logging.log4j.util.PropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    public final static Integer STRING_START_INDEX = 0;
    public final static Integer STRING_LENGHT = 150;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public OutputPostResponse getPosts(int offset, int limit, Mode mode) {
        OutputPostResponse outputPostResponse = new OutputPostResponse();
        int count = 0;
        List<Post> postList = new ArrayList<>();
//        postRepository.findAll().forEach(
//        post -> {if ((post.getIsActive() == 1)&&(post.getModerationStatus().equals(Status.ACCEPTED)))
//        postList.add(post);});
        switch (mode) {
            case recent:
                postRepository.findAllByModerationStatusAndSortDesc().
                        forEach(post -> {postList.add(post);});
                break;
            case early:
                postRepository.findAllByModerationStatusAndSortAbs().
                        forEach(post -> {postList.add(post);});
                break;
            case best:
                postRepository.findAllByAccepted().
                        forEach(post -> {postList.add(post);});
                postList.sort(Comparator.comparing(Post::getCountVotes).reversed());
                break;
            case popular:
                postRepository.findAllByAccepted().
                        forEach(post -> {postList.add(post);});
                postList.sort(Comparator.comparing(Post::getCountComment).reversed());
                break;
        }
        int countPost = postList.size();
        outputPostResponse.setCount(countPost);
        for (int i = offset; i < countPost; i++)
        {
            PostResponse postResponse = new PostResponse();
            UserResponse userResponse = new UserResponse();

            if (limit == count){
                break;
            }
            postResponse.setPostId(postList.get(i).getId());
            User user = postList.get(i).getUser();
            userResponse.setUserId(user.getId());
            userResponse.setUserName(user.getName());
            postResponse.setUserResponse(userResponse);
            postResponse.setTitle(postList.get(i).getTitle());
            if (postList.get(i).getText().length() > STRING_LENGHT) {
                postResponse.setAnnounce(postList.get(i).getText().
                        substring(STRING_START_INDEX, STRING_LENGHT));
            } else {
                postResponse.setAnnounce(postList.get(i).getText());
            }
            postResponse.setLikeCount((int) postList.get(i).getPostVotesList().stream()
                    .filter(x -> x.getValue() == 1).count());
            postResponse.setDislikeCount((int) postList.get(i).getPostVotesList().stream()
                    .filter(x -> x.getValue() == -1).count());
            postResponse.setCommentCount(postList.get(i).getCommentList().size());
            postResponse.setViewCount(postList.get(i).getViewCount());
            outputPostResponse.addPostList(postResponse);
            count++;
        }
        return outputPostResponse;
    }
}
