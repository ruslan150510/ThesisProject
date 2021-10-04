package main.controller;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.StatisticsResponse;
import main.model.User;
import main.model.repository.UserRepository;
import main.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    private final InitResponse initResponse;
    private final SettingsService settingsService;

    @Autowired
    private UserRepository userRepository;

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
    }

    @GetMapping("/init")
    private InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    private SettingsResponse settings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/statistics/my")//требуется авторизация
    @PreAuthorize("hasAuthority('user:write')")
    private ResponseEntity<StatisticsResponse> myStatisticsResponse(Principal principal) {
        StatisticsResponse statisticsResponse = settingsService.getMyStatistics(principal);
        if (statisticsResponse == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            return ResponseEntity.ok(statisticsResponse);
        }
    }

    @GetMapping("/statistics/all")
    private ResponseEntity<StatisticsResponse> allStatisticsResponse(Principal principal) {
        if (!settings().isStatisticIsPublic()) {
            try {
                User user = userRepository.findByEmail(principal.getName()).orElseThrow(
                        () -> new UsernameNotFoundException(principal.getName()));
                if (user.getIsModerator().intValue() == 0) {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>(settingsService.allStatistics(), HttpStatus.OK);
    }
}