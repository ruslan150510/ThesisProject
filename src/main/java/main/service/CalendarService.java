package main.service;

import main.api.response.CalendarResponse;
import main.model.Post;
import main.model.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarService {
    @Autowired
    private PostRepository postRepository;

    public CalendarResponse getCalendarResponse(Integer year){
        CalendarResponse calendarResponse = new CalendarResponse();
        List<Post> postList = postRepository.findAllByYear(year);
        if (postList.size() > 0) {
            calendarResponse.addYears(year);
        }
        for (Post post : postList)
        {
            LocalDateTime date = post.getTime();
            int count = 0;
            if (calendarResponse.getCalendarList().containsKey(date.toLocalDate().toString()))
            {
                count = calendarResponse.getCalendarList().get(date.toLocalDate().toString()).intValue();
            }
            calendarResponse.getCalendarList().put(date.toLocalDate().toString(), ++count);
        }
        return calendarResponse;
    }
}
