package main.controller;

import main.api.response.*;
import main.service.CalendarService;
import main.service.PostService;
import main.service.TagService;
import org.springframework.web.bind.annotation.*;

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
    @ResponseBody
    public OutputPostResponse getOutputPostResponse(@RequestParam Integer offset,
                                                    @RequestParam Integer limit,
                                                    @RequestParam Mode mode) {
        return postService.getPosts(offset, limit, mode, requestText, DATE_TIME, tagFromPost);
    }

    @GetMapping("/tag")
    public TagsResponse getTagsResponse() {
        return tagService.getTags();
    }

    @GetMapping("/post/search")
    @ResponseBody
    public OutputPostResponse getOutputPostSearchResponse(@RequestParam Integer offset,
                                                          @RequestParam Integer limit,
                                                          @RequestParam String query) {
        return postService.getPosts(offset, limit, Mode.recent, query, DATE_TIME, tagFromPost);
    }

    @GetMapping("/calendar")
    @ResponseBody
    public CalendarResponse getCalendarResponse(@RequestParam Integer year) {
        return calendarService.getCalendarResponse(year);
    }

    @GetMapping("/post/byDate")
    @ResponseBody
    public OutputPostResponse getOutputPostByDateResponse(@RequestParam Integer offset,
                                                          @RequestParam Integer limit,
                                                          @RequestParam String date) {
        return postService.getPosts(offset, limit, Mode.recent, requestText, date, tagFromPost);
    }

    @GetMapping("/post/byTag")
    @ResponseBody
    public OutputPostResponse getOutputPostByTagResponse(@RequestParam int offset,
                                                         @RequestParam int limit,
                                                         @RequestParam String tag) {
        return postService.getPosts(offset, limit, Mode.recent, requestText, DATE_TIME, tag);
    }

    @GetMapping("/post/{id}")
    public PostResponse getPostResponse(@PathVariable int id) {
        return postService.postResponse(id);
    }
}
