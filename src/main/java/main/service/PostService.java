package main.service;

import main.MapperUtil;
import main.api.response.Mode;
import main.api.response.OutputPostResponse;
import main.api.response.PostResponse;
import main.dto.*;
import main.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    public final static Integer STRING_START_INDEX = 0;
    public final static Integer STRING_LENGHT = 15; //ограничение по отображению текста, 150 знаков.
//    для примера взяли ограничение 15 символов

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper modelMapper;

    public OutputPostResponse getPosts(int offset, int limit, Mode mode, String query,
                                       String dateTime, String tag) {
        OutputPostResponse outputPostResponse = new OutputPostResponse();
        int countPost = 0;
        List<Post> postList;
        String queryRes = query.replaceAll("\\s", "");
        switch (mode) {
            case recent:
                if (queryRes.length() != 0) {
                    postList = postRepository.findAllByQuery(offset, limit, query);
                    countPost = postRepository.findAllByQueryCount(query);
                } else {
                    if (!dateTime.toString().equals("0001-01-01")) {
                        postList = postRepository.findAllByDate(offset, limit, dateTime);
                        countPost = postRepository.findAllByDateCount(dateTime);
                    } else {
                        if (tag.length() != 0) {
                            postList = postRepository.findAllByTag(offset, limit, tag);
                            countPost = postRepository.findAllByTagCount(tag);
                        } else {
                            postList = postRepository.findAllByModerationStatusAndSortDesc(offset, limit);
                            countPost = postRepository.postsIsActive();
                        }
                    }
                }
                outputPostResponse.setPostList(MapperUtil.convertList(postList, this::convertToPostDto));
                break;
            case early:
                postList = postRepository.findAllByModerationStatusAndSortAbs(offset, limit);
                outputPostResponse.setPostList(MapperUtil.convertList(postList, this::convertToPostDto));
                countPost = postRepository.postsIsActive();
                break;
            case best:
                postList = postRepository.findAllByBest(offset, limit);
                outputPostResponse.setPostList(MapperUtil.convertList(postList, this::convertToPostDto));
                countPost = postRepository.postsIsActive();
                break;
            case popular:
                postList = postRepository.findAllByPopular(offset, limit);
                outputPostResponse.setPostList(MapperUtil.convertList(postList, this::convertToPostDto));
                countPost = postRepository.postsIsActive();
                break;
        }
        outputPostResponse.setCount(countPost);
        return outputPostResponse;
    }

    public PostResponse postResponse(int id) {
        PostResponse postResponse = new PostResponse();
        Optional<Post> postOptional = postRepository.findById(id);
        postResponse.setPostId(id);
        postResponse.setTitle(postOptional.get().getTitle());
        postResponse.setText(postOptional.get().getText());
        postResponse.setIsActive(postOptional.get().getIsActive());
        postResponse.setTimestamp(postOptional.get().getTime().toEpochSecond(ZoneOffset.UTC));
        postResponse.setLikeCount((int) postOptional.get().getPostVotesList().stream()
                .filter(x -> x.getValue() == 1).count());
        postResponse.setDislikeCount((int) postOptional.get().getPostVotesList().stream()
                .filter(x -> x.getValue() == -1).count());
        postResponse.setUserDto(convertToUserDto(postOptional.get().getUser()));
        postResponse.setCommentDtoList(MapperUtil.convertList(postOptional.get().
                getCommentList(), this::convertToCommentDto));
        postOptional.get().getTagList().forEach((tag)->{postResponse.getTags().add(tag.getName());});
        postRepository.iterableViewCount(id);
        return postResponse;
    }

    private PostDto convertToPostDto(Post post) {
        if (post.getText().length() > STRING_LENGHT) {
            post.setText(post.getText().substring(STRING_START_INDEX, STRING_LENGHT));
        }
        PostDto postDto = modelMapper.map(post, PostDto.class);
        postDto.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.UTC));
        postDto.setLikeCount((int) post.getPostVotesList().stream()
                .filter(x -> x.getValue() == 1).count());
        postDto.setDislikeCount((int) post.getPostVotesList().stream()
                .filter(x -> x.getValue() == -1).count());
        postDto.setUserDto(convertToUserDto(post.getUser()));
        return postDto;
    }

    private UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private UserIdDto convertToUserIdDto(User user) {
        return modelMapper.map(user, UserIdDto.class);
    }

    private CommentDto convertToCommentDto(PostComments postComments) {
        CommentDto commentDto = modelMapper.map(postComments, CommentDto.class);
        commentDto.setTimestamp(postComments.getTime().toEpochSecond(ZoneOffset.UTC));
        commentDto.setUserIdDto(convertToUserIdDto(postComments.getUser()));
        return commentDto;
    }
}
