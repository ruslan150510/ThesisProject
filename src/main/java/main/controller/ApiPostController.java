package main.controller;

import main.api.response.*;
import main.model.ModerationStatus;
import main.request.CommentRequest;
import main.request.LikeRequest;
import main.request.ModerationRequest;
import main.request.NewPostRequest;
import main.service.CalendarService;
import main.service.PostService;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private final PostService postService;
    private final TagService tagService;
    private final CalendarService calendarService;
    private static final String DATE_TIME = "0001-01-01";
    private static final Integer ID_DEFAULT = -1;
    private static final byte LIKE = 1;
    private static final byte DISLIKE = -1;
    private String requestText = "";
    private String tagFromPost = "";

    public ApiPostController(PostService postService, TagService tagService,
                             CalendarService calendarService) {
        this.postService = postService;
        this.tagService = tagService;
        this.calendarService = calendarService;
    }

    @GetMapping("/post")
    @ResponseBody
    public ResponseEntity<OutputPostResponse> getOutputPostResponse(@RequestParam(defaultValue = "0") Integer offset,
                                                                    @RequestParam(defaultValue = "10") Integer limit,
                                                                    @RequestParam(defaultValue = "recent") Mode mode) {

        return ResponseEntity.ok(postService.getPosts(offset, limit, mode, requestText, DATE_TIME, tagFromPost));
    }

    @GetMapping("/tag")
    public ResponseEntity<TagsResponse> getTagsResponse() {
        return ResponseEntity.ok(tagService.getTags());
    }

    @GetMapping("/post/search")
    @ResponseBody
    public ResponseEntity<OutputPostResponse> getOutputPostSearchResponse
            (@RequestParam(defaultValue = "0") Integer offset,
             @RequestParam(defaultValue = "10") Integer limit,
             @RequestParam String query) {
        return ResponseEntity.ok(postService.getPosts(offset, limit, Mode.recent, query, DATE_TIME, tagFromPost));
    }

    @GetMapping("/calendar")
    @ResponseBody
    public ResponseEntity<CalendarResponse> getCalendarResponse(
            @RequestParam(defaultValue = "2021") Integer year) {
        return ResponseEntity.ok(calendarService.getCalendarResponse(year));
    }

    @GetMapping("/post/byDate")
    @ResponseBody
    public ResponseEntity<OutputPostResponse> getOutputPostByDateResponse
            (@RequestParam(defaultValue = "0") Integer offset,
             @RequestParam(defaultValue = "10") Integer limit,
             @RequestParam String date) {
        return ResponseEntity.ok(postService.getPosts(offset, limit, Mode.recent, requestText, date, tagFromPost));
    }

    @GetMapping("/post/byTag")
    @ResponseBody
    public ResponseEntity<OutputPostResponse> getOutputPostByTagResponse
            (@RequestParam(defaultValue = "0") Integer offset,
             @RequestParam(defaultValue = "10") Integer limit,
             @RequestParam String tag) {
        return ResponseEntity.ok(postService.getPosts(offset, limit, Mode.recent, requestText, DATE_TIME, tag));
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostResponse> getPostResponse(@PathVariable int id) {
        if (postService.postResponse(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postService.postResponse(id));
    }

    @GetMapping("/post/my")
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseBody
    public ResponseEntity<OutputPostResponse> getMyPosts(Principal principal,
               @RequestParam(defaultValue = "0") Integer offset,
               @RequestParam(defaultValue = "10") Integer limit,
               @RequestParam(defaultValue = "inactive") ModerationStatus status) {
        return ResponseEntity.ok(postService.getMyPosts(principal, offset, limit, status));
    }

    @GetMapping("/post/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    @ResponseBody
    public ResponseEntity<OutputPostResponse> getModerationPosts(Principal principal,
                                                         @RequestParam(defaultValue = "0") Integer offset,
                                                         @RequestParam(defaultValue = "10") Integer limit,
                                                         @RequestParam(defaultValue = "new") String status) {
            return ResponseEntity.ok(postService.getModerationPosts(principal, offset, limit, status));
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseBody
    public ResponseEntity<NewPostResponse> createNewPost(Principal principal,
                                                         @RequestBody NewPostRequest newPostRequest){
        return ResponseEntity.ok(postService.addNewPost(principal, newPostRequest, ID_DEFAULT));
    }

    @PutMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<NewPostResponse> putPost(Principal principal,
                                                @RequestBody NewPostRequest newPostRequest,
                                                @PathVariable int id) {
        return ResponseEntity.ok(postService.addNewPost(principal, newPostRequest, id));
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<NewPostResponse> addCommentPost(Principal principal,
                                                          @RequestBody CommentRequest commentRequest){
        NewPostResponse newPostResponse = postService.addComment(principal, commentRequest);
        return new ResponseEntity<>(newPostResponse, newPostResponse.getId() == null
                        ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<Response> moderationPost(Principal principal,
                                                   @RequestBody ModerationRequest moderationRequest){
        return ResponseEntity.ok(postService.moderationPost(principal, moderationRequest));
    }

    @PostMapping("/post/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> likePost(Principal principal, @RequestBody LikeRequest likeRequest)
    {
        return ResponseEntity.ok(postService.likePost(principal, likeRequest, LIKE));
    }

    @PostMapping("/post/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> dislikePost(Principal principal, @RequestBody LikeRequest likeRequest)
    {
        return ResponseEntity.ok(postService.likePost(principal, likeRequest, DISLIKE));
    }
}
