package com.kotlarz.backend.service;

import com.kotlarz.backend.domain.TemperatureLog;
import com.kotlarz.backend.persistance.TemperatureLogRepository;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Log4j2
@Service
public class TemperatureCleanupService {
    public static final Integer CLEAR_OLDER_THAN_DAYS = 30;

    @Autowired
    private TemperatureLogRepository temperatureLogRepository;

    @Transactional
    @Scheduled(fixedDelay = 3600 * 1000)
    public void cleanup() {
        log.info("Cleaning temperature logs");

        DateTime now = DateTime.now();
        DateTime monthBefore = now.minusDays(CLEAR_OLDER_THAN_DAYS);
        List<TemperatureLog> toDelete = temperatureLogRepository.findEarlierThan(new Date(monthBefore.getMillis()));
        log.info("Found " + toDelete.size() + " logs to delete");

        toDelete.forEach(log -> temperatureLogRepository.delete(log));
    }
}
