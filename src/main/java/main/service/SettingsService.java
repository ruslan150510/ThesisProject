package main.service;

import main.api.response.SettingsResponse;
import main.model.GlobalSettings;
import main.model.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    public static final String STATUS = "YES";
    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        Iterable<GlobalSettings> globalSettingsIterable = globalSettingsRepository.findAll();
        for (GlobalSettings settings : globalSettingsIterable) {
            switch (settings.getCode()) {
                case "MULTIUSER_MODE":
                    if (settings.getValue().equals(STATUS)) {
                        settingsResponse.setMultiuserMode(true);
                    } else {
                        settingsResponse.setMultiuserMode(false);
                    }
                    break;
                case "POST_PREMODERATION":
                    if (settings.getValue().equals(STATUS)) {
                        settingsResponse.setPostPremoderation(true);
                    } else {
                        settingsResponse.setPostPremoderation(false);
                    }
                    break;
                case "STATISTICS_IS_PUBLIC":
                    if (settings.getValue().equals(STATUS)) {
                        settingsResponse.setStatisticIsPublic(true);
                    } else {
                        settingsResponse.setStatisticIsPublic(false);
                    }
                    break;
            }
        }
        return settingsResponse;
    }
}
