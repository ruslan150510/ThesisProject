package main.controller;

import main.api.response.*;
import main.model.ModerationStatus;
import main.service.CalendarService;
import main.service.PostService;
import main.service.TagService;
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
    private String requestText = "";
    private String tagFromPost = "";

    public ApiPostController(PostService postService, TagService tagService,
                             CalendarService calendarService) {
        this.postService = postService;
        this.tagService = tagService;
        this.calendarService = calendarService;
    }

    @GetMapping("/post")
//    @PreAuthorize("hasAuthority('user:write')")
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
//    @PreAuthorize("hasAuthority('user:moderate')")
    @ResponseBody
    public ResponseEntity<OutputPostResponse> getOutputPostSearchResponse
            (@RequestParam(defaultValue = "0") Integer offset,
             @RequestParam(defaultValue = "10") Integer limit,
             @RequestParam String query) {
        return ResponseEntity.ok(postService.getPosts(offset, limit, Mode.recent, query, DATE_TIME, tagFromPost));
    }

    @GetMapping("/calendar")
    @ResponseBody
    public ResponseEntity<CalendarResponse> getCalendarResponse(@RequestParam Integer year) {
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
        if (postService.postResponse(id).equals(null)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postService.postResponse(id));
    }

    @GetMapping("/post/my")//требуется авторизация
    @ResponseBody
    public ResponseEntity<OutputPostResponse> getMyPosts(Principal principal,
               @RequestParam(defaultValue = "0") Integer offset,
               @RequestParam(defaultValue = "10") Integer limit,
               @RequestParam(defaultValue = "inactive") ModerationStatus status) {
        return ResponseEntity.ok(postService.getMyPosts(principal, offset, limit, status));
    }
}
