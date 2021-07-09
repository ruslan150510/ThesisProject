package main.service;

import main.api.response.SettingsResponse;
import main.model.GlobalSettings;
import main.model.repository.GlobalSettingsRepository;
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
}
