package main.service;

import main.api.response.SettingsResponse;
import main.api.response.StatisticsResponse;
import main.model.GlobalSettings;
import main.model.Post;
import main.model.User;
import main.model.repository.GlobalSettingsRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Optional;

@Service
public class SettingsService {
    public static final String STATUS = "YES";
    public static final int LIKE = 1;
    public static final int DISLIKE = -1;

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        Iterable<GlobalSettings> globalSettingsIterable = globalSettingsRepository.findAll();
        for (GlobalSettings settings : globalSettingsIterable) {
            switch (settings.getCode()) {
                case "MULTIUSER_MODE":
                    settingsResponse.setMultiuserMode(settings.getValue().equals(STATUS));
                    break;
                case "POST_PREMODERATION":
                    settingsResponse.setPostPremoderation(settings.getValue().equals(STATUS));
                    break;
                case "STATISTICS_IS_PUBLIC":
                    settingsResponse.setStatisticIsPublic(settings.getValue().equals(STATUS));
                    break;
            }
        }
        return settingsResponse;
    }

    public StatisticsResponse getMyStatistics(Principal principal)
    {
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        try {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                    () -> new UsernameNotFoundException(principal.getName()));
            Optional<Post> postOptional = postRepository.findById(user.getId());
            if (!postOptional.isPresent()) {
                return null;
            }
            statisticsResponse.setDislikesCount((int) postOptional.get().getPostVotesList().stream()
                    .filter(x -> x.getValue() == DISLIKE).count());
            statisticsResponse.setViewsCount(postOptional.get().getViewCount());
            statisticsResponse.setPostsCount((int) postOptional.stream().count());
            statisticsResponse.setLikesCount((int) postOptional.get().getPostVotesList().stream()
                    .filter(x -> x.getValue() == LIKE).count());
            statisticsResponse.setFirstPublication(postOptional.get().getTime().toEpochSecond(ZoneOffset.UTC));
            return statisticsResponse;
        }
        catch (Exception exception)
        {
            return null;
        }
    }

    public StatisticsResponse allStatistics(){
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        Iterable<Post> postIterrator = postRepository.findAll();
        int like = 0;
        int disLike =0;
        int viewCount = 0;
        int postCount = 0;
        long date = 0;
        for (Post post: postIterrator) {
            disLike = disLike + (int) post.getPostVotesList().stream().filter(x -> x.getValue() == DISLIKE).count();
            like = like + (int) post.getPostVotesList().stream().filter(x -> x.getValue() == LIKE).count();
            viewCount = viewCount + post.getViewCount();
            if (postCount == 0) {
                date = post.getTime().toEpochSecond(ZoneOffset.UTC);
            }
            postCount++;
        }
        statisticsResponse.setDislikesCount(disLike);
        statisticsResponse.setViewsCount(viewCount);
        statisticsResponse.setPostsCount(postCount);
        statisticsResponse.setLikesCount(like);
        statisticsResponse.setFirstPublication(date);
        return statisticsResponse;
    }
}
