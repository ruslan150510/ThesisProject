package main.controller;

import main.api.response.Mode;
import main.api.response.OutputPostResponse;
import main.api.response.TagsResponse;
import main.service.PostService;
import main.service.TagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private final PostService postService;
    private final TagService tagService;

    public ApiPostController(PostService postService, TagService tagService) {
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping("/post")
    @ResponseBody
    public OutputPostResponse getOutputPostResponse(@RequestParam Integer offset,
                                                    @RequestParam Integer limit,
                                                    @RequestParam Mode mode) {
        return postService.getPosts(offset, limit, mode);
    }

    @GetMapping("/tag")
    public TagsResponse getTagsResponse() {
        return tagService.getTags();
    }
}
