package com.HRMS.HRMS.service.Config;

import com.HRMS.HRMS.dto.ConfigDto;
import com.HRMS.HRMS.entity.Config.Config;
import com.HRMS.HRMS.repository.Config.ConfigRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ConfigService {
    private final ConfigRepository configRepository;

    @Autowired
    public ConfigService(ConfigRepository configRepository){
        this.configRepository = configRepository;
    }

    public void UpdateConfig( Long configId, ConfigDto configDto ){
        Config config =  configRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Config not found"));
        config.setConfigKey(configDto.getKey());
        config.setConfigValue(configDto.getValue());
        configRepository.save(config);
    }

    public List<String> allKey(){
       List<Config> configs = configRepository.findAll();
       if( configs.isEmpty() ){
         return new ArrayList<>();
       }
       return configs.stream().map(
               Config::getConfigKey
       ).toList();
    }

    public List<Config> allConfiguration(){
        return configRepository.findAll();
    }

    public String findValueByKey(String key){
        Config config = configRepository.findByConfigKey(key);
        return config.getConfigValue();
    }
}