package com.kotlarz.backend.service;

import com.kotlarz.backend.web.dto.NewTemperatureDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LastTemperaturesResolver {
    private Map<String, NewTemperatureDto> lastTemperatures = new HashMap<>();

    private boolean isLastLog(NewTemperatureDto temperatureDto) {
        String address = temperatureDto.getAddress();
        Date date = temperatureDto.getDate();

        return !lastTemperatures.entrySet().stream()
                .filter(entry -> entry.getKey().equals(address))
                .anyMatch(entry -> entry.getValue().getDate().after(date));
    }

    public List<NewTemperatureDto> filterLastLogs(List<NewTemperatureDto> logs) {
        synchronized (lastTemperatures) {
            Map<String, List<NewTemperatureDto>> groupedByAddress = logs.stream()
                    .collect(Collectors.groupingBy(NewTemperatureDto::getAddress));

            List<NewTemperatureDto> distinctLogs = groupedByAddress.entrySet().stream()
                    .map(entry -> entry.getValue().stream()
                            .max(Comparator.comparing(NewTemperatureDto::getDate))
                            .orElseThrow(RuntimeException::new))
                    .collect(Collectors.toList());

            return distinctLogs.stream()
                    .filter(this::isLastLog)
                    .peek(log -> lastTemperatures.put(log.getAddress(), log))
                    .collect(Collectors.toList());
        }
    }

    public List<NewTemperatureDto> getLastCached() {
        synchronized (lastTemperatures) {
            return lastTemperatures.entrySet().stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }
    }
}
