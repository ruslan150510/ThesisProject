package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CalendarResponse {
    List<Integer> years = new ArrayList<>();

    @JsonProperty("posts")
    HashMap<String, Integer> calendarList = new HashMap<>();

    public List<Integer> getYears() {
        return years;
    }

    public void addYears(Integer year) {
        this.years.add(year);
    }

    public HashMap<String, Integer> getCalendarList() {
        return calendarList;
    }

    public void addCalendarList(String datePublication, Integer countOfPublications) {
        this.calendarList.put(datePublication, countOfPublications);
    }
}
//"years": [2017, 2018, 2019, 2020],
//"posts": {
//"2019-12-17": 56,
//"2019-12-14": 11,
//"2019-06-17": 1,
//"2020-03-12": 6