package main.service;

import main.api.response.TagsListResponse;
import main.api.response.TagsResponse;
import main.model.PostRepository;
import main.model.Tag;
import main.model.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    public TagsResponse getTags() {
        TagsResponse tagsResponse = new TagsResponse();
        int countPost = (int) postRepository.count();
        double dWeightMax = 0;
        Iterable<Tag> tagIterable = tagRepository.findAll();
        for (Tag tag : tagIterable) {
            TagsListResponse tagsListResponse = new TagsListResponse();
            tagsListResponse.setName(tag.getName());
            if (dWeightMax < (double) tag.getPostList().size() / countPost) {
                dWeightMax = (double) tag.getPostList().size() / countPost;
            }
            tagsListResponse.setWeight((double) tag.getPostList().size() / countPost);
            tagsResponse.addTags(tagsListResponse);
        }
        double normalizationСoefficient = 1 / dWeightMax;
        tagsResponse.getTags().forEach(x -> {
            x.setWeight(normalizationСoefficient * x.getWeight());
        });
        return tagsResponse;
    }
}
