package main.service;

import main.MapperUtil;
import main.api.response.*;
import main.dto.CommentDto;
import main.dto.PostDto;
import main.dto.UserDto;
import main.dto.UserIdDto;
import main.model.*;
import main.model.repository.*;
import main.request.CommentRequest;
import main.request.LikeRequest;
import main.request.ModerationRequest;
import main.request.NewPostRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final static Integer STRING_START_INDEX = 0;
    private final static Integer STRING_LENGHT = 15; //ограничение по отображению текста, 150 знаков.
    //    для примера взяли ограничение 15 символов
    private final static String TITLE_ERROR = "Заголовок не установлен";
    private final static Integer MIN_TITLE_LENGTH = 3;
    private final static String TEXT_ERROR = "Текст публикации слишком короткий";
    private final static Integer MIN_TEXT_LENGTH = 50;
    private static final Integer ID_DEFAULT = -1;
    private final static String TEXT_COMMENT_ERROR = "Текст комментария не задан или слишком короткий";
    private final static String ID_COMMENT_ERROR = "комментарий и/или пост не существуют";
    private static final byte LIKE = 1;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostCommentsRepository commentsRepository;

    @Autowired
    private PostVotesRepository postVotesRepository;

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

    public PostResponse postResponseHasAuthority(Principal principal, int id) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                () -> new UsernameNotFoundException(principal.getName()));
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
        postResponse.setUserDto(convertToUserDto(user));
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

    public OutputPostResponse getModerationPosts(Principal principal,
                                                 Integer offset,
                                                 Integer limit,
                                                 String status) {
        OutputPostResponse outputPostResponse = new OutputPostResponse();
        int countPost = 0;
        List<Post> postList = null;
        int userId = userRepository.findByEmail(principal.getName()).get().getId();
        switch (status) {
            case "new":
                postList = postRepository.findNewPost();
                countPost = postRepository.findNewPost().size();
                break;
            case "accepted":
                postList = postRepository.findModerationPublishedPost(userId);
                countPost = postRepository.findModerationPublishedPost(userId).size();
                break;
            case "declined":
                postList = postRepository.findModerationDeclinedPost(userId);
                countPost = postRepository.findModerationDeclinedPost(userId).size();
                break;
        }
        outputPostResponse.setPostList(MapperUtil.convertList(postList, this::convertToPostDto));
        outputPostResponse.setCount(countPost);
        return outputPostResponse;
    }

    public NewPostResponse addNewPost(Principal principal, NewPostRequest newPostRequest) {
        NewPostResponse newPostResponse = new NewPostResponse();
        if ((newPostRequest.getTitle().length() >= MIN_TITLE_LENGTH)
                && (newPostRequest.getText().length() > MIN_TEXT_LENGTH)) {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                    () -> new UsernameNotFoundException(principal.getName()));
            long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            Post post = new Post();
            post.setUser(user);
            post.setModerationStatus(Status.NEW);
            post.setTitle(newPostRequest.getTitle());
            post.setText(newPostRequest.getText());
            post.setIsActive(newPostRequest.getActive());
            post.setTime(timestamp > newPostRequest.getTimestamp() ?
                    LocalDateTime.ofEpochSecond(newPostRequest.getTimestamp(), 100, ZoneOffset.UTC)
                    : LocalDateTime.ofEpochSecond(timestamp, 100, ZoneOffset.UTC));
            post.setViewCount(0);

            newPostRequest.getTags().forEach(tag ->
            {
                if (!tagRepository.findByTagName(tag).isPresent()) {
                    Tag tagNew = new Tag();
                    tagNew.setName(tag);
                    tagRepository.save(tagNew);
                }
                post.addTag(tagRepository.findByTagName(tag).get());
            });
            postRepository.save(post);
            newPostResponse.setResult(true);
        } else {
            ErrorsPostResponse errorsNewPostResponse = new ErrorsPostResponse();
            errorsNewPostResponse.setText(
                    newPostRequest.getText().length() < MIN_TEXT_LENGTH ? TEXT_ERROR : null);
            errorsNewPostResponse.setTitle(
                    newPostRequest.getTitle().length() < MIN_TITLE_LENGTH ? TITLE_ERROR : null);

            newPostResponse.setResult(false);
            newPostResponse.setErrors(errorsNewPostResponse);
        }
        return newPostResponse;
    }

    public NewPostResponse editNewPost(Principal principal, NewPostRequest newPostRequest, Integer id) {
        NewPostResponse newPostResponse = new NewPostResponse();
        if ((newPostRequest.getTitle().length() >= MIN_TITLE_LENGTH)
                && (newPostRequest.getText().length() > MIN_TEXT_LENGTH)) {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                    () -> new UsernameNotFoundException(principal.getName()));
            long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            Post post = postRepository.findById(id).get();
            if (postRepository.findById(id).isPresent()) {
                if (postRepository.findById(id).get().getUser().getId().equals(user.getId())) {
                    post.setUser(user);
                } else {
                    post.setModeratorId(user.getId());
                }
            }
            post.setModerationStatus(Status.NEW);
            post.setTitle(newPostRequest.getTitle());
            post.setText(newPostRequest.getText());
            post.setIsActive(newPostRequest.getActive());
            post.setTime(timestamp > newPostRequest.getTimestamp() ?
                    LocalDateTime.ofEpochSecond(newPostRequest.getTimestamp(), 100, ZoneOffset.UTC)
                    : LocalDateTime.ofEpochSecond(timestamp, 100, ZoneOffset.UTC));
            post.setViewCount(0);

            newPostRequest.getTags().forEach(tag ->
            {
                if (!tagRepository.findByTagName(tag).isPresent()) {
                    Tag tagNew = new Tag();
                    tagNew.setName(tag);
                    tagRepository.save(tagNew);
                }
                post.addTag(tagRepository.findByTagName(tag).get());
            });
            postRepository.save(post);
            newPostResponse.setResult(true);
        } else {
            ErrorsPostResponse errorsNewPostResponse = new ErrorsPostResponse();
            errorsNewPostResponse.setText(
                    newPostRequest.getText().length() < MIN_TEXT_LENGTH ? TEXT_ERROR : null);
            errorsNewPostResponse.setTitle(
                    newPostRequest.getTitle().length() < MIN_TITLE_LENGTH ? TITLE_ERROR : null);

            newPostResponse.setResult(false);
            newPostResponse.setErrors(errorsNewPostResponse);
        }
        return newPostResponse;
    }

    public NewPostResponse addComment(Principal principal, CommentRequest commentRequest) {
        NewPostResponse newPostResponse = new NewPostResponse();
        long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        if (commentRequest.getText().length() >= MIN_TEXT_LENGTH) {
            PostComments comments = new PostComments();
            User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                    () -> new UsernameNotFoundException(principal.getName()));
            if (commentRequest.getParentId() != null) {
                comments.setParentId(commentRequest.getParentId());
            }
            comments.setText(commentRequest.getText());
            comments.setPost(postRepository.findByIdAccepted(commentRequest.getPostId()).get());
            comments.setUser(user);
            comments.setTime(LocalDateTime.ofEpochSecond(timestamp, 100, ZoneOffset.UTC));

            newPostResponse.setId(commentsRepository.save(comments).getId());
            newPostResponse.setResult(true);
        } else {
            ErrorsPostResponse errorsPostResponse = new ErrorsPostResponse();
            errorsPostResponse.setText(TEXT_COMMENT_ERROR);
            newPostResponse.setResult(false);
            newPostResponse.setErrors(errorsPostResponse);
        }
        return newPostResponse;
    }

    public Response moderationPost(Principal principal, ModerationRequest moderationRequest) {
        Response response = new Response();
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                () -> new UsernameNotFoundException(principal.getName()));
        if (user.getIsModerator().equals(1)) {
            Post post = postRepository.findByIdAccepted(moderationRequest.getPostId()).get();
            post.setModeratorId(user.getId());
            post.setModerationStatus(moderationRequest.getDecision().equals(Decision.decline)
                    ? Status.DECLINED : Status.ACCEPTED);
            postRepository.save(post);
            response.setResult(true);
        }
        return response;
    }

    public Response likePost(Principal principal, LikeRequest likeRequest, Byte like) {
        Response response = new Response();
        try {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                    () -> new UsernameNotFoundException(principal.getName()));
            Optional<PostVotes> postVotes = postVotesRepository.
                    findByUserIdAndPostId(user.getId(), likeRequest.getPostId());
            if (!postVotes.isPresent()) {
                postVotes.get().setValue(like);
                postVotesRepository.save(postVotes.get());
                response.setResult(true);
            } else {
                if ((postVotes.get().getValue() != LIKE) && (like == LIKE)) {
                    postVotes.get().setValue(like);
                    postVotesRepository.save(postVotes.get());
                    response.setResult(true);
                } else {
                    response.setResult(false);
                }
            }
        } catch (Exception e) {
            response.setResult(false);
        }
        return response;
    }


}
