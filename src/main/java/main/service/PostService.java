package main.service;

import main.MapperUtil;
import main.api.response.Mode;
import main.api.response.OutputPostResponse;
import main.api.response.PostResponse;
import main.dto.CommentDto;
import main.dto.PostDto;
import main.dto.UserDto;
import main.dto.UserIdDto;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostComments;
import main.model.User;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.ZoneOffset;
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

    @Autowired
    private UserRepository userRepository;

    public OutputPostResponse getPosts(int offset, int limit, Mode mode, String query,
                                       String dateTime, String tag) {
        OutputPostResponse outputPostResponse = new OutputPostResponse();
        int countPost = 0;
        int page = offset == 0 ? 0 : offset / limit;
        List<Post> postList;
        String queryRes = query.replaceAll("\\s", "");
        switch (mode) {
            case recent:
                if (queryRes.length() != 0) {
                    postList = postRepository.findAllByQuery(offset, limit, query);
                    countPost = postRepository.findAllByQuery(offset, limit, query).size();
                } else {
                    if (!dateTime.toString().equals("0001-01-01")) {
                        postList = postRepository.findAllByDate(offset, limit, dateTime);
                        countPost = postRepository.findAllByDate(offset, limit, dateTime).size();
                    } else {
                        if (tag.length() != 0) {
                            postList = postRepository.findAllByTag(offset, limit, tag);
                            countPost = postRepository.findAllByTag(offset, limit, tag).size();
                        } else {
                            countPost = postRepository.postsIsActive().orElse(0);
                            page = offset == 0 ? 0 : offset / limit;
                            Pageable pageableRecent = PageRequest.of(page, limit, Sort.by("time").descending());
                            postList = postRepository.findAllByModerationStatusAndSort(offset, pageableRecent)
                                    .toList();
                        }
                    }
                }
                outputPostResponse.setPostList(MapperUtil.convertList(postList, this::convertToPostDto));
                break;
            case early:
                countPost = postRepository.postsIsActive().orElse(0);
                Pageable pageableEarly = PageRequest.of(page, limit, Sort.by("time").ascending());
                postList = postRepository.findAllByModerationStatusAndSort(offset, pageableEarly)
                        .toList();
                outputPostResponse.setPostList(MapperUtil.convertList(postList, this::convertToPostDto));
                break;
            case best:
                countPost = postRepository.postsIsActive().orElse(0);
                postList = postRepository.findAllByBest(offset, limit);
                outputPostResponse.setPostList(MapperUtil.convertList(postList, this::convertToPostDto));
                break;
            case popular:
                countPost = postRepository.postsIsActive().orElse(0);
                postList = postRepository.findAllByPopular(offset, limit);
                outputPostResponse.setPostList(MapperUtil.convertList(postList, this::convertToPostDto));
                break;
        }
        outputPostResponse.setCount(countPost);
        return outputPostResponse;
    }

    public PostResponse postResponse(int id) {
        PostResponse postResponse = new PostResponse();
        Optional<Post> postOptional = postRepository.findById(id);
        if (!postOptional.isPresent()) {
            return null;
        }
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
        postOptional.get().getTagList().forEach((tag) -> {
            postResponse.getTags().add(tag.getName());
        });
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

    public OutputPostResponse getMyPosts(Principal principal,
                                         Integer offset,
                                         Integer limit,
                                         ModerationStatus status) {
        OutputPostResponse outputPostResponse = new OutputPostResponse();
        int countPost = 0;
        List<Post> postList = null;
        int userId = userRepository.findByEmail(principal.getName()).get().getId();
        System.out.println(userId);
        switch (status) {
            case inactive:
                postList = postRepository.findMyInactivePost(userId);
                countPost = postRepository.findMyInactivePost(userId).size();
                break;
            case declined:
                postList = postRepository.findMyDeclinedPost(userId);
                countPost = postRepository.findMyDeclinedPost(userId).size();
                break;
            case published:
                postList = postRepository.findMyPublishedPost(userId);
                countPost = postRepository.findMyPublishedPost(userId).size();
                break;
            case pending:
                postList = postRepository.findMyPendingPost(userId);
                countPost = postRepository.findMyPendingPost(userId).size();
                break;
        }
        outputPostResponse.setPostList(MapperUtil.convertList(postList, this::convertToPostDto));
        outputPostResponse.setCount(countPost);
        return outputPostResponse;
    }
}
