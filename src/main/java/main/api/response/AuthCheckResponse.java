package main.api.response;

import main.model.User;

import java.util.List;

public class AuthCheckResponse {
    private boolean result;

    private List<User> user;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<User> getUser() {
        return user;
    }

    public void addUser(User user) {
        this.user.add(user);
    }
}
//"result": true,
//        "user": {
//        "id": 576,
//        "name": "Дмитрий Петров",
//        "photo": "/avatars/ab/cd/ef/52461.jpg",
//        "email": "petrov@petroff.ru",
//        "moderation": true,
//        "moderationCount": 56,
//        "settings": true
//        }