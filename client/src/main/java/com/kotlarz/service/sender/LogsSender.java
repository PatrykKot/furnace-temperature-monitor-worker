package com.kotlarz.service.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kotlarz.application.AppSettings;
import com.kotlarz.service.dto.TemperatureLog;
import lombok.SneakyThrows;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class LogsSender {
    private CloseableHttpClient client;

    private ObjectMapper mapper;

    private LogsQueue queue;

    private Thread runner;

    public LogsSender() {
        client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(60 * 1000)
                        .setConnectionRequestTimeout(60 * 1000)
                        .setSocketTimeout(60 * 1000)
                        .build())
                .disableAutomaticRetries()
                .build();
        mapper = new ObjectMapper();
        queue = new LogsQueue();
    }

    @SneakyThrows
    public synchronized void invoke(List<TemperatureLog> temperatureLogs) {
        queue.insert(temperatureLogs);

        if (runner == null || !runner.isAlive()) {
            runner = new Thread(() -> {
                System.out.println("Invoking sender");
                call();
            });
            runner.start();
        }
    }

    private void call() {
        try {
            List<TemperatureLog> toSend = queue.get();
            System.out.println("Sending " + toSend.size() + " logs");

            send(toSend);
            System.out.println("Sending success");

            queue.remove(toSend);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void send(List<TemperatureLog> toSend) throws IOException {
        String baseUrl = buildBaseUrl();
        HttpPost post = createPost(toSend, baseUrl);
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
