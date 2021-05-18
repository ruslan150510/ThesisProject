package main.service;

import main.api.response.AuthCheckResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthCheckService {
    public AuthCheckResponse getStatus() {
        AuthCheckResponse authCheckResponse = new AuthCheckResponse();
        authCheckResponse.setResult(false);
        return authCheckResponse;
    }
}
