package com.kotlarz.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kotlarz.application.AppSettings;
import com.kotlarz.service.dto.TemperatureLog;
import lombok.SneakyThrows;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.UnsupportedEncodingException;

public class LogsSender {
    private CloseableHttpClient client;

    private ObjectMapper mapper;

    public LogsSender() {
        client = HttpClientBuilder
                .create()
                .build();
        mapper = new ObjectMapper();
    }

    @SneakyThrows
    public void send(TemperatureLog temperatureLog) {
        String baseUrl = buildBaseUrl();
        HttpPost post = createPost(temperatureLog, baseUrl);
        client.execute(post);
    }

    private HttpPost createPost(TemperatureLog temperatureLog, String baseUrl) throws UnsupportedEncodingException, JsonProcessingException {
        HttpPost post = new HttpPost(baseUrl + "/temperatures");
        post.setEntity(createEntity(temperatureLog));
        post.setHeader("Content-type", "application/json");
        return post;
    }

    private StringEntity createEntity(TemperatureLog temperatureLog) throws UnsupportedEncodingException, JsonProcessingException {
        return new StringEntity(mapper.writeValueAsString(temperatureLog));
    }

    private String buildBaseUrl() {
        return "http://" + AppSettings.arguments.getIp() + ":" + AppSettings.arguments.getPort();
    }
}
