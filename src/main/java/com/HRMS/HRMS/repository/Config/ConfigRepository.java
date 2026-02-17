package com.HRMS.HRMS.repository.Config;

import com.HRMS.HRMS.entity.Config.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends  JpaRepository<Config,Long> {
    Config findByConfigKey(String configKey);
}

