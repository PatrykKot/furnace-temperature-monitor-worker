package com.kotlarz.service.sender;

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
import java.util.List;

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
    public void send(List<TemperatureLog> temperatureLogs) {
        System.out.println("Sending logs " + temperatureLogs);
        String baseUrl = buildBaseUrl();
        HttpPost post = createPost(temperatureLogs, baseUrl);
        client.execute(post);
    }

    private HttpPost createPost(List<TemperatureLog> temperatureLogs, String baseUrl) throws UnsupportedEncodingException, JsonProcessingException {
        HttpPost post = new HttpPost(baseUrl + "/temperatures");
        post.setEntity(createEntity(temperatureLogs));
        post.setHeader("Content-type", "application/json");
        return post;
    }

    private StringEntity createEntity(List<TemperatureLog> temperatureLog) throws UnsupportedEncodingException, JsonProcessingException {
        return new StringEntity(mapper.writeValueAsString(temperatureLog));
    }

    private String buildBaseUrl() {
        return "http://" + AppSettings.arguments.getHost() + ":" + AppSettings.arguments.getPort();
    }
}
